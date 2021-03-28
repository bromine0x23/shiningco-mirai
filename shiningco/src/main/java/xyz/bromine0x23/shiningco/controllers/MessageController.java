package xyz.bromine0x23.shiningco.controllers;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.bromine0x23.shiningco.representations.MessageRepresentation;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/app/messages")
public class MessageController {

	private final Bot bot;

	public MessageController(Bot bot) {
		this.bot = bot;
	}

	@PostMapping
	public ResponseEntity<Void> create(
		@RequestBody MessageRepresentation message
	) {
		var target = message.getTarget();
		if (target.startsWith("friend:")) {
			var friendId = Long.parseLong(target.substring(7));
			var friend = bot.getFriend(friendId);
			if (friend != null) {
				sendMessage(friend, message.getMessages());
			}
		}
		if (target.startsWith("group:")) {
			var groupId = Long.parseLong(target.substring(6));
			var group = bot.getGroup(groupId);
			if (group != null) {
				sendMessage(group, message.getMessages());
			}
		}
		return ResponseEntity.accepted().build();
	}

	public void sendMessage(Contact contact, List<MessageRepresentation.Message> messages) {
		var messageChainBuilder = new MessageChainBuilder();
		for (var message : messages) {
			switch (message.getType()) {
				case PARAGRAPH:
					var paragraphMessage = (MessageRepresentation.ParagraphMessage) message;
					var text = paragraphMessage.getData().getText();
					messageChainBuilder.add(new PlainText(text));
					break;
				case IMAGE:
					var imageMessage = (MessageRepresentation.ImageMessage) message;
					var imageDataUrl = imageMessage.getData().getFile().getUrl();
					var imageBase64 = imageDataUrl.substring(imageDataUrl.indexOf(",") + 1);
					var imageBytes = Base64.getDecoder().decode(imageBase64);
					messageChainBuilder.add(contact.uploadImage(ExternalResource.create(imageBytes)));
					break;
				default:
					break;
			}
		}
		if (!messageChainBuilder.isEmpty()) {
			contact.sendMessage(messageChainBuilder.build());
		}
	}
}

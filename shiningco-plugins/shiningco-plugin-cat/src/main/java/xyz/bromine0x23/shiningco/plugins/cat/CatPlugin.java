package xyz.bromine0x23.shiningco.plugins.cat;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.web.client.RestClientException;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;
import xyz.bromine0x23.shiningco.plugins.PluginCommandSubject;

import java.io.IOException;

@Slf4j
@Plugin(id = "cat", name = "猫插件", usage = "来点猫")
public class CatPlugin {

	private final CatService catService;

	public CatPlugin(CatService catService) {
		this.catService = catService;
	}

	@PluginCommand(pattern = "来点猫", callRequired = false)
	public MessageChainBuilder get(
		@PluginCommandSubject Contact subject,
		MessageChainBuilder reply
	) throws IOException {
		try {
			var stream = catService.get();
			if (stream.length > 0) {
				try (var resource = ExternalResource.create(stream)) {
					reply.add(subject.uploadImage(resource));
				}
			}
		} catch (RestClientException exception) {
			reply.add("网络异常\uD83D\uDE3F");
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("插件异常\uD83D\uDE3F");
		}
		return reply;
	}

}

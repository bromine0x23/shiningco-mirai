package xyz.bromine0x23.shiningco.controllers;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.bromine0x23.shiningco.representations.FriendRepresentation;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/app/friends")
public class FriendsController implements ResourceControllerHelper {

	private final Bot bot;

	public FriendsController(Bot bot) {
		this.bot = bot;
	}

	@GetMapping
	public List<FriendRepresentation> index() {
		return toRepresentations(bot.getFriends().stream().sorted(Comparator.comparing(Friend::getId)), FriendRepresentation::from);
	}

}

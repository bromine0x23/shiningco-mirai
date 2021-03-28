package xyz.bromine0x23.shiningco.controllers;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.bromine0x23.shiningco.representations.GroupRepresentation;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/app/groups")
public class GroupsController implements ResourceControllerHelper {

	private final Bot bot;

	public GroupsController(Bot bot) {
		this.bot = bot;
	}

	@GetMapping
	public List<GroupRepresentation> index() {
		return toRepresentations(bot.getGroups().stream().sorted(Comparator.comparing(Group::getId)), GroupRepresentation::from);
	}

}

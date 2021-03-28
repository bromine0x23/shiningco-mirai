package xyz.bromine0x23.shiningco.controllers;

import net.mamoe.mirai.Bot;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {

	private final Bot bot;

	public IndexController(Bot bot) {
		this.bot = bot;
	}

	@GetMapping
	public String index(Model model) {
		model.addAttribute("bot", bot);
		return "index.pug";
	}

}

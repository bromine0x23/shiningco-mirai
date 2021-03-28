package xyz.bromine0x23.shiningco.plugins.ehentai;

import net.mamoe.mirai.event.events.MessageEvent;
import org.springframework.stereotype.Component;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.io.IOException;
import java.util.regex.Matcher;

@Component
public class ExhentaiPlugin {

	private final ExhentaiService exhentaiService;

	public ExhentaiPlugin(ExhentaiService exhentaiService) {
		this.exhentaiService = exhentaiService;
	}

	@PluginCommand("\\A来点(?<keyword>.+)本子\\Z")
	public void search(MessageEvent event, Matcher matcher) throws IOException {
		var keyword = matcher.group("keyword");

		// var illusts = exhentaiService.search(keyword);

		event.getSubject().sendMessage("没有找到" + keyword + "本子");

	}

}

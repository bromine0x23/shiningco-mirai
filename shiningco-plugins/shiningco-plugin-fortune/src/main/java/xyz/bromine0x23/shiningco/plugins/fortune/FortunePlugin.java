package xyz.bromine0x23.shiningco.plugins.fortune;

import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.springframework.util.StringUtils;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.regex.Matcher;

@Plugin(id = "fortune", name = "运势插件", usage = "@我 我的运势")
public class FortunePlugin {

	private static final String[] FORTUNE_LEVELS = {
		"溢出", "大凶", "凶", "凶", "凶", "木吉",
		"末吉", "末吉", "末吉", "末吉", "末吉",
		"半吉", "半吉", "吉", "吉", "小吉",
		"小吉", "中吉", "中吉", "大吉", "大吉",
		"秀吉"
	};

	private final FortuneService fortuneService;

	public FortunePlugin(
		FortuneService fortuneService
	) {
		this.fortuneService = fortuneService;
	}

	@PluginCommand(pattern = "\\A我的运势\\Z")
	public MessageChainBuilder get(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		var instant = Instant.now();
		var sender = event.getSender();
		var fortune = fortuneService.fortune(LocalDate.now().toEpochDay() ^ sender.getId());
		var level = FORTUNE_LEVELS[(fortune + 5) / 5];

		reply.add(fortuneService.formatGregorianCalendar(instant));
		reply.add("\n");
		reply.add(fortuneService.formatChineseCalendar(instant));
		reply.add("\n");
		reply.add(MessageFormat.format("{0}的运势指数: {1} （{2}）", nameCardOrNick(sender), fortune, level));
		return reply;
	}

	private String nameCardOrNick(User sender) {
		String result = sender.getNick();
		if (sender instanceof Member) {
			var nameCard = ((Member) sender).getNameCard();
			if (StringUtils.hasText(nameCard)) {
				result = nameCard;
			}
		}
		return result;
	}

}

package xyz.bromine0x23.shiningco.plugins.eve;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.web.client.RestClientException;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Plugin(id = "eve", name = "EVE插件", usage = "@我 EVE [晨曦|宁静]市场 <物品>")
public class EVEPlugin {
	private static final DecimalFormat ISK_FORMAT = new DecimalFormat("###,###,##0.00");

	private final EVEService eveService;

	public EVEPlugin(
		EVEService eveService
	) {
		this.eveService = eveService;
	}

	@PluginCommand(pattern = "\\AEVE\\s?(?<market>晨曦|宁静)?市场\\s+(?<itemname>.+)\\Z", patternFlags = Pattern.CASE_INSENSITIVE)
	public MessageChainBuilder price(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		try {
			var market   = matcher.group("market");
			var itemName = matcher.group("itemname");
			var item     = eveService.findItem(itemName);
			if (item == null) {
				reply.add("未找到物品：" + itemName);
			} else {
				var prices = eveService.prices(market, null, null, item);

				reply.add(itemName);
				reply.add(" 市场价格");
				reply.add("\n");
				reply.add("买入：");
				reply.add(ISK_FORMAT.format(prices.getBuy().getMax()));
				reply.add("\n");
				reply.add("卖出：");
				reply.add(ISK_FORMAT.format(prices.getSell().getMin()));
			}
		} catch (RestClientException exception) {
			reply.add("网络异常");
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("EVE插件异常");
		}
		return reply;
	}

	@PluginCommand(pattern = "\\AEVE\\s+(?:ping)\\Z", patternFlags = Pattern.CASE_INSENSITIVE)
	public MessageChainBuilder status(MessageEvent event, MessageChainBuilder reply) {
		try {
			var image = eveService.status();
			try (var resource = ExternalResource.create(image.getByteArray())) {
				reply.add(event.getSender().uploadImage(resource));
			}
		} catch (RestClientException | IOException exception) {
			reply.add("网络异常");
		} catch (Exception exception) {
			log.debug("插件异常: ", exception);
			reply.add("EVE插件异常");
		}
		return reply;
	}

}

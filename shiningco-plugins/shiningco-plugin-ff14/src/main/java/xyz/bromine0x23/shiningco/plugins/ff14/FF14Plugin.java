package xyz.bromine0x23.shiningco.plugins.ff14;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(id = "ff14", name = "FF14插件", usage = "<TODO>")
public class FF14Plugin {
	private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("###,##0,000");

	private final FF14Service ff14Service;

	public FF14Plugin(
		FF14Service ff14Service
	) {
		this.ff14Service = ff14Service;
	}

	@PluginCommand(pattern = "\\AFF14\\s+(?<market>.+)\\s+(?<itemname>.+)\\Z", patternFlags = Pattern.CASE_INSENSITIVE)
	public MessageChainBuilder prices(MessageEvent event, Matcher matcher, MessageChainBuilder reply) {
		var market   = matcher.group("market");
		var itemName = matcher.group("itemname");
		if (ff14Service.hasMarket(market)) {
			var item = ff14Service.findItem(itemName);
			if (item != null) {
				var optionalPrice = ff14Service.price(market, item);
				if (optionalPrice.isPresent()) {
					var price = optionalPrice.get();
					reply.add(MessageFormat.format("{0} 在 {1} 的价格：", itemName, market));
					reply.add("\n");
					reply.add("最低: " + PRICE_FORMAT.format(price.getMinPrice()));
					reply.add("\n");
					reply.add("最高: " + PRICE_FORMAT.format(price.getMaxPrice()));
				} else {
					reply.add("网络异常:(" + itemName);
				}
			} else {
				reply.add("未找到物品：" + itemName);
			}
		} else {
			reply.add("未找到市场：" + market);
		}
		return reply;
	}
}

package xyz.bromine0x23.shiningco.plugins;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import xyz.bromine0x23.shiningco.services.PluginManageService;

import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.regex.Matcher;

@Plugin(id = "core", name = "核心插件", usage = "/help\n/help <插件>")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorePlugin {

	private final PluginManageService pluginManageService;

	public CorePlugin(
		@Lazy PluginManageService pluginManageService
	) {
		this.pluginManageService = pluginManageService;
	}

	@PluginCommand(pattern = "\\A/help\\s+(?<IdOrName>.+)\\Z", callRequired = false)
	public Message usage(Matcher matcher) {
		var idOrName    = matcher.group("IdOrName");
		var foundPlugin = pluginManageService.getPlugin(idOrName);
		var builder     = new StringBuilder();
		if (foundPlugin.isPresent()) {
			var plugin = foundPlugin.get();
			builder
				.append(plugin.getName())
				.append("#<")
				.append(plugin.getId())
				.append(">")
				.append('\n')
				.append(plugin.getUsage());
		} else {
			builder.append(MessageFormat.format("未找到插件 {0}", idOrName));
		}
		return new PlainText(builder.toString());
	}

	@PluginCommand(pattern = "\\A/help\\Z", callRequired = false)
	public Message plugins(MessageEvent event) {
		var plugins = pluginManageService.getPlugins(event);
		var builder = new StringBuilder();
		for (var plugin : plugins) {
			builder
				.append(plugin.getName())
				.append(" <")
				.append(plugin.getId())
				.append(">")
				.append('\n');
		}
		builder.deleteCharAt(builder.length() - 1);
		return new PlainText(builder.toString());
	}

	@PluginCommand("\\A/info\\Z")
	public Message info() {
		var runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		var memoryMXBean  = ManagementFactory.getMemoryMXBean();

		var heapMemoryUsage    = memoryMXBean.getHeapMemoryUsage();
		var nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

		var heapCommitted    = heapMemoryUsage.getCommitted();
		var heapUsed         = heapMemoryUsage.getUsed();
		var nonHeapCommitted = nonHeapMemoryUsage.getCommitted();
		var nonHeapUsed      = nonHeapMemoryUsage.getUsed();

		var builder = new StringBuilder();
		builder.append(runtimeMXBean.getVmName());
		builder.append(" (build ");
		builder.append(runtimeMXBean.getVmVersion());
		builder.append(")");
		builder.append('\n');
		builder.append("Heap Memory Committed: ").append(String.format("%.2f", heapCommitted / 1024.0 / 1024.0)).append("MB");
		builder.append('\n');
		builder.append("Heap Memory Used: ").append(String.format("%.2f", heapUsed / 1024.0 / 1024.0)).append("MB");
		builder.append('\n');
		builder.append("Non-Heap Memory Committed: ").append(String.format("%.2f", nonHeapCommitted / 1024.0 / 1024.0)).append("MB");
		builder.append('\n');
		builder.append("Non-Heap Memory Used: ").append(String.format("%.2f", nonHeapUsed / 1024.0 / 1024.0)).append("MB");

		return new PlainText(builder.toString());
	}

	@PluginCommand(pattern = "\\A/time(?: (?<zone>.+))?\\Z", callRequired = false)
	public MessageChainBuilder datetime(MessageChainBuilder reply, Matcher matcher) {
		var instant = Instant.now();
		var builder = new StringBuilder();
		var zone = matcher.group("zone");
		if (StringUtils.hasText(zone)) {
			try {
				var zoneId = ZoneId.of(zone);
				builder.append(instant.atZone(zoneId));
			} catch (DateTimeException exception) {
				builder.append("没有找到时区“").append(zone).append("”");
				builder.append('\n');
				builder.append(instant);
			}
		} else {
			builder.append(instant.atZone(ZoneId.of("Asia/Shanghai")));
		}
		reply.add(builder.toString());
		return reply;
	}

}

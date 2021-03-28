package xyz.bromine0x23.shiningco.plugins;

import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import xyz.bromine0x23.shiningco.services.PluginManageService;

import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.time.Clock;
import java.util.regex.Matcher;

@Plugin(id = "core", name = "核心插件", usage = "/help\n/help <插件>")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorePlugin {

	private final PluginManageService pluginManageService;

	public CorePlugin(PluginManageService pluginManageService) {
		this.pluginManageService = pluginManageService;
	}

	@PluginCommand("\\A/help\\s+(?<IdOrName>.+)\\Z")
	public Message usage(Matcher matcher) {
		var idOrName = matcher.group("IdOrName");
		var foundPlugin   = pluginManageService.getPlugin(idOrName);
		var builder = new StringBuilder();
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

	@PluginCommand("\\A/help\\Z")
	public Message plugins() {
		var plugins = pluginManageService.getPlugins();
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
		var memoryMXBean = ManagementFactory.getMemoryMXBean();

		var heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
		var nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

		var heapCommitted = heapMemoryUsage.getCommitted();
		var heapUsed = heapMemoryUsage.getUsed();
		var nonHeapCommitted = nonHeapMemoryUsage.getCommitted();
		var nonHeapUsed = nonHeapMemoryUsage.getUsed();

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

}

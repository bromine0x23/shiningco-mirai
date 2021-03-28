package xyz.bromine0x23.shiningco.listeners;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xyz.bromine0x23.shiningco.services.PluginHandleService;

@Slf4j
@Component
public class ShiningcoListener extends SimpleListenerHost {

	private final PluginHandleService pluginHandleService;

	public ShiningcoListener(
		PluginHandleService pluginHandleService
	) {
		this.pluginHandleService = pluginHandleService;
	}

	@EventHandler
	public void onMessage(@NotNull MessageEvent event) throws Exception {
		log.debug("发送人：{}", event.getSender());
		for (var message : event.getMessage()) {
			log.debug("接收消息：{} ({})", message, message.getClass().getSimpleName());
		}

		var message = pluginHandleService.handle(event);
		if (message != null) {
			event.getSubject().sendMessage(message);
		}

	}

}

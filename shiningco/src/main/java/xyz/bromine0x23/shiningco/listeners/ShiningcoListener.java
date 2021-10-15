package xyz.bromine0x23.shiningco.listeners;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.event.events.NudgeEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.bromine0x23.shiningco.services.PluginHandleService;

@Slf4j
@Component
public class ShiningcoListener extends SimpleListenerHost {

	private final PluginHandleService pluginHandleService;

	public ShiningcoListener(
		@Lazy PluginHandleService pluginHandleService
	) {
		this.pluginHandleService = pluginHandleService;
	}

	@EventHandler
	public void onNudge(@NotNull NudgeEvent event) throws Exception {
		if (event.getTarget().getId() == event.getBot().getId()) {
			var reply = new MessageChainBuilder();
			if (event.getSubject() instanceof Group) {
				reply.add(new At(event.getFrom().getId()));
				reply.add("\n");
			}
			reply.add("/help 获取插件列表");
			reply.add("\n");
			reply.add("/help <plugin-id> 获取插件帮助");
			event.getSubject().sendMessage(reply.asMessageChain());
		}
	}

	@EventHandler
	public void onMessage(@NotNull MessageEvent event) throws Exception {
		log.debug("发送人：{}", event.getSender());
		for (var message : event.getMessage()) {
			log.debug("接收消息：{} ({})", message, message.getClass().getSimpleName());
		}
		try {
			var message = pluginHandleService.handle(event);
			if (message != null) {
				event.getSubject().sendMessage(message);
			}
		} catch (Exception exception) {
			log.error("Exception in onMessage: ", exception);
			event.getSubject().sendMessage("消息处理过程中发生错误");
		}
	}

}

package xyz.bromine0x23.shiningco.services;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PluginHandleService {

	private final PluginManageService pluginManageService;

	public PluginHandleService(
		@Lazy PluginManageService pluginManageService
	) {
		this.pluginManageService = pluginManageService;
	}

	public Message handle(MessageEvent event) throws Exception {
		var context = prepareHandleContext(event);
		for (var plugin : pluginManageService.getPlugins(event)) {
			var message = doHandle(plugin, context);
			if (message != null) {
				return message;
			}
		}
		return null;
	}

	private Message doHandle(RegisteredPlugin plugin, HandleContext context) throws Exception {
		for (var command : plugin.getCommands()) {
			if (!command.isCallRequired() || context.called) {
				var message = doHandle(plugin, command, context);
				if (message != null) {
					return message;
				}
			}
		}
		return null;
	}

	private Message doHandle(RegisteredPlugin plugin, RegisteredPluginCommand command, HandleContext context) throws Exception {
		var text    = command.isCallRequired() ? context.strippedText : context.text;
		var matcher = command.getPattern().matcher(text);
		log.trace("\"{}\" challenge /{}/", text, command.getPattern());
		if (matcher.find()) {
			return doInvoke(plugin, command, context, matcher);
		}
		return null;
	}

	private Message doInvoke(RegisteredPlugin plugin, RegisteredPluginCommand command, HandleContext context, Matcher matcher) throws ReflectiveOperationException {
		var method         = command.getMethod();
		var parameterTypes = command.getParameterTypes();
		var arguments      = new Object[parameterTypes.length];
		for (var i = 0; i < parameterTypes.length; ++i) {
			var parameterType = parameterTypes[i];
			switch (parameterType) {
				case MESSAGE_EVENT:
					arguments[i] = context.getEvent();
					break;
				case MATCHER:
					arguments[i] = matcher;
					break;
				case MESSAGE_CHAIN_BUILDER:
					arguments[i] = createMessageChainBuilder(context);
					break;
				case BOT:
					arguments[i] = context.getEvent().getBot();
					break;
				case SUBJECT:
					arguments[i] = context.getEvent().getSubject();
					break;
				case SOURCE:
					arguments[i] = context.getEvent().getSource();
					break;
				case SENDER:
					arguments[i] = context.getEvent().getSender();
					break;
				default:
					arguments[i] = null;
					break;
			}
		}
		var result = method.invoke(plugin.getBean(), arguments);
		if (result instanceof Message) {
			return (Message) result;
		}
		if (result instanceof MessageChainBuilder) {
			return ((MessageChainBuilder) result).build();
		}
		return null;
	}

	private HandleContext prepareHandleContext(MessageEvent event) {
		var context = new HandleContext();
		context.event        = event;
		context.text         = event.getMessage().stream()
			.filter(PlainText.class::isInstance)
			.map(Message::contentToString)
			.collect(Collectors.joining())
			.strip();
		context.strippedText = context.text;
		var source = event.getSource();
		if (source instanceof OnlineMessageSource.Incoming.FromFriend) {
			context.fromFriend = true;
			context.called     = true;
		} else if (source instanceof OnlineMessageSource.Incoming.FromGroup) {
			context.fromGroup = true;
			var fromGroup = (OnlineMessageSource.Incoming.FromGroup) source;
			for (var message : event.getMessage()) {
				if (message instanceof At) {
					var at = (At) message;
					if (event.getBot().getId() == at.getTarget()) {
						context.called = true;
					}
				}
			}

			var dummy =
				tryPrefix(context, "@" + event.getBot().getNick()) ||
					tryPrefix(context, event.getBot().getNick()) ||
					tryPrefix(context, "@" + fromGroup.getGroup().getBotAsMember().getNameCard()) ||
					tryPrefix(context, fromGroup.getGroup().getBotAsMember().getNameCard());
		}
		return context;
	}

	private MessageChainBuilder createMessageChainBuilder(HandleContext context) {
		var messageChainBuilder = new MessageChainBuilder();
		if (context.fromGroup) {
			var messageSource = context.event.getMessage().get(MessageSource.Key);
			assert messageSource != null;
			messageChainBuilder.add(new QuoteReply(messageSource));
		}
		return messageChainBuilder;
	}

	private boolean tryPrefix(HandleContext context, String prefix) {
		if (StringUtils.hasText(prefix) && context.text.startsWith(prefix)) {
			context.called       = true;
			context.strippedText = context.text.substring(prefix.length()).strip();
			return true;
		}
		return false;
	}

	@Data
	public static class HandleContext {

		private MessageEvent event;

		private String text;

		private boolean called = false;

		private String strippedText;

		private boolean fromFriend = false;

		private boolean fromGroup = false;

	}

}

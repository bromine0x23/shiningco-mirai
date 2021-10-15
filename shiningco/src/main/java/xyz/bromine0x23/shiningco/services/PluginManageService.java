package xyz.bromine0x23.shiningco.services;

import lombok.Setter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.GroupMemberEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;
import xyz.bromine0x23.shiningco.plugins.PluginCommandSender;
import xyz.bromine0x23.shiningco.plugins.PluginCommandSource;
import xyz.bromine0x23.shiningco.plugins.PluginCommandSubject;

import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PluginManageService implements InitializingBean, ApplicationContextAware {

	@Setter
	private ApplicationContext applicationContext;

	private List<RegisteredPlugin> plugins;

	private final Enforcer enforcer;

	public PluginManageService(
		Enforcer enforcer
	) {
		this.enforcer = enforcer;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.plugins = registerPlugins(applicationContext);
	}

	public List<RegisteredPlugin> getPlugins() {
		return Collections.unmodifiableList(plugins);
	}

	public List<RegisteredPlugin> getPlugins(MessageEvent event) {
		var sender  = event.getSender();
		var subject = event.getSubject();

		var requestSender  = toStringForEnforce(sender);
		var requestSubject = toStringForEnforce(subject);

		return plugins.stream().filter(plugin -> enforcer.enforce(requestSender, requestSubject, plugin.getId())).toList();
	}

	public Optional<RegisteredPlugin> getPlugin(String idOrName) {
		return plugins.stream()
			.filter(plugin -> plugin.getId().equals(idOrName) || plugin.getName().equals(idOrName))
			.findFirst();
	}

	private static List<RegisteredPlugin> registerPlugins(ApplicationContext applicationContext) {
		var beans = applicationContext.getBeansWithAnnotation(Plugin.class).values().stream()
			.sorted(AnnotationAwareOrderComparator.INSTANCE)
			.toList();
		var plugins = new ArrayList<RegisteredPlugin>();
		for (var bean : beans) {
			var annotation = AnnotatedElementUtils.findMergedAnnotation(bean.getClass(), Plugin.class);
			if (annotation != null) {
				var commands = registerPluginsCommands(bean);
				var plugin = RegisteredPlugin.builder()
					.bean(bean)
					.beanType(ClassUtils.getUserClass(bean))
					.id(annotation.id())
					.name(annotation.name())
					.usage(annotation.usage())
					.commands(commands)
					.build();
				plugins.add(plugin);
			}
		}
		return Collections.unmodifiableList(plugins);
	}

	private static List<RegisteredPluginCommand> registerPluginsCommands(Object bean) {
		var commands = new ArrayList<RegisteredPluginCommand>();
		ReflectionUtils.doWithMethods(
			ClassUtils.getUserClass(bean),
			method -> {
				var annotations = AnnotatedElementUtils.findMergedRepeatableAnnotations(method, PluginCommand.class);
				for (var annotation : annotations) {
					var parameters     = method.getParameters();
					var parameterTypes = new RegisteredPluginCommand.ParameterType[parameters.length];
					for (int i = 0; i < parameters.length; ++i) {
						parameterTypes[i] = resolvePluginCommandParameters(parameters[i]);
					}
					var command = RegisteredPluginCommand.builder()
						.method(method)
						.pattern(Pattern.compile(annotation.pattern(), annotation.patternFlags()))
						.callRequired(annotation.callRequired())
						.parameterTypes(parameterTypes)
						.build();
					commands.add(command);
				}
			}
		);
		return Collections.unmodifiableList(commands);
	}

	private static RegisteredPluginCommand.ParameterType resolvePluginCommandParameters(Parameter parameter) {
		if (parameter.getType() == MessageEvent.class) {
			return RegisteredPluginCommand.ParameterType.MESSAGE_EVENT;
		}
		if (parameter.getType() == Matcher.class) {
			return RegisteredPluginCommand.ParameterType.MATCHER;
		}
		if (parameter.getType() == MessageChainBuilder.class) {
			return RegisteredPluginCommand.ParameterType.MESSAGE_CHAIN_BUILDER;
		}
		if (parameter.getType() == Bot.class) {
			return RegisteredPluginCommand.ParameterType.BOT;
		}
		if (parameter.isAnnotationPresent(PluginCommandSubject.class) && parameter.getType().isAssignableFrom(Contact.class)) {
			return RegisteredPluginCommand.ParameterType.SUBJECT;
		}
		if (parameter.isAnnotationPresent(PluginCommandSource.class) && parameter.getType().isAssignableFrom(OnlineMessageSource.Incoming.class)) {
			return RegisteredPluginCommand.ParameterType.SUBJECT;
		}
		if (parameter.isAnnotationPresent(PluginCommandSender.class) && parameter.getType().isAssignableFrom(User.class)) {
			return RegisteredPluginCommand.ParameterType.SUBJECT;
		}
		var message = MessageFormat.format("不支持的参数类型: {0} ({1})", parameter.getType(), Arrays.toString(parameter.getAnnotations()));
		throw new IllegalArgumentException(message);
	}

	private String toStringForEnforce(Contact contact) {
		if (contact instanceof User user) {
			return "/user/" + user.getId();
		}
		if (contact instanceof Group group) {
			return "/group/" + group.getId();
		}
		return "/contact/" + contact.getId();
	}

}

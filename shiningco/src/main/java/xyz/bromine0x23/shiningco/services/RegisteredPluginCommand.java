package xyz.bromine0x23.shiningco.services;

import lombok.Builder;
import lombok.Value;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

@Value
@Builder
public class RegisteredPluginCommand {

	Method method;

	Pattern pattern;

	boolean callRequired;

	ParameterType[] parameterTypes;

	public enum ParameterType {
		MESSAGE_EVENT,
		MATCHER,
		BOT,
		SUBJECT,
		SOURCE,
		SENDER,
		MESSAGE_CHAIN_BUILDER
	}

}

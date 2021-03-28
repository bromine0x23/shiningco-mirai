package xyz.bromine0x23.shiningco.services;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RegisteredPlugin {

	Object bean;

	Class<?> beanType;

	String id;

	String name;

	String usage;

	List<RegisteredPluginCommand> commands;

}

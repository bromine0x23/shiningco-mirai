package xyz.bromine0x23.shiningco.plugins;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(PluginCommand.List.class)
public @interface PluginCommand {

	@AliasFor("pattern")
	String value() default "";

	@AliasFor("value")
	String pattern() default "";

	int patternFlags() default 0;

	/**
	 * 是否需要显式@，或包含机器人昵称前缀
	 */
	boolean callRequired() default true;

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		PluginCommand[] value();
	}

}

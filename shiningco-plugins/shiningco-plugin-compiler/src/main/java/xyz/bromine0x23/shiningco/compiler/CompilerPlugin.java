package xyz.bromine0x23.shiningco.compiler;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import xyz.bromine0x23.shiningco.compiler.representations.BuildResult;
import xyz.bromine0x23.shiningco.compiler.representations.Line;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Plugin(id = "compiler", name = "编译插件", usage = "/compile.(c|c++|go|rust)\n<source>")
public class CompilerPlugin {

	private static final Pattern ANSI_ESCAPE = Pattern.compile("\u001B\\[[\\d;]*[\\w~]");

	private final CompilerService compilerService;

	public CompilerPlugin(
		CompilerService compilerService
	) {
		this.compilerService = compilerService;
	}

	@PluginCommand(pattern = "^/compile(?:\\.(?<language>c|c\\+\\+|go|rust))?\n(?<source>(?:.|\n)*)$", patternFlags = Pattern.CASE_INSENSITIVE, callRequired = false)
	public MessageChainBuilder compile(Matcher matcher, MessageChainBuilder reply) {
		try {
			var language = matcher.group("language");
			var source = matcher.group("source");
			var buildResult = compilerService.compileAndExecute(language, source);
			reply.add(extract(buildResult));
		} catch (Exception exception) {
			log.debug("Exception: ", exception);
			reply.add("执行异常：" + exception.getLocalizedMessage());
		}
		return reply;
	}

	public String extract(BuildResult buildResult) {
		if (buildResult.getCode() != 0) {
			var stdout = extractLines(buildResult.getStdout());
			var stderr = extractLines(buildResult.getStderr());
			return "" + stdout + stderr;
		} else {
			var execResult = buildResult.getExecResult();
			var stdout = extractLines(execResult.getStdout());
			var stderr = extractLines(execResult.getStderr());
			return "" + stdout + stderr;
		}
	}

	public String extractLines(List<Line> lines) {
		var builder = new StringBuilder();
		for (var line : lines) {
			var text = line.getText();
			var matcher = ANSI_ESCAPE.matcher(text);
			while (matcher.find()) {
				matcher.appendReplacement(builder, "");
			}
			matcher.appendTail(builder);
			builder.append("\n");
		}
		return builder.toString();
	}
}

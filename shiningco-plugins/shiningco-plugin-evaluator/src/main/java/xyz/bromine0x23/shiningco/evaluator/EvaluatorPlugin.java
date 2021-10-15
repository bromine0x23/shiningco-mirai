package xyz.bromine0x23.shiningco.evaluator;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.apache.commons.math3.util.FastMath;
import xyz.bromine0x23.shiningco.plugins.Plugin;
import xyz.bromine0x23.shiningco.plugins.PluginCommand;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(id = "evaluator", name = "求值插件", usage = "/eval <expression>\n/eval.i <expression>\n/eval.c <expression>")
public class EvaluatorPlugin {

	private final Evaluator evaluator = new Evaluator();
	private final IntegerEvaluator integerEvaluator = new IntegerEvaluator();
	private final ComplexEvaluator complexEvaluator = new ComplexEvaluator();

	@PluginCommand(pattern = "\\A[\\\\/]eval\\s+(?<expression>.+)\\Z", patternFlags = Pattern.CASE_INSENSITIVE, callRequired = false)
	public MessageChainBuilder evaluate(Matcher matcher, MessageChainBuilder reply) {
		return eval(evaluator, matcher, reply, result -> {
			reply.add(String.format("%.15G", result));
		});
	}

	@PluginCommand(pattern = "\\A[\\\\/]eval\\.i\\s+(?<expression>.+)\\Z", patternFlags = Pattern.CASE_INSENSITIVE, callRequired = false)
	public MessageChainBuilder integerEvaluate(Matcher matcher, MessageChainBuilder reply) {
		return eval(integerEvaluator, matcher, reply, result -> {
			reply.add("DEC: " + result.toString(10));
			reply.add("\n");
			reply.add("HEX: 0x" + result.toString(16));
		});
	}

	@PluginCommand(pattern = "\\A[\\\\/]eval\\.c\\s+(?<expression>.+)\\Z", patternFlags = Pattern.CASE_INSENSITIVE, callRequired = false)
	public MessageChainBuilder complexEvaluate(Matcher matcher, MessageChainBuilder reply) {
		return eval(complexEvaluator, matcher, reply, result -> {
			if (result.getReal() != 0) {
				if (result.getImaginary() != 0) {
					reply.add(
						String.format(
							result.getImaginary() < 0 ? "%.15G - %.15Gi" : "%.15G + %15Gi",
							result.getReal(),
							FastMath.abs(result.getImaginary())
						)
					);
				} else {
					reply.add(String.format("%.15G", result.getReal()));
				}
			} else {
				if (result.getImaginary() != 0) {
					reply.add(String.format("%.15G", result.getImaginary()));
					reply.add("i");
				} else {
					reply.add("0");
				}
			}
		});
	}

	private <T> MessageChainBuilder eval(AbstractEvaluator<T> evaluator, Matcher matcher, MessageChainBuilder reply, Consumer<T> formatter) {
		var expression = matcher.group("expression");
		if ("operators".equals(expression)) {
			var operators = evaluator.getOperators();
			for (var operator : operators) {
				reply.add(operator.getSymbol());
				reply.add("\n");
			}
			return reply;
		} else if ("functions".equals(expression)) {
			var functions = evaluator.getFunctions();
			for (var function : functions) {
				reply.add(function.getName());
				reply.add("\n");
			}
			return reply;
		} else if ("constants".equals(expression)) {
			var constants = evaluator.getConstants();
			for (var constant : constants) {
				reply.add(constant.getName());
				reply.add("\n");
			}
			return reply;
		} else {
			try {
				var result = evaluator.evaluate(expression);
				formatter.accept(result);
			} catch (RuntimeException exception) {
				reply.add("求值异常：" + exception.getLocalizedMessage());
			}
			return reply;
		}
	}

}

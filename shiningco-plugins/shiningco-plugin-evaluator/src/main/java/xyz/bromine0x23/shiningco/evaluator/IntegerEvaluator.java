package xyz.bromine0x23.shiningco.evaluator;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Function;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;
import org.apache.commons.math3.complex.Complex;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

public class IntegerEvaluator extends AbstractEvaluator<BigInteger> {

	public static final Operator NEGATE       = new Operator("-", 1, Operator.Associativity.RIGHT, 3);
	public static final Operator PLUS         = new Operator("+", 2, Operator.Associativity.LEFT, 1);
	public static final Operator MINUS        = new Operator("-", 2, Operator.Associativity.LEFT, 1);
	public static final Operator MULTIPLY     = new Operator("*", 2, Operator.Associativity.LEFT, 2);
	public static final Operator DIVIDE       = new Operator("/", 2, Operator.Associativity.LEFT, 2);
	public static final Operator MODULO       = new Operator("%", 2, Operator.Associativity.LEFT, 2);
	public static final Operator EXPONENT     = new Operator("^", 2, Operator.Associativity.LEFT, 4);

	public static final Function ABS    = new Function("abs", 1);
	public static final Function MIN    = new Function("min", 1, Integer.MAX_VALUE);
	public static final Function MAX    = new Function("max", 1, Integer.MAX_VALUE);
	public static final Function SUM    = new Function("sum", 1, Integer.MAX_VALUE);
	public static final Function MODPOW = new Function("modpow", 3);
	public static final Function RANDOM = new Function("random", 0);

	private static final Operator[] OPERATORS = new Operator[]{NEGATE, MINUS, PLUS, MULTIPLY, DIVIDE, EXPONENT, MODULO};
	private static final Function[] FUNCTIONS = new Function[]{ABS, MIN, MAX, SUM, MODPOW, RANDOM};

	private static final Random random = new SecureRandom();

	private static final Pattern LITERAL_PATTERN = Pattern.compile("^(?:(?<base2>0b)(?<value2>[0-1]+)|(?<base8>0o?)(?<value8>[0-7]+)|(?<base16>0x)(?<value16>[0-9a-f]+)|(?<value10>[0-9]+))$", Pattern.CASE_INSENSITIVE);

	private static Parameters defaultParameters;

	public IntegerEvaluator() {
		super(getParameters());
	}

	@Override
	protected BigInteger toValue(String literal, Object evaluationContext) {
		var matcher = LITERAL_PATTERN.matcher(literal);
		if (matcher.matches()) {
			if (matcher.group("base2") != null) {
				return new BigInteger(matcher.group("value2"), 2);
			}
			if (matcher.group("base8") != null) {
				return new BigInteger(matcher.group("value8"), 8);
			}
			if (matcher.group("base16") != null) {
				return new BigInteger(matcher.group("value16"), 16);
			}
			return new BigInteger(matcher.group("value10"));
		}
		throw new IllegalArgumentException(literal+" is not an integer");
	}

	@Override
	protected BigInteger evaluate(Operator operator, Iterator<BigInteger> operands, Object evaluationContext) {
		if (NEGATE.equals(operator)) {
			return operands.next().negate();
		} else if (PLUS.equals(operator)) {
			return operands.next().add(operands.next());
		} else if (MINUS.equals(operator)) {
			return operands.next().subtract(operands.next());
		} else if (MULTIPLY.equals(operator)) {
			return operands.next().multiply(operands.next());
		} else if (DIVIDE.equals(operator)) {
			return operands.next().divide(operands.next());
		} else if (EXPONENT.equals(operator)) {
			return operands.next().pow(operands.next().intValueExact());
		} else if (MODULO.equals(operator)) {
			return operands.next().mod(operands.next());
		} else {
			return super.evaluate(operator, operands, evaluationContext);
		}
	}

	@Override
	protected BigInteger evaluate(Function function, Iterator<BigInteger> arguments, Object evaluationContext) {
		BigInteger result;
		if (ABS.equals(function)) {
			result = arguments.next().abs();
		} else if (MIN.equals(function)) {
			result = StreamSupport.stream(Spliterators.spliteratorUnknownSize(arguments, Spliterator.ORDERED), false)
				.reduce(BigInteger::min)
				.orElse(BigInteger.ZERO);
		} else if (MAX.equals(function)) {
			result = StreamSupport.stream(Spliterators.spliteratorUnknownSize(arguments, Spliterator.ORDERED), false)
				.reduce(BigInteger::max)
				.orElse(BigInteger.ZERO);
		} else if (SUM.equals(function)) {
			result = BigInteger.ZERO;
			while (arguments.hasNext()) {
				result = result.add(arguments.next());
			}
		} else if (MODPOW.equals(function)) {
			var b = arguments.next();
			var e = arguments.next();
			var m = arguments.next();
			result = b.modPow(e, m);
		} else if (RANDOM.equals(function)) {
			result = BigInteger.valueOf(random.nextLong());
		} else {
			result = super.evaluate(function, arguments, evaluationContext);
		}

		return result;
	}

	public static Parameters getDefaultParameters() {
		Parameters result = new Parameters();
		result.addOperators(Arrays.asList(OPERATORS));
		result.addFunctions(Arrays.asList(FUNCTIONS));
		result.addFunctionBracket(BracketPair.PARENTHESES);
		result.addExpressionBracket(BracketPair.PARENTHESES);
		return result;
	}

	private static Parameters getParameters() {
		var parameters = defaultParameters;
		if (parameters == null) {
			parameters = defaultParameters = getDefaultParameters();
		}
		return parameters;
	}

}

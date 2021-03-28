package xyz.bromine0x23.shiningco.evaluator;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Constant;
import com.fathzer.soft.javaluator.Function;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;
import org.apache.commons.math3.complex.Complex;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ComplexEvaluator extends AbstractEvaluator<Complex> {

	public static final Operator NEGATE    = new Operator("-", 1, Operator.Associativity.RIGHT, 3);
	public static final Operator CONJUGATE = new Operator("~", 1, Operator.Associativity.RIGHT, 3);
	public static final Operator PLUS      = new Operator("+", 2, Operator.Associativity.LEFT, 1);
	public static final Operator MINUS     = new Operator("-", 2, Operator.Associativity.LEFT, 1);
	public static final Operator MULTIPLY  = new Operator("*", 2, Operator.Associativity.LEFT, 2);
	public static final Operator DIVIDE    = new Operator("/", 2, Operator.Associativity.LEFT, 2);
	public static final Operator EXPONENT  = new Operator("^", 2, Operator.Associativity.LEFT, 4);

	public static final Function ABS  = new Function("abs", 1);
	public static final Function SIN  = new Function("sin", 1);
	public static final Function COS  = new Function("cos", 1);
	public static final Function TAN  = new Function("tan", 1);
	public static final Function SINH = new Function("sinh", 1);
	public static final Function COSH = new Function("cosh", 1);
	public static final Function TANH = new Function("tanh", 1);
	public static final Function ASIN = new Function("asin", 1);
	public static final Function ACOS = new Function("acos", 1);
	public static final Function ATAN = new Function("atan", 1);
	public static final Function EXP  = new Function("exp", 1);
	public static final Function LOG  = new Function("log", 1);
	public static final Function SQRT  = new Function("sqrt", 1);
	public static final Function SUM  = new Function("sum", 1, Integer.MAX_VALUE);
	public static final Function AVG  = new Function("avg", 1, Integer.MAX_VALUE);

	public static final Constant I   = new Constant("i");
	public static final Constant PI  = new Constant("pi");
	public static final Constant E   = new Constant("e");
	public static final Constant INF = new Constant("inf");

	private static final Operator[] OPERATORS = new Operator[]{NEGATE, MINUS, PLUS, MULTIPLY, DIVIDE, EXPONENT};
	private static final Function[] FUNCTIONS = new Function[]{ABS, SIN, COS, TAN, SINH, COSH, TANH, ASIN, ACOS, ATAN, EXP, LOG, SQRT, SUM, AVG};
	private static final Constant[] CONSTANTS = new Constant[]{I, PI, E};

	private static final Pattern SCIENTIFIC_NOTATION_PATTERN = Pattern.compile("([+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)[eE][+-]?\\d+)i?$");

	private static Parameters defaultParameters;

	public ComplexEvaluator() {
		super(getParameters());
	}

	@Override
	protected Iterator<String> tokenize(String expression) {
		var tokens    = new ArrayList<String>();
		var rawTokens = super.tokenize(expression);
		while (rawTokens.hasNext()) {
			tokens.add(rawTokens.next());
		}
		for (int i = 1; i < tokens.size() - 1; ++i) {
			testScientificNotation(tokens, i);
		}
		return tokens.iterator();
	}

	@Override
	protected Complex toValue(String literal, Object evaluationContext) {
		boolean imaginary = false;
		if (literal.endsWith("i") || literal.endsWith("I")) {
			imaginary = true;
			literal   = literal.substring(0, literal.length() - 1);
		}
		var parsePosition = new ParsePosition(0);
		var result        = NumberFormat.getNumberInstance(Locale.US).parse(literal, parsePosition);
		if (parsePosition.getIndex() == 0 || parsePosition.getIndex() != literal.length()) {
			// For an unknown reason, NumberFormat.getNumberInstance(...) returns a formatter that does not tolerate
			// scientific notation :-(
			// Let's try with Double.parse(...)
			if (!isScientificNotation(literal)) {
				throw new IllegalArgumentException(literal + " is not a number");
			}
			result = Double.parseDouble(literal);
		}
		return imaginary
			? Complex.valueOf(0, result.doubleValue())
			: Complex.valueOf(result.doubleValue(), 0);
	}

	@Override
	protected Complex evaluate(Operator operator, Iterator<Complex> operands, Object evaluationContext) {
		if (NEGATE.equals(operator)) {
			return operands.next().negate();
		} else if (CONJUGATE.equals(operator)) {
			return operands.next().conjugate();
		} else if (MINUS.equals(operator)) {
			return operands.next().subtract(operands.next());
		} else if (PLUS.equals(operator)) {
			return operands.next().add(operands.next());
		} else if (MULTIPLY.equals(operator)) {
			return operands.next().multiply(operands.next());
		} else if (DIVIDE.equals(operator)) {
			return operands.next().divide(operands.next());
		} else if (EXPONENT.equals(operator)) {
			return operands.next().pow(operands.next());
		} else {
			return super.evaluate(operator, operands, evaluationContext);
		}
	}

	@Override
	protected Complex evaluate(Function function, Iterator<Complex> arguments, Object evaluationContext) {
		Complex result;
		if (ABS.equals(function)) {
			result = Complex.valueOf(arguments.next().abs());
		} else if (SIN.equals(function)) {
			result = arguments.next().sin();
		} else if (COS.equals(function)) {
			result = arguments.next().cos();
		} else if (TAN.equals(function)) {
			result = arguments.next().tan();
		} else if (SINH.equals(function)) {
			result = arguments.next().sinh();
		} else if (COSH.equals(function)) {
			result = arguments.next().cosh();
		} else if (TANH.equals(function)) {
			result = arguments.next().tanh();
		} else if (ASIN.equals(function)) {
			result = arguments.next().asin();
		} else if (ACOS.equals(function)) {
			result = arguments.next().acos();
		} else if (ATAN.equals(function)) {
			result = arguments.next().atan();
		} else if (EXP.equals(function)) {
			result = arguments.next().exp();
		} else if (LOG.equals(function)) {
			result = arguments.next().log();
		} else if (SQRT.equals(function)) {
			result = arguments.next().sqrt();
		} else if (SUM.equals(function)) {
			result = Complex.ZERO;
			while (arguments.hasNext()) {
				result = result.add(arguments.next());
			}
		} else if (AVG.equals(function)) {
			int count = 0;
			result = Complex.ZERO;
			while (arguments.hasNext()) {
				result = result.add(arguments.next());
				++count;
			}
			result = result.divide(count);
		} else {
			result = super.evaluate(function, arguments, evaluationContext);
		}
		return result;
	}

	@Override
	protected Complex evaluate(Constant constant, Object evaluationContext) {
		if (I.equals(constant)) {
			return Complex.I;
		} else if (PI.equals(constant)) {
			return Complex.valueOf(Math.PI);
		} else if (E.equals(constant)) {
			return Complex.valueOf(Math.E);
		} else if (INF.equals(constant)) {
			return Complex.INF;
		} else {
			return super.evaluate(constant, evaluationContext);
		}
	}

	private void testScientificNotation(List<String> tokens, int index) {
		var previous  = tokens.get(index - 1);
		var current   = tokens.get(index);
		var next      = tokens.get(index + 1);
		var candidate = previous + current + next;
		if (isScientificNotation(candidate)) {
			tokens.set(index - 1, candidate);
			tokens.remove(index);
			tokens.remove(index);
		}
	}

	public static boolean isScientificNotation(String str) {
		var matcher = SCIENTIFIC_NOTATION_PATTERN.matcher(str);
		if (matcher.find()) {
			String matched = matcher.group();
			return matched.length() == str.length();
		} else {
			return false;
		}
	}

	public static Parameters getDefaultParameters() {
		Parameters result = new Parameters();
		result.addOperators(Arrays.asList(OPERATORS));
		result.addFunctions(Arrays.asList(FUNCTIONS));
		result.addConstants(Arrays.asList(CONSTANTS));
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

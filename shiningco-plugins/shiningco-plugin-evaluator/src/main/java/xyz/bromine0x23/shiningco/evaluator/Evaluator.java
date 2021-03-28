package xyz.bromine0x23.shiningco.evaluator;

import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.Function;
import com.fathzer.soft.javaluator.Parameters;
import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.special.Gamma;

import java.util.Arrays;
import java.util.Iterator;

public class Evaluator extends DoubleEvaluator {

	public static final Function   eval      = new Function("eval", 0, 1);
	public static final Function   Γ         = new Function("Γ", 1);
	public static final Function   gamma     = new Function("gamma", 1);
	public static final Function   lnΓ       = new Function("lnΓ", 1);
	public static final Function   lgamma    = new Function("lgamma", 1);
	public static final Function   ψ         = new Function("ψ", 1);
	public static final Function   digamma   = new Function("digamma", 1);
	public static final Function   lbeta     = new Function("lbeta", 2);
	public static final Function   lnΒ       = new Function("lnΒ", 2);
	public static final Function[] FUNCTIONS = new Function[]{eval, Γ, gamma, lnΓ, lgamma, ψ, digamma, lbeta, lnΒ};

	public Evaluator() {
		super(getDefaultParameters());
	}

	public static Parameters getDefaultParameters() {
		var result = DoubleEvaluator.getDefaultParameters();
		result.addFunctions(Arrays.asList(FUNCTIONS));
		result.addExpressionBracket(BracketPair.BRACKETS);
		return result;
	}

	@Override
	protected Double evaluate(Function function, Iterator<Double> arguments, Object evaluationContext) {
		Double result;
		if (eval.equals(function)) {
			if (arguments.hasNext()) {
				result = arguments.next();
			} else {
				result = 0.;
			}
		} else if (Γ.equals(function) || gamma.equals(function)) {
			result = Gamma.gamma(arguments.next());
		} else if (lnΓ.equals(function) || lgamma.equals(function)) {
			result = Gamma.logGamma(arguments.next());
		} else if (ψ.equals(function) || digamma.equals(function)) {
			result = Gamma.digamma(arguments.next());
		} else if (lnΒ.equals(function) || lbeta.equals(function)) {
			result = Beta.logBeta(arguments.next(), arguments.next());
		} else  {
			result = super.evaluate(function, arguments, evaluationContext);
		}
		return result;
	}

}

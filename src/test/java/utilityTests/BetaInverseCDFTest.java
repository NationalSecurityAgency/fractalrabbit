package utilityTests;

import java.util.Arrays;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math3.distribution.BetaDistribution;

/**
 * Used only for development
 * @author rwdarli
 * @see simulators.SporadicReporter
 */
public class BetaInverseCDFTest {

	private BetaDistribution dist;

	/**
	 * 
	 * @param alpha parameter of Beta distribution
	 * @param beta  parameter of Beta distribution
	 */
	public BetaInverseCDFTest(double alpha, double beta) {
		this.dist = new BetaDistribution(alpha, beta);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double alpha = 4.17;
		double beta = 2.98;
		BetaInverseCDFTest test = new BetaInverseCDFTest(alpha, beta);
		Random g = new Random();
		DoubleUnaryOperator adjust = x -> Math.floor(x) + test.dist.inverseCumulativeProbability(x - Math.floor(x));
		long n = 20;
		double interval = 10.0;
		// Sorted
		Double[] t = g.doubles(n, 0.0, interval).boxed().sorted().toArray(Double[]::new);
		Double[] tseas = Arrays.stream(t).map(x -> adjust.applyAsDouble(x)).toArray(Double[]::new);
		System.out.println("Times with seasonal adjustments");
		for (int i = 0; i < n; i++) {
			System.out.println(t[i] + ", " + tseas[i]);
		}

	}

}

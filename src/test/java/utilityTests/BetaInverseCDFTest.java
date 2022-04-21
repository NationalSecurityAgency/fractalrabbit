package utilityTests;
/**
 * Passed 12.12.2018
 * This functionality is needed for SporadicReporter
 */


import java.util.Arrays;
import java.util.Random;
import java.util.function.DoubleFunction;

import org.apache.commons.math3.distribution.BetaDistribution;

/**
 * @author rwdarli
 *
 */
public class BetaInverseCDFTest {

	BetaDistribution dist;

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
		DoubleFunction adjust = x -> Math.floor(x) + test.dist.inverseCumulativeProbability(x - Math.floor(x));
		long n = 20;
		double interval = 10.0;
		// Sorted
		Double[] t = g.doubles(n, 0.0, interval).boxed().sorted().toArray(Double[]::new);
		Double[] tseas = Arrays.stream(t).map(x -> adjust.apply(x)).toArray(Double[]::new);
		System.out.println("Times with seasonal adjustments");
		for (int i = 0; i < n; i++) {
			System.out.println(t[i] + ", " + tseas[i]);
		}

	}

}

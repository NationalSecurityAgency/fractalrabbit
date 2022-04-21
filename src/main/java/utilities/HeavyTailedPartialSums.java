
package utilities;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.Collectors;

/**
 * 
 * Generates partial sums (Y_1 + ... + Y_r)/S, where S = Y_1 + ... + Y_m and the
 * Y_i are i.i.d. truncated Pareto in the range (delta, 1]. Tail exponent of
 * Pareto is alpha.
 * <p>
 * Suggested parameters: delta = 0.001; alpha = -1.5
 * 
 * This class replaces HeavyTailPointProcess
 * 
 * @author rwdarli
 *
 */
public class HeavyTailedPartialSums {
	private Random g;

	public HeavyTailedPartialSums() {
		g = new Random();
	}

	/**
	 * 
	 * @param size  dimension of vector to be returned
	 * @param delta least time between distinct events, greater than zero
	 * @param alpha tail exponent for (truncated) Pareto law, less than minus 1
	 *              <p>
	 *              Maximum time between distinct events can be taken as 1.0.
	 * @return increasing sequence of partial sums of Pareto random random
	 *         variables, divided by total sum.
	 */
	public List<Double> generate(int size, double delta, double alpha) {
		/*
		 * Set upper bound for uniform sampling
		 */
		double u1 = Math.pow(delta, 1.0 / alpha); // u1 > 1 since alpha < 0
		// Simulate truncated Pareto random variables
		double[] y = g.doubles(size + 1).boxed().mapToDouble(x -> Math.pow(1.0 + (u1 - 1.0) * x, alpha)).toArray();
		// Partial sums
		DoubleBinaryOperator partialSum = (y1, y2) -> y1 + y2;
		Arrays.parallelPrefix(y, partialSum);
		double sum = y[size]; // this index exists!
		// drop last value which must be 1.0
		return Arrays.stream(y).limit(size).boxed().map(z -> z / sum).collect(Collectors.toList());
	}

}

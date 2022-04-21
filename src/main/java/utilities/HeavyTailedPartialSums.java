/**
 * Generates partial sums (Y_1 + ... + Y_r)/S
 * where S = Y_1 + ... + Y_m
 * and the Y_i are i.i.d. truncated Pareto in the range (delta, 1]
 * Tail exponent is alphaDaily.
 * Suggested parameters: delta = 0.001; alphaDaily = -1.5
 * 
 * This class replaces HeavyTailPointProcess
 */
package utilities;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.Collectors;

/**
 * @author rwdarli
 *
 */
public class HeavyTailedPartialSums {
	Random g;
	/**
	 * 
	 */
	public HeavyTailedPartialSums() {
		g = new Random();
	}
	/*
	 * @param count = dimension of vector to be returned
	 * 
	 * @param delta > 0.0 meaning the least time between distinct events
	 * 
	 * Maximum time between distinct events can be taken as 1.0
	 * 
	 * @param alpha < -1.0
	 * 
	 * t^alpha is tail probability, up to cut-off
	 */
	public List<Double> generate(int count, double delta,  double alpha) {
		/*
		 * Set upper bound for uniform sampling
		 */
		double u1 = Math.pow(delta, 1.0 / alpha); // u1 > 1 since alpha < 0
		// Simulate truncated Pareto random variables
		double[] y = g.doubles(count + 1).boxed().mapToDouble(x->Math.pow(1.0 + (u1 - 1.0) * x, alpha) ).toArray();
		// Partial sums
		DoubleBinaryOperator partialSum = (y1, y2) -> y1 + y2;
		Arrays.parallelPrefix(y, partialSum); 
		double sum = y[count]; // this index exists!
		// drop last value which must be 1.0
		return Arrays.stream(y).limit(count).boxed().map(z->z/sum).collect(Collectors.toList());		
	}

}

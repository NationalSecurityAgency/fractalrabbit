/**
 * Written August 4, 2018. Not tested.
 */
package utilities;

import java.util.Arrays;
import java.util.Random;

/**
 * @author RWRD
 * 
 */
public class CensoredMultinomialTrial {

	/**
	 * Sample on of the categories: {0,1,2,... n-1}. Two vectors are used: (1) rates
	 * r[0], r[1], ..., r[n-1], all non-negative doubles, (2) boolean vector showing
	 * which states are allowed. In application, allowed states will be UNVISITED
	 * states.
	 */
	private final Random g;
	private final int dimension;
	private final double[] q; // unnormalized probability vector of given dimension, different each time
				// sampling occurs

	public CensoredMultinomialTrial(int n) {
		this.g = new Random();
		this.dimension = n;
		this.q = new double[this.dimension];
	}

	/*
	 * @parameter r - vector of unnormalized rates
	 * 
	 * @parameter u - u[j] true means j has been visited, hence unavailable for sampling
	 */
	public int sample(double[] r, boolean[] u) throws IllegalArgumentException {
		if (!(r.length == this.dimension) | !(u.length == this.dimension)) {
			throw new IllegalArgumentException(" Dimensions do not match");
		} else {
			Arrays.setAll(this.q, i -> u[i] ? 0.0: r[i]); // rate is zero if u[i] is true
			// Multinomial sampling: generate threshold uniformly in (0, rateSum)
			double rateSum = Arrays.stream(this.q).sum();
			double threshold = rateSum * this.g.nextDouble();
			int index = 0;
			double rateAdder = this.q[0];
			// The index i is returned when q[0] + ... + q[i-1] =< threshold < q[0] + ... +
			// q[i]
			while (rateAdder < threshold) {
				index++;
				rateAdder += this.q[index];
			}
			return index;
		}
	}

}

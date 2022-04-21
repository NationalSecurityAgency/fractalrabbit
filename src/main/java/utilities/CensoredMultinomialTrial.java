
package utilities;

import java.util.Arrays;
import java.util.Random;

/**
 * Sample one of the categories: {0,1,2,... n-1}. Two vectors are used:
 * <p>
 * (1) rates r[0], r[1], ..., r[n-1], all non-negative doubles,
 * <p>
 * (2) boolean vector showing which states are allowed.
 * <p>
 * In application, allowed states will be UNVISITED states.
 * 
 * @author rwdarli
 * 
 */
public class CensoredMultinomialTrial {

	private Random g;
	private final int dimension;
	/*
	 * unnormalized probability vector of given dimension, different each time
	 * sampling occurs
	 */
	private double[] q;

	/**
	 * 
	 * @param n number of outcomes of a multinomial trial
	 */
	public CensoredMultinomialTrial(int n) {
		g = new Random();
		this.dimension = n;
		this.q = new double[this.dimension];
	}

	/**
	 * 
	 * @param r vector of unnormalized rates
	 * @param u boolean array, where u[j] true means j has been visited, hence
	 *          unavailable for sampling
	 * @return an index from 0 to n-1. The index i is returned when
	 * 
	 *         <pre>
	 * q[0] + ... +	q[i-1] &#8804; threshold &#8808; q[0] + ... + q[i]
	 *         </pre>
	 * 
	 * @throws IllegalArgumentException
	 */
	public int sample(double[] r, boolean[] u) throws IllegalArgumentException {
		if (!(r.length == this.dimension) | !(u.length == this.dimension)) {
			throw new IllegalArgumentException(" Dimensions do not match");
		} else {
			Arrays.setAll(this.q, i -> u[i] ? 0.0 : r[i]); // rate is zero if u[i] is true
			// Multinomial sampling: generate threshold uniformly in (0, rateSum)
			double rateSum = Arrays.stream(this.q).sum();
			double threshold = rateSum * this.g.nextDouble();
			int index = 0;
			double rateAdder = this.q[0];
			//
			while (rateAdder < threshold) {
				index++;
				rateAdder += this.q[index];
			}
			return index;
		}
	}

}

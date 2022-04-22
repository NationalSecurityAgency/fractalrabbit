
package utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Sample from a (small) set of states, weighted according to rate times
 * frequency
 * 
 * @see simulators.Retropreferential
 * 
 * @author rwdarli
 * 
 */
public class ReweightedMultinomialTrial {

	private Random g;
	// For integer k < n, g.get(k) will be an unnormalized rate of transition to k
	private Map<Integer, Double> q;

	/**
	 * Constructor
	 */
	public ReweightedMultinomialTrial() {
		g = new Random();
		this.q = new HashMap<Integer, Double>();
	}

	/**
	 * 
	 * @param places subset of {0, 1, ..., n-1}
	 * @param r      vector of unnormalized rates, indices from 0 to n-1
	 * @param f      vector of visit frequencies, indices from 0 to n-1
	 * @return Integer sampled from places, weighted according to rate times
	 *         frequency
	 * @throws IllegalArgumentException
	 */
	public Integer sample(Set<Integer> places, double[] r, double[] f) throws IllegalArgumentException {
		if (!(r.length == f.length)) {
			throw new IllegalArgumentException(" Dimensions do not match");
		} else {
			this.q.clear();
			for (Integer k : places) {
				this.q.put(k, f[k] * r[k]); // weighting of rates by visit frequencies
			}
			// Multinomial sampling: generate threshold uniformly in (0, rateSum)
			double rateSum = this.q.values().stream().reduce(0.0, Double::sum);
			double threshold = rateSum * this.g.nextDouble();
			double rateAdder = 0.0;
			Integer index = Integer.MIN_VALUE;
			/*
			 * Main point of this class is that this loop covers O(sqrt(n)) steps, not O(n),
			 * for the classic retro-preferential process, since places.size() is
			 * O(sqrt(n)).
			 */
			for (Integer k : this.q.keySet()) {
				rateAdder += this.q.get(k);
				if (rateAdder > threshold) {
					index = k;
					break;
				}
			}
			return index;
		}
	}

}

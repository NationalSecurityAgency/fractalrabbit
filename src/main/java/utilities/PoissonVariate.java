
package utilities;

import java.util.Random;

/**
 * Generator of Poisson random variates
 * 
 * @author rwdarli
 *
 */
public class PoissonVariate {

	private double lambda;
	private Random g;

	/**
	 * 
	 * @param lam parameter for a Poisson distribution (mean and variance)
	 */
	public PoissonVariate(double lam) {
		this.lambda = lam;
		this.g = new Random();
	}

	/**
	 * 
	 * @return random variable with specified Poisson distribution
	 */
	public int generate() {
		int count = 0;
		double sum = 0.0;
		while (sum < this.lambda) {
			sum -= Math.log(g.nextDouble());
			count++;
		}
		return count;
	}

}

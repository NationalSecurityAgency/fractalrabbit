/**
 * Passed test 1.13.19
 */
package utilities;

import java.util.Random;

/**
 * @author RWRD
 *
 */
public class PoissonVariate {

	/**
	 * 
	 */
	double lambda;
	Random g;
	public PoissonVariate(double lam) {
		this.lambda = lam;
		this.g= new Random();
	}
	
	public int generate() {
		int count = 0;
		double sum = 0.0;
		while(sum < this.lambda) {
			sum-=Math.log(g.nextDouble());
			count++;
		}
		return count;
	}

}

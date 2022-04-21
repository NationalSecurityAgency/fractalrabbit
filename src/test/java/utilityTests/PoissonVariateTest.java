package utilityTests;
/**
 * Passed test 1.13.19
 */


import java.util.Arrays;
import java.util.IntSummaryStatistics;

import utilities.PoissonVariate;

/**
 * @author rwdarli
 *
 */
public class PoissonVariateTest {

	PoissonVariate pv;
	public PoissonVariateTest(double lam) {
		this.pv = new PoissonVariate(lam);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double lambda = 1000.0;
		PoissonVariateTest test = new PoissonVariateTest(lambda);
		int ntrials = 100;
		int[] data = new int[ntrials];
		Arrays.setAll(data, i->test.pv.generate());
		
		IntSummaryStatistics stats = Arrays.stream(data).summaryStatistics();
		System.out.println(ntrials + " trials: " + stats);

	}

}

package utilityTests;
/**
 * Passed tests August 7, 2018
 */


import java.util.Arrays;

import utilities.CensoredMultinomialTrial;

/**
 * @author rwdarli August 7, 2018
 */
public class CensoredMultinomialTest {

	CensoredMultinomialTrial cmt;

	public CensoredMultinomialTest(int n) {
		cmt = new CensoredMultinomialTrial(n);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int n = 51;
		CensoredMultinomialTest test = new CensoredMultinomialTest(n);
		double[] rate = new double[n];
		/*
		 * in this test the final rate component equals all the others put together
		 */
		Arrays.setAll(rate, i -> (i < 50) ? 1.0 : 50.0);
		boolean[] offrange = new boolean[n];
		Arrays.fill(offrange, false);
		int s = 30;
		System.out.println("Draw " + s + " samples from distribution on {0, 1, ..., " + (n-1) + "}");
		for (int t = 0; t < s; t++) {
			System.out.print(test.cmt.sample(rate, offrange) + "; ");
		}
		System.out.println();
		System.out.println("Same where " + (n-1) + " is unavailable.");
		offrange[n-1] = true;
		for (int t = 0; t < s; t++) {
			System.out.print(test.cmt.sample(rate, offrange) + "; ");
		}
		System.out.println();
		System.out.println("Same where " + (n-1) + "  and states 0 to 9 are unavailable.");
		Arrays.fill(offrange, 0, 10, true);
		for (int t = 0; t < s; t++) {
			System.out.print(test.cmt.sample(rate, offrange) + "; ");
		}
	}

}

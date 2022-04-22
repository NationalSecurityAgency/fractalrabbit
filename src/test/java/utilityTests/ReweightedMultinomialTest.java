package utilityTests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import utilities.ReweightedMultinomialTrial;

/**
 * Used only for development
 * 
 * @author rwdarli
 * @see utilities.ReweightedMultinomialTrial
 */
public class ReweightedMultinomialTest {

	ReweightedMultinomialTrial rmt;

	public ReweightedMultinomialTest() {
		this.rmt = new ReweightedMultinomialTrial();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int n = 60; // overall set of states
		final int s = 35; // sample size
		double[] rate = new double[n];
		double[] freq = new double[n];
		Random g = new Random();
		Arrays.setAll(rate, i -> g.nextDouble()); // random rates
		final double mu = 4.0;
		Arrays.setAll(freq, i -> Math.floor(mu * g.nextDouble())); // round a random double down
		/*
		 * the places are states whose frequency is not zero
		 */
		Set<Integer> places = new HashSet<Integer>();
		for (int j = 0; j < n; j++) {
			if (freq[j] > 0.0) {
				places.add(j);
			}
		}
		ReweightedMultinomialTest test = new ReweightedMultinomialTest();
		System.out.println("Draw " + s + " samples from distribution on " + places.size() + " integers.");
		for (int t = 0; t < s; t++) {
			System.out.print(test.rmt.sample(places, rate, freq) + "; ");
		}

	}

}

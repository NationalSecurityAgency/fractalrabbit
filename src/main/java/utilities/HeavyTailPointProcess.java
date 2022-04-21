/**
 * @deprecated 11.28.2018
 * Passed tests 8.9.2018
 * 
 * Generate truncated Pareto random variables, 
 * X:=U^alphaDaily, U~Uniform(u0, u1), where alphaDaily < -1
 * Cumulative sums give a Point Process with heavy-tailed inter-arrival times.
 * 
 * Ingredient for sporadic reporting construction
 */
package utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author rwdarli 
 */
public class HeavyTailPointProcess {

	Random g;

	public HeavyTailPointProcess() {
		g = new Random();
	}
	/*
	 * @param duration = upper bound for last arrival
	 * 
	 * @param lo > 0.0 meaning the least time between distinct events
	 * 
	 * @param hi > lo meaning the maximum time between distinct events
	 * 
	 * @param alphaDaily < -1.0
	 * 
	 * t^alphaDaily is tail probability, up to cut-off
	 * 
	 * paretoMean:
	 * (Math.pow(u0, c) - Math.pow(u1, c)) / ((-1.0 - alphaDaily) *(u1 - u0));
	 * with c = 1.0 + alphaDaily;
	 * Expect 1.0 / paretoMean events per day. Validated
	 * 
	 */

	public double[] generate(double duration, double lo, double hi, double alpha) {
		/*
		 * Set upper and lower bounds for uniform sampling
		 */
		double u0 = Math.pow(hi, 1.0 / alpha); // hi gives LOWER bound on U
		double u1 = Math.pow(lo, 1.0 / alpha); // lo gives UPPER bound on U
		List<Double> timeSeries = new ArrayList<>();
		double lastTime = 0.0;
		double eta; // increment
		while (lastTime < duration) {
			eta = Math.pow(u0 + (u1 - u0) * g.nextDouble(), alpha); // truncated Pareto
			lastTime += eta;
			timeSeries.add(lastTime);
		}
		double[] x = new double[timeSeries.size() - 1]; // skip last time, which exceeds bound
		Arrays.setAll(x, j -> timeSeries.get(j));
		return x;
	}
	
	public double meanRate(double lo, double hi, double alpha) {
		double u0 = Math.pow(hi, 1.0 / alpha); // hi gives LOWER bound on U
		double u1 = Math.pow(lo, 1.0 / alpha);
		double c = 1.0 + alpha; 
		double paretoMean = (Math.pow(u0, c) - Math.pow(u1, c)) / ((-1.0 - alpha) *(u1 - u0));
		return paretoMean;
	}
	
	/*
	 * It would be desirable, given a desired mean rate from 1/8 to 1/30,
	 * to invert the monotone function, and supply alphaDaily.
	 */

}

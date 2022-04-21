
package deprecated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generate truncated Pareto random variables, X:=U^alphaDaily, U~Uniform(u0,
 * u1), where alphaDaily < -1 Cumulative sums give a Point Process with
 * heavy-tailed inter-arrival times.
 * 
 * @author rwdarli
 * @deprecated
 */
public class HeavyTailPointProcess {

	private Random g;
	public HeavyTailPointProcess() {
		g = new Random();
	}

	/**
	 * 
	 * @param duration upper bound for last arrival
	 * @param lo       least time between distinct events
	 * @param hi       maximum time between distinct events
	 * @param alpha    less than minus 1.0; t^alpha is tail probability, up to
	 *                 cut-off
	 * @return time series whose inter-event times are truncated Pareto random
	 *         variables
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

	/**
	 * 
	 * paretoMean: (Math.pow(u0, c) - Math.pow(u1, c)) / ((-1.0 - alpha) *(u1 -
	 * u0)); with c = 1.0 + alpha; Expect 1.0 / paretoMean events per day.
	 * 
	 * @param lo    least time between distinct events
	 * @param hi    maximum time between distinct events
	 * @param alpha less than minus 1.0; t^alpha is tail probability, up to cut-off
	 * @return mean rate
	 */
	public double meanRate(double lo, double hi, double alpha) {
		double u0 = Math.pow(hi, 1.0 / alpha); // hi gives LOWER bound on U
		double u1 = Math.pow(lo, 1.0 / alpha);
		double c = 1.0 + alpha;
		double paretoMean = (Math.pow(u0, c) - Math.pow(u1, c)) / ((-1.0 - alpha) * (u1 - u0));
		return paretoMean;
	}


}

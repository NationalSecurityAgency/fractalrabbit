/* THIS PUBLIC DOMAIN SOFTWARE WAS PRODUCED BY AN EMPLOYEE OF U.S. GOVERNMENT 
 * AS PART OF THEIR OFFICIAL DUTIES.
 */

package simulators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.math3.distribution.BetaDistribution;

import utilities.HeavyTailedPartialSums;
import utilities.IrregularBinAssignment;
import utilities.Metrizable;

/**
 * 
 * Stochastic embedding of visit sequence in continuous time, followed by
 * generation of report times, with daily seasonality. Impossible travel is
 * precluded by allowing no reportTimes during "travel time". In order to allow
 * multiple report sets for the same trajectory, the continuous time embedding
 * is logically separate from the reporting.
 * 
 * @see utilities.HeavyTailedPartialSums
 * @see utilities.IrregularBinAssignment
 * @see org.apache.commons.math3.distribution.BetaDistribution for Inverse CDF
 *      for Beta.
 * 
 * @author rwdarli
 *
 */
public class SporadicReporter {
	/*
	 * finite metric space - States of the process
	 */
	private Metrizable[] points;
	/*
	 * Truncated Pareto inter-event times, with final parameters
	 */
	private HeavyTailedPartialSums htps;
	private IrregularBinAssignment iba;
	private BetaDistribution dist;

	private double speedbound; // units per day
	// private double kilometersPerUnit; // # km per one distance unit for points
	/**
	 * Daily easonality is introduced via a Beta distribution with static
	 * parameters.
	 */
	static final double alphaDaily = 4.17; // seasonality
	static final double betaDaily = 2.98; // seasonality

	/*
	 * Once an arrival time T_i to x_i is known, prevent x_{i-1} reportTimes from
	 * being inserted at times T_i - s which would potentially violate speed bound.
	 */
	private List<Double> uniformOrderStatistics;
	private List<Double> cumulativeTravelTime;
	private List<Double> reportTimes;
	private List<Integer> reportPlaces;
	private Random g;

	public SporadicReporter(Metrizable[] pointsArray, double maxKmPerHour, double kmper1) {

		// this.kilometersPerUnit = kmper1; // Drop?
		this.speedbound = 24.0 * maxKmPerHour / (kmper1); // measured in units per day
		this.points = pointsArray;
		this.htps = new HeavyTailedPartialSums();
		this.iba = new IrregularBinAssignment();
		this.dist = new BetaDistribution(alphaDaily, betaDaily);
		this.uniformOrderStatistics = new ArrayList<Double>();
		this.cumulativeTravelTime = new ArrayList<Double>();
		this.reportTimes = new ArrayList<Double>();
		this.reportPlaces = new ArrayList<Integer>();

		this.g = new Random();
	}

	/**
	 * Embeds a trajectory in continuous time, allowing time for travel from place
	 * to place.
	 * 
	 * @see fractalRabbitGenerator.MainClassFR
	 * @param visitSequence indices of places visited during a trajectory
	 */
	public void embedTrajectory(List<Integer> visitSequence) {
		int moves = visitSequence.size() - 1;

		/*
		 * to account for dead time while traveling, accumulate travel times
		 */
		this.cumulativeTravelTime.clear();
		this.cumulativeTravelTime.add(0.0); // no time needed to reach first state
		double shift;
		double lastTime = 0.0;
		for (int j = 0; j < moves; j++) {
			shift = this.points[visitSequence.get(j)].distanceTo(this.points[visitSequence.get(j + 1)]);
			lastTime += shift / this.speedbound; // time needed for transition from state j to state j+1
			this.cumulativeTravelTime.add(lastTime);
		}

		/*
		 * to determine when transitions occur this list sets up the bins into which the
		 * time series observations are grouped
		 */
		this.uniformOrderStatistics.clear();
		this.uniformOrderStatistics.add(0.0); // first element
		// generate i.i.d. uniform(0,1) random variables, sort them, and add them to
		// list
		this.uniformOrderStatistics.addAll(g.doubles(moves).boxed().sorted().collect(Collectors.toList()));
		this.uniformOrderStatistics.add(1.0); // last element

	}

	/**
	 * Generates bursty report times with heavy-tailed inter-event times.
	 * 
	 * @see utilities.HeavyTailedPartialSums
	 * 
	 * @param visitSequence indices of places visited during a trajectory
	 * @param count         number of reports
	 * @param delta         least time (exceeds 0.0) between distinct events
	 * @param alpha         (less than minus 1) t^alpha is inter-event time tail probability,
	 *                      up to cut-off
	 * @param days          days available for report generation, before subtracting
	 *                      travel time.
	 * 
	 */
	public void generateReports(List<Integer> visitSequence, int count, double delta, double alpha, double days) {
		this.reportTimes.clear();
		this.reportPlaces.clear();
		int moves = visitSequence.size() - 1;
		double travelDuration = this.cumulativeTravelTime.get(moves);
		double daysAvailable = days - travelDuration; // # days available for report generation
		if (daysAvailable > 0.0) {
			List<Double> timeSeries = htps.generate(count, delta, alpha);
			/*
			 * uniformOrderStatistics, including end points 0.0, 1.0, supply the bins
			 * timeSeries are to be assigned to bins, to decide on the current place
			 */
			iba.assign(this.uniformOrderStatistics, timeSeries);
			/*
			 * If the time series has 10 elements, and visitSequence has 4, indexed by 0, 1,
			 * 2, 3 bin assignment could be 0, 0, 0, 1, 1, 3, 3, 3, 3, 3 See paper for full
			 * logical explanation
			 */
			int binAssigned;
			/*
			 * Each element of time series is used to generate a reported place and time,
			 * with daily seasonal adjustment
			 */
			double t, adjustedTime;
			for (int j = 0; j < count; j++) {
				binAssigned = iba.getPlacements().get(j); // how far along in the trajectory are we?
				this.reportPlaces.add(visitSequence.get(binAssigned)); // where exactly are we?
				// apply scale factor to report time, then add the travel time to reach the
				// location. Also make seasonality adjustment
				t = this.cumulativeTravelTime.get(binAssigned) + daysAvailable * timeSeries.get(j);
				adjustedTime = Math.floor(t) + this.dist.inverseCumulativeProbability(t - Math.floor(t));
				this.reportTimes.add(adjustedTime);
			}

		} else {
			System.out.println("Duration in days is insufficient for travel. No reports generated.");
		}
	}

	/**
	 * @return the cumulativeTravelTime
	 */
	public List<Double> getCumulativeTravelTime() {
		return this.cumulativeTravelTime;
	}

	/**
	 * @return the reportTimes
	 */
	public List<Double> getReportTimes() {
		return this.reportTimes;
	}

	/**
	 * @return the reportPlaces
	 */
	public List<Integer> getReportPlaces() {
		return this.reportPlaces;
	}

}

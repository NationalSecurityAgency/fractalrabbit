/* THIS PUBLIC DOMAIN SOFTWARE WAS PRODUCED BY AN EMPLOYEE OF U.S. GOVERNMENT 
 * AS PART OF THEIR OFFICIAL DUTIES.
 */

/**
 * 
 * Generates the Classic Retro-preferential Process.
 * The visited points can be of any metrizable type (e.g. graph vertices).
 * Aims for O(n^{3/2}) performance with n points,  achieved by not computing interpoint 
 * distances between unvisited points, and parsimonious Multinomial sampling at a revisit step.
 * 
 * @author rwdarli
 */
package simulators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.IntToDoubleFunction;

import utilities.CensoredMultinomialTrial;
import utilities.Metrizable;
import utilities.ReweightedMultinomialTrial;

public class Retropreferential {

	private int numPlaces;
	/*
	 * finite metric space - States of the process
	 */
	private Metrizable[] points;
	/*
	 * rate.get(k)[j] is raw rate of transition from points[j] to points[k],
	 * computable on demand using freshRateVector(k)
	 */
	private Map<Integer, double[]> rate;
	private double exponent; // Negative, e.g. -2.0
	private Set<Integer> placesVisited;
	private boolean[] isVisited;
	private double[] visitTally;
	private ReweightedMultinomialTrial revisitor;
	private CensoredMultinomialTrial explorer;
	private Random g;

	/**
	 * Constructor based upon a set of points in a metric space.
	 * 
	 * @param pointsArray finite metric space - States of the process
	 * @param exponent    - used for conversion of distances to transition rates.
	 *                    For example, -2.0 gives quadratic decay.
	 * @see utilities.Metrizable
	 */
	public Retropreferential(Metrizable[] pointsArray, double exponent) {
		this.points = pointsArray;
		this.numPlaces = pointsArray.length;
		this.exponent = exponent;
		// keep track of Set of indices which have occurred so far
		this.placesVisited = new HashSet<Integer>();
		this.isVisited = new boolean[this.numPlaces];
		this.visitTally = new double[this.numPlaces];
		this.revisitor = new ReweightedMultinomialTrial(); // tested
		this.explorer = new CensoredMultinomialTrial(this.numPlaces); // tested
		this.rate = new HashMap<Integer, double[]>();
		this.g = new Random();
	}

	/**
	 * Lazy computation of transition rates.
	 * 
	 * @param k index of point in <code>pointsArray</code>. When this point is
	 *          visited for the first time, transition rates from it are computed.
	 *          This lazy approach reduces complexity, because number of distinct
	 *          points visited grows sublinearly.
	 */
	public void insertRate(int k) {
		double[] vector = new double[this.numPlaces];
		IntToDoubleFunction proximity = (j -> (j == k) ? 0.0
				: Math.pow(this.points[k].distanceTo(this.points[j]), this.exponent));
		Arrays.setAll(vector, proximity);
		this.rate.put(k, vector);
	}

	/**
	 * Method to create a trajectory. It may be invoked many times with different
	 * parameters {phi, steps, start}. No need to clear the rate Map.
	 * 
	 * @param phi   exploration parameter determining growth rate of pure birth
	 *              process Y, the # of places visited
	 * @param steps number of steps in the trajectory
	 * @param start  index of the starting point in the list of places
	 * @return
	 */
	public List<Integer> trajectory(double phi, int steps, int start) {
		// clear visit data
		this.placesVisited.clear();
		Arrays.fill(this.isVisited, false);
		/*
		 * Initialize the visits array, the trajectory, and places visited, to show one
		 * visit to starting point
		 */
		Arrays.setAll(this.visitTally, i -> (i == start) ? 1.0 : 0.0);
		int state = start;
		// indices of sequence of places visited along trajectory
		ArrayList<Integer> x = new ArrayList<Integer>();
		x.add(state);
		// this data may have been left over from a previous trajectory
		if (!this.rate.keySet().contains(state)) {
			this.insertRate(state);
		}
		this.placesVisited.add(state);
		this.isVisited[state] = true;

		/*
		 * Retro-preferential loop, truncated when every place has been visited.
		 */
		boolean explore;
		while (x.size() < steps && this.placesVisited.size() < this.numPlaces) {
			/*
			 * Pure birth process with birth rate depending on # distinct places visited.
			 * "explore = true" means a birth (new place to visit). First step is ALWAYS an
			 * explore step.
			 */
			explore = (g.nextDouble() < phi / (phi + this.placesVisited.size() - 1.0));
			// explore or revisit dichotomy: state update, and fresh rate computation
			// System.out.println("Is a rate vector available? " +
			// this.rate.keySet().contains(state));
			state = explore ? this.explorer.sample(this.rate.get(state), this.isVisited)
					: this.revisitor.sample(this.placesVisited, this.rate.get(state), this.visitTally);
			x.add(state); // extend trajectory
			this.visitTally[state]++; // clock up another visit to this state
			if (!this.rate.keySet().contains(state)) {
				this.insertRate(state); // compute transition rates out of this state
			}
			if (explore) {
				this.placesVisited.add(state); // update places Visited
				this.isVisited[state] = true; // update "is Visited"
			}
		}
		return x;
	}

	/**
	 * @return the visitTally
	 */
	public double[] getVisitTally() {
		return visitTally;
	}

	/**
	 * @return the placesVisited
	 */
	public Set<Integer> getPlacesVisited() {
		return placesVisited;
	}

	/**
	 * @return the isVisited
	 */
	public boolean[] getIsVisited() {
		return isVisited;
	}

}

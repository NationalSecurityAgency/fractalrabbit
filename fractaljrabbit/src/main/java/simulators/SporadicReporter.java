/**
 * Simplified version of November 28, 2018.
 * Passed test Passed 11.28.2018, 12.12.2018
 * Random embedding of visit sequence in continuous time is performed BEFORE
 * generation of report times, which uses the HeavyTailedPartialSums class.
 * Impossible travel is precluded by allowing no reportTimes during "travel time".
 *
 * In order to allow multiple report sets for the same trajectory, the continuous time
 * embedding is logically separate from the reporting.
 * Seasonality is introduced via the Commons Math Inverse CDF for Beta.
 * Currently seasonality adjustment is messing up the sped bound by factor of 1.8
 */
package simulators;

import org.apache.commons.math3.distribution.BetaDistribution;
import utilities.HeavyTailedPartialSums;
import utilities.IrregularBinAssignment;
import utilities.Metrizable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author RWRD
 *
 */
public class SporadicReporter {
    /*
     * finite metric space - States of the process
     */
    Metrizable[] points;

    /**
     * Truncated Pareto inter-event times, with final parameters
     */
    HeavyTailedPartialSums htps;
    IrregularBinAssignment iba;
    BetaDistribution dist;

    double speedbound; // units per day
    double kilometersPerUnit; // # km per one distance unit for points
    static double  alphaDaily = 4.17; // seasonality
    static double betaDaily = 2.98; // seasonality

    /*
     * Once an arrival time T_i to x_i is known, prevent x_{i-1} reportTimes from
     * being inserted at times T_i - s which would potentially violate speed bound.
     */
    List<Double> uniformOrderStatistics;
    List<Double> cumulativeTravelTime;
    List<Double> reportTimes;
    List<Integer> reportPlaces;
    Random g;

    public SporadicReporter(Metrizable[] pointsArray, double maxKmPerHour, double kmper1) {

        this.kilometersPerUnit = kmper1; // Drop?
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

    /*
     * Typical value of lo = 1.0 / (30.0 * 24.0); // two minutes in days Typical
     * value of hi = 1.5; // day & a half Typical value of alphaDaily = -2.5; // tail
     * parameter
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


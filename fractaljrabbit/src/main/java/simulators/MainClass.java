package simulators;

/** THIS PUBLIC DOMAIN SOFTWARE WAS PRODUCED BY AN EMPLOYEE OF U.S. GOVERNMENT
 * Produces an output csv file where each line has the format
 * identifier (integer), time (days), latitude, longitude
 * or else
 * identifier (integer), time (days), x (km), y (km)
 * Here identifier refers to a specific traveler.
 *
 * A set of input parameters is here given explicitly.
 * Later these will be input through a csv file
 * args[0] will be a path to write the output file
 *
 * Extra functionality is the generation of MULTIPLE trajectories in the same
 * set of points, with some co-travelers
 *
 * Ran successfully Februay 1, 2019
 *
 * April 3, 2019: modified to read in 7 principal parameters via parameters.csv input file
 */


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import utilities.EuclideanPoint;
import utilities.PoissonVariate;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author RWRD
 *
 */
public class MainClass
{
    /*
     * Three tiers of the FRACTALRABBIT simulator
     */
    AgoraphobicPoints app;
    Retropreferential rpp;
    SporadicReporter spore;
    List<List<Integer>> trajectoryList;
    Map<Integer, Integer> trajectoryAssignment; // traveler to trajectory number
    List<Integer> coTravellers;
    Map<Integer, List<Double>> reportTimesAssignment; // traveler to list of report times
    Map<Integer, List<Integer>> reportPlacesAssignment; // traveler to list of point references (at given times)

    /*
     * Minimal parameters to create an instance of each simulator Some are
     * hard-coded, but seven are in the parameters.csv file
     */
    public MainClass(int n, int d, double h, double theta, double exponent, double maxKmPerHour, double kmper1) {
        this.app = new AgoraphobicPoints(n, d, h, theta);
        EuclideanPoint[] euclidPoints = new EuclideanPoint[app.getPoints().size()];
        Arrays.setAll(euclidPoints, j -> new EuclideanPoint(app.getPoints().get(j)));
        this.rpp = new Retropreferential(euclidPoints, exponent);
        this.spore = new SporadicReporter(euclidPoints, maxKmPerHour, kmper1);
        this.trajectoryList = new ArrayList<List<Integer>>();
        this.coTravellers = new ArrayList<Integer>();
        /*
         * The following three maps all have the SAME key set, i.e. shuffled travelers
         */
        this.trajectoryAssignment = new HashMap<Integer, Integer>(); // assigns traveler to trajectory
        this.reportTimesAssignment = new HashMap<Integer, List<Double>>(); // assigns traveler to times
        this.reportPlacesAssignment = new HashMap<Integer, List<Integer>>(); // assigns traveler to places
    }

    /*
     * Assign travelers to trajectories, with randomization
     */
    public void travelerConfigure(int numCoTravelers, int numTravelers) {
        Integer[] travelerIDs = new Integer[numTravelers];
        Arrays.setAll(travelerIDs, i -> i);
        /*
         * Shuffle the traveler IDs, and assign the first #(numCoTravelers) to first
         * trajectory
         */
        List<Integer> travelerShuffle = Arrays.asList(travelerIDs);
        Collections.shuffle(travelerShuffle);
        // All the co-travelers are assigned to trajectory 0, and their labels are
        // recorded
        for (int t = 0; t < numCoTravelers; t++) {
            this.trajectoryAssignment.put(travelerShuffle.get(t), 0);
            this.coTravellers.add(travelerShuffle.get(t));
        }
        // Other travelers are assigned to trajectories 1 through numTrajectories - 1
        for (int t = numCoTravelers; t < numTravelers; t++) {
            this.trajectoryAssignment.put(travelerShuffle.get(t), t - numCoTravelers + 1);
        }
    }

    /**
     * @param args[0] in Path to input file, such as parameters.csv
     * @param args[1] is Path to an output file
     */
    public static void main(String[] args) throws Exception {

        try {
            Reader inputParameterFile = new FileReader(args[0]);
            String csvOutputFile = args[1];
            Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(inputParameterFile);
            List<String> par = new ArrayList<>(); //parameters as strings
            for (CSVRecord record : records) {
                System.out.println(record.get(0) + " has value " + record.get(1));
                par.add(record.get(1).replaceAll("\\s", "")); // remove whitespace
            }
            /*
             * Small number of parameters which will often change
             */
            int dimension = Integer.parseInt(par.get(0));
            double h = Double.parseDouble(par.get(1)); // fractal dimension for AgoraphobicPoints
            int n = Integer.parseInt(par.get(2));// # points in Agoraphobic points process
            int numTravelers = Integer.parseInt(par.get(3));
            int numCoTravelers = Integer.parseInt(par.get(4));
            double days = Double.parseDouble(par.get(5)); // Sporadic Reporter - duration in days
            double countMean = Double.parseDouble(par.get(6)); // Sporadic Reporter - # reports - mean value

            /*
             * Large number of parameters which seldom change
             * */

            double theta = 0.75; // restart rate for AgoraphobicPoints
            double exponent = -2.0; // convert distances to Retro-preferential matrix
            double speedbound = 50.0; // Sporadic Reporter - units per day
            double kilometersPerUnit = 200.0; // Sporadic Reporter - # km per one distance unit for points
            /*
             * Instantiate generator with these parameters. Here the points are generated.
             */
            MainClass frg = new MainClass(n, dimension, h, theta, exponent, speedbound, kilometersPerUnit);
            System.out.println(n + " points generated for fractal dimension " + h);
            /*
             * Generate the trajectories (points exist already). Each trajectory needs a
             * random phi, random # of steps, random start point,
             */
            double phiMean = 10.0; // Retro-preferential exploration - mean value
            PoissonVariate phiRandom = new PoissonVariate(phiMean);
            double stepsMean = 100.0; // Retro-preferential trajectory length - mean value
            PoissonVariate stepsRandom = new PoissonVariate(stepsMean);
            Random g = new Random();
            /*
             * Set up co-travel structure
             */
            frg.travelerConfigure(numCoTravelers, numTravelers);
            int numTrajectories = numTravelers - numCoTravelers + 1;// all co-travelers use same trajectory
            // Loop through trajectories
            for (int t = 0; t < numTrajectories; t++) {
                // new trajectory with Poisson phi, Poisson # of steps, uniform random start
                // point
                frg.trajectoryList
                        .add(frg.rpp.trajectory(phiRandom.generate(), stepsRandom.generate(), g.nextInt(n)));
            }

            /*
             * Sporadic reporting parameters
             */
            double delta = 0.001; // Sporadic Reporter - Pareto cutoff
            double alpha = -1.5; // Sporadic Reporter - Pareto tail
            PoissonVariate countRandom = new PoissonVariate(countMean);

            System.out.println(
                    numTrajectories + " trajectories generated, in which travelers assigned to same trajectory are: ");
            for (Integer p : frg.coTravellers) {
                System.out.print(p + ", ");
            }
            System.out.println();
            /*
             * Loop through travelers, attaching trajectory to each. p < # travelers,
             * defined above as (numTrajectories - 1 + numCoTravelers) Co-travelers will
             * receive same trajectory.
             *
             * ERROR HERE!
             */
            int currentTrajectory;
            for (Integer p : frg.trajectoryAssignment.keySet()) {
                currentTrajectory = frg.trajectoryAssignment.get(p); // trajectory # for traveler p
                frg.spore.embedTrajectory(frg.trajectoryList.get(currentTrajectory)); // embed in continuous time
                frg.spore.generateReports(frg.trajectoryList.get(currentTrajectory), countRandom.generate(), delta,
                        alpha, days); // generate sporadic reports for this trajectory
                frg.reportTimesAssignment.put(p, List.copyOf(frg.spore.getReportTimes())); // tag p with Report Times
                frg.reportPlacesAssignment.put(p, List.copyOf(frg.spore.getReportPlaces())); // tag p with Report Places
            }
            /*
             * Check that the trajectories differ , by inspecting first element of each -
             * FAILED!
             */
            for (Integer p : frg.reportTimesAssignment.keySet()) {
                System.out.println(
                        "First report for traveler " + p + " at time " + frg.reportTimesAssignment.get(p).get(0)
                                + " is place " + frg.reportPlacesAssignment.get(p).get(0));
            }
            System.out.println("Reports have been generated for " + numTravelers + " travelers.");
            /*
             * Need only format the data as (traveler ID, time, x in km, y in km), and write
             * to csv using Apache Commons CSV
             */
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvOutputFile + ".csv"));
                 CSVPrinter csvPrinter = new CSVPrinter(writer,
                         CSVFormat.DEFAULT.withHeader("ID", "Days", "x(km)", "y(km)"))) {
                double x, y, time;
                int place;
                int lines = 0;
                // Write to file ERROR - repetitions of one report set!

                for (Integer p : frg.reportTimesAssignment.keySet()) {
                    for (int t = 0; t < frg.reportTimesAssignment.get(p).size(); t++) {
                        time = frg.reportTimesAssignment.get(p).get(t);
                        place = frg.reportPlacesAssignment.get(p).get(t);
                        if (t == 0) {
                            System.out.println("First report for traveler " + p + " is point "
                                    + frg.reportPlacesAssignment.get(p).get(t) + " at time "
                                    + frg.reportTimesAssignment.get(p).get(t));
                        }
                        x = kilometersPerUnit * frg.app.getPoints().get(place)[0];
                        y = kilometersPerUnit * frg.app.getPoints().get(place)[1];
                        csvPrinter.printRecord(p, time, x, y);
                        lines++;
                    }
                }

                System.out.println("CSV file created with " + lines + " lines, called " + csvOutputFile + ".csv");
                csvPrinter.flush();
                writer.flush();
                writer.close();
            }

            /*
             * SECOND VERSION! (traveler ID, time, place ID), and write to csv using Apache
             * Commons CSV
             */
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvOutputFile + "PLACES.csv"));
                 CSVPrinter csvPrinter = new CSVPrinter(writer,
                         CSVFormat.DEFAULT.withHeader("Traveler ID", "Days", "Place ID"))) {
                int lines = 0;
                // Write to file ERROR - repetitions of one report set!

                for (Integer p : frg.reportTimesAssignment.keySet()) {
                    for (int t = 0; t < frg.reportTimesAssignment.get(p).size(); t++) {
                        csvPrinter.printRecord(p, frg.reportTimesAssignment.get(p).get(t),
                                frg.reportPlacesAssignment.get(p).get(t));
                        lines++;
                    }
                }
                System.out
                        .println("CSV Places file created with " + lines + " lines, called " + csvOutputFile + "PLACES.csv");
                csvPrinter.flush();
                writer.flush();
                writer.close();
            }

        } catch (IOException ex) {
            System.out.println(ex.toString());
            System.out.println("Could not find parameter file.");
        }

    }

}


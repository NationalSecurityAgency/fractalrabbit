package simulatorTests;
/*
 * Write CSV file of Agoraphobic Point Process in any dimension
 * June 12, 2019.
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import simulators.AgoraphobicPoints;

/**
 * Used only for development
 * 
 * @see simulators.AgoraphobicPoints
 *
 */
public class AgoraphobicPointsWriter {

	final int dimension;

	public AgoraphobicPointsWriter(int dim) {
		this.dimension = dim;
	}

	public static void main(String[] args) throws IOException {
		try {
			int n = 100;
			int dim = 2;
			double h = 1.33;
			double theta = 0.75;
			String csvOutputFile = "fractaldim-" + h + "-ambient-" + dim + "-size-" + n + ".csv";
			AgoraphobicPointsWriter apt = new AgoraphobicPointsWriter(dim);
			AgoraphobicPoints app = new AgoraphobicPoints(n, apt.dimension, h, theta);
			app.generatePoints();
			System.out.println("Sampling efficiency = " + app.getSamplingEfficiency() + " after " + n + " points.");
			/*
			 * Need only format the data as (traveler ID, time, x in km, y in km), and write
			 * to csv using Apache Commons CSV
			 */
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvOutputFile));
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(','));

			int lines = 0;
			// Write to file ERROR - repetitions of one report set!

			for (double[] point : app.getPoints()) {
				csvPrinter.printRecords(point);
				lines++;

			}

			System.out.println("CSV file created with " + lines + " lines, called " + csvOutputFile + ".csv");
			csvPrinter.flush();
			writer.flush();
			csvPrinter.close();
			writer.close();

		} catch (IOException ex) {
			System.out.println(ex.toString());
			System.out.println("Could not find parameter file.");
		}

	}
}

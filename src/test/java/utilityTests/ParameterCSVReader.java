
package utilityTests;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Used only for development
 * @author rwrd
 *
 */
public class ParameterCSVReader {

	public ParameterCSVReader() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Reader in = new FileReader(args[0]);
			Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
			List<String> p = new ArrayList<>();
			for (CSVRecord record : records) {
			    System.out.println(record.get(0) + " has value " + record.get(1));
			    p.add(record.get(1).replaceAll("\\s","")); // remove whitespace
			}
			int d = Integer.parseInt(p.get(0));
			double h = Double.parseDouble(p.get(1));
			int n = Integer.parseInt(p.get(2));
			int numTravelers = Integer.parseInt(p.get(3));
			int numCoTravelers = Integer.parseInt(p.get(4));
			double days = Double.parseDouble(p.get(5));
			double countMean = Double.parseDouble(p.get(6));
			
		} catch (IOException ex) {
			System.out.println(ex.toString());
			System.out.println("Could not find file.");
		}

	}

}

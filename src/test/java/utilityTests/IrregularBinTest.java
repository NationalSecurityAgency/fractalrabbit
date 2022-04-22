package utilityTests;

/**
 * Ran successfully 11.14.2018
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import utilities.IrregularBinAssignment;

/**
 * Used only for development
 * @author rwdarli
 * @see utilities.IrregularBinAssignment
 */
public class IrregularBinTest {

	private IrregularBinAssignment<Double> iba;

	public IrregularBinTest() {
		iba = new IrregularBinAssignment<>();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IrregularBinTest test = new IrregularBinTest();
		Random g = new Random();
		List<Double> obs = g.doubles(100).map(x -> -Math.log(x)).sorted().boxed().collect(Collectors.toList());
		double maxObs = obs.get(obs.size() - 1);
		List<Double> bins = new ArrayList<Double>();
		bins.add(0.0); // first element
		int bincount = 20; // add one for endpoints
		bins.addAll(g.doubles(bincount, 0.0, Math.ceil(maxObs)).sorted().boxed().collect(Collectors.toList()));
		test.iba.assign(bins, obs);
		System.out.println("bin boundaries:");
		for (Double a : bins) {
			System.out.print(a + ", ");
		}
		System.out.println();
		int testbin = g.nextInt(bincount);

		int counter = 0;
		for (Double b : obs) {
			if ((b >= bins.get(testbin)) && (b < bins.get(testbin + 1))) {
				counter++;
			}
		}
		System.out.println("observation count for bin " + testbin + " = " + counter);
		System.out.println();
		System.out.println("placements of observations in bins:");
		for (Integer p : test.iba.getPlacements()) {
			System.out.print(p + ", ");
		}
	}

}

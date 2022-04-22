package utilityTests;

import java.util.Arrays;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import utilities.HeavyTailedPartialSums;
import utilities.ScatterPlotInputData;

/**
 * Used only for development
 * @author rwdarli
 * @see utilities.HeavyTailedPartialSums
 */
public class HeavyTailedPartialSumsTest {
	private HeavyTailedPartialSums ps;

	public HeavyTailedPartialSumsTest() {
		ps = new HeavyTailedPartialSums();
	}

	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		final int count = 1000; 
		// Take delta = 0.001 (about 1/30 divided by 36 hours), alpha = -1.5
		final double delta = 0.001; // two minutes / 1.5 days
		final double alpha = -1.5;
		final double days = 90.0;
		
		HeavyTailedPartialSumsTest test = new HeavyTailedPartialSumsTest();
		
		Double[] xx = test.ps.generate(count, delta, alpha).toArray(new Double[count]);
		System.out.println("first entry: " + xx[0]);
		System.out.println("last entry: " + xx[count - 1]);
				
		// Create Scatter Plot
		double[] t = new double[xx.length];
		Arrays.setAll(t, j -> 1.0 + j); //1,2,3, ..., n
		double[] x = new double[count];
		Arrays.parallelSetAll(x, j->days*xx[j].doubleValue()); //unboxing
		
		ScatterPlotInputData scatter = new ScatterPlotInputData("Point Process "+(-alpha), x, t);
		XYChart chartAPP = scatter.getChart();
		new SwingWrapper<XYChart>(chartAPP).displayChart();

	}

}

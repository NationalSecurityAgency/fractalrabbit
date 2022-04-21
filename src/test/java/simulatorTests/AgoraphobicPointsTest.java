package simulatorTests;
/**
 * Tests AgoraphobicPoints and displays 2-d graphic of Agoraphobic Point Process
 * Passed April 4, 2018.
 */

import java.util.Arrays;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import simulators.AgoraphobicPoints;
import utilities.ScatterPlotInputData;

/**
 * @author rwdarli
 *
 */
public class AgoraphobicPointsTest {

	final int dimension;

	public AgoraphobicPointsTest(int dim) {
		this.dimension = dim;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int n = 100;
		int dim = 2;
		double h = 1.33;
		double theta = 0.75;
		AgoraphobicPointsTest apt = new AgoraphobicPointsTest(dim);
		AgoraphobicPoints app = new AgoraphobicPoints(n, apt.dimension, h, theta);
		app.generatePoints();
		double[] x = new double[n];
		Arrays.setAll(x, j ->app.getPoints().get(j)[0]);
		double[] y = new double[n];
		Arrays.setAll(y, j ->app.getPoints().get(j)[1]);

		// Create Scatter Plot
		ScatterPlotInputData scatter = new ScatterPlotInputData("Agoraphobic "+h, x, y);
		XYChart chartAPP = scatter.getChart();
		new SwingWrapper<XYChart>(chartAPP).displayChart();
		
		System.out.println("Sampling efficiency = " + app.getSamplingEfficiency() + " after " + n + " points.");
		
	}

}

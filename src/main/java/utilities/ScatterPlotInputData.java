package utilities;

import java.util.Arrays;
import java.util.Random;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.demo.charts.ExampleChart;
import org.knowm.xchart.style.Styler.LegendPosition;

/**
 * Visualization of fractal point clouds, tested on random data.
 * @see org.knowm.xchart.XYChart
 * @see simulators.AgoraphobicPoints
 * @author rwdarli
 *
 */
public class ScatterPlotInputData implements ExampleChart<XYChart> {
	private double[] x, y;
	private String legend;

	public ScatterPlotInputData(String title, double[] xData, double[] yData) {
		this.x = xData;
		this.y = yData;
		this.legend = title;
	}

	@Override
	public XYChart getChart() {

		// Create Chart
		XYChart chart = new XYChartBuilder().width(800).height(600).build();

		// Customize Chart
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
		chart.getStyler().setChartTitleVisible(true);
		chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
		chart.getStyler().setMarkerSize(4);
		chart.addSeries(this.legend, this.x, this.y);
		return chart;
	}

	public String getExampleChartName() {
		return "Scatter Plot";
	}
/**
 * 
 * @param args args[0] is number of random points to generate in a scatter plot.
 */
	public static void main(String[] args) {
		Random h = new Random();
		double[] x = new double[Integer.parseInt(args[0])];
		Arrays.setAll(x, (i) -> h.nextDouble());
		double[] y = new double[Integer.parseInt(args[0])];
		Arrays.setAll(y, (i) -> h.nextDouble());
		ScatterPlotInputData scatter = new ScatterPlotInputData("test", x, y);
		// ExampleChart<XYChart> exampleChart = new ScatterChart01();
		XYChart chart = scatter.getChart();
		new SwingWrapper<XYChart>(chart).displayChart();
	}

}

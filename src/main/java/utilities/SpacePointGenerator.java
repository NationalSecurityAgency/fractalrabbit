
package utilities;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.ToDoubleBiFunction;

/**
 * Utility class for vectors in Euclidean space. Generates a random point in the
 * unit disk, and tests equality of two vectors to machine precision.
 * 
 * @author rwdarli
 */
public class SpacePointGenerator {
	private int d;
	private final double[] zerovec;
	private Random g;

	/**
	 * @param dimension of ambient space
	 */
	public SpacePointGenerator(int dimension) {
		this.d = dimension;
		this.zerovec = new double[this.d]; // all zeros
		this.g = new Random();
	}

	public double[] zeroVector() {
		return this.zerovec;
	}

	/**
	 * Generates uniformly at random a d-dimensional vector in
	 * 
	 * <pre>
	 * (-1, 1)^d
	 * </pre>
	 * 
	 * . Repeat until one is found which lies inside unit sphere.
	 * 
	 * @return random point in unit sphere in dimension d.
	 */
	public double[] samplePointUnitDisk() {
		boolean tooBig = true;
		double[] y = new double[this.d];
		while (tooBig) {
			Arrays.setAll(y, (i) -> -1.0 + 2.0 * g.nextDouble());
			tooBig = (this.sumSquares(y) > 1.0);
		}
		return y;
	}

	/**
	 * Given a center point, sample uniformly a point within given radius of center
	 * 
	 * @param radius radial distance
	 * @param center center point in dimension d
	 * @return random point within given radius of center in dimension d.
	 */
	public double[] sampleNearbyPoint(double radius, double[] center) {
		double[] y = new double[this.d];
		double[] z = this.samplePointUnitDisk();
		Arrays.setAll(y, (i) -> center[i] + radius * z[i]);
		return y;
	}

	/**
	 * 
	 * @param vector point in dimension d
	 * @return norm squared
	 */
	public double sumSquares(double[] vector) {
		double sum = 0.0;
		for (double y : vector) {
			sum += y * y;
		}
		return sum;
	}

	/**
	 * 
	 * @param vector1 point in dimension d
	 * @param vector2 point in dimension d
	 * @return distance
	 */
	public static double distance(double[] vector1, double[] vector2) {
		if (!(vector1.length == vector2.length)) {
			return Double.NaN;
		} else {
			double sumdiff2 = 0.0;
			double z;
			for (int i = 0; i < vector1.length; i++) {
				z = vector1[i] - vector2[i];
				sumdiff2 += z * z;
			}
			return (Math.sqrt(sumdiff2));
		}
	}

	/*
	 * Lambda version of the latter
	 */
	public ToDoubleBiFunction<double[], double[]> distance = ((v1, v2) -> this.distance(v1, v2));

	/**
	 * 
	 * @param vector1 point in dimension d
	 * @param vector2 point in dimension d
	 * @return Are they equal?
	 */
	public static boolean equalPoints(double[] vector1, double[] vector2) {
		/*
		 * False if dimensions differ, or if the points are a positive distance apart
		 */
		if (!(vector1.length == vector2.length)) {
			return false;
		} else if (distance(vector1, vector2) > Math.ulp(1.0)) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Lambda version of
	 * 
	 * @see equalPoints
	 */
	public BiPredicate<double[], double[]> equalPoints = ((v1, v2) -> this.equalPoints(v1, v2));
}

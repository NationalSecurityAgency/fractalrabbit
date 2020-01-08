/**
 * 
 */
package utilities;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.ToDoubleBiFunction;

/**
 * Utility class for vectors in Euclidean space. It is able to generate a random
 * point in the unit disk, and test equality of two vectors to machine precision
 * 
 * @param d
 *            - dimension
 * @author RWRD Tested 3.28.2018. 
 * 8.8.18 with functional interfaces added
 */
public class SpacePointGenerator{
	int d;
	final double[] zerovec;
	Random g;

	/**
	 * Constructor given a dimension
	 */
	public SpacePointGenerator(int dimension) {
		this.d = dimension;
		this.zerovec = new double[this.d]; // all zeros
		this.g = new Random();
	}

	public double[] zeroVector() {
		return this.zerovec;
	}

	/*
	 * Sample all coordinates in (-1. 1) and reject if distance from 0 is more than
	 * 1
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

	/*
	 * Given a center point, sample uniformly a point with given radius of center
	 */
	public double[] sampleNearbyPoint(double radius, double[] center) {
		double[] y = new double[this.d];
		double[] z = this.samplePointUnitDisk();
		Arrays.setAll(y, (i) -> center[i] + radius * z[i]);
		return y;
	}

	public double sumSquares(double[] vector) {
		double sum = 0.0;
		for (double y : vector) {
			sum += y * y;
		}
		return sum;
	}

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
	public ToDoubleBiFunction<double[],double[]> distance = ((v1, v2)-> distance(v1, v2));
	

	public static boolean equalPoints(double[] vector1, double[] vector2) {
		/*
		 * False if dimensions differ, or if the points are a positive distance apart
		 */
		if (!(vector1.length == vector2.length)) {
			return false;
		} else return !(distance(vector1, vector2) > Math.ulp(1.0));
	}
	/*
	 * Lambda version of the latter
	 */
	public BiPredicate<double[],double[]> equalPoints =((v1, v2)-> equalPoints(v1, v2));
}

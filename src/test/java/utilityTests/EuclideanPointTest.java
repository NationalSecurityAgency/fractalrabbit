package utilityTests;
/**
 * 
 */


import java.util.Arrays;
import java.util.Random;

import utilities.EuclideanPoint;

/**
 * @author rwdarli
 * Worked 8.9.18
 */
public class EuclideanPointTest {

	public EuclideanPointTest() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Random g = new Random();
		int d = 3;
		/*
		 * Two random points in [-1,1)^d
		 */
		double[] x = g.doubles(d, -1.0, 1.0).toArray();
		EuclideanPoint px = new EuclideanPoint(x);
		double[] y = g.doubles(d, -1.0, 1.0).toArray();
		EuclideanPoint py = new EuclideanPoint(y);
		System.out.println("Distance from x to y: " +px.distanceTo(py) );
		System.out.println("Are x and y equal? " + px.equals(py));
		/*
		 * Corrupt vector x by sqrt then square
		 */
		double[] xx = new double[d];
		Arrays.setAll(xx, j->Math.pow(Math.sqrt(x[j]), 2.0));
		EuclideanPoint pxx = new EuclideanPoint(xx);
		System.out.println("Are x and xx equal? " + px.equals(pxx));
	}

}

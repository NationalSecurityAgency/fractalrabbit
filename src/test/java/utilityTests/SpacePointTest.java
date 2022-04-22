package utilityTests;

import utilities.SpacePointGenerator;

/**
 * Used only for development
 * @see utilities.SpacePointGenerator
 * @author rwdarli
 */
public class SpacePointTest {


	SpacePointGenerator x;
/**
 * 
 * @param d dimension of ambient Euclidean space
 */
	public SpacePointTest(int d) {
		this.x = new SpacePointGenerator(d);
	}

	/**
	 * @param args null
	 */
	public static void main(String[] args) {
		int d = 3;
		SpacePointTest spt = new SpacePointTest(d);
		System.out.println("Random point v1 in disc: ");
		double[] v1 = spt.x.samplePointUnitDisk();
		for (double z : v1) {
			System.out.print("," + z);
		}
		System.out.println();
		double[] v2 = spt.x.samplePointUnitDisk();
		System.out.println("Distance between two random points in disc: " + SpacePointGenerator.distance(v1, v2));
		System.out.println("Test ToDoubleBifunction version: " + spt.x.distance.applyAsDouble(v1, v2));
		System.out.println("Are v1 and v2 equal? " + SpacePointGenerator.equalPoints(v1, v2));
		System.out.println("Test BiPredicate version: " + spt.x.equalPoints.test(v1, v2));
		System.out.println("Are v1 and v1 equal? " + SpacePointGenerator.equalPoints(v1, v1));
		System.out.println("Test BiPredicate version: " + spt.x.equalPoints.test(v1, v1));

		double radius = 0.07;
		double[] v3 = spt.x.sampleNearbyPoint(radius, v1);
		System.out.println("Random point v3 within radius " + radius + " of v1: ");
		for (double z : v3) {
			System.out.print("," + z);
		}
		System.out.println();
		System.out.println("Distance between v1 and v3: " + SpacePointGenerator.distance(v1, v3));

	}

}

/**
 * Point in a Euclidean metric space. 
 * Passed tests 8.9.18
 */
package utilities;

/**
 * @author RWRD
 *
 */
public class EuclideanPoint implements Metrizable {

	private final double[] point;
	private final int dimension;

	public EuclideanPoint(double[] p) {
		this.point = p;
		this.dimension = p.length;
	}

	/* (non-Javadoc)
	 * @see utilities.Metrizable#distanceTo(utilities.Metrizable)
	 */
	@Override
	public double distanceTo(Metrizable o) {
		if(o.getClass()==this.getClass()) {
			EuclideanPoint q = (EuclideanPoint)o;
			double sumdiff2 = 0.0;
			double z;
			for (int i = 0; i < this.dimension; i++) {
				z = this.point[i] - q.point[i];
				sumdiff2 += z * z;
			}
			return (Math.sqrt(sumdiff2));		
		} else {
			return Double.NaN;
		}

	}
	@Override
	/*
	 * (non-Javadoc)
	 * @see utilities.Metrizable#equals(utilities.Metrizable)
	 * equality means distance is zero up to machine precision
	 */
	public boolean equals(Metrizable o) {
        return this.distanceTo(o) < Math.ulp(1.0);
	}

}

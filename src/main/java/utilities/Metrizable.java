/**
 * @deprecated
 * August 8, 2018. Seem to be unable to implement this class for syntactical reasons
 */
package utilities;

/**
 * @author rwdarli
 * Modeled on Comparable interface
 * Examples will include 
 * - SpacePointGenerator, where distance is Euclidean, and 
 * - graph vertices, where distance is path length
 */
public interface Metrizable {
	public double distanceTo(Metrizable o);
	public boolean equals(Metrizable o);
	

}

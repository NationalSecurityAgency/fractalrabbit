/**
 * Ran successfully 11.14.2018
 * 
 * Suppose a[0] < a[1] < ... < a[n] are stored in a sorted list a ("bins")
 * Suppose b[0]  <= b[1] =< ... <= b[m-1] are such that a[0] <=b[0] and b[m-1] < a[n]
 * Refer to the interval [a[i-1], a[i]) as bin i-1.
 * Consider the b[j] as "observations" to be placed in "bins".
 * Construct an int array c of length m so that c[j] = k means that b[j] falls in bin k.
 */
package utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RWRD
 *
 */
public class IrregularBinAssignment {

	private final List<Integer> placements;

	public IrregularBinAssignment() {
		this.placements = new ArrayList<>();
	}

	public void assign(List<? extends Comparable> bins, List<? extends Comparable> obs) {
		this.placements.clear();

		int k = 1; // bin counter
		Comparable a = bins.get(k); // top of first bin
		for (Comparable b : obs) {
			while (!(b.compareTo(a) < 0)) {
				k++; // possibly some bins may have no observations
				a = bins.get(k);
			}
			this.placements.add(k - 1);
		}

	}

	/**
	 * @return the placements
	 */
	public List<Integer> getPlacements() {
		return placements;
	}
	

}

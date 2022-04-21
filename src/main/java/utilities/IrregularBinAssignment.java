package utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * 
 * Suppose a[0] &lt a[1] &lt ... &lt a[n] are stored in a sorted list a ("bins")
 * Suppose b[0] &le; b[1] &le; ... &le; b[m-1] are such that a[0] &le; b[0] and
 * b[m-1] &lt a[n] Refer to the interval [a[i-1], a[i]) as bin i-1. Consider the
 * b[j] as "observations" to be placed in "bins". Construct an int array c of
 * length m so that c[j] = k means that b[j] falls in bin k.
 * 
 * @author rwdarli
 *
 */
public class IrregularBinAssignment<T extends Comparable> {

	private List<Integer> placements;

	/**
	 * Constructor
	 */
	public IrregularBinAssignment() {
		this.placements = new ArrayList<Integer>();
	}

	/**
	 * Assigns each observation to the correct bin.
	 * @param bins list of bins, in ascending order with respect to T.compare()
	 * @param obs  list of observations to be placed in bins
	 */
	public void assign(List<T> bins, List<T> obs) {
		this.placements.clear();

		int k = 1; // bin counter
		T topBoundary = bins.get(k); // top of first bin
		for (T y : obs) {
			while (!(y.compareTo(topBoundary) < 0)) {
				k++; // possibly some bins may have no observations
				topBoundary = bins.get(k);
			}
			this.placements.add(k - 1);
		}

	}

	/**
	 * @return the list of bin counts, invoking assign()
	 */
	public List<Integer> getPlacements() {
		return placements;
	}

}

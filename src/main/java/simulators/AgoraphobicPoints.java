/* THIS PUBLIC DOMAIN SOFTWARE WAS PRODUCED BY AN EMPLOYEE OF U.S. GOVERNMENT 
 * AS PART OF THEIR OFFICIAL DUTIES.
 */
package simulators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.IntToDoubleFunction;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import utilities.SpacePointGenerator;

/**
 * Simulates the Agoraphobic Point Process using the Hard Threshold method. The
 * points are nodes in a rooted tree, where the zero vector is the root.
 * 
 * @author rwdarli
 * @see utilities.SpacePointGenerator
 */
public class AgoraphobicPoints {
	private int nPoints, dimEuclid;
	private double dimHausdorff, innovation;
	private SpacePointGenerator spg;
	private List<double[]> points;
	private MutableValueGraph<double[], Double> rootedTree;
	private final double[] radialBounds;
	private Random g;
	private int totalSamples;

	/**
	 * Class constructor which accepts model parameters and generates points
	 * 
	 * @param n     the number of points to be created
	 * @param d     dimension of the Euclidean space in which points are embedded
	 * @param h     fractal dimension of the limit set (less than d)
	 * @param theta non-negative double controlling rate at which additional clumps
	 *              form
	 */
	public AgoraphobicPoints(int n, int d, double h, double theta) {
		this.nPoints = n;
		this.dimEuclid = d;
		this.dimHausdorff = h;
		this.innovation = theta;
		this.spg = new SpacePointGenerator(this.dimEuclid);
		this.radialBounds = new double[this.nPoints];
		Arrays.setAll(this.radialBounds, i -> (i > 0 ? Math.pow(i, -1.0 / this.dimHausdorff) : 1.0));
		this.g = new Random();
		/*
		 * Nodes of the rooted tree are stored in a List for random retrieval purposes.
		 */
		this.rootedTree = ValueGraphBuilder.directed().build();
		this.points = new ArrayList<double[]>();
		/*
		 * Root vertex at zero
		 */
		this.points.add(this.spg.zeroVector());
		this.rootedTree.addNode(this.points.get(0));
	}
/**
 * Generates a cloud of n points in dimension d, with approximate fractal dimension h.
 */
	public void generatePoints() {
		/*
		 * Set the probability of starting a new clump, as in Chinese restaurant
		 * process. Decays to zero.
		 */
		IntToDoubleFunction newClumpRate = (i -> this.innovation / (this.innovation + i));
		boolean acceptPoint; // mutable
		int parent; // mutable
		int neighborCount; // mutable
		this.totalSamples = 1;
		for (int i = 1; i < this.nPoints; i++) {
			/*
			 * Bernoulli trial decides whether to form a new clump. In that case, the root
			 * is the parent of the new point.
			 */
			if (g.nextDouble() < newClumpRate.applyAsDouble(i)) {
				this.points.add(this.spg.samplePointUnitDisk());
				this.totalSamples++;
				/*
				 * Adding this edge also adds the new node to the tree.
				 */
				this.rootedTree.putEdgeValue(this.points.get(0), this.points.get(i),
						Math.sqrt(spg.sumSquares(this.points.get(i))));
			}
			/*
			 * In the case of failure, we select the parent of the new point, and generate a
			 * candidate close by. Rejection sampling determines whether to accept the
			 * candidate;
			 */
			else {
				acceptPoint = false;
				while (!acceptPoint) {
					parent = g.nextInt(i);
					double[] candidate = this.spg.sampleNearbyPoint(this.radialBounds[i], this.points.get(parent));
					this.totalSamples++;
					/*
					 * Count close neighbors in order to determine rejection probability
					 */
					neighborCount = 0;
					for (double[] x : this.points) {
						if (SpacePointGenerator.distance(x, candidate) < this.radialBounds[i]) {
							neighborCount++;
						}
					}
					/*
					 * neighborCount is at least 1, because the parent is close. Acceptance rate is
					 * reciprocal of neighborCount.
					 */
					acceptPoint = g.nextDouble() < 1.0 / neighborCount;
					if (acceptPoint) {
						this.points.add(candidate);
						/*
						 * This candidate has just become the point with index i.
						 */
						this.rootedTree.putEdgeValue(this.points.get(parent), this.points.get(i),
								SpacePointGenerator.distance(this.points.get(parent), this.points.get(i)));
					}
				}

			}
		}
	}

	/**
	 * @return - list of Agoraphobic points in Euclidean space, starting with zero
	 *         vector
	 */
	public List<double[]> getPoints() {
		return points;
	}

	/**
	 * @return - the rootedTree, whose nodes are the Agoraphobic points, and whose
	 *         edges encode the parent-child relationships of the Agoraphobic points
	 */
	public MutableValueGraph<double[], Double> getRootedTree() {
		return rootedTree;
	}

	/**
	 * @return - what proportion of the samples taken produced a new point?
	 */
	public double getSamplingEfficiency() {
		return (double) this.nPoints / (double) this.totalSamples;
	}

}

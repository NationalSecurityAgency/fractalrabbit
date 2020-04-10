/**
 * Passed tests 8.9.2018
 */
package simulators;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import utilities.SpacePointGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.IntToDoubleFunction;

/**
 * Simulates the Agoraphobic Point Process using the Hard Threshold method, as
 * described in:
 * <p>
 * <i> Retropreferential stochastic mobility models on random fractals under
 * sporadic observations </i> (2018)
 * <p>
 * The points are nodes in a rooted tree, where the zero vector is the root.
 *
 * @param nPoints
 *            - the number of points to be created
 * @param dimEuclid
 *            - dimension of the Euclidean space in which points are embedded
 * @param dimHausdorff
 *            - fractal dimension of the limit set
 * @param innovation
 *            - non-negative double controlling rate at which additional clumps
 *            form
 *
 * @author RWRD
 *
 */
public class AgoraphobicPoints {
    private final int nPoints;
    private final double dimHausdorff;
    private final double innovation;
    private final List<double[]> points;
    private final MutableValueGraph<double[], Double> rootedTree;
    private int totalSamples;

    /**
     * Class constructor which accepts model parameters and generates points
     */
    public AgoraphobicPoints(int n, int d, double h, double theta) {
        this.nPoints = n;
        this.dimHausdorff = h;
        this.innovation = theta;
        SpacePointGenerator p = new SpacePointGenerator(d);
        double[] radialBounds = new double[this.nPoints];
        Arrays.setAll(radialBounds, i -> (i > 0 ? Math.pow(i, -1.0 / this.dimHausdorff) : 1.0));
        Random g = new Random();
        /*
         * Nodes of the rooted tree are stored in a List for random retrieval purposes.
         */
        this.rootedTree = ValueGraphBuilder.directed().build();
        this.points = new ArrayList<>();
        /*
         * Root vertex at zero
         */
        this.points.add(p.zeroVector());
        this.rootedTree.addNode(this.points.get(0));
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
                this.points.add(p.samplePointUnitDisk());
                this.totalSamples++;
                /*
                 * Adding this edge also adds the new node to the tree.
                 */
                this.rootedTree.putEdgeValue(this.points.get(0), this.points.get(i),
                        Math.sqrt(p.sumSquares(this.points.get(i))));
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
                    double[] candidate = p.sampleNearbyPoint(radialBounds[i], this.points.get(parent));
                    this.totalSamples++;
                    /*
                     * Count close neighbors in order to determine rejection probability
                     */
                    neighborCount = 0;
                    for (double[] x : this.points) {
                        if (SpacePointGenerator.distance(x, candidate) < radialBounds[i]) {
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
        return (double)this.nPoints/(double)this.totalSamples;
    }

}

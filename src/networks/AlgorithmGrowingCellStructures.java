package networks;

import java.util.*;
import structures.*;
import support.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: Growing Cell Structures</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 * @version 1.1 23 November 2007 Added <Generics> to silence compiler 
 *          > 1.5 bitching about unsafes and casts
 */

public class AlgorithmGrowingCellStructures extends Algorithm {
    final private int dimension = 3;

    private Vertex[] data = null;
    // Growing Cell Structure specific parameters
    private int LAMBDA; // Node insertion rate
    final private int LAMBDA_PRUNE; // Cell deletion rate
    final private double EPSILONB; // Winner takes most
    final private double EPSILONN; // Neighbours take a bit less
    final private double ALPHA = 0.02; // Signal Counter Cooling rate
    static private int nCellsMax;
    static private int dataDim;
    static private double norm;
    final private boolean error_minimising = false;
    // Growing Neural Gas global number of data Inputs
    private int label = 0;
    // Global Iteration Counter
    private Counter adaptation_step;
    private int epoch;
    private Dealer dealer;

    GCSGraph graph = new GCSGraph();
    public Graph getGraph() {
        return graph;
    }

    public Vertex [] getInputs() {
      return data;
    }

    /**
     * @param _data_ The data to be classified
     * @param _maxNodes_ The maximum size of the graph
     * @param _dataDim_ The dimension of the data
     * @param _power_ The Minkowski norm to be used for training
     * @param _epoch_ The number of epochs to run for
     * @param _cellDeletion_ Iteration to perform cell pruning
     * @param _nodeInsertion_ Node insertion rate
     * @param _epsilonb_ The BMU learning rate
     * @param _epsilonn_ The neighbourhood learning rate
     * @param _iter_ A reference to the iteration counter object
     */
    public AlgorithmGrowingCellStructures(Vertex[] _data_,
                                          int _dataDim_,
                                          int _epoch_,
                                          Counter _iter_,
                                          double _power_,
                                          int _maxNodes_,
                                          double _epsilonb_,
                                          double _epsilonn_,
                                          int _cellDeletion_,
                                          int _nodeInsertion_) {
            super();

            // pass the command line parameters
            data = _data_;
            nCellsMax = _maxNodes_;
            dataDim = _dataDim_;
            norm = _power_;
            epoch = _epoch_;
            LAMBDA = _nodeInsertion_;
            LAMBDA_PRUNE = _cellDeletion_;
            EPSILONB = _epsilonb_;
            EPSILONN = _epsilonn_;
            adaptation_step = _iter_;
            init();
    }

    /** Provide complex constructor initialisation */
    private void init() {
        // populate graph with a single random simplex
        GCSVertex[] cell = new GCSVertex[dimension];
        for (int i = 0; i < dimension; i++) {
            double[] location = new double[dataDim];
            for (int j = 0; j < dataDim; j++) {
                if (application.Launcher.DEBUG) {
                    location[j] = 0.5d;
                } else {
                    location[j] = Math.random();
                }
            }
            cell[i] = new GCSVertex(location, String.valueOf(label++));
        }
        graph.add(new GCSSimplex(cell[0], cell[1], cell[2]));
        // Create epoch dealer
        dealer = new Dealer(data, epoch);
    }

    public void run() {
        Vector<Vertex> shared = new Vector<Vertex>();
        Vector<Vertex> fNeigh = new Vector<Vertex>();
        Vector<Vertex> qNeigh = new Vector<Vertex>();
        while (getState() != STOP) {
            // force a break when the dealer is finished
            if (!dealer.hasNext()) {
                this.setState(STOP);
                break;
            }
            if (getState() != PAUSE) {
                // 1.  Choose and input signal xi according to the proability
                //     distribution P(xi)
                double[] input;
                if (application.Launcher.DEBUG) {
                    input = ((Vertex)dealer.getNextFixed()).getPosition();
                } else {
                    input = ((Vertex)dealer.getNext()).getPosition();
                }
                // 2.  Locate the bets matching unit s = phi_w(xi)
                double S1Value = Double.MAX_VALUE;
                GCSVertex S1 = null;
                for (Iterator i = graph.getAllVertices(); i.hasNext(); ) {
                    final GCSVertex candidate = (GCSVertex)i.next();
                    final double error = Vertex.Minkowski(norm, input, candidate.getPosition());
                    if (error < S1Value) {
                        S1Value = error;
                        S1 = candidate;
                    }
                }
                // 3.  Increse the matching for s and its direct
                //     topological neighbours
                // 3.1  S
                double[] S1pos = S1.getPosition();
                for (int d = 0; d < dataDim; d++) {
                    S1pos[d] = S1pos[d] + (EPSILONB * (input[d] - S1pos[d]));
                }
                // 3.2  topological neighbours
                for (Iterator j = graph.getNeighbours(S1); j.hasNext(); ) {
                    double[] neigh = ((GCSVertex)j.next()).getPosition();
                    for (int d = 0; d < dataDim; d++) {
                        neigh[d] = neigh[d] + (EPSILONN * (input[d] - neigh[d]));
                    }
                }
                // 4.  Increment the Signal Counter of s
                if (error_minimising) {
                    S1.setSignalCounter(S1.getSignalCounter() + S1Value);
                } else {
                    S1.setSignalCounter(S1.getSignalCounter() + 1.0);
                }
                // 5.  Decrease all signal counters by a fraction ALPHA
                for (Iterator i = graph.getAllVertices(); i.hasNext(); ) {
                    final GCSVertex dalpha = (GCSVertex)i.next();
                    dalpha.setSignalCounter(dalpha.getSignalCounter() - (ALPHA * dalpha.getSignalCounter()));
                }
                // Growth Phase
                if ((adaptation_step.getCounter() % LAMBDA == 0) && (graph.numVertices() <= nCellsMax)) {
                    if (application.Launcher.DEBUG) {
                        System.out.print("\n\nGrowth Phase...\n");
                        System.out.print("Graph consists of:\n");
                        for (Iterator i = graph.getSimplices(); i.hasNext(); ) {
                            GCSSimplex simplex = (GCSSimplex)i.next();
                            System.out.print(simplex + "\n");
                        }
                    }
                    // We do not know P(xi) explicitly, but with the local
                    // signal counters we can compute an estimate of P(xi),
                    // namely the relative frequency of the input signals
                    // received by a certain cell.
                    // 6.  The relative signal frequency of a cell is
                    //     NOT USED
                    double total_tau = 0.0d;
                    for (Iterator i = graph.getAllVertices(); i.hasNext(); ) {
                        total_tau += ((GCSVertex)i.next()).getSignalCounter();
                    }
                    // 7.  Always after a fixed number LAMBDA of adaptation
                    //     steps we determine the cell with the largest
                    //     signal counter
                    double hq = -Double.MAX_VALUE;
                    GCSVertex hqc = null;
                    for (Iterator i = graph.getAllVertices(); i.hasNext(); ) {
                        GCSVertex candidate = (GCSVertex)i.next();
                        double candidatesc = candidate.getSignalCounter();
                        if (candidatesc > hq) {
                            hq = candidatesc;
                            hqc = candidate;
                        }
                    }
                    // 8.  Determine the direct neighbour of hqc with the
                    //     largest distance in the input space
                    double fDistance = 0.0d;
                    GCSVertex fFurthest = null;
                    for (Iterator i = graph.getNeighbours(hqc); i.hasNext(); ) {
                        GCSVertex candidate = (GCSVertex)i.next();
                        double diff = GCSVertex.Minkowski(norm, hqc.getPosition(), candidate.getPosition());
                        if (diff > fDistance) {
                            fDistance = diff;
                            fFurthest = candidate;
                        }
                    }
                    if (application.Launcher.DEBUG) {
                        System.out.print("Largest signal counter is " + hqc + "\n");
                        System.out.print("Furthest neighbour of Largest signal counter is " + fFurthest + "\n");
                    }
                    // Store the nodes that are shared between the q and f nodes
                    // ready to connect to the newly inserted r node
                    fNeigh.removeAllElements();
                    for (Iterator i = graph.getNeighbours(fFurthest); i.hasNext(); ) {
                        GCSVertex candidate = (GCSVertex)i.next();
                        if (!fNeigh.contains(candidate)) {
                            if (candidate != fFurthest) {
                                fNeigh.add(candidate);
                                if (application.Launcher.DEBUG) System.out.print("fNeigh found " + candidate + "\n");
                            }
                        }
                    }
                    qNeigh.removeAllElements();
                    for (Iterator i = graph.getNeighbours(hqc); i.hasNext(); ) {
                        GCSVertex candidate = (GCSVertex)i.next();
                        if (!qNeigh.contains(candidate)) {
                            if (candidate != hqc) {
                                qNeigh.add(candidate);
                                if (application.Launcher.DEBUG) System.out.print("qNeigh found " + candidate + "\n");
                            }
                        }
                    }
                    shared.removeAllElements();
                    for (Iterator i = fNeigh.iterator(); i.hasNext(); ) {
                        GCSVertex candidate = (GCSVertex)i.next();
                        if (qNeigh.contains(candidate)) {
                            if (application.Launcher.DEBUG) System.out.print("shared found " + candidate + "\n");
                            shared.add(candidate);
                        }
                    }
                    // 9.  The synaptic vector of r is initialised to the
                    //     mid point of q and f
                    double[] rpos = Vertex.mid(fFurthest.getPosition(), hqc.getPosition());
                    GCSVertex r = new GCSVertex(rpos, String.valueOf(label++));
                    if (application.Launcher.DEBUG) System.out.print("created " + r.toString() + "\n");
                    // Insert connections between r and the
                    // shared nodes of f and q.
                    // The new cell is connected to the other cells in
                    // such a way that we have again a structure consisting
                    // of k dimensional simplices
                    Vector<GCSSimplex> marked = new Vector<GCSSimplex>();
                    for (Iterator i = shared.iterator(); i.hasNext(); ) {
                        GCSVertex vertex = (GCSVertex)i.next();
                        for (Iterator j = graph.getSimplices(); j.hasNext(); ) {
                            GCSSimplex s = (GCSSimplex)j.next();
                            if (s.contains(hqc) && s.contains(fFurthest) && s.contains(vertex)) {
                                marked.add(s);
                            }
                        }
                        if (application.Launcher.DEBUG) {
                            System.out.print("\n\nBEGINNING GROWTH PHASE\n");
                        }
                        GCSSimplex sq = new GCSSimplex(hqc, r, vertex);
                        if (application.Launcher.DEBUG) {
                            System.out.print("\n\nGCS request... adding simplex " + sq.toString() + "\n");
                        }
                        graph.add(sq);
                        GCSSimplex sf = new GCSSimplex(fFurthest, r, vertex);
                        if (application.Launcher.DEBUG) {
                            System.out.print("\n\nGCS request... adding simplex " + sf.toString() + "\n");
                        }
                        graph.add(sf);
                        for (Iterator itdel = marked.iterator(); itdel.hasNext(); ) {
                            GCSSimplex simplex = (GCSSimplex)itdel.next();
                            if (application.Launcher.DEBUG) {
                                System.out.print("\n\nGCS request... removing simplex" + simplex.toString() + "\n");
                            }
                            graph.remove(simplex);
                        }
                        if (application.Launcher.DEBUG) {
                            System.out.print("\n\nENDING GROWTH PHASE\n");
                        }
                    }
                    if (application.Launcher.DEBUG) {
                        System.out.print("Graph consists of:\n");
                        for (Iterator i = graph.getSimplices(); i.hasNext(); ) {
                            GCSSimplex simplex = (GCSSimplex)i.next();
                            System.out.print(simplex + "\n");
                        }
                    }
                    // Distribute the signal counters.
                    // The insertion of r leads to a new Voronoi region Fr in
                    // the input space.
                    // Determine the topographical neighbors of r
                    // for each topographical neighbour, determine
                    // the new approximate Voronoi volumes.
                    // The voronoi regions of the topographical neighbours
                    // of r are diminished.  This change is reflected by an
                    // according redistribution of the counter variables tau_c
                    //  10.  We compute the changes in signal counters as
                    double dec_total = 0.0d;
                    // Calculate the number of neighbours of r
                    final int counter = graph.degree(r);
                    for (Iterator rn = graph.getNeighbours(r); rn.hasNext(); ) {
                        GCSVertex di = (GCSVertex)rn.next();
                        di.setSignalCounter(di.getSignalCounter() - di.getSignalCounter() / counter);
                        dec_total += di.getSignalCounter() / counter;
                    }
                    // Finally, the initial counter value of the new cell
                    // is defined as
                    r.setSignalCounter(dec_total);
                    if (application.Launcher.DEBUG) {
                        System.out.print("Graph is " + graph.toString() + "\n");
                        System.out.print("end of growth\n");
                    }
                } // End of growth phase
                // Prunning Phase
                if (adaptation_step.getCounter() % LAMBDA_PRUNE == 0) {
                    if (application.Launcher.DEBUG)
                        System.out.print("\n\nPruning Phase...\n");
                    GCSVertex candidate = null;
                    for (Iterator i = graph.getAllVertices(); i.hasNext(); ) {
                        GCSVertex di = (GCSVertex)i.next();
                        if (application.Launcher.DEBUG) System.out.print("  Processing: " + di.toString() + "\n");
                        double distMax = 0.0d;
                        double dist = 0.0d;
                        int count = 0;
                        for (Iterator j = graph.getNeighbours(di); j.hasNext(); count++) {
                            GCSVertex dj = (GCSVertex)j.next();
                            dist += GCSVertex.Minkowski(norm, di.getPosition(), dj.getPosition());
                        }
                        dist /= count;
                        if (dist > distMax) {
                            distMax = dist;
                            candidate = di;
                        }
                    } // Generated to delete candidate
                    if (application.Launcher.DEBUG) {
                        System.out.print("\n\nBEGINNING PRUNING PHASE\n");
                        System.out.print("  Initiated by vertex " + candidate.toString() + "\n");
                    }
                    // those simplices that contain the candidate vertex are
                    // marked for removal from the graph
                    Vector<GCSSimplex> marked = new Vector<GCSSimplex>();
                    for (Iterator j = graph.getSimplices(); j.hasNext(); ) {
                        GCSSimplex simplex = (GCSSimplex)j.next();
                        if (simplex.contains(candidate)) {
                            marked.add(simplex);
                        }
                    }
                    // and having been marked for removal, are removed here
                    for (Iterator j = marked.iterator(); j.hasNext(); ) {
                        GCSSimplex simplex = (GCSSimplex)j.next();
                        graph.remove(simplex);
                        if (application.Launcher.DEBUG)
                            System.out.print("removed " + simplex.toString() + "\n");
                    }
                    if (application.Launcher.DEBUG) {
                        System.out.print("\n\nENDING PRUNING PHASE\n");
                    }
                } // End prunning phase
                this.delay(this.SHORT_DELAY);
                adaptation_step.increment();
            }
            this.delay(this.LONG_DELAY);
        }
    }
}

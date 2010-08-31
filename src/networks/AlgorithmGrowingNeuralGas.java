package networks;

import java.util.*;
import support.*;
import structures.*;

/**
 * <p>Title: Growing Neural Gas</p>
 * <p> Description: Generates a number of neighbourhood edges between the nodes of a
 * network in the style of Competitive Hebbian Learning from Martinetz
 * and Schulten 1991, and implements the Growing Neural Gas Fritzke 1994.</p>
 * <p> The GrowingNeuralGas implementation is an instance of a Graph comprising of GNGVertex objects
 * connected with GNGEdge objects.  The edges are undirected. </p>
 * <p>Copyright: Copyright (c) 2004</p> <p>Company:
 * University of Hertfordshire</p>
 * @author K.A.J.Doherty@herts.ac.uk
 * @version 1.0 19 to 26 February 2003 First Release
 * @version 1.1 01 June 2004 Graph Imnplementation
 * @version 1.2 06 July 2004 Re-implementation with jdsl api
 * @version 1.3 08 July 2004 Re-implemented with own graph class.
 * Added Error class as the arraylists of errors and nodes were not in sync?
 * @version 1.4 21 September 2004 Changed the observable functionality, as the Graph needs to be observable,
 * at a different rate to the input observer.
 * @version 1.5 14 October 2004 Removed all references to output rate - the algorithm now notifies all
 * registered observers on a change to the graph.
 * @version 1.6 04 January 2005 Removed a large amount of runtime error checking to speed things up
 * @version 1.7 10 February 2005 Moved initialisation into init()
 * @version 1.8 23 November 2007 Added <Generics> to silence compiler > 1.5 bitching about unsafes and casts
 * @version 1.9 21 February 2008 Wrapped log file output in DEBUG conditional
 */
final public class AlgorithmGrowingNeuralGas extends Algorithm {
    // Growing Neural Gas specific parameters not passed in from the command line
    // They don't seem to do that much!
    private final double ALPHA = 0.003; // Winners cooling rate
    private final double BETA = 0.001; // Global Cooling Rate
    // to code the discrepancies between the original
    // paper and the algorithm detailed on Frizke's website
    final private boolean ORIGINAL = false;
    // internal parameters passed in from main to launch GNG
    private Vertex[] theInputs; // the input space data
    private int ndatumsMax; // maximum number of graph vertices
    private int dataDimension; // Data dimensionality
    private double norm; // The distance norm
    private int epoch; // Number of epochs to run for
    private double EPSILONB; // Winner gets most
    private double EPSILONN; // Neighbours get less
    private int ALPHAMax; // Edge dies at this age
    private int LAMBDA; // Growth at iteration number
    private Counter iteration; // Iteration counter
    private ErrorTable errorTable; // local error table
    private Dealer dealer; // Epoch handler
    private int label = 0; // Label the vertices of the graph
    
    private Logger log;

    Graph  graph = new Graph();
    public Graph getGraph() {
        return graph;
    }

    public Vertex [] getInputs() {
      return theInputs;
    }

    /**
     * @param _data_ The data to be classified
     * @param _maxNodes_ The maximum size of the network
     * @param _dataDim_ The dimension of the data
     * @param _power_ The Minkowski norm to be used for training
     * @param _epoch_ The number of epochs to run for
     * @param _edgeDeletion_ The iteration to delete the edge at...
     * @param _nodeInsertion_ The iteration to insert a node at...
     * @param _epsilonb_ The BMU learning rate
     * @param _epsilonn_ The neighbourhood learning rate
     * @param _iter_ A reference to the iteration counter object
     */
    public AlgorithmGrowingNeuralGas(Vertex[] _data_,
                                     int _dataDim_,
                                     int _epoch_,
                                     Counter _iter_,
                                     double _power_,
                                     int _maxNodes_,
                                     double _epsilonb_,
                                     double _epsilonn_,
                                     int _nodeInsertion_,
                                     int _edgeDeletion_) {
            super();
            // pass the command line parameters
            theInputs = _data_;
            ndatumsMax = _maxNodes_;
            dataDimension = _dataDim_;
            norm = _power_;
            epoch = _epoch_;
            ALPHAMax = _edgeDeletion_;
            LAMBDA = _nodeInsertion_;
            EPSILONB = _epsilonb_;
            EPSILONN = _epsilonn_;
            iteration = _iter_;
            init();
    }

    final private void init() {
        // Create epoch handler dealer
        dealer = new Dealer(theInputs, epoch);

        if (application.Launcher.DEBUG) {
           log = new Logger();
	}

        // Step 0. randomise the two reference vectors in R dimensinal space
        // Initialise graph construct with two verices
        // conected by a single edge
        GNGVertex v1, v2;
        if (application.Launcher.DEBUG) {
            double[] p1 = {0.1d,0.99d};
            v1 = new GNGVertex(p1, String.valueOf(label++));
            double[] p2 = {0.11d,0.99d};
            v2 = new GNGVertex(p2, String.valueOf(label++));
        } else {
            v1 = new GNGVertex(rnd(dataDimension), String.valueOf(label++));
            v2 = new GNGVertex(rnd(dataDimension), String.valueOf(label++));
        }
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addEdge(new GNGEdge(v1, v2));
        // algorithm has an error table
        errorTable = new ErrorTable();
    }


    /** Start the training */
    public void run() {
        Vector<Edge> deleteEdges = new Vector<Edge>();
        Vector<Vertex> deleteVertices = new Vector<Vertex>();
        while (getState() != STOP) {
            // force a break when the dealer is finished
            if (!dealer.hasNext()) {
                this.setState(STOP);
                if (application.Launcher.DEBUG) {
                   log.close();
                }
                break;
            }
            if ((getState() != PAUSE)) {

                if (application.Launcher.DEBUG) {

                  double idtnum = getInducedDelaunayTriangulation().numSubGraphs();    
		    
                  log.append(
                    (new Double(getTopologyMeasure()).toString())+"\t"+
                    (new Double(getSSE()).toString())+"\t"+
                    (new Double(graph.numSubGraphs()).toString())+"\t"+
                    (new Double(idtnum).toString())+
                    "\n"
                  );
                }

                // Step 1. Select random input signal e
                //         according to P(e)
                Vertex input;
                if (application.Launcher.DEBUG) {
                  input = (Vertex)dealer.getNextFixed();
                } else {
                  input = (Vertex)dealer.getNext();
                }
                final double[] inputpos = input.getPosition();
                // Step 2. Find the nearest unit S1 and the second
                //         nearest unit S2
                // Generate global error table
                errorTable.clear();
                for (Iterator e = graph.getAllVertices(); e.hasNext(); ) {
                  GNGVertex vertex = ((GNGVertex)e.next());
                  errorTable.addEntry(vertex, GNGVertex.Minkowski(norm, input.getPosition(), vertex.getPosition()));
                }
                // Determine the winner S1
                GNGVertex ws1 = null; // Winning Node
                // Determine second nearest unit S2
                GNGVertex ws2 = null; // Second Node
                int loopcount = 1;
                for (Iterator e = errorTable.getEntries(); e.hasNext(); ) {
                  Entry entry = (Entry)e.next();
                  if (loopcount == 1) {
                     ws1 = (GNGVertex)entry.getVertex();
                  } else if (loopcount == 2) {
                     ws2 = (GNGVertex)entry.getVertex();
                  } else {
                     continue;
                  }
                  loopcount++;
                }
                // Step 4. Set error of S1
                ws1.setError(ws1.getError() + (errorTable.getError(ws1) * errorTable.getError(ws1)));
                if (ws1 == null) {
                    System.err.print("GNG: Unable to determine a winner\n");
                    System.exit(-2);
                }
                if (ws2 == null) {
                    System.err.print("GNG: Unable to determine a second place\n");
                    System.exit(-2);
                }
                // Step 3. Increment the age of all edges eminating from S1
                Iterator S1_edges = graph.getIncidentEdges(ws1);
                if (S1_edges != null) {
                  for ( ; S1_edges.hasNext(); ) {
                    ((GNGEdge)S1_edges.next()).incrementAge();
                  }
                }
                else {
                  System.err.print("GNG: Unable to get an edge iterator from the winner\n");
                  System.exit(-2);
                }

                // Step 5.
                // Adapt the reference vector of the winner
                double[] pws1 = ws1.getPosition();
                for (int i = 0; i < dataDimension; i++) {
                    pws1[i] = pws1[i] + (EPSILONB * (inputpos[i] - pws1[i]));
                }
                ws1.setPosition(pws1);
                // Adapt the reference vector of the neighbourhood
                Iterator S1_neighbours = graph.getNeighbours(ws1);
                if (S1_neighbours != null) {
                  for ( ; S1_neighbours.hasNext(); ) {
                    GNGVertex vertex = (GNGVertex)S1_neighbours.next();
                    double[] vlen = vertex.getPosition();
                    for (int i = 0; i < dataDimension; i++) {
                      vlen[i] = vlen[i] + (EPSILONN * (inputpos[i] - vlen[i]));
                    }
                    vertex.setPosition(vlen);
                  }
                }

                // Step 6. If S1 and S2 are connected by and edge, then
                //           set age to zero
                //         else
                //           edge doesn't exist so create edge between S1 and S2
                if (graph.areConnected(ws1, ws2)) {
                  ((GNGEdge)graph.connection(ws1, ws2)).resetAge();
                } else {
                  graph.addEdge(new GNGEdge(ws1, ws2));
                }

                // Step 7. Remove all edges with age greater than ALPHAMax
                if (graph.numVertices() > 2) {
                  deleteEdges.removeAllElements();
                  for (Iterator e = graph.getAllEdges(); e.hasNext(); ) {
                    GNGEdge edge = (GNGEdge)e.next();
                    if (edge.getAge() > ALPHAMax) {
                      deleteEdges.add(edge);
                    }
                  }
                  for (Iterator i = deleteEdges.iterator(); i.hasNext(); ) {
                    GNGEdge e = (GNGEdge)i.next();
                    graph.deleteEdge(e);
                  }
                }
                // Remove vertices from the graph if they have no neighbours
                deleteVertices.removeAllElements();
                for (Iterator e = graph.getAllVertices(); e.hasNext(); ) {
                  final GNGVertex vertex = (GNGVertex)e.next();
                  if (graph.degree(vertex) == 0) {
                    deleteVertices.add(vertex);
                  }
                }
                for (Iterator i = deleteVertices.iterator(); i.hasNext(); ) {
                  GNGVertex v = (GNGVertex)i.next();
                  graph.deleteVertex(v);
                }
                if (graph.numVertices() < ndatumsMax) {
                    // Step 8. If the number of input signals generated so far is
                    //         an integer multiple of lambda, insert a new unit
                    if (iteration.getCounter() % LAMBDA == 0) {
                        double qValue = -Double.MAX_VALUE;
                        GNGVertex q = null;
                        for (Iterator e = graph.getAllVertices(); e.hasNext(); ) {
                          GNGVertex vertex = (GNGVertex)e.next();
                          if (vertex.getError() > qValue) {
                            qValue = vertex.getError();
                            q = vertex;
                          }
                        }
                        if (q == null) {
                          System.err.print("GrowingNeuralGas: Unable to determine the " +
                                "node with the maximum accumilated error\n");
                          System.exit(-2);
                        }
                        // Determine neighbour f of Datum q with the
                        // maximum accumulated error
                        double fValue = -Double.MAX_VALUE;
                        GNGVertex f = null;
                        Iterator q_neighbours = graph.getNeighbours(q);
                        if (q_neighbours != null) {
                          for ( ; q_neighbours.hasNext(); ) {
                            GNGVertex vertex = ((GNGVertex)q_neighbours.next());
                            double error = vertex.getError();
                            if (error > fValue) {
                              fValue = error;
                              f = vertex;
                            }
                          }
                          if (f == null) {
                            System.err.print("GrowingNeuralGas: Unable to find a neighbour of q\n");
                            System.exit(-2);
                          }
                        }
                        // Create a new Datum r, with weight vector the average
                        // of f and q
                        // for some God known reason, f is sometimes null?
                        // I would have thought q would always have a neighbourhood -
                        // I am obviously not managing the graph correctly?
                        // So, with the null checks above and these
                        // checks, it should never happen without exiting the runtime.
                        //
                        // 8 Feb 2005 - Refactoring!
                        // I still don't have the balls (or inclination) to remove these
                        // null checks - so here they stay
                        if (f != null && q != null) {
                            double[] rpos = new double[dataDimension];
                            final double[] fpos = f.getPosition();
                            final double[] qpos = q.getPosition();
                            for (int i = 0; i < dataDimension; i++) {
                                rpos[i] = (fpos[i] + qpos[i]) / 2.0d;
                            }
                            GNGVertex r = new GNGVertex(rpos, String.valueOf(label++));
                            // Decrease the accumulated errors of f and q
                            final double auError = f.getError();
                            final double buError = q.getError();
                            f.setError(auError - (ALPHA * auError));
                            q.setError(buError - (ALPHA * buError));
                            // In original paper: Initialise the error value of
                            // r with the new value of the error value of q
                            // On website: error value of r is set to mean of
                            // errors of q and f
                            if (ORIGINAL) {
                                r.setError(buError);
                            }
                            else {
                                r.setError((auError + buError) / 2.0d);
                            }
                            // and insert edges between q and f, and the new vertex r
                            // Add new vertex r to the graph
                            graph.addVertex(r);
                            graph.addEdge(new GNGEdge(q, r));
                            graph.addEdge(new GNGEdge(r, f));
                            graph.deleteEdge(graph.connection(f, q));
                        } else {
                            System.err.print("AlgorithmGrowingNeuralGas: f or q is null");
                            System.exit(-2);
                        } // End of f and q null checks
                    } // End of growing stage
                } // End exceeeded maximum number of Datums
                // Step 9. Decrease all error values by multiplying them with
                //         a constant BETA
                for (Iterator e = graph.getAllVertices(); e.hasNext(); ) {
                  final GNGVertex vertex = (GNGVertex)e.next();
                  vertex.setError(vertex.getError() * BETA);
                }
                // Increment the iteration counter
                this.delay(this.SHORT_DELAY);
                iteration.increment();
            } // if not PAUSE
            this.delay(this.LONG_DELAY);
        } // End while less than epoch and RUN
    } // end run()

}

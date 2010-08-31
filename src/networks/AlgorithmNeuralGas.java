package networks;

import java.util.*;
import support.*;
import structures.*;

/**
 * <p>Title: Neural Gas</p>
 * <p> Description: The Neural Gas Algorithm</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company:
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
 */
final public class AlgorithmNeuralGas extends Algorithm {
    // internal parameters passed in from main to launch GNG
    private Vertex[] theInputs; // the input space data
    private int ndatumsMax; // maximum number of graph vertices
    private int dataDimension; // Data dimensionality
    private double norm; // The distance norm
    private int epoch; // Number of epochs to run for
    private Counter iteration; // Iteration counter
    private double li;
    private double lf;
    private double ei;
    private double ef;

    private int T_MAX;
    private ErrorTable errorTable; // local error table
    private Dealer dealer; // Epoch handler
    private int label = 0; // Label the vertices of the graph

    Graph  graph = new Graph();
    public Graph getGraph() {
        return graph;
    }

    public Vertex [] getInputs() {
      return theInputs;
    }


    /**
     * @param _data_ The data to be classified
     * @param _dataDim_ The dimension of the data
     * @param _epoch_ The number of epochs to run for
     * @param _iter_ Iteration Counter
     * @param _power_ The Minkowski norm to be used for training
     * @param _maxNodes_ The maximum size of the network
     * @param _neighbourhood_initial_
     * @param _neighbourhood_final_
     * @param _learning_initial_ 
     * @param _learning_final_ 
     */
    public AlgorithmNeuralGas(Vertex[] _data_,
                              int _dataDim_,
                              int _epoch_,
                              Counter _iter_,
                              double _power_,
                              int _maxNodes_,
                              double _neighbourhood_initial_,
                              double _neighbourhood_final_,
                              double _learning_initial_,
                              double _learning_final_) {
        super();
        // pass the command line parameters
        theInputs = _data_;
        ndatumsMax = _maxNodes_;
        dataDimension = _dataDim_;
        norm = _power_;
        epoch = _epoch_;
        iteration = _iter_;

        li = _neighbourhood_initial_;
        lf = _neighbourhood_final_;
        ei = _learning_initial_;
        ef = _learning_final_;

        init();
    }

    final private void init() {
        // Create epoch handler dealer
        dealer = new Dealer(theInputs, epoch);
        // Step 0. randomise all the reference vectors in R dimensinal space
        // and add to the graph structure
        for (int i = 0; i < ndatumsMax; i++) {
            if (application.Launcher.DEBUG) {
                double[] p1 = {0.1d,0.99d};
                graph.addVertex(new GNGVertex(p1, String.valueOf(label++)));
            } else {
                graph.addVertex(new GNGVertex(rnd(dataDimension), String.valueOf(label++)));
            }
        }
        // algorithm has an error table
        errorTable = new ErrorTable();
        // Total number of iterations
        T_MAX = theInputs.length * epoch;
    }

    /** Start the training */
    public void run() {
        double et;
        double ethl;
        double lt;
        while (getState() != STOP) {
            // force a break when the dealer is finished
            if (!dealer.hasNext()) {
                this.setState(STOP);
                break;
            }
            if ((getState() != PAUSE)) {
                // Set up the time dependencies
                double power = ((double)iteration.getCounter()) / ((double)T_MAX);
                lt = li * Math.pow(lf / li, power);
                et = ei * Math.pow(ef / ei, power);
                // Step 1. Select random input signal e
                //         according to P(e)
                Vertex input;
                if (application.Launcher.DEBUG) {
                    input = (Vertex)dealer.getNextFixed();
                } else {
                    input = (Vertex)dealer.getNext();
                }
                final double[] inputpos = input.getPosition();
                // Generate global error table
                errorTable.clear();
                for (Iterator e = graph.getAllVertices(); e.hasNext(); ) {
                    GNGVertex vertex = ((GNGVertex)e.next());
                    errorTable.addEntry(vertex, GNGVertex.Minkowski(norm, inputpos, vertex.getPosition()));
                }
                int num = 0;
                for (Iterator e = errorTable.getEntries(); e.hasNext(); num++) {
                    Vertex vertex = (Vertex)(((Entry)e.next()).getVertex());
                    ethl = Math.exp(-num / lt) * et;
                    double[] pws1 = vertex.getPosition();
                    for (int i = 0; i < dataDimension; i++) {
                        pws1[i] = pws1[i] + (ethl * (inputpos[i] - pws1[i]));
                    }
                    vertex.setPosition(pws1);
                }
                // Increment the iteration counter
                this.delay(this.SHORT_DELAY);
                iteration.increment();
            } // if not PAUSE
            this.delay(this.LONG_DELAY);
        } // End while less than epoch and RUN
    } // end run()

}

package networks;

import structures.Graph;
import structures.Vertex;
import support.Dealer;
import structures.Counter;
import java.util.Iterator;

/**
 * <p>Title: Competitve Learning</p>
 *
 * <p>Description: A Competitve Learning Algorithm Demo for Rod Adams</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */
public class AlgorithmCompetitiveLearningDemo extends Algorithm {
        // internal parameters passed in from main to launch CL
        private Vertex[] theInputs; // the input data
        private int ndatumsMax; // maximum number of graph vertices
        private int dataDimension; // Data dimensionality
        private double norm; // The distance norm
        private int epoch; // Number of epochs to run for
        private Counter iteration; // Iteration counter
        private double EPSILONB; // WTA learning rate
        private ErrorTable errorTable; // local error table
        private Dealer dealer; // Epoch handler
        private int label = 0; // Label the vertices of the graph

        private int clbsdelay;
        private int claiidelay;
        private int clabidelay;
        private int clabmdelay;


        Graph  graph = new Graph();
        public Graph getGraph() {
            return graph;
        }


        public Vertex [] getInputs() {
          return theInputs;
        }

        public AlgorithmCompetitiveLearningDemo(Vertex[] _data_,
                                            int _dataDim_,
                                            int _epoch_,
                                            Counter _iter_,
                                            double _power_,
                                            int _maxNodes_,
                                            double _epsilonb_,
                                            int bsdelay,
                                            int aiidelay,
                                            int abidelay,
                                            int abmdelay) {
                super();
                // pass the command line parameters
                theInputs = _data_;
                ndatumsMax = _maxNodes_;
                dataDimension = _dataDim_;
                norm = _power_;
                epoch = _epoch_;
                iteration = _iter_;
                EPSILONB = _epsilonb_;

                // delay initialisation
                clbsdelay =  bsdelay;
                claiidelay = aiidelay;
                clabidelay = abidelay;
                clabmdelay =  abmdelay;
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
        }

        /** Start the training */
        public void run() {
            Vertex input = null;
            Vertex bmu = null;
            while (getState() != STOP) {
                // force a break when the dealer is finished
                if (!dealer.hasNext()) {
                    this.setState(STOP);
                    break;
                }
                if ((getState() != PAUSE)) {
                    // Step 1. Select random input signal e
                    //         according to P(e)
                    if (application.Launcher.DEBUG) {
                        input = (Vertex)dealer.getNextFixed();
                    } else {
                        input = (Vertex)dealer.getNext();
                    }

                    //
                    // Set the input as selected for display
                    //
                    input.setSelected( true );
                    iteration.increment();
                    delay(claiidelay);

                    final double[] inputpos = input.getPosition();
                    // Generate global error table
                    errorTable.clear();
                    for (Iterator e = graph.getAllVertices(); e.hasNext(); ) {
                        GNGVertex vertex = ((GNGVertex)e.next());
                        errorTable.addEntry(vertex, GNGVertex.Minkowski(norm, inputpos, vertex.getPosition()));
                    }
                    // ErrorTable iterator returns an ordered Iterator.  As this is
                    // a WTA algorithm, we simply get the winner, update it and break.
                    for (Iterator e = errorTable.getEntries(); e.hasNext(); ) {
                        bmu = (Vertex)(((Entry)e.next()).getVertex());


                        bmu.setSelected(true);
                        iteration.increment();
                        delay(clabidelay);

                        double[] pws1 = bmu.getPosition();
                        for (int i = 0; i < dataDimension; i++) {
                            pws1[i] = pws1[i] + (EPSILONB * (inputpos[i] - pws1[i]));
                        }

                        bmu.setPosition(pws1);
                        iteration.increment();
                        delay(clabmdelay);
                        break;

                    }
                    // Increment the iteration counter
                    bmu.setSelected(false);
                    input.setSelected(false);
                    iteration.increment();
                    delay(clbsdelay);
                } // if not PAUSE
                this.delay(50);
            } // End while less than epoch and RUN
        } // end run()

}

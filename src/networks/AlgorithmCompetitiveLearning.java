package networks;

import java.util.*;
import support.*;
import structures.*;

/**
 *
 * <p>Title: Competitve Learning</p>
 *
 * <p>Description: The Competitve Learning Algorithm</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public class AlgorithmCompetitiveLearning extends Algorithm {
    // internal parameters passed in from main to launch CL
    private Vertex[] theInputs; // the input data
    private int ndatumsMax; // maximum number of graph vertices
    private int dataDimension; // Data dimensionality
    private double norm; // The distance norm
    private int epoch; // Number of epochs to run for
    private double EPSILONB; // WTA learning rate
    private ErrorTable errorTable; // local error table
    private int label = 0; // Label the vertices of the graph

    Graph  graph = new Graph();
    public Graph getGraph() {
        return graph;
    }


    public Vertex [] getInputs() {
      return theInputs;
    }

    /**  */
    public AlgorithmCompetitiveLearning(Vertex[] _data_,
                                        int _dataDim_,
                                        int _epoch_,
                                        double _power_,
                                        int _maxNodes_,
                                        double _epsilonb_) {
            super();
            // pass the command line parameters
            theInputs = _data_;
            ndatumsMax = _maxNodes_;
            dataDimension = _dataDim_;
            norm = _power_;
            epoch = _epoch_;
            EPSILONB = _epsilonb_;
            init();
    }

    final private void init() {
        // Create epoch handler dealer
        dealer = new Dealer(theInputs, epoch);
        // Step 0. randomise all the reference vectors in R dimensinal space
        // and add to the graph structure
        for (int i = 0; i < ndatumsMax; i++) {
            if (log.isDebugEnabled()) {
                double[] p1 = {0.1d,0.99d};
                graph.addVertex(new GNGVertex(p1, String.valueOf(label++)));
            } else {
                graph.addVertex(new GNGVertex(rnd(dataDimension), String.valueOf(label++)));
            }
        }
        // algorithm has an error table
        errorTable = new ErrorTable();
    }
    
    protected void initialize() {
    	
    }
    
    protected void iterate() {
    	Vertex input;
        if (log.isDebugEnabled()) {
            input = (Vertex)dealer.getNextFixed();
        } else {
            input = (Vertex)dealer.getNext();
        }
        final double[] inputpos = input.getPosition();
        // Generate global error table
        errorTable.clear();
        for (Iterator<Vertex> e = graph.getAllVertices(); e.hasNext(); ) {
            GNGVertex vertex = ((GNGVertex)e.next());
            errorTable.addEntry(vertex, GNGVertex.Minkowski(norm, inputpos, vertex.getPosition()));
        }
        // ErrorTable iterator returns an ordered Iterator.  As this is
        // a WTA algorithm, we simply get the winner, update it and break.
        for (Iterator<Entry> e = errorTable.getEntries(); e.hasNext(); ) {
            Vertex vertex = (Vertex)((e.next()).getVertex());
            double[] pws1 = vertex.getPosition();
            for (int i = 0; i < dataDimension; i++) {
                pws1[i] = pws1[i] + (EPSILONB * (inputpos[i] - pws1[i]));
            }
            vertex.setPosition(pws1);
            break;
        }
	
    }
    
}

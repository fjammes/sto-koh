package networks;

import java.util.*;
import support.*;
import structures.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: SOM</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public class AlgorithmSOM extends Algorithm {

    // SOM parameters passed in from the command line
    private double li;
    private double lf;
    private double ei;
    private double ef;

    private Vertex[] theInputs; // the input space data
    private int dataDimension; // Data dimensionality
    private double norm; // The distance norm
    private int epoch; // Number of epochs to run for
    private Counter iteration; // Iteration counter
    private int nRows;
    private int nColumns;

    private int T_MAX;
    private Dealer dealer; // Epoch handler

    private int label = 0;

    private SOMVertex[][] theSOM;

    public AlgorithmSOM(Vertex[] _data_,
                        int _dataDim_,
                        int _epoch_,
                        Counter _iter_,
                        double _power_,
                        int _rows_,
                        int _columns_,
                        double _neighbourhood_initial_,
                        double _neighbourhood_final_,
                        double _learning_initial_,
                        double _learning_final_) {

        // pass the command line parameters
        theInputs = _data_;
        dataDimension = _dataDim_;
        norm = _power_;
        epoch = _epoch_;
        iteration = _iter_;
        nRows = _rows_;
        nColumns = _columns_;
        li = _neighbourhood_initial_;
        lf = _neighbourhood_final_;
        ei = _learning_initial_;
        ef = _learning_final_;

        init();
    }


    Graph  graph = new Graph();
    public Graph getGraph() {
        return graph;
    }

    public Vertex [] getInputs() {
      return theInputs;
    }



    final private void init() {

        theSOM = new SOMVertex[nRows][nColumns];

        // Create epoch handler dealer
        dealer = new Dealer(theInputs, epoch);
        // Step 0. randomise the two reference vectors in R dimensinal space
        // Initialise graph construct with two verices
        // conected by a single edge
        for (int r = 0; r < nRows; r++) {
            for (int c = 0; c < nColumns; c++) {
              SOMVertex vertex;
              if (application.Launcher.DEBUG) {
                double[] p1 = {0.1d,0.99d};
                vertex = new SOMVertex(p1, String.valueOf(label++));
              } else {
                vertex = new SOMVertex(rnd(dataDimension), String.valueOf(label++));
              }
              theSOM[r][c] = vertex;
              graph.addVertex(vertex);
            }
        }

        // Connect the vertices
        for (int r = 0; r < nRows-1; r++) {
            for (int c = 0; c < nColumns-1; c++) {
                graph.addEdge(new SOMEdge(theSOM[r][c], theSOM[r][c+1]));
                graph.addEdge(new SOMEdge(theSOM[r][c], theSOM[r+1][c]));
            }
        }
        // Fill in the boundary edges of the graph
        for (int r = 0; r < nRows-1; r++) {
            graph.addEdge(new SOMEdge(theSOM[r][nColumns-1], theSOM[r+1][nColumns-1]));
        }
        for (int c = 0; c < nColumns-1; c++) {
            graph.addEdge(new SOMEdge(theSOM[nRows-1][c], theSOM[nRows-1][c+1]));
        }

        // Total number of iterations
        T_MAX = theInputs.length * epoch;

    }



   public void run(){

       int bmuRow = -1;
       int bmuColumn = -1;

        while (getState() != STOP) {
            // force a break when the dealer is finished
            if (!dealer.hasNext()) {
                this.setState(STOP);
                break;
            }
            if ((getState() != PAUSE)) {

              // Select random input
              // Step 1. Select random input signal e
              //         according to P(e)
              Vertex input;
              if (application.Launcher.DEBUG) {
                  input = (Vertex)dealer.getNextFixed();
              } else {
                  input = (Vertex)dealer.getNext();
              }
              double[] inputpos = input.getPosition();

              // Generate errors
              double minerror = Double.MAX_VALUE;
              SOMVertex bmu= null;
              for (Iterator e = graph.getAllVertices(); e.hasNext(); ) {
                  SOMVertex vertex = ((SOMVertex)e.next());
                  double error = Vertex.Minkowski(norm, inputpos, vertex.getPosition());
                  if (error < minerror) {
                    minerror = error;
                    bmu = vertex;
                  }
              }

              // Set up the time dependencies
              double power = ((double) iteration.getCounter()) / ((double) T_MAX);
              // Neighbourhood size
              double lt = li * Math.pow(lf/li,power);
              double ltlt2 = lt * lt * 2;
              // Learning rate
              double et = ei * Math.pow(ef/ei,power);

              // Search theSOM looking for bmu
              for (int r = 0; r < nRows; r++) {
                for (int c = 0; c < nColumns; c++) {
                  if ( theSOM[r][c] == bmu ) {
                    bmuRow = r;
                    bmuColumn = c;
                  }
                }
              }

              // update each unit
              for (int r = 0; r < nRows; r++) {
                for (int c = 0; c < nColumns; c++) {
                  double d = Math.abs(bmuRow-r)+Math.abs(bmuColumn-c);
                  double hrs = Math.exp( -(d * d) / ltlt2 );
                  double [] pos = theSOM[r][c].getPosition();
                  for (int i = 0; i < dataDimension; i++){
                    pos[i] += et * hrs * (inputpos[i] - pos[i]);
                  }
                }
              }
              this.delay(this.SHORT_DELAY);
              iteration.increment();
            }
            this.delay(this.LONG_DELAY);
        }
   }

}

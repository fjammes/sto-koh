package networks;

import structures.*;
import support.*;
import Jama.*;
import java.util.*;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: Provides an abstract stub for concrete algorithm classes.
 * The algorithms have a runnable state, and must provide an implementation of the run()
 * and getGraph() methods.  In addition, the run() method should make
 * a call to delay() to yield some cpu time.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (e-mail K.A.J.Doherty@herts.ac.uk)
 * @version 1.0 Initial Release
 * @version 1.1 Added static int for long and short delays for thread yield
 */
abstract public class Algorithm implements Runnable {

    // Set the possible states
    final static public int STOP = -1;
    final static public int PAUSE = 0;
    final static public int RUN = 1;
    protected int _state_ = PAUSE;


    /**
     * State setter
     * @param state int
     */
    final public void setState(int state) {
        _state_ = state;
    }

    /**
     * State getter
     * @return int
     */
    final public int getState() {
        return _state_;
    }


    abstract public Vertex [] getInputs();

    // All algorithms have a Graph
    // and somethings need access to it
    abstract public Graph getGraph();

    // All algorithms are runnable, so we must run()
    abstract public void run();


    // thread yield parameters
    final static int SHORT_DELAY = 1;
    final static int LONG_DELAY = 10;

    // and sleep() for a while
    protected void delay(int milliseconds) {
      try {
        Thread.sleep(milliseconds);
      }
      catch (InterruptedException ie) {}
    }


    /**
     * Get the SSE for the classified data
     * @return double
     */
    public double getSSE() {
      final Vertex [] v = new Vertex[this.getGraph().numVertices()];
      int count = 0;
      for (java.util.Iterator i = this.getGraph().getAllVertices(); i.hasNext(); count++){
        v[count] = ((Vertex)i.next());
      }
      return (new Classification(this.getInputs(), v)).sse();
    }


    /**
     * Generate the induced delaunay triangulation from the
     * network nodes and the input data
     * @return Graph The Induced Delaunay Triangulation
     */
    public Graph getInducedDelaunayTriangulation() {
      Graph triangulation = new Graph();

      // Create an array of references to the algorithm graph vertices
      Vertex [] v = new Vertex[this.getGraph().numVertices()];
      int count = 0;
      for (java.util.Iterator i = this.getGraph().getAllVertices(); i.hasNext(); count++){
        v[count] = ((Vertex)i.next());
        triangulation.addVertex(v[count]);
      }


      // iterate over all members of the input data, and
      // create the induced Delaunay Triangulation
      java.util.TreeMap<Double,Vertex> map = new java.util.TreeMap<Double,Vertex>();
      final Vertex [] inputs = this.getInputs();
      for (int i = 0; i < inputs.length; i++) {
        map.clear();
        Vertex input = inputs[i];
        for (int j = 0; j < v.length; j++ ) {
          map.put(new Double( Vertex.Minkowski(2.0d, input.getPosition(), v[j].getPosition())),v[j]);
        }

        Vertex vc = (Vertex) map.remove( (Double) map.firstKey() );
        Vertex vs = (Vertex) map.remove( (Double) map.firstKey() );
        if ( !triangulation.areConnected(vc,vs) ) {
          triangulation.addEdge(new ConcreteEdge(vc, vs));
        }
      }


      return triangulation;
    }




    /**
     * Public getter for the correlation coefficient
     */
    public double getTopologyMeasure() {
      return createTopologyMeasure();
    }



    /**
     * Private work function
     * @return
     */
    private double createTopologyMeasure() {

      Graph network = this.getGraph();
      Matrix nm = new Matrix(network.numVertices(), network.numVertices());

      Graph induced = this.getInducedDelaunayTriangulation();
      Matrix im = new Matrix(induced.numVertices(), induced.numVertices());

      // create the adjacency matrices

      int ii = 0;
      Iterator it = network.getAllVertices();
      Vertex [] vit = new Vertex[network.numVertices()];
      for (Iterator i = network.getAllVertices(); i.hasNext(); ) {
        vit[ii++] = (Vertex) i.next();
      }
      for (int vii = 0; vii < vit.length; vii++) {
        for (int vjj = 0; vjj < vit.length; vjj++) {
          nm.set(vii, vjj, network.distance(vit[vii],vit[vjj]));
          im.set(vii, vjj, induced.distance(vit[vii],vit[vjj]));
        }
      }
      return HC(nm,im);
    }


    /**
     * private hierarchical correlation for the lower halves of the
     * network adjacency matrix and the Induced Delaunay Triangulation
     * adjancey matrix
     * @param pdist
     * @param hdist
     * @return
     */
    final private double HC(final Matrix pdist, final Matrix hdist){
      int i,j; // General purpose indexing
      int row = 0; // Matrix row pointer
      int column = 0; // Matrix column pointer
      int numElements = 0; // Number of elements of lower triangle of matrices
      double avh = 0.0d; // Average of the hdist matrix
      double avd = 0.0d; // Average of the pdist matrix
      // Cophenetic Correlation Coefficient
      double cpcc = 0.0d; // Hierarchical Correlation Coefficient
      double d1 = 0.0d; // numerator pdist partial term
      double h1 = 0.0d; // numerator hdist partial term
      double num = 0.0d; // numerator term
      double cpcc_d2 = 0.0d; // denominator pdist squared term
      double cpcc_h2 = 0.0d; // denominator hdist squared term
      double den = 0.0d; // denominator term
      final int numLeaves = pdist.getColumnDimension();
      // Calculate the mean of pdist and hdist
      for (i = 1; i < numLeaves; i++) {
        for (j = 0; j < i ; j++) {
          numElements++;
          avd += pdist.get(i,j);
          avh += hdist.get(i,j);
        }
      }
      avd /= numElements;
      avh /= numElements;

      // Calculate the various terms
      for (i = 1; i < numLeaves; i++) {
        for (j = 0; j < i ; j++) {
          // numerator term
          d1 = pdist.get(i,j) - avd;
          h1 = hdist.get(i,j) - avh;
          num += d1 * h1;
          // denominator partial terms
          cpcc_d2 += d1 * d1;
          cpcc_h2 += h1 * h1;
        }
      }
      return num /  Math.sqrt(cpcc_d2 * cpcc_h2);
    }







    /**
     * Helper function to generate a d-dimensional array of randoms
     * @param d int
     * @return double[]
     */
    final protected double[] rnd(final int d) {
        final double[] r = new double[d];
        for (int i = 0; i < d; i++) {
            r[i] = Math.random();
        }
        return r;
    }

}


/**
 *
 * <p>Title: Competitive Learning</p>
 * <p>Description: Classify the input against the
 * network for SSE calculation</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: University of Hertfordshire</p>
 * @author not attributable
 * @version 1.0
 */
class Classification {

    private support.Cluster [] theClassification = null;
    private Vertex [] input = null;
    private Vertex [] output = null;
    private int inlen = 0;
    private int outlen = 0;

    Classification (final Vertex [] in, final Vertex [] out){
      input = in;
      output = out;
      outlen = output.length;
      inlen = input.length;
      theClassification = new Cluster[outlen];
      for (int i = 0; i < outlen; i++) {
        theClassification[i] = new Cluster();
      }
      classify(input, output);
    }



    final private void classify(final Vertex [] in, final Vertex [] out){
      double d;
      double dist;
      int smallest;
      int i, n;

      for (i = inlen; --i >= 0; ){
        smallest = 0;
        dist = Double.MAX_VALUE;
        for (n = 0; n < outlen; n++){
          d = Vertex.Minkowski(2.0d,in[i].getPosition(), out[n].getPosition());
          if (d < dist) {
            dist = d;
            smallest = n;
          }
        }
        theClassification[smallest].add(input[i]);
      }
    }

    final public double sse(){
      double sse = 0.0d;
      for (int i = 0; i < theClassification.length; i++){
        Cluster c = theClassification[i];
        Vertex u = (Vertex) output[i];
        for (int j = 0; j < c.size(); j++){
          sse += Math.pow(Vertex.Minkowski(2.0d, c.get(j).getPosition(), u.getPosition()), 2.0d);
        }
      }
      return sse;
    }


}

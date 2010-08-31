package ui;


import java.util.*;
import networks.*;
import structures.*;

import Jama.*;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: A suite of Competitve Learning Algorithms</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public class TopologyMeasurePanel {

  Hashtable parameters;
  Algorithm algorithm;

  /**
   * Measure the correlation coefficient between the
   * network graph and the induced delaunay triangulation
   * @param algorithm
   * @param parameters
   */
  public TopologyMeasurePanel(Algorithm algorithm, Hashtable parameters) {
    this.parameters = parameters;
    this.algorithm = algorithm;
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

    Graph network = this.algorithm.getGraph();
    Matrix nm = new Matrix(network.numVertices(), network.numVertices());

    Graph induced = this.algorithm.getInducedDelaunayTriangulation();
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
    return Tc(nm,im);
  }


  /**
   * private Topological Correlation for the lower halves of the
   * network adjacency matrix and the Induced Delaunay Triangulation
   * adjancey matrix
   * @param pdist
   * @param hdist
   * @return
   */
  final private double Tc(final Matrix pdist, final Matrix hdist){
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
}
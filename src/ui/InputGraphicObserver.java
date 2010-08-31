package ui;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import structures.*;
import support.*;

/**
 * <p>Title: Input Graphic Observer</p>
 * <p>Description: Graphic Display of Input Data and Growing Neural Gas network.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: University of Hertfordshire</p>
 * @author K.A.J.Doherty@herts.ac.uk
 * @version 1.1 Implemented as an Observer
 * @version 1.2 Incorporated interval functionality for user defined update rate,
 * and placed a repaint timer on the panel for good measure.
 * @version 1.3 Removed Observer functionality
 * @version 1.4 Refactor - create helper methods for drawing axis, input data
 * and network vertices.  Reworked scaling calculations ans removed a whole
 * shed load of redundant calculations.  I can't see any speed up!
 * @version 1.5 Added Induced Delaunay Triangulation drawing routine with
 * parameter hashtable
 */
public class InputGraphicObserver extends JPanel {

    private boolean COLOR = true;
    // Constructor parameters
    private Graph theGraph;
    private Graph theDelaunay;
    private Vertex[] thedata = null;
    private Hashtable parameters;
    //  private int iteration = 0;
    final private Coloriser color = new Coloriser();
    final private Color axis = new Color(0, 0, 0);
    final private Color text = new Color(0, 0, 0);
    final private Color nodes = new Color(0, 0, 0);
    final private Color data = new Color(100, 100, 100);
    final private Color data_selected = new Color(255, 0, 0);
    final private Color delaunay_edges_color = new Color(100, 150, 0);
    private Rectangle r;
    private int xscale;
    private int yscale;

    private int rw2panLR;
    private int rh2panUD;

    final private int charsize = 12;
    private int X0 = 0;
    private int X1 = 1;

    /**
     * Display X0 axis setter
     * @param dim int
     */
    public synchronized void setX0(int dim) {
        X0 = dim;
        this.repaint();
    }

    /**
     * Display X0 axis getter
     * @return int
     */
    public int getX0() { return X0; }

    /**
     * Display X1 axis setter
     * @param dim int
     */
    public synchronized void setX1(int dim) {
        X1 = dim;
        this.repaint();
    }

    /**
     * Display X1 axis getter
     * @return int
     */
    public int getX1() { return X1; }




    // Constructor
    public InputGraphicObserver(Vertex[] data, Hashtable parameters) {
        super();
        this.thedata = data;
        this.parameters = parameters;
        // Configure the display
        this.setBackground(new Color(255, 255, 255));
    }

    /**
     * Calls to paint() with a graph should be directed through this public method.
     * @param g Graph
     * @param delaunay Graph
     */
    public void trypaint(Graph g, Graph delaunay) {
      if (g != null) {
        theGraph = g;
      }
      if (delaunay != null) {
        theDelaunay = delaunay;
      }
      repaint();
    }


    /**
     * Overrides the Component paint() method
     * @param g Graphics
     */
    public void paint(Graphics g) {
        super.paint(g);
        // Set up the constants
        // Get the display rectangle
        r = g.getClipBounds();
        int rw2 = r.width / 2;
        int rh2 = r.height / 2;
        // make the display fill most of the
        // available area
        xscale = 7 * r.width / 8;
        yscale = 7 * r.height / 8;
        // pan to the middle of the display area
        int panUD = -yscale / 2;
        int panLR = -xscale / 2;

        rw2panLR = rw2 + panLR;
        rh2panUD = rh2 + panUD;

        if (theGraph != null  || theDelaunay != null) {

          displayStaticContent(g);
          displayData(g);

          // draw the network graph
          int vc = 0;
          int xo = 0;
          int yo = 0;
          double[] pos;
          double[] from;
          double[] to;
          int xm, ym, xn, yn;
          Vertex overtex = null;
          Vertex vertex;

          // Draw the network Induced Delaunay Triangulation if required
          if ((new Boolean((String)parameters.get("DISPLAY_INDUCED_DELAUNAY_EDGES"))).booleanValue()) {

            for (Iterator i = theDelaunay.getSubGraphs(); i.hasNext(); vc++) {
              Graph subgraph = (Graph) i.next();
              g.setColor(delaunay_edges_color);

              Iterator vertexIterator = subgraph.getAllVertices();
              if (vertexIterator != null) {
                for ( ; vertexIterator.hasNext(); ) {
                  vertex = (Vertex)vertexIterator.next();
                  // Draw the network GNGEdge objects
                  Iterator edgeIterator = subgraph.getNeighbours(vertex);
                  if (edgeIterator != null) {
                    for ( ; edgeIterator.hasNext(); ) {
                      overtex = (Vertex)edgeIterator.next();
                      from = vertex.getPosition();
                      to = overtex.getPosition();
                      xm = (int)(from[X0] * xscale) + rw2panLR;
                      ym = (int)(from[X1] * yscale) + rh2panUD;
                      xn = (int)(to[X0] * xscale) + rw2panLR;
                      yn = (int)(to[X1] * yscale) + rh2panUD;
                      g.drawLine(xm, r.height - ym, xn, r.height - yn);
                    }
                  }
                }
              }

            }

          }



          for (Iterator i = theGraph.getSubGraphs(); i.hasNext(); vc++) {
            Graph subgraph = (Graph)i.next();
            if (COLOR) {
              g.setColor(color.getColor(vc));
            } else {
              g.setColor(nodes);
            }

            // Draw the network GNGEdges if required
            if (new Boolean((String)parameters.get("DISPLAY_NETWORK_EDGES")).booleanValue()) {

              Iterator vertexIterator = subgraph.getAllVertices();
              if (vertexIterator != null) {
                for ( ; vertexIterator.hasNext(); ) {
                  vertex = (Vertex)vertexIterator.next();
                  // Draw the network GNGEdge objects
                  Iterator edgeIterator = subgraph.getNeighbours(vertex);
                  if (edgeIterator != null) {
                    for ( ; edgeIterator.hasNext(); ) {
                      overtex = (Vertex)edgeIterator.next();
                      from = vertex.getPosition();
                      to = overtex.getPosition();
                      xm = (int)(from[X0] * xscale) + rw2panLR;
                      ym = (int)(from[X1] * yscale) + rh2panUD;
                      xn = (int)(to[X0] * xscale) + rw2panLR;
                      yn = (int)(to[X1] * yscale) + rh2panUD;
                      g.drawLine(xm, r.height - ym, xn, r.height - yn);
                    }
                  }
                }
              }

            }

            Iterator vertexIterator = subgraph.getAllVertices();
              if (vertexIterator != null) {
                for ( ; vertexIterator.hasNext(); ) {
                  vertex = (Vertex)vertexIterator.next();
                  pos = vertex.getPosition();
                  xo = (int)(pos[X0] * xscale) + rw2panLR;
                  yo = (int)(pos[X1] * yscale) + rh2panUD;
                  displayVertex(g, xo, yo, vertex.getSelected());
                }
              }
            }
        } else { // the graph is null, so just display the data and axis
          displayStaticContent(g);
          displayData(g);
        }
    }


    /**
     * Helper method to display a network vertex
     * @param g Graphics
     * @param x int
     * @param y int
     * @param selected boolean
     */
    final private void displayVertex(Graphics g, int x, int y, boolean selected) {

      final Color color = g.getColor();
      final Color color_lite = color.brighter();
      final Color color_dark = color.darker();
      int outRadius;

      // Graduate the fill based on selection state
      if (selected) {
        outRadius = 14;
        g.setColor(color_dark);
        g.fillOval(x - outRadius / 2, r.height - y - outRadius / 2, outRadius, outRadius);
        g.setColor(color);
        g.fillOval(x - (outRadius-4) / 2, r.height - y - (outRadius-4) / 2, outRadius-4, outRadius-4);
        g.setColor(color_lite);
        g.fillOval(x - (outRadius-8) / 2, r.height - y - (outRadius-8) / 2, outRadius-8, outRadius-8);
      } else {
        outRadius = 8;
        g.setColor(color_dark);
        g.fillOval(x - outRadius / 2, r.height - y - outRadius / 2, outRadius, outRadius);
        g.setColor(color);
        g.fillOval(x - (outRadius-4) / 2, r.height - y - (outRadius-4) / 2, outRadius-4, outRadius-4);
      }

      //restore color
      g.setColor(color);
    }


    /**
     * Helper method to display the garph axis
     * @param g Graphics
     */
    private void displayStaticContent(Graphics g) {
      // Output axis
      g.setColor(axis);
      g.drawLine(rw2panLR, r.height - rh2panUD,
                 rw2panLR, r.height - (yscale + rh2panUD));
      g.drawLine(rw2panLR, r.height - rh2panUD,
                 xscale + rw2panLR, r.height - rh2panUD);
      g.setColor(text);
      g.drawString("X0", xscale + rw2panLR - charsize / 2 - 6,
                         r.height - rh2panUD + charsize);
      g.drawString("X1", rw2panLR - charsize - 6,
                         r.height - (yscale + rh2panUD) + charsize);
    }


    /**
     * Helper method to display the input data
     * @param g Graphics
     */
    private void displayData(Graphics g){
      final int radius = 15;
      for (int v = 0; v < thedata.length; v++) {
        final double[] pos = thedata[v].getPosition();
        if (thedata[v].getSelected()) {
          g.setColor(data_selected);
          final int x_origin = (int)(pos[X0] * xscale) + rw2panLR;
          final int y_origin = r.height - ((int)(pos[X1] * yscale) + rh2panUD);
          g.drawLine(x_origin-radius, y_origin, x_origin+radius, y_origin);
          g.drawLine(x_origin, y_origin-radius, x_origin, y_origin+radius);
          g.drawOval(x_origin-radius,y_origin-radius,2*radius,2*radius);
        } else {
          g.setColor(data);
          g.fillOval((int)(pos[X0] * xscale) + rw2panLR,
                     r.height - ((int)(pos[X1] * yscale) + rh2panUD), 1, 1);
        }
      }
    }

}

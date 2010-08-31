package networks;

import java.util.*;
import structures.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: A Growing Cell Structures Graph</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 * @version 1.1 23 November 2007 Added <Generics> to silence compiler > 1.5 bitching about unsafes and casts
 */

public class GCSGraph extends Graph {
    /** This GCS Graph is constructed of Simplices */
    Vector<GCSSimplex> simplices = new Vector<GCSSimplex>();

    /** No parameter Constructor */
    public GCSGraph() {
        super();
    }

    /**
     * Get an iterator of the Simplices of this GCS Graph
     * @return Iterator
     */
    public Iterator getSimplices() {
        return simplices.iterator();
    }

    /**
     * Add a new Simplex to this GCS Graph
     * @param s GCSSimplex
     */
    public void add(GCSSimplex s) {
        simplices.add(s);
        if (application.Launcher.DEBUG) {
            System.out.print("\n\nAdding Simplex(" + s.toString() + ")\n");
        }
        // Extract the vertices from the simplex and add to this graph
        GCSVertex[] verts = s.getVertices();
        int index = 0;
        for (int i = 0; i < verts.length; i++) {
            GCSVertex vertex = verts[i];
            if (!this.verts_.contains(vertex)) {
                if (application.Launcher.DEBUG) {
                    System.out.print(" inserted " + vertex.toString() + "\n");
                }
                this.addVertex(vertex);
            } else {
                if (application.Launcher.DEBUG) {
                    System.out.print(" insertion failed " + vertex.toString() + "\n");
                }
            }
        }
        // Now look for the edges between the vertices of the simplex.
        // If it does not exist in this graph then create the edge.
        for (int i = 0; i < s.getDimension() - 1; i++) {
            for (int j = i + 1; j < s.getDimension(); j++) {
                if (!this.areConnected(verts[i], verts[j])) {
                    GCSEdge edge = new GCSEdge(verts[i], verts[j]);
                    if (application.Launcher.DEBUG) {
                        System.out.print(" inserted " + edge.toString() + "\n");
                    }
                    this.addEdge(edge);
                } else {
                    if (application.Launcher.DEBUG) {
                        System.out.print(" insertion failed " + connection(verts[i], verts[j]).toString() + "\n");
                    }
                }
            }
        }
    }

    /**
     * Remove a Simplex from this GCS Graph
     * @param s GCSSimplex
     */
    public void remove(GCSSimplex s) {
        Vertex v1;
        Vertex v2;
        Vector<Edge> edgesToDelete = new Vector<Edge>();
        Vector<Vertex> verticesToDelete = new Vector<Vertex>();
        if (application.Launcher.DEBUG) {
            System.out.print("\n\nRemoving Simplex(" + s.toString() + ")\n");
            System.out.print("super() is " + super.toString() + ")\n");
        }
        Vector<GCSSimplex> simplexBar = new Vector<GCSSimplex>();
        // generate the complementary set of simplex and store
        // in simplexBar
        if (application.Launcher.DEBUG) {
            System.out.print("Extracting complementary set of simplex objects...\n");
        }
        for (Iterator i = this.getSimplices(); i.hasNext(); ) {
            GCSSimplex simplex = (GCSSimplex)i.next();
            if (application.Launcher.DEBUG) {
                System.out.print("  Processing Simplex: " + simplex.toString() + ")\n");
            }
            if (simplex != s) {
                simplexBar.add(simplex);
            }
        }
        if (application.Launcher.DEBUG) {
            System.out.print("Complementary set is: " + simplexBar.toString() + ")\n");
        }
        // Do not try to delete the only simplex!!!
        if (!simplexBar.isEmpty()) {
            GCSVertex[] verts = s.getVertices();
            // Iterate of the edges of the simplex to
            // be deleted
            for (int i = 0; i < s.getDimension() - 1; i++) {
                for (int j = i + 1; j < s.getDimension(); j++) {
                    GCSEdge edge = (GCSEdge)this.connection(verts[i], verts[j]);
                    if (application.Launcher.DEBUG) {
                        System.out.print("  Processing Edge: " + edge.toString() + "\n");
                    }
                    boolean found = false;
                    // Iterate over the exclusive set of simplex objects
                    // checking each for the existance of the same edge
                    for (Iterator jbar = simplexBar.iterator(); jbar.hasNext(); ) {
                        GCSSimplex simplex = (GCSSimplex)jbar.next();
                        if (application.Launcher.DEBUG) {
                            System.out.print("    against simplex: " + simplex.toString() + "\n");
                        }
                        GCSVertex[] simplexverts = simplex.getVertices();
                        for (int isimplex = 0; isimplex < simplexverts.length - 1; isimplex++) {
                            for (int jsimplex = isimplex + 1; jsimplex < simplexverts.length; jsimplex++) {
                                GCSEdge edgesimplex = (GCSEdge)this.connection(simplexverts[isimplex], simplexverts[jsimplex]);
                                if (application.Launcher.DEBUG) {
                                    System.out.print("      extracting edge: " + edgesimplex.toString() + "\n");
                                }
                                if (edge.equals(edgesimplex)) {
                                    found = true;
                                    if (application.Launcher.DEBUG) {
                                        System.out.print("        same....\n");
                                    }
                                } else {
                                    if (application.Launcher.DEBUG) {
                                        System.out.print("        different....\n");
                                    }
                                }
                            }
                        }
                    }
                    if (!found) {
                        if (application.Launcher.DEBUG) {
                            System.out.print("  Unable to locate edge in another simplex, so let us delete it...\n");
                        }
                        v1 = edge.getOneEnd();
                        v2 = edge.getOtherEnd();
                        if (this.areConnected(v1, v2)) {
                            if (!edgesToDelete.contains(this.connection(v1, v2))) {
                                edgesToDelete.add(this.connection(v1, v2));
                            }
                        }
                        // Now is the time to delete the vertices of this edge if they
                        // are not shared with another Simplex - oh my god - here we go again
                        // with the iterating over the structures...  Will this never end?
                        // 1. for vertex v1 and v2
                        // 2. Go through the complementary set and extract each simplex
                        // 3. Extract the vertices from the simplex
                        // 4. If none of the vertices match v1 or v2
                        //      delete vertex v1 or v2
                        //    else
                        //      do nothing
                        // Process v1
                        if (application.Launcher.DEBUG) {
                            System.out.print("Checking for unconnected vertex v1: " + v1.toString() + "\n");
                        }
                        boolean v1found = false;
                        for (Iterator jbar = simplexBar.iterator(); jbar.hasNext(); ) {
                            GCSSimplex simplex = (GCSSimplex)jbar.next();
                            if (application.Launcher.DEBUG) {
                                System.out.print("  Processsing simplex: " + simplex.toString() + "\n");
                            }
                            GCSVertex[] simplexverts = simplex.getVertices();
                            for (int isimplex = 0; isimplex < simplexverts.length; isimplex++) {
                                if (application.Launcher.DEBUG) {
                                    System.out.print("    vertex: " + simplexverts[isimplex].toString() + "\n");
                                }
                                if (simplexverts[isimplex].equals(v1)) {
                                    v1found = true;
                                    if (application.Launcher.DEBUG) {
                                        System.out.print("        found....\n");
                                    }
                                } else {
                                    if (application.Launcher.DEBUG) {
                                        System.out.print("        not found....\n");
                                    }
                                }
                            }
                        }
                        if (!v1found) {
                            if (application.Launcher.DEBUG) {
                                System.out.print("deleting v1 " + v1.toString() + "\n");
                            }
                            if (!verticesToDelete.contains(v1)) {
                                verticesToDelete.add(v1);
                            }
                        } else {
                            if (application.Launcher.DEBUG) {
                                System.out.print("not deleting v1 " + v1.toString() + "\n");
                            }
                        }
                        // Process v2 - ugh
                        // put it in a 2 element array and loop!! lazy dog
                        if (application.Launcher.DEBUG) {
                            System.out.print("Checking for unconnected vertex v2: " + v2.toString() + "\n");
                        }
                        boolean vert2found = false;
                        for (Iterator jbar = simplexBar.iterator(); jbar.hasNext(); ) {
                            GCSSimplex simplex = (GCSSimplex)jbar.next();
                            if (application.Launcher.DEBUG) {
                                System.out.print("  Processsing simplex: " + simplex.toString() + "\n");
                            }
                            GCSVertex[] simplexverts = simplex.getVertices();
                            for (int isimplex = 0; isimplex < simplexverts.length; isimplex++) {
                                if (application.Launcher.DEBUG) {
                                    System.out.print("    vertex: " + simplexverts[isimplex].toString() + "\n");
                                }
                                if (simplexverts[isimplex].equals(v2)) {
                                    vert2found = true;
                                    if (application.Launcher.DEBUG) {
                                        System.out.print("        same....\n");
                                    }
                                } else {
                                    if (application.Launcher.DEBUG) {
                                        System.out.print("        different....\n");
                                    }
                                }
                            }
                        }
                        if (!vert2found) {
                            if (application.Launcher.DEBUG) {
                                System.out.print("deleting v2 " + v2.toString() + "\n");
                            }
                            if (!verticesToDelete.contains(v2)) {
                                verticesToDelete.add(v2);
                            }
                        } else {
                            if (application.Launcher.DEBUG) {
                                System.out.print("not deleting v2 " + v2.toString() + "\n");
                            }
                        }
                    } else {
                        if (application.Launcher.DEBUG) {
                            System.out.print("  edge is shared by another simplex.  Do not delete it...\n");
                        }
                    }
                }
            }
            // now loop over the elements of the ...ToDelete containers
            // and delete the graph elements
            for (Iterator i = edgesToDelete.iterator(); i.hasNext(); ) {
                this.deleteEdge((Edge)i.next());
            }
            for (Iterator i = verticesToDelete.iterator(); i.hasNext(); ) {
                this.deleteVertex((Vertex)i.next());
            }
            simplices.remove(s);
        }
    }

    /**
     * toString() overrides the Object protected method
     * @return a String describing the Graph by sub-Graph grouping
     */
    public String toString() {
        StringBuffer s = new StringBuffer(256);
        s.append("Simplices: ");
        for (Iterator i = this.getSimplices(); i.hasNext(); ) {
            s.append(i.next().toString());
            if (i.hasNext()) s.append(",");
        }
        for (Iterator graphs = getSubGraphs(); graphs.hasNext(); ) {
            final Graph graph = (Graph)graphs.next();
            s.append("; Vertices: ");
            for (Iterator i = graph.getAllVertices(); i.hasNext(); ) {
                s.append(((Vertex)i.next()).toString());
                if (i.hasNext()) s.append(",");
            }
            s.append("; Edges: ");
            for (Iterator i = graph.getAllEdges(); i.hasNext(); ) {
                s.append(((Edge)i.next()).toString());
                if (i.hasNext()) s.append(",");
            }
        }
        return s.toString();
    }
}

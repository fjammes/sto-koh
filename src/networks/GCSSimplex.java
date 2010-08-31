package networks;

import java.util.*;

/**
 * <p>Title: Growing Cell Structures</p>
 * <p>Description: A simplex structure for GCS</p>
 * <p>Copyright: Copyright (c) 2004-2005</p>
 * <p>Company: University of Hertfordshire</p>
 * @author K.A.J.Doherty@herts.ac.uk
 * @version 1.0 16 March 2004 first release
 * @version 1.1 8 February 2005 Reworked to incorporate edges
 * @version 1.1 23 November 2007 Added <Generics> to silence compiler > 1.5 bitching about unsafes and casts
 */
public class GCSSimplex {
    /** Simplex Dimension */
    private int dimension;

    /** Vertex container */
    private Vector<GCSVertex> vertices;

    /**
     * Generate a 2 dimensional Triangular Simplex entity
     * @param a Vertex
     * @param b Vertex
     * @param c Vertex
     */
    public GCSSimplex(final GCSVertex a, final GCSVertex b, final GCSVertex c) {
        dimension = 3;
        vertices = new Vector<GCSVertex>(dimension);
        if ((a != null) && (b != null) && (c != null)) {
            vertices.add(a);
            vertices.add(b);
            vertices.add(c);
        } else {
            throw new RuntimeException("Simplex constructor\n");
        }
    }

    /**
     * Overrides th Object toString() method
     * @return String Description of this Simplex
     */
    public String toString() {
        StringBuffer s = new StringBuffer(64);
        s.append("Simplex: ");
        GCSVertex[] verts = this.getVertices();
        for (int i = 0; i < verts.length; i++) {
            s.append(((GCSVertex)verts[i]).toString());
            if (i < verts.length - 1) s.append(" ,");
        }
        return s.toString();
    }

    /**
     * Method to determine if this Simplex object consists of the GCSVertex a
     * @param vertex Vertex
     * @return boolean
     */
    final public boolean contains(final GCSVertex vertex) {
        return (vertices.contains(vertex));
    }

    /**
     * Get the GCSVertex members of this simplex
     * @return GCSVertex [] of all GCSVertex members of this Simplex
     */
    final public GCSVertex[] getVertices() {
        GCSVertex[] array = new GCSVertex[vertices.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = (GCSVertex)vertices.elementAt(i);
        }
        return array;
    }

    /**
     * Get this Simplex dimensionality
     * @return int
     */
    final public int getDimension() {
        return dimension;
    }
}

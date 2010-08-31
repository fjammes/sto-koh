package networks;

import structures.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: Error store - essentially a hashmap entry of vertex and error</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public class Entry implements Comparable {
    /** The Vertex */
    private Vertex vertex;

    /** The error associated with this Vertex */
    private double error;

    /**
     * Construct an Vertex/error pair in this Entry
     * @param v Vertex
     * @param e double
     */
    public Entry(final Vertex v, final double e) {
        vertex = v;
        error = e;
    }

    /**
     * Determine whether this Entry is for the parameter Vertex
     * @param v Vertex
     * @return boolean Returns true if this Entry is for the parameter Vertex
     */
    final public boolean containsVertex(final Vertex v) {
        return (vertex == v);
    }

    /**
     * Get the Vertex associated with this Entry
     * @return Vertex Returns the Vertex associated with this Entry
     */
    final public Vertex getVertex() {
        return vertex;
    }

    /**
     * Get the Error associated with this Entry
     * @return double Returns the error associated with this Entry
     */
    final public double getError() {
        return error;
    }

    /**
     * Sets the error associated with this Entry
     * @param e double
     */
    final public void setError(final double e) {
        error = e;
    }

    /**
     * Implement the Comparable Interface
     * @param anotherEntry - the Entry Object to compare to this Entry
     * @return int 0 if Entry Objects are equal, 1 if this Entry is greater that
     * anotherEntry, and -1 if this Entry is less than anotherEntry
     */
    public int compareTo(final Object anotherEntry) throws ClassCastException {
        if (!(anotherEntry instanceof Entry)) {
            throw new ClassCastException("An Entry object expected.");
        }
        double d = this.getError() - ((Entry)anotherEntry).getError();
        if (d > 0.0) {
            return 1;
        } else if (d < 0.0) {
            return -1;
        } else {
            return 0;
        }
    }
}

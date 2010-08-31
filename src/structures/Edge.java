package structures;


/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: A suite of Competitve Learning Algorithms, all of which
 * are constructred with a graph - some of which have edges.  So extend
 * this abstract class, and do it.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public abstract class Edge implements Selectable, Cloneable {
    /** One end */
    private Vertex v1;

    /** Other end */
    private Vertex v2;

    /** Edge is selectable */
    protected boolean selected = false;

    /**
     * Determine the selected state of this Edge
     * @return boolean Selected state
     */
    public boolean getSelected() { return selected; }

    /**
     * Set the selected state of this Edge
     * @param value boolean Set the Selected state
     */
    public void setSelected(boolean value) { selected = value; }

    /**
     * <p>Grow the Hebbian Learned Topology between nodes 'from' and 'to', and set the age to 0</p>
     * @param from The from Vertex
     * @param to The to Vertex
     */
    public Edge(Vertex from, Vertex to) {
        v1 = from;
        v2 = to;
    }

    /**
     * Determines whether this Edge contains a vertex
     * @param query Vertex
     * @return boolean Returns true if this Edge contains the Vertex, false otherwise
     */
    public boolean contains(Vertex query) {
        return ((v1 == query) || (v2 == query));
    }

    /**
     * Get any old end
     * @return Vertex one end of this edge
     */
    public Vertex getOneEnd() {
        return v1;
    }

    /**
     * Get any old end
     * @return Vertex other end of this edge
     */
    public Vertex getOtherEnd() {
        return v2;
    }

    /**
     * Get the vertex at the other end of this Edge given a vertex
     * @param vertex Vertex
     * @return Vertex The Vertex at the other end of this Edge
     */
    public Vertex otherEnd(Vertex vertex) {
        if (vertex == v1) {
            return v2;
        } else if (vertex == v2) {
            return v1;
        } else {
            return null;
        }
    }

    /**
     * Overrides the Object toString() method to provide a useful String decriptor of this Edge
     * @return String A description of this Edge
     */
    public String toString() {
        String s = "";
        s += "e(" + v1 + "," + v2 + ")";
        return s;
    }

    /**
     * Overrides the Object clone() method to provide a clone of this edge
     * @return Object
     */
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new Error("This should not occur since we implement Cloneable");
        }
        ((Edge)clone).v1 = (Vertex)v1.clone();
        ((Edge)clone).v2 = (Vertex)v2.clone();
        ((Edge)clone).selected = getSelected();
        return clone;
    }
}

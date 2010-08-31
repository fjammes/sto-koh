package networks;

import structures.*;

/**
 * <p>Title: Growing Neural Gas</p>
 * <p>Description: The Edge for the Growing Neural Gas algorithm.  This class
 * is an edge with an age variable added, with getter and setter methods.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (e-mail K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */
public class GNGEdge extends Edge implements Cloneable {
    /**
     * Constructor
     * @param from Vertex
     * @param to Vertex
     */
    public GNGEdge(Vertex from, Vertex to) {
        super(from, to);
        age = 0;
    }

    /** The age of this edge */
    private int age;

    /** Increment the age of this edge */
    synchronized public void incrementAge() {
        this.age++;
    }

    /** Reset the age of this edge to zero */
    synchronized public void resetAge() {
        this.age = 0;
    }

    /** @return The age of this edge */
    public int getAge() {
        return this.age;
    }

    public Object clone() {
        Object clone = super.clone();
        ((GNGEdge)clone).age = getAge();
        return clone;
    }
}

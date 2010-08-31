package networks;

import structures.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: A SOM Vertex for the graph</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public class SOMVertex extends Vertex {

    /**
     * The error of this SOMVertex
     */
    protected double error;

    /**
     * Constructor for this SOMVertex
     * @param position Where to construct this SOMVertex
     * @param label The lable of this Vertex
     */
    public SOMVertex(final double[] position, final String label) {
        super(position, label);
        error = 0.0d;
    }

    /**
     * @param error Sets the local error
     */
    public void setError(final double error) {
        this.error = error;
    }

    /**
     * @return double The error of this SOMVertex
     */
    public double getError() {
        return this.error;
    }

}

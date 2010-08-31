package networks;

import structures.*;

/**
 * <p>Title: Growing Neural Gas</p>
 * <p>Description: The Vertex for the Growing Neural Gas Algorithm.  Simply
 * incoporates an error variable, with getter and setter methods.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (e-mail K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */
public class GNGVertex extends Vertex {
    /** The error of this GNGVertex */
    protected double error;

    /**
     * Constructor for this GNGVertex
     * @param position Where to construct this GNGVertex
     * @param label The lable of this Vertex
     */
    public GNGVertex(final double[] position, final String label) {
        super(position, label);
        error = 0.0d;
    }

    /** @param error Sets the local error */
    public void setError(final double error) {
        this.error = error;
    }

    /** @return The error of this GNGVertex */
    public double getError() {
        return this.error;
    }
}

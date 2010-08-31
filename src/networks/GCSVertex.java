package networks;

import structures.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: A Growing Cell Structures Vertex</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public class GCSVertex extends Vertex {
    /** The error of this GCSVertex */
    protected double error;

    /** The frequency counter */
    protected double signalCounter;

    public void setSignalCounter(double value) {
        signalCounter = value;
    }

    public double getSignalCounter() {
        return signalCounter;
    }

    /**
     * Constructor for this GCSVertex
     * @param position Where to construct this GNGVertex
     * @param label The lable of this Vertex
     */
    public GCSVertex(final double[] position, final String label) {
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

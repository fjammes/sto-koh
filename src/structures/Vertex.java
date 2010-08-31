package structures;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: A simple Traversable and Selectable abstract Vertex for
 * use in the network.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (e-mail K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */
public class Vertex implements Traversable, Selectable, Cloneable {
    /** @param position Where to construct this Vertex */
    public Vertex(final double[] position, final String label) {
        this.setPosition(position);
        this.setLabel(label);
    }

    /** The weight vector */
    protected double[] pos;

    /** The label */
    protected String label;

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
        ((Vertex)clone).setPosition((double[]) pos.clone());
        ((Vertex)clone).setLabel(label.toString());
        return clone;
    }

    /**
     * Set the label
     * @param label String
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the label
     * @return String
     */
    public String getLabel() {
        return label;
    }

    /**
     * Override the Object toString() method
     * @return String
     */
    public String toString() {
        return "v_" + label;
    }

    /** @param position Where to position this Vertex */
    public void setPosition(final double[] position) {
        this.pos = position;
    }

    /** @return The weight vector of this Vertex */
    public double[] getPosition() {
        return this.pos;
    }

    final static public double[] mid(final double[] a, final double[] b) {
        if (a.length != b.length) {
            System.out.print(" Method: mid(double [] a, double [] b)\n");
            System.out.print("a is: " + a.length + " b is: " + b.length + "\n");
            System.exit(-2);
        }
        double[] d = new double[a.length];
        for (int n = 0; n < a.length; n++) {
            d[n] = (a[n] + b[n]) / 2.0d;
        }
        return d;
    }

    /**
     * Calculate the L(power) norm between the two double []s
     * @param power double L norm, where 0.0 < power <= Double.MAX_VALUE
     * @param a weight vector 1
     * @param b weight vector 2
     * @return a double indicating the distance
     */
    final static public double Minkowski(final double power, final double[] a, final double[] b) {
        // final declarations
        final double[] apos = a;
        final double[] bpos = b;
        final int adim = apos.length;
        // dynamic declarations
        double d = 0.0d;
        // Euclidean
        if (power == 2.0) {
            double diff = 0.0d;
            for (int n = adim; --n >= 0; ) {
                diff = apos[n] - bpos[n];
                d += diff * diff;
            }
            // Consider Power > 99 as L infinity
        } else if (power >= 99.9d) {
            d = Double.MIN_VALUE;
            double scratch;
            for (int n = adim; --n >= 0; ) {
                scratch = Math.abs(apos[n] - bpos[n]);
                if (scratch > d) {
                    d = scratch;
                }
            }
        }
        else if (power <= 0.0) {
            System.out.print("Unsupported Minkowski power: " + power + "\n");
            System.exit(-5);
        }
        else {
            double diff;
            // Code for standard norms
            for (int n = adim; --n >= 0; ) {
                diff = apos[n] - bpos[n];
                // good old fashioned optimised ABS
                if (diff < 0.0d) {
                    d += Math.pow(-diff, power);
                }
                else {
                    d += Math.pow(diff, power);
                }
            }
            if (d < 0.0) {
                // Kludge as pow returns NaN for -d
                // See Sun Java Pages for discussion
                d = -Math.pow(-d, 1.0d / power);
            }
            else {
                d = Math.pow(d, 1.0d / power);
            }
        }
        return d;
    }

    // implement traversable
    protected boolean visited = false;

    /**
     * Set the visited state of this object
     * @param state true if visited, false otherwise
     */
    public void setVisited(final boolean state) {
        visited = state;
    }

    /**
     * Has this object been visited?
     * @return true if it has been visited, false otherwise
     */
    public boolean getVisited() {
        return visited;
    }

    // Implement Selectable
    protected boolean selected = false;

    /**
     * Is this Object Selected?
     * @return true if selected, false otherwise
     */
    public boolean getSelected() {
        return selected;
    }

    /**
     * Set this object state to selected
     * @param state The value to set the selection state to
     */
    public void setSelected(final boolean state) {
        selected = state;
    }
}

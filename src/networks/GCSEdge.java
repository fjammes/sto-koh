package networks;

import structures.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: Growing Cell Structures Edge</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public class GCSEdge extends Edge {
    /**
     * Constructor
     * @param from Vertex
     * @param to Vertex
     */
    public GCSEdge(GCSVertex from, GCSVertex to) {
        super(from, to);
    }
}

package networks;

import structures.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: A SOM edge for the graph</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public class SOMEdge extends Edge {
    /**
     * Constructor
     * @param from Vertex
     * @param to Vertex
     */
    public SOMEdge(SOMVertex from, SOMVertex to) {
        super(from, to);
    }

}

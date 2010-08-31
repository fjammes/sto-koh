package support;

import java.util.*;
import structures.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: A suite of Competitve Learning Algorithms which
 * produce clusters</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 * @version 1.1 23 November 2007 Added <Generics> to silence compiler > 1.5 bitching about unsafes and casts
 */

public class Cluster {
    protected Vector<Vertex> theCluster;

    public Cluster() {
        theCluster = new Vector<Vertex>();
    }

    public void add(Vertex a) {
        theCluster.add(a);
    }

    public Vertex get(int i) {
        if (i > (this.size() - 1)) {
            return null;
        } else {
            return (Vertex)theCluster.get(i);
        }
    }

    public int size() {
        return theCluster.size();
    }
}

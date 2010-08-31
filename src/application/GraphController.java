package application;

import java.util.*;
import structures.*;
import networks.*;
import ui.*;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: The controller between the algorithm garph and the viewer</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: University of Hertfordshire</p>
 * @author K.A.J.Doherty@herts.ac.uk
 * @version 1.0 Initial release
 * @version 1.1 Error plot update (selgraphics) is no longer called
 *              as a seperate runnable
 * @version 1.2 Removed compiler > 1.5 container unchecked warnings
 */
public class GraphController implements Observer {

    private Algorithm algorithm;
    private DataView dataView;
    private int rate;
    private SelGraphics error;
    private Hashtable<Object,Object> parameters;

    /**
     * Graph controller constructor controlling this graph,
     * with the dataView viewer at a rate update period
     * @param algorithm Algorithm
     * @param view DataView
     * @param updateRate int
     * @param error SelGraphics
     * @param parameters Hashtable
     */
    public GraphController(Algorithm algorithm, DataView view,
                           int updateRate, SelGraphics error,
                           Hashtable<Object,Object> parameters) {
      this.algorithm = algorithm;
      this.dataView = view;
      this.rate = updateRate;
      this.error = error;
      this.parameters = parameters;
    }


    /**
     * the GraphController is an Observer of the Counter class
     * and performs an update on the dataview and the error
     * @param subject Observable
     * @param arg Object
     */
    public void update(Observable subject, Object arg) {
      this.rate = new Integer((String) parameters.get("DISPLAY_UPDATE_RATE")).intValue();
      if ((((Counter)subject).getCounter() % this.rate) == 0) {

        dataView.update( (Graph)algorithm.getGraph(), (Graph)algorithm.getInducedDelaunayTriangulation() );
        error.add(algorithm.getSSE());

      }
    }
}

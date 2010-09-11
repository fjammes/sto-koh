package application;


import java.util.*;

import org.apache.commons.logging.LogFactory;

import structures.*;
import support.*;
import networks.Algorithm;
import networks.AlgorithmCompetitiveLearning;
import networks.AlgorithmCompetitiveLearningDemo;
import networks.AlgorithmGrowingCellStructures;
import networks.AlgorithmGrowingNeuralGas;
import networks.AlgorithmNeuralGas;
import networks.AlgorithmSOM;
import ui.*;
import ui.controller.GraphController;
import ui.controller.UserInterfaceController;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: The main() for Competitive Learning Application</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: University of Hertfordshire</p>
 * @author K.A.J.Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0 First submission
 * @version 1.1 14 June 2005
 * Add new algorithm menu functionality
 *  - add newConfigurationFrame method that nulls all the references out,
 *  - the main work is now in the setup method, where all the
 *    previously set null references are repopulated.  This setup() is
 *    is called from the configurationframe
 *  - the algorithm thread start() is now in a private run() method
 * @version 1.2 Removed compiler > 1.5 container unchecked warnings
 */
public class Ui extends Common {

    // These are the algorithms and
    // algorithm specific bits and bobs
    // Hashtables
    // for the dropdown algorithm text
    private Object [][] o = {
       {"GNG",     "Growing Neural Gas",          "ui.GNG_panel"},
       {"GCS",     "Growing Cell Structues",      "ui.GCS_panel"},
       {"CL",      "Competitive Learning",        "ui.CL_panel"},
       {"CL-DEMO", "Competitive Learning Demo",   "ui.CL_panel"},
       {"SOM",     "Self-Organising Map",         "ui.SOM_panel"},
       {"NG",      "Neural Gas",                  "ui.NG_panel"}
    };

    private Hashtable<Object,Object> dropdown = new Hashtable<Object,Object>();
    // for the algorithm specific configuration panel class
    private Hashtable<Object,Object> panel = new Hashtable<Object,Object>();

    // Use Euclidean distance throughout
    // for the algorith specific configuration panel class
    // final static double Minkowski = 2.0d;

    private GraphController graphController;
    private DataView dataView;
    private UserInterfaceController uiController;
    private SelGraphics errorPlot;


    // main for the application
    public static void main(final String[] args) {
      new Ui(args);
    }


    /**
     * Application Constructor
     * @param args String[]
     */
    Ui(String[] args) {
    	
    	log = LogFactory.getLog(this.getClass());

        // Make the important parameter Hashtable that is passes
        // around everywhere in this application
        loadConfigurationFile();
    	
        // Make the hashtables for the GUI dropdown
        // and associated classes
        makeHashTables();

        // Make a new UI controller
        uiController = new UserInterfaceController(properties);
        uiController.setModel(this);

    }

    
    // Ugly public method to reset the interface,
    // views, algorithm, and then to display
    // a new configuration frame
    public void newConfigurationFrame() {

      // begin reseting the uicontroller
      // by marking it as busy
      uiController.setBusy(true);

      iteration = null;

      dataView = null;
      graphController = null;
      errorPlot = null;

      // reset the uicontroller
      uiController.reset();
      uiController.setBusy(false);

      // create and display the configuration window
      new ConfigurationFrame("Configuration Window", this, properties);
    }



    public void setup() {

      this.loadDataFile();
      this.prepareAlgorithm();
      
      int dataSize = this.algorithm.getInputs().length;
     
      // *********************
      //  MODELS AND OBSERVERS
      // *********************
      // Graph Controller
      // The graph controller is linked to the graph of the algorithm.
      // The graph controller is an Observer of the iteration counter
      // and an update is called every iteration.
      // Register an Input Observer with the graphController
      errorPlot = new SelGraphics("SSE", 1);
      dataView = new DataView(this.algorithm.getInputs(),
                              new Integer((String)properties.get("DIMENSIONALITY")).intValue(),
                              properties);
      graphController = new GraphController(algorithm,
                              dataView,
                              new Integer((String) properties.get("DISPLAY_UPDATE_RATE")).intValue(),
                              errorPlot,
                              properties);

      // Create the User Interface and Controller.
      // The user interface has a progress bar to indicate the progression
      // of the algorithm.  This requires access to the iteration counter.
      
      iteration = algorithm.getIteration();
      
      iteration.addObserver(graphController);
      iteration.addObserver(uiController);

      uiController.configure(dataSize*(new Integer((String)properties.get("EPOCH")).intValue()));

    }


    // helper method to populate the hashtables
    private void makeHashTables() {
      for (int i = 0; i < o.length; i++) {
        dropdown.put(o[i][0], o[i][1]);
        panel.put(o[i][0], o[i][2]);
      }
    }


    /**
     * Getter for the dropdown titles enumeration
     * @return Enumeration
     */
    public Enumeration getDropdownKeys() {
      return dropdown.keys();
    }


    /**
     * Get the dropdown entry for the given key
     * @param key Object
     * @return Object
     */
    public Object getDropdownEntry(Object key) {
      return dropdown.get(key);
    }


    /**
     * Gte the panel class name for the given key
     * @param key Object
     * @return Object
     */
    public Object getPanelEntry(Object key) {
      return panel.get(key);
    }

    /**
     * Get the Input Observer pane
     * @return InputGraphicObserver
     */
    public DataView getDataView() {
        return dataView;
    }


    /**
     * Get the error plot component
     * @return SelGraphics
     */
    public SelGraphics getErrorPlot() {
      return errorPlot;
    }


    /**
     * Get the underlying algorithm
     * @return Algorithm The underlying graph managing algorithm
     */
    public Algorithm getAlgorithm() {
        return algorithm;
    }

}

package application;


import java.util.*;
import structures.*;
import support.*;
import networks.*;
import application.*;
import ui.*;
import ui.SelGraphics;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.Cursor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: The main() for Competitive Learning Application</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: University of Hertfordshire</p>
 * @author K.A.J.Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0 First submission
 * @version 1.1 14 June 2005
 * Add new algorithm menu funcionality
 *  - add newConfigurationFrame method that nulls all the references out,
 *    kills the algorithm thread - all wrapped in an hourglass busy.
 *  - the main work is now in the setup method, where all the
 *    previously set null references are repopulated.  This setup() is
 *    is called from the configurationframe
 *  - the algorithm thread start() is now in a private run() method
 * @version 1.2 Removed compiler > 1.5 container unchecked warnings
 */
public class Launcher {

    public static boolean DEBUG;

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
    // for the algorith specific configuration panel class
    private Hashtable<Object,Object> panel = new Hashtable<Object,Object>();
    // for the algorithm specific parameters
    private Hashtable<Object,Object> parameters = new Hashtable<Object,Object>();

    // Progress bar is driven by this counter
    public Counter iteration;

    // The algorithm
    private Algorithm algorithm;
    private Thread algorithmthread;

    // Use Euclidean distance throughout
    // for the algorith specific configuration panel class
    // final static double Minkowski = 2.0d;

    private GraphController graphController;
    private DataView dataView;
    private UserInterfaceController uiController;
    private SelGraphics errorPlot;


    // main for the application
    public static void main(final String[] args) {
      new Launcher(args);
    }


    /**
     * Application Constructor
     * @param args String[]
     */
    Launcher(String[] args) {

        parseCommandLine(args);

        // Make the hashtables for the GUI dropdown
        // and associated classes
        makeHashTables();

        // Make the important parameter Hashtable that is passes
        // around everywhere in this application
        makeParametersTable(parameters);

        // Set the global debug flag
        DEBUG = new Boolean((String)parameters.get("DEBUG")).booleanValue();

        // Make a new UI controller
        uiController = new UserInterfaceController(parameters);
        uiController.setModel(this);

    }

    public void parseCommandLine(String[] args) {

        // create the command line parser
        CommandLineParser parser = new PosixParser();

        // create the Options
        Options options = new Options();
        options.addOption( "a", "all", false, "do not hide entries starting with ." );
        options.addOption( "A", "almost-all", false, "do not list implied . and .." );
        options.addOption( "b", "escape", false, "print octal escapes for nongraphic "
                + "characters" );
        options.addOption( OptionBuilder.withLongOpt( "block-size" )
                .withDescription( "use SIZE-byte blocks" )
                .hasArg()
                .withArgName("SIZE")
                .create() );
        options.addOption( "B", "ignore-backups", false, "do not list implied entried "
                + "ending with ~");
        options.addOption( "c", false, "with -lt: sort by, and show, ctime (time of last " 
                + "modification of file status information) with "
                + "-l:show ctime and sort by name otherwise: sort "
                + "by ctime" );
        options.addOption( "C", false, "list entries by columns" );

        /* 
         * String[] args = new String[]{ "--block-size=10" };
*/

        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            // validate that block-size has been set
            if( line.hasOption( "block-size" ) ) {
                // print the value of block-size
                System.out.println( line.getOptionValue( "block-size" ) );
            }
        }
        catch( ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        }
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

      // Kill the algorithm thread by STOPping
      // the algorithm, waiting until the
      // algorithm thread is no longer alive,
      // and nulling the references
      if (algorithmthread != null) {
        algorithm.setState(Algorithm.STOP);
        while (algorithmthread.isAlive()) {
          // do nothing
        }
        algorithmthread = null;
        algorithm = null;
      }

      // reset the uicontroller
      uiController.reset();
      uiController.setBusy(false);

      // create and display the configuration window
      new ConfigurationFrame("Configuration Window", this, parameters);
    }



    public void setup() {

      iteration = new Counter();

      boolean normalised = new Boolean((String)parameters.get("NORMALISED")).booleanValue();
      final String preprocessing_mode = normalised?"NORMALISED":"RAW";

      FileOpener theFileOpener =
          new FileOpener((String)parameters.get("FILE"),
                         new Integer((String)parameters.get("DIMENSIONALITY")).intValue(),
                         ((Boolean)parameters.get("LABELLED")).booleanValue(),
                         preprocessing_mode);
      // ****************************
      // EPOCH / ITERATION CONVERSION
      // ****************************
      Vertex [] input = theFileOpener.getData();
      int dataSize = input.length;

      String model = (String) parameters.get("ALGORITHM");
      // GROWING NEURAL GAS
      if (model.equals("GNG")) {
          algorithm = new AlgorithmGrowingNeuralGas(input,
                                  new Integer((String)parameters.get("DIMENSIONALITY")).intValue(),
                                  new Integer((String)parameters.get("EPOCH")).intValue(),
                                  iteration,
                                  new Double((String)parameters.get("MINKOWSKI")).doubleValue(),
                                  new Integer((String)parameters.get("GNG_NODES")).intValue(),
                                  new Double((String)parameters.get("GNG_BMU_LEARNING_RATE")).doubleValue(),
                                  new Double((String)parameters.get("GNG_NEIGHBOUR_LEARNING_RATE")).doubleValue(),
                                  new Integer((String)parameters.get("GNG_NODE_INSERTION")).intValue(),
                                  new Integer((String)parameters.get("GNG_EDGE_DELETION")).intValue());
      // Neural Gas
      } else if (model.equals("NG")) {
          algorithm = new AlgorithmNeuralGas(input,
                                  new Integer((String)parameters.get("DIMENSIONALITY")).intValue(),
                                  new Integer((String)parameters.get("EPOCH")).intValue(),
                                  iteration,
                                  new Double((String)parameters.get("MINKOWSKI")).doubleValue(),
                                  new Integer((String)parameters.get("NG_NODES")).intValue(),
                                  new Double((String)parameters.get("NG_NEIGHBOURHOOD_INITIAL")).doubleValue(),
                                  new Double((String)parameters.get("NG_NEIGHBOURHOOD_FINAL")).doubleValue(),
                                  new Double((String)parameters.get("NG_LEARNING_INITIAL")).doubleValue(),
                                  new Double((String)parameters.get("NG_LEARNING_FINAL")).doubleValue());
      // Competitive Learning (WTA)
      } else if (model.equals("CL")) {
          algorithm = new AlgorithmCompetitiveLearning(input,
                                  new Integer((String)parameters.get("DIMENSIONALITY")).intValue(),
                                  new Integer((String)parameters.get("EPOCH")).intValue(),
                                  iteration,
                                  new Double((String)parameters.get("MINKOWSKI")).doubleValue(),
                                  new Integer((String)parameters.get("CL_NODES")).intValue(),
                                  new Double((String)parameters.get("CL_LEARNING_RATE")).doubleValue());

      } else if (model.equals("CL-DEMO")) {
                algorithm = new AlgorithmCompetitiveLearningDemo(input,
                                  new Integer((String)parameters.get("DIMENSIONALITY")).intValue(),
                                  new Integer((String)parameters.get("EPOCH")).intValue(),
                                  iteration,
                                  new Double((String)parameters.get("MINKOWSKI")).doubleValue(),
                                  new Integer((String)parameters.get("CL_NODES")).intValue(),
                                  new Double((String)parameters.get("CL_LEARNING_RATE")).doubleValue(),
                                  new Integer((String)parameters.get("CL_BETWEEN_STEPS_DELAY")).intValue(),
                                  new Integer((String)parameters.get("CL_AFTER_INPUT_IDENTIFIED_DELAY")).intValue(),
                                  new Integer((String)parameters.get("CL_AFTER_BMU_IDENTIFIED_DELAY")).intValue(),
                                  new Integer((String)parameters.get("CL_AFTER_BMU_MOVED_DELAY")).intValue() );

      // Self-Organising Map
      } else if (model.equals("SOM")) {
          algorithm = new AlgorithmSOM(input,
                                  new Integer((String)parameters.get("DIMENSIONALITY")).intValue(),
                                  new Integer((String)parameters.get("EPOCH")).intValue(),
                                  iteration,
                                  new Double((String)parameters.get("MINKOWSKI")).doubleValue(),
                                  new Integer((String)parameters.get("SOM_ROWS")).intValue(),
                                  new Integer((String)parameters.get("SOM_COLUMNS")).intValue(),
                                  new Double((String)parameters.get("SOM_NEIGHBOURHOOD_INITIAL")).doubleValue(),
                                  new Double((String)parameters.get("SOM_NEIGHBOURHOOD_FINAL")).doubleValue(),
                                  new Double((String)parameters.get("SOM_LEARNING_INITIAL")).doubleValue(),
                                  new Double((String)parameters.get("SOM_LEARNING_FINAL")).doubleValue());
      // Growing Cell Structures
      } else if (model.equals("GCS")) {
          algorithm = new AlgorithmGrowingCellStructures(input,
                                  new Integer((String)parameters.get("DIMENSIONALITY")).intValue(),
                                  new Integer((String)parameters.get("EPOCH")).intValue(),
                                  iteration,
                                  new Double((String)parameters.get("MINKOWSKI")).doubleValue(),
                                  new Integer((String)parameters.get("GCS_NODES")).intValue(),
                                  new Double((String)parameters.get("GCS_BMU_LEARNING_RATE")).doubleValue(),
                                  new Double((String)parameters.get("GCS_NEIGHBOUR_LEARNING_RATE")).doubleValue(),
                                  new Integer((String)parameters.get("GCS_NODE_DELETION")).intValue(),
                                  new Integer((String)parameters.get("GCS_EDGE_INSERTION")).intValue());
      } else {
          throw new RuntimeException("application.Launcher: Unsupported algorithm "+model);
      }
      // *********************
      //  MODELS AND OBSERVERS
      // *********************
      // Graph Controller
      // The graph controller is linked to the graph of the algorithm.
      // The graph controller is an Oserver of the iteration counter
      // and an update is called every iteration.
      // Register an Input Observer with the graphController
      errorPlot = new SelGraphics("SSE", 1);
      dataView = new DataView(input,
                              new Integer((String)parameters.get("DIMENSIONALITY")).intValue(),
                              parameters);
      graphController = new GraphController(algorithm,
                              dataView,
                              new Integer((String) parameters.get("DISPLAY_UPDATE_RATE")).intValue(),
                              errorPlot,
                              parameters);

      // Create the User Interface and Controller.
      // The user interface has a progess bar to indicate the progression
      // of the algorithm.  This requires access to the iteration counter.
      iteration.addObserver(graphController);
      iteration.addObserver(uiController);

      uiController.configure(dataSize*(new Integer((String)parameters.get("EPOCH")).intValue()));

      // Instantiate and run the algorithm thread
      this.run();

    }


    // helper method to populate the hashtables
    private void makeHashTables() {
      for (int i = 0; i < o.length; i++) {
        dropdown.put(o[i][0], o[i][1]);
        panel.put(o[i][0], o[i][2]);
      }
    }


    // helper method to populate the parameters
    // hashtable from the application ini file
    // hardcoded as "defaults.ini"
    private void makeParametersTable(Hashtable<Object,Object> table) {
      final Properties p = new Properties();

      try {
        p.load((java.io.InputStream) new java.io.FileInputStream("defaults.ini"));
      }
      catch (java.io.FileNotFoundException e) {
        e.printStackTrace();
      }
      catch (java.io.IOException e) {
        e.printStackTrace();
      };

      // Iterate over properties, and populate
      // the parameters hashtable
      for (Enumeration e = p.propertyNames(); e.hasMoreElements(); ) {
        final Object o = e.nextElement();
        table.put(o, p.getProperty((String)o));
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
     * Everything has been configured, so now we throw in a run().
     */
    public void run() {
      // Run the competitive learning algorithm in its own thread
      (new Thread(algorithm)).start();
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

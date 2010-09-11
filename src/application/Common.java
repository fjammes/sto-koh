/**
 * 
 */
package application;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import structures.Counter;
import structures.Vertex;
import support.DataFileReader;

import networks.Algorithm;
import networks.AlgorithmCompetitiveLearning;
import networks.AlgorithmCompetitiveLearningDemo;
import networks.AlgorithmGrowingCellStructures;
import networks.AlgorithmGrowingNeuralGas;
import networks.AlgorithmNeuralGas;
import networks.AlgorithmSOM;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: Commons tool for main() of  Competitive Learning Application</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: University Paris 1</p>
 * @author Fabrice Jammes (fabrice.jammes@gmail.com)
 * @version 1.0 First submission
 */
public abstract class Common {
	
    public Log log = LogFactory.getLog(this.getClass());
    
    // for the algorithm specific parameters
    protected Properties properties = new Properties();
    
    // Progress bar is driven by this counter
    public Counter iteration;
    
    // The algorithm
    protected Algorithm algorithm;
    protected Vertex [] inputData;
    protected Thread algorithmThread;
    
    // helper method to populate the parameters
    // hashtable from the application ini file
    // hardcoded as "defaults.ini"
    protected void loadConfigurationFile() {
      try {
        properties.load(new FileInputStream("defaults.ini")); 
      }
      catch (java.io.IOException e) {
        e.printStackTrace();
      };
    }
    
    protected void loadDataFile() {
    
    boolean normalised = new Boolean(properties.getProperty("NORMALISED")).booleanValue();
    final String preprocessing_mode = normalised?"NORMALISED":"RAW";

    DataFileReader fileOpener =
        new DataFileReader(properties.getProperty("FILE"),
                       Integer.valueOf(properties.getProperty("DIMENSIONALITY")).intValue(),
                       Boolean.valueOf(properties.getProperty("LABELLED")).booleanValue(),
                       preprocessing_mode);
    // ****************************
    // EPOCH / ITERATION CONVERSION
    // ****************************
    inputData = fileOpener.getData();
    
    }

    protected void prepareAlgorithm() {
    
    String model = (String) properties.getProperty("ALGORITHM");
    // GROWING NEURAL GAS
    if (model.equals("GNG")) {
        algorithm = new AlgorithmGrowingNeuralGas(inputData,
                                new Integer((String)properties.getProperty("DIMENSIONALITY")).intValue(),
                                new Integer((String)properties.getProperty("EPOCH")).intValue(),
                                new Double((String)properties.getProperty("MINKOWSKI")).doubleValue(),
                                new Integer((String)properties.getProperty("GNG_NODES")).intValue(),
                                new Double((String)properties.getProperty("GNG_BMU_LEARNING_RATE")).doubleValue(),
                                new Double((String)properties.getProperty("GNG_NEIGHBOUR_LEARNING_RATE")).doubleValue(),
                                new Integer((String)properties.getProperty("GNG_NODE_INSERTION")).intValue(),
                                new Integer((String)properties.getProperty("GNG_EDGE_DELETION")).intValue());
    // Neural Gas
    } else if (model.equals("NG")) {
        algorithm = new AlgorithmNeuralGas(inputData,
                                new Integer((String)properties.getProperty("DIMENSIONALITY")).intValue(),
                                new Integer((String)properties.getProperty("EPOCH")).intValue(),
                                new Double((String)properties.getProperty("MINKOWSKI")).doubleValue(),
                                new Integer((String)properties.getProperty("NG_NODES")).intValue(),
                                new Double((String)properties.getProperty("NG_NEIGHBOURHOOD_INITIAL")).doubleValue(),
                                new Double((String)properties.getProperty("NG_NEIGHBOURHOOD_FINAL")).doubleValue(),
                                new Double((String)properties.getProperty("NG_LEARNING_INITIAL")).doubleValue(),
                                new Double((String)properties.getProperty("NG_LEARNING_FINAL")).doubleValue());
    // Competitive Learning (WTA)
    } else if (model.equals("CL")) {
        algorithm = new AlgorithmCompetitiveLearning(inputData,
                                new Integer((String)properties.getProperty("DIMENSIONALITY")).intValue(),
                                new Integer((String)properties.getProperty("EPOCH")).intValue(),
                                new Double((String)properties.getProperty("MINKOWSKI")).doubleValue(),
                                new Integer((String)properties.getProperty("CL_NODES")).intValue(),
                                new Double((String)properties.getProperty("CL_LEARNING_RATE")).doubleValue());

    } else if (model.equals("CL-DEMO")) {
              algorithm = new AlgorithmCompetitiveLearningDemo(inputData,
                                new Integer((String)properties.getProperty("DIMENSIONALITY")).intValue(),
                                new Integer((String)properties.getProperty("EPOCH")).intValue(),
                                new Double((String)properties.getProperty("MINKOWSKI")).doubleValue(),
                                new Integer((String)properties.getProperty("CL_NODES")).intValue(),
                                new Double((String)properties.getProperty("CL_LEARNING_RATE")).doubleValue(),
                                new Integer((String)properties.getProperty("CL_BETWEEN_STEPS_DELAY")).intValue(),
                                new Integer((String)properties.getProperty("CL_AFTER_INPUT_IDENTIFIED_DELAY")).intValue(),
                                new Integer((String)properties.getProperty("CL_AFTER_BMU_IDENTIFIED_DELAY")).intValue(),
                                new Integer((String)properties.getProperty("CL_AFTER_BMU_MOVED_DELAY")).intValue() );

    // Self-Organising Map
    } else if (model.equals("SOM")) {
        algorithm = new AlgorithmSOM(inputData,
                                new Integer((String)properties.getProperty("DIMENSIONALITY")).intValue(),
                                new Integer((String)properties.getProperty("EPOCH")).intValue(),
                                new Double((String)properties.getProperty("MINKOWSKI")).doubleValue(),
                                new Integer((String)properties.getProperty("SOM_ROWS")).intValue(),
                                new Integer((String)properties.getProperty("SOM_COLUMNS")).intValue(),
                                new Double((String)properties.getProperty("SOM_NEIGHBOURHOOD_INITIAL")).doubleValue(),
                                new Double((String)properties.getProperty("SOM_NEIGHBOURHOOD_FINAL")).doubleValue(),
                                new Double((String)properties.getProperty("SOM_LEARNING_INITIAL")).doubleValue(),
                                new Double((String)properties.getProperty("SOM_LEARNING_FINAL")).doubleValue());
    // Growing Cell Structures
    } else if (model.equals("GCS")) {
        algorithm = new AlgorithmGrowingCellStructures(inputData,
                                new Integer((String)properties.getProperty("DIMENSIONALITY")).intValue(),
                                new Integer((String)properties.getProperty("EPOCH")).intValue(),
                                new Double((String)properties.getProperty("MINKOWSKI")).doubleValue(),
                                new Integer((String)properties.getProperty("GCS_NODES")).intValue(),
                                new Double((String)properties.getProperty("GCS_BMU_LEARNING_RATE")).doubleValue(),
                                new Double((String)properties.getProperty("GCS_NEIGHBOUR_LEARNING_RATE")).doubleValue(),
                                new Integer((String)properties.getProperty("GCS_NODE_DELETION")).intValue(),
                                new Integer((String)properties.getProperty("GCS_EDGE_INSERTION")).intValue());
    } else {
        throw new RuntimeException("application.Launcher: Unsupported algorithm "+model);
    }
    }
}

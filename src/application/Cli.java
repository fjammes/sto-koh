package application;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.apache.commons.logging.LogFactory;


public class Cli extends Common {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Cli cli = new Cli();
		cli.initialize(args);
		cli.algorithm.run();
	}
    
    private void initialize(String[] args) {
    	
    	log = LogFactory.getLog(this.getClass());
    	
        // Make the important parameter Hashtable that is passes
        // around everywhere in this application
        loadConfigurationFile();

        parseCommandLine(args);
        
        this.loadDataFile();
        this.prepareAlgorithm();
    }
	
	public void parseCommandLine(String[] args) {

        // create the command line parser
        CommandLineParser parser = new PosixParser();

        // create the Options
        Options options = new Options();
        options.addOption( "d", "dimension", true, "data dimension" );
        options.addOption( "l", "labelled", false, "for labelled data" );

        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            properties.setProperty( "FILE", line.getArgs()[0]);
            log.debug(properties.getProperty( "FILE" ));
            
            properties.setProperty( "ALGORITHM", line.getArgs()[1]);
            log.debug(properties.getProperty( "ALGORITHM" ));
            
            if( line.hasOption( "dimension" ) ) {
            	properties.setProperty( "DIMENSIONALITY", line.getOptionValue( "dimension" ));
            	log.debug(properties.getProperty( "DIMENSIONALITY" ));
            }
            if( line.hasOption( "labelled" ) ) {
            	properties.setProperty( "LABELLED", line.getOptionValue( "labelled" ));
            	log.debug(properties.getProperty( "LABELLED" ));
            }
            
            //TODO : usage function
        }
        catch(ParseException e) {
        	log.debug("Unexpected exception:" + e.getMessage(), e);
        }
    }



}

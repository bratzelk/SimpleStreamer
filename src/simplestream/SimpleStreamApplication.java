package simplestream;

import org.kohsuke.args4j.*;
import common.*;


public class SimpleStreamApplication {

	
	/**
	 * Setup the command line arguments and their defaults
	 */	
   
    @Option(name="-remote",usage="The remote hostname",metaVar = "HOSTNAME")
    private String hostname = Settings.DEFAULT_HOSTNAME;

    @Option(name="-rport",usage="Remote Port Number")
    private int remotePort = Settings.DEFAULT_REMOTE_PORT;
    
    @Option(name="-sport",usage="Streaming Port Number")
    private int streamingPort = Settings.DEFAULT_STREAMING_PORT;
    
    @Option(name="-rate",usage="Streaming Rate")
    private int streamingRate = Settings.DEFAULT_STREAMING_RATE;
 

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new SimpleStreamApplication().runMain(args);
	}
	
	private void runMain(String[] args) {
		
		//read in command line args to variables
		readCommandLineArgs(args);
		
		Out.printHeading(Settings.APP_NAME + " " + Settings.APP_VERSION);

		
		//Show the local image viewer
		LocalView localView = new LocalView();
		localView.start();
	}

	
	
	/**
	 * This will parse command line args and read them into our class variables
	 * 
	 * On error the system will exit.
	 */
	private void readCommandLineArgs(String[] args){
		CmdLineParser parser = new CmdLineParser(this);
		try {
            // parse the arguments.
            parser.parseArgument(args);

        } catch( CmdLineException e ) {
            // if there's a problem in the args
            Out.error(e.getMessage());
            Out.error("Usage is: java -jar SimpleStreamer.jar [-sport X] [-remote hostname [-rport Y]] [-rate Z]");
            parser.printUsage(System.err);
            System.exit(-1);
        }
	}

}

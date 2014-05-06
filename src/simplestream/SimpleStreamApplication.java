package simplestream;

import org.kohsuke.args4j.*;
import common.*;


public class SimpleStreamApplication {

	//This tells us what mode the application is running in (local or remote)
	private Boolean localMode = true;
	
	/**
	 * Setup the command line arguments and their default values
	 */	
    @Option(name="-remote",usage="The remote hostname",metaVar = "HOSTNAME")
    private String hostname = Strings.EMPTY_REMOTE_HOSTNAME; //this is used to set localMode, don't change

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
		
		//set localMode on or off depending on the command line arg, -remote
		setMode();
		
		Out.printHeading(Settings.APP_NAME + " " + Settings.APP_VERSION);

		
		
		//Start the server
		
		if(localMode) {
			//Show the local image viewer
			LocalView localView = new LocalView();
			localView.start();
		}

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
	
	private void setMode() {
		//if the remote flag was not set then we need to make sure that we set local mode on
		if(this.hostname.equals(Strings.EMPTY_REMOTE_HOSTNAME)) {
			setLocalMode();
		}
		else {
			setRemoteMode();
		}
	}
	
	private void setRemoteMode() {
		this.localMode = false;
		Out.print("Running in Remote Mode");
	}
	private void setLocalMode() {
		this.localMode = true;
		Out.print("Running in Local Mode");
	}

}

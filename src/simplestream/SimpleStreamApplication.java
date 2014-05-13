package simplestream;


import org.kohsuke.args4j.*;
import common.*;
import messages.*;


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
	 * @throws MessageNotFoundException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws MessageNotFoundException, InterruptedException {
		
		new SimpleStreamApplication().runMain(args);
	}
	
	private void runMain(String[] args) throws MessageNotFoundException, InterruptedException {
		
		//read in command line args to variables
		readCommandLineArgs(args);
	
		Out.printHeading(Settings.APP_NAME + " " + Settings.APP_VERSION);

		//set localMode on or off depending on the command line arg, -remote
		setMode();
			
		
		
		//TODO: Start the server in a new thread.
		//TODO: when you get a connection, start a new thread and send them messages.
		
		
		//TODO: Listen for user input, when a user presses enter send a shutdown request.
		
		
		//TODO: stream from local camera if in local mode
		if (localMode) {
			
			//TODO: Show the local image viewer.
			//TODO: The StreamViewer currently listens for the enter key. It doesn't do anything when it catches the event yet. This needs to be implemented.
			StreamViewer viewer = new StreamViewer();
			
			Webcam webcam = new Webcam();
			
			
			//TODO: nice exit from this loop.
			while(true) {
				byte[] imageData = webcam.getImage();
				viewer.addImage(imageData);	
				Thread.sleep(streamingRate);
			}
			
		}
		//TODO: else connect to remote host, and send them messages.
		else {
			//This needs to be added to the overloadedMessage.
			Peer remoteServer = new Peer(hostname, remotePort);
			
			
			//TODO: This is an example message
			OverloadedResponseMessage overloadedMessage = (OverloadedResponseMessage)MessageFactory.createMessage(Strings.OVERLOADED_RESPONSE_MESSAGE);
			overloadedMessage.addServer(remoteServer);
			//overloadedMessage.addClients(clients);
			Out.print(overloadedMessage.toJSON());
			
			//TODO: The messages themselves don't compress any byte arrays.
			//You need to do this explicitly before adding the data to a message.
		}
		
				
	}

	
	
	/**
	 * This will parse command line args and read them into our class variables
	 * 
	 * On error the system will exit.
	 */
	private void readCommandLineArgs(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
            //parse the arguments.
            parser.parseArgument(args);
        } catch( CmdLineException e ) {
            //if there's a problem with the args.
            Out.error(e.getMessage());
            Out.error("Usage is: java -jar SimpleStreamer.jar [-sport X] [-remote hostname [-rport Y]] [-rate Z]");
            parser.printUsage(System.err);
            System.exit(-1);
        }
	}
	
	private void setMode() {
		//if the remote flag was not set then we need to make sure that we set local mode to on.
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

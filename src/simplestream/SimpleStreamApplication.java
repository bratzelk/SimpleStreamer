package simplestream;


import java.io.IOException;
import java.net.Socket;

import messages.MessageNotFoundException;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import simplestream.client.RemoteWebcamStreamer;
import simplestream.client.LocalWebcamStreamer;
import au.edu.unimelb.orlade.comp90015.filesync.ConnectionBuffer;
import au.edu.unimelb.orlade.comp90015.filesync.ConnectionBuffer.Response;
import au.edu.unimelb.orlade.comp90015.filesync.server.ConnectionListener;
import au.edu.unimelb.orlade.comp90015.filesync.server.ConnectionListener.Callback;
import common.Out;
import common.Settings;
import common.Strings;


public class SimpleStreamApplication {

	/** Whether the application is running in local (true) or remote (false) mode. */
	private Boolean localMode = true;

	// Setup the command line arguments and their default values

	// this is used to set localMode, don't change
	@Option(name = "-remote", usage = "The remote hostname", metaVar = "HOSTNAME")
	private String hostname = Strings.EMPTY_REMOTE_HOSTNAME;

	@Option(name = "-rport", usage = "Remote Port Number")
	private int remotePort = Settings.DEFAULT_REMOTE_PORT;

	@Option(name = "-sport", usage = "Streaming Port Number")
	private int streamingPort = Settings.DEFAULT_STREAMING_PORT;

	@Option(name = "-rate", usage = "Streaming Rate")
	private int streamingRate = Settings.DEFAULT_STREAMING_RATE;

	private Logger log = Logger.getLogger(getClass());

	/**
	 * @param args
	 * @throws MessageNotFoundException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws MessageNotFoundException, InterruptedException {
		new SimpleStreamApplication().runMain(args);
	}

	private void runMain(String[] args) throws MessageNotFoundException, InterruptedException {
		// read in command line args to variables
		readCommandLineArgs(args);

		Out.printHeading(Settings.APP_NAME + " " + Settings.APP_VERSION);

		// set localMode on or off depending on the command line arg, -remote
		setMode();

		if (localMode) {
			new LocalWebcamStreamer(streamingRate).init();
		} else {
			new RemoteWebcamStreamer(streamingRate, hostname, remotePort).init();
		}


		// TODO: Listen for user input, when a user presses enter send a shutdown request.
	}

	public void stop() throws IOException {
		log.debug("Stopping server...");
		listener.close();
	}

	/**
	 * This will parse command line args and read them into our class variables
	 *
	 * On error the system will exit.
	 */
	private void readCommandLineArgs(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			// parse the arguments.
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			// if there's a problem with the args.
			Out.error(e.getMessage());
			Out.error("Usage is: java -jar SimpleStreamer.jar [-sport X] [-remote hostname [-rport Y]] [-rate Z]");
			parser.printUsage(System.err);
			System.exit(-1);
		}
	}

	private void setMode() {
		// if the remote flag was not set then we need to make sure that we set local mode to on.
		if (this.hostname.equals(Strings.EMPTY_REMOTE_HOSTNAME)) {
			setLocalMode();
		} else {
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

package simplestream.app;


import java.io.IOException;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import simplestream.Webcam;
import simplestream.client.LocalWebcamStreamer;
import simplestream.client.StreamClient;
import simplestream.server.StreamServer;
import common.Out;
import common.Settings;
import common.Strings;


public class SimpleStreamApplication {

	private Logger log = Logger.getLogger(getClass());

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

	private StreamServer server;
	private StreamClient client;

	/**
	 * @param args
	 * @throws MessageNotFoundException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) {
		SimpleStreamApplication instance = new SimpleStreamApplication();
		instance.readCommandLineArgs(args);
		instance.init();
		// TODO: Listen for user input, when a user presses enter send a shutdown request.
	}

	/**
	 * Sets up the client and server components. All command line arguments should have been parsed.
	 */
	public void init() {
		Out.printHeading(Settings.APP_NAME + " " + Settings.APP_VERSION);

		// Create a shared streamer for the local webcam.
		LocalWebcamStreamer streamer = new LocalWebcamStreamer(new Webcam(), streamingRate);

		log.debug("Creating server");
		server = new StreamServer(streamer, streamingRate, streamingPort);

		log.debug("Creating client");
		client = new StreamClient(streamer, streamingRate);

		if (isLocal()) {
			log.debug("Initiating local webcam stream...");
			client.switchToLocal();
		} else {
			log.debug("Initiating remote webcam stream...");
			client.switchToRemote(hostname, remotePort);
		}
	}

	public void stop() throws IOException {
		log.debug("Stopping server...");
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

	/**
	 * The app is considered to be in local mode if the remote flag was not set.
	 */
	private boolean isLocal() {
		return this.hostname.equals(Strings.EMPTY_REMOTE_HOSTNAME);
	}

}

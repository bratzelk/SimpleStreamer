package simplestream.app;


import java.io.IOException;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import simplestream.client.StreamClient;
import simplestream.server.StreamServer;
import simplestream.webcam.LocalWebcam;
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
	 * A callback to invoke to shut down the whole application gracefully, including streaming
	 * connections remote hosts.
	 */
	private final Runnable exitCallback = new Runnable() {
		@Override
		public void run() {
			try {
				stop();
			} catch (IOException e) {
				log.error("Failed to exit cleanly", e);
			}
		}
	};

	/**
	 * @param args
	 * @throws MessageNotFoundException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) {
		SimpleStreamApplication instance = new SimpleStreamApplication();
		instance.readCommandLineArgs(args);
		instance.init();
	}

	/**
	 * Sets up the client and server components. All command line arguments should have been parsed.
	 */
	public void init() {
		Out.printHeading(Settings.APP_NAME + " " + Settings.APP_VERSION);

		// Create a shared streamer for the local webcam.
		LocalWebcam localWebcam = new LocalWebcam();

		log.debug("Creating server...");
		server = new StreamServer(localWebcam, streamingRate, streamingPort);

		log.debug("Creating client...");
		client = new StreamClient(localWebcam, streamingRate, exitCallback);

		if (isLocal()) {
			log.debug("Initiating local webcam stream...");
			client.switchToLocal();
		} else {
			log.debug("Initiating remote webcam stream...");
			client.switchToRemote(hostname, remotePort);
		}
		client.runViewer();
	}

	/**
	 * Stops and cleans up the application's resources.
	 */
	public void stop() throws IOException {
		log.debug("Stopping application...");
		server.kill();
		client.kill();
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

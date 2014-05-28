package simplestream.app;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import simplestream.client.StreamClient;
import simplestream.common.Settings;
import simplestream.common.Strings;
import simplestream.server.StreamServer;
import simplestream.webcam.LocalWebcam;

/**
 * This is the greatest and best app in the world.
 */
public class SimpleStreamApplication {

	private Logger log = Logger.getLogger(getClass());

	/** The hostname of the remote host to connect to. */
	@Option(name = "-remote", usage = "The remote hostname", metaVar = "HOSTNAME")
	private String hostname = Strings.EMPTY_REMOTE_HOSTNAME;

	/** The port on the remote machine to connect to. */
	@Option(name = "-rport", usage = "Remote Port Number")
	private int remotePort = Settings.DEFAULT_REMOTE_PORT;

	/** The port on the local machine to receive connections on. */
	@Option(name = "-sport", usage = "Streaming Port Number")
	private int streamingPort = Settings.DEFAULT_STREAMING_PORT;

	/** The number of ms between remote images being sent. */
	@Option(name = "-rate", usage = "Streaming Rate")
	private int streamingRate = Settings.DEFAULT_STREAMING_RATE;

	/** The local server component that streams out to incoming connections. */
	private StreamServer server;

	/** The local client component that displays whatever images are being streamed. */
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
	 * The main application entry point.
	 *
	 * @param args Command line arguments.
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
		log.debug(Settings.APP_NAME + " " + Settings.APP_VERSION);

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
		client.kill();
		server.kill();
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
			log.error(e);
			log.error("Usage is: java -jar SimpleStreamer.jar "
				+ "[-sport X] [-remote hostname [-rport Y]] [-rate Z]");
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

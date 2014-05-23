package simplestream.client;

import org.apache.log4j.Logger;

import simplestream.StreamViewer;
import simplestream.webcam.LocalWebcam;
import simplestream.webcam.Webcam;


/**
 * Manages the display of the webcam stream in either local or remote modes.
 */
public class StreamClient {

	private final Logger log = Logger.getLogger(getClass());

	private int streamingRate;

	/** The viewer in which to render the local webcam images. */
	private StreamViewer viewer;


	/** The {@link Webcam} on the local machine. */
	private LocalWebcam localWebcam;

	/** The {@link Webcam} currently being used to stream images (local or remote). */
	private Webcam currentWebcam;

	/** The callback to invoke when the client viewer is closed to clean up gracefully. */
	private final Runnable exitCallback;

	/**
	 * Constructs a {@link StreamClient} in the default local mode.
	 */
	public StreamClient(LocalWebcam localWebcam, int streamingRate, Runnable exitCallback) {
		this.exitCallback = exitCallback;
		this.streamingRate = streamingRate;
	}

	/**
	 * Begins receiving webcam data from the local webcam, if not doing so already.
	 */
	public void switchToLocal() {
		log.debug("Switching to Local LocalWebcam");
		localStreamer.start();
	}

	/**
	 * Begins receiving webcam data from a remote host.
	 *
	 * @param hostname The hostname of the remote host.
	 * @param remotePort The connected port on the remote host.
	 */
	public void switchToRemote(String hostname, int remotePort) {
		log.debug("Switching to Remote LocalWebcam");
		remoteStreamer =
			new RemoteWebcamStreamer(streamingRate, exitCallback, hostname, remotePort);
		remoteStreamer.start();
	}

	/**
	 * Stops and cleans up the client's resources.
	 */
	public void kill() {
		log.debug("Shutting down StreamClient...");
		localStreamer.kill();
		if (remoteStreamer != null) {
			remoteStreamer.kill();
		}
		log.debug("StreamClient shut down");
	}

	public Webcam getCurrentWebcam() {
		return currentWebcam;
	}

	/**
	 * Changes the source of the webcam images to display.
	 */
	public void setCurrentWebcam(Webcam newWebcam) {
		log.debug("Switching webcam to " + newWebcam);
		this.currentWebcam = newWebcam;
	}

	public int getStreamingRate() {
		return streamingRate;
	}

	public void setStreamingRate(int streamingRate) {
		this.streamingRate = streamingRate;
	}

}

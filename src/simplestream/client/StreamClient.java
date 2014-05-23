package simplestream.client;

import org.apache.log4j.Logger;


/**
 * Manages the display of the webcam stream in either local or remote modes.
 */
public class StreamClient {

	private final Logger log = Logger.getLogger(getClass());

	private int streamingRate;

	/** The current client local stream manager. */
	private LocalWebcamStreamer localStreamer;

	/** The current client local stream manager. */
	private RemoteWebcamStreamer remoteStreamer;

	/**
	 * Constructs a {@link StreamClient} in the default local mode.
	 */
	public StreamClient(LocalWebcamStreamer localStreamer, int streamingRate) {
		this.localStreamer = localStreamer;
		this.streamingRate = streamingRate;
	}

	/**
	 * Begins receiving webcam data from the local webcam, if not doing so already.
	 */
	public void switchToLocal() {
		log.debug("Switching to Local Webcam");
		localStreamer.setDisplay(true);
		localStreamer.init();
	}

	/**
	 * Begins receiving webcam data from a remote host.
	 *
	 * @param hostname The hostname of the remote host.
	 * @param remotePort The connected port on the remote host.
	 */
	public void switchToRemote(String hostname, int remotePort) {
		log.debug("Switching to Remote Webcam");
		localStreamer.setDisplay(false);
		remoteStreamer = new RemoteWebcamStreamer(streamingRate, hostname, remotePort);
		remoteStreamer.init();
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

	public int getStreamingRate() {
		return streamingRate;
	}

	public void setStreamingRate(int streamingRate) {
		this.streamingRate = streamingRate;
	}

}

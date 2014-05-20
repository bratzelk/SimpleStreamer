package simplestream.client;


/**
 * Manages the display of the webcam stream in either local or remote modes.
 */
public class StreamClient {

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
		localStreamer.setDisplay(true);
	}

	/**
	 * Begins receiving webcam data from a remote host.
	 *
	 * @param hostname The hostname of the remote host.
	 * @param remotePort The connected port on the remote host.
	 */
	public void switchToRemote(String hostname, int remotePort) {
		localStreamer.setDisplay(false);
		remoteStreamer = new RemoteWebcamStreamer(streamingRate, hostname, remotePort);
		remoteStreamer.init();
	}

//	/**
//	 * Stops any {@link LocalWebcamStreamer} that is currently running.
//	 */
//	protected void killStreamer() {
//		if (streamer != null) {
//			streamer.stop();
//		}
//	}

	public int getStreamingRate() {
		return streamingRate;
	}

	public void setStreamingRate(int streamingRate) {
		this.streamingRate = streamingRate;
	}

}

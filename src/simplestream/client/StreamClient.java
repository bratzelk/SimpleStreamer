package simplestream.client;


/**
 * Manages the display of the webcam stream in either local or remote modes.
 */
public class StreamClient implements Runnable {

	private int streamingRate;

	/** The current client stream manager. */
	private WebcamStreamer streamer;

	/**
	 * Remote stream constructor.
	 */
	public StreamClient(int streamingRate, String hostname, int remotePort) {
		this.streamingRate = streamingRate;
		switchToRemote(hostname, remotePort);
	}

	/**
	 * Constructs a {@link StreamClient} in the default local mode.
	 */
	public StreamClient(int streamingRate) {
		this.streamingRate = streamingRate;
		switchToLocal();
	}

	@Override
	public void run() {}

	/**
	 * Begins receiving webcam data from the local webcam, if not doing so already.
	 */
	protected void switchToLocal() {
		killStreamer();
		streamer = new LocalWebcamStreamer(streamingRate);
		streamer.init();
	}

	/**
	 * Begins receiving webcam data from a remote host.
	 *
	 * @param hostname The hostname of the remote host.
	 * @param remotePort The connected port on the remote host.
	 */
	public void switchToRemote(String hostname, int remotePort) {
		killStreamer();
		streamer = new RemoteWebcamStreamer(streamingRate, hostname, remotePort);
		streamer.init();
	}

	/**
	 * Stops any {@link LocalWebcamStreamer} that is currently running.
	 */
	protected void killStreamer() {
		if (streamer != null) {
			streamer.stop();
		}
	}

	public int getStreamingRate() {
		return streamingRate;
	}

	public void setStreamingRate(int streamingRate) {
		this.streamingRate = streamingRate;
	}

}

package simplestream.client;


/**
 * Manages the display of the webcam stream in either local or remote modes.
 */
public class StreamClient implements Runnable {

	private int streamingRate;
	private boolean localMode;

	private WebcamStreamer streamer;

	public StreamClient(int streamingRate) {
		new Thread(this).start();
		this.streamingRate = streamingRate;
	}

	public void run() {}

	public void setLocal() {
		if (localMode) return;


		streamer = new WebcamStreamer(streamingRate);
		streamer.init();
	}

	public void setRemote(String hostname, int remotePort) {
		if (!localMode) return;

		streamer = new RemoteWebcamStreamer(streamingRate, hostname, remotePort);
		streamer.init();
	}

	/**
	 * Stops any {@link LocalWebcamStreamer} that is currently running.
	 */
	protected void killStreamer() {
		if (streamer!=null) {
			streamer.
		}
	}

	public int getStreamingRate() {
		return streamingRate;
	}

	public void setStreamingRate(int streamingRate) {
		this.streamingRate = streamingRate;
	}

}

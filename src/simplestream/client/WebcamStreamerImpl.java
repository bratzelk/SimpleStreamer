package simplestream.client;

import simplestream.Webcam;

public class WebcamStreamerImpl implements WebcamStreamer {

	private Webcam webcam;

	private int streamingRate;
	private boolean running = true;
	private byte[] currentFrame;

	public WebcamStreamerImpl(final int streamingRate) {
		this.streamingRate = streamingRate;
	}

	@Override
	public void init() {
		webcam = new Webcam();
	}

	/**
	 * Gets the next frame of webcam image data to display.
	 *
	 * @return The image data.
	 */
	public byte[] getFrame() {
		return webcam.getImage();
	}

	public synchronized void start() {
		running = true;
		notifyAll();
	}

	/**
	 * Stops streaming webcam data.
	 */
	public synchronized void stop() {
		running = false;
	}

	public int getStreamingRate() {
		return streamingRate;
	}

	public void setStreamingRate(int streamingRate) {
		this.streamingRate = streamingRate;
	}

	protected Webcam getWebcam() {
		return webcam;
	}

	protected void setWebcam(Webcam webcam) {
		this.webcam = webcam;
	}

	public boolean isRunning() {
		return running;
	}

	protected void setRunning(boolean running) {
		this.running = running;
	}

	public byte[] getCurrentFrame() {
		return currentFrame;
	}

	protected void setCurrentFrame(byte[] currentFrame) {
		this.currentFrame = currentFrame;
	}

}

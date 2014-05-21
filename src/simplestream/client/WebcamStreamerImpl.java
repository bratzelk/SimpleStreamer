package simplestream.client;

import org.apache.log4j.Logger;

import simplestream.StreamViewer;
import simplestream.Webcam;

/**
 * Implements logic common to managing and rendering data streams from webcams, both local and
 * remote.
 */
public abstract class WebcamStreamerImpl implements WebcamStreamer, Runnable {

	protected final Logger log = Logger.getLogger(getClass());

	/** The webcam from which the stream is being read. */
	private Webcam webcam;

	/** The viewer in which to render the local webcam images. */
	private StreamViewer viewer;

	private int streamingRate;
	private boolean running = true;
	private byte[] currentFrame;
	/** Whether to render the output of the local webcam. */
	private boolean display;

	private final Thread thread;

	public WebcamStreamerImpl(Webcam webcam, int streamingRate, boolean display) {
		setWebcam(webcam);
		setStreamingRate(streamingRate);
		setDisplay(display);

		log.debug("Running " + this + " on new thread...");
		thread = new Thread(this);
	}

	public WebcamStreamerImpl(Webcam webcam, int streamingRate) {
		this(webcam, streamingRate, true);
	}

	public void init() {
		thread.start();
	}

	/**
	 * Gets the next frame of webcam image data to display.
	 *
	 * @return The image data.
	 */
	public byte[] getFrame() {
		return webcam.getImage();
	}

	/**
	 * Displays an image in the viewer.
	 *
	 * @param imageData The image to display.
	 */
	public void displayFrame(byte[] imageData) {
		log.debug("Displaying the new image!");
		viewer.addImage(imageData);
	}

	/**
	 * Sets whether the display the local webcam on the local host or not. If so, a window is
	 * created to render the images; otherwise any existing window is closed.
	 *
	 * @param display Whether to display the local images.
	 */
	public void setDisplay(boolean display) {
		if (display == this.display) return;

		this.display = display;
		if (display) {
			viewer = new StreamViewer();
		} else {
			viewer.close();
		}
	}

	public boolean isDisplaying() {
		return display;
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

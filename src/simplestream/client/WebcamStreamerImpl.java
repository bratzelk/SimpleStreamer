package simplestream.client;

import org.apache.log4j.Logger;

import simplestream.StreamViewer;
import simplestream.webcam.LocalWebcam;

/**
 * Implements logic common to managing and rendering data streams from webcams, both local and
 * remote.
 */
public abstract class WebcamStreamerImpl extends Thread implements WebcamStreamer, Runnable {

	protected final Logger log = Logger.getLogger(getClass());

	/** The localWebcam from which the stream is being read. */
	private LocalWebcam localWebcam;

	/** The viewer in which to render the local localWebcam images. */
	private StreamViewer viewer;

	protected int streamingRate;
	private boolean running = true;
	private byte[] currentFrame;

	public WebcamStreamerImpl(LocalWebcam localWebcam, int streamingRate, Runnable exitCallback) {
		this.viewer = new StreamViewer(exitCallback);
		setWebcam(localWebcam);
		setStreamingRate(streamingRate);

		log.debug("Running " + this + " on new thread...");
	}

	/**
	 * Gets the next frame of localWebcam image data to display.
	 *
	 * @return The image data.
	 */
	public byte[] getFrame() {
		return localWebcam.getImage();
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

	public synchronized void start() {
		running = true;
		notifyAll();
	}

	/**
	 * Stops streaming localWebcam data.
	 */
	public void kill() {
		log.debug("Stopping " + this + "...");
		setRunning(false);
		if (isAlive()) {
			interrupt();
		}
	}

	public int getStreamingRate() {
		return streamingRate;
	}

	public void setStreamingRate(int streamingRate) {
		this.streamingRate = streamingRate;
	}

	protected LocalWebcam getWebcam() {
		return localWebcam;
	}

	protected void setWebcam(LocalWebcam localWebcam) {
		this.localWebcam = localWebcam;
	}

	public StreamViewer getViewer() {
		return viewer;
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

package simplestream.client;

import simplestream.StreamViewer;
import simplestream.Webcam;

/**
 * Displays a webcam stream from the default camera.
 */
public class LocalWebcamStreamer implements WebcamStreamer {

	private int streamingRate;
	private boolean running = true;

	private StreamViewer viewer;
	private Webcam webcam;

	public LocalWebcamStreamer(int streamingRate) {
		this.streamingRate = streamingRate;
	}

	public synchronized void init() {
		// TODO: Show the local image viewer.
		// TODO: The StreamViewer currently listens for the enter key. It doesn't do anything when it
		// catches the event yet. This needs to be implemented.
		viewer = new StreamViewer();
		webcam = new Webcam();

		// TODO: nice exit from this loop.
		while (true) {
			// Pause activity if not running.
			if (!running) try {
				wait();
			} catch (InterruptedException e1) {}

			displayFrame(getFrame());
			try {
				Thread.sleep(streamingRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException("Webcam streamer was interrupted.");
			}
		}
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
		viewer.addImage(imageData);
	}

	/**
	 * Stops streaming webcam data.
	 */
	public synchronized void stop() {
		running = false;
	}

	public synchronized void start() {
		running = true;
		notifyAll();
	}

	public int getStreamingRate() {
		return streamingRate;
	}

	public void setStreamingRate(int streamingRate) {
		this.streamingRate = streamingRate;
	}

}

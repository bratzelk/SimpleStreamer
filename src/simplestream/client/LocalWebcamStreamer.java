package simplestream.client;

import simplestream.StreamViewer;

import common.Out;

/**
 * Displays a webcam stream from the default camera.
 */
public class LocalWebcamStreamer extends WebcamStreamerImpl {

	/** The viewer in which to render the local webcam images. */
	private StreamViewer viewer;

	/** Whether to render the output of the local webcam. */
	private boolean display;

	public LocalWebcamStreamer(int streamingRate, boolean display) {
		super(streamingRate);
		setDisplay(display);
	}

	public LocalWebcamStreamer(int streamingRate) {
		this(streamingRate, true);
	}

	public synchronized void init() {
		// TODO: Show the local image viewer.
		// TODO: The StreamViewer currently listens for the enter key. It doesn't do anything when
		// it
		// catches the event yet. This needs to be implemented.
		super.init();
		Out.print("Receiving local webcam stream");

		// TODO: nice exit from this loop.
		while (true) {
			// Pause activity if not running.
			if (!isRunning()) try {
				wait();
			} catch (InterruptedException e) {}

			setCurrentFrame(getFrame());
			if (display) {
				displayFrame(getCurrentFrame());
			}
			try {
				Thread.sleep(getStreamingRate());
			} catch (InterruptedException e) {
				throw new RuntimeException("Local webcam streamer was interrupted", e);
			}
		}
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

}

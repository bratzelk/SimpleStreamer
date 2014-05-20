package simplestream.client;

import simplestream.Webcam;
import common.Out;

/**
 * Displays a webcam stream from the default camera.
 */
public class LocalWebcamStreamer extends WebcamStreamerImpl {

	public LocalWebcamStreamer(Webcam webcam, int streamingRate, boolean display) {
		super(webcam,streamingRate, display);
	}

	public LocalWebcamStreamer(Webcam webcam, int streamingRate) {
		super(webcam,streamingRate);
	}

	@Override
	public void run() {
		// TODO: Show the local image viewer.
		// TODO: The StreamViewer currently listens for the enter key. It doesn't do anything when
		// it
		// catches the event yet. This needs to be implemented.
		log.debug("Starting local webcam stream...");

		// TODO: nice exit from this loop.
		while (true) {
			// Pause activity if not running.
			if (!isRunning()) try {
				wait();
			} catch (InterruptedException e) {}

			setCurrentFrame(getFrame());
			if (isDisplaying()) {
				displayFrame(getCurrentFrame());
			}
			try {
				Thread.sleep(getStreamingRate());
			} catch (InterruptedException e) {
				throw new RuntimeException("Local webcam streamer was interrupted", e);
			}
		}
	}

}

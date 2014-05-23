package simplestream.client;

import simplestream.webcam.LocalWebcam;

/**
 * Displays a webcam stream from the default camera.
 */
public class LocalWebcamStreamer extends WebcamStreamerImpl {

	public LocalWebcamStreamer(LocalWebcam localWebcam, int streamingRate, Runnable exitCallback) {
		super(localWebcam, streamingRate, exitCallback);
	}

	/**
	 * Initiates the local webcam streaming logic.
	 */
	@Override
	public void run() {
		// TODO: Show the local image viewer.
		log.debug("Starting local webcam stream...");
		loop();
	}

	/**
	 * So long as the streamer is running, receives frames from the webcam and displays them.
	 */
	protected void loop() {
		while (true) {
			// TODO(orlade): Pause activity if not running.
			if (!isRunning()) {
				log.debug("Waiting for the local streamer to start...");
			} else {
				setCurrentFrame(getFrame());
				displayFrame(getCurrentFrame());
			}

			// Wait until it's time to display the next frame.
			try {
				Thread.sleep(getStreamingRate());
			} catch (InterruptedException e) {
				log.error("Local webcam streamer was interrupted", e);
				return;
			}
		}
	}

}

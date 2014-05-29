package simplestream.client;

import org.apache.log4j.Logger;

import simplestream.webcam.Webcam;


/**
 * Manages the display of the webcam stream in either local or remote modes.
 */
public class StreamClient {

	private final Logger log = Logger.getLogger(getClass());

	/** Number of ms between rendering each frame. */
	private static final int DISPLAY_RATE = 100;

	private final int streamingRate;

	/** The {@link Webcam} currently being used to stream images (local or remote). */
	private Webcam webcam;

	/** The viewer in which to render the local webcam images. */
	private final StreamViewer viewer;

	/** The thread that constantly updates the current image in the {@link StreamViewer}. */
	private Thread viewerThread;

	/**
	 * Constructs a {@link StreamClient} in the default local mode.
	 */
	public StreamClient(Webcam webcam, int streamingRate, Runnable exitCallback) {
		this.webcam = webcam;
		this.streamingRate = streamingRate;
		this.viewer = new StreamViewer(exitCallback);
	}

	/**
	 * Renders the current {@link Webcam} frame in the {@link StreamViewer}.
	 */
	public void runViewer() {
		viewerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					// Display the current frame.
					byte[] frame = getCurrentWebcam().getImage();
					if (frame != null) {
						viewer.addImage(frame);
					}

					// Wait until it's time to display the next frame.
					try {
						Thread.sleep(DISPLAY_RATE);
					} catch (InterruptedException e) {
						log.error("Client was interrupted", e);
						return;
					}
				}
			}
		});
		viewerThread.start();
	}

	/**
	 * Stops and cleans up the client's resources.
	 */
	public void kill() {
		log.debug("Shutting down StreamClient...");
		if (webcam != null) {
			webcam.kill();
		}
		viewerThread.interrupt();
		viewer.close();
		log.debug("StreamClient shut down successfully");
	}

	public Webcam getCurrentWebcam() {
		return webcam;
	}

	public int getStreamingRate() {
		return streamingRate;
	}

}

package simplestream.client;

import java.io.IOException;

import org.apache.log4j.Logger;

import simplestream.webcam.LocalWebcam;
import simplestream.webcam.RemoteWebcam;
import simplestream.webcam.Webcam;


/**
 * Manages the display of the webcam stream in either local or remote modes.
 */
public class StreamClient {

	private final Logger log = Logger.getLogger(getClass());

	private final int streamingRate;

	/** The {@link Webcam} on the local machine. */
	private final LocalWebcam localWebcam;

	/** The current {@link Webcam} connected on a remote host. */
	private RemoteWebcam remoteWebcam;

	/** The {@link Webcam} currently being used to stream images (local or remote). */
	private Webcam currentWebcam;

	/** The viewer in which to render the local webcam images. */
	private final StreamViewer viewer;

	/** The thread that constantly updates the current image in the {@link StreamViewer}. */
	private Thread viewerThread;

	/**
	 * Constructs a {@link StreamClient} in the default local mode.
	 */
	public StreamClient(LocalWebcam localWebcam, int streamingRate, Runnable exitCallback) {
		this.localWebcam = localWebcam;
		this.streamingRate = streamingRate;
		this.viewer = new StreamViewer(exitCallback);
	}

	/**
	 * Begins receiving webcam data from the local webcam, if not doing so already.
	 */
	public void switchToLocal() {
		log.debug("Switching to local Webcam...");
		setCurrentWebcam(localWebcam);
	}

	/**
	 * Begins receiving webcam data from a remote host.
	 *
	 * @param hostname The hostname of the remote host.
	 * @param remotePort The connected port on the remote host.
	 */
	public void switchToRemote(String hostname, int remotePort) {
		log.debug("Switching to remote Webcam...");
		// If the remote host is already connected, do nothing.
		if (remoteWebcam != null && remoteWebcam.getPeer().equals(hostname, remotePort)) return;

		remoteWebcam = new RemoteWebcam(streamingRate, hostname, remotePort);
		setCurrentWebcam(remoteWebcam);
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
						Thread.sleep(getStreamingRate());
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
		if (localWebcam != null) {
			localWebcam.kill();
		}
		if (remoteWebcam != null) {
			// Don't call kill(); stopStreaming will kill the webcam once it is disconnected.
			try {
				remoteWebcam.stopStreaming();
			} catch (IOException e) {
				log.error("Failed to stop streaming cleanly, killing...", e);
				remoteWebcam.kill();
			}
		}
		viewerThread.interrupt();
		viewer.close();
		log.debug("StreamClient shut down successfully");
	}

	public Webcam getCurrentWebcam() {
		return currentWebcam;
	}

	/**
	 * Changes the source of the webcam images to display.
	 */
	public void setCurrentWebcam(Webcam newWebcam) {
		log.debug("Switching webcam to " + newWebcam);
		this.currentWebcam = newWebcam;
	}

	public int getStreamingRate() {
		return streamingRate;
	}

}

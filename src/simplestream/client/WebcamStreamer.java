package simplestream.client;

/**
 * Displays image data from a webcam.
 */
public interface WebcamStreamer {

	/**
	 * Gets the next frame of webcam image data to display.
	 *
	 * @return The image data.
	 */
	public byte[] getFrame();

	/**
	 * Initializes the streamer.
	 */
	public void init();

	/**
	 * Stops the streamer from updating.
	 */
	public void stop();

}

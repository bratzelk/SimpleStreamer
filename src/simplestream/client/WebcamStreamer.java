package simplestream.client;

/**
 * Displays image data from a webcam.
 */
public interface WebcamStreamer {

	/**
	 * Initializes the streamer.
	 */
	public void init();

	/**
	 * Gets the next frame of webcam image data to display.
	 *
	 * @return The image data.
	 */
	public byte[] getFrame();

	/**
	 * Returns the rate at which new frames are streamed from the webcam (frames per second).
	 */
	public int getStreamingRate();

	/**
	 * Stops the streamer from updating.
	 */
	public void stop();

}

package simplestream.webcam;

/**
 * Interface for a device that provides images to display.
 */
public interface Webcam {

	/**
	 * Returns the current webcam image to display.
	 */
	public byte[] getImage();

	/**
	 * Stops the webcam stream and cleans up any resources it was using.
	 */
	public void kill();

}

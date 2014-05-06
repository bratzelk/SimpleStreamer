package common;

/**
 * @author Kim
 *
 */
public class Settings {

	
	/**
	 * Default Settings
	 * 
	 * These are the Default Values but can be changed at runtime in the GUI or through the command line
	 * 
	 */
	
	
	public static final int DEFAULT_STREAMING_PORT = 6262;
	
	public static final String DEFAULT_HOSTNAME = "localhost";
	public static final int DEFAULT_REMOTE_PORT = 5555;
	
	public static final int DEFAULT_STREAMING_RATE = 100;
		
	
	/**
	 * General Settings
	 * 
	 */
		
	//Should we output lots of text... (good for debugging and for the GUI)
	public static final Boolean VERBOSE = true;
	
	/**
	 * Systems Settings
	 * 
	 */
	
	public static final String APP_NAME = "Simple Streamer";
	public static final String APP_AUTHOR = "Kim & Oliver";
	public static final String APP_VERSION = "1.0 2014";
}

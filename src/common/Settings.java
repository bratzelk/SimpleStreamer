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
	
	public static final String DEFAULT_HOSTNAME = "localhost";
	public static final int DEFAULT_PORT = 6262;
		
	
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

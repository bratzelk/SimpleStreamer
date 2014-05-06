package common;

public class Strings {

	/**
	 * Common Strings
	 * 
	 * These are strings used throughout the application.
	 * 
	 */
	
	public static final String EMPTY_REMOTE_HOSTNAME = "NO_REMOTE_HOST";
		
	/**
	 * Message Types Strings
	 * 
	 * These are strings used for messages
	 * According to the specification they are always lowercase
	 * 
	 */
	public static final String REQUEST_MESSAGE = "request";
	public static final String RESPONSE_MESSAGE = "response";
	
	public static final String START_REQUEST_MESSAGE = "startstream";
	public static final String START_RESONSE_MESSAGE = "startingstream";
	public static final String STATUS_RESONSE_MESSAGE = "status";
	public static final String IMAGE_RESONSE_MESSAGE = "image";
	public static final String STOP_REQUEST_MESSAGE = "stopstream";
	public static final String STOPPED_RESPONSE_MESSAGE = "stoppedstream";
}

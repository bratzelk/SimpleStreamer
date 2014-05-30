package simplestream.common;

/**
 * A collection of common strings used throughout the application.
 */
public class Strings {

	public static final String EMPTY_REMOTE_HOSTNAME = "NO_REMOTE_HOST";

	// JSON Message Protocol.
	public static final String LOCAL_STREAMING_TYPE_JSON = "local";
	public static final String REMOTE_STREAMING_TYPE_JSON = "remote";

	public static final String CLIENTS_JSON = "clients";
	public static final String SERVER_JSON = "server";
	public static final String RATELIMIT_JSON = "ratelimit";
	public static final String RATELIMITING_JSON = "ratelimiting";
	public static final String DATA_JSON = "data";
	public static final String STREAMING_JSON = "streaming";
	public static final String HANDOVER_JSON = "handover";
	public static final String IP_JSON = "ip";
	public static final String PORT_JSON = "port";
	public static final String SPORT_JSON = "sport";

	public static final String YES_JSON = "yes";
	public static final String NO_JSON = "no";

	// Message type strings, lowercase as per spec.
	public static final String REQUEST_MESSAGE = "request";
	public static final String RESPONSE_MESSAGE = "response";

	public static final String START_REQUEST_MESSAGE = "startstream";
	public static final String START_RESONSE_MESSAGE = "startingstream";
	public static final String STATUS_RESONSE_MESSAGE = "status";
	public static final String IMAGE_RESPONSE_MESSAGE = "image";
	public static final String STOP_REQUEST_MESSAGE = "stopstream";
	public static final String STOPPED_RESPONSE_MESSAGE = "stoppedstream";
	public static final String OVERLOADED_RESPONSE_MESSAGE = "overloaded";

}

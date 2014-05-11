package messages;

import org.json.simple.JSONObject;

import common.Settings;
import common.Strings;

public class StartRequestMessage extends RequestMessage {

	private int ratelimit;
	
	private int serverPort; //the port the client is serving on (as per LMS change).
	
	public StartRequestMessage() {
		this.ratelimit = Settings.DEFAULT_STREAMING_RATE;
		this.serverPort = Settings.DEFAULT_STREAMING_PORT;
	}
	
	@Override
	public String getType() {
		return Strings.START_REQUEST_MESSAGE;
	}
	
	public void setRatelimit(int ratelimit) {
		this.ratelimit = ratelimit;
	}
	
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toJSON() {
		
		//put the response part into the message
		JSONObject jsonMessage = standardMessageJSON();
		
		jsonMessage.put(Strings.RATELIMIT_JSON, this.ratelimit);
		
		jsonMessage.put(Strings.SPORT_JSON, this.serverPort);

		return jsonMessage.toJSONString();
	}

}

package messages;

import org.json.simple.JSONObject;

import common.Settings;
import common.Strings;

public class StartRequestMessage extends RequestMessage {

	private int ratelimit;
	
	public StartRequestMessage() {
		this.ratelimit = Settings.DEFAULT_STREAMING_RATE;
	}
	
	@Override
	public String getType() {
		return Strings.START_REQUEST_MESSAGE;
	}
	
	public void setRatelimit(int ratelimit) {
		this.ratelimit = ratelimit;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toJSON() {
		
		//put the response part into the message
		JSONObject jsonMessage = standardMessageJSON();
		
		jsonMessage.put(Strings.RATELIMIT_JSON, this.ratelimit);

		return jsonMessage.toJSONString();
	}

}

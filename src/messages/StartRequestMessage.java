package messages;

import org.json.simple.JSONObject;

import common.Strings;

public class StartRequestMessage extends RequestMessage {

	@Override
	public String getType() {
		return Strings.START_REQUEST_TYPE;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public String toJSON() {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put(requestOrResponseMessage(), getType());
		return jsonMessage.toJSONString();
	}

}

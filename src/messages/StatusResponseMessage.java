package messages;

import org.json.simple.JSONObject;

import common.Strings;

public class StatusResponseMessage extends ResponseMessage {

	private int numberOfConnectedClients;
	private String streamingType;
	
	private String ratelimitingImplemented;
	private String handoverImplemented;
	
	public StatusResponseMessage() {
		
		super();
		
		this.numberOfConnectedClients = 0;
		this.streamingType = Strings.LOCAL_STREAMING_TYPE_JSON; //Defaults to Local but should be explicitly set
		
		//These are both implemented so they will always be set to YES
		this.handoverImplemented = Strings.YES_JSON;
		this.ratelimitingImplemented = Strings.YES_JSON;
		
	}
	
	@Override
	public String getType() {
		
		return Strings.STATUS_RESONSE_MESSAGE;
	}

	
	public void setStreamingType(String streamingType) {
		this.streamingType = streamingType;
	}
	
	public void setNumberOfConnectedClients(int numberOfConnectedClients) {
		this.numberOfConnectedClients = numberOfConnectedClients;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toJSON() {
		
		//put the response part into the message
		JSONObject jsonMessage = standardMessageJSON();
		
		jsonMessage.put(Strings.HANDOVER_JSON, this.handoverImplemented);
		jsonMessage.put(Strings.RATELIMITING_JSON, this.ratelimitingImplemented);
		
		jsonMessage.put(Strings.CLIENTS_JSON, this.numberOfConnectedClients);
		jsonMessage.put(Strings.STREAMING_JSON, this.streamingType);
		
		return jsonMessage.toJSONString();
	}

}

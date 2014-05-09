package messages;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import simplestream.Peer;

import common.Strings;

public class OverloadedResponseMessage extends ResponseMessage {

	private List<Peer> connectedClients;
	
	private Boolean inRemoteMode;
	private Peer connectedServer;
	
	
	public OverloadedResponseMessage() {
		super();
	
		this.connectedServer = null;
		this.connectedClients = new ArrayList<Peer>();
		
		this.inRemoteMode = false;
	}
	
	@Override
	public String getType() {
		return Strings.OVERLOADED_RESPONSE_MESSAGE;
	}

	/**
	 * Optionally add in the current server you are connected to (if it exists)
	 * */
	public void addServer(Peer server) {
		this.connectedServer = server;
		this.inRemoteMode = true;
	}

	/**
	 * Add in the list of connected clients
	 * */
	public void addClients(List<Peer> clients) {
		this.connectedClients.addAll(clients);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toJSON() {
		
		//put the repsonse part into the message
		JSONObject jsonMessage = standardMessageJSON();
		
		//put the connected clients into a json array
		JSONArray clientsJSON = new JSONArray();
		for(Peer client: connectedClients) {
			clientsJSON.add(client.toJSON());
		}
		jsonMessage.put(Strings.CLIENTS_JSON, clientsJSON);
		
		//put the server (if it is connected to one)
		if(inRemoteMode) {
			jsonMessage.put(Strings.SERVER_JSON, connectedServer.toString());
		}
		
		return jsonMessage.toJSONString();
	}

}

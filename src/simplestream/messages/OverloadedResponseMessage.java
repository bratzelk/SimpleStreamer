package simplestream.messages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import simplestream.common.Strings;
import simplestream.networking.Peer;

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
	
	public List<Peer> getClients() {
		return this.connectedClients;
	}
	
	public Peer getServer() {
		return this.connectedServer;
	}
	
	public Boolean inRemoteMode() {
		return this.inRemoteMode;
	}
	
	/**
	 * Add in a single connected client
	 * */
	public void addClient(Peer client) {
		this.connectedClients.add(client);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toJSON() {

		// put the repsonse part into the message
		JSONObject jsonMessage = standardMessageJSON();

		// put the connected clients into a json array
		JSONArray clientsJSON = new JSONArray();
		for (Peer client : connectedClients) {
			clientsJSON.add(client.toJSON());
		}
		jsonMessage.put(Strings.CLIENTS_JSON, clientsJSON);

		// put the server (if it is connected to one)
		if (inRemoteMode) {
			jsonMessage.put(Strings.SERVER_JSON, connectedServer.toString());
		}

		return jsonMessage.toJSONString();
	}
	
	public void populateFieldsFromJSON(String jsonMessageString) {
		JSONParser parser = new JSONParser();
		JSONObject jsonMessage = null;
		
		log.debug("Populating fields using: " + jsonMessageString);
		  
		try {
			jsonMessage = (JSONObject) parser.parse(jsonMessageString);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		//Populate server (if it exists)
		String serverString = (String) jsonMessage.get(Strings.SERVER_JSON);
		if(serverString != null) {
			this.addServer(Peer.fromJSON(serverString));
		}
		
		//Populate clients
		JSONArray clients = (JSONArray) jsonMessage.get(Strings.CLIENTS_JSON);
		Iterator<JSONObject> iterator = clients.iterator();
		while (iterator.hasNext()) {
			 JSONObject client = (JSONObject) iterator.next();

			 String hostname = (String) client.get(Strings.IP_JSON);
			 int port = ((Long)client.get(Strings.PORT_JSON)).intValue();
			 
			 this.addClient(new Peer(hostname, port));
		}

	}

}

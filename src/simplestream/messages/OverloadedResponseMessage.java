package simplestream.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import simplestream.common.Strings;
import simplestream.networking.Peer;

/**
 * Indicates that the server has the maximum number of client connections
 * already. Provides a list of those clients so that a prospective client can
 * try connecting to them instead.
 */
public class OverloadedResponseMessage extends ResponseMessage {

	/** The clients currently connected to the server. */
	private Collection<Peer> connectedClients;

	/** Whether the server is streaming from a remote host. */
	private Boolean inRemoteMode;

	/** The host that the server is connected to. */
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

	public Collection<Peer> getClients() {
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

	/**
	 * Serializes the message to a JSON string.
	 */
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

	/**
	 * Popualtes the {@link Message} contents by deserializing the given JSON
	 * string.
	 *
	 * @param jsonMessageString
	 *            The serialized JSON content of the {@link Message}.
	 */
	public void populateFieldsFromJSON(String jsonMessageString) {
		JSONObject jsonMessage = deserialize(jsonMessageString);

		// Populate server (if it exists)
		String serverString = (String) jsonMessage.get(Strings.SERVER_JSON);
		if (serverString != null) {
			this.addServer(Peer.fromJSON(serverString));
		}

		// Populate clients
		JSONArray clients = (JSONArray) jsonMessage.get(Strings.CLIENTS_JSON);
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> iterator = (Iterator<JSONObject>) clients
				.iterator();
		while (iterator.hasNext()) {
			JSONObject client = (JSONObject) iterator.next();

			String hostname = (String) client.get(Strings.IP_JSON);
			int port = ((Long) client.get(Strings.PORT_JSON)).intValue();

			this.addClient(new Peer(hostname, port));
		}
	}

}

package simplestream.networking;

import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import simplestream.common.Strings;

/**
 * Represents a remote machine for the application can interact with.
 */
public class Peer {

	/** The hostname of the peer machine. */
	private final String hostname;

	/** The port on which the peer is receiving messages. */
	private final int port;

	public Peer(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	/**
	 * Creates a {@link Peer} from data extracted from the given {@link Socket}.
	 *
	 * @param socket
	 *            The socket of the connection to the peer.
	 * @return The {@link Peer} containing data from the socket.
	 */
	public static Peer fromSocket(Socket socket) {
		InetSocketAddress remoteAddress = (InetSocketAddress) socket
				.getRemoteSocketAddress();
		return new Peer(remoteAddress.getHostName(), remoteAddress.getPort());
	}

	/**
	 * Deserializes the {@link Peer} data from the given JSON string.
	 *
	 * @param jsonPeer
	 *            A serialized {@link Peer}.
	 * @return A deserialized {@link Peer}.
	 */
	public static Peer fromJSON(String jsonPeer) {
		String hostname = null;
		int port = 0;

		JSONParser parser = new JSONParser();
		JSONObject jsonMessage = null;
		try {
			jsonMessage = (JSONObject) parser.parse(jsonPeer);
		} catch (ParseException e) {
			throw new IllegalStateException("Unable to deserialize peer JSON: "
					+ jsonPeer);
		}

		if (jsonMessage != null) {
			hostname = (String) jsonMessage.get(Strings.IP_JSON);
			port = ((Long) jsonMessage.get(Strings.PORT_JSON)).intValue();
		}

		if (hostname != null && port != 0) {
			return new Peer(hostname, port);
		} else {
			throw new IllegalArgumentException(
					"Invalid JSON Supplied for new Peer");
		}
	}

	public String getHostname() {
		return this.hostname;
	}

	public int getPort() {
		return this.port;
	}

	/**
	 * Returns whether this {@link Peer} has the given hostname and port.
	 */
	public boolean equals(String hostname, int port) {
		return this.hostname.equals(hostname) && this.port == port;
	}

	@Override
	public String toString() {
		return toJSON().toJSONString();
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put(Strings.IP_JSON, getHostname());
		jsonMessage.put(Strings.PORT_JSON, getPort());
		return jsonMessage;
	}

}

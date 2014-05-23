package simplestream;

import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.simple.JSONObject;

import common.Strings;

public class Peer {

	String hostname;
	int port;

	public Peer(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public static Peer fromSocket(Socket socket) {
		InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
		return new Peer(remoteAddress.getHostName(), remoteAddress.getPort());
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

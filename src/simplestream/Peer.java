package simplestream;

import org.json.simple.JSONObject;

import common.Strings;

public class Peer {

	String hostname;
	int port;

	public Peer(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public String getHostname() {
		return this.hostname;
	}

	public int getPort() {
		return this.port;
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

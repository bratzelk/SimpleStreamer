package simplestream.messages;

import org.json.simple.JSONObject;

import simplestream.common.Settings;
import simplestream.common.Strings;

/**
 * Requests image streaming from the server.
 */
public class StartRequestMessage extends RequestMessage {

	private int ratelimit;

	private int serverPort; // the port the client is serving on (as per LMS
							// change).

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

	public int getRatelimit() {
		return ratelimit;
	}

	public int getServerPort() {
		return serverPort;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toJSON() {
		JSONObject jsonMessage = standardMessageJSON();
		jsonMessage.put(Strings.RATELIMIT_JSON, this.ratelimit);
		jsonMessage.put(Strings.SPORT_JSON, this.serverPort);
		return jsonMessage.toJSONString();
	}

	public void populateFieldsFromJSON(String jsonMessageString) {
		JSONObject jsonMessage = deserialize(jsonMessageString);

		int sport = ((Long) jsonMessage.get(Strings.SPORT_JSON)).intValue();
		this.setServerPort(sport);

		int rateLimit = ((Long) jsonMessage.get(Strings.RATELIMIT_JSON))
				.intValue();
		this.setRatelimit(rateLimit);
	}

}

package simplestream.messages;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * An abstract class representing all {@link Message}s sent by the system.
 *
 * Extend either {@link RequestMessage} or {@link ResponseMessage} instead of
 * this.
 */
public abstract class Message {

	protected final Logger log = Logger.getLogger(getClass());

	/**
	 * Returns "request" or "response" depending on whether the {@link Message}
	 * is a {@link RequestMessage} or a {@link ResponseMessage} respectively.
	 */
	public abstract String requestOrResponseMessage();

	/**
	 * Returns the type of the {@link Message}.
	 */
	public abstract String getType();

	/**
	 * Serializes the {@link Message} to a JSON string. Should be overridden to
	 * add additional fields.
	 */
	public String toJSON() {
		return standardMessageJSON().toJSONString();
	}

	/**
	 * Generates the standard message JSON, for example:
	 * {"response":"stoppedstream"}.
	 */
	@SuppressWarnings("unchecked")
	protected JSONObject standardMessageJSON() {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put(requestOrResponseMessage(), getType());
		return jsonMessage;
	}

	/**
	 * Deserializes the message string into a {@link JSONObject}.
	 *
	 * @param jsonMessageString
	 *            The serialized JSON of the message.
	 * @return The deserialized representation of the message.
	 */
	protected JSONObject deserialize(String jsonMessageString) {
		log.debug("Populating fields using: " + jsonMessageString);

		JSONParser parser = new JSONParser();
		try {
			return (JSONObject) parser.parse(jsonMessageString);
		} catch (ParseException e) {
			throw new IllegalStateException(
					"Unable to deserialize message JSON: " + jsonMessageString);
		}
	}

}

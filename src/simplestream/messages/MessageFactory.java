package simplestream.messages;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import simplestream.common.Strings;

/**
 * Creates default {@link Message} objects of the given type.
 */
public class MessageFactory {

	private static final JSONParser parser = new JSONParser();

	/**
	 * Creates an empty message of the given type.
	 *
	 * @param messageType
	 *            The type of message to create.
	 * @return The created {@link Message}.
	 */
	public static Message createMessage(String messageType) {
		messageType = messageType.toLowerCase();

		if (messageType.equals(Strings.START_REQUEST_MESSAGE)) {
			return new StartRequestMessage();
		} else if (messageType.equals(Strings.START_RESONSE_MESSAGE)) {
			return new StartResponseMessage();
		} else if (messageType.equals(Strings.STATUS_RESONSE_MESSAGE)) {
			return new StatusResponseMessage();
		} else if (messageType.equals(Strings.IMAGE_RESPONSE_MESSAGE)) {
			return new ImageResponseMessage();
		} else if (messageType.equals(Strings.STOP_REQUEST_MESSAGE)) {
			return new StopRequestMessage();
		} else if (messageType.equals(Strings.STOPPED_RESPONSE_MESSAGE)) {
			return new StoppedResponseMessage();
		} else if (messageType.equals(Strings.OVERLOADED_RESPONSE_MESSAGE)) {
			return new OverloadedResponseMessage();
		} else {
			throw new IllegalArgumentException("Unknown message type: "
					+ messageType);
		}
	}

	/**
	 * Extracts the type of the {@link Message} from a serialized JSON string.
	 *
	 * @param messageJson
	 *            The JSON of the message to get the type of.
	 * @return The type of the message.
	 */
	public static String getMessageType(String messageJson) {
		if (messageJson == null)
			return null;

		JSONObject obj;
		try {
			obj = (JSONObject) parser.parse(messageJson);
			if (obj == null) {
				throw new IllegalArgumentException(
						"Deserialized message is null: " + messageJson);
			}
		} catch (ParseException e) {
			throw new IllegalArgumentException("Message not valid JSON: "
					+ messageJson);
		}
		if (obj.containsKey(Strings.REQUEST_MESSAGE)) {
			return obj.get(Strings.REQUEST_MESSAGE).toString();
		} else {
			return obj.get(Strings.RESPONSE_MESSAGE).toString();
		}
	}

}

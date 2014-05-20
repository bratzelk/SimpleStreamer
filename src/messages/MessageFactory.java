package messages;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import common.Strings;

/**
 * Creates default {@link Message} objects of the given type.
 */
public class MessageFactory {

	private static final JSONParser parser = new JSONParser();

	public static Message createMessage(String messageType) {
		// Ensure this string is always lowercase.
		messageType = messageType.toLowerCase();

		if (messageType.equals(Strings.START_REQUEST_MESSAGE)) {
			return new StartRequestMessage();
		} else if (messageType.equals(Strings.START_RESONSE_MESSAGE)) {
			return new StartResponseMessage();
		} else if (messageType.equals(Strings.STATUS_RESONSE_MESSAGE)) {
			return new StatusResponseMessage();
		} else if (messageType.equals(Strings.IMAGE_RESONSE_MESSAGE)) {
			return new ImageResponseMessage();
		} else if (messageType.equals(Strings.STOP_REQUEST_MESSAGE)) {
			return new StopRequestMessage();
		} else if (messageType.equals(Strings.STOPPED_RESPONSE_MESSAGE)) {
			return new StoppedResponseMessage();
		} else if (messageType.equals(Strings.OVERLOADED_RESPONSE_MESSAGE)) {
			return new OverloadedResponseMessage();
		} else {
			throw new IllegalArgumentException("Unknown message type: " + messageType);
		}
	}

	public static String getMessageType(String messageJson) {
		JSONObject obj;
		try {
			obj = (JSONObject) parser.parse(messageJson);
			if (obj == null) {
				throw new IllegalArgumentException("Deserialized message is null: " + messageJson);
			}
		} catch (ParseException e) {
			throw new IllegalArgumentException("Message not valid JSON: " + messageJson);
		}
		if (obj.containsKey(Strings.REQUEST_MESSAGE)) {
			return obj.get(Strings.REQUEST_MESSAGE).toString();
		} else {
			return obj.get(Strings.RESPONSE_MESSAGE).toString();
		}
	}

	// public static Message deserializeMessage(String messageJson) {
	// JSONObject obj;
	// try {
	// obj = (JSONObject) parser.parse(messageJson);
	// if (obj == null) {
	// throw new IllegalArgumentException("Deserialized message is null: " + messageJson);
	// }
	// } catch (ParseException e) {
	// throw new IllegalArgumentException("Message not valid JSON: " + messageJson);
	// }
	//
	// if (obj != null) {
	// String messageType = obj.get("Type").toString();
	// if (messageType.equals(Strings.START_REQUEST_MESSAGE)) {
	// Message message = new StartRequestMessage();
	// }
	// if (obj.get("Type").equals("StartUpdate"))
	// message = new StartUpdateInstruction();
	// else if (obj.get("Type").equals("EndUpdate"))
	// message = new EndUpdateInstruction();
	// else if (obj.get("Type").equals("CopyBlock"))
	// message = new CopyBlockInstruction();
	// else if (obj.get("Type").equals("NewBlock"))
	// message = new NewBlockInstruction();
	// else if (obj.get("Type").equals("Config")) message = new ConfigInstruction();
	// message.FromJSON(jst);
	// return message;
	// } else
	// return null;
	// }
}

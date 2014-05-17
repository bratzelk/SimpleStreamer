package messages;

import common.Strings;

/**
 * Creates default {@link Message} objects of the given type.
 */
public class MessageFactory {

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

}

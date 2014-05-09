package messages;

import common.Strings;

/**
 * @author Kim
 *
 */
public class MessageFactory {

	private MessageFactory() {
		
	}
	
	public static Message createMessage(String messageType) throws MessageNotFoundException {
		
		//Let's ensure this string is always lowercase
		String lowercaseMessageType = messageType.toLowerCase();
		
		if(lowercaseMessageType.equals(Strings.START_REQUEST_MESSAGE)) {
			return new StartRequestMessage();
		}
		else if(lowercaseMessageType.equals(Strings.START_RESONSE_MESSAGE)) {
			return new StartResponseMessage();
		}
		else if(lowercaseMessageType.equals(Strings.STATUS_RESONSE_MESSAGE)) {
			return new StatusResponseMessage();
		}
		else if(lowercaseMessageType.equals(Strings.IMAGE_RESONSE_MESSAGE)) {
			return new ImageResponseMessage();
		}
		else if(lowercaseMessageType.equals(Strings.STOP_REQUEST_MESSAGE)) {
			return new StopRequestMessage();
		}
		else if(lowercaseMessageType.equals(Strings.STOPPED_RESPONSE_MESSAGE)) {
			return new StoppedResponseMessage();
		}
		else if(lowercaseMessageType.equals(Strings.OVERLOADED_RESPONSE_MESSAGE)) {
			return new OverloadedResponseMessage();
		}
		else {
			throw new MessageNotFoundException();
		}
		
	}
}

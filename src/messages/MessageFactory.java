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
		
		if(lowercaseMessageType.equals(Strings.START_REQUEST_TYPE)){
			return new StartRequestMessage();
		}
		else{
			throw new MessageNotFoundException();
		}
		
	}
}

package simplestream;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import simplestream.common.Strings;
import simplestream.messages.Message;
import simplestream.messages.MessageFactory;

public class MessageTests {


	@Test
	public void messageCreation() {
		String desiredMessageType = Strings.START_REQUEST_MESSAGE;
		Message message = MessageFactory.createMessage(desiredMessageType);
		String actualMessageType = message.getType();

		assertTrue(desiredMessageType.equals(actualMessageType));
	}
}

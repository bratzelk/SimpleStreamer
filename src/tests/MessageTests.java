package tests;

import static org.junit.Assert.assertTrue;
import messages.Message;
import messages.MessageFactory;

import org.junit.Test;

import common.Strings;

public class MessageTests {


	@Test
	public void messageCreation() {
		String desiredMessageType = Strings.START_REQUEST_MESSAGE;
		Message message = MessageFactory.createMessage(desiredMessageType);
		String actualMessageType = message.getType();

		assertTrue(desiredMessageType.equals(actualMessageType));
	}
}

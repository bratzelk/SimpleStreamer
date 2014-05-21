package tests;

import static org.junit.Assert.*;
import messages.*;

import org.junit.Test;

import common.Strings;

public class MessageTests {
	
	
	@Test
	public void messageCreation() 
	{
		String desiredMessageType = Strings.START_REQUEST_MESSAGE;
		Message message = MessageFactory.createMessage(desiredMessageType);
		String actualMessageType = message.getType();
		
		assertTrue(desiredMessageType.equals(actualMessageType));
	}
}

package simplestream.messages;

import simplestream.common.Strings;

/**
 * A message sent to the server requesting some action.
 */
public abstract class RequestMessage extends Message {

	@Override
	public String requestOrResponseMessage() {
		return Strings.REQUEST_MESSAGE;
	}
}

package simplestream.messages;

import simplestream.common.Strings;

/**
 * A message returned from a server to a client.
 */
public abstract class ResponseMessage extends Message {

	@Override
	public String requestOrResponseMessage() {
		return Strings.RESPONSE_MESSAGE;
	}

}

package simplestream.messages;

import simplestream.common.Strings;

public abstract class ResponseMessage extends Message {

	@Override
	public String requestOrResponseMessage() {

		return Strings.RESPONSE_MESSAGE;
	}
}

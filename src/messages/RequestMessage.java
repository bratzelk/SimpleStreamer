package messages;

import common.Strings;

public abstract class RequestMessage extends Message{

	@Override
	public String requestOrResponseMessage() {
		return Strings.MESSAGE_REQUEST_TYPE;
	}
}

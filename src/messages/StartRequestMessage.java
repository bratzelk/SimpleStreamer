package messages;

import common.Strings;

public class StartRequestMessage extends RequestMessage {

	@Override
	public String getType() {
		return Strings.START_REQUEST_MESSAGE;
	}

}

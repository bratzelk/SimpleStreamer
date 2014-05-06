package messages;

import common.Strings;

public class StopRequestMessage extends RequestMessage {

	@Override
	public String getType() {
		return Strings.STOP_REQUEST_MESSAGE;
	}

}

package messages;

import common.Strings;

public class StoppedResponseMessage extends ResponseMessage {

	@Override
	public String getType() {
		return Strings.STOPPED_RESPONSE_MESSAGE;
	}

}

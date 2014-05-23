package simplestream.messages;

import simplestream.common.Strings;

public class StartResponseMessage extends ResponseMessage {

	@Override
	public String getType() {
		return Strings.START_RESONSE_MESSAGE;
	}


}

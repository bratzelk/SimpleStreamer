package simplestream.messages;

import simplestream.common.Strings;

/**
 * Acknowledgement of the {@link StartRequestMessage}
 */
public class StartResponseMessage extends ResponseMessage {

	@Override
	public String getType() {
		return Strings.START_RESONSE_MESSAGE;
	}

}

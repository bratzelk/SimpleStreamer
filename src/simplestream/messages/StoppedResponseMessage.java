package simplestream.messages;

import simplestream.common.Strings;

/**
 * Acknowledgement of the {@link StopRequestMessage}.
 */
public class StoppedResponseMessage extends ResponseMessage {

	@Override
	public String getType() {
		return Strings.STOPPED_RESPONSE_MESSAGE;
	}

}

package simplestream.messages;

import simplestream.common.Strings;

/**
 * Requests the server stop streaming data.
 */
public class StopRequestMessage extends RequestMessage {

	@Override
	public String getType() {
		return Strings.STOP_REQUEST_MESSAGE;
	}

}

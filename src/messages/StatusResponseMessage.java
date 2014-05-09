package messages;

import common.Strings;

public class StatusResponseMessage extends ResponseMessage {

	@Override
	public String getType() {
		
		return Strings.STATUS_RESONSE_MESSAGE;
	}

	@Override
	public String toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}

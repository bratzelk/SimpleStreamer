package messages;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;

import common.Strings;

public class ImageResponseMessage extends ResponseMessage {

	byte[] base64ImageData;
	
	
	@Override
	public String getType() {
		return Strings.IMAGE_RESONSE_MESSAGE;
	}
	
	
	public void setImageData(byte[] imageData) {
		this.base64ImageData = Base64.encodeBase64(imageData);
	}
	
	public byte[] getImageData() {
		return Base64.decodeBase64(this.base64ImageData);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toJSON() {
		
		//put the response part into the message
		JSONObject jsonMessage = standardMessageJSON();
		
		jsonMessage.put(Strings.DATA_JSON, this.base64ImageData);
		
		return jsonMessage.toJSONString();
	}

}

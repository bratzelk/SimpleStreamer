package messages;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import common.Out;
import common.Strings;

public class ImageResponseMessage extends ResponseMessage {

	byte[] base64ImageData;
	
	
	@Override
	public String getType() {
		return Strings.IMAGE_RESONSE_MESSAGE;
	}
	
	
	public void setImageData(byte[] imageData) {
		Out.print("Setting image data: " + imageData);
		this.base64ImageData = Base64.encodeBase64(imageData);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public String toJSON() {
		
		//put the response part into the message
		JSONObject jsonMessage = standardMessageJSON();
		
		jsonMessage.put(Strings.DATA_JSON, this.base64ImageData.toString());
		
		return jsonMessage.toJSONString();
	}
	
	
	public static byte[] imagedataFromJSON(String jsonMessageString)  {
		
		JSONParser parser = new JSONParser();
		JSONObject jsonMessage = null;
		
		byte[] imageData = null;
		
		try {
			jsonMessage = (JSONObject) parser.parse(jsonMessageString);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		if(jsonMessage != null) {
			imageData = Base64.decodeBase64(  (String) jsonMessage.get(Strings.DATA_JSON));

		}
		return imageData;
		
	}


}

package messages;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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


	@SuppressWarnings("unchecked")
	@Override
	public String toJSON() {

		// put the response part into the message
		JSONObject jsonMessage = standardMessageJSON();

		// We need to store this byte array as a String otherwise it won't form
		// a valid JSON message (doesn't add " " to byte[])
		// Need to ensure we convert this back to a byte[] when received.
		String imageData = null;
		try {
			imageData = new String(this.base64ImageData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonMessage.put(Strings.DATA_JSON, imageData);

		return jsonMessage.toJSONString();
	}


	public static byte[] imagedataFromJson(String jsonMessageString) {

		JSONParser parser = new JSONParser();
		JSONObject jsonMessage = null;

		byte[] imageDataDecoded = null;

		try {
			jsonMessage = (JSONObject) parser.parse(jsonMessageString);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		if (jsonMessage != null) {
			// Convert the image data from a JSON string to a byte[] and decode it.
			byte[] imageData = null;
			try {
				imageData = ((String) jsonMessage.get(Strings.DATA_JSON)).getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imageDataDecoded = Base64.decodeBase64(imageData);

		}
		return imageDataDecoded;

	}


}

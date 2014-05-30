package simplestream.messages;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import simplestream.common.Strings;

import com.github.sarxos.webcam.Webcam;

/**
 * Contains the image data captured from a {@link Webcam}.
 */
public class ImageResponseMessage extends ResponseMessage {

	byte[] base64ImageData;

	@Override
	public String getType() {
		return Strings.IMAGE_RESPONSE_MESSAGE;
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
		// a valid JSON message (doesn't add " " to byte[]).
		// Need to ensure we convert this back to a byte[] when received.
		String imageData = null;
		try {
			imageData = new String(this.base64ImageData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(
					"UTF-8 encoding unsupported for image data", e);
		}
		jsonMessage.put(Strings.DATA_JSON, imageData);

		return jsonMessage.toJSONString();
	}

	/**
	 * Constructs a byte array of image data from the serialized JSON.
	 *
	 * @param messageJson
	 *            The JSON content of the message to deserialise.
	 * @return The extracted image data.
	 */
	public static byte[] imageDataFromJson(String messageJson) {
		if (messageJson == null) {
			return null;
		}

		JSONParser parser = new JSONParser();
		JSONObject jsonMessage = null;

		try {
			jsonMessage = (JSONObject) parser.parse(messageJson);
			if (jsonMessage == null) {
				return null;
			}
		} catch (ParseException e) {
			throw new IllegalStateException(
					"Failed to parse image data of message: " + messageJson, e);
		}

		// Convert the image data from a JSON string to a byte[] and decode.
		byte[] imageData = null;
		try {
			imageData = ((String) jsonMessage.get(Strings.DATA_JSON))
					.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(
					"UTF-8 encoding not suppoted for image data.");
		}
		return Base64.decodeBase64(imageData);
	}

}

package simplestream.messages;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public abstract class Message {

	/**
	 * Message
	 * 
	 * This is the base Message class.
	 * 
	 * Please don't extend this class. Instead, extend either the RequestMessage or ResponseMessage
	 * classes.
	 * 
	 */
	protected final Logger log = Logger.getLogger(getClass());

	/**
	 * A message can be a request or a response message.
	 */
	public abstract String requestOrResponseMessage();

	/**
	 * Each Message has its own type
	 */
	public abstract String getType();


	/**
	 * This should be overridden if you want to send a non-standard message.
	 */
	public String toJSON() {

		return standardMessageJSON().toJSONString();
	}


	/**
	 * This method generates standard message JSON. Example: {"response":"stoppedstream"}
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected JSONObject standardMessageJSON() {

		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put(requestOrResponseMessage(), getType());
		return jsonMessage;
	}


}

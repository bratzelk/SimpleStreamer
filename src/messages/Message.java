package messages;

public abstract class Message {
	

	/**
	 * A message can be a request or a response message, this method returns the type
	 */
	public abstract String requestOrResponseMessage();
	
	public abstract String getType();
	
	public abstract String toJSON();
	
	
}

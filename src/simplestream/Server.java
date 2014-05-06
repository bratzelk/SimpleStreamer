package simplestream;

import java.io.IOException;

import messages.MessageNotFoundException;

public interface Server {

	public void waitForConnection() throws IOException, ClassNotFoundException, MessageNotFoundException;
	
}

package simplestream;

import java.io.IOException;

public interface Server {

	public void waitForConnection() throws IOException, ClassNotFoundException;

}

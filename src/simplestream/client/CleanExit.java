package simplestream.client;

import java.io.IOException;

import messages.MessageFactory;
import simplestream.StreamViewer;
import simplestream.networking.ConnectionBuffer;
import common.Strings;

/**
 * Ensures that a clean shutdown occurs when run. Designed to be passed to the {@link StreamViewer}
 * constructor.
 */
public class CleanExit implements Runnable {

	/** The server connection to close gracefully. */
	private final ConnectionBuffer buffer;

	public CleanExit(ConnectionBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * Perform the clean exit by sending a {@code stopstream} message to the server.
	 */
	@Override
	public void run() {
		try {
			buffer.send(MessageFactory.createMessage(Strings.STOP_REQUEST_MESSAGE));
		} catch (IOException e) {
			System.out.println("Failed to perform clean exit");
			e.printStackTrace();
		}
	}

}

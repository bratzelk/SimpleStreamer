package simplestream.server;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

import au.edu.unimelb.orlade.comp90015.filesync.ConnectionBuffer;
import au.edu.unimelb.orlade.comp90015.filesync.ConnectionBuffer.Response;
import au.edu.unimelb.orlade.comp90015.filesync.server.ConnectionListener;
import au.edu.unimelb.orlade.comp90015.filesync.server.ConnectionListener.Callback;

/**
 * Wraps and runs a {@link ConnectionListener} on a new thread to catch incoming connections.
 */
public class StreamServer {

	private final Logger log = Logger.getLogger(getClass());

	private ConnectionListener listener;

	/**
	 * The callback to invoke when a new client connection is received.
	 */
	private final Callback clientConnectionCallback = new Callback() {

		// /**
		// * Parses the {@link ConfigInstruction} from the client on the given socket.
		// *
		// * @throws IOException
		// */
		// private ConfigInstruction receiveConfig(final ConnectionBuffer directionBuffer)
		// throws IOException {
		// final String configJson = directionBuffer.receive();
		// log.debug("Received configuration " + configJson);
		// return (ConfigInstruction) new InstructionFactory().FromJSON(configJson);
		// }

		@Override
		public void onRequest(final Socket clientSocket) {
			try {
				ConnectionBuffer buffer = new ConnectionBuffer(clientSocket);
				// ConfigInstruction config = receiveConfig(directionBuffer);

				buffer.respond(Response.ACKNOWLEDGED);
			} catch (IOException e) {
				throw new IllegalStateException("Failed to connect with client", e);
			}
		}
	};

	public StreamServer(int streamingPort) {
		// Start the server in a new thread.
		// TODO: when you get a connection, start a new thread and send them messages.
		try {
			listener = new ConnectionListener(streamingPort, clientConnectionCallback);
			listener.start();
		} catch (IOException e) {
			log.error("Listen socket error", e);
		}
	}

}

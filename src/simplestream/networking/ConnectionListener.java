package simplestream.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Listens for a connection request.
 */
public class ConnectionListener extends Thread {

	Logger log = Logger.getLogger(getClass());

	/** The socket that receives incoming requests. */
	private ServerSocket socket;

	/** The callback to invoke when a request is received. */
	private Callback callback;

	/**
	 * Provides a mechanism of notifying a listener when a request is received.
	 */
	public static interface Callback {
		/**
		 * Invoked when a new client connection {@link Socket} is established.
		 *
		 * @param clientSocket The {@link Socket} established with the requesting client.
		 */
		public void onRequest(Socket clientSocket);
	}

	public ConnectionListener(final int port, Callback callback) throws IOException {
		this.socket = new ServerSocket(port);
		this.callback = callback;
	}

	/**
	 * Listens for incoming connections and hands new clients off the to {@link Callback}.
	 */
	public void run() {
		int i = 0;
		while (true) {
			if (socket.isClosed()) {
				log.debug("ConnectionListener socket is closed, stopping...");
				return;
			}

			try {
				log.debug("Server listening for connections on port " + socket.getLocalPort()
					+ "...");
				Socket clientSocket = socket.accept();
				log.debug(String.format("Received connection %d from %s", ++i,
					clientSocket.getRemoteSocketAddress()));
				callback.onRequest(clientSocket);
			} catch (IOException e) {
				log.error("Socket closed unexpectedly", e);
				return;
			}
		}
	}

	/**
	 * Stops and cleans up the {@link ConnectionListener}'s resources.
	 */
	public void kill() throws IOException {
		log.debug("Shutting down connection listener...");
		this.interrupt();
		socket.close();
	}

}

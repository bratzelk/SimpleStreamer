package simplestream.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Listens for a connection request.
 */
public class ConnectionListener extends Thread {

	Logger log = Logger.getLogger(getClass());

	private ServerSocket socket;
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

	public void run() {
		int i = 0;
		while (true) {
			Socket clientSocket;
			try {
				if (!socket.isClosed()) {
					log.debug("Server listening for connections on port " + socket.getLocalPort() + "...");
					clientSocket = socket.accept();
					i++;
					log.debug("Received connection " + i + " from " + clientSocket.getRemoteSocketAddress());
					callback.onRequest(clientSocket);
				}
			} catch (IOException e) {
				if (!socket.isClosed()) {
					// TODO(orlade): Handle more gracefully.
					log.error("Socket closed unexpectedly", e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void close() throws IOException {
		log.debug("Closing connection listener...");
		this.interrupt();
		socket.close();
	}

}

package simplestream.server;

import java.io.IOException;
import java.net.Socket;

import messages.Message;
import messages.MessageFactory;
import messages.MessageNotFoundException;
import messages.StatusResponseMessage;

import org.apache.log4j.Logger;

import simplestream.server.ConnectionListener.Callback;
import common.Strings;

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

		/**
		 * Handle new requests by responding with a {@link StatusResponseMessage}.
		 */
		@Override
		public void onRequest(final Socket clientSocket) {
			try {
				ConnectionBuffer buffer = new ConnectionBuffer(clientSocket);
				Message statusMessage = MessageFactory.createMessage(Strings.STATUS_RESONSE_MESSAGE);
				buffer.send(statusMessage.toJSON());
			} catch (IOException e) {
				throw new IllegalStateException("Failed to connect with client", e);
			} catch (MessageNotFoundException e) {
				throw new IllegalStateException("Unable to respond to new connection", e);
			}
		}
	};

	/**
	 * Wraps a connection listener on a new thread that
	 * @param streamingPort The port to stream
	 */
	public StreamServer(int streamingPort) {
		try {
			listener = new ConnectionListener(streamingPort, clientConnectionCallback);
			listener.start();
		} catch (IOException e) {
			log.error("Listen socket error", e);
		}
	}

}

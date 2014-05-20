package simplestream.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import messages.Message;
import messages.MessageFactory;
import messages.StatusResponseMessage;

import org.apache.log4j.Logger;

import simplestream.client.LocalWebcamStreamer;
import simplestream.client.WebcamStreamer;
import simplestream.server.ConnectionListener.Callback;

import common.Strings;

/**
 * Wraps and runs a {@link ConnectionListener} on a new thread to catch incoming connections. New
 * clients are delegated to be handled by individual {@link ClientHandler}s on new threads.
 */
public class StreamServer {

	private final Logger log = Logger.getLogger(getClass());

	/** Listens for new incoming connections. */
	private ConnectionListener listener;

	/** A stream from the webcam local to this host to be sent to remote clients. */
	private WebcamStreamer webcam;

	/** The collection of clients that have connected and are being serviced. */
	private Collection<ClientHandler> clients = new ArrayList<ClientHandler>();

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
				Message statusMessage =
								MessageFactory.createMessage(Strings.STATUS_RESONSE_MESSAGE);
				buffer.send(statusMessage.toJSON());
				serve(buffer);
			} catch (IOException e) {
				throw new IllegalStateException("Failed to connect with client", e);
			}
		}
	};

	/**
	 * Wraps a connection listener on a new thread that
	 *
	 * @param streamingPort The port to stream
	 */
	public StreamServer(final WebcamStreamer webcam, final int streamingRate,
					final int streamingPort) {
		this.webcam = webcam;
		try {
			listener = new ConnectionListener(streamingPort, clientConnectionCallback);
			listener.start();
		} catch (IOException e) {
			log.error("Listen socket error", e);
		}
	}

	/**
	 * Listens for and handles requests from a client represented by the {@link ConnectionBuffer}.
	 *
	 * @param buffer The connection to the client.
	 */
	protected void serve(ConnectionBuffer buffer) {
		log.debug("Serving client " + buffer);
		ClientHandler client = new ClientHandler(buffer, webcam);
		clients.add(client);
	}

	public void stop() {
		for (ClientHandler client : clients) {
			client.stop();
		}
	}

}

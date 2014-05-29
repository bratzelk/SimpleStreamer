package simplestream.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import simplestream.common.Settings;
import simplestream.common.Strings;
import simplestream.messages.Message;
import simplestream.messages.MessageFactory;
import simplestream.messages.OverloadedResponseMessage;
import simplestream.messages.StatusResponseMessage;
import simplestream.networking.ConnectionBuffer;
import simplestream.networking.ConnectionListener;
import simplestream.networking.ConnectionListener.Callback;
import simplestream.networking.Peer;
import simplestream.webcam.Webcam;

/**
 * Wraps and runs a {@link ConnectionListener} on a new thread to catch incoming connections. New
 * clients are delegated to be handled by individual {@link ClientHandler}s on new threads.
 */
public class StreamServer {

	private final Logger log = Logger.getLogger(getClass());

	/** Listens for new incoming connections. */
	private ConnectionListener listener;

	/** A stream from the current webcam this host is streaming from (local or remote). */
	private final Webcam webcam;

	/** The rate to display localWebcam images at. */
	private final int streamingRate;

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
				log.info("Received connection, connected on " + buffer);
				Message statusMessage =
					MessageFactory.createMessage(Strings.STATUS_RESONSE_MESSAGE);
				buffer.send(statusMessage.toJSON());

				// If we have reached the maximum number of allowed connections
				// then we send an overloaded response message.
				if (clients.size() < Settings.MAX_CONNECTIONS) {
					serve(buffer);
				} else {
					doHandover(buffer);
				}
			} catch (IOException e) {
				throw new IllegalStateException("Failed to connect with client", e);
			}
		}
	};

	/**
	 * Responds to a client attempting to connect to an overloaded server with the list of existing
	 * clients streaming from the same server.
	 *
	 * @param buffer The {@link ConnectionBuffer} to the prospective client.
	 * @throws IOException
	 */
	protected void doHandover(ConnectionBuffer buffer) throws IOException {
		OverloadedResponseMessage overloadedResponse =
			(OverloadedResponseMessage) MessageFactory
				.createMessage(Strings.OVERLOADED_RESPONSE_MESSAGE);

		// TODO
		// add the server if in remote mode
		// overloadedResponse.addServer(connctedServer);

		// add the connected clients
		for (ClientHandler client : clients) {
			Peer servingPeer = new Peer(client.getPeer().getHostname(), client.getSport());
			// Add client peer to message.
			overloadedResponse.addClient(servingPeer);
		}
		buffer.send((Message) overloadedResponse);

		log.info("Sending an overloaded response message.");
	}

	/**
	 * Wraps a connection listener on a new thread that
	 *
	 * @param streamingPort The port to stream
	 */
	public StreamServer(final Webcam webcam, final int streamingRate, final int streamingPort) {
		this.webcam = webcam;
		this.streamingRate = streamingRate;
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
		log.info("Serving client on " + buffer);
		final ClientHandler client = new ClientHandler(buffer, webcam, streamingRate);

		client.setShutdownCallback(new Runnable() {
			@Override
			public void run() {
				clients.remove(client);
			}
		});

		client.run();
		clients.add(client);
	}

	/**
	 * Stops and cleans up the server's resources.
	 */
	public void kill() {
		log.debug("Shutting down StreamServer...");
		try {
			listener.kill();
		} catch (IOException e) {
			log.error("Error while shutting down connection listener", e);
		}
		for (ClientHandler client : clients) {
			client.kill();
		}
		log.debug("StreamServer shut down");
	}
}

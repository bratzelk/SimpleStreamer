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
import simplestream.messages.StatusResponseMessage;
import simplestream.networking.ConnectionBuffer;
import simplestream.networking.ConnectionListener;
import simplestream.networking.Peer;
import simplestream.networking.ConnectionListener.Callback;
import simplestream.webcam.LocalWebcam;

/**
 * Wraps and runs a {@link ConnectionListener} on a new thread to catch incoming connections. New
 * clients are delegated to be handled by individual {@link ClientHandler}s on new threads.
 */
public class StreamServer {

	private final Logger log = Logger.getLogger(getClass());

	/** Listens for new incoming connections. */
	private ConnectionListener listener;

	/** A stream from the localWebcam local to this host to be sent to remote clients. */
	private LocalWebcam localWebcam;

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
				Message statusMessage =
					MessageFactory.createMessage(Strings.STATUS_RESONSE_MESSAGE);
				buffer.send(statusMessage.toJSON());

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
		// TODO(orlade): Create the handover message by serializing the current clients.
		Message handoverMessage = null; // = create...
		for (ClientHandler client : clients) {
			Peer peer = client.getPeer();
			// Add to message...
		}
		buffer.send(handoverMessage);
	}

	/**
	 * Wraps a connection listener on a new thread that
	 *
	 * @param streamingPort The port to stream
	 */
	public StreamServer(final LocalWebcam localWebcam, final int streamingRate, final int streamingPort) {
		this.localWebcam = localWebcam;
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
		log.debug("Serving client " + buffer);
		ClientHandler client = new ClientHandler(buffer, localWebcam, streamingRate);
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

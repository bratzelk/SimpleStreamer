package simplestream.webcam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import simplestream.common.Strings;
import simplestream.messages.ImageResponseMessage;
import simplestream.messages.Message;
import simplestream.messages.MessageFactory;
import simplestream.messages.OverloadedResponseMessage;
import simplestream.messages.StartRequestMessage;
import simplestream.networking.Compressor;
import simplestream.networking.ConnectionBuffer;
import simplestream.networking.Peer;
import simplestream.server.BFSPeerIterator;
import simplestream.server.NoUnseenPeersException;

/**
 * Treats a remote host like a webcam and returns the images received over the
 * network on demand.
 */
public class RemoteWebcam implements Webcam {

	private final Logger log = Logger.getLogger(getClass());

	/** The latest image frame to have been received. */
	private byte[] currentFrame;

	/** The rate at which to request image data be sent. */
	private final int streamingRate;

	/** The port that this instance is streaming on. */
	private final int streamingPort;

	/** The details of the remote host streaming the images. */
	private Peer peer;

	/**
	 * The connection to the remote host for sending and receiving
	 * {@link Message}s.
	 */
	private ConnectionBuffer buffer;

	/** The thread that is listening for new image data {@link Message}s. */
	private Thread listenThread;

	private BFSPeerIterator peerIterator;

	/**
	 * Creates a new {@link RemoteWebcam} and connects it to the given remote
	 * host.
	 */
	public RemoteWebcam(int streamingRate, int streamingPort,
			String remoteHostname, int remotePort) {
		this.streamingRate = streamingRate;
		this.streamingPort = streamingPort;
		this.peer = new Peer(remoteHostname, remotePort);

		this.peerIterator = new BFSPeerIterator();

		init();
	}

	/**
	 * Connects to the saved remote host and starts listening for image data.
	 */
	protected void init() {
		buffer = connect();
		listen();
	}

	/**
	 * Establish a connection with the peer.
	 *
	 * @return A {@link ConnectionBuffer} to the connected {@link Peer}.
	 */
	protected ConnectionBuffer connect() {
		try {
			ConnectionBuffer buffer = ConnectionBuffer.bind(peer);

			@SuppressWarnings("unused")
			String statusMessage = buffer.receive();
			// TODO: Check contents of status message, not currently used.

			log.info("Connected to remote host " + buffer);
			return buffer;
		} catch (IOException e) {
			throw new IllegalStateException(
					"Unable to connect to remote peer: " + peer);
		}
	}

	/**
	 * Sends a request to the remote peer to start streaming image data.
	 */
	protected void startStreaming() {
		StartRequestMessage startMessage = (StartRequestMessage) MessageFactory
				.createMessage(Strings.START_REQUEST_MESSAGE);

		startMessage.setRatelimit(streamingRate);
		startMessage.setServerPort(streamingPort);

		log.info("Requesting start stream (rate: " + streamingRate + ") to "
				+ buffer + "...");

		try {
			buffer.send(startMessage);
		} catch (IOException e) {
			log.error("Failed to send start message to " + buffer, e);
		}
	}

	/**
	 * Tries connecting to one of the other hosts being served by the overloaded
	 * remote peer.
	 */
	protected void followHandover(Collection<Peer> alternativeHosts) {

		log.debug("Finding the next available peer...");

		peerIterator.addPeers(alternativeHosts);

		Peer newServer = null;
		try {
			newServer = peerIterator.getNextPeer();
		} catch (NoUnseenPeersException e) {
			log.error("No potential servers left to try...");
			throw new IllegalStateException("Could not follow handover for "
					+ peer + ": no available peers");
		}

		log.debug("Available peer found: " + newServer
				+ ". Attempting to connect...");

		// Restart connecting to the alternative host.
		this.peer = newServer;
		init();
	}

	/**
	 * Sends a request to the remote peer to stop streaming image data.
	 *
	 * @throws IOException
	 *             if the message cannot be sent.
	 */
	public void stopStreaming() throws IOException {
		buffer.send(MessageFactory.createMessage(Strings.STOP_REQUEST_MESSAGE));
	}

	/**
	 * Runs a thread that listens for image data messages on the local
	 * {@link ConnectionBuffer} from the remote peer and saves them they are
	 * received.
	 */
	protected void listen() {
		// Set up the local stream listener.
		listenThread = new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("Listening for remote image data on " + buffer + "...");
				while (true) {
					// Wait for a message from the remote peer.
					String response;
					try {
						response = buffer.receive();
					} catch (IOException e) {
						log.error("Connection with " + peer + " interrupted", e);
						return;
					}

					log.debug("Got message on " + buffer);

					// If the message was an image response message, save it as
					// the current frame.
					String messageType = MessageFactory
							.getMessageType(response);
					if (messageType == null) {
						throw new IllegalStateException(
								"Received null message type");
					}
					switch (messageType) {
					case Strings.IMAGE_RESPONSE_MESSAGE:
						handleImageMessage(response);
						break;
					case Strings.STOPPED_RESPONSE_MESSAGE:
						log.debug(this
								+ " peer acknowledged stop request, shutting down...");
						kill();
						return;
					case Strings.OVERLOADED_RESPONSE_MESSAGE:
						OverloadedResponseMessage overloadedMessage = (OverloadedResponseMessage) MessageFactory
								.createMessage(Strings.OVERLOADED_RESPONSE_MESSAGE);
						overloadedMessage.populateFieldsFromJSON(response);
						log.debug("Remote peer " + buffer
								+ " overloaded, performing handover... ("
								+ overloadedMessage + ")");

						// build up a lost of alternative peers we can connect
						// to.
						Collection<Peer> alternativeHosts = new ArrayList<Peer>();
						// Add the streaming server first (if it exists).
						if (overloadedMessage.inRemoteMode()) {
							alternativeHosts.add(overloadedMessage.getServer());
						}
						// Add the connected clients.
						alternativeHosts = overloadedMessage.getClients();

						followHandover(alternativeHosts);
						listenThread.interrupt();
						break;
					}
				}
			}
		});
		listenThread.start();

		// Start streaming data from the remote host.
		startStreaming();
	}

	/**
	 * Handles the content of an image response message, saving the image data
	 * as the current frame.
	 *
	 * @param messageJson
	 *            The JSON content of the message.
	 */
	protected void handleImageMessage(String messageJson) {
		byte[] compressedImageData = ImageResponseMessage
				.imageDataFromJson(messageJson);
		byte[] decompressedImageData = Compressor
				.decompress(compressedImageData);
		currentFrame = decompressedImageData;
	}

	/**
	 * Returns the latest image that has been received from the remote webcam.
	 */
	@Override
	public byte[] getImage() {
		return currentFrame;
	}

	@Override
	public void kill() {
		log.debug("Shutting down " + this + "...");
		try {
			stopStreaming();
		} catch (IOException e) {
			log.error("Failed to stop streaming", e);
		}

		listenThread.interrupt();
		try {
			buffer.kill();
		} catch (IOException e) {
			log.error("Error shutting down " + buffer, e);
		}
		log.debug(this + " shut down successful");
	}

	public Peer getPeer() {
		return peer;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + peer + "]";
	}

}

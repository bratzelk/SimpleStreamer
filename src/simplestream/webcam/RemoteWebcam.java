package simplestream.webcam;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;

import simplestream.common.Strings;
import simplestream.messages.ImageResponseMessage;
import simplestream.messages.MessageFactory;
import simplestream.messages.OverloadedResponseMessage;
import simplestream.messages.StartRequestMessage;
import simplestream.networking.Compressor;
import simplestream.networking.ConnectionBuffer;
import simplestream.networking.Peer;
import simplestream.server.BFSPeerIterator;
import simplestream.server.NoUnseenPeersException;

/**
 * Treats a remote host like a webcam and returns the images received over the network on demand.
 */
public class RemoteWebcam implements Webcam {

	private final Logger log = Logger.getLogger(getClass());

	/** The latest image frame to have been received. */
	private byte[] currentFrame;

	private int streamingRate;

	/** The details of the remote host streaming the images. */
	private Peer peer;

	/** The connection to the remote host for sending and receiving simplestream.messages. */
	private ConnectionBuffer buffer;

	/** The thread that is listening for new image data simplestream.messages. */
	private Thread listenThread;

	private BFSPeerIterator peerIterator;

	public RemoteWebcam(int streamingRate, String remoteHostname, int remotePort) {
		this.streamingRate = streamingRate;
		this.peer = new Peer(remoteHostname, remotePort);

		this.peerIterator = new BFSPeerIterator();

		buffer = connect();
		listen();
	}

	/**
	 * Establish a connection with the peer.
	 */
	protected ConnectionBuffer connect() {
		// This needs to be added to the overloadedMessage.
		try {
			ConnectionBuffer buffer = ConnectionBuffer.bind(peer);
			String statusMessage = buffer.receive();
			log.info("Connected to remove host " + buffer);
			// TODO(orlade): Check status, perform any further setup.
			return buffer;
		} catch (IOException e) {
			throw new IllegalStateException("Unable to connect to remote peer: " + peer);
		}
	}

	/**
	 * Sends a request to the remote peer to start streaming image data.
	 */
	protected void startStreaming() {
		StartRequestMessage startMessage =
			(StartRequestMessage) MessageFactory.createMessage(Strings.START_REQUEST_MESSAGE);
		// TODO : Verify that these are correct (Is that the right port to use here?).
		startMessage.setRatelimit(streamingRate);
		startMessage.setServerPort(peer.getPort());

		log.info("Requesting start stream to " + buffer + "...");
		String response = buffer.sendAndReceive(startMessage);
	}

	/**
	 * Tries connecting to one of the other hosts being served by the overloaded remote peer.
	 */
	protected void followHandover(Collection<Peer> alternativeHosts) {

		log.debug("Finding the next available Peer...");

		// TODO(kim): Handle the overloaded response message here.
		peerIterator.addPeers(alternativeHosts);

		Peer newServer = null;
		try {
			newServer = peerIterator.getNextPeer();
		} catch (NoUnseenPeersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug("No potential servers left to try...");
		}

		log.debug("Available Peer found: " + newServer + ". Trying to connect...");

		// TODO I'm pretty sure this isn't nice...
		// Start the whole process again
		this.peer = newServer;
		buffer = connect();
		listen();
	}

	/**
	 * Sends a request to the remote peer to stop streaming image data.
	 *
	 * @throws IOException
	 */
	public void stopStreaming() throws IOException {
		buffer.send(MessageFactory.createMessage(Strings.STOP_REQUEST_MESSAGE));
	}

	/**
	 * Runs a thread that listens for image data messages on the local {@link ConnectionBuffer} from
	 * the remote peer and saves them they are received.
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

					log.info("Got message on " + buffer + ": " + response);

					
					// If the message was an image response message, save it as the current frame.
					String messageType = MessageFactory.getMessageType(response);
					if (messageType == null) {
						throw new IllegalStateException("Received null message type");
					}
					switch (messageType) {
						case Strings.IMAGE_RESPONSE_MESSAGE:
							handleImageMessage(response);
							break;
						case Strings.STOPPED_RESPONSE_MESSAGE:
							log.debug(this + " peer acknowledged stop request, shutting down...");
							kill();
							return;
						case Strings.OVERLOADED_RESPONSE_MESSAGE:
							OverloadedResponseMessage overloadedMessage =
								(OverloadedResponseMessage) MessageFactory
									.createMessage(Strings.OVERLOADED_RESPONSE_MESSAGE);// response;
							overloadedMessage.populateFieldsFromJSON(response);
							log.debug("Remote peer " + buffer
								+ " overloaded, performing handover... (" + overloadedMessage + ")");
							Collection<Peer> alternativeHosts = overloadedMessage.getClients();
							if (overloadedMessage.inRemoteMode()) {
								alternativeHosts.add(overloadedMessage.getServer());
							}
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
	 * Handles the content of an image response message, saving the image data as the current frame.
	 *
	 * @param messageJson The JSON content of the message.
	 */
	protected void handleImageMessage(String messageJson) {
		byte[] compressedImageData = ImageResponseMessage.imageDataFromJson(messageJson);
		byte[] decompressedImageData = Compressor.decompress(compressedImageData);
		setCurrentFrame(decompressedImageData);
	}

	/**
	 * Returns the latest image that has been received from the remote webcam.
	 */
	@Override
	public byte[] getImage() {
		return getCurrentFrame();
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

	public synchronized byte[] getCurrentFrame() {
		return currentFrame;
	}

	public synchronized void setCurrentFrame(byte[] currentFrame) {
		this.currentFrame = currentFrame;
	}

	public Peer getPeer() {
		return peer;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + peer + "]";
	}

}

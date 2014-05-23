package simplestream.webcam;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;

import messages.ImageResponseMessage;
import messages.Message;
import messages.MessageFactory;
import messages.StartRequestMessage;
import simplestream.Compressor;
import simplestream.Peer;
import simplestream.networking.ConnectionBuffer;
import common.Strings;

/**
 * Treats a remote host like a webcam and returns the images received over the network on demand.
 */
public class RemoteWebcam implements Webcam {

	private final Logger log = Logger.getLogger(getClass());

	/** The latest image frame to have been received. */
	private byte[] currentFrame;

	private int streamingRate;

	/** The details of the remote host streaming the images. */
	private final Peer peer;

	/** The connection to the remote host for sending and receiving messages. */
	private final ConnectionBuffer buffer;

	/** The thread that is listening for new image data messages. */
	private Thread listenThread;

	public RemoteWebcam(int streamingRate, String remoteHostname, int remotePort) {
		this.streamingRate = streamingRate;
		this.peer = new Peer(remoteHostname, remotePort);

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

		String response = buffer.sendAndReceive(startMessage);

		Message responseMessage =
			MessageFactory.createMessage(MessageFactory.getMessageType(response));
		String responseMessageType = responseMessage.getType();
		// Handle the overloaded response message.
		if (responseMessageType.equals(Strings.OVERLOADED_RESPONSE_MESSAGE)) {
			Collection<Peer> alternativeHosts = null; // TODO: Extract this from the message.
			followHandover(alternativeHosts);
		}
		// Otherwise the responseMessageType should be "startingstream".
	}

	/**
	 * Tries connecting to one of the other hosts being served by the overloaded remote peer.
	 */
	protected void followHandover(Collection<Peer> alternativeHosts) {
		// TODO(kim): Handle the overloaded response message here.
	}

	/**
	 * Sends a request to the remote peer to stop streaming image data.
	 */
	protected void stopStreaming() {
		try {
			buffer.send(MessageFactory.createMessage(Strings.STOP_REQUEST_MESSAGE));
		} catch (IOException e) {
			log.error("Failed to perform clean exit", e);
		}
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
				log.debug("Listening for remote image data...");
				while (true) {
					// Wait for a message from the remote peer.
					String response;
					try {
						response = buffer.receive();
					} catch (IOException e) {
						log.error("Connection with " + peer + " interrupted", e);
						return;
					}

					// If the message was an image response message, save it as the current frame.
					if (MessageFactory.getMessageType(response).equals(
						Strings.IMAGE_RESONSE_MESSAGE)) {
						byte[] compressedImageData =
							ImageResponseMessage.imageDataFromJson(response);
						byte[] decompressedImageData = Compressor.decompress(compressedImageData);
						setCurrentFrame(decompressedImageData);
					}
				}
			}
		});
		listenThread.start();

		// Start streaming data from the remote host.
		startStreaming();
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
		listenThread.interrupt();
		stopStreaming();
		log.debug(this + " shut down successfull");
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

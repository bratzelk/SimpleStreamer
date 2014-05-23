package simplestream.server;

import java.io.IOException;

import org.apache.log4j.Logger;

import simplestream.client.StreamClient;
import simplestream.common.Strings;
import simplestream.messages.ImageResponseMessage;
import simplestream.messages.Message;
import simplestream.messages.MessageFactory;
import simplestream.networking.Compressor;
import simplestream.networking.ConnectionBuffer;
import simplestream.networking.Peer;
import simplestream.webcam.LocalWebcam;

/**
 * Services a single {@link StreamClient} by responding to requests and streaming localWebcam data
 * when applicable.
 *
 * New {@link ClientHandler}s start themselves on their own thread, so calling {@link #kill()} will
 * stop the handler gracefully and clean up all necessary resources.
 */
public class ClientHandler implements Runnable {

	private final Logger log = Logger.getLogger(getClass());

	/** The connection to the client. */
	private final ConnectionBuffer buffer;

	/** The local localWebcam to send images from. */
	private final LocalWebcam localWebcam;

	/** The rate to display localWebcam images at. */
	private final int streamingRate;

	/** Whether we are streaming images to the client. */
	private boolean streaming = false;

	/** The thread listening for stop request messages. */
	private Thread stopThread;

	/** The thread sending image data. */
	private Thread sendThread;

	public ClientHandler(ConnectionBuffer buffer, LocalWebcam localWebcam, int streamingRate) {
		this.buffer = buffer;
		this.localWebcam = localWebcam;
		this.streamingRate = streamingRate;
	}

	/**
	 * Performs the main loop of listening for requests from the client and streaming the local
	 * localWebcam.
	 */
	@Override
	public void run() {
		log.debug("Running client handler...");
		while (!streaming) {
			try {
				log.debug("Listening for startstream request...");
				String request = buffer.receive();
				if (MessageFactory.getMessageType(request).equals(Strings.START_REQUEST_MESSAGE)) {
					// TODO(orlade): Parse ratelimit argument of startstream message.
					// int requestLimit = message.get(Strings.RATELIMIT_JSON);
					//
					log.debug("Received startstream request from " + buffer);
					streaming = true;
					buffer.send(MessageFactory.createMessage(Strings.START_RESONSE_MESSAGE));
				}
			} catch (IOException e) {
				throw new RuntimeException("Error listening for request", e);
			}
		}
		log.debug("Transitioning to sending image data...");

		// Set up a listener for stopstream messages.
		listenForStop();
		loopSend();
	}

	/**
	 * Starts a thread to continually send a stream of images from the local webcam.
	 */
	protected void loopSend() {
		sendThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					// TODO(orlade): Listen for incoming requests.
					try {
						log.debug("Sending image data...");
						buffer.send(buildImageMessage());
					} catch (IOException e) {
						log.error("Error retrieving localWebcam image for " + buffer);
						log.error("Error was " + e);
					}

					// TODO(orlade): Allow for a streaming rate different from the local localWebcam
					// (i.e. implement client rate limiting)
					try {
						Thread.sleep(streamingRate);
					} catch (InterruptedException e) {
						log.error("Client handler was interrupted", e);
						return;
					}
				}
			}
		});
		sendThread.start();
	}

	/**
	 * Spins up a thread to listen for stop requests. If one is received, stops streaming image data
	 * to the client.
	 */
	protected void listenForStop() {
		stopThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String request = buffer.receive();
						String messageType = MessageFactory.getMessageType(request);
						if (Strings.STOP_REQUEST_MESSAGE.equals(messageType)) {
							log.debug("Received stop requset from " + buffer.getPeer());
							acknowledgeStop();
							return;
						}
					} catch (IOException e) {
						log.error("Error while listening for stop request", e);
						return;
					}
				}
			}
		});
		stopThread.start();
	}

	/**
	 * Constructs an {@link ImageResponseMessage} to send to the client.
	 *
	 * @return The constructed message.
	 */
	protected Message buildImageMessage() {
		ImageResponseMessage message =
			(ImageResponseMessage) MessageFactory.createMessage(Strings.IMAGE_RESPONSE_MESSAGE);

		// get the localWebcam image data and compress it
		byte[] imageData = localWebcam.getImage();
		log.debug("Compressing data: " + imageData);
		byte[] compressedImageData = Compressor.compress(imageData);
		message.setImageData(compressedImageData);
		return message;
	}

	/**
	 * Notifies the client that the request to stop streaming has been received and actioned.
	 */
	protected void acknowledgeStop() throws IOException {
		buffer.send(MessageFactory.createMessage(Strings.STOPPED_RESPONSE_MESSAGE));
		kill();
	}

	/**
	 * Stops and cleans up the client handler's resources.
	 */
	public void kill() {
		log.debug("Shutting down ClientHandler for " + getPeer() + "...");
		if (stopThread != null) {
			stopThread.interrupt();
		}
		if (sendThread != null) {
			sendThread.interrupt();
		}
		try {
			buffer.kill();
		} catch (IOException e) {
			log.error("Error while killing buffer", e);
		}
		log.debug("ClientHandler for " + getPeer() + " shut down successfully");
	}

	/**
	 * Returns the {@link Peer} of the client's {@link ConnectionBuffer}.
	 */
	public Peer getPeer() {
		return buffer.getPeer();
	}

}

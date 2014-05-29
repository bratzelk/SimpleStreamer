package simplestream.server;

import java.io.IOException;

import org.apache.log4j.Logger;

import simplestream.client.StreamClient;
import simplestream.common.Strings;
import simplestream.messages.ImageResponseMessage;
import simplestream.messages.Message;
import simplestream.messages.MessageFactory;
import simplestream.messages.StartRequestMessage;
import simplestream.networking.Compressor;
import simplestream.networking.ConnectionBuffer;
import simplestream.networking.Peer;
import simplestream.webcam.Webcam;

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
	private final Webcam webcam;

	/** The rate to display localWebcam images at. */
	private int streamingRate;

	/** The port on which the client is serving its own data. */
	private int sport;

	/** Whether we are streaming images to the client. */
	private boolean streaming = false;

	/** The thread listening for stop request messages. */
	private Thread stopThread;

	/** The thread sending image data. */
	private Thread sendThread;

	/** The callback to run when the client is shut down. */
	private Runnable shutdownCallback;

	public ClientHandler(ConnectionBuffer buffer, Webcam webcam, int streamingRate) {
		this.buffer = buffer;
		this.webcam = webcam;
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
					StartRequestMessage message =
						(StartRequestMessage) MessageFactory
							.createMessage(Strings.START_REQUEST_MESSAGE);
					message.populateFieldsFromJSON(request);

					// Read out the port the client is serving on and the rate to serve at.
					setSport(message.getServerPort());
					streamingRate = Math.max(message.getRatelimit(), 100);
					log.info("Received startstream request (sport: " + getSport() + ", rate: "
						+ streamingRate + ") from " + buffer);
					streaming = true;
					buffer.send(MessageFactory.createMessage(Strings.START_RESONSE_MESSAGE));
					log.info("Sent status response to " + buffer);
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
				log.info("Starting data send loop to " + buffer);
				while (true) {
					// TODO(orlade): Listen for incoming requests.
					try {
						log.info("Sending image data on " + buffer + "...");
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
							log.info("Received stop request from " + buffer.getPeer());
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
		byte[] imageData = webcam.getImage();
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
		if (shutdownCallback != null) {
			shutdownCallback.run();
		}
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

	/**
	 * Returns the hostname of the connected client.
	 */
	public String getHostname() {
		return buffer.getPeer().getHostname();
	}

	/**
	 * Returns the port on which the client is serving its data.
	 */
	public int getSport() {
		return sport;
	}

	public void setSport(int sport) {
		this.sport = sport;
	}

	public void setShutdownCallback(Runnable callback) {
		this.shutdownCallback = callback;
	}
}

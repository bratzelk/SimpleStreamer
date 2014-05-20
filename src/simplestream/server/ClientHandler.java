package simplestream.server;

import java.io.IOException;

import messages.ImageResponseMessage;
import messages.Message;
import messages.MessageFactory;

import org.apache.log4j.Logger;

import simplestream.Compressor;
import simplestream.Peer;
import simplestream.client.StreamClient;
import simplestream.client.WebcamStreamer;
import common.Strings;

/**
 * Services a single {@link StreamClient} by responding to requests and streaming webcam data when
 * applicable.
 *
 * New {@link ClientHandler}s start themselves on their own thread, so calling {@link #stop()} will
 * stop the handler gracefully and clean up all necessary resources.
 */
public class ClientHandler implements Runnable {

	private final Logger log = Logger.getLogger(getClass());

	/** The connection to the client. */
	private final ConnectionBuffer buffer;
	

	/** The local webcam to send images from. */
	private final WebcamStreamer webcam;

	/** Whether we are streaming images to the client. */
	private boolean streaming = false;

	/** The thread of control running this {@link ClientHandler}. */
	private final Thread thread;

	public ClientHandler(ConnectionBuffer buffer, WebcamStreamer webcam) {
		this.buffer = buffer;
		this.webcam = webcam;

		log.debug("setting up thread...");
		thread = new Thread(this);
		thread.start();
		log.debug("Started thread...");
	}

	/**
	 * Performs the main loop of listening for requests from the client and streaming the local
	 * webcam.
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

		while (true) {
			// TODO(orlade): Listen for incoming requests.
			try {
				log.debug("Sending image data...");
				buffer.send(buildImageMessage());
			} catch (IOException e) {
				log.error("Error retrieving webcam image for " + buffer);
			}

			// TODO(orlade): Allow for a streaming rate different from the local webcam
			// (i.e. implement client rate limiting)
			try {
				Thread.sleep(webcam.getStreamingRate());
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException("Webcam streamer was interrupted.");
			}
		}
	}

	/**
	 * Constructs an {@link ImageResponseMessage} to send to the client.
	 *
	 * @return The constructed message.
	 */
	protected Message buildImageMessage() {
		ImageResponseMessage message =
						(ImageResponseMessage) MessageFactory
										.createMessage(Strings.IMAGE_RESONSE_MESSAGE);
		
		//get the webcam image data and compress it
		byte[] imageData = webcam.getFrame();
		byte[] compressedImageData = Compressor.compress(imageData);
		message.setImageData(compressedImageData);
		return message;
	}

	public void stop() {

	}

	public Peer getPeer() {
		return buffer.getPeer();
	}

}

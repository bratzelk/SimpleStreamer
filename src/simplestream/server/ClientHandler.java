package simplestream.server;

import java.io.IOException;

import messages.ImageResponseMessage;
import messages.Message;
import messages.MessageFactory;

import org.apache.log4j.Logger;

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

	/** The thread of control running this {@link ClientHandler}. */
	private final Thread thread;

	public ClientHandler(ConnectionBuffer buffer, WebcamStreamer webcam) {
		this.buffer = buffer;
		this.webcam = webcam;

		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Performs the main loop of listening for requests from the client and streaming the local
	 * webcam.
	 */
	@Override
	public void run() {
		while (true) {
			// TODO(orlade): Listen for incoming requests.
			try {
				log.debug("Sending data");
				buffer.send(buildImageMessage());
			} catch (IOException e) {
				log.error("Error retrieving webcam image for " + buffer);
			}

			// TODO(orlade): Allow for different streaming rate from local webcam?
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
		message.setImageData(webcam.getFrame());
		return message;
	}

	public void stop() {

	}

}

package simplestream.client;

import java.io.IOException;

import messages.ImageResponseMessage;
import messages.Message;
import messages.MessageFactory;
import messages.StartRequestMessage;
import simplestream.Compressor;
import simplestream.Peer;
import simplestream.networking.ConnectionBuffer;
import common.Out;
import common.Strings;

/**
 * Displays a webcam stream from a remote camera.
 */
public class RemoteWebcamStreamer extends WebcamStreamerImpl {

	private final String remoteHostname;
	private final int remotePort;

	private ConnectionBuffer buffer;

	public RemoteWebcamStreamer(int streamingRate, String remoteHostname, int remotePort) {
		super(null, streamingRate);
		this.remoteHostname = remoteHostname;
		this.remotePort = remotePort;
	}

	@Override
	public void run() {
		log.debug("Starting remote webcam stream...");

		// This needs to be added to the overloadedMessage.
		Peer remoteServer = new Peer(remoteHostname, remotePort);
		try {
			buffer = ConnectionBuffer.bind(remoteServer);
			// Provide a callback to perform a clean exit to the remote host.
			getViewer().setExitCallback(new CleanExit(buffer));
			String statusMessage = buffer.receive();
			// TODO(orlade): Check status.
		} catch (IOException e) {
			throw new IllegalStateException("Unable to connect to remote host: " + remoteServer);
		}
		Out.print("Receiving remote webcam stream");

		// Start streaming from the remote host.
		StartRequestMessage startMessage =
			(StartRequestMessage) MessageFactory.createMessage(Strings.START_REQUEST_MESSAGE);

		// TODO : Verify that these are correct (Is that the right port to use here?).
		startMessage.setRatelimit(streamingRate);
		startMessage.setServerPort(remotePort);

		String response = buffer.sendAndReceive(startMessage);

		Message responseMessage =
			MessageFactory.createMessage(MessageFactory.getMessageType(response));
		String responseMessageType = responseMessage.getType();

		// Handle the overloaded response message.
		if (responseMessageType.equals(Strings.OVERLOADED_RESPONSE_MESSAGE)) {
			// TODO(kim): Handle the overloaded response message here;
		}
		// Otherwise the responseMessageType should be "startingstream".

		try {
			listen();
		} catch (IOException e) {
			log.error("Connection problem with remote host: " + remoteServer, e);
		}

		// // TODO: This is an example message
		// OverloadedResponseMessage overloadedMessage =
		// (OverloadedResponseMessage) MessageFactory
		// .createMessage(Strings.OVERLOADED_RESPONSE_MESSAGE);
		// overloadedMessage.addServer(remoteServer);
		// // overloadedMessage.addClients(clients);
		// Out.print(overloadedMessage.toJSON());
	}

	/**
	 * Waits and receives streaming messages from the remote host.
	 *
	 * @throws IOException
	 */
	protected void listen() throws IOException {
		log.debug("Listening to responses forever!");
		while (true) {
			String response = buffer.receive();
			// Check if we got an image response message
			if (MessageFactory.getMessageType(response).equals(Strings.IMAGE_RESONSE_MESSAGE)) {
				handleImageResponseMessage(response);
			}
		}
	}

	/**
	 * Handles a ImageResponseMessage which is received in JSON format by decompressing and
	 * displaying the image data.
	 *
	 * @param json The content of the {@link ImageResponseMessage}.
	 */
	protected void handleImageResponseMessage(String json) {
		byte[] compressedImageData = ImageResponseMessage.imagedataFromJson(json);
		byte[] decompressedImageData = Compressor.decompress(compressedImageData);
		displayFrame(decompressedImageData);
	}

}

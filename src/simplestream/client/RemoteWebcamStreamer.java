package simplestream.client;

import java.io.IOException;

import messages.Message;
import messages.MessageFactory;
import simplestream.Peer;
import simplestream.server.ConnectionBuffer;

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
			String statusMessage = buffer.receive();
			// TODO(orlade): Check status.
		} catch (IOException e) {
			throw new IllegalStateException("Unable to connect to remote host: " + remoteServer);
		}
		Out.print("Receiving remote webcam stream");

		// Start streaming from the remote host.
		Message startMessage = MessageFactory.createMessage(Strings.START_REQUEST_MESSAGE);
		String response = buffer.sendAndReceive(startMessage);

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

		// TODO: The messages themselves don't compress any byte arrays.
		// You need to do this explicitly before adding the data to a message.

		// TODO: display remote stream.
	}

	/**
	 * Waits and receives streaming messages from the remote host.
	 *
	 * @throws IOException
	 */
	protected void listen() throws IOException {
		while (true) {
			String incoming = buffer.receive();
		}
	}

}

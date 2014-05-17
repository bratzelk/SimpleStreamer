package simplestream.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import messages.Message;
import messages.MessageFactory;
import messages.OverloadedResponseMessage;
import messages.StartRequestMessage;

import org.apache.log4j.Logger;

import simplestream.Peer;
import simplestream.server.ConnectionBuffer;
import common.Out;
import common.Strings;

/**
 * Displays a webcam stream from a remote camera.
 */
public class RemoteWebcamStreamer extends LocalWebcamStreamer {

	Logger log = Logger.getLogger(getClass());

	private final String remoteHostname;
	private final int remotePort;

	private byte[] currentFrame;

	private ConnectionBuffer buffer;

	public RemoteWebcamStreamer(int streamingRate, String remoteHostname, int remotePort) {
		super(streamingRate);
		this.remoteHostname = remoteHostname;
		this.remotePort = remotePort;
	}

	public void init() {
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

	/**
	 * Retrieve the next frame from the remote host.
	 */
	@Override
	public byte[] getFrame() {
		return currentFrame;
	}

}

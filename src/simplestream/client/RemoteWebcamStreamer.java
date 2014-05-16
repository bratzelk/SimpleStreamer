package simplestream.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import messages.Message;
import messages.MessageFactory;
import messages.MessageNotFoundException;
import messages.OverloadedResponseMessage;

import org.apache.log4j.Logger;

import simplestream.Peer;
import simplestream.server.ConnectionBuffer;
import simplestream.server.ConnectionBuffer.Response;
import common.Out;
import common.Strings;

/**
 * Displays a webcam stream from a remote camera.
 */
public class RemoteWebcamStreamer extends LocalWebcamStreamer {

	Logger log = Logger.getLogger(getClass());

	private final String remoteHostname;
	private final int remotePort;

	public RemoteWebcamStreamer(int streamingRate, String remoteHostname, int remotePort) {
		super(streamingRate);
		this.remoteHostname = remoteHostname;
		this.remotePort = remotePort;
	}

	public void init() {
		// This needs to be added to the overloadedMessage.
		Peer remoteServer = new Peer(remoteHostname, remotePort);
		new ConnectionBuffer(socket);
		Out.print("Receiving remote webcam stream");

		// TODO: This is an example message
		try {
			OverloadedResponseMessage overloadedMessage =
					(OverloadedResponseMessage) MessageFactory
							.createMessage(Strings.OVERLOADED_RESPONSE_MESSAGE);
			overloadedMessage.addServer(remoteServer);
			// overloadedMessage.addClients(clients);
			Out.print(overloadedMessage.toJSON());
		} catch (MessageNotFoundException e) {
			throw new IllegalArgumentException("Overloaded message unknown", e);
		}

		// TODO: The messages themselves don't compress any byte arrays.
		// You need to do this explicitly before adding the data to a message.

		// TODO: display remote stream.
	}

	/**
	 * Create a socket to connect to the server.
	 */
	protected Socket bind(final String serverHostname, final int port) throws UnknownHostException,
			IOException {
		Socket socket = new Socket(serverHostname, port);
		log.debug("Established connection to " + serverHostname + ":" + port);

		// Establish the direction of the connection.
		// final String serverDirection = direction.equals("push") ? "pull" : "push";
		// ConfigInstruction config = new ConfigInstruction(serverDirection, blockSize);
		Message startMessage = MessageFactory.createMessage(Strings.START_REQUEST_MESSAGE);
		String response = new ConnectionBuffer(socket).send(startMessage.toJSON());
		if (response != Response.ACKNOWLEDGED) {
			throw new RuntimeException("Failed to negotiate direction");
		}
		return socket;
	}

	@Override
	public byte[] getFrame() {}

}

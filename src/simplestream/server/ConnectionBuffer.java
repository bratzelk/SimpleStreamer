package simplestream.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.annotation.Resources;

import messages.Message;

import org.apache.log4j.Logger;

import simplestream.Peer;

/**
 * Sends and receives messages between hosts.
 */
public class ConnectionBuffer {

	private static final Logger log = Logger.getLogger(ConnectionBuffer.class);

	private final Socket socket;

	private final DataInputStream in;
	private final DataOutputStream out;

	public ConnectionBuffer(Socket socket) throws IOException {
		this.socket = socket;
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
	}

	/**
	 * Create a socket to connect to the server.
	 */
	public static ConnectionBuffer bind(Peer remoteServer) throws IOException {
		Socket socket = new Socket(remoteServer.getHostname(), remoteServer.getPort());
		ConnectionBuffer buffer = new ConnectionBuffer(socket);
		log.debug(buffer + " Established connection to " + remoteServer.getHostname() + ":"
						+ remoteServer.getPort());
		return buffer;
	}

	/**
	 * Reads in request data from the input stream of the socket.
	 *
	 * @return The contents of the request message.
	 * @throws IOException
	 */
	public String receive() throws IOException {
		String response = in.readUTF();
		log.debug(this + " Received response: " + response);
		return response;
	}

	/**
	 * Sends the given message in response to a request.
	 *
	 * @param response The response message to send.
	 * @throws IOException
	 */
	public void respond(String response) throws IOException {
		out.writeUTF(response.toString());
	}

	/**
	 * Sends a message without waiting for a response.
	 *
	 * @param message The message to send.
	 * @throws IOException
	 */
	public void send(String message) throws IOException {
		// Send the message.
		log.debug(this + " Sending data: " + message);
		out.writeUTF(message);
	}

	/**
	 * Sends a message via the socket.
	 *
	 * @param message The message to send.
	 * @return The response received.
	 */
	public String sendAndReceive(String message) {
		try {
			// Send the message.
			send(message);
			// Receive the response.
			String response = receive();
			if (response.equals("ERROR")) {
				throw new RuntimeException("Server failed to handle message:" + message);
			}
			return response;
		} catch (UnknownHostException e) {
			log.error("Failed to send/receive message", e);
		} catch (EOFException e) {
			log.error("Failed to send/receive message", e);
		} catch (IOException e) {
			log.error("Failed to send/receive message", e);
		}
		return "ERROR";
	}

	/**
	 * Serializes and sends the given message.
	 *
	 * @param message The {@link Message} to send.
	 * @return The response received.
	 */
	public String sendAndReceive(Message message) {
		return sendAndReceive(message.toJSON());
	}

	/**
	 * Cleans up any {@link Resources} being used.
	 *
	 * @throws IOException
	 */
	public void close() throws IOException {
		socket.close();
	}

	@Override
	public String toString() {
		return "ConnectionBuffer[" + socket.getLocalAddress() + ":" + socket.getLocalPort() + "]";
	}

}

package simplestream.networking;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.annotation.Resources;

import messages.Message;

import org.apache.log4j.Logger;

import simplestream.Peer;

/**
 * Sends and receives messages between hosts.
 *
 * @see http://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
 */
public class ConnectionBuffer {

	private static final Logger log = Logger.getLogger(ConnectionBuffer.class);

	/** The socket representing the connection to the client. */
	private final Socket socket;

	/** Details of the client machine. */
	private final Peer peer;

	/** Reads in messages received by the socket. */
	private final BufferedReader reader;

	/** Writes messages out to the socket. */
	private final PrintWriter writer;

	public ConnectionBuffer(Socket socket) throws IOException {
		this.socket = socket;
		this.peer = Peer.fromSocket(socket);

		DataInputStream in = new DataInputStream(socket.getInputStream());
		this.reader = new BufferedReader(new InputStreamReader(in));

		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		this.writer = new PrintWriter(out, true);
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
		String response = reader.readLine();
		log.debug(this + " Received response: " + truncate(response, 40));
		return response;
	}

	/**
	 * Sends a message without waiting for a response.
	 *
	 * @param message The message to send.
	 * @throws IOException
	 */
	public void send(String message) throws IOException {
		log.debug(this + " Sending data: " + truncate(message, 40));
		writer.println(message);
	}

	/**
	 * Sends a {@link Message} without waiting for a response.
	 *
	 * @param message The message to send.
	 * @throws IOException
	 */
	public void send(Message message) throws IOException {
		send(message.toJSON());
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
	 * Truncates the string to the given length.
	 *
	 * @param message The message to truncate.
	 * @param lengthThe maximum length of the truncated message string.
	 * @return The truncated message.
	 */
	protected String truncate(String message, int maxLength) {
		if (message.length() > maxLength) {
			return message.substring(0, maxLength) + "...";
		}
		return message;
	}

	/**
	 * Cleans up any resources being used.
	 *
	 * @throws IOException
	 */
	public void kill() throws IOException {
		log.debug("Shutting down " + this + "...");
		socket.close();
		reader.close();
		writer.close();
	}

	public Peer getPeer() {
		return peer;
	}

	@Override
	public String toString() {
		return "ConnectionBuffer[" + socket.getLocalAddress() + ":" + socket.getLocalPort() + "]";
	}

}

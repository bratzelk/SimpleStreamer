package au.edu.unimelb.orlade.comp90015.filesync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.annotation.Resources;

import org.apache.log4j.Logger;

/**
 * Handles file sync changes.
 */
public class ConnectionBuffer extends Thread {

	Logger log = Logger.getLogger(getClass());

	/** The possible simple response messages from the server. */
	public enum Response {
		ACKNOWLEDGED, NEED_BLOCK, ERROR
	}

	private final Socket socket;

	private final DataInputStream in;
	private final DataOutputStream out;

	public ConnectionBuffer(Socket socket) throws IOException {
		this.socket = socket;
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
	}

	/**
	 * Reads in request data from the input stream of the socket.
	 *
	 * @return The contents of the request message.
	 * @throws IOException
	 */
	public String receive() throws IOException {
		return in.readUTF();
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
	 * Sends a message via the socket.
	 *
	 * @param message The message to send.
	 * @return The response received.
	 */
	public String send(String message) {
		try {
			// Send the message.
			log.debug("Sending data: " + message);
			out.writeUTF(message);

			// Receive the response.
			String response = in.readUTF();
			if (response.equals(Response.ERROR)) {
				throw new RuntimeException("Server failed to handle message:" + message);
			}
			log.debug("Received response: " + response);
			return response;
		} catch (UnknownHostException e) {
			log.error("Failed to send/receive message", e);
		} catch (EOFException e) {
			log.error("Failed to send/receive message", e);
		} catch (IOException e) {
			log.error("Failed to send/receive message", e);
		}
		return Response.ERROR.toString();
	}

	/**
	 * Cleans up any {@link Resources} being used.
	 *
	 * @throws IOException
	 */
	public void close() throws IOException {
		socket.close();
	}

}

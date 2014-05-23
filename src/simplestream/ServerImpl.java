package simplestream;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import messages.Message;
import messages.MessageFactory;

import common.Settings;
import common.Strings;

public class ServerImpl implements Server {

	protected ServerSocket serverSocket;
	protected Socket connectionSocket;

	protected InputStream inputStream;
	protected ObjectInputStream objectInputStream;
	protected OutputStream outputStream;
	protected ObjectOutputStream objectOutputStream;

	public ServerImpl(int port) throws IOException {

		this.serverSocket = new ServerSocket(port);

	}

	public void waitForConnection() throws IOException, ClassNotFoundException {

		// Sit and wait for a client to connect...
		while (true) {
			connectionSocket = serverSocket.accept();

			inputStream = connectionSocket.getInputStream();
			objectInputStream = new ObjectInputStream(inputStream);

			outputStream = connectionSocket.getOutputStream();
			objectOutputStream = new ObjectOutputStream(outputStream);


			// This is totally wrong atm, should be waiting for JSON
			// Message requestMessage = (Message)objectInputStream.readObject();

			break;
		}

		// Send a JSON Response...

		Message responseMessage = MessageFactory.createMessage(Strings.START_REQUEST_MESSAGE);

		Socket socket = new Socket(Settings.DEFAULT_HOSTNAME, Settings.DEFAULT_REMOTE_PORT);

		try (OutputStreamWriter outputStreamWriter =
			new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)) {
			outputStreamWriter.write(responseMessage.toJSON());
		}

	}
}

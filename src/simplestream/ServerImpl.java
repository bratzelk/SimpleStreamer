package simplestream;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import common.Settings;
import common.Strings;


import messages.Message;
import messages.MessageFactory;
import messages.MessageNotFoundException;

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
    
	public void waitForConnection() throws IOException, ClassNotFoundException, MessageNotFoundException {
    	
    	//Sit and wait for a client to connect...
        while(true){
           connectionSocket = serverSocket.accept();
           
           inputStream = connectionSocket.getInputStream();  
           objectInputStream = new ObjectInputStream(inputStream);
           
           outputStream = connectionSocket.getOutputStream();  
           objectOutputStream = new ObjectOutputStream(outputStream);  
			
		
           //This is totally wrong atm, should be waiting for JSON
           //Message requestMessage = (Message)objectInputStream.readObject();
           
           break;
        }
        
        //Send a JSON Response...
        
        Message responseMessage = MessageFactory.createMessage(Strings.START_REQUEST_TYPE);
        
        Socket socket = new Socket(Settings.DEFAULT_HOSTNAME, Settings.DEFAULT_REMOTE_PORT);
        
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)) {
        	outputStreamWriter.write(responseMessage.toJSON());
        }
    	
    }
}

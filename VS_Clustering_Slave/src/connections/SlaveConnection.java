package connections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import application.SlaveServer;
import featurehandling.FeatureHandling;

public class SlaveConnection extends Connection {

	//TODO: List of processors
	private FeatureHandling processor;
	private SlaveServer server;
	
	private InetAddress address;
	private int port;
	
	public SlaveConnection(Socket socket, FeatureHandling processor,SlaveServer server, InetAddress address, int port) {
		
		super(socket);	
		this.processor = processor;
		this.server = server;
		
		this.address = address;
		this.port = port;
		
	}
	
	@Override
	public void run() {
		
		
			String message = receive();
			System.out.println();
			System.out.println("message: " + message);
			System.out.println();
			//String index = message.split(";")[1];
			
			//TODO: Select right processor (if List of processors)			
			String erg = processor.processRequest(message);
			System.out.println("Sending following message to Master: " + erg);
			
			sendMessageToMaster(erg);
			
			//send(erg);
			
			server.decrementOpenRequests();
			
		
	}
	
	private void sendMessageToMaster(String message) {
		try {
			Socket socket = new Socket(address, port);
			DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
			//System.out.println("Send message to slave ["+ "Request;" + guid + ";" + feature + ";" + arguments  + "]" );
			clientOutput.writeUTF(message);

			clientOutput.flush();
			
			socket.close();		
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	
}

package connections;

import java.net.Socket;

import application.SlaveServer;
import featurehandling.FeatureHandling;

public class SlaveConnection extends Connection {

	//TODO: List of processors
	private FeatureHandling processor;
	private SlaveServer server;
		
	public SlaveConnection(Socket socket, FeatureHandling processor,SlaveServer server) {
		
		super(socket);	
		this.processor = processor;
		this.server = server;
	}
	
	@Override
	public void run() {
		
		while(!isInterrupted()) {
				
			String message = receive();
			System.out.println();
			System.out.println("message: " + message);
			System.out.println();
			String index = message.split(";")[1];
			
			//TODO: Select right processor (if List of processors)			
			String erg = processor.processRequest(message);
			System.out.println("Sending following message to Master: " + "Result;" + index + ";" + erg);
			
			send("Result;" + index + ";" + erg);
			
			server.decrementOpenRequests();
			
		}
		
	}
	
	
	
	
}

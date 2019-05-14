package connections;

import java.net.Socket;

import featurehandling.FeatureHandling;

/***
 * 
 * Connection from Master-Server to the Slave-Server
 * which holds the logic
 * 
 * @author Dennis
 *
 */
public class SlaveConnection extends Connection {

	//TODO: List of processors
	FeatureHandling processor;
	
	public SlaveConnection(Socket socket, FeatureHandling processor) {
		
		super(socket);	
		this.processor = processor;
	}
	
	@Override
	public void run() {
		
		while(!isInterrupted()) {
				
			String message = receive();
			String index = message.split(";")[2];
			
			//TODO: Select right processor			
			String erg = processor.processRequest(message);
			send("Result;" + index + ";" + erg);
			
		}
		
	}
	
	
	
	
}

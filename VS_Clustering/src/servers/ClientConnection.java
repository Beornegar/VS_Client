package servers;

import java.net.Socket;

import connections.Connection;

/***
 * 
 * Open Connection of client to master server
 * 
 * @author Dennis
 *
 */
public class ClientConnection extends Connection {

	public ClientConnection(Socket socket) {
		super(socket);
	}
	
	@Override
	public void run() {
		
		while(!isInterrupted()) {
			reactToToClientRequest();	
		}
		
	}

	private void reactToToClientRequest() {
		
	}
	
}

package connections;

import java.net.Socket;

public class ResponseConnection extends Connection {

	public ResponseConnection(Socket socket) {
		super(socket);
	}

	@Override
	public void run() {
	
		processResponse();
	}
	
	
	private void processResponse() {
		
		String message = receive();
		
		System.out.println("Received response: ["+ message +"]");
		System.out.println();
		
	}
	
}

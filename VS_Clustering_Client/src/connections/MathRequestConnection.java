package connections;

import java.net.Socket;

import utils.MathParameter;

public class MathRequestConnection extends Connection {

	private MathParameter params;
	private String guid;
	
	public MathRequestConnection(Socket socket, MathParameter params, String guid) {
		super(socket);
	}
	
	@Override
	public void run() {
	
		processRequest();
	}

	

	/***
	 * Message-Form: "Request;Guid;Feature;Arguments" <br>
	 * 
	 * message has to start with "Request" <br>
	 * 
	 */
	private void processRequest() {
		
		//1. Send request to loadbalancer	
		send(createRequestString());
		
		//2. Sysout the returned String
		//TODO: Differentiate between error-return and value-return
		String message = receive();
		String[] messageParts = message.split(";");
		
		if(messageParts.length != 3) {
			System.out.println("Received broken message [" + message + "]");
		}
		String result = messageParts[2];
		
		System.out.println("Returned message: " + result);
		
	}

	private String createRequestString() {
		
		String erg = "Request;" + guid+ ";" + "calculate;" + params.getA() + ":" + params.getB() + ":" + params.getOperation();
		
		return erg;
	}
	
	
	
}

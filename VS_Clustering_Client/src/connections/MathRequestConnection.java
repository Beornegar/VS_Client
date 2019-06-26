package connections;

import java.net.Socket;

import utils.MathParameter;

public class MathRequestConnection extends Connection {

	private MathParameter params;
	private String guid;
	private int ownPort;
	
	public MathRequestConnection(Socket socket, MathParameter params, String guid, int port) {
		super(socket);
		this.params = params;
		this.guid = guid;
		this.ownPort = port;
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
		System.out.println(params);
		String erg = "Request;"+ ownPort + ";"  + guid+ ";" + "calculate;" + params.getA() + ":" + params.getB() + ":" + params.getOperation().getValue();
		
		return erg;
	}
	
	
	
}

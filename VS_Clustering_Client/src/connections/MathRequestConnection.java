package connections;

import java.net.Socket;

import utils.MathOperations;

public class MathRequestConnection extends Connection {

	private String guid;
	private int ownPort;
	
	private double a;
	private double b;
	String operation;
	
	public MathRequestConnection(Socket socket, String guid, int port, double a, double b, MathOperations op) {
		super(socket);
		
		this.guid = guid;
		this.ownPort = port;
		
		this.a = a;
		this.b = b;
		this.operation = op.getValue();
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
		send(createRequestString());
	}

	private String createRequestString() {
		
		String erg = "Request;"+ ownPort + ";"  + guid+ ";" + "calculate;" + a + ":" + b + ":" + operation;
		
		System.out.println("Sending request [" + erg + "]");
		
		return erg;
	}
	
	
	
}

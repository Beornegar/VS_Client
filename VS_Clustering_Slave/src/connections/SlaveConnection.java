package connections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import application.SlaveServer;
import featurehandling.FeatureHandling;

public class SlaveConnection extends Connection {

	private FeatureHandling processor;
	private SlaveServer server;

	private InetAddress address;
	private int port;

	private long numberOfMessages;
	
	private boolean verbose = false;

	public SlaveConnection(Socket socket, FeatureHandling processor, SlaveServer server, InetAddress address, int port,
			boolean verbose) {

		super(socket);
		this.processor = processor;
		this.server = server;

		this.address = address;
		this.port = port;

		this.verbose = verbose;

	}

	@Override
	public void run() {

		String message = receive();

		numberOfMessages += 1;
		
		System.out.println("["+ numberOfMessages + "] Received message: " + message);

		String erg = processor.processRequest(message);
		//if (verbose) {
			System.out.println("Sending following message to Master: " + erg);
		//}
		sendMessageToMaster(erg);

		server.decrementOpenRequests();
	}

	private void sendMessageToMaster(String message) {
		try {
			Socket socket = new Socket(address, port);
			DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());

			clientOutput.writeUTF(message);
			clientOutput.flush();

			socket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}

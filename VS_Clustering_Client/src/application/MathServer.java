package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import connections.Connection;
import connections.MathRequestConnection;
import connections.ResponseConnection;
import utils.MathParameter;

public class MathServer extends Thread {

	private static Executor threadPool = Executors.newCachedThreadPool();
	private ServerSocket socket;
	
	private int ownPort;
	
	MathServer(int port) {
		
		if(port < 1000 || port > 65535) {
			port = 12345;
			System.out.println("Inserted port is not in allowed range [1000 - 65535]");
			System.out.println("Set port to: 12340");
		}
		
		this.ownPort = port;
		
		try {
			
			this.socket = new ServerSocket(port);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void run() {

		System.out.println("Math-Client running...");
		
		while (!isInterrupted()) {
			reactToRequest();
		}

		System.out.println("Math-Client down!");
	}

	/**
	 * 
	 * Create a new Connection to send a request for calculation
	 * 
	 * @param internetAdress
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void sendMathRequest(String internetAdress, int port, MathParameter params, String guid) {
		try {
			Socket sendingSocket = new Socket(internetAdress, port);
			Connection task = new MathRequestConnection(sendingSocket, params, guid, this.ownPort);
			threadPool.execute(task);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Creates new Thread on server which processes a request Change FeatureHandler
	 * here to set a different feature to the SlaveServer
	 * 
	 */
	private void reactToRequest() {
		Connection task;
		try {
			task = new ResponseConnection(socket.accept());
			threadPool.execute(task);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * End server
	 */
	public void close() {
		this.interrupt();
	}


}

package application;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import connections.Connection;
import connections.MathRequestConnection;
import utils.MathParameter;

public class MathServer extends Thread {

	private static Executor threadPool = Executors.newCachedThreadPool();

	@Override
	public void run() {

		System.out.println("Math-Client running...");
		
		while (!isInterrupted()) {

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
			Connection task = new MathRequestConnection(sendingSocket, params, guid);
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

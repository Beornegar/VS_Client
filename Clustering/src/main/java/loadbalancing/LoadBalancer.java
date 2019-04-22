package loadbalancing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import utils.Configuration;

public class LoadBalancer extends Thread {

	private static Executor threadPool = Executors.newCachedThreadPool();

	private ServerSocket socket;
	private boolean endServer = false;

	private long connections = 0;
	private Map<String, Connection> openConnections = new ConcurrentHashMap<>();
	private Map<String, Connection> possibleServers = new ConcurrentHashMap<>();

	public void Server(int port) {

		if (port < Configuration.getMinPort() || port > Configuration.getMaxPort()) {
			port = Configuration.getDefaultPort();
		}

		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		while (!endServer) {
			reactToConnection();
		}

		System.out.println("Server down.");

	}

	/**
	 * 
	 * Wait for Connections which engage the server in key exchange
	 * 
	 */
	private void reactToConnection() {
		Connection task;
		try {
			
			//1. Get Initial message
			
			//2. If service -> register service in list
			
			//3. If client -> forward to service with function required by client
			
			Socket newSocket = socket.accept();
			
			task = new Connection(newSocket);
			threadPool.execute(task);
			registerThread(task);
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Create a new Connection to start the key exchange
	 * 
	 * @param internetAdress
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void reactToNewConnection(String internetAdress, int port) {
		try {
			Socket sendingSocket = new Socket(internetAdress, port);
			Connection task = new Connection(sendingSocket);
			threadPool.execute(task);
			registerThread(task);
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Insert new Connection in List of open Connections
	 * 
	 * @param t
	 * @return
	 */
	private boolean registerThread(Connection t) {
		connections++;
		openConnections.put("Connection-" + connections, t);
		return true;
	}

}

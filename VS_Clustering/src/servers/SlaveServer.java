package servers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import connections.Connection;
import connections.LifeCycleConnection;
import connections.SlaveConnection;
import utils.Configuration;

/***
 * 
 * Slave server which holds the logic of the features
 * 
 * @author Dennis
 *
 */
public class SlaveServer extends Thread {

	private static Executor threadPool = Executors.newCachedThreadPool();

	private ServerSocket socket;

	private boolean endServer = false;
	private Map<String, Connection> allConnections = new ConcurrentHashMap<>();

	private InetAddress masterAddress;
	private int masterPort;

	public SlaveServer(int port, InetAddress masterAddress, int masterPort) {

		if (port < Configuration.getMinPort() || port > Configuration.getMaxPort()) {
			port = Configuration.getServerPort();
		}

		this.masterAddress = masterAddress;
		this.masterPort = masterPort;

		try {
			this.socket = new ServerSocket(port);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			registerWithMasterServer(masterAddress, masterPort, true);

			while (!endServer) {
				reactToRequest();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			allConnections.forEach((name, st) -> {
				try {
					st.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});

			registerWithMasterServer(masterAddress, masterPort, false);
		}
		System.out.println("Server down.");
	}

	/**
	 * End server
	 */
	public void close() {
		endServer = true;
	}

	/**
	 * Insert new Connection in List of open Connections
	 * 
	 * @param t
	 * @return
	 */
	private boolean registerThread(Connection t) {
		allConnections.put("Connection-" + allConnections.size(), t);
		return true;
	}

	/**
	 * 
	 * Creates new Thread on server which processes a request
	 * 
	 */
	private void reactToRequest() {
		Connection task;
		try {
			task = new SlaveConnection(socket.accept());
			threadPool.execute(task);
			registerThread(task);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 
	 * Registers/Deregisters the server with the loadbalancer (Master) <br>
	 * Registering called 1 time at the start of the server <br>
	 * 
	 * @param address
	 * @param port
	 * @param register
	 * @return
	 */
	private boolean registerWithMasterServer(InetAddress address, int port, boolean register) {
		try {
			Socket sendingSocket = new Socket(address, port);
			Connection task = new LifeCycleConnection(sendingSocket, register);
			threadPool.execute(task);
			registerThread(task);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}

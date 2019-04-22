package servers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
	private int maxAmountOfRequests;
	
	private InetAddress masterAddress;
	private int masterPort;

	private List<String> features = new ArrayList<>();

	
	public SlaveServer(int port, String masterAddress, int masterPort,int maxAmountOfRequests) {

		if (port < Configuration.getMinPort() || port > Configuration.getMaxPort()) {
			port = Configuration.getServerPort();
		}
	
		if(maxAmountOfRequests > 0) {
			threadPool = Executors.newFixedThreadPool(maxAmountOfRequests);
		}
		
		this.masterPort = masterPort;
		
		initializeFeatures();

		try {
			this.masterAddress = InetAddress.getByName(masterAddress);
			this.socket = new ServerSocket(port);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeFeatures() {
			features.add("calculate");
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
	 * 
	 * Creates new Thread on server which processes a request
	 * 
	 */
	private void reactToRequest() {
		Connection task;
		try {
			task = new SlaveConnection(socket.accept());
			threadPool.execute(task);
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
	public boolean registerWithMasterServer(InetAddress address, int port, boolean register) {
		try {
			Socket sendingSocket = new Socket(address, port);
			Connection task = new LifeCycleConnection(sendingSocket, register, maxAmountOfRequests, features);
			threadPool.execute(task);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}

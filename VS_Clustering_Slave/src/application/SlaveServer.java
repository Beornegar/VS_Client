package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import arguments.Configuration;
import connections.Connection;
import connections.LifeCycleConnection;
import connections.LifeCycleMethods;
import connections.SlaveConnection;
import featurehandling.MathFeatureHandler;

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

	private volatile boolean isRegistered = false;
	private InetAddress masterAddress;
	private int masterPort;

	private volatile int openRequests = 0;
	private List<String> features = new ArrayList<>();

	public SlaveServer(int port, String masterAddress, int masterPort, int maxAmountOfRequests) {

		if (port < Configuration.getMinPort() || port > Configuration.getMaxPort()) {
			port = Configuration.getServerPort();
		}

		if (maxAmountOfRequests <= 0) {
			threadPool = Executors.newFixedThreadPool(1);
		} else {
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

			while (!endServer) {

				if (!isRegistered() && masterAddress != null && masterPort > 0) {
					registerWithMasterServer(masterAddress, masterPort, LifeCycleMethods.REGISTER);
				} else {

					while (!endServer) {
						reactToRequest();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			registerWithMasterServer(masterAddress, masterPort, LifeCycleMethods.UNREGISTER);
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
	 * Creates new Thread on server which processes a request Change FeatureHandler
	 * here to set a different feature to the SlaveServer
	 * 
	 */
	private void reactToRequest() {
		Connection task;
		try {
			task = new SlaveConnection(socket.accept(), new MathFeatureHandler(), this);
			threadPool.execute(task);
			incrementOpenRequests();

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
	public void registerWithMasterServer(InetAddress address, int port, LifeCycleMethods lifecycleMethod) {
		try {
			Socket sendingSocket = new Socket(address, port);
			Connection task = new LifeCycleConnection(sendingSocket, lifecycleMethod, maxAmountOfRequests, features,
					this);
			threadPool.execute(task);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getMaxAmountOfRequests() {
		return maxAmountOfRequests;
	}

	public void setMaxAmountOfRequests(int maxAmountOfRequests) {
		this.maxAmountOfRequests = maxAmountOfRequests;
	}

	public synchronized void incrementOpenRequests() {
		openRequests++;
		System.out.println("OpenRequests" + openRequests);
	}

	public synchronized void decrementOpenRequests() {
		openRequests--;
		System.out.println("OpenRequests" + openRequests);
	}

	public void registerMaster(InetAddress address, int port) {
		this.masterAddress = address;
		this.masterPort = port;
		this.isRegistered = true;
	}

	public void deregisterMaster() {
		this.masterAddress = null;
		this.masterPort = 0;
		this.isRegistered = false;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

}

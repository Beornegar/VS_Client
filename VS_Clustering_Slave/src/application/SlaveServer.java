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
	private volatile boolean registerInProcess = false;

	private InetAddress masterAddress;
	private int masterPort;

	private int ownPort;
	private boolean verbose = false;

	private volatile int openRequests = 0;
	private List<String> features = new ArrayList<>();

	public SlaveServer(int port, String masterAddress, int masterPort, int maxAmountOfRequests, boolean verbose) {

		if (port < Configuration.getMinPort() || port > Configuration.getMaxPort()) {
			port = Configuration.getServerPort();
		}

		this.maxAmountOfRequests = maxAmountOfRequests;
		if (maxAmountOfRequests <= 0) {
			threadPool = Executors.newFixedThreadPool(5);
			this.maxAmountOfRequests = 5;
		} else {
			threadPool = Executors.newFixedThreadPool(maxAmountOfRequests + 2);
		}

		this.masterPort = masterPort;
		this.ownPort = port;
		this.verbose = verbose;
		initializeFeatures();

		try {

			if (masterAddress != null && !"".equals(masterAddress) && !"null".equals(masterAddress)) {
				this.masterAddress = InetAddress.getByName(masterAddress);
			}
			this.socket = new ServerSocket(port);

			if (verbose) {
				System.out.println("SlaveServer tries to connect to :" + masterAddress + ":" + masterPort
						+ " with MaxAmountOfRequests " + maxAmountOfRequests);
			}
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
			System.out.println("Run start");

			while (!endServer) {

				if (!isRegistered() && registerInProcess == false && masterAddress != null && masterPort > 0) {

					registerWithMasterServer(masterAddress, masterPort, LifeCycleMethods.REGISTER);
					if (verbose) {
						System.out.println("IsRegistered" + isRegistered() + "Masteraddress:" + masterAddress);
					}
				} else {

					while (!endServer && isRegistered()) {
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
			task = new SlaveConnection(socket.accept(), new MathFeatureHandler(), this, masterAddress, masterPort, verbose);
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
			System.out.println("In register with master server");
			registerInProcess = true;
			Socket sendingSocket = new Socket(address, port);
			Connection task = new LifeCycleConnection(sendingSocket, lifecycleMethod, this.maxAmountOfRequests,
					features, this);
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
		if(verbose) {
			System.out.println("OpenRequests: " + openRequests);
		}
		
		if(openRequests > maxAmountOfRequests) {
			System.out.println("Mehr Anforderungen als erlaubt!!");
		}
		
	}

	public synchronized void decrementOpenRequests() {
		openRequests--;
		if(verbose) {
			System.out.println("OpenRequests: " + openRequests);
		}
		
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

	public boolean isRegisterInProcess() {
		return registerInProcess;
	}

	public void setRegisterInProcess(boolean registerInProcess) {
		this.registerInProcess = registerInProcess;
	}

	public int getOwnPort() {
		return ownPort;
	}

	public void setOwnPort(int ownPort) {
		this.ownPort = ownPort;
	}

}

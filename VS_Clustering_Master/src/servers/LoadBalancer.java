package servers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import arguments.Configuration;
import connections.Connection;
import connections.MasterConnection;
import connections.QueueConnection;
import utils.Request;
import utils.ConnectionInformation;
import utils.SlaveInformation;
import utils.SynchronizedList;

/***
 * 
 * Master-Server, where Slave-Servers can register/deregister themselves<br>
 * and clients can send their requests to.
 * 
 * Also does the load balancing to the slaves.
 * 
 * @author Dennis
 *
 */
public class LoadBalancer extends Thread {

	private ServerSocket socket;

	private static Executor threadPool = Executors.newCachedThreadPool();
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private SynchronizedList<SlaveInformation> slaves = new SynchronizedList<>();
	private SynchronizedList<ConnectionInformation> clientRequests = new SynchronizedList<>();

	private Queue<Request> requestsToProcess = new ConcurrentLinkedQueue<>();

	private boolean verbose;

	public LoadBalancer(int port, boolean verbose) {
		this.verbose = verbose;
		try {
			if (port < Configuration.getMinPort() || port > Configuration.getMaxPort()) {
				System.err.println("Port " + port + " was not between min max of Configuration: "
						+ Configuration.getMinPort() + " and " + Configuration.getMaxPort()
						+ "Port will get overridden by Config to " + Configuration.getServerPort());
				port = Configuration.getServerPort();
			}
			this.socket = new ServerSocket(port);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {

			scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {

					if (requestsToProcess.size() > 0) {
						System.out.println("Scheduler processing requests now");
						for (int i = 0; i < 10; i++) {
							Request r = requestsToProcess.poll();

							if (r == null) {
								break;
							}

							processQueueItem(r);
						}

					}
				}
			}, 5 /* Startverzögerung */, 20 /* Dauer */, TimeUnit.SECONDS);

			while (!isInterrupted()) {
				reactToRequest();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		System.out.println("Server down.");
	}

	public void reactToRequest() {
		Connection task;
		try {
			Socket requestSocket = socket.accept();
			task = new MasterConnection(requestSocket, this);
			threadPool.execute(task);
			// clientRequests.add(new ConnectionInformation(requestSocket.getInetAddress(),
			// requestSocket.getPort()));
			System.out
					.println("Got new request from " + requestSocket.getInetAddress() + ":" + requestSocket.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processQueueItem(Request request) {
		Connection task = new QueueConnection(null, this, request);
		threadPool.execute(task);
	}

	public void register(SlaveInformation slave) {
		slaves.add(slave);
		System.out.println("Registered new Slave: " + slave);
	}

	public void unregister(InetAddress address, int port) {

		List<SlaveInformation> list = new ArrayList<>();
		slaves.stream().filter(s -> s.getAdress().toString().equals(address.toString()) && s.getPort() == port)
				.forEach(s -> {
					list.add(s);
				});

		for (SlaveInformation s : list) {
			System.out.println("Removed slave [" + s + "]");
			slaves.remove(s);
		}
	}

	/**
	 * 1. See if ClientInformation already exists (address + port) <br>
	 * 1a if true: add guid <br>
	 * 1b Else: new ClientInformation + guid <br>
	 * 
	 * @param address
	 * @param port
	 * @param guid
	 */
	public synchronized void addClientRequest(InetAddress address, int port, UUID guid) {

		System.out.println("Add new GUID to right ConnectionInformation");

		System.out.println("------ Existing ConnectionInformations ----------");

		for (ConnectionInformation ci : this.getClientRequests()) {
			System.out.println(ci);
		}

		System.out.println("----------------");

		System.out.println("Looking for same address + port");
		List<ConnectionInformation> list = this.clientRequests.stream()
				.filter(ci -> ci.getAdress() == address && ci.getPort() == port).collect(Collectors.toList());
		System.out.println(list.size() + " elements found.");

		if (list.size() == 1) {
			System.out.println("Adding guid[" + guid + "]" + " to CI [" + list.get(0).getAdress() + ":"
					+ list.get(0).getPort() + "]");
			list.get(0).getListOfOpenRequests().add(guid);
		} else if (list.size() > 1) {
			System.err.println(
					"Several connection informations of 1 client found ! Should not be possible! Just taking first one");
			list.get(0).getListOfOpenRequests().add(guid);
		} else {
			System.out.println("No ConnectionInformation found. Creating new one");
			ConnectionInformation ci = new ConnectionInformation(address, port);
			ci.getListOfOpenRequests().add(guid);
			this.getClientRequests().add(ci);
		}

		System.out.println("------ After change ConnectionInformations ----------");

		for (ConnectionInformation ci : this.getClientRequests()) {
			System.out.println(ci);
		}

		System.out.println("----------------");
	}

	public SynchronizedList<ConnectionInformation> getClientRequests() {
		return clientRequests;
	}

	public void setClientRequests(SynchronizedList<ConnectionInformation> clientRequests) {
		this.clientRequests = clientRequests;
	}

	public Queue<Request> getRequestsToProcess() {
		return requestsToProcess;
	}

	public void setRequestsToProcess(Queue<Request> requestsToProcess) {
		this.requestsToProcess = requestsToProcess;
	}

	public SynchronizedList<SlaveInformation> getSlaves() {
		return slaves;
	}

	public void setSlaves(SynchronizedList<SlaveInformation> slaves) {
		this.slaves = slaves;
	}

}

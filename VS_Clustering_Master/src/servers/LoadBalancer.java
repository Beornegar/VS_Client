package servers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import arguments.Configuration;
import connections.Connection;
import connections.MasterConnection;
import connections.QueueConnection;
import utils.ClientRequest;
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
	private Queue<ClientRequest> clientRequests = new ConcurrentLinkedQueue<ClientRequest>();

	private boolean verbose;
	private int queueFrequency;
	private int queueAmount;

	public LoadBalancer(int port, boolean verbose, int queueFrequency, int queueAmount) {
		this.verbose = verbose;
		this.queueFrequency = queueFrequency;
		this.queueAmount = queueAmount;
		
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
					
					if (clientRequests.size() > 0) {

						int index = clientRequests.size() < queueAmount ? clientRequests.size() : queueAmount;

						List<ClientRequest> requests = new ArrayList<>();
						for (int i = 0; i < index; i++) {
							requests.add(clientRequests.poll());
						}

						processQueueItem(requests);

					}
				}
			}, 5 /* Startverzögerung */, queueFrequency /* Dauer */, TimeUnit.SECONDS);

			while (!isInterrupted()) {
				reactToRequest();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		System.out.println("Server down...");
	}

	public void reactToRequest() {
		Connection task;
		try {
			Socket requestSocket = socket.accept();
			task = new MasterConnection(requestSocket, this, verbose);
			threadPool.execute(task);

			if (verbose) {
				System.out.println(
						"Got new request from " + requestSocket.getInetAddress() + ":" + requestSocket.getPort());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processQueueItem(List<ClientRequest> requests) {
		Connection task = new QueueConnection(null, this, requests, verbose);
		threadPool.execute(task);
	}

	public void register(SlaveInformation slave) {
		slaves.add(slave);
		if (verbose) {
			System.out.println("Registered new Slave: " + slave);
		}

	}

	public void unregister(InetAddress address, int port) {

		List<SlaveInformation> list = new ArrayList<>();
		slaves.stream().filter(s -> s.getAddress().toString().equals(address.toString()) && s.getPort() == port)
				.forEach(s -> {
					list.add(s);
				});

		for (SlaveInformation s : list) {

			if (verbose) {
				System.out.println("Removed slave [" + s + "]");
			}
			slaves.remove(s);
		}
	}

	public Queue<ClientRequest> getClientRequests() {
		return clientRequests;
	}

	public void setClientRequests(Queue<ClientRequest> clientRequests) {
		this.clientRequests = clientRequests;
	}

	public SynchronizedList<SlaveInformation> getSlaves() {
		return slaves;
	}

	public void setSlaves(SynchronizedList<SlaveInformation> slaves) {
		this.slaves = slaves;
	}

}

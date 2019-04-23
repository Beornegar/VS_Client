package servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

	public LoadBalancer(int port) {
		try {
			if (port < Configuration.getMinPort() || port > Configuration.getMaxPort()) {
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

					// TODO: Look into the queue
					// If Items in Queue look if there are some free slaves who can do the work
					if (requestsToProcess.size() > 0) {
						
						for(Request r : requestsToProcess) {
							processQueueItem(r);
						}
						
						
					}
				}
			}, 0 /* Startverzögerung */, 5 /* Dauer */, TimeUnit.SECONDS);

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
			clientRequests.add(new ConnectionInformation(requestSocket.getInetAddress(), requestSocket.getPort()));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processQueueItem(Request request) {
		try {
			Socket requestSocket = new Socket(request.getClientName(),request.getClientPort());
			Connection task = new QueueConnection(requestSocket, this, request);
			threadPool.execute(task);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void register(SlaveInformation slave) {
		slaves.add(slave);
	}

	public void unregister(SlaveInformation slave) {
		if (slaves.contains(slave)) {
			slaves.remove(slaves.indexOf(slave));
		}
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

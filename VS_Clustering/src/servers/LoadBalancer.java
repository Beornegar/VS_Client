package servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import connections.Connection;
import connections.MasterConnection;
import utils.CalculationRequest;
import utils.Configuration;
import utils.ConnectionInformation;
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
	
	private SynchronizedList<ConnectionInformation> slaves = new SynchronizedList<>();
	private SynchronizedList<ConnectionInformation> clientRequests = new SynchronizedList<>();
	
	private Queue<CalculationRequest> requestsToProcess = new ConcurrentLinkedQueue<>();
	
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
			clientRequests.add(new ConnectionInformation(requestSocket.getInetAddress(),requestSocket.getPort()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void register(ConnectionInformation slave) {
		slaves.add(slave);
	}

	public void unregister(ConnectionInformation slave)  {
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

	public Queue<CalculationRequest> getRequestsToProcess() {
		return requestsToProcess;
	}

	public void setRequestsToProcess(Queue<CalculationRequest> requestsToProcess) {
		this.requestsToProcess = requestsToProcess;
	}

	public SynchronizedList<ConnectionInformation> getSlaves() {
		return slaves;
	}

	public void setSlaves(SynchronizedList<ConnectionInformation> slaves) {
		this.slaves = slaves;
	}

}

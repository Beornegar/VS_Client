package connections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

import servers.LoadBalancer;
import utils.ClientRequest;
import utils.SlaveInformation;

public class QueueConnection extends Connection {

	private LoadBalancer balancer;
	private List<ClientRequest> requestsToProcess;

	private boolean verbose;

	public QueueConnection(Socket socket) {
		super(socket);
	}

	public QueueConnection(Socket socket, LoadBalancer balancer, List<ClientRequest> requestsToProcess,
			boolean verbose) {
		super(socket);

		this.balancer = balancer;
		this.requestsToProcess = requestsToProcess;

		this.verbose = verbose;
	}

	@Override
	public void run() {
		processQueueItem(requestsToProcess);
	}

	public void processQueueItem(List<ClientRequest> requests) {

		for (ClientRequest cr : requests) {

			String feature = cr.getFeature();
			String arguments = cr.getArguments();
			UUID guid = cr.getGuid();

			SlaveInformation slaveInfo = SlaveInformation.getFreeSlaveWithLeastAmountOfWork(balancer.getSlaves(),
					feature, guid);

			if (slaveInfo != null) {
				balancer.getClientRequests().add(cr);
				sendRequestToSlave(feature, arguments, slaveInfo, guid);

			} else {
				cr.setInProgress(false);
				balancer.getClientRequests().add(cr);

			}
		}
	}

	private void sendRequestToSlave(String feature, String arguments, SlaveInformation slaveInfo, UUID guid) {
		Socket socket;
		try {

			if (verbose) {
				System.out.println("" + slaveInfo.getAddress() + slaveInfo.getPort());
			}

			socket = new Socket(slaveInfo.getAddress(), slaveInfo.getPort());
			DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());

			String request = "Request;" + guid + ";" + feature + ";" + arguments;
			if (verbose) {
				System.out.println("Send message to slave [" + request + "]");
			}

			clientOutput.writeUTF(request);

			clientOutput.flush();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

package connections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import servers.LoadBalancer;
import utils.Request;
import utils.SlaveInformation;

public class QueueConnection extends Connection {

	private LoadBalancer balancer;
	private Request requestToProcess;

	public QueueConnection(Socket socket) {
		super(socket);
	}

	public QueueConnection(Socket socket, LoadBalancer balancer, Request requestToProcess) {
		super(socket);

		this.balancer = balancer;
		this.requestToProcess = requestToProcess;
	}

	@Override
	public void run() {
		processQueueItem(requestToProcess);
	}

	public void processQueueItem(Request request) {

		String feature = request.getFeature();
		String arguments = request.getArguments();

		SlaveInformation slaveInfo = SlaveInformation.getFreeSlaveWithLeastAmountOfWork(balancer.getSlaves(), feature);

		if (slaveInfo != null) {
			sendRequestToSlave(feature, arguments, slaveInfo, request.getGuid());
			balancer.getRequestsToProcess().remove(requestToProcess);
		}
	}

	private void sendRequestToSlave(String feature, String arguments, SlaveInformation slaveInfo, UUID guid) {
		Socket socket;
		try {
			socket = new Socket(slaveInfo.getAdress(), slaveInfo.getPort());
			DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());

			clientOutput.writeUTF(feature + ";" + arguments + ";" + balancer.getSlaves().indexOf(slaveInfo));

			clientOutput.flush();
			socket.close();

			slaveInfo.getListOfOpenRequests().add(guid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

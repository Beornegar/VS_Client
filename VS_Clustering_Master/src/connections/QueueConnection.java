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

		System.out.println("Timer-Thread: Started");
		String feature = request.getFeature();
		String arguments = request.getArguments();

		System.out.println("Timer-Thread: FInd Slave");
		SlaveInformation slaveInfo = SlaveInformation.getFreeSlaveWithLeastAmountOfWork(balancer.getSlaves(), feature);

		System.out.println("Timer-Thread: Found slave");
		if (slaveInfo != null) {
			sendRequestToSlave(feature, arguments, slaveInfo, request.getGuid());
			balancer.getRequestsToProcess().remove(requestToProcess);
		} else {
			System.out.println("No slave found");
			System.out.println("Reenter reqeust to queue");
			balancer.getRequestsToProcess().add(request);
		}
		System.out.println("Timer-Thread ending now");
	}

	private void sendRequestToSlave(String feature, String arguments, SlaveInformation slaveInfo, UUID guid) {
		Socket socket;
		try {
			
			System.out.println(""+slaveInfo.getAdress()+ slaveInfo.getPort());
			socket = new Socket(slaveInfo.getAdress(), slaveInfo.getPort());
			DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
			System.out.println("Send message to slave ["+ "Request;" + guid + ";" + feature + ";" + arguments  + "]" );
			clientOutput.writeUTF("Request;" + guid + ";" + feature + ";" + arguments);

			clientOutput.flush();
			socket.close();
			
//			
//			socket = new Socket(slaveInfo.getAdress(), slaveInfo.getPort());
//			DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
//
//			slaveInfo.getListOfOpenRequests().add(guid);
//			clientOutput.writeUTF(feature + ";" + arguments + ";" + balancer.getSlaves().indexOf(slaveInfo));
//
//			clientOutput.flush();
//			socket.close();

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

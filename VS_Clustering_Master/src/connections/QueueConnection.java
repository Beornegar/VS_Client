package connections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
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

	public QueueConnection(Socket socket, LoadBalancer balancer, List<ClientRequest> requestsToProcess, boolean verbose) {
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
		
		System.out.println("[Queue] Start");
		//List<ClientRequest> sentRequests = new ArrayList<>();
		for(ClientRequest cr : requests) {
			
			String feature = cr.getFeature();
			String arguments = cr.getArguments();
			UUID guid = cr.getGuid();
			
			SlaveInformation slaveInfo = SlaveInformation.getFreeSlaveWithLeastAmountOfWork(balancer.getSlaves(), feature, guid);

			System.out.println("");
			
			if (slaveInfo != null) {
				balancer.getClientRequests().add(cr);
				sendRequestToSlave(feature, arguments, slaveInfo, guid);
				//sentRequests.add(cr);
				System.out.println("[Queue] Open Requests: " + balancer.getClientRequests().size());
			} else {
				cr.setInProgress(false);
				balancer.getClientRequests().add(cr);
				System.out.println("Komme in else vorbei. Da kein Slave vorhanden. Vll beim nächsten Versuch");
			}
		}
		System.out.println("[Queue] END");
		//balancer.getClientRequests().removeAll(sentRequests);

		
	}

	private void sendRequestToSlave(String feature, String arguments, SlaveInformation slaveInfo, UUID guid) {
		Socket socket;
		try {
			
			if(verbose) {
				System.out.println(""+slaveInfo.getAddress()+ slaveInfo.getPort());
			}
			
			socket = new Socket(slaveInfo.getAddress(), slaveInfo.getPort());
			DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
			
			if(verbose) {
				System.out.println("Send message to slave ["+ "Request;" + guid + ";" + feature + ";" + arguments  + "]" );
			}

			clientOutput.writeUTF("Request;" + guid + ";" + feature + ";" + arguments);

			clientOutput.flush();
			socket.close();
						
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

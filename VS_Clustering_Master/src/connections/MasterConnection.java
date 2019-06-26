package connections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;

import servers.LoadBalancer;
import utils.ConnectionInformation;
import utils.Request;
import utils.SlaveInformation;

public class MasterConnection extends Connection {

	private LoadBalancer balancer;

	public MasterConnection(Socket socket, LoadBalancer balancer) {
		super(socket);

		this.balancer = balancer;
	}

	/***
	 * Message-Form: "RequestType;Guid;Feature;Arguments" <br>
	 * For math request: String request = "Request;"+port + guid+ ";" + "calculate;" +
	 * params.getA() + ":" + params.getB() + ":" + params.getOperation(); <br>
	 * 
	 * 1. Get message from outside with listenSocket <br>
	 * 2. If message is from a slave -> Register/DeRegister/ResultOfCalculation <br>
	 * 2a If register ->call method "register" <br>
	 * 2b If UnRegister -> call method "unregister" <br>
	 * 2c if ResultOfCalculation -> forward result to client who called <br>
	 * 3. If message comes from a client (StartsWith : Request) <br>
	 * 
	 */
	@Override
	public void run() {

		String message = receive();
		System.out.println("Received Message: " + message);
		String[] messageParts = message.split(";");

		if (messageParts.length > 1) {

			if (messageParts[0].toLowerCase().equals("register")) {

				registerSlave(messageParts);
				
			} else if (messageParts[0].toLowerCase().equals("unregister")) {

				balancer.unregister(new SlaveInformation(this.socket.getInetAddress(), this.socket.getPort()));
				send("ok");

			} else if (messageParts[0].toLowerCase().equals("result")) {

				returnResultToClient(messageParts);

			} else if (messageParts[0].toLowerCase().equals("request")) {

				processClientRequest(message);
			}
		}

	}

	/***
	 * 
	 * Example of Request for Calculation: <br>
	 * For math request: String request = "Request;" + guid+ ";" + "calculate;" +
	 * params.getA() + ":" + params.getB() + ":" + params.getOperation(); <br>
	 * 
	 * @param messageParts
	 */
	public void processClientRequest(String message) {

		String[] messageParts = message.split(";");
		if (messageParts.length < 4) {
			System.out.println("Incorrect Request String [" + message + "]!!");
			System.out.println("Minimum is 4 : [REQUEST,PORT,GUID,FEATURE]");
		}

		int clientPort = Integer.parseInt(messageParts[1]);
		String guidString = messageParts[2];
		UUID guid = UUID.fromString(guidString);
		
		String feature = messageParts[3].toLowerCase();

		String arguments = "";
		if (messageParts.length > 4) {
			arguments = messageParts[4];
		}

		SlaveInformation slaveInfo = SlaveInformation.getFreeSlaveWithLeastAmountOfWork(balancer.getSlaves(), feature);
		System.out.println("Slave with least amount of work: " + slaveInfo);
		if (slaveInfo != null) {
			
			balancer.addClientRequest(socket.getInetAddress(), clientPort, guid);
			
			sendRequestToSlave(feature, arguments, slaveInfo, guid);
							
		} else {
			Request requestToProcess = new Request(feature, arguments, this.socket.getInetAddress(),
					this.socket.getPort());
			
			balancer.addClientRequest(socket.getInetAddress(), clientPort, guid);
			
			balancer.getRequestsToProcess().add(requestToProcess);
			
		}
	}

	public void processClientRequest(Request request) {

		String feature = request.getFeature();
		String arguments = request.getArguments();
		UUID guid = request.getGuid();

		SlaveInformation slaveInfo = SlaveInformation.getFreeSlaveWithLeastAmountOfWork(balancer.getSlaves(), feature);

		if (slaveInfo != null) {
			sendRequestToSlave(feature, arguments, slaveInfo, guid);
		} else {
			Request requestToProcess = new Request(feature, arguments, this.socket.getInetAddress(),
					this.socket.getPort());
			
			balancer.getRequestsToProcess().add(requestToProcess);
		}

	}

	/**
	 * 
	 * Method that sends the actual string to the slave server Send string :
	 * "Request;" + guid + ";" + feature + ";" + arguments
	 * 
	 * @param feature
	 * @param arguments
	 * @param slaveInfo
	 * @param guid
	 */
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

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returned message: "RESULT;GUID;RESULT_OR_ERROR_STRING"
	 * 
	 * @param messageParts
	 */
	private void returnResultToClient(String[] messageParts) {

		if (messageParts.length != 3) {
			System.out.println("Return message broken.");
			System.out.println("Should have following format [RESULT;GUID;RESULT_OR_ERROR_STRING]");
			return;
		}

		UUID guid = UUID.fromString(messageParts[1]);
		String ergMessage = messageParts[2];

//		System.out.println("Received answer from slave: ");
//		for(String m : messageParts) {
//			System.out.println(m);
//		}
//		
//		System.out.println("List of open requests");
//		for(ConnectionInformation c : balancer.getClientRequests()) {
//			
//			System.out.println("Client: " + c);
//			for(UUID u : c.getListOfOpenRequests()) {
//				System.out.println(u);
//				
//			}
//		}
		
		Optional<ConnectionInformation> infoOptional = balancer.getClientRequests().stream()
				.filter(i -> i.getListOfOpenRequests().contains(guid)).findFirst();

		if (infoOptional.isPresent()) {
			ConnectionInformation info = infoOptional.get();
			
			System.out.println("Received Result["+ "Result;" + guid + " ; Returned message [" + ergMessage + "]" +"] and sending it back to client: " + info);
			InetAddress clientAddress = info.getAdress();
			int clientPort = info.getPort();

			try {
				Socket socket = new Socket(clientAddress, clientPort);
				DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
				clientOutput.writeUTF("Result;" + guid + " ; Returned message [" + ergMessage + "]");
				clientOutput.flush();
				socket.close();
				info.getListOfOpenRequests().remove(guid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("GUID is not in existing!");
		}
	}

	/**
	 * Registeres slave to list of slaves
	 * @param messageParts
	 */
	private void registerSlave(String[] messageParts) {
		

		int maxAmount = Integer.parseInt(messageParts[1]);

		String f = messageParts[2];
		String[] features = f.split(":");
		int port = Integer.parseInt(messageParts[3]);
		
		balancer.register(
				new SlaveInformation(this.socket.getInetAddress(), port, maxAmount, features));
		System.out.println();
		send("ok");
	}

}

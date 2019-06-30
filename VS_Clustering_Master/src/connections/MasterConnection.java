package connections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;

import servers.LoadBalancer;
import utils.ClientRequest;
import utils.Request;
import utils.SlaveInformation;

public class MasterConnection extends Connection {

	private LoadBalancer balancer;

	private boolean verbose;
	
	public MasterConnection(Socket socket, LoadBalancer balancer,boolean verbose) {
		super(socket);

		this.balancer = balancer;
		this.verbose = verbose;
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

				int port = Integer.parseInt(messageParts[1]); 
				
				balancer.unregister(this.socket.getInetAddress(), port);
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

		balancer.getClientRequests().add(new ClientRequest(socket.getInetAddress(), clientPort, guid, feature, arguments));
		
		SlaveInformation slaveInfo = SlaveInformation.getFreeSlaveWithLeastAmountOfWork(balancer.getSlaves(), feature, guid);
		
		if(slaveInfo != null) {
			System.out.println("Slave with least amount of work: " + slaveInfo + ", Amount of requests: " + slaveInfo.getAmountOfCurrentRequests());
		}
		
		if (slaveInfo != null) {
			sendRequestToSlave(feature, arguments, slaveInfo, guid);				
		} 
	}

	public void processClientRequest(Request request) {

		String message = "Request;" + request.getClientPort() + ";" + request.getGuid() + ";" + request.getFeature() + ";" + request.getArguments();
		
		this.processClientRequest(message);
		
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
			
			if(verbose) {
				System.out.println(""+slaveInfo.getAddress()+ slaveInfo.getPort());
			}
			
			System.out.println("Requests of Slave: " + slaveInfo.getAmountOfCurrentRequests());
			
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

		System.out.println("Checking for clientdata and slavedata");
		Optional<ClientRequest> infoOptional = balancer.getClientRequests().stream()
				.filter(i -> i.getGuid().equals(guid)).findFirst();

		Optional<SlaveInformation> slaveInfo = balancer.getSlaves().stream()
				.filter(i -> i.getGuids().contains(guid)).findFirst();
		
		System.out.println("Client-Data: SIZE:" + balancer.getClientRequests().size());
		
		if(infoOptional.isPresent()) {
			System.out.println("Client-Data is present");
		}
		if(slaveInfo.isPresent()) {
			System.out.println("Slave-Data is present. Requests: "+ slaveInfo.get().getAmountOfCurrentRequests());
		}
		
		if (infoOptional.isPresent() && slaveInfo.isPresent()) {
			ClientRequest info = infoOptional.get();
			SlaveInformation slave = slaveInfo.get();
						
			System.out.println("Received Result["+ "Result;" + guid + " ; Returned message [" + ergMessage + "]" +"] and sending it back to client: " + info);
			InetAddress clientAddress = info.getAdress();
			int clientPort = info.getPort();

			try {
				Socket socket = new Socket(clientAddress, clientPort);
				DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
				clientOutput.writeUTF("Result;" + guid + " ; Returned message [" + ergMessage + "]");
				clientOutput.flush();
				socket.close();
				
				System.out.println("Removing client request: OLD:" + balancer.getClientRequests().size());
				balancer.getClientRequests().remove(info);
				System.out.println("Removing client request: NEW:" + balancer.getClientRequests().size());
				
				System.out.println("Removing slave request: OLD:" + slave.getGuids().size());
				slave.getGuids().remove(guid);
				System.out.println("Removing slave request: NEW:" + slave.getGuids().size());
							
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
		
		send("ok");
	}

}

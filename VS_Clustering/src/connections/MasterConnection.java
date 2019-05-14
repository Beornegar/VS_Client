package connections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

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
	 * Message-Form: "Guid;Method;Feature;Arguments" <br>
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
		String[] messageParts = message.split(";");

		if (messageParts.length > 1) {

			if (messageParts.length == 2 && messageParts[1].equals("Register")) {

				registerSlave(messageParts);

			} else if (messageParts[1].equals("Unregister")) {

				balancer.unregister(new SlaveInformation(this.socket.getInetAddress(), this.socket.getPort()));

				//TODO: Change length here or even remove?
			} else if (messageParts.length == 3 && messageParts[0].equals("Result")) {

				returnResultToClient(messageParts);
				//TODO: Change length here or even remove?
			} else if (messageParts.length == 4 && messageParts[1].equals("Request")) {

				processClientRequest(messageParts);
			}
		}

	}

	/***
	 * 
	 * Example of Request for Calculation: "guid;calculate;2:3:add"
	 * 
	 * @param messageParts
	 */
	public void processClientRequest(String[] messageParts) {
		
		String guid = messageParts[0];
		String feature = messageParts[1].toLowerCase();
		String arguments = messageParts[2];
		

		SlaveInformation slaveInfo = SlaveInformation.getFreeSlaveWithLeastAmountOfWork(balancer.getSlaves(),feature);

		if (slaveInfo != null) {
			sendRequestToSlave(feature, arguments, slaveInfo,guid);
		} else {
			Request requestToProcess = new Request(feature, arguments,this.socket.getInetAddress(), this.socket.getPort());
			balancer.getRequestsToProcess().add(requestToProcess);
		}
	}

	public void processClientRequest(Request request) {

		String feature = request.getFeature();
		String arguments = request.getArguments();
		String guid = request.getGuid();
		
		SlaveInformation slaveInfo = SlaveInformation.getFreeSlaveWithLeastAmountOfWork(balancer.getSlaves(),feature);

		if (slaveInfo != null) {
			sendRequestToSlave(feature, arguments, slaveInfo, guid);
		} else {
			Request requestToProcess = new Request(feature, arguments,this.socket.getInetAddress(), this.socket.getPort());
			balancer.getRequestsToProcess().add(requestToProcess);
		}

	}

	private void sendRequestToSlave(String feature, String arguments, SlaveInformation slaveInfo, String guid) {
		Socket socket;
		try {
			socket = new Socket(slaveInfo.getAdress(), slaveInfo.getPort());
			DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
			clientOutput.writeUTF(balancer.getSlaves().indexOf(slaveInfo) + ";" + guid + ";" + feature + ";" + arguments );
			clientOutput.flush();
			socket.close();
			slaveInfo.setOpenRequests(slaveInfo.getOpenRequests() + 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void returnResultToClient(String[] messageParts) {
		int listItemIndex = Integer.parseInt(messageParts[1]);
		double erg = Double.parseDouble(messageParts[2]);

		ConnectionInformation info = balancer.getClientRequests().get(listItemIndex);

		InetAddress clientAddress = info.getAdress();
		int clientPort = info.getPort();

		try {
			Socket socket = new Socket(clientAddress, clientPort);
			DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
			clientOutput.writeUTF("Result:" + erg);
			clientOutput.flush();
			socket.close();
			info.setOpenRequests(info.getOpenRequests() - 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void registerSlave(String[] messageParts) {
		String[] m = messageParts[1].split(":");

		int maxAmount = Integer.parseInt(m[1]);

		String f = messageParts[2];
		String[] features = f.split(":");

		balancer.register(
				new SlaveInformation(this.socket.getInetAddress(), this.socket.getPort(), maxAmount, features));
	}

}

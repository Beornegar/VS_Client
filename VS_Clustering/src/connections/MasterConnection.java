package connections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import servers.LoadBalancer;
import utils.ConnectionInformation;
import utils.Request;
import utils.SlaveInformation;

public class MasterConnection extends Connection {

	LoadBalancer balancer;

	public MasterConnection(Socket socket, LoadBalancer balancer) {
		super(socket);

		this.balancer = balancer;
	}

	/***
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

		if (messageParts.length > 0) {

			if (messageParts[0].equals("Register") && messageParts.length == 2) {

				registerSlave(messageParts);

			} else if (messageParts[0].equals("Unregister")) {

				balancer.unregister(new SlaveInformation(this.socket.getInetAddress(), this.socket.getPort()));

			} else if (messageParts[0].equals("Result") && messageParts.length == 3) {

				// TODO: 1.Remove messageParts.length or replace with correct value if method of
				// request will also be send to client
				// TODO: 2.OR send all infos of request + erg back
				// TODO: If 2. then perhaps also send type of erg back aswell

				returnResultToClient(messageParts);

			} else if (messageParts[0].equals("Request") && messageParts.length == 5) {

				processClientRequest(messageParts);
			}
		}

	}

	public void processClientRequest(String[] messageParts) {
		String feature = messageParts[1].toLowerCase();

		// For calculation it has "a=2:b=3:function=add"
		String arguments = messageParts[2];

		// TODO: b + c

		// 3b Enter request in Queue
		// 3c Use Timer Thread who looks periodically in slaves if a slave is free

		SlaveInformation slaveInfo = chooseSlave(feature);

		if (slaveInfo != null) {

			sendRequestToSlave(feature, arguments, slaveInfo);
		} else {
			// TODO: If slaveInfo == null -> Insert Request into Queue
		}
	}

	public void processClientRequest(Request request) {

		String feature = request.getFeature();
		String arguments = request.getArguments();

		SlaveInformation slaveInfo = chooseSlave(feature);

		if (slaveInfo != null) {
			sendRequestToSlave(feature, arguments, slaveInfo);
		} else {
			Request requestToProcess = new Request(feature, arguments);
			balancer.getRequestsToProcess().add(requestToProcess);
		}

	}

	//TODO: Validate that the the lock works as it should because the timer and the Master-Connections can perhaps take the same slave even if it only has capacities for one of them
	private SlaveInformation chooseSlave(String feature) {
		List<SlaveInformation> slaves = balancer.getSlaves().stream().sorted().collect(Collectors.toList());

		SlaveInformation slaveInfo = null;
		for (SlaveInformation s : slaves) {
			if (s.getMaxAmountOfParallelRequests() > s.getOpenRequests() && s.getListOfFeatures().contains(feature)) {
				slaveInfo = s;
				break;
			}
		}
		return slaveInfo;
	}

	private void sendRequestToSlave(String feature, String arguments, SlaveInformation slaveInfo) {
		Socket socket;
		try {
			socket = new Socket(slaveInfo.getAdress(), slaveInfo.getPort());
			DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
			clientOutput.writeUTF(feature + ";" + arguments + ";" + balancer.getSlaves().indexOf(slaveInfo));
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

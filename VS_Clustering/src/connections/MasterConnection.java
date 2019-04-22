package connections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import servers.LoadBalancer;
import utils.ConnectionInformation;
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

			} else if (messageParts[0].equals("Deregister")) {
				
				balancer.unregister(new SlaveInformation(this.socket.getInetAddress(), this.socket.getPort()));

			} else if (messageParts[0].equals("Result") && messageParts.length == 3) {

				//TODO: 1.Remove messageParts.length or replace with correct value if method of request will also be send to client
				//TODO: 2.OR send all infos of request + erg back
				//TODO: If 2. then perhaps also send type of erg back aswell 
				
				returnResultToClient(messageParts);
				
			} else if (messageParts[0].equals("Request") && messageParts.length == 5) {

				sendRequestToSlave(messageParts);
			}
		}

	}

	private void sendRequestToSlave(String[] messageParts) {
		String feature = messageParts[1].toLowerCase();
		int a = Integer.parseInt(messageParts[2]);
		int b = Integer.parseInt(messageParts[3]);
		String function = messageParts[4];

		// TODO: b + c
		
		// 3b Enter request in Queue
		// 3c Use Timer Thread who looks periodically in slaves if a slave is free

		// 3a Get Slaves who implement calculation function needed
		// 3d Get Slave with least amount of work
		List<SlaveInformation> slaves = balancer.getSlaves().stream().sorted().collect(Collectors.toList());

		SlaveInformation slaveInfo = null;
		for (SlaveInformation s : slaves) {
			if (s.getMaxAmountOfParallelRequests() > s.getOpenRequests() && s.getListOfFeatures().contains(feature)) {
				slaveInfo = s;
				break;
			}
		}

		if (slaveInfo != null) {

			Socket socket;
			try {
				socket = new Socket(slaveInfo.getAdress(), slaveInfo.getPort());
				DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
				clientOutput.writeUTF(feature + ";" + a + ";" + b + ";" + function + ";"
						+ balancer.getSlaves().indexOf(slaveInfo));
				clientOutput.flush();
				socket.close();
				slaveInfo.setOpenRequests(slaveInfo.getOpenRequests() + 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			//TODO: If slaveInfo == null -> Insert Request into Queue	
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
					new SlaveInformation(this.socket.getInetAddress(), this.socket.getPort(), maxAmount,features));
	}

}

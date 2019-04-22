package servers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import utils.ConnectionInformation;

public class MasterConnection extends Connection {

	LoadBalancer balancer;

	public MasterConnection(Socket socket, LoadBalancer balancer) {
		super(socket);

		this.balancer = balancer;
	}

	@Override
	public void run() {

		// 1. Get message from outside with listenSocket
		String message = receive();
		String[] messageParts = message.split(";");

		if (messageParts.length > 0) {

			// 2. If message is from a slave -> Register/DeRegister/ResultOfCalculation
			// 2a If register ->call method "register"
			if (messageParts[0].equals("Register")) {
				balancer.register(new ConnectionInformation(this.socket.getInetAddress(), this.socket.getPort()));

				// 2b If UnRegister -> call method "unregister"
			} else if (messageParts[0].equals("Deregister")) {
				balancer.unregister(new ConnectionInformation(this.socket.getInetAddress(), this.socket.getPort()));

				// 2c if ResultOfCalculation -> forward result to client who called
			} else if (messageParts[0].equals("Result") && messageParts.length == 3) {

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

				// 3. If message comes from a client (StartsWith : Request)
			} else if (messageParts[0].equals("Request") && messageParts.length == 5) {

				String feature = messageParts[1];
				int a = Integer.parseInt(messageParts[2]);
				int b = Integer.parseInt(messageParts[3]);
				String function = messageParts[4];

				// TODO: a - c
				// 3a Get Slaves who implement calculation function needed
				// 3b Enter request in Queue
				// 3c Use Timer Thread who looks periodically in slaves if a slave is free

				// 3d Get Slave with least amount of work
				List<ConnectionInformation> slaves = balancer.getSlaves().stream().sorted()
						.collect(Collectors.toList());
				ConnectionInformation slaveInfo = slaves.get(0);

				Socket socket;
				try {
					socket = new Socket(slaveInfo.getAdress(), slaveInfo.getPort());
					DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
					clientOutput.writeUTF(feature + ";" + a + ";" + b + ";" + function);
					clientOutput.flush();
					socket.close();
					slaveInfo.setOpenRequests(slaveInfo.getOpenRequests() + 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}

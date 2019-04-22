package loadbalancing;

import java.io.IOException;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import features.CalculateMaster;
import features.LoadBalancingMaster;
import features.Slave;
import utils.SynchronizedList;

public class LoadBalancer2 implements CalculateMaster, LoadBalancingMaster {

	private Registry registry;

	private SynchronizedList<Slave> slaves = new SynchronizedList<>();
	private Queue<CalculationRequest> requestsToProcess = new ConcurrentLinkedQueue<>();
	private Socket listenSocket;

	public static void main(String[] args) {

		LoadBalancer2 balancer = new LoadBalancer2();

		try {
			Registry registry = LocateRegistry.createRegistry(9000);

			CalculateMaster stub = (CalculateMaster) UnicastRemoteObject.exportObject(balancer, 9001);
			LoadBalancingMaster stub2 = (LoadBalancingMaster) UnicastRemoteObject.exportObject(balancer, 9002);
			registry.bind("CalcService", stub);
			registry.bind("LoadBalancing", stub2);

		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}

	}

//	@Override
//	public void run() {
//		
//		while(!isInterrupted()) {
//			
//			//1. Get message from outside with listenSocket
//			
//			//2. If message is from a slave -> Register/DeRegister/ResultOfCalculation
//			//2a If register ->call method "register"
//			//2b If UnRegister -> call method "unregister"
//			//2c if ResultOfCalculation -> forward result to client who called
//			
//			//3. If message comes from a client
//			//3a Get Slaves who implement calculation function needed
//			//3b Enter request in Queue
//			//3c Use Timer Thread who looks periodically in slaves if a slave is free
//			//3d If slave is free 
//			//	-> allocate request of the queue to slave 
//			//3e after receivingResult
//			//	-> Send answer to Client on own Thread/Socket??
//			//	-> OR: Let Slave send answer??
//			
//			
//		}
//		
//	}

	@Override
	public void register(Slave slave) throws RemoteException {
		slaves.add(slave);
	}

	@Override
	public void unregister(Slave slave) throws RemoteException {
		if (slaves.contains(slave)) {
			slaves.remove(slaves.indexOf(slave));
		}
	}

	@Override
	public double calculate(double a, double b, String function) throws IOException, RemoteException {

		double erg = 0;
		if (slaves.size() > 0) {
			// TODO: 3a Get Slaves who implement calculation function needed
			// TODO: 3b Enter request in Queue
			// TODO: 3c Use Timer Thread who looks periodically in slaves if a slave is free
			// 3d If slave is free
			// -> allocate request of the queue to slave

			int rndm = new Random().nextInt(slaves.size());

			Slave slave = slaves.get(rndm);
			erg = slave.calculate(a, b, function);
			

		}

		return erg;
	}

}

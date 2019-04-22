package features;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EchoServer implements Runnable, IEcho {

	@Override
	public String sayHello() throws RemoteException {
		return "Hello, world!";
	}

	@Override
	public void run() {
		try {
			EchoServer obj = new EchoServer();
			IEcho stub = (IEcho) UnicastRemoteObject.exportObject(obj, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("Hello", stub);

			System.err.println("Server ready");
		} catch (RemoteException | AlreadyBoundException e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

}

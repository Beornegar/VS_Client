package connections;

import java.net.Socket;
import java.util.List;

import application.SlaveServer;

public class LifeCycleConnection extends Connection {

	private LifeCycleMethods method;
	private int maxAmountOfRequests;
	private String featureString = "";
	private SlaveServer server;
	
	public LifeCycleConnection(Socket socket, LifeCycleMethods method,int maxAmountOfRequests,List<String> features,SlaveServer server) {
		super(socket);
		
		this.method = method;
		features.stream().forEach(s -> featureString += s + ":" );
		
		this.server = server;
		this.maxAmountOfRequests = maxAmountOfRequests;
	}
	
	@Override
	public void run() {
		
		System.out.println("In LifeCylcle-Thread");
		
		
		if(method.equals(LifeCycleMethods.REGISTER)) {	
			
			System.out.println("In Register");
			
			send("Register" + ";" + maxAmountOfRequests + ";" + featureString + ";" + server.getOwnPort());
			String returnMassage = receive();
			
			if(returnMassage.toLowerCase().equals("ok")) {
				System.out.println("registered = true");
				server.setRegistered(true);
			} else {
				System.out.println("registered = false");
				server.setRegistered(false);
			}
			
		} else if(method.equals(LifeCycleMethods.UNREGISTER)) {
			send("Unregister;" + server.getOwnPort());
			String returnMassage = receive();
			
			if(returnMassage.toLowerCase().equals("ok")) {
				server.deregisterMaster();
			} else {
				System.out.println("Could not deregister from master. Message of Master ["+ returnMassage +"]");
			}
			
		} 
		server.setRegisterInProcess(false);
	}
	
}

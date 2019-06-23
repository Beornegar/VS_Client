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
	}
	
	@Override
	public void run() {
		
		if(method.equals(LifeCycleMethods.REGISTER)) {	
			send("Register" + ";" + maxAmountOfRequests + ";" + featureString);
			String returnMassage = receive();
			
			if(returnMassage.toLowerCase().equals("ok")) {
				server.setRegistered(true);
			} else {
				server.setRegistered(false);
			}
			
		} else if(method.equals(LifeCycleMethods.UNREGISTER)) {
			send("Unregister");
			String returnMassage = receive();
			
			if(returnMassage.toLowerCase().equals("ok")) {
				server.deregisterMaster();
			} else {
				System.out.println("Could not deregister from master. Message of Master ["+ returnMassage +"]");
			}
			
		} 
		
	}
	
}

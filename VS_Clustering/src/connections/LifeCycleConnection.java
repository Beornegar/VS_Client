package connections;

import java.net.Socket;
import java.util.List;

public class LifeCycleConnection extends Connection {

	private boolean register;
	private int maxAmountOfRequests;
	private String featureString = "";
	
	public LifeCycleConnection(Socket socket, boolean register,int maxAmountOfRequests,List<String> features) {
		super(socket);
		
		this.register = register;
		features.stream().forEach(s -> featureString += s + ":" );
	}
	
	@Override
	public void run() {
		
		if(register) {	
			send("Register" + ";" + maxAmountOfRequests + ";" + featureString);
			
		} else {
			send("Deregister");
		}
		
	}
	
}

package connections;

import java.net.Socket;
import java.util.List;

public class LifeCycleConnection extends Connection {

	private LifeCycleMethods method;
	private int maxAmountOfRequests;
	private String featureString = "";
	
	public LifeCycleConnection(Socket socket, LifeCycleMethods method,int maxAmountOfRequests,List<String> features) {
		super(socket);
		
		this.method = method;
		features.stream().forEach(s -> featureString += s + ":" );
	}
	
	@Override
	public void run() {
		
		if(method.equals(LifeCycleMethods.REGISTER)) {	
			send("Register" + ";" + maxAmountOfRequests + ";" + featureString);
		} else if(method.equals(LifeCycleMethods.UNREGISTER)) {
			send("Unregister");
		} else if(method.equals(LifeCycleMethods.UPDATE)) {
			send("Update;" + maxAmountOfRequests + ";" + featureString);
		}
		
	}
	
}

package servers;

import java.net.Socket;

public class LifeCycleConnection extends Connection {

	private boolean register;
	
	public LifeCycleConnection(Socket socket, boolean register) {
		super(socket);
		
		this.register = register;		
	}
	
	@Override
	public void run() {
		
		if(register) {	
			send("Register");
			
		} else {
			send("Deregister");
		}
		
	}
	
}

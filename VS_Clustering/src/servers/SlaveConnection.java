package servers;

import java.net.Socket;

import utils.CalculationRequest;

/***
 * 
 * Connection from Master-Server to the Slave-Server
 * which holds the logic
 * 
 * @author Dennis
 *
 */
public class SlaveConnection extends Connection {

	public SlaveConnection(Socket socket) {
		super(socket);
	}
	
	@Override
	public void run() {
		
		while(!isInterrupted()) {
			
			String message = receive();			
			String[] messageParts = message.split(";");
			
			if(messageParts.length == 4) {
			
				double value = calculate(new CalculationRequest(Integer.parseInt(messageParts[1]), Integer.parseInt(messageParts[2]), messageParts[3]) );
				send("Result:" + value);
			}
			
			send("Error! calculation request malformed!");
		}
		
	}
	
	public double calculate(CalculationRequest request) {
		
		double erg = 0;
		
		double a = request.getA();
		double b = request.getB();
		
		switch (request.getFunction()) {

		case "add":
			erg = a + b;
			break;
		case "sub":
			erg = a - b;
			break;
		case "mul":
			erg = a * b;
			break;
		case "div":
			erg = a / b;
			break;
		case "mod":
			erg = a % b;
			break;
		default:
			erg = 0;
		}

		return erg;
	}
	
	
}

package connections;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import utils.Request;

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
			
			
			
			
			String[] argumentParts = messageParts[1].split(":");
			Map<String,String> arguments = new HashMap<>();
			
			for(String a : argumentParts) {
				String[] keyValuePair = a.split("=");
				if(keyValuePair.length == 2) {
					String key = keyValuePair[0];
					String value = keyValuePair[1];
					arguments.put(key, value);
				} else {
					System.out.println("Malformed message in [arguments] :" + message);
				}
			}
			
			//TODO: Call method with Reflection
			//TODO: messageParts[0] is name of feature/function
			//TODO: messageParts[1] = arguments for function -> needs generic approach
			//TODO: Insert parameter values for method with help of the map
			
			
			//For calculation it has "a=2:b=3:function=add"
			if(messageParts.length == 3) {
			
				double value = calculate(Integer.parseInt(arguments.get("a")), Integer.parseInt(arguments.get("b")), arguments.get("function"));
				send("Result;" + messageParts[2] + ";" + value);
			}
			
			send("Error! calculation request malformed!");
		}
		
	}
	
	/***
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 */
	public double calculate(double a, double b, String function) {
		
		double erg = 0;

		switch (function) {

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

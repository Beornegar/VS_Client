package loadbalancing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

import features.Slave;

public class SlaveServer extends Thread implements Slave {

	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	public SlaveServer(Socket socket) {

		if (socket == null) {
			return;
		}
		this.socket = socket;

		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		while (!isInterrupted()) {
			acceptRequest();
		}

		end();
	}

	private void acceptRequest() {

		String message = receive();

		if (message != null) {

			// TODO: Denominator configurable
			String[] messageParts = message.split(";");

			if (messageParts.length == 3) {
				int a = Integer.parseInt(messageParts[0]);
				int b = Integer.parseInt(messageParts[1]);
				String function = messageParts[2];

				try {
					double erg = calculate(a, b, function);

					send("Result;" + erg);

				} catch (IOException e) {
					e.printStackTrace();
				}

			} else if(messageParts.length == 2) {
				
			}
			else {
				System.out.println("Invalid request [Time: " + LocalDateTime.now() + " , Request:" + message + "]");
			}

		}

	}

	/**
	 * Receive a message <br>
	 * 
	 * @return
	 * 
	 */
	public String receive() {
		try {
			String message = dis.readUTF();
			return message;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Send a message
	 * 
	 * @param message
	 */
	private void send(String message) {
		try {
			dos.writeUTF(message);
			dos.flush();
		} catch (SocketException ex) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ends the Thread
	 */
	public void end() {
		try {
			this.socket.close();
			this.socket = null;
			this.dis = null;
			this.dos = null;

		} catch (SocketException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.interrupt();
	}

	@Override
	public double calculate(double a, double b, String function) throws IOException, RemoteException {

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

	//TODO: Functions as Enum
	@Override
	public String getFunctions() throws RemoteException {
		
		String functions = "add;" + "sub;" + "mul;" + "div;" + "mod";
		
		return functions;
	}

}
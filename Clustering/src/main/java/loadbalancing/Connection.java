package loadbalancing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Connection extends Thread {

	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	public Connection(Socket socket) {

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
		return "";
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

}
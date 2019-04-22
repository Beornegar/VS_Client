package utils;

import java.net.InetAddress;

public class ConnectionInformation implements Comparable<ConnectionInformation> {

	private InetAddress adress;
	private int port;
	private int openRequests;
	
	public ConnectionInformation(InetAddress address ,int port) {
		this.adress = address;
		this.port = port;
	}
	
	public InetAddress getAdress() {
		return adress;
	}
	public void setAdress(InetAddress adress) {
		this.adress = adress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public int getOpenRequests() {
		return openRequests;
	}

	public void setOpenRequests(int openRequests) {
		this.openRequests = openRequests;
	}

	@Override
	public int compareTo(ConnectionInformation arg0) {
		
		if(this.openRequests == arg0.getOpenRequests()) {
			return 0;
		} else if(this.openRequests < arg0.getOpenRequests()) {
			return -1;
		} else {
			return 1;
		}
	}
	
}

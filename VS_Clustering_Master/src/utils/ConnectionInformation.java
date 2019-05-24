package utils;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

public class ConnectionInformation {

	private InetAddress adress;
	private int port;
	private List<UUID> listOfOpenRequests = new SynchronizedList<>();

	protected static final Object LOCK = new Object();

	public ConnectionInformation(InetAddress address, int port) {
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

	public List<UUID> getListOfOpenRequests() {
		return listOfOpenRequests;
	}

	@Override
	public String toString() {
		return "ConnectionInformation [adress=" + adress + ", port=" + port + "]";
	}

}

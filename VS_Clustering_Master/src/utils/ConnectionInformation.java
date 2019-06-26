package utils;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

public class ConnectionInformation {

	protected InetAddress adress;
	protected int port;
	protected List<UUID> listOfOpenRequests = new SynchronizedList<>();

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
		
		String erg = "ConnectionInformation [adress=" + adress + ", port=" + port + ",OpenRequests= ["; 
		
		for(UUID u : listOfOpenRequests) {
			erg += u.toString();
		}
		
		erg +=  "] ]";
		
		return erg;
	}

}

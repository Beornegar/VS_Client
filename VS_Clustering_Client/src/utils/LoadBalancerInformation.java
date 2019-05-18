package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LoadBalancerInformation {

	public LoadBalancerInformation(String address, int port) {
		super();
		try {
			this.adress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			System.out.println("Could not resolve address to InetAddress");
			e.printStackTrace();
		}
		this.port = port;
	}
	
	private InetAddress adress;
	private int port;
	
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
	
	@Override
	public String toString() {
		return "LoadBalancerInformation [adress=" + adress + ", port=" + port + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adress == null) ? 0 : adress.hashCode());
		result = prime * result + port;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoadBalancerInformation other = (LoadBalancerInformation) obj;
		if (adress == null) {
			if (other.adress != null)
				return false;
		} else if (!adress.equals(other.adress))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
	
	
	
}

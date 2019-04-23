package utils;

import java.net.InetAddress;

public class Request {

	private String feature;
	private String arguments;

	private InetAddress clientName;
	private int clientPort;

	public Request(String feature, String arguments, InetAddress clientName, int clientPort) {
		super();
		this.feature = feature;
		this.arguments = arguments;

		this.clientName = clientName;
		this.clientPort = clientPort;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public InetAddress getClientName() {
		return clientName;
	}

	public void setClientName(InetAddress clientName) {
		this.clientName = clientName;
	}

	public int getClientPort() {
		return clientPort;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}
}

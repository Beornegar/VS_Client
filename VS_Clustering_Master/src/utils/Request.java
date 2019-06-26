package utils;

import java.net.InetAddress;
import java.util.UUID;

public class Request {

	private UUID guid;
	private String message;
	
	private String feature;
	private String arguments;

	private InetAddress clientName;
	private int clientPort;

	public Request(String feature, String arguments, InetAddress clientName, int clientPort,UUID guid) {
		super();
		this.feature = feature;
		this.arguments = arguments;

		this.clientName = clientName;
		this.clientPort = clientPort;
		this.guid = guid;
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

	public UUID getGuid() {
		return guid;
	}

	public void setGuid(UUID guid) {
		this.guid = guid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

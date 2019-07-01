package utils;

import java.net.InetAddress;
import java.util.UUID;

public class ClientRequest {

	protected InetAddress adress;
	protected int port;

	private String message;
	
	private String feature;
	private String arguments;
	
	protected UUID guid;
	
	private boolean inProgress;
	
	protected static final Object LOCK = new Object();

	public ClientRequest(InetAddress address, int port, UUID guid, String feature, String arguments) {
		this.adress = address;
		this.port = port;
		this.guid = guid;
		this.feature = feature;
		this.arguments = arguments;
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

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

}

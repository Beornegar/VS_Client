package utils;

public class Request {

	private String feature;
	private String arguments;

	public Request(String feature, String arguments) {
		super();
		this.feature = feature;
		this.arguments = arguments;
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
}

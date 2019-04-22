package utils;

public class CalculationRequest {

	private double a;
	private double b;
	private String function;


	public CalculationRequest(double a, double b, String function) {
		this.a = a;
		this.b = b;
		this.function = function;
	}
	
	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}
	
	
}

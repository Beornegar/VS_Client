package utils;

public class MathParameter {

	public MathParameter(double a, double b, MathOperations operation) {
		super();
		this.a = a;
		this.b = b;
		this.operation = operation;
	}

	private double a;
	private double b;
	
	private MathOperations operation;

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

	public MathOperations getOperation() {
		return operation;
	}

	public void setOperation(MathOperations operation) {
		this.operation = operation;
	}

	@Override
	public String toString() {
		return "MathParameter [a=" + a + ", b=" + b + ", operation=" + operation + "]";
	}
	
}

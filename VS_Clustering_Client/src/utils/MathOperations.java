package utils;

public enum MathOperations {

	Add("add"),
	Substract("sub"),
	Multiply("mul"),
	Divide("div"),
	Modulo("mod");
	
	private final String value;
	
	MathOperations(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

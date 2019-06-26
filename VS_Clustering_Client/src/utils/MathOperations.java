package utils;

public enum MathOperations {

	add("add"),
	sub("sub"),
	mul("mul"),
	div("div"),
	mod("mod");
	
	private final String value;
	
	MathOperations(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

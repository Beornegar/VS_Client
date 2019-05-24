package connections;


public enum LifeCycleMethods {
    REGISTER("register"),
    UNREGISTER("unregister"),
    UPDATE("update");

    private final String value;

    LifeCycleMethods(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    /**
     * @return the Enum representation for the given string.
     * @throws IllegalArgumentException if unknown string.
     */
    public static LifeCycleMethods fromString(String s) throws IllegalArgumentException {
        
        for(LifeCycleMethods pM:LifeCycleMethods.values()) {
            if(pM.value.equals(s.toLowerCase())) {
                return pM;
            }
        }
        
        throw new IllegalArgumentException("unknown value: " + s+" valid Values are: "+ LifeCycleMethods.values());
    }

}
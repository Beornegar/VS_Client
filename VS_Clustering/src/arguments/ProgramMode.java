package arguments;

public enum ProgramMode {
    MASTER("master"),
    CLIENT("client"),
    SLAVE("slave");

    private final String value;

    ProgramMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    /**
     * @return the Enum representation for the given string.
     * @throws IllegalArgumentException if unknown string.
     */
    public static ProgramMode fromString(String s) throws IllegalArgumentException {
        
        for(ProgramMode pM:ProgramMode.values()) {
            if(pM.value.equals(s.toLowerCase())) {
                return pM;
            }
        }
        
        throw new IllegalArgumentException("unknown value: " + s+" valid Values are: "+ProgramMode.values());
    }

}
package arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * OptionsContainer to Parse Arguments from Commandline and get it into an Proper object for easier
 * handling Also provides read methods and a default implementation and allowed parameters.
 * 
 * @author vdinger
 *
 */
public class OptionsContainer {
    protected HashMap<String, String> optionsHashMap = new HashMap<String, String>();
    protected List<String> allowedOptions = null;
    protected static HashMap<String, String> defaultOptionsHashMap = null;
    private static final String[] allowed = new String[] {"-verbose"// Put out all comments?
    };

    public OptionsContainer() {
        if (allowedOptions == null) {
            allowedOptions = new ArrayList<String>();
            Collections.addAll(allowedOptions, allowed);
        }

        InitDefaults();
    }

    /**
     * Parses parameters into this {@link OptionsContainer} and previously validates them
     * 
     * @param param
     * @param value
     */
    public void setParameterValue(String param, String value) {
        try {
            if (allowedOptions.contains(param)) {
                optionsHashMap.put(param, value);
            } else {
                throw new IllegalArgumentException("Parameter " + param + " is not valid. Typo?");
            }
        } catch (IllegalArgumentException e) {

            System.err.println("Error: Undefined parameter '" + param + " " + value + "'");
            System.err.println(e.getMessage());
            System.err.println("Some defaults might be used instead");
        }
    }

    public String getParameterValue(String param) {
        String ret;
        ret = optionsHashMap.get(param);
        if (ret != null)
            return ret;

        if (!allowedOptions.contains(param)) {
            System.err.println("Parameter " + param + " is not valid. Typo?");
            return "";
        }

        ret = defaultOptionsHashMap.get(param);

        optionsHashMap.put(param, ret);
        return ret;

    }

    /**
     * Parses String into Integer
     * 
     * @param s
     * @return
     */
    protected int makeInteger(String s) {
        return Integer.parseInt(s);
    }

    /**
     * parse String into Float
     * 
     * @param s
     * @return
     */
    protected float makeFloat(String s) {
        return Float.parseFloat(s);
    }

    /**
     * 
     * @param b
     * @return
     */
    protected String makeBoolString(boolean b) {
        return b ? "on" : "off";
    }

    /**
     * parse any object as String
     * 
     * @param i
     * @return
     */
    protected String makeString(Object i) {
        return String.valueOf(i);
    }

    /**
     * Parse on off String into boolean
     * 
     * @param s
     * @return
     */
    protected boolean b(String s) {
        if ("on".equals(s) || "true".equals(s) || "1".equals(s))
            return true;
        if ("off".equals(s) || "false".equals(s) || "0".equals(s))
            return false;


        throw new Error("\n ~ Wrong parameter value got <" + s
                + "> whereas expected 'on' or 'off' or 'true' or 'false' or '1' or '0'\n ~ Execution Terminated");
    }

    /**
     * Init for Default Values, which will be taken if nothing is there
     */
    private static void InitDefaults() {
        if (defaultOptionsHashMap == null) {
            defaultOptionsHashMap = new HashMap<String, String>();

            defaultOptionsHashMap.put("-verbose", "false");
         

        }
    }

    public static int getTotalNumberOfOptions() {
        return defaultOptionsHashMap.size();
    }

    public static int getNumberOfAllowedOptions() {
        return allowed.length;
    }

    public String getDefaultParameterValue(String param) {
        if (allowedOptions.contains(param)) {
            assert (defaultOptionsHashMap.get(param) != null);
            return defaultOptionsHashMap.get(param);
        } else {
            System.err.println("Requires for Default Parameter " + param + " Failed. Typo?");
            return "";
        }
    }

    public boolean getVerbose() {
        return this.b(this.getParameterValue("-verbose"));
    }


    /**
     * Parts single {@link String} by spaces into {@link String[]} and sets {@link OptionsContainer}
     * s values by this {@link String[]}
     * 
     * @param argString
     */
    public void setArgs(String argString) {
        if (!"".equals(argString))
            this.setArgs(
                    argString.trim()
                            .split("\\s+"));
        else
            this.setArgs((String[]) null);
    }

    /**
     * Sets args of {@link String[]} to this Options
     * 
     * @param args
     */
    public void setArgs(String[] args) {
        if (args != null)
            for (int i = 0; i < args.length - 1; i += 2)
                try {
                    String arg = args[i];
                    String value = args[i + 1];

                    if (arg != null)
                        arg = arg.toLowerCase();
                    if (value != null)
                        value = value.toLowerCase();
                    this.setParameterValue(arg, value);
                } catch (ArrayIndexOutOfBoundsException e) {

                    System.err.println("Error: Wrong number of input parameters");
                }
    }

    /**
     * Prints {@link OptionsContainer} in Human readable form
     * 
     * @return
     */
    public String toStringSingleLine() {
        return "\n[Options]" + this.optionsHashMap + "\n[Defaults]" + OptionsContainer.defaultOptionsHashMap;
    }

}

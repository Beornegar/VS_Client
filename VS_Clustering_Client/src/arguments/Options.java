package arguments;

import java.util.Collections;
import java.util.HashMap;

/**
 * OptionsContainer to Parse Arguments from Commandline and get it into an Proper object for easier
 * handling Also provides read methods and a default implementation and allowed parameters.
 * 
 * @author vdinger
 *
 */
public class Options extends OptionsContainer {
    protected HashMap<String, String> optionsHashMap = new HashMap<String, String>();
   
    private final String[] allowed = new String[] {
    		"-port"
    };

    public Options() {
        super();
        Collections.addAll(super.allowedOptions, allowed);
        InitDefaults();
    }
    
    /**
     * Init for Default Values, which will be taken if nothing is there
     */
    private void InitDefaults() {
        if (defaultOptionsHashMap == null) {
            defaultOptionsHashMap = new HashMap<String, String>();
        }
        defaultOptionsHashMap.put("-port", "10001");
    }

    public int getPort() {
		return this.makeInteger(getParameterValue("-port"));
	}
    
}

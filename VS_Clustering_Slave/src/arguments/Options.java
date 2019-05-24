package arguments;

import java.util.Collections;
import java.util.HashMap;

/**
 * OptionsContainer to Parse Arguments from Commandline and get it into an
 * Proper object for easier handling Also provides read methods and a default
 * implementation and allowed parameters.
 * 
 * @author vdinger
 *
 */
public class Options extends OptionsContainer {
	protected HashMap<String, String> optionsHashMap = new HashMap<String, String>();

	private final String[] allowed = new String[] { "-verbose", // Put out all comments?
			"-port", // with port
			"-masterport", // port of master -> only needed for slaves
			"-masteraddress", // address of master -> only needed for slaves
			"-maxamountofrequests" // Amount of parallel processible requests
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
		defaultOptionsHashMap.put("-port", "626");
		defaultOptionsHashMap.put("-maxamountofrequests", "0");
		defaultOptionsHashMap.put("-masterport", "9000");
		defaultOptionsHashMap.put("-masteraddress", "127.0.0.1");

	}

	public int getPort() {
		return this.makeInteger(getParameterValue("-port"));
	}

	public int getMaxAmountOfRequests() {
		return this.makeInteger(getParameterValue("-maxamountofrequests"));
	}

	public int getMasterPort() {
		return this.makeInteger(getParameterValue("-masterport"));
	}

	public String getMasterAddress() {
		return this.getParameterValue("-masteraddress");
	}

}

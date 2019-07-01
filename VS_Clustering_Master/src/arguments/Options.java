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
			"-queueamount", // number of elements per queue run
			"-queuefrequency" // frequency of queue run
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
		defaultOptionsHashMap.put("-port", "10000");
		defaultOptionsHashMap.put("-queuefrequency", "10");
		defaultOptionsHashMap.put("-queueamount", "10");
	}

	public int getPort() {
		return this.makeInteger(getParameterValue("-port"));
	}
	
	public int getQueueFrequency() {
		return this.makeInteger(getParameterValue("-queuefrequency"));
	}
	
	public int getQueueAmount() {
		return this.makeInteger(getParameterValue("-queueamount"));
	}
}

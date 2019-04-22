package application;

import arguments.ArgumentParsing;
import arguments.Options;
import utils.Configuration;

public class Application {

	private static Options options = null;
	
	public static void main(String[] args) {
			
		if(args.length == 0) {
			Configuration.loadConfig();
			options.setParameterValue("verbose", Configuration.isVerbose() + "");
			
			options.setParameterValue("masterport", Configuration.getMasterPort() + "");
			options.setParameterValue("masteraddress", Configuration.getMasterAddress() + "");
			options.setParameterValue("serverport", Configuration.getServerPort() + "");
			
			options.setParameterValue("mode", Configuration.getMode().getValue());
			
		} else {
			options = ArgumentParsing.parseStringsFromArgs(args);
		}
		
		
		
		
	}
	
	
}

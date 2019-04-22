package utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SlaveInformation extends ConnectionInformation {

	private int maxAmountOfParallelRequests;
	private List<String> listOfFeatures = new ArrayList<>();
	
	public SlaveInformation(InetAddress address, int port) {
		super(address, port);
	}
	
	public SlaveInformation(InetAddress address, int port,int maxAmountOfParallelRequests, String[] features) {
		super(address, port);
		
		this.maxAmountOfParallelRequests = maxAmountOfParallelRequests;
		
		if(features.length > 0) {
			
			for(int i = 0; i < features.length; i++) {
				listOfFeatures.add(features[i].toLowerCase());
			}
			
		}
		
	}

	public int getMaxAmountOfParallelRequests() {
		return maxAmountOfParallelRequests;
	}

	public void setMaxAmountOfParallelRequests(int maxAmountOfParallelRequests) {
		this.maxAmountOfParallelRequests = maxAmountOfParallelRequests;
	}

	public List<String> getListOfFeatures() {
		return listOfFeatures;
	}

	public void setListOfFeatures(List<String> listOfFeatures) {
		this.listOfFeatures = listOfFeatures;
	}

}

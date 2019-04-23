package utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SlaveInformation extends ConnectionInformation {

	private int maxAmountOfParallelRequests;
	private List<String> listOfFeatures = new SynchronizedList<>();

	private static final Object REQUESTLOCK = new Object();
	private static final Object LISTLOCK = new Object();

	public SlaveInformation(InetAddress address, int port) {
		super(address, port);
	}

	public SlaveInformation(InetAddress address, int port, int maxAmountOfParallelRequests, String[] features) {
		super(address, port);

		this.maxAmountOfParallelRequests = maxAmountOfParallelRequests;

		if (features.length > 0) {

			for (int i = 0; i < features.length; i++) {
				listOfFeatures.add(features[i].toLowerCase());
			}

		}

	}

	public int getMaxAmountOfParallelRequests() {
		synchronized (REQUESTLOCK) {
			return maxAmountOfParallelRequests;
		}
	}

	public void setMaxAmountOfParallelRequests(int maxAmountOfParallelRequests) {
		synchronized (REQUESTLOCK) {
			this.maxAmountOfParallelRequests = maxAmountOfParallelRequests;
		}
	}

	public List<String> getListOfFeatures() {
		return listOfFeatures;
	}

	public void setListOfFeatures(List<String> listOfFeatures) {
		synchronized (LISTLOCK) {
			this.listOfFeatures = listOfFeatures;
		}
	}

}

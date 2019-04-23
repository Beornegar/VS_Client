package utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	//TODO: Validate that the the lock works as it should because the timer and the Master-Connections can perhaps take the same slave even if it only has capacities for one of them
	//TODO: Find good place for this method
	public static SlaveInformation getFreeSlaveWithLeastAmountOfWork(List<SlaveInformation> slaves, String feature) {
		SlaveInformation slaveInfo = null;
		
		slaves = slaves.stream().sorted().collect(Collectors.toList());
		
		for (SlaveInformation s : slaves) {
			if (s.getMaxAmountOfParallelRequests() > s.getOpenRequests() && s.getListOfFeatures().contains(feature)) {
				slaveInfo = s;
				break;
			}
		}
		return slaveInfo;
	}
	
}

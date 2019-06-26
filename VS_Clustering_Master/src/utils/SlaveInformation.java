package utils;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
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

	public static SlaveInformation getFreeSlaveWithLeastAmountOfWork(List<SlaveInformation> slaves, String feature) {
		SlaveInformation slaveInfo = null;
		
		slaves = slaves.stream().sorted().collect(Collectors.toList());
		
		for (SlaveInformation s : slaves) {
			if (s.getMaxAmountOfParallelRequests() > s.getListOfOpenRequests().size() && s.getListOfFeatures().contains(feature)) {
				slaveInfo = s;
				break;
			}
		}
		return slaveInfo;
	}

	@Override
	public String toString() {
		return "SlaveInformation [maxAmountOfParallelRequests=" + maxAmountOfParallelRequests + ", listOfFeatures="
				+ listOfFeatures + "]";
	}

	
}

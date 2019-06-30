package utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SlaveInformation implements Comparable<SlaveInformation> {

	private int maxAmountOfParallelRequests;
	
	private List<String> listOfFeatures = new SynchronizedList<>();
	private List<UUID> guids = Collections.synchronizedList(new ArrayList<UUID>());

	protected InetAddress address;
	protected int port;

	public SlaveInformation(InetAddress address, int port) {
		
	}

	public SlaveInformation(InetAddress address, int port, int maxAmountOfParallelRequests, String[] features) {
		
		this.address = address;
		this.port = port;
		this.maxAmountOfParallelRequests = maxAmountOfParallelRequests;

		if (features.length > 0) {

			for (int i = 0; i < features.length; i++) {
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

	public synchronized static SlaveInformation getFreeSlaveWithLeastAmountOfWork(List<SlaveInformation> slaves, String feature, UUID guid) {
		
		System.out.println("Feature:" + feature);
		
		SlaveInformation slaveInfo = null;

		slaves = slaves.stream().sorted().collect(Collectors.toList());
		
		for (SlaveInformation s : slaves) {
			System.out.println("SlaveInformation: Open Requests: " + s.getGuids().size());
			if (s.getMaxAmountOfParallelRequests() > s.getGuids().size() && s.getListOfFeatures().contains(feature)) {
				slaveInfo = s;
				System.out.println("Free slave: " + s.getAddress() + ":" + s.getPort() + " Requests: " + s.getAmountOfCurrentRequests() + "/" + s.getGuids().size());
				break;
			}
		}
		
		if(slaveInfo != null) {
			slaveInfo.getGuids().add(guid);
		}
		
		return slaveInfo;
	}

	@Override
	public String toString() {
		return "SlaveInformation [adress="+address+ ", port=" + port +", maxAmountOfParallelRequests=" + maxAmountOfParallelRequests + ", listOfFeatures="
				+ listOfFeatures + "]";
	}

	@Override
	public int compareTo(SlaveInformation o) {
		
		if(this.getAmountOfCurrentRequests() == o.getAmountOfCurrentRequests()){
			return 0;
		} else if(this.getAmountOfCurrentRequests() < o.getAmountOfCurrentRequests()) {
			return -1;
		} else {
			return 1;
		}
	}

	public synchronized int getAmountOfCurrentRequests() {
		return this.getGuids().size();
	}

	public List<UUID> getGuids() {
		return guids;
	}

	public void setGuids(List<UUID> guids) {
		this.guids = guids;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}

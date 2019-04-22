package application;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import arguments.ArgumentParsing;
import arguments.Options;
import arguments.ProgramMode;
import servers.LoadBalancer;
import servers.SlaveServer;
import utils.Configuration;


public class Application {

	private static Options options = null;
	private static boolean notEnded = true;
	
	private static LoadBalancer balancer = null;
	private static SlaveServer slaveServer = null;
	
	public static void main(String[] args) {
			
		if(args.length == 0) {
			Configuration.loadConfig();
			options.setParameterValue("verbose", Configuration.isVerbose() + "");
			
			options.setParameterValue("masterport", Configuration.getMasterPort() + "");
			options.setParameterValue("masteraddress", Configuration.getMasterAddress() + "");
			options.setParameterValue("serverport", Configuration.getServerPort() + "");
			
			options.setParameterValue("mode", Configuration.getMode().getValue());
			options.setParameterValue("maxamountofrequests", Configuration.getMaxAmountOfRequests()+ "");
			
		} else {
			options = ArgumentParsing.parseStringsFromArgs(args);
		}
		
		if(options.getProgramMode().equals(ProgramMode.MASTER)) {
			balancer = new LoadBalancer(options.getMasterPort());
			balancer.start();
		}else if(options.getProgramMode().equals(ProgramMode.SLAVE)) {
			slaveServer = new SlaveServer(options.getPort(),options.getMasterAddress(),options.getMasterPort(), options.getMaxAmountOfRequests());
			slaveServer.start();
		} else {
			System.out.println("Undefined Mode");
		}

		runApplication();
		
	}
	
	/**
	 * 
	 * Life-Cicle-Method. Accepting input from Console <br>
	 * after user terminates the application, it will save the keys in a logfile <br>
	 * then finish the application
	 * 
	 */
	private static void runApplication() {
		Scanner sc = new Scanner(System.in);
		getConsoleInput(sc);
		sc.close();
		System.out.println("Server closing...");
		System.exit(0);
	}

	/**
	 * 
	 * Method, that scans the console for input <br>
	 * Possible commands: <br>
	 * 1.)end : Ends the application <br>
	 * 2.)connect: Starts key exchange with given ip/port
	 * 
	 * @param sc
	 */
	private static void getConsoleInput(Scanner sc) {
		while (notEnded) {		
			if(sc.hasNext())
			{
				
				System.out.println("--------------------------------------");
				
				System.out.println("Possible commands:");
				System.out.println("End : Ends the server");
				
				if(options.getProgramMode().equals(ProgramMode.MASTER)) {
					System.out.println("GetAllSlaves : get all registered slaves");
					System.out.println("GetOpenRequests : get all pending requests");
				}
				
				if(options.getProgramMode().equals(ProgramMode.SLAVE)) {
					System.out.println("DeregisterFromMaster : Deregister slave from master");
					System.out.println("RegisterToMaster : Register slave to master");
				}
				
				System.out.println("--------------------------------------");
				
				String s = sc.nextLine();
				
				if(s.toLowerCase().equals("end"))
				{
					notEnded = false;
					
					if(balancer != null) {
						balancer.interrupt();
					}
					
					if(slaveServer != null) {
						slaveServer.interrupt();
					}
					
					break;
				} else if(s.toLowerCase().equals("registertomaster") && options.getProgramMode().equals(ProgramMode.SLAVE)) {
					System.out.print("Enter ip: ");
					String ip = sc.nextLine();
					System.out.println();
					System.out.print("Enter port: ");
					int port = sc.nextInt();
					
					try {
						slaveServer.registerWithMasterServer(InetAddress.getByName(ip),port,true);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				} else if(s.toLowerCase().equals("deregisterfrommaster") && options.getProgramMode().equals(ProgramMode.SLAVE)) {
					System.out.print("Enter ip: ");
					String ip = sc.nextLine();
					System.out.println();
					System.out.print("Enter port: ");
					int port = sc.nextInt();
					
					try {
						slaveServer.registerWithMasterServer(InetAddress.getByName(ip),port,false);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				} else if(s.toLowerCase().equals("getallslaves".toLowerCase()) && options.getProgramMode().equals(ProgramMode.MASTER)) {
					
					balancer.getSlaves().stream().forEach(ci -> System.out.println(ci));
					
				} else if(s.toLowerCase().equals("getallopenrequests") && options.getProgramMode().equals(ProgramMode.MASTER)) {
					
					balancer.getClientRequests().stream().forEach(ci -> System.out.println(ci));
					
				}
				
			}
		}
	}
	


	
	
}

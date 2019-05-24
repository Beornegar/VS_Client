package application;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import arguments.ArgumentParsing;
import arguments.Configuration;
import arguments.Options;

import connections.LifeCycleMethods;

public class Application {

	private static Options options = new Options();
	private static boolean notEnded = true;

	private static SlaveServer slaveServer = null;

	public static void main(String[] args) {

		if (args.length == 0) {
			Configuration.loadConfig();
			options.setParameterValue("-verbose", Configuration.isVerbose() + "");
			
			options.setParameterValue("-masterport", Configuration.getMasterPort() + "");
			options.setParameterValue("-masteraddress", Configuration.getMasterAddress() + "");
			
			options.setParameterValue("-port", Configuration.getServerPort() + "");
			options.setParameterValue("-maxamountofrequests", Configuration.getMaxAmountOfRequests() + "");

		} else {
			options = ArgumentParsing.parseStringsFromArgs(args);
		}

		slaveServer = new SlaveServer(options.getPort(), options.getMasterAddress(), options.getMasterPort(),
				options.getMaxAmountOfRequests());
		slaveServer.start();

		runApplication();

	}

	/**
	 * 
	 * Life-Cicle-Method. Accepting input from Console <br>
	 * after user terminates the application, it will save the keys in a logfile
	 * <br>
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
			if (sc.hasNext()) {

				System.out.println("--------------------------------------");

				System.out.println("Possible commands:");
				System.out.println("End : Ends the server");

				System.out.println("DeregisterFromMaster : Deregister slave from master");
				System.out.println("RegisterToMaster : Register slave to master");
				System.out.println("Update : Update slave info at master");

				System.out.println("--------------------------------------");

				String s = sc.nextLine();

				if (s.toLowerCase().equals("end")) {
					notEnded = false;

					if (slaveServer != null) {
						slaveServer.interrupt();
					}

					break;
				} else if (s.toLowerCase().equals("registertomaster")) {
					System.out.print("Enter ip: ");
					String ip = sc.nextLine();
					System.out.println();
					System.out.print("Enter port: ");
					int port = sc.nextInt();

					try {
						slaveServer.registerWithMasterServer(InetAddress.getByName(ip), port,
								LifeCycleMethods.REGISTER);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				} else if (s.toLowerCase().equals("deregisterfrommaster")) {
					System.out.print("Enter ip: ");
					String ip = sc.nextLine();
					System.out.println();
					System.out.print("Enter port: ");
					int port = sc.nextInt();

					try {
						slaveServer.registerWithMasterServer(InetAddress.getByName(ip), port,
								LifeCycleMethods.UNREGISTER);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				} else if (s.toLowerCase().equals("update")) {

					System.out.print("Enter ip: ");
					String ip = sc.nextLine();
					System.out.println();
					System.out.print("Enter port: ");
					int port = sc.nextInt();
					System.out.println();
					System.out.print("Enter max amount of parallel requests: ");
					int maxAmount = sc.nextInt();
					slaveServer.setMaxAmountOfRequests(maxAmount);

					try {
						slaveServer.registerWithMasterServer(InetAddress.getByName(ip), port, LifeCycleMethods.UPDATE);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}

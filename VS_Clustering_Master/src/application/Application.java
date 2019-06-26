package application;

import java.util.Scanner;

import arguments.ArgumentParsing;
import arguments.Configuration;
import arguments.Options;
import servers.LoadBalancer;

public class Application {

	private static Options options = new Options();
	private static boolean notEnded = true;

	private static LoadBalancer balancer = null;

	public static void main(String[] args) {

		if (args.length == 0) {
			Configuration.loadConfig();

			options.setParameterValue("-verbose", Configuration.isVerbose() + "");
			options.setParameterValue("-port", Configuration.getServerPort() + "");

		} else {
			options = ArgumentParsing.parseStringsFromArgs(args);
		}

		balancer = new LoadBalancer(options.getPort());
		balancer.start();
		System.out.println("Loadbalaner/Master running now on port: " + options.getPort());
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
				System.out.println();
				System.out.println("--------------------------------------");

				System.out.println("Possible commands:");
				System.out.println("End : Ends the server");

				System.out.println("GetAllSlaves : get all registered slaves");
				System.out.println("GetOpenRequests : get all pending requests");

				System.out.println("--------------------------------------");
				System.out.println();

				String s = sc.nextLine();

				if (s.toLowerCase().equals("end")) {
					notEnded = false;

					if (balancer != null) {
						balancer.interrupt();
					}

					break;
				} else if (s.toLowerCase().equals("getallslaves".toLowerCase())) {
					System.out.println();
					System.out.println("-----------------OPEN SLAVES---------------------");
					balancer.getSlaves().stream().forEach(ci -> System.out.println(ci));
					System.out.println("--------------------------------------");
					System.out.println();
				} else if (s.toLowerCase().equals("getallopenrequests")) {
					System.out.println();
					System.out.println("-----------------OPEN REQUESTS---------------------");
					balancer.getClientRequests().stream().forEach(ci -> System.out.println(ci));
					System.out.println("--------------------------------------");
					System.out.println();
				}
			}
		}
	}
}

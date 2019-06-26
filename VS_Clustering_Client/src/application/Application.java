package application;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import arguments.ArgumentParsing;
import arguments.Options;
import utils.LoadBalancerInformation;
import utils.MathOperations;
import utils.MathParameter;

public class Application {

	private static MathServer server;
	private static Options options = null;
	private static boolean notEnded = true;
	
	private static Set<LoadBalancerInformation> loadbalancer = new HashSet<>();

	public static void main(String[] args) {

		options = ArgumentParsing.parseStringsFromArgs(args);
		server = new MathServer(options.getPort());

		server.start();

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
		System.out.println("--------------");
		System.out.println("Possible commands:");
		System.out.println("registerLoadBalancer");
		System.out.println("end");
		System.out.println("MathRequest");
		System.out.println("SendSeveralRequests");
		System.out.println("--------------");
		System.out.println();
		
		
		while (notEnded) {
			if (sc.hasNext()) {
				System.out.print("Command:");
				String s = sc.nextLine();

				if (s.toLowerCase().equals("end")) {
					notEnded = false;
					server.close();
					break;
				} else if (s.toLowerCase().equals("registerloadbalancer")) {

					System.out.println("----------- Registered Loadbalancer -------------");
					for (LoadBalancerInformation info : loadbalancer) {
						System.out.println(info.toString());
					}
					System.out.println("------------------------------------------------");
					System.out.println();

					System.out.print("Enter Loadbalancer-IP: ");
					String ip = sc.nextLine();
					System.out.println();
					System.out.print("Enter Loadbalancer-Port: ");
					int port = sc.nextInt();

					LoadBalancerInformation info = new LoadBalancerInformation(ip, port);
					loadbalancer.add(info);

				} else if (s.toLowerCase().equals("mathrequest")) {
					System.out.println();
					System.out.println("----------- Available Loadbalancer -------------");
					for (LoadBalancerInformation info : loadbalancer) {
						System.out.println(info.toString());
					}
					System.out.println("------------------------------------------------");
					System.out.println();

					System.out.print("Enter Loadbalancer-IP: ");
					String ip = sc.nextLine();
					System.out.println();
					System.out.print("Enter Loadbalancer-Port: ");
					int port = sc.nextInt();

					System.out.println();
					System.out.print("Parameter a: ");
					double a = sc.nextDouble();

					System.out.println();
					System.out.print("Parameter b: ");
					double b = sc.nextDouble();

					System.out.println();
					System.out.println("Operations: add,sub,div,mul,mod");
					System.out.print("Operation: ");
					String op = sc.next().toLowerCase();

					MathParameter params = new MathParameter(a, b, MathOperations.valueOf(op));

					server.sendMathRequest(ip, port, params, UUID.randomUUID().toString());
				} else if (s.toLowerCase().equals("sendseveralrequests")) {

					System.out.println();
					System.out.println("----------- Available Loadbalancer -------------");
					for (LoadBalancerInformation info : loadbalancer) {
						System.out.println(info.toString());
					}
					System.out.println("------------------------------------------------");
					System.out.println();

					System.out.print("Enter Loadbalancer-IP: ");
					String ip = sc.nextLine();
					System.out.println();
					System.out.print("Enter Loadbalancer-Port: ");
					int port = sc.nextInt();

					System.out.println();
					System.out.print("Parameter a: ");
					double a = sc.nextDouble();

					System.out.println();
					System.out.print("Parameter b: ");
					double b = sc.nextDouble();

					System.out.println();
					System.out.println("Operations: add,sub,div,mul,mod");
					System.out.print("Operation: ");
					String op = sc.next().toLowerCase();
					
					System.out.println();
					System.out.print("Number of requests: ");
					int numberOfRequests = sc.nextInt();

					MathParameter params = new MathParameter(a, b, MathOperations.valueOf(op));
					
					for(int i = 0; i < numberOfRequests; i++) {
						server.sendMathRequest(ip, port, params, UUID.randomUUID().toString());
					}
					
				} else if(s.toLowerCase().equals("help")) {
					System.out.println("--------------");
					System.out.println("Possible commands:");
					System.out.println("registerLoadBalancer");
					System.out.println("end");
					System.out.println("MathRequest");
					System.out.println("SendSeveralRequests");
					System.out.println("--------------");
					System.out.println();
				}

				System.out.print("Command:");
				
			}
		}
	}

}

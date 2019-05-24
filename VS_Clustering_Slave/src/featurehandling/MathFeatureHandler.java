package featurehandling;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MathFeatureHandler implements FeatureHandling {

	public String[] getFeatures() {

		Class<?> cls = this.getClass();

		Method m[] = cls.getDeclaredMethods();
		List<Method> methodList = new ArrayList<>(Arrays.asList(m));

		List<String> featureList = methodList.stream().filter(me -> !me.getName().equals("getFeatures")
				&& !me.getName().equals("processRequest") && me.getModifiers() == 1).map(me -> me.getName())
				.collect(Collectors.toList());

		String[] featureMethods = new String[featureList.size()];
		for (int i = 0; i < featureList.size(); i++) {
			featureMethods[i] = featureList.get(i);
		}

		return featureMethods;
	}

	/***
	 * 
	 * Processes a request <br>
	 * Form of Request(client): String request = "Request;" + guid+ ";" + "calculate;" + params.getA() + ":" + params.getB() + ":" + params.getOperation();
	 * 
	 * @param message
	 * @return RESULT;GUID;RESULT_OR_ERROR_MESSAGE
	 */
	public String processRequest(String message) {
		
		String[] messageParts = message.split(";");

		if(messageParts.length != 4) {
			System.out.println("Wrong format of requests ["+ message + "]!");
		}
		
		String guid = messageParts[1];
		
		String argumentString = messageParts[3];
		String[] argumentsAsString = argumentString.split(":");
		
		
		try {

			double a = Double.parseDouble(argumentsAsString[0]);
			double b = Double.parseDouble(argumentsAsString[1]);
			String function = argumentsAsString[2];
		

			String erg = calculate(a, b, function) + "";
			
			return "Result;"+ guid + ";" + erg;
		} catch (NumberFormatException ex) {
			return "Result;"+ guid +";NumberFormatException [" + message + "]";
		}
	}

	/***
	 * 
	 * Simple Calculator
	 * 
	 * @param request
	 * @return
	 */
	public double calculate(double a, double b, String function) {

		double erg = 0;

		switch (function) {

		case "add":
			erg = a + b;
			break;
		case "sub":
			erg = a - b;
			break;
		case "mul":
			erg = a * b;
			break;
		case "div":
			erg = a / b;
			break;
		case "mod":
			erg = a % b;
			break;
		default:
			erg = 0;
		}

		return erg;
	}
}

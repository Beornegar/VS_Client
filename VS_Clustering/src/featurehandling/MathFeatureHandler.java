package featurehandling;

public class MathFeatureHandler extends FeatureHandling {

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

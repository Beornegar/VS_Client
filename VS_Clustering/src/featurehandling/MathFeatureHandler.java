package featurehandling;

import java.util.List;

public class MathFeatureHandler extends FeatureHandling {

	/***
	 * 
	 * Simple Calculator
	 * 
	 * @param request
	 * @return
	 */
	public double calculate(List<String> params) {
		
		if(params == null || params.size() != 3) {
			System.out.println("Method call with wrong parameters [" + params + "]");
		}
		
		double erg = 0;
		
		
		double a = Double.parseDouble(params.get(0));
		double b = Double.parseDouble(params.get(1));

		String function = params.get(2);
		
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

package test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import featurehandling.MathFeatureHandler;

public class MathFeatureHandlerTest {

	
	@Test
	void TestGetFeatures() {
		
		MathFeatureHandler handler = new MathFeatureHandler();
		
		String[] features = handler.getFeatures();
		
		assertTrue(features.length == 1);
		assertTrue(features[0].equals("calculate"));
		
	}
	
	@Test
	void processRequestTest() {
		
		MathFeatureHandler handler = new MathFeatureHandler();
		
		String message = "calculate;a=2:b=3:function=add;2";
		
		String erg  = handler.processRequest(message);
		
		assertTrue(erg.equals("5.0"));
	}
	
}

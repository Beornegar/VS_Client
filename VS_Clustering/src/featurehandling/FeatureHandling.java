package featurehandling;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FeatureHandling {

	public String[] getFeatures() {

		Class<?> cls = this.getClass();
		
		Method m[] = cls.getDeclaredMethods();
		List<Method> methodList = new ArrayList<>(Arrays.asList(m));
		
		List<String> featureList = methodList
				.stream()
				.filter(me -> !me.getName().equals("getFeatures") 
						&& !me.getName().equals("processRequest")
						&& me.getModifiers() == 1
						)
				.map(me -> me.getName())
				.collect(Collectors.toList());
		
		String[] featureMethods	= new String[featureList.size()];	
		for(int i = 0; i < featureList.size(); i++) {
			featureMethods[i] = featureList.get(i);
		}
		
		return featureMethods;
	}


	/***
	 * 
	 * Processes a request <br>
	 * Form of Request: feature;argName1=argValue1:argName2=argValue2;Index
	 * 
	 * @param message
	 * @return
	 */
	public String processRequest(String message) {

		try {

			String[] messageParts = message.split(";");

			String feature = messageParts[0];
			
			String[] argumentParts = messageParts[1].split(":");
			//Map<String,String> arguments = new HashMap<>();
			List<String> arguments = new ArrayList<>();
			
			for(int i = 0; i < argumentParts.length; i++) {
				String a = argumentParts[i];
				
//				
				String[] keyValuePair = a.split("=");
				if(keyValuePair.length == 2) {
					arguments.add(keyValuePair[1]);
//					String key = "arg" + i;
//					String value = keyValuePair[1];
//					arguments.put(key, value);
				} else {
					System.out.println("Malformed message in [arguments] :" + message);
				}
			}
			
			Class<? extends FeatureHandling> classs = this.getClass();
			List<Method> methods = Arrays.asList(classs.getMethods())
					.stream()
					.filter(me -> me.getName().equals(feature))
					.collect(Collectors.toList());
			
			if(methods.size() != 1) {
				System.out.println("Method could not be found [" + feature +"]");
				return null;
			}
			
			
			Class<?>[] paramTypes = methods.get(0).getParameterTypes();
			Method method = this.getClass().getMethod(feature, paramTypes);
			
		//	List<String> params = new ArrayList<>();
		//	Parameter[] parameterNames = method.getParameters();
			
//			for(int i = 0; i < parameterNames.length; i++) {
//				
//				Parameter p = parameterNames[i];
//				String paramValue = arguments.get(p.getName());
//				
//				//Class<?> cl = paramTypes[i];
//				
//				//Object o = cl.cast(paramValue);
//				//Object o = castObject(cl,paramValue);
//				//Object o = paramValue;
//				params.add(paramValue);
//			}
			
		//	Class<?> returnTypeOfMethod = method.getReturnType();
			
			Object erg = method.invoke(this, arguments);
			return String.valueOf(erg);
		//	return returnTypeOfMethod.cast(erg).toString();
			
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private <T> T castObject(Class<T> clazz, Object object) {
		  return (T) object;
		}
	
}

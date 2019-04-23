package featurehandling;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class FeatureHandling {


	public String[] getFeatures() {

		Class<?> cls = this.getClass();
		
		Method m[] = cls.getMethods();
		List<Method> methodList = new ArrayList<>(Arrays.asList(m));
		
		String[] featureMethods =methodList
				.stream()
				.filter(me -> !me.getName().toLowerCase().contains("getFeatures".toLowerCase()) 
						|| !me.getName().toLowerCase().contains("processRequest".toLowerCase()))
				.collect(Collectors.toList()).toArray(new String[0]);
		
		return featureMethods;
	}


	public String processRequest(String message) {

		try {

			String[] messageParts = message.split(";");
			
			String feature = messageParts[0];
			
			String[] argumentParts = messageParts[1].split(":");
			Map<String,String> arguments = new HashMap<>();
			
			for(String a : argumentParts) {
				String[] keyValuePair = a.split("=");
				if(keyValuePair.length == 2) {
					String key = keyValuePair[0];
					String value = keyValuePair[1];
					arguments.put(key, value);
				} else {
					System.out.println("Malformed message in [arguments] :" + message);
				}
			}
			
			
			Class<?>[] paramTypes = this.getClass().getMethod(feature).getParameterTypes();
			Method method = this.getClass().getMethod(feature, paramTypes);
			
			List<Object> params = new ArrayList<>();
			Parameter[] parameterNames = method.getParameters();
			
			for(int i = 0; i < parameterNames.length; i++) {
				
				Parameter p = parameterNames[i];
				String paramValue = arguments.get(p.getName());
				
				Class<?> cl = paramTypes[i];
				Object o = cl.cast(paramValue);
				params.add(o);
			}
			
			Class<?> returnTypeOfMethod = method.getReturnType();
			
			Object erg = method.invoke(this, params);
			return returnTypeOfMethod.cast(erg).toString();
			
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}

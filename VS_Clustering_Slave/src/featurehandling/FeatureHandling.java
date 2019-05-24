package featurehandling;

import java.util.List;
import java.util.UUID;

public interface FeatureHandling {

	public String[] getFeatures();

	public String processRequest(String message);
}

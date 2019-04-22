package features;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CalculateMaster extends Remote {

	double calculate(double a, double b, String function) throws IOException, RemoteException;
	
}

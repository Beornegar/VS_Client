package features;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Slave extends Remote
{
   
    double calculate(double  a, double b, String function) throws IOException, RemoteException;
    
    /** @return the functions existing on this slave. */
    String getFunctions() throws RemoteException;
}
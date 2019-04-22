package features;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalancingMaster extends Remote
{
    void register(Slave slave) throws RemoteException;
    void unregister(Slave slave) throws RemoteException;
}
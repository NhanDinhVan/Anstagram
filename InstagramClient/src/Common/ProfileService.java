package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.json.simple.JSONObject;

public interface ProfileService extends Remote {
	String detailProfile(int idProfile) throws RemoteException;
}

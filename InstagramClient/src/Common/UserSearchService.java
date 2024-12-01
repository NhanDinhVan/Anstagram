package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Model.User;

public interface UserSearchService extends Remote {
	List<User> searchUsers() throws RemoteException;
}

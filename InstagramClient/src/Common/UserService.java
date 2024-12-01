package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserService extends Remote {
	String register(String username, String password, String EmailOrPhone) throws RemoteException;

	String verifyOTP(String otp) throws RemoteException;
}

package Network;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONObject;

import Common.UserService;
import ConnectDB.Lists;
import Model.User;
import Utils.EmailSender; // Class hỗ trợ gửi email
//import Utils.SmsSender;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {
	private static Map<String, String> otpStorage = new HashMap<>();
	private static Map<String, Long> otpExpiry = new HashMap<>();
	private static final String NUMERIC_CHARACTERS = "0123456789";
	public static String TEST_EMAIL = "";

	public UserServiceImpl() throws RemoteException {
		super();
	}

	@Override
	public String register(String username, String password, String emailOrPhone) throws RemoteException {
		System.out.println("Received register request: " + username);
		System.out.println("Password: " + password);
		System.out.println("email or phone: " + emailOrPhone);

		JSONObject responseJSON = new JSONObject();

		// check duplicate username:
		try {
			List<User> listUser = new Lists().getUsers();
			for (User user : listUser) {
				if (user.getUsername().equalsIgnoreCase(username)) {
					responseJSON.put("status", "ERROR");
					responseJSON.put("inform", "Username already exists !");
					return responseJSON.toJSONString();

				}
			}

			// gửi mã OTP để client xác nhận: (OTP có length = 6);
			String OTPCode = generateSecureOTP(6);
			if (emailOrPhone.contains("@")) {
				try {
					TEST_EMAIL = emailOrPhone;
					EmailSender.sendOtpEmail("VERIFY CODE", "This is your verify code: " + OTPCode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			else {
//				SmsSender.sendOtpSms(emailOrPhone, otp);
//			}

			// check client đã nhập OTP đúng chưa ?:

//			responseJSON.put("status", "SUCCESS");
//			responseJSON.put("inform", "Registration is successful");
//			return responseJSON.toJSONString();
			responseJSON.put("status", "PENDING");
			responseJSON.put("inform", "OTP has been sent. Please verify!");
			return responseJSON.toJSONString();

		} catch (SQLException e) {
			e.printStackTrace();
			responseJSON.put("status", "error");
			responseJSON.put("inform", "Error during registration !");
			return responseJSON.toJSONString();
		}

	}

	// generate OTP Code :
	public String generateSecureOTP(int length) {
		if (length <= 0) {
			throw new IllegalAccessError("OTP length must be greater than 0");
		}
		SecureRandom secureRandom = new SecureRandom();
		StringBuilder OTP = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int randomIndex = secureRandom.nextInt(NUMERIC_CHARACTERS.length());
			OTP.append(NUMERIC_CHARACTERS.charAt(randomIndex));
		}
		return OTP.toString();
	}

}

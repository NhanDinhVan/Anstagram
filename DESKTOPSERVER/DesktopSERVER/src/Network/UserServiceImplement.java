//package Network;
//
//import java.rmi.RemoteException;
//import java.rmi.server.UnicastRemoteObject;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//import org.json.simple.JSONObject;
//
//import Common.UserService;
//import ConnectDB.Lists;
//import Model.User;
//import Utils.EmailSender; // Class hỗ trợ gửi email
////import Utils.SmsSender;
//
//public class UserServiceImplement extends UnicastRemoteObject implements UserService {
//
//	public UserServiceImplement() throws RemoteException {
//		super();
//	}
//
//	@Override
//	public String register(String username, String password, String emailOrPhone) throws RemoteException {
//		System.out.println("Received register request: " + username);
//		System.out.println("Password: " + password);
//		System.out.println("email or phone: " + emailOrPhone);
//
//		JSONObject responseJSON = new JSONObject();
//
//		// check duplicate username:
//		try {
//			List<User> listUser = new Lists().getUsers();
//			for (User user : listUser) {
//				if (user.getUsername().equalsIgnoreCase(username)) {
//					responseJSON.put("status", "ERROR");
//					responseJSON.put("inform", "Username already exists !");
//					return responseJSON.toJSONString();
//
//				}
//			}
//
//			// check xem phone hoặc email có tồn tại (có hợp lệ) hay không?:
//
//			// gửi mã OTP để client xác nhận:
//			String OTPCode = String.format("%06d", new Random().nextInt(999999));
//			if (emailOrPhone.contains("@")) {
//				EmailSender.sendOtpEmail(emailOrPhone, OTPCode);
//			} 
////			else {
////				SmsSender.sendOtpSms(emailOrPhone, otp);
////			}
//
//			// check client đã nhập OTP đúng chưa ?:
//
////			responseJSON.put("status", "SUCCESS");
////			responseJSON.put("inform", "Registration is successful");
////			return responseJSON.toJSONString();
//			responseJSON.put("status", "PENDING");
//			responseJSON.put("inform", "OTP has been sent. Please verify!");
//			return responseJSON.toJSONString();
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			responseJSON.put("status", "error");
//			responseJSON.put("inform", "Error during registration !");
//			return responseJSON.toJSONString();
//		}
//
//	}
//
//	@Override
//	public String verifyOTP(String emailPhone, String otpCode) throws RemoteException {
//		System.out.println("Verifying OTP for: " + emailPhone);
//
//		JSONObject responseJSON = new JSONObject();
//
//		// Check OTP tồn tại và hợp lệ:
//		if (otpStorage.containsKey(emailPhone) && otpExpiry.get(emailPhone) > System.currentTimeMillis()) {
//			if (otpStorage.get(emailPhone).equals(otpCode)) {
//				otpStorage.remove(emailPhone);
//				otpExpiry.remove(emailPhone);
//
//				// Lưu tài khoản mới vào database:
//				try {
//					new Lists().saveUser(new User(emailPhone, otpCode)); // Giả định User có hàm dựng này
//				} catch (SQLException e) {
//					e.printStackTrace();
//					responseJSON.put("status", "ERROR");
//					responseJSON.put("inform", "Error saving user data!");
//					return responseJSON.toJSONString();
//				}
//
//				responseJSON.put("status", "SUCCESS");
//				responseJSON.put("inform", "Registration successful!");
//				return responseJSON.toJSONString();
//			} else {
//				responseJSON.put("status", "ERROR");
//				responseJSON.put("inform", "Invalid OTP!");
//				return responseJSON.toJSONString();
//			}
//		} else {
//			responseJSON.put("status", "ERROR");
//			responseJSON.put("inform", "OTP expired or not found!");
//			return responseJSON.toJSONString();
//		}
//	}
//
//}

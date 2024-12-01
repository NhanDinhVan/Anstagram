package Network;

import java.rmi.RemoteException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Common.UserService;
import Controller.ShowOptionPane;
import Controller.SignupController;
import View.OTPPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignupHandler {

	private SignupController signupController;
	private OTPPane otpPane = new OTPPane();

	// Setter để gán đối tượng SignupController
	public void setSignupController(SignupController signupController) {
		this.signupController = signupController;
	}

	public void validateFields(TextField email_phone_field, TextField username_field, PasswordField password_field,
			PasswordField confirm_password_field, ShowOptionPane showOptionPane, UserService userService) {
		try {
			if (email_phone_field.getText().equals("") || username_field.getText().equals("")
					|| password_field.getText().equals("") || confirm_password_field.getText().equals("")) {
				showOptionPane.showOptionPane("Thông báo", "Lưu ý", "Vui lòng điền đầy đủ thông tin !");
			} else {
				// nếu không empty fields => dùng giao thức rmi để send to server:
				String emailPhone = email_phone_field.getText();

				String username = username_field.getText();
				validateUsername(username);

				String password = password_field.getText();
				validatePassword(password);

				String confirmPassword = confirm_password_field.getText();
				checkMatchesPassword(password, confirmPassword);

				sendRegisterInformation(username, password, confirmPassword, emailPhone, showOptionPane, userService);

			}

		} catch (SignupException e) {
			showOptionPane.showOptionPane("Warning", "Attention", e.getMessage());

		}

	}

	// gửi thông tin register đến cho server:
	public void sendRegisterInformation(String username, String password, String confirm_password, String emailPhone,
			ShowOptionPane showOptionPane, UserService userService) {
		try {
			String responseJSONString = userService.register(username, password, emailPhone);

			try {
				receiveInform(showOptionPane, responseJSONString, username, confirm_password, responseJSONString);
			} catch (ParseException e) {
				e.printStackTrace();
				showOptionPane.showOptionPane("Thông báo", "Lỗi", "Không thể kết nối đến server. Vui lòng thử lại.");

			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// nhan tbao tu server:
	public void receiveInform(ShowOptionPane showOptionPane, String responseJSONString, String username,
			String password, String emailOrPhone) throws ParseException {
		JSONObject response = (JSONObject) (new JSONParser().parse(responseJSONString));
		String inform = (String) response.get("inform");
		String status = (String) response.get("status");

		if (status.equals("error")) {
			showOptionPane.showOptionPane("Warning", "Error !", inform);
		} else if (status.equals("PENDING")) {
			System.out.println("1.status: " + status);
			showOptionPane.showOptionPane("Thông báo", "Please enter verify code", inform);

			// hiển thị ra showOptionPane có chứa ô nhập code:
			otpPane.visibleOTPPane();

		} else if (status.equals("USERNAME_EXISTS")) {
			showOptionPane.showOptionPane("Attention", "Invalid username !", inform);

		}
	}

	// reset lại form signup khi success:
	public void clearFormSignup() {
		if (signupController != null) {
			signupController.getEmailField().clear();
			signupController.getUsernameField().clear();
			signupController.getConfirmPasswordField().clear();
			signupController.getPasswordField().clear();
		}
	}

	// check validate username: username chỉ được chứa số, dấu chấm, dấu gạch
	// dưới,chữ cái, không chứa kí tự
	// đặc biệt, không chứa whiteSpace,

	public void validateUsername(String usernameField) throws SignupException {
		String regularExpression = "^[a-zA-Z0-9._]+$";
		if (!usernameField.matches(regularExpression)) {
			throw new SignupException("Username not contains special characters !");
		}

	}

	// mật khẩu gồm 8 kí tự trở lên
	public void validatePassword(String password) throws SignupException {
		if (password.length() < 8) {
			throw new SignupException("Password contains 8 characters or  more");
		}
	}

	// check confirmpassword matches với password ?
	public void checkMatchesPassword(String password, String confirmPassword) throws SignupException {
		if (!confirmPassword.equals(password)) {
			throw new SignupException("Do not match password");
		}
	}

	// send OTP CODE để server confirm:
	public void submitOTP(String otpCode, UserService userService, ShowOptionPane showOptionPane) {
		try {
			// send otp to server:
			String otpJSONString = userService.verifyOTP(otpCode);

			// receive otp from server:
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(otpJSONString);
			String inform = (String) jsonObject.get("inform");
			String status = (String) jsonObject.get("status");

			// Neu status isSuccessfulRegistration (dang ky thanh cong):
			if (status.equals("SUCCESSFUL_REGISTRATION")) {

				// showOptionPane success:
				showOptionPane.showOptionPane("Announce", "Success", inform);

				// close OTPPane:
				otpPane.closeOTPPane();

				// reset lại textfields trên form signup:
				clearFormSignup();

			} else if (status.equals("FAILURE_REGISTRATION")) {

				// neu dang ky that bai, showOptionPane (sai ma OTP):
				showOptionPane.showOptionPane("Announce", "Failure",
						inform + ", Please check email and retype OTP Code !");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

class SignupException extends Exception {
	public SignupException(String error) {
		super(error);

	}

}

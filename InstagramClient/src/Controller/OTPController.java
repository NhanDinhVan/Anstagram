package Controller;

import Network.SignupHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OTPController {

	// Hàm xử lý khi nhấn Submit
	public void handleSubmit(TextField[] otpFields, Stage otpStage) {
		StringBuilder otp = new StringBuilder();
		for (TextField field : otpFields) {
			otp.append(field.getText());
		}

		String otpCode = otp.toString();
		if (otpCode.length() == 6) {
			// send OTP CODE cho server xác nhận (xem code đó có trùng khớp với code mà
			// server cung cấp về email không):
			SignupController.signupHandler.submitOTP(otpCode, SignupController.userService,
					SignupController.showOptionPane);

		} else {
			SignupController.showOptionPane.showOptionPane("Attention", "Invalid OTP Code !",
					"Please enter a valid 6-digit OTP.");
		}
	}
}

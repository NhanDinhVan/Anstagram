package Controller;

import java.rmi.Naming;
import java.rmi.RemoteException;

import Common.Constants;
import Common.UserService;
import Network.SignupHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class SignupController {
	public static ShowOptionPane showOptionPane = new ShowOptionPane();

	public static SignupHandler signupHandler = new SignupHandler();

	public static UserService userService; // Interface RMI để kết nối server

	@FXML
	private TextField username_field;

	@FXML
	private PasswordField confirm_password_field;

	@FXML
	private TextField email_phone_field;

	@FXML
	private PasswordField password_field;

	@FXML
	private Button have_accountl_btn;

	@FXML
	private Button submit_signup_btn;

	public PasswordField getPasswordField() {
		return this.password_field;
	}

	public PasswordField getConfirmPasswordField() {
		return this.confirm_password_field;
	}

	public TextField getUsernameField() {
		return this.username_field;
	}

	public TextField getEmailField() {
		return this.email_phone_field;
	}

	@FXML
	private void initialize() {

		// Gán SignupController cho SignupHandler
		signupHandler.setSignupController(this);

		// Thiết lập kết nối RMI
		try {
			userService = (UserService) Naming.lookup("rmi://"+ Constants.SERVER_ADDRESS +"/UserService");
		} catch (Exception e) {
			e.printStackTrace();
			showOptionPane.showOptionPane("Thông báo", "Lỗi", "Không thể kết nối tới server.");
			return;
		}

		// Xử lý sự kiện nhấn Enter trong các trường nhập liệu
		activateTextfieldEvent(confirm_password_field);
		activateTextfieldEvent(password_field);
		activateTextfieldEvent(username_field);
		activateTextfieldEvent(email_phone_field);

		submit_signup_btn.setOnAction(e -> {
			System.out.println("enter key in signup-view is clicked...");

			// check tính hợp lệ của các fields;
			signupHandler.validateFields(email_phone_field, username_field, password_field, confirm_password_field,
					showOptionPane, userService);
		});
	}

	public void activateTextfieldEvent(TextField TextField) {
		TextField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				submit_signup_btn.fire();
			}
		});
	}

}

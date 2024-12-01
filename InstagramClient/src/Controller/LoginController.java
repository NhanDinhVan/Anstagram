package Controller;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import Network.P2P.Peer;
import org.json.simple.JSONObject;

import Network.LoginAction;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController {
	private Redirect redirect = new Redirect();

	public static String username, password;

	@FXML
	private Text forget_password;

	@FXML
	private Button login_btn;

	@FXML
	private Pane login_fields;

	@FXML
	private Text login_with_facebook_btn;

	@FXML
	private PasswordField password_field;

	@FXML
	private Button signup_btn;

	@FXML
	private TextField username_field;


	private static final String SUCCESS_LOGIN = "APPROPRIATE ACCOUNT";
	private static final String WRONG_PASSWORD = "INCORRECT PASSWORD";
	private static final String WRONG_USERNAME = "INCORRECT USERNAME";

	@FXML
	public void initialize() {
		login_btn.setOnMouseClicked(event -> handleLoginSubmission());
		signup_btn.setOnMouseClicked(event -> navigateToSignup());
	}
	@FXML
	private void handleLoginSubmission() {
		try {
			username = username_field.getText().trim();
			password = password_field.getText().trim();

			if (username.isEmpty() || password.isEmpty()) {
				showOptionPane("Thông báo", "Chú ý !", "Vui lòng nhập thông tin đầy đủ !");
				return;
			}

			LoginAction loginAction = new LoginAction();
			loginAction.requestLogin(username, password);

			String notify = loginAction.notify;
			if (notify == null) {
				System.err.println("notify is null !");
				return;
			}

			processLoginResponse(notify);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processLoginResponse(String notify) {
		switch (notify) {
			case SUCCESS_LOGIN:
				startPeer();
				redirectToNewFeeds();
				break;

			case WRONG_PASSWORD:
				showOptionPane("Thông báo", "Đăng nhập không thành công", "Sai mật khẩu !");
				break;

			case WRONG_USERNAME:
				showOptionPane("Thông báo", "Đăng nhập không thành công", "Tên đăng nhập sai !");
				break;

			default:
				System.err.println("Unknown response: " + notify);
				break;
		}
	}

	private void navigateToSignup() {
		try {
			redirect.redirectPage("/View/Signup-view.fxml", signup_btn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void redirectToNewFeeds() {
		try {
			redirect.redirectPage("/View/Newfeeds-view.fxml", login_btn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showOptionPane(String title, String headerText, String contentText) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	private void startPeer() {
		try {
			Peer peer = new Peer(username_field.getText());
			peer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

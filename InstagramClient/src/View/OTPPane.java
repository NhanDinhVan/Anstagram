package View;

import Controller.OTPController;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OTPPane {
	private OTPController otpController;
	private Stage otpStage;

	// Hiển thị giao diện nhập OTP
	public void visibleOTPPane() {
		
		// Khởi tạo OTPController();
		otpController = new OTPController();

		// Tạo một Stage mới cho OTP
		otpStage = new Stage();
		otpStage.initModality(Modality.APPLICATION_MODAL); // Đặt thành modal (chặn các cửa sổ khác)
		otpStage.setTitle("Enter OTP");

		// GridPane chứa các ô TextField
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		// Mảng TextField để lưu trữ các ô nhập OTP
		TextField[] otpFields = new TextField[6];

		// Tạo các TextField cho OTP
		for (int i = 0; i < 6; i++) {
			TextField textField = new TextField();
			textField.setPrefWidth(40); // Đặt kích thước mỗi ô
			textField.setAlignment(Pos.CENTER); // Canh giữa text
			textField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
				// Chỉ cho phép nhập một ký tự số
				if (change.getControlNewText().length() > 1 || !change.getControlNewText().matches("\\d*")) {
					return null;
				}
				return change;
			}));

			otpFields[i] = textField;

			// Thêm TextField vào GridPane
			gridPane.add(textField, i, 0);

			// Xử lý chuyển con trỏ khi nhập
			final int currentIndex = i; // Biến để dùng trong lambda
			textField.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.length() == 1 && currentIndex < otpFields.length - 1) {
					otpFields[currentIndex + 1].requestFocus(); // Chuyển con trỏ sang ô tiếp theo
				}
			});

			// Xử lý di chuyển con trỏ bằng phím Backspace
			textField.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.BACK_SPACE) {
					if (textField.getText().isEmpty() && currentIndex > 0) {
						otpFields[currentIndex - 1].requestFocus();
					}
				}
			});

			// Xử lý nhấn Enter trong ô cuối cùng
			if (i == otpFields.length - 1) {
				textField.setOnKeyPressed(event -> {
					if (event.getCode() == KeyCode.ENTER) {
						otpController.handleSubmit(otpFields, otpStage); // Gọi hàm xử lý submit
					}
				});
			}
		}

		// Tạo nút Submit
		Button submitButton = new Button("Submit");
		submitButton.setOnAction(e -> otpController.handleSubmit(otpFields, otpStage));

		// Thêm nút Submit vào GridPane
		gridPane.add(submitButton, 0, 1, 6, 1); // Ô kéo dài 6 cột
		GridPane.setHalignment(submitButton, HPos.CENTER);

		// Đặt GridPane vào Scene
		Scene scene = new Scene(gridPane, 400, 200);

		// Hiển thị giao diện
		otpStage.setScene(scene);
		otpStage.showAndWait(); // Dùng `showAndWait` để chặn các thao tác khác cho đến khi đóng OTPPane
	}

	public void closeOTPPane() {
		otpStage.close();
	}

}

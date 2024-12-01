package Controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ShowOptionPane {
	public void showOptionPane(String title, String headerText, String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);

		// Hiển thị Alert
		alert.showAndWait();
	}
	
	
}

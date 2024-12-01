package Controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Redirect {
	public void redirectPage(String resource, javafx.scene.control.Button button) throws IOException {
		Parent view = FXMLLoader.load(getClass().getResource(resource));
		Stage stage = (Stage) button.getScene().getWindow();
		stage.setScene(new Scene(view));
	}
}

package Controller;

import java.net.URL;
import java.util.ResourceBundle;

import Model.DetailMessage;
import Network.WebSocketClientHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class SenderMessageController {

	@FXML
	private Label send_time;

	@FXML
	private Label message_of_sender;

	private DetailMessage detailMessage;

	public void setSenderMessage(DetailMessage dm) {
		this.detailMessage = dm;
		send_time.setText(dm.getSend_time());
		message_of_sender.setText("\t" + dm.getMessage_text());

		message_of_sender.setMinWidth(190); // Đảm bảo tin nhắn có không gian đủ rộng
		message_of_sender.setWrapText(true);

	}

	

}

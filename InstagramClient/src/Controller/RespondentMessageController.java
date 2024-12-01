package Controller;

import org.json.simple.JSONObject;

import Model.DetailMessage;
import Network.WebSocketClientHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RespondentMessageController {

	@FXML
	private Button downloadFile_btn;

	@FXML
	private ImageView download_file_icon;

	@FXML
	private Label message_of_respondent;

	@FXML
	private Label response_time;

	private DetailMessage detailMessage;

	@FXML
	public void initialize() {
		downloadFile_btn.setOnMouseClicked(arg0 -> {
//			System.out.println("gửi yêu cầu cho server muốn download file");
			// click vaof download_btn => yeu cau server tra ve noi dung file:
			requestDownloadFileEvent(arg0);
		});

	}

	public void setRespondentMessage(DetailMessage dm) {
		this.detailMessage = dm;
		message_of_respondent.setText("\t" + dm.getMessage_text());
		response_time.setText(dm.getSend_time());
//		message_of_respondent.setMinWidth(250); // Đảm bảo tin nhắn có không gian đủ rộng
		message_of_respondent.setWrapText(true);

		if (dm.getType().equals("file")) {
			visible_download_btn(); // Hiển thị nút tải xuống nếu đây là một tệp
		}
	}

	// nếu tn nào là file, thì show download_btn:
	public void visible_download_btn() {
		downloadFile_btn.setVisible(true);
		download_file_icon.setVisible(true);

	}

	public void requestDownloadFileEvent(MouseEvent event) {
		System.out.println("Request download file triggered.");
		if (MessageController.webSocketClient != null && MessageController.webSocketClient.isOpen()) {
			MessageController.webSocketClient.requestLoadFile();
		}
	}

}

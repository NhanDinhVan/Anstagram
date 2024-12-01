package Controller;

import Model.OnlineUser;
import Model.Partner;
import Model.DetailMessage;
import Network.ChatAction;
import Network.LoginAction;
import Network.P2P.PeerList;
import Network.P2P.PeerListObserver;
import Network.WebSocketClientHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class MessageController implements Initializable, PeerListObserver {
	public static WebSocketClientHandler webSocketClient; // WebSocket client toàn cục

	private File file;

	@FXML
	private Button home_btn;

	@FXML
	private ImageView userState;

	@FXML
	private ImageView avt;

	@FXML
	private ScrollPane messageItemPanel;

	@FXML
	private VBox onlineUser_vbox;

	@FXML
	private ScrollPane conversation_pane;

	@FXML
	private Button send_message_btn;

	@FXML
	private Button file_chooser_btn;

	@FXML
	private ImageView file_icon;

	@FXML
	private TextArea enter_message_field;

	@FXML
	private Button new_conversation_btn;

	@FXML
	private Label username_label;

	public ScrollPane getConversationPane() {
		return this.conversation_pane;
	}

	public TextArea getTextArea() {
		return this.enter_message_field;
	}

	public Button getSendBtn() {
		return this.send_message_btn;
	}

	public Button getFileChooserBtn() {
		return this.file_chooser_btn;
	}

	public ImageView getFileIcon() {
		return this.file_icon;
	}

	private List<Partner> partners = ChatAction.partnerList;

	private PeerList peerList;

	private Redirect redirect = new Redirect();

	private OnlineUser currentUserChatting;

	public void open_message_view(MouseEvent event, Button message_btn) throws IOException {
		Parent message_view = FXMLLoader.load(getClass().getResource("/View/Message-view.fxml"));
		Stage stage = (Stage) message_btn.getScene().getWindow();
		stage.setScene(new Scene(message_view));
	}

	public void initialize(URL url, ResourceBundle resourceBundle) {
		// Khởi tạo kết nối WebSocket khi ứng dụng bắt đầu
//		try {
//			URI serverUri = new URI("ws://"+ Constants.SERVER_ADDRESS +":8080");
//			webSocketClient = new WebSocketClientHandler(serverUri, this);
//			webSocketClient.connect();
//
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//
//		send_message_btn.setOnMouseClicked(arg0 -> {
//			sendMessageEvent(arg0);
//
//		});
//
//		file_chooser_btn.setOnMouseClicked(e -> {
//			openFileChooser(e);
//		});
//
//		new_conversation_btn.setOnMouseClicked(event -> {
//			SearchPane searchPane = new SearchPane(new SearchPane.SearchCallback() {
//				@Override
//				public void onUserSelected(Partner partner) {
//					addMessageItemForUser(partner); // Thêm message-item cho người dùng được chọn
//				}
//			});
//			searchPane.visibleSearchPane();
//		});
//
//		// add message item into scrollpane "messageItemPanel"
//		addPartnersToChat();

		peerList = PeerList.getInstance();
		peerList.addObserver(this); // Đăng ký để nhận thông báo khi danh sách người dùng thay đổi

		// Thêm các user hiện có vào giao diện khi bắt đầu
		update(peerList.getPeers());

		home_btn.setOnMouseClicked(arg->{
			redirectToNewFeeds();
		});
	}

	public void addMessageItemForUser(Partner partner) {
		try {
			// Tạo một message-item mới
			URL resource = getClass().getResource("/View/message-item.fxml");
			if (resource == null) {
				System.err.println("FXML file not found!");
			}
			FXMLLoader fxmlLoader = new FXMLLoader(resource);
			fxmlLoader.setLocation(resource);

			// Tạo AnchorPane cho message-item mới
			AnchorPane anchorPane = fxmlLoader.load();
			MessageItemController messageItemController = fxmlLoader.getController();
			messageItemController.setParentController(this); // Đặt parent controller
			messageItemController.showPartners(partner); // Hiển thị thông tin người dùng mới
			onlineUser_vbox.getChildren().add(anchorPane); // Thêm message-item vào VBox

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void addPartnersToChat() {
		try {
			for (Partner partner : partners) {
				URL resource = getClass().getResource("/View/message-item.fxml");
				if (resource == null) {
					System.err.println("FXML file not found!");
				}
				FXMLLoader fxmlLoader = new FXMLLoader(resource);
				fxmlLoader.setLocation(resource);

				AnchorPane anchorPane = fxmlLoader.load();
				MessageItemController messageItemController = fxmlLoader.getController();
				messageItemController.setParentController(this); // This is where you call it
				messageItemController.showPartners(partner);
				onlineUser_vbox.getChildren().add(anchorPane);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}



	// open hộp thoại chọn file:
	public void openFileChooser(MouseEvent event) {
		try {
			// Tạo FileChooser
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select file");

			// Đặt bộ lọc file (ví dụ chỉ hiển thị các file văn bản)
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("All Files", "*.*");
			fileChooser.getExtensionFilters().add(filter);

			file = fileChooser.showOpenDialog(null);

			// Kiểm tra xem người dùng đã chọn file chưa
			if (file != null) {
				// Hiển thị tên file hoặc thực hiện các thao tác khác
				System.out.println("Selected file: " + file.getAbsolutePath());

				enter_message_field.setText("File:" + file.getName());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
//
//	public void sendMessageEvent(MouseEvent event) {
//		if (enter_message_field.getText().startsWith("File:")) {
//			// send file to server:
//			if (webSocketClient != null && webSocketClient.isOpen()) {
//				webSocketClient.transferFileToServer(file);
//
//				// show sender message:
//				try {
//					showSenderMessage(
//							enter_message_field.getText().substring(5, enter_message_field.getText().length()), "file");
//
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				// reset textarea = "" khi gửi tn thành công:
//				resetTextArea();
//			}
//		} else {
//			if (webSocketClient != null && webSocketClient.isOpen()) {
//				webSocketClient.sendMessageToServer(webSocketClient.prepareMessage(enter_message_field));
//
//				// show tin nhắn của sender trong GUI:
//				try {
//					showSenderMessage(enter_message_field.getText(), "message_text");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				// reset textarea = "" khi gửi tn thành công:
//				resetTextArea();
//
//			} else {
//				System.out.println("WebSocket is not open!");
//			}
//		}
//
//	}

	public void sendMessageEvent(MouseEvent event) {
		try (DatagramSocket socket = new DatagramSocket()) {
			String ip = currentUserChatting.ipAddress;
			int port = 5000;
			InetAddress targetAddress = InetAddress.getByName(ip);

			String message = enter_message_field.getText();
			byte[] messageBytes = message.getBytes();

			DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, targetAddress, port);
			socket.send(packet);

			System.out.println("Tin nhắn đã được gửi tới " + ip + ":" + 5000 + " - Nội dung: " + message);

			// show tin nhắn của sender trong GUI:
			showSenderMessage(message, "message_text");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showSenderMessage(String enter_message, String messageType) throws IOException {
		DetailMessage lastestMessage = new DetailMessage(0, LoginAction.userID.intValue(),
				LoginAction.userID.intValue(), MessageItemController.choosed_partnerID, enter_message, getTime(),
				messageType);

		URL resource = getClass().getResource("/View/sender-detail-message.fxml");
		if (resource == null) {
			System.err.println("FXML file not found!");
		}
		FXMLLoader fxmlLoader = new FXMLLoader(resource);
		fxmlLoader.setLocation(resource);
		AnchorPane senderPane = fxmlLoader.load();

		SenderMessageController senderMessageController = fxmlLoader.getController();
		senderMessageController.setSenderMessage(lastestMessage);

		if (this != null) {
			javafx.scene.control.ScrollPane conversationPane = this.getConversationPane();
			if (conversationPane != null) {
				MessageItemController.detail_message_vbox.setMinWidth(996.0);
				MessageItemController.detail_message_vbox.getChildren().add(senderPane);
				conversationPane.setContent(MessageItemController.detail_message_vbox);
			} else {
				System.err.println("conversation pane is null !");
			}
		} else {
			System.err.println("parentController is null");
		}

	}

	// thoi gian gui tin nhan:
	public static String getTime() {
		// Lấy thời gian hiện tại
		LocalDateTime currentDateTime = LocalDateTime.now();

		// Định dạng thời gian theo kiểu "dd:mm:yyyy H:M:S"
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
		String formattedTime = currentDateTime.format(formatter);
		return formattedTime;

	}

	public void showReceiverMessage(int senderID, int receiverID, String message_content, String time,
			String messageType) throws IOException {
		DetailMessage lastestMessage = new DetailMessage(0, receiverID, senderID, receiverID, message_content, time,
				messageType);

		URL resource = getClass().getResource("/View/respondent-detail_message.fxml");
		if (resource == null) {
			System.err.println("FXML file not found!");
		}
		FXMLLoader fxmlLoader = new FXMLLoader(resource);
		fxmlLoader.setLocation(resource);
		AnchorPane senderPane = fxmlLoader.load();

		RespondentMessageController respondentMessageController = fxmlLoader.getController();
		respondentMessageController.setRespondentMessage(lastestMessage);

		if (this != null) {
			javafx.scene.control.ScrollPane conversationPane = this.getConversationPane();
			if (conversationPane != null) {
				MessageItemController.detail_message_vbox.setMinWidth(996.0);
				MessageItemController.detail_message_vbox.getChildren().add(senderPane);
				conversationPane.setContent(MessageItemController.detail_message_vbox);
			} else {
				System.err.println("conversation pane  is null !");
			}
		} else {
			System.err.println("parentController is null");
		}
	}

	public void resetTextArea() {
		enter_message_field.setText("");
	}


	@Override
	public void update(Set<OnlineUser> onlineUsers) {
		// Chuyển tất cả thao tác liên quan đến giao diện vào luồng JavaFX
		Platform.runLater(() -> {
			// Làm sạch danh sách trước khi cập nhật lại
			onlineUser_vbox.getChildren().clear();

			// Thêm mỗi user vào giao diện như một message item
			for (OnlineUser user : onlineUsers) {
				addMessageItemForUser(user);
			}
		});
	}

	// Thêm message item cho mỗi user
	public void addMessageItemForUser(OnlineUser user) {
		try {
			// Tạo một message-item mới từ FXML
			URL resource = getClass().getResource("/View/message-item.fxml");
			if (resource == null) {
				System.err.println("FXML file not found!");
			}
			FXMLLoader fxmlLoader = new FXMLLoader(resource);
			fxmlLoader.setLocation(resource);

			// Tạo AnchorPane cho message-item mới
			AnchorPane anchorPane = fxmlLoader.load();
			MessageItemController messageItemController = fxmlLoader.getController();
			messageItemController.setParentController(this); // Đặt parent controller
			messageItemController.showOnlineUser(user); // Hiển thị thông tin user
			onlineUser_vbox.getChildren().add(anchorPane); // Thêm message-item vào VBox

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void redirectToNewFeeds() {
		try {
			redirect.redirectPage("/View/Newfeeds-view.fxml", home_btn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleClickUserOnline(OnlineUser onlineUser){
		username_label.setText(onlineUser.email);
		currentUserChatting = onlineUser;
	}
}

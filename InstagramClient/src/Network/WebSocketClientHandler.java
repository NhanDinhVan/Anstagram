package Network;

import java.awt.TextArea;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Base64;

import Common.Constants;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Controller.MessageController;
import Controller.MessageItemController;
import Controller.RespondentMessageController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class WebSocketClientHandler extends WebSocketClient {
	public static Long file_id;
	public static String extension;

	private MessageController messageController; // Đối tượng MessageController hiện tại

	public WebSocketClientHandler(URI serverUri, MessageController messageController) {
		super(serverUri);
		this.messageController = messageController;
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("Connected to Server !");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("action", "CONNECT TO WEBSOCKET SERVER");
		jsonObject.put("webSocketClient_id", LoginAction.userID);
		this.send(jsonObject.toJSONString());
	}

	@Override
	public void onMessage(String message) {
		try {
			JSONObject object = (JSONObject) (new JSONParser().parse(message));
			String action = (String) object.get("action");

			if (action.equals("CHAT")) {
				String message_content = (String) object.get("message_content");
				Long senderID = (Long) object.get("senderID");
				Long receiverID = (Long) object.get("receiverID");
				String time = (String) object.get("send_time");
				String messageType = (String) object.get("message_type");

				// nếu user đang login có id == receiver_id => show tin nhắn bên phía
				// respondent:
				if (LoginAction.userID == receiverID) {
					// show message của receiver trong GUI:
					Platform.runLater(() -> {
						try {
							messageController.showReceiverMessage(senderID.intValue(), receiverID.intValue(),
									message_content, time, "message_text");

						} catch (IOException e) {
							e.printStackTrace();
						}
					});

				}
			} else if (action.equals("FILE_IS_RECEIVED")) {
				Long senderID = (Long) object.get("senderID");
				Long receiverID = (Long) object.get("receiverID");
				String time = (String) object.get("send_time");
				String filename = (String) object.get("file_name");
				String filepath = (String) object.get("file_path");
				file_id = (Long) object.get("file_id");
				extension = (String) object.get("extension");
				String message_type = (String) object.get("message_type");

				if (LoginAction.userID == receiverID) {
					// show message của receiver trong GUI:
					Platform.runLater(() -> {
						try {
							messageController.showReceiverMessage(senderID.intValue(), receiverID.intValue(), filename,
									time, "file");

							if (message_type.equals("file")) {
								// hiển thị download_file_btn:
//								RespondentMessageController.visible_download_btn();
							}

						} catch (IOException e) {
							e.printStackTrace();
						}
					});

				}
			} else if (action.equals("FILE_CONTENT")) {
				Long file_id = (Long) object.get("file_id");
				String base64FileData = (String) object.get("file_data");
				byte[] file_data = Base64.getDecoder().decode(base64FileData);
				String file_name = (String) object.get("file_name");
				String extension = (String) object.get("extension");
				Long requestorID = (Long) object.get("requestorID");

				saveFile(extension, file_data);
				System.out.println("Client đã nhận được content file...");

			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("WebSocket is not open.");
		Platform.runLater(() -> {
			this.reConnectWebSocket();
		});
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
	}

	public void sendMessageToServer(String message) {
		if (this.isOpen()) {
			this.send(message);
		} else {
			System.out.println("WebSocket is closed !");
		}
	}

	public void reConnectWebSocket() {
		new Thread(() -> {
			try {
				Thread.sleep(5000); // Đợi 5 giây trước khi thử kết nối lại
				URI serverUri = new URI("ws://"+ Constants.SERVER_ADDRESS +":8080");
				MessageController.webSocketClient = new WebSocketClientHandler(serverUri, messageController);
				MessageController.webSocketClient.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	// chuẩn bị message để send to server:
	public String prepareMessage(javafx.scene.control.TextArea enter_message_field) { // convert message thành //
																						// jsonString để send to server:
		String jsonString = null;
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "CHAT");
			jsonObject.put("message_content", enter_message_field.getText());
			jsonObject.put("senderID", LoginAction.userID.intValue());
			jsonObject.put("receiverID", MessageItemController.choosed_partnerID);
			jsonObject.put("send_time", MessageController.getTime());
			jsonObject.put("message_type", "message_text");

			jsonString = jsonObject.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	// readFile sau đó send data file cho server:
	public void transferFileToServer(File file) {
		try {
			String fileName = file.getName();
			String extension = fileName.substring(fileName.indexOf('.'));
			String file_path = file.getAbsolutePath();

			// Đọc file và chuyển sang byte array
			byte[] fileData = Files.readAllBytes(file.toPath());

			// Chuyển byte array thành base64 (nếu cần thiết)
			String base64FileData = Base64.getEncoder().encodeToString(fileData);

			JSONObject jsonFile = new JSONObject();
			jsonFile.put("action", "SEND_FILE_TO_SERVER");
			jsonFile.put("file_name", fileName);
			jsonFile.put("file_data", base64FileData);
			jsonFile.put("senderID", LoginAction.userID);
			jsonFile.put("receiverID", MessageItemController.choosed_partnerID);
			jsonFile.put("send_time", MessageController.getTime());
			jsonFile.put("extension", extension);
			jsonFile.put("file_path", file_path);
			jsonFile.put("message_type", "file");

			sendMessageToServer(jsonFile.toJSONString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// gửi reqquest downloadfile
	public void requestLoadFile() {
		try {

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("requestorID", LoginAction.userID);
			jsonObject.put("action", "DOWNLOAD_FILE");
			jsonObject.put("file_id", file_id);
			jsonObject.put("extension", extension);

			sendMessageToServer(jsonObject.toJSONString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// receiver save file xuống máy:
	public void saveFile(String extension, byte[] fileData) {
		try {
			Platform.runLater(() -> {
				FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", extension));
				File fileToSave = fileChooser.showSaveDialog(null);
				if (fileToSave != null) {
					if (fileToSave.exists()) {
						fileToSave.delete(); // Nếu tệp đã tồn tại, xóa đi trước khi lưu
					}

					// Tạo ProgressBar để hiển thị tiến trình
					ProgressBar progressBar = new ProgressBar(0); // Khởi tạo giá trị tiến độ ban đầu là 0
					progressBar.setPrefWidth(300);

					// Tạo một Dialog và thêm ProgressBar vào nó
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Saving File");
					alert.setHeaderText("Please wait while the file is being saved...");

					VBox content = new VBox(10, progressBar);
					alert.getDialogPane().setContent(content);

					// Hiển thị Alert
					alert.show();

					// Tạo một thread để thực hiện lưu file và cập nhật tiến độ
					new Thread(() -> {
						try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
							int totalSize = fileData.length;
							int bufferSize = 1024; // Kích thước buffer để đọc và ghi từng phần
							int bytesWritten = 0;

							// Ghi file từng phần và cập nhật tiến độ
							for (int i = 0; i < totalSize; i += bufferSize) {
								int remainingBytes = Math.min(bufferSize, totalSize - i);
								byte[] buffer = new byte[remainingBytes];
								System.arraycopy(fileData, i, buffer, 0, remainingBytes);

								fos.write(buffer);
								bytesWritten += remainingBytes;

								// Tính toán tiến độ
								double progress = (double) bytesWritten / totalSize;

								// Cập nhật tiến độ lên ProgressBar
								Platform.runLater(() -> progressBar.setProgress(progress));

								// Nếu đã hoàn thành, thoát khỏi vòng lặp
								if (bytesWritten >= totalSize) {
									break;
								}

								// Giả lập độ trễ để người dùng có thể thấy tiến độ (có thể bỏ qua trong thực
								// tế)
								Thread.sleep(10);
							}

							// Sau khi lưu xong, đóng Alert
							Platform.runLater(alert::close);

						} catch (IOException | InterruptedException e) {
							e.printStackTrace();
						}
					}).start();

				} else {
					System.out.println("No file selected.");
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

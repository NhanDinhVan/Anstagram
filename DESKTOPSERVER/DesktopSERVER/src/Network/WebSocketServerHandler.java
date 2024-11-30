package Network;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WebSocketServerHandler extends WebSocketServer {

	// Lưu trữ kết nối của từng client theo ID
	public static Map<Integer, WebSocket> connectedClients = new HashMap<Integer, WebSocket>();

	public WebSocketServerHandler(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("New WebSocket connection: " + conn.getRemoteSocketAddress());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
		for (Map.Entry<Integer, WebSocket> entry : connectedClients.entrySet()) {
			if (entry.getValue() == conn) {
				connectedClients.remove(entry.getKey());
				break;
			}
		}
	}

	@Override
	public void onMessage(WebSocket conn, String JSONmessage) {
		System.out.println("Message from client: " + JSONmessage);
		try {
			JSONObject jsonObject = (JSONObject) (new JSONParser().parse(JSONmessage));
			String action = (String) jsonObject.get("action");
			if (action.equals("CONNECT TO WEBSOCKET SERVER")) {
				Long connectedSocketClient = (Long) jsonObject.get("webSocketClient_id");
				connectedClients.put(connectedSocketClient.intValue(), conn);
			} else if (action.equals("CHAT")) {
				String message_content = (String) jsonObject.get("message_content");
				Long senderID = (Long) jsonObject.get("senderID");
				Long receiverID = (Long) jsonObject.get("receiverID");
				String time = (String) jsonObject.get("send_time");
				String messageType = (String) jsonObject.get("message_type");
				responseToPartner(message_content, senderID, receiverID, action, time, messageType);
			} else if (action.equals("SEND_FILE_TO_SERVER")) {
				// Xử lý khi nhận được jsonString từ sender client;
				String file_name = (String) jsonObject.get("file_name");
				String file_path = (String) jsonObject.get("file_path");
				String extension = (String) jsonObject.get("extension");
				String base64FileData = (String) jsonObject.get("file_data");
				Long senderID = (Long) jsonObject.get("senderID");
				Long receiverID = (Long) jsonObject.get("receiverID");
				String time = (String) jsonObject.get("send_time");
				String message_type = (String) jsonObject.get("message_type");

				// Giải mã base64 thành byte array
				byte[] file_data = Base64.getDecoder().decode(base64FileData);

				// lưu file xuống ổ đĩa server:
				long file_id = saveFile(extension, base64FileData);

				// thông báo cho receiver biết có 1 file đang được gửi
				// nếu khi nào receiver muốn xem file => request đến server'd folder
				informPartner(file_id, extension, file_name, file_path, senderID, receiverID, time, message_type);

			} else if (action.equals("DOWNLOAD_FILE")) {
				System.out.println("server nhận yêu cầu download file từ client...");
				Long file_id = (Long) jsonObject.get("file_id");
				Long requestorID = (Long) jsonObject.get("requestorID");
				String extension = (String) jsonObject.get("extension");
				responseFileContent(file_id, requestorID, extension);

			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	// khi server nhận được message từ client1 => server sẽ xử lí và phản hồi cho
	// client2
	public void responseToPartner(String messageContent, Long senderID, Long receiverID, String action, String sendtime,
			String messageType) {
		WebSocket receiverConn = connectedClients.get(receiverID.intValue());
		if (receiverConn != null && receiverConn.isOpen()) {
			// Nếu receiver đã kết nối, gửi tin nhắn tới receiver
			JSONObject responseMessage = new JSONObject();
			responseMessage.put("action", action);
			responseMessage.put("senderID", senderID);
			responseMessage.put("receiverID", receiverID);
			responseMessage.put("message_content", messageContent);
			responseMessage.put("send_time", sendtime);
			responseMessage.put("message_type", messageType);

			// Gửi tin nhắn tới client nhận
			receiverConn.send(responseMessage.toJSONString());
			System.out.println("Message: " + responseMessage.toJSONString() + " forwarded to receiver: " + receiverID);
		} else {
			System.out.println("Receiver is not connected or WebSocket is closed.");
		}
	}

	// tbao cho receiver có 1 file đang được gửi đến:
	public void informPartner(long file_id, String extension, String fileName, String file_path, Long senderID,
			Long receiverID, String time, String messageType) {
		try {
			WebSocket receiverConn = connectedClients.get(receiverID.intValue());
			if (receiverConn != null && receiverConn.isOpen()) {
				// Nếu receiver đã kết nối, gửi tin nhắn tới receiver
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("action", "FILE_IS_RECEIVED");
				jsonObject.put("senderID", senderID);
				jsonObject.put("receiverID", receiverID);
				jsonObject.put("send_time", time);
				jsonObject.put("file_name", fileName);
				jsonObject.put("file_path", file_path);
				jsonObject.put("file_id", file_id);
				jsonObject.put("extension", extension);
				jsonObject.put("message_type", messageType);

				receiverConn.send(jsonObject.toJSONString());
				System.out.println("File đã được lưu vào folder server");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long saveFile(String extension, String base64FileData) {
		long id = System.currentTimeMillis();
		String fileStore = "C:\\SERVER\\";
		File file = new File(fileStore + id + extension);

		try {
			// Giải mã Base64 thành byte array
			byte[] fileData = Base64.getDecoder().decode(base64FileData);

			// Ghi dữ liệu vào tệp trên ổ đĩa
			Files.write(file.toPath(), fileData);

			// Sau khi lưu tệp thành công, có thể gửi thông báo về việc lưu tệp cho client
			System.out.println("File saved at: " + file.getAbsolutePath());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return id;
	}

	// phản hồi file mà requestor muốn download:
	public void responseFileContent(Long file_id, Long requestorID, String extension) {
		try {

			WebSocket requestorConn = connectedClients.get(requestorID.intValue());

			String fileStore = "C:\\SERVER\\"; // Thư mục lưu trữ file
			File file = new File(fileStore + file_id + extension); // Tìm file theo ID

			if (file.exists()) {
				// Đọc dữ liệu file thành mảng byte
				byte[] fileData = Files.readAllBytes(file.toPath());

				// Mã hóa dữ liệu file thành Base64
				String base64FileData = Base64.getEncoder().encodeToString(fileData);

				// Gửi dữ liệu file về client yêu cầu tải file
				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("action", "FILE_CONTENT");
				jsonResponse.put("file_id", file_id);
				jsonResponse.put("file_data", base64FileData);
				jsonResponse.put("file_name", file.getName());
				jsonResponse.put("extension", extension);
				jsonResponse.put("requestorID", requestorID);

				// Gửi thông báo file về client
				requestorConn.send(jsonResponse.toJSONString());
				System.out.println("File content sent to client.");

			} else {
				System.out.println("File not found.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

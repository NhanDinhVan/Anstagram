package Network;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class PostHandler implements HttpHandler {
	private static final String UPLOAD_DIR = "C:\\SERVER\\posts\\";

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if ("POST".equals(exchange.getRequestMethod())) {
			String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
			if (contentType != null && contentType.contains("multipart/form-data")) {
				String boundary = contentType.split("boundary=")[1];

				// Đọc và phân tích dữ liệu multipart
				Map<String, Object> formData = parseMultipart(exchange.getRequestBody(), boundary);

				// Lấy và in caption
				System.out.println("Caption: " + formData.get("caption"));

				// Lưu file vào thư mục UPLOAD_DIR
				if (formData.containsKey("file")) {
					String filePath = saveFile((ByteArrayOutputStream) formData.get("file"), formData, "uploaded_file");
					System.out.println("File saved to: " + filePath);
				}

				// Phản hồi lại thành công
				String response = "Post received successfully";
				exchange.sendResponseHeaders(200, response.getBytes().length);
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			} else {
				String response = "Unsupported Content-Type";
				exchange.sendResponseHeaders(415, response.getBytes().length);
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		} else {
			String response = "Only POST method is supported";
			exchange.sendResponseHeaders(405, response.getBytes().length);
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	// Phương thức để phân tích dữ liệu multipart
	private Map<String, Object> parseMultipart(InputStream inputStream, String boundary) throws IOException {
		Map<String, Object> formData = new HashMap<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String line;
		String currentField = null;
		ByteArrayOutputStream fileOutputStream = null;
		String fileName = null; // Biến để lưu tên file
		String fileExtension = null;

		while ((line = reader.readLine()) != null) {
			if (line.startsWith("--" + boundary)) {
				// Lưu dữ liệu file sau khi đọc xong
				if (currentField != null && fileOutputStream != null) {
					formData.put(currentField, fileOutputStream);
					fileOutputStream.close();
				}
				currentField = null;
				fileOutputStream = null;
				fileName = null;
			} else if (line.startsWith("Content-Disposition: form-data;")) {
				String[] parts = line.split(";");
				for (String part : parts) {
					if (part.trim().startsWith("name=")) {
						currentField = part.split("=")[1].replace("\"", "").trim();
					}
					if (part.trim().startsWith("filename=")) {
						fileName = part.split("=")[1].replace("\"", "").trim();
						System.out.println("file name: " + fileName);

						
						// lấy extension từ fileName:
						int lastIndex = fileName.indexOf(".");
						fileExtension = fileName.substring(lastIndex);
						formData.put("file_extension", fileExtension);
					}
				}
				if (currentField != null && fileName != null) {
					fileOutputStream = new ByteArrayOutputStream();
					System.out.println("Starting to read file data: " + fileName);
				}
			}

			else if (!line.isEmpty() && fileOutputStream != null) {
				fileOutputStream.write(line.getBytes("ISO_8859_1"));
				fileOutputStream.write("\r\n".getBytes("ISO_8859_1"));
			} else {
				if (fileOutputStream != null) {
					fileOutputStream.write(line.getBytes("ISO_8859_1"));
					fileOutputStream.write("\r\n".getBytes("ISO_8859_1"));
				} else if (currentField != null) {
					formData.put(currentField, line.trim());
				}
			}

		}
		return formData;
	}

	// Phương thức để lưu file
	private String saveFile(ByteArrayOutputStream fileData, Map<String, Object> formData, String defaultFileName)
			throws IOException {
		// Nếu không có tên file, dùng tên mặc định
		String fileName = defaultFileName + "_" + System.currentTimeMillis();
		String fileExtension = (String) formData.get("file_extension");
		System.out.println("ex get from formdata: " + fileExtension);
//
		// Tạo thư mục upload nếu chưa có
		File uploadDir = new File(UPLOAD_DIR);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		// Tạo đường dẫn lưu file
		String filePath = UPLOAD_DIR + fileName + fileExtension;

		// Lưu dữ liệu vào file
		Files.write(Paths.get(filePath), fileData.toByteArray());
		return filePath;
	}

}

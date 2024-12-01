package Network;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import Common.Constants;
import org.json.simple.JSONObject;

public class PostHandler {

	// Phương thức gửi video / hình ảnh đến server:
	public void sendImage_Video(String filePath, String caption) {
		try {
			URL url = new URL("http://"+ Constants.SERVER_ADDRESS +":8081/api/posts");
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			String boundary = "---MyBoundary";
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			httpConnection.setConnectTimeout(60000); // 30 seconds timeout
			httpConnection.setReadTimeout(60000); // 30 seconds read timeout
			httpConnection.setDoOutput(true);

			try (OutputStream outputStream = httpConnection.getOutputStream()) {
				// Gửi caption
				sendFormData(outputStream, boundary, "caption", caption);
				System.out.println("đã send caption file");

				// Gửi file
				if (filePath != null && !filePath.isEmpty()) {
					File file = new File(filePath);
					sendFileData(outputStream, boundary, file);
					System.out.println("đã send file data");
				}

				// Đóng boundary
				outputStream.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
				outputStream.flush();
			}
			// Xử lí phản hồi từ Server:
			handleResponseFromServer(httpConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Phương thức gửi form data (caption)
	private void sendFormData(OutputStream outputStream, String boundary, String fieldName, String value)
			throws IOException {
		outputStream.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
		outputStream.write(("Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n\r\n")
				.getBytes(StandardCharsets.UTF_8));
		outputStream.write((value + "\r\n").getBytes(StandardCharsets.UTF_8));
	}

	// Phương thức gửi file data
	private void sendFileData(OutputStream outputStream, String boundary, File file) throws IOException {
		String fileName = file.getName();
		String fileType = fileName.endsWith(".mp4") ? "video/mp4" : "image/png"; // Định nghĩa loại file

		outputStream.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
		outputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"; extension=\""
				+ getFileExtension(fileName) + "\"\r\n").getBytes(StandardCharsets.UTF_8));
		outputStream.write(("Content-Type: " + fileType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));

		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		}

		outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

	}

	// Xử lý phản hồi từ server
	public void handleResponseFromServer(HttpURLConnection httpConnection) {
		try {
			int statusCode = httpConnection.getResponseCode();
			BufferedReader reader;
			if (statusCode == HttpURLConnection.HTTP_OK) {
				reader = new BufferedReader(
						new InputStreamReader(httpConnection.getInputStream(), StandardCharsets.UTF_8));
				System.out.println("Post sent successfully!");
			} else {
				reader = new BufferedReader(
						new InputStreamReader(httpConnection.getErrorStream(), StandardCharsets.UTF_8));
				System.out.println("Failed to send post. Response code: " + statusCode);
			}

			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line.trim());
			}
			System.out.println("Response: " + response.toString());
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// lấy phần extension của file:
	public String getFileExtension(String fileName) {
		// ví dụ file name là : abc.png;
		String extension = fileName.substring(fileName.indexOf("."));
		System.out.println("extension: " + extension);
		return extension;
	}
}

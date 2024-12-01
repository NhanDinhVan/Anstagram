package Network;

import Common.Constants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javafx.scene.control.Button;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FollowAction {
	// send id followed cho server:
	public void transferFollowedID(int idFollower, int idFollowed, String state, Button followBtn) {
		try {
			String serverURL = "http://"+ Constants.SERVER_ADDRESS +":8081/api/follow";
			URL url = new URL(serverURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			// Tạo JSON chứa userID và idFollowed
			JSONObject json = new JSONObject();
			json.put("action", "FOLLOW_USER");
			json.put("followerID", idFollower);
			json.put("followedID", idFollowed);
			json.put("state", state); // follow hoặc unfollow

			// Gửi dữ liệu
			try (OutputStream os = connection.getOutputStream()) {
				os.write(json.toJSONString().getBytes("utf-8"));
			}

			// Kiểm tra phản hồi từ server:
			checkResponseFromServerHttp(connection, followBtn);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// check phản hồi từ server sau khi transfer idFollowed:
	public void checkResponseFromServerHttp(HttpURLConnection connection, Button followBtn) throws IOException {
		int responseCode = connection.getResponseCode();
		if (responseCode == 200) {
			// nếu code == 200 (thành công) => Thay button follow thành button following:
			if ("Follow".equals(followBtn.getText())) {
				followBtn.setText("Unfollow");
			} else {
				followBtn.setText("Follow");
			}
		}

	}
}

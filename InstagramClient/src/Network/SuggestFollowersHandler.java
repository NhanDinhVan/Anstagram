package Network;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Common.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SuggestFollowersHandler {

	public JSONArray requestSuggestFollow(int userID) {
		String serverURL = "http://"+ Constants.SERVER_ADDRESS +":8081/api/suggest-follows";
		JSONArray suggestions = new JSONArray();
		try {

			// Tạo kết nối HTTP POST
			URL url = new URL(serverURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			// Gửi userId dưới dạng JSON (hoặc chuỗi thuần)
			JSONObject json = new JSONObject();
			json.put("action", "REQUEST_SUGGEST_FOLLOWS");
			json.put("userID", userID);
			try (OutputStream os = connection.getOutputStream()) {
				os.write(json.toJSONString().getBytes("utf-8"));
			}

			// Gọi hàm nhận phản hồi từ server:
			suggestions = getSuggestedList(connection);
			System.out.println("Đã nhận list suggestion");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return suggestions;
	}

	// nhận phản hồi từ server:
	public JSONArray getSuggestedList(HttpURLConnection connection) {
		JSONArray suggestedJsonArray = null;
		try {
			int resposeCode = connection.getResponseCode();
			if (resposeCode == 200) { // Thành công
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {
					StringBuilder responseBuilder = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						responseBuilder.append(line);
					}

					JSONObject responseJson = (JSONObject) (new JSONParser().parse(responseBuilder.toString()));
					String action = (String) responseJson.get("action");

					if (action.equals("RESPONSE_SUGGESTED_FOLLOWS")) {
						suggestedJsonArray = (JSONArray) responseJson.get("suggestedList");
					}
				}
				System.out.println("array suggestions : " + suggestedJsonArray);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return suggestedJsonArray;

	}

	
}

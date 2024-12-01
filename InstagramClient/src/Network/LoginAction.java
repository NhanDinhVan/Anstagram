package Network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LoginAction {
	public static DatagramSocket cSocket;
	private DatagramPacket dataPacket;
	public static String notify;
	public static Long userID;

	public void requestLogin(String username, String password) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "LOGIN");
			jsonObject.put("username", username);
			jsonObject.put("password", password);
			byte jsonData[] = (jsonObject.toJSONString()).getBytes();

			cSocket = new DatagramSocket();
			InetAddress inetAddress = InetAddress.getByName("localhost");
			dataPacket = new DatagramPacket(jsonData, jsonData.length, inetAddress, 7704);
			cSocket.send(dataPacket);

			receiveResponse(cSocket);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void receiveResponse(DatagramSocket cSocket) {
		try {
			byte data[] = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			cSocket.receive(packet);

			format_jsonString(new String(packet.getData()));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void format_jsonString(String data) {
		try {
			int lastIndexOf = data.lastIndexOf("}");
			String formatted_jsonString = data.substring(0, lastIndexOf + 1);
			JSONObject jsonObject = (JSONObject) (new JSONParser()).parse(formatted_jsonString);
			notify = (String) jsonObject.get("notify");
			if (notify.equals("APPROPRIATE ACCOUNT")) {
				userID = (Long) jsonObject.get("user_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

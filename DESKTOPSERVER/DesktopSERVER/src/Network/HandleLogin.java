package Network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ConnectDB.Lists;
import Model.User;

public class HandleLogin {
	private JSONObject json = new JSONObject();
//	public static List<Integer> onlineClients = new ArrayList<Integer>();

	public void handleLogin(JSONObject jsonObject, DatagramSocket serverSocket, int port, InetAddress addr) {
		try {
			String username = (String) jsonObject.get("username");
			System.out.println("U: " + username);
			String password = (String) jsonObject.get("password");
			System.out.println("P: " + password);

			String notify = "NOT FIND ACCOUNT";
			List<User> users = new Lists().getUsers();
			for (User user : users) {
				System.out.println("ID user: " + user.getUserID());
				if (user.getUsername().equals(username)) {
					if (user.getPassword().equals(password)) {
						notify = "APPROPRIATE ACCOUNT";
						json.put("notify", notify);
						json.put("user_id", user.getUserID());

						// sau khi login thành công -> save ID của user vào hashmap để xe những user nào
						// đang online
//						onlineClients.add(user.getUserID());
					} else {
						notify = "INCORRECT PASSWORD";
						json.put("notify", notify);
					}
					break;
				}
			}

			if (notify.equals("NOT FIND ACCOUNT")) {
				notify = "INCORRECT USERNAME";
				json.put("notify", notify);
			}
			responseLogin(serverSocket, json.toJSONString(), addr, port);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void responseLogin(DatagramSocket sSocket, String responseData, InetAddress addr, int port) {
		try {
			byte data[] = responseData.getBytes();
			DatagramPacket responsePacket = new DatagramPacket(data, data.length, addr, port);
			sSocket.send(responsePacket);
			System.out.println("Server responded login");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

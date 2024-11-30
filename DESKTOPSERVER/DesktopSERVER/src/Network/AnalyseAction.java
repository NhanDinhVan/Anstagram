package Network;

import java.net.DatagramSocket;
import java.net.InetAddress;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AnalyseAction {
	public void analyseAction(String data, int port, InetAddress addr, DatagramSocket sSocket) {
		try {
			int lastIndex = data.lastIndexOf("}"); // lấy vị trí cuối cùng của chuỗi json
			String formated_JSONString = data.substring(0, lastIndex + 1); // jsonString đúng định dạng : {....}

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(formated_JSONString);
			String action = (String) jsonObject.get("action");
			if (action.equals("LOGIN")) {
				new HandleLogin().handleLogin(jsonObject, sSocket, port, addr);

			} else if (action.equals("CONVERSATION")) {
				new HandleMessage().response_relevant_partner(jsonObject, sSocket, port, addr);

			} else if (action.equals("CHOOSE PARTNER")) {
				HandleMessage handleMessage = new HandleMessage();
				int requestor = handleMessage.get_Requestor_ID(jsonObject);
				int choosed_partner = handleMessage.get_choosed_partner(jsonObject);
				handleMessage.return_Detail_Message_List(requestor, choosed_partner, sSocket, port, addr);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

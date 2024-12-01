package Network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Controller.LoginController;
import Model.Partner;
import Model.DetailMessage;

public class ChatAction {
	LoginAction loginAction = new LoginAction();
	public static List<Partner> partnerList = new ArrayList<Partner>();
	public static List<DetailMessage> detailMessageList = new ArrayList<DetailMessage>();

	public void requestChat() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "CONVERSATION");
			jsonObject.put("loginUser", new LoginController().username);
			String jsonString = jsonObject.toJSONString();

			DatagramSocket cSocket = new DatagramSocket();
			byte data[] = jsonString.getBytes();
			InetAddress inetAddr = InetAddress.getLocalHost();
			DatagramPacket packet = new DatagramPacket(data, data.length, inetAddr, 7704);
			cSocket.send(packet);

			receive_Relevant_Partner(cSocket);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void receive_Relevant_Partner(DatagramSocket cSocket) {
		try {
			byte dataUsers[] = new byte[1024*5];
			DatagramPacket packet = new DatagramPacket(dataUsers, dataUsers.length);
			cSocket.receive(packet);
			System.out.println("relevant partner: " + new String(packet.getData()));
			format_jsonArray(new String(packet.getData()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void format_jsonArray(String jsonString) {
		try {
			int lastIndexOf = jsonString.lastIndexOf("]");
			String jsonArrayString = jsonString.substring(0, lastIndexOf + 1);

			JSONParser parser = new JSONParser();
			JSONArray rlvmJSONArray = (JSONArray) parser.parse(jsonArrayString);

			for (int i = 0; i < rlvmJSONArray.size(); i++) {
				JSONObject relevantPartner = (JSONObject) rlvmJSONArray.get(i);
				Long pID = (Long) relevantPartner.get("partnerID");
				String pEmail = (String) relevantPartner.get("partner_email");
				String pUsername = (String) relevantPartner.get("partnerUsername");
				String pPassword = (String) relevantPartner.get("partner_Password");
				String pBio = (String) relevantPartner.get("partner_bio");
				String pCreatedAt = (String) relevantPartner.get("partner_created_at");
				String pProfilePic = (String) relevantPartner.get("partner_profile_picture");
				String pName = (String) relevantPartner.get("partner_name");

				Partner partner = new Partner(pID.intValue(), pUsername, pPassword, pBio, pEmail, pProfilePic,
						pCreatedAt, pName);
				partnerList.add(partner);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// click chọn pârtner để chat:
	public void requestPartner(int partnerID) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "CHOOSE PARTNER");
			jsonObject.put("choosed_partner", partnerID); // id người được chọn để chat:
			jsonObject.put("requestor_ID", new LoginAction().userID); // owner's account ( người đang dùng IG):

			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress addr = InetAddress.getLocalHost();
			byte[] data = (jsonObject.toJSONString()).getBytes();
			DatagramPacket requestPacket = new DatagramPacket(data, data.length, addr, 7704);
			clientSocket.send(requestPacket);

			receive_detail_conversation(clientSocket);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// sau khi gui requestPartner() -> nhận về chi tiết tất cả các message liên quan
	// đến partner đó;
	public void receive_detail_conversation(DatagramSocket cSocket) {
		try {
			byte data[] = new byte[1024 * 5];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			cSocket.receive(packet);

			System.out.println("detail message: " + new String(packet.getData()));
			format_message_jsonArr(new String(packet.getData()));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void format_message_jsonArr(String data) {
		try {
			int lastIndexOf = data.lastIndexOf("]");
			String jsonArr_String = data.substring(0, lastIndexOf + 1);
			JSONArray jsonArray = (JSONArray) (new JSONParser().parse(jsonArr_String));
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				Long message_id = (Long) jsonObject.get("message_id");
				String message_content = (String) jsonObject.get("message_content");
				String send_time = (String) jsonObject.get("send_time");
				Long receiver_id = (Long) jsonObject.get("receiver_id");
				Long sender_id = (Long) jsonObject.get("sender_id");
				Long requestor_id = (Long) jsonObject.get("requestor_id");
				String message_type = (String) jsonObject.get("message_type");
				DetailMessage detailMessage = new DetailMessage(message_id.intValue(), requestor_id.intValue(),
						sender_id.intValue(), receiver_id.intValue(), message_content, send_time, message_type);
				detailMessageList.add(detailMessage);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

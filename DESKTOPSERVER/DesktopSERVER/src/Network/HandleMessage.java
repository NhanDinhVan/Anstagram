package Network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ConnectDB.ConnectDB;
import ConnectDB.DetailMessages;
import ConnectDB.Lists;
import Model.Partner;
import Model.DetailMessage;
import Model.User;

public class HandleMessage {

	public void response_relevant_partner(JSONObject jsonObject, DatagramSocket serverSocket, int port,
			InetAddress addr) {
		try {
			String loginUser = (String) jsonObject.get("loginUser");
			List<User> users = new Lists().getUsers();
			JSONArray arrJSON = new JSONArray();

			for (User u : users) {
				if (u.getUsername().equals(loginUser)) {
					List<Partner> releventPartners = new Lists().getReleventPartner(u.getUserID());
					for (Partner rlvPartner : releventPartners) {
						JSONObject rlvJSON = new JSONObject();
						rlvJSON.put("partnerID", rlvPartner.getUserID());
						rlvJSON.put("partnerUsername", rlvPartner.getUsername());
						rlvJSON.put("partner_Password", rlvPartner.getPassword());
						rlvJSON.put("partner_bio", rlvPartner.getBio());
						rlvJSON.put("partner_created_at", rlvPartner.getCreated_at());
						rlvJSON.put("partner_email", rlvPartner.getEmail());
						rlvJSON.put("partner_profile_picture", rlvPartner.getProfile_picture());
						arrJSON.add(rlvJSON);
					}
				}
			}
			byte dataArray[] = arrJSON.toJSONString().getBytes();
			DatagramPacket packet = new DatagramPacket(dataArray, dataArray.length, addr, port);
			serverSocket.send(packet);
			System.out.println("Server responded arrJSON: " + new String(packet.getData()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void return_Detail_Message_List(int resquestor, int choosed_partner, DatagramSocket sSocket, int port,
			InetAddress addr) {
		try {
			List<DetailMessage> Messages = new DetailMessages().get_detail_messages(resquestor, choosed_partner);
			JSONArray jsonArr = new JSONArray();

			for (DetailMessage dm : Messages) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("message_id", dm.getMessageID());
				jsonObject.put("message_content", dm.getMessage_text());
				jsonObject.put("sender_id", dm.getSender());
				jsonObject.put("receiver_id", dm.getReceiver());
				jsonObject.put("send_time", dm.getSend_time());
				jsonObject.put("requestor_id", dm.getUserLogin());
				jsonObject.put("message_type", dm.getMessageType());
				jsonArr.add(jsonObject);
			}

			byte dataArr[] = (jsonArr.toJSONString()).getBytes();
			DatagramPacket packet = new DatagramPacket(dataArr, dataArr.length, addr, port);
			sSocket.send(packet);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int get_Requestor_ID(JSONObject jsonObject) {
		Long requestorID = (Long) jsonObject.get("requestor_ID");
		return requestorID.intValue();
	}

	public int get_choosed_partner(JSONObject jsonObject) {
		Long choosed_partner_id = (Long) jsonObject.get("choosed_partner");
		return choosed_partner_id.intValue();
	}

}

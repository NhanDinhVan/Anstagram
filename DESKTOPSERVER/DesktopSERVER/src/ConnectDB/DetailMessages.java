package ConnectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.DetailMessage;

public class DetailMessages {

	// select ra những messages liên quan đến requestor và choosed_partner:
	private List<DetailMessage> detailMessageList = new ArrayList<DetailMessage>();

	public List<DetailMessage> get_detail_messages(int requestor, int choosed_partner) {
		try {
			Connection connection = null;
			if (connection == null) {
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					String url = "jdbc:mysql://localhost:3306/desktopappdb";
					String nameConnect = "root";
					String pass = "";
					connection = DriverManager.getConnection(url, nameConnect, pass);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			String query_old_messages = "select messages.messageType, messages.message_id,users.username , senderID, receiverID, message_text, send_time \r\n"
					+ "from messages\r\n" + "inner join users on users.userID = messages.senderID \r\n"
					+ "where (messages.senderID = '" + requestor + "' or messages.receiverID = '" + requestor
					+ "') and (senderID = " + choosed_partner + " or receiverID = " + choosed_partner + ")\r\n"
					+ "order by send_time asc";
			Statement statement = connection.createStatement();
			ResultSet rss = statement.executeQuery(query_old_messages);
			while (rss.next()) {
				int message_id = rss.getInt("message_id");
				int senderID = rss.getInt("senderID");
				int receiverID = rss.getInt("receiverID");
				String send_time = rss.getString("send_time");
				String message_content = rss.getString("message_text");
				String message_type = rss.getString("messageType");

				DetailMessage detailMessage = new DetailMessage(message_id, requestor, senderID, receiverID,
						message_content, send_time, message_type);
				detailMessageList.add(detailMessage);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return detailMessageList;
	}
}

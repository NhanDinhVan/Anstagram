package ConnectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Partner;
import Model.DetailMessage;
import Model.User;

public class Lists {

	private List<User> userList = new ArrayList();
	private List<Partner> relevantPartners = new ArrayList<Partner>();

	public List<User> getUsers() throws SQLException { // users (userID, bio, username, password, email,profile_picture,
														// created_time)
//		ConnectDB connectDB = new ConnectDB();
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
		String selectUsers = "select * from users";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(selectUsers);

		while (resultSet.next()) {
			int userID = resultSet.getInt("userID");
			String username = resultSet.getString("username");
			String password = resultSet.getString("password");
			String email = resultSet.getString("email");
			String profile_picture_url = resultSet.getString("profile_picture");
			String bio = resultSet.getString("bio");
			String created_at = resultSet.getString("created_at");

			User user = new User(userID, username, password, bio, email, profile_picture_url, created_at);
			userList.add(user);
		}
		return userList;

	}

	public List<Partner> getReleventPartner(int userID) {
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
			String selectRelevantPartner = "SELECT DISTINCT u.userID , u.username , u.password , u.profile_picture , u.bio , u.email , u.created_at \r\n"
					+ "FROM Users u\r\n"
					+ "inner JOIN Messages m ON m.senderID = u.userID OR m.receiverID = u.userID\r\n"
					+ "WHERE (m.senderID = '" + userID + "' OR m.receiverID = '" + userID + "') AND u.userID != '"
					+ userID + "'\r\n" + "group by u.userID ";

			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(selectRelevantPartner);
			while (resultSet.next()) {
				int partnerID = resultSet.getInt("userID");
				String partner_Username = resultSet.getString("username");
				String partner_Password = resultSet.getString("password");
				String partner_Email = resultSet.getString("email");
				String partner_Profile_Picture_Url = resultSet.getString("profile_picture");
				String partner_Bio = resultSet.getString("bio");
				String partner_Created_At = resultSet.getString("created_at");
				Partner relevantPartner = new Partner(partnerID, partner_Username, partner_Password, partner_Bio,
						partner_Email, partner_Profile_Picture_Url, partner_Created_At);
				relevantPartners.add(relevantPartner);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return relevantPartners;
	}

}

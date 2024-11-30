package ConnectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectDB {

	private Connection connection;

	public ConnectDB() {
		if (connection == null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				String url = "jdbc:mysql://localhost:3306/desktopappdb";
				String nameConnect = "root";
				String pass = "";
				connection = DriverManager.getConnection(url, nameConnect, pass);
//				System.out.println("successfully connect...");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public Connection getConnection() {
		return this.connection;
	}

	public Statement getStatement() {
		Statement stm = null;
		try {
			stm = connection.createStatement();
		} catch (Exception e) {
			System.err.println("Error get statement: " + e.getMessage());
		}
		return stm;
	}

}

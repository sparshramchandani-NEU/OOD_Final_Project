package utility;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

	public static Connection databaseConnection() {
		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/vehirenthub", "root", "12345678");
			return connect;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

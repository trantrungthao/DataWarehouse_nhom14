package connection.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//Ket noi vao Database (mySQL)
public class ConnectionDB {
	public static Connection getConnectionDB() throws ClassNotFoundException, SQLException {
		String hostName = "127.0.0.1";
		String userName = "root";
		String password = "1234";
		String dbName = "control_db";
		return getConnectionDB(hostName, userName, password, dbName);
	}
	public static Connection getConnectionDB(String hostName, String userName, String password, String dbName) 
			throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://" + hostName + ":3306/" + dbName;
		Connection connection = DriverManager.getConnection(url, userName, password);
		return connection;
	}
}

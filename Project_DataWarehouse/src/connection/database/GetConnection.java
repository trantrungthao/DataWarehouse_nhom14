package connection.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class GetConnection {

	String driver = null;
	String url = null;
	String userName = null;
	String passWord = null;
	String dbName = null;

	public Connection getConnection(String location) {
		String link = "files/config.properties";
		Connection result = null;
		
		if (location.equalsIgnoreCase("control_db")) {
			try (InputStream input = new FileInputStream(link)) {
				Properties prop = new Properties();
				prop.load(input);
				driver = prop.getProperty("driver_local");
				url = prop.getProperty("url_local");
				dbName = prop.getProperty("dbName_control");
				userName = prop.getProperty("userName_local");
				passWord = prop.getProperty("password_local");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} 
		try {
			Class.forName(driver);
			String connectionURL = url + dbName;
			try {
				result = DriverManager.getConnection(connectionURL, userName, passWord);
			} catch (SQLException e) {
				System.out.println("Lỗi kết nối vui lòng thử lại...");
				System.exit(0);
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			System.out.println("Không tìm thấy file config...");
			System.exit(0);
			e.printStackTrace();
		}

		return result;
	}
	//
	public static void main(String[] args) {
		Connection conn = new GetConnection().getConnection("control_db");
		if (conn != null) {
			System.out.println("Kết nối thành công...");
		}
	}
}

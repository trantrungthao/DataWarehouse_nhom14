package model;

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
	String user = null;
	String pass = null;
	String databasebName = null;

	public Connection getConnection(String location) {
		String link = "C:\\DevPrograms\\git\\DataWarehouse_nhom14\\Project_DataWarehouse\\src\\model\\config.properties";
		Connection result = null;
		
		if (location.equalsIgnoreCase("control")) {
			try (InputStream input = new FileInputStream(link)) {
				Properties prop = new Properties();
				prop.load(input);
				driver = prop.getProperty("driver_server");
				url = prop.getProperty("url_server");
				databasebName = prop.getProperty("dbName_control");
				user = prop.getProperty("userName_server");
				pass = prop.getProperty("password_server");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else if (location.equalsIgnoreCase("staging")) {
			try (InputStream input = new FileInputStream(link)) {
				Properties prop = new Properties();
				prop.load(input);
				driver = prop.getProperty("driver_local");
				url = prop.getProperty("url_local");
				databasebName = prop.getProperty("dbName_staging");
				user = prop.getProperty("userName_local");
				pass = prop.getProperty("password_local");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			System.out.println(driver);

		}
		try {
			Class.forName(driver);
			String connectionURL = url + databasebName;
			try {
				result = DriverManager.getConnection(connectionURL, user, pass);
			} catch (SQLException e) {
				System.out.println("Loi ket noi, kiem tra lai");
				System.exit(0);
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			System.out.println("Khong thay file config");
			System.exit(0);
			e.printStackTrace();
		}

		return result;
	}
///test
	public static void main(String[] args) {
		Connection conn = new GetConnection().getConnection("control");
		if (conn != null) {
			System.out.println("Thanh cong");
		}
	}
}

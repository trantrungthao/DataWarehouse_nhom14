package connection.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConfig {
	public static String[] getConfigInformation() throws ClassNotFoundException, SQLException {
		String[] line = new String[9];
		Connection connection = ConnectionDB.getConnectionDB();
		String sql = "SELECT * FROM table_config";
		PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			line[0] = Integer.toString(rs.getInt("id"));
			line[1] = rs.getString("fileName");
			line[2] = rs.getString("hostName");
			line[3] = Integer.toString(rs.getInt("port"));
			line[4] = rs.getString("userName");
			line[5] = rs.getString("passWord");
			line[6] = rs.getString("remotePath");
			line[7] = rs.getString("localPath");
			line[8] = rs.getString("status");
		}

		connection.close();
		return line;
	}
//	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//		String[] line = DBConfig.getConfigInformation();
//		for (int i = 0; i < line.length; i++) {
//			System.out.println(line[i]);
//		}
//	}
}

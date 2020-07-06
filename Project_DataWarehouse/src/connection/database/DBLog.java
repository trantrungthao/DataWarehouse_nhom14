package connection.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBLog {
	public static void getLogInformation(String id_log, String filename) throws ClassNotFoundException, SQLException {
		int id = Integer.parseInt(id_log);
		Connection connection = ConnectionDB.getConnectionDB();
		String sql = "Insert into table_log (id, fileName, state) values ('"+id+"','" +filename +"','ER') ";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.executeUpdate(sql);
		connection.close();
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		}
	}

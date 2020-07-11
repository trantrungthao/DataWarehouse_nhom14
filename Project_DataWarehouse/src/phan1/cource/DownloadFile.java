package phan1.cource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;

import connection.database.GetConnection;

public class DownloadFile {
	static {
		try {
			System.loadLibrary("chilkat");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}

	//
	public void DownloadFie() throws ClassNotFoundException, SQLException {
		// 1. Kết nối tới Database Control_DB
		Connection connection = new GetConnection().getConnection("control_db");
		// 2. Kết nối tới bảng table_config
		String sql = "SELECT * FROM table_config";
		PreparedStatement ps = connection.prepareStatement(sql);
		// 3. Nhận resultSet chứa các record truy xuất
		ResultSet rs = ps.executeQuery();
		// 4. Duyệt record
		while (rs.next()) {
			String id = Integer.toString(rs.getInt("id"));
			String fileName = rs.getString("fileName");
			String hostName = rs.getString("hostName");
			int port = rs.getInt("port");
			String userName = rs.getString("userName");
			String passWord = rs.getString("passWord");
			String remotePath = rs.getString("remotePath");
			String localPath = rs.getString("localPath");

		// 5. Kết nối đến Server Cource
			CkSsh ssh = new CkSsh();
			CkGlobal ck = new CkGlobal();
			ck.UnlockBundle("Download");
			boolean success = ssh.Connect(hostName, port);
			if (success != true) {
				// 5.1 In thông báo lỗi ra màn hình
				System.out.println(ssh.lastErrorText());
				System.out.println("Kết nối đến server cource bị lỗi...");
				// 5.2 Gửi mail thông báo lỗi
				SendMail.sendMail("test@st.hcmuaf.edu.vn", "Warehouse", "bị lỗi kết nối");
				// 5.3 Ghi lại log file bị lỗi
				
				return;
			}
			ssh.put_IdleTimeoutMs(5000);
			//
			success = ssh.AuthenticatePw(userName, passWord);
			System.out.println("Đã đăng nhập...");
			if (success != true) {
				System.out.println(ssh.lastErrorText());
				return;
			}
			//
			CkScp scp = new CkScp();
			success = scp.UseSsh(ssh);
			if (success != true) {
				System.out.println(scp.lastErrorText());
				return;
			}
		// 6. Gọi hàm DownloadFile để tải file
			String srcPath = remotePath + "/" + fileName;
			String localhost = localPath + "\\" + fileName;
			success = scp.DownloadFile(srcPath, localhost);
		// 7. In thông báo tải file thành công ra màn hình
			System.out.println(fileName+": tải thành công");
			
		// 8. Ghi lai log
			String log = "Insert into table_log(id, fileName, status) values ('"+id+"','" +fileName +"','ER') ";
			PreparedStatement pslog = connection.prepareStatement(log);
			pslog.executeUpdate(log);
			System.out.println(id+": "+fileName+": "+"Đã ghi vào table_log...");
			//Kiểm tra tải file nếu tải không thành công
			if (success != true) {
				// 8.1 In thông báo lỗi ra màn hình
				System.out.println(ssh.lastErrorText());
				System.out.println(id+": "+fileName+": "+" Bị lỗi...");
				// 8.2 Gửi mail thông báo lỗi
				SendMail.sendMail("test@st.hcmuaf.edu.vn", "Warehouse", fileName+": Bị lỗi");
				// 8.3 Ghi lại log file bị lỗi
				
				return;
			}
			//
			ssh.Disconnect();

		}
		// 11. Đóng kết nối
		connection.close();

	}
	public static void main(String argv[]) throws ClassNotFoundException, SQLException {
		DownloadFile load = new DownloadFile();
		load.DownloadFie();
	}
}

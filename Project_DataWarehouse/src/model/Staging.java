package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

//Step2: Load data file from local to DB Staging
public class Staging {

	public void staging(String condition) {
		Connection conn = null;
		PreparedStatement pre_control = null;
		try {
			// 1. Kết nối tới Control_DB
			conn = new GetConnection().getConnection("control");
			// 2. Tìm các file có trạng thái Download ok ở các nhóm đang active
			pre_control = conn.prepareStatement(
					"SELECT data_file_logs.id ,ID_host, your_filename, table_staging_load, "
							+ " data_file_configuaration.delimiter, data_file_configuaration.local_directory,encode,"
							+ "data_file_configuaration.number_column from data_file_logs "
							+ "JOIN data_file_configuaration ON data_file_logs.ID_host = data_file_configuaration.id"
							+ " where "
							+ "data_file_logs.status_file like 'Download ok' AND data_file_configuaration.isActive=1 ");
			// 3. Nhận được ResultSet chứa các record thỏa điều kiện truy xuất
			ResultSet re = pre_control.executeQuery();
			int id;
			String filename = null;
			// 4. chạy từng record trong resultset
			while (re.next()) {
				// open file
				id = re.getInt("id");
				String encode = re.getString("encode");

				String table_staging = re.getString("table_staging_load");// valuesList
				String dir = re.getString("local_directory");
				filename = re.getString("your_filename");

				String delimiter = re.getString("delimiter");
				int number_column = re.getInt("number_column");// số cột thuộc tính

				// 5. Kiểm tra file có tồn tại trên folder local?
				String path = dir + "\\" + filename;
				System.out.println(path);
				File file = new File(path);// mo file
				if (!file.exists()) {
					// 6.1.1. Thông báo file không tồn tại ra màn hình
					System.out.println(path + "khong ton tai");
					// 6.1.2. Cập nhật status_file là ERROR Staging, time_staging là ngày giờ hiện
					// tại
					String sql2 = "UPDATE data_file_logs SET "
							+ "status_file='ERROR Staging', data_file_logs.time_staging=now() WHERE id=" + id;
					pre_control = conn.prepareStatement(sql2);
					pre_control.executeUpdate();
				} else {

					try {
						// 6.2.1. Mở file để đọc dữ liệu lên, có kèm theo encoding
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(new FileInputStream(file), encode));
						// 6.2.2. Đọc bỏ phần header
						reader.readLine();

						// 6.2.3. Bắt đầu từ hàng thứ 2, đọc từng hàng dữ liệu đến khi cuối file
						String data;
						data = reader.readLine();
						int count = 0, z = 0;
						System.out.println(data);

						String value = "insert into " + table_staging + " values";
						// lay tung hang len
						while (data != null) {// còn hàng?
							// 6.2.4. cắt hàng theo delimeter lưu trên data_file_logs
							StringTokenizer st = new StringTokenizer(data, delimiter);
							// 6.2.5. Lưu hàng sinh viên đó vào chuỗi value
							// staging
							if (st.countTokens() == number_column) {
								value += "(";// 1 bo du lieu(, , , ,)
								value += "'" + st.nextToken() + "'";
								for (int j = 2; j <= number_column; j++) {
									value += ", '" + st.nextToken() + "'";
								}
								value += "),";
								z++;
							}
							// lay hang tiep theo len
							data = reader.readLine();
							if (data != null) {
								System.out.println(data);
							}
						} // end while row of file
							// xoa cai dau , cuoi cung

						if (z > 0) {
							// 6.2.6 Cắt phần thừa của chuỗi value
							value = value.substring(0, value.lastIndexOf(","));
							value += ";";
							// 7. Đóng nguồn file
							reader.close();
							// thuc hien them 1 file vao
							// 8. Mở kết nối DB staging
							Connection conn_Staging = new GetConnection().getConnection("staging");
							// 9. Load tất cả students vào DB
							PreparedStatement pre = conn_Staging.prepareStatement(value);
							// 10. Lưu số dòng load thành công
							count = pre.executeUpdate();
						}

						// update: cot staging_load_count trong logs

						System.out.println(
								"Thanh Cong:\t" + "file name: " + filename + " ==> So dong thanh cong: " + count);
						// 12. Kiểm tra sô dòng đọc được vào staging của file
						if (count > 0) {
							// 12.1 Cập nhật số trạng thái file là Staging ok, time_staging là ngày giờ hiện
							// tại, cập nhật số dòng đọc được
							String sql2 = "UPDATE data_file_logs SET staging_load_count=" + count + ", "
									+ "status_file='Staging ok', data_file_logs.time_staging=now()  WHERE id=" + id;
							pre_control = conn.prepareStatement(sql2);
							pre_control.executeUpdate();

						} else {
							// 12.1 Cập nhật số trạng thái file là ERROR Staging, time_staging là ngày giờ
							// hiện tại, cập nhật số dòng đọc được
							String sql2 = "UPDATE data_file_logs SET staging_load_count=" + count + ", "
									+ "status_file='ERROR Staging', data_file_logs.time_staging=now() WHERE id=" + id;
							pre_control = conn.prepareStatement(sql2);
							pre_control.executeUpdate();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				} // else file exist end

			} // end while file

			// 11. Đóng kết nối
			re.close();
			pre_control.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new Staging().staging("'Download ok'");
	}
}

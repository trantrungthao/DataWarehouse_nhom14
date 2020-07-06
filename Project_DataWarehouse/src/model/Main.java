package model;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
	public Main() {

	}
	public static void main(String[] args) {
		while (true) {
			System.out.println("Nhập số tùy chọn:\n 1. Kết nối\n "
					+ "2. Load to DB Staging\n 0. Thoát");
			Scanner sc = new Scanner(System.in);
			int value = sc.nextInt();
			if (value == 1) {
				Connection conn = new GetConnection().getConnection("staging");
				if (conn != null) {
					System.out.println("Kết nối thành công");
				}

			}  else if (value == 2) {
				// staging
				System.out.println("Nhập status file: (Ví dụ: Download ok) ");
				Scanner sc2 = new Scanner(System.in);
				String input = sc2.nextLine();
				new Staging().staging(input);
			}  else if (value == 0) {
				System.out.println("Hẹn gặp lại sau!");
				break;
			} else {
				System.out.println("Nhập không đúng số trong danh mục!");
			}
		}
	}

}

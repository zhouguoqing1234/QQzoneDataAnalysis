package com.whut.zhou.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

public class JDBCUtils {
	
	public static Connection getConnection() throws SQLException {
		Connection conn = null;
		String url = "jdbc:mysql://localhost:3306/qqzonespider?"
	                + "user=root&password=123456&useUnicode=true&characterEncoding=UTF8";
		 
		  try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		  
		return  conn;
	}

}

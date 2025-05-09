package com.patreon.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            String url = "jdbc:sqlite:JavaDatabase.db";
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Connected to DB at: " + new File("JavaDatabase.db").getAbsolutePath());
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}


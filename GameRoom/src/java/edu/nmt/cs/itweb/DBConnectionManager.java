/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author huipingyao
 */
public class DBConnectionManager {
    /**
     * Connect to a database using the MySQL JDBC driver
     * @return 
     */
    public static Connection getConnection()
    {
        // Driver
        String driver = "com.mysql.jdbc.Driver";
        
        // URL
        String url = "jdbc:mysql://localhost:3306/gameroom";
        
        // username
        String username = "hyao";
        
        // password
        String password = "gameroompwd";
      
        return getConnection(driver, url, username, password);
    }
    
    /**
     * Connect to a database
     * @param driver    driver name
     * @param url   MySQL database URL
     * @param dbNme database name
     * @param username  database username
     * @param password  database password
     * @return 
     */
    public static Connection getConnection(String driver, String url, String username, String password){
        Connection conn = null;
        try {
            // Load driver
            Class.forName(driver);
            
            // Connect database
            conn = DriverManager.getConnection(url, username, password);
            
            if(conn != null && !conn.isClosed()){
                System.out.println("Succeeded connecting to the Database!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } 
        return conn;
    }
}

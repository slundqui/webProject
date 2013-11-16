/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author huipingyao
 */
public class RegisterServlet extends HttpServlet{
    private Connection conn = null;
	
    private PrintWriter out = null;
    private HttpServletResponse res = null;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        res = response;
        out = res.getWriter();

        // Get a connection
        String GameRoomMySQLServer = "jdbc:mysql://localhost:3306/gameroom";
        String dbUsername = "hyao";
        String dbPassword = "gameroompwd";
        connect(GameRoomMySQLServer, dbUsername, dbPassword);

        // Insert the new user into our tables
        String playerName = request.getParameter("username");
        String playerPwd = request.getParameter("pwd");
        insertData(playerName, playerPwd);

        // disconnect from the database
        disconnect();

        HttpSession session =  request.getSession();
        session.setAttribute("LOGIN_USER", playerName);
        session.setAttribute("MESSAGE", null);
        response.sendRedirect("lobby.jsp");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    /**
     * Insert data in our table
     * @param userName: username of the new registered user 
     * @param password: password of the new registered user
     */
    public void insertData(String userName, String password) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO user(uid, pwd) VALUES ('"+userName+"', '"+password+"')");
        } catch (Exception e) {
            reportError("Error performing INSERT" + e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                reportError("Error closing" + e);
            }
        }
    }
    
    /**
     * Connects to a database using the MySQL JDBC driver
     */
    public void connect(String url, String login, String password)
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, login, password);
        } catch (Exception e) {
            reportError("Error loading mysql JDBC driver: " + e);
        }

        out.println("<p>Connected...");
    }
    
    /**
     * Closes our connection to the database
     */
    public void disconnect() {
        try {
            conn.close();
        } catch (Exception e) {
	    // Extensive error handling could be done here but not
            // neccessary for this trivial application
        }
    }
    
    /**
     * Report Errors
     */
    public void reportError(String errmsg) {
        try {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errmsg);
        } catch (Exception e) {
        }
    }
}

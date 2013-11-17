/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
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

        //Get a connection
        String GameRoomMySQLServer = "jdbc:mysql://localhost:3306/gameroom";
        String dbUsername = "hyao";
        String dbPassword = "gameroompwd";
        connect(GameRoomMySQLServer, dbUsername, dbPassword);

        // Get the username and password from the UI
        String playerName = request.getParameter("username");
        String playerPwd = request.getParameter("pwd");
     
        // Encrypt the password with SHA256
        String encryptedPwd = "";
        try{
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(playerPwd.getBytes("UTF-8"));
            for(int i : hash){
                encryptedPwd += Integer.toHexString(0XFF & i);
            }
        }
        catch(NoSuchAlgorithmException ex){
            Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        // Insert the username and encrypted password into database
        insertData(playerName, encryptedPwd);

        // Disconnect from the database
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
     * @param userName  username of the new registered user 
     * @param password  password of the new registered user
     */
    public void insertData(String userName, String password) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO user(uid, pwd) VALUES ('"+userName+"', '"+password+"')");
        } catch (SQLException e) {
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

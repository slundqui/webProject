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
    private PrintWriter out = null;
    private HttpServletResponse res = null;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        res = response;
        out = res.getWriter();

        // Get the username and password from the UI
        String firstName = request.getParameter("firstname");
        String middleName = request.getParameter("middlename");
        String lastName = request.getParameter("lastname");
        String email = request.getParameter("email");
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
        insertData(playerName, encryptedPwd, firstName, middleName, lastName, email);

        // Get the session and redirect user to lobby
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
    public void insertData(String userName, String password, String firstname, String middlename, String lastname, String email) {
        if(middlename == null){
            middlename = "";
        }
        
        Connection conn = DBConnectionManager.getConnection();
        if(conn != null){
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate("INSERT INTO user(uid, pwd, firstname, middlename, lastname, email) VALUES "
                                + "('" + userName + "', '" + password + "', '"+ firstname + "','"+ middlename + "','" + lastname + "','" + email +"')");
            } catch (SQLException ex) {
                Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if(stmt != null) try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}

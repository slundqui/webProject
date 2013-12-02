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
import java.sql.ResultSet;
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
public class ChangePasswordServlet extends HttpServlet{
    private PrintWriter out = null;
        
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        
        // Get the password from the interface
        String playerPwd = request.getParameter("oldPwd");
        
        // Encrypt the password with SHA256
        String oldEncryptedPwd = "";
        try{
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(playerPwd.getBytes("UTF-8"));
            for(int i : hash){
                oldEncryptedPwd += Integer.toHexString(0XFF & i);
            }
        }
        catch(NoSuchAlgorithmException ex){
            Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Get the username from session
        HttpSession session =  request.getSession();
        String username = (String)session.getAttribute("LOGIN_USER");
        
        // Get the user's stored password
        String retrievedHashPwd = null;
        Connection conn = DBConnectionManager.getConnection();
        if(conn != null){
            ResultSet rs = null;
            Statement stmt = null;
            try {
                // Get the data of that user from database
                String sqlQuery = "SELECT * FROM user WHERE uid = '" + username + "'";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sqlQuery);
                while (rs.next()) {
                    retrievedHashPwd = rs.getString("pwd");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close the connection
                try {
                    //Resultset closed prior to conn.close(), even if Statement object closes the ResultSet object implicitly when it closes
                    if (rs != null) {
                        rs.close(); //If you don't close the ResultRet(cursor), it will throw an error like "Maximum open cursors exceeded"
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        // Compare the two passwords
        if(!oldEncryptedPwd.equals(retrievedHashPwd)){
            //response.getWriter().write("<msg>wrongpwd</msg>");
            request.setAttribute("oldpwd_warning", "Password is incorrect.");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
        }else{
            // Encrypt the new password
            String newPwd = request.getParameter("newPwd");
            String newEncryptedPwd = "";
            try{
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(newPwd.getBytes("UTF-8"));
            for(int i : hash){
                newEncryptedPwd += Integer.toHexString(0XFF & i);
                }
            }
            catch(NoSuchAlgorithmException ex){
                Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Update the new password
            conn = DBConnectionManager.getConnection();
            if (conn != null) {
                Statement stmt = null;
                try {
                    stmt = conn.createStatement();
                    stmt.executeUpdate("UPDATE user SET pwd='" + newEncryptedPwd + "' where uid='" + username + "'");
                } catch (SQLException ex) {
                    Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            response.sendRedirect("changePasswordSuccess.jsp");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
 * @author Chen
 */
public class LoginServlet extends HttpServlet {
    
    private boolean getUserAuthentication(String username, String password){
        // To-do: connect database to check if (username, password) exists
        if( (username.equals("chen") && password.equals("123"))
          ||(username.equals("hyao") && password.equals("123")) 
          ||(username.equals("slundqui") && password.equals("123")) ){
            return true;
        }
        
        // Encrypt password with SHA256
        String encryptedPwd = "";
        try{
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(password.getBytes("UTF-8"));
            for(int i : hash){
                encryptedPwd += Integer.toHexString(0XFF & i);
            }
        } catch(NoSuchAlgorithmException ex){
            Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Get the hashed password from database
        String retrievedHashPwd = null;
        Connection conn = DBConnectionManager.getConnection();
        if(conn != null){
            ResultSet rs = null;
            Statement stmt = null;
            try {
                // Get the data of that user from database
                String sqlQuery = "SELECT * FROM USER WHERE uid = '" + username + "'";
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
        
        //Compare the passwords, return true if they are the same
        if(retrievedHashPwd != null && retrievedHashPwd.equals(encryptedPwd))
        {
            return true;
        }
        
        return false;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session =  request.getSession();
        if(getUserAuthentication(username, password)){
            session.setAttribute("LOGIN_USER", username);
            session.setAttribute("MESSAGE", null);
            response.sendRedirect("lobby.jsp");
        }
        else {
            session.setAttribute("MESSAGE", "The username or password is incorrect. Please try again.");
            response.sendRedirect("login.jsp");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

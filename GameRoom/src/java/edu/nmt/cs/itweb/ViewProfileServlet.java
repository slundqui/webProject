/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.io.IOException;
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
public class ViewProfileServlet extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        // Get the username from session
        HttpSession session =  request.getSession();
        String username = (String)session.getAttribute("LOGIN_USER");
        
        // Get the user's resume
        String firstname = "";
        String middlename = "";
        String lastname = "";
        String email = "";
        Connection conn = DBConnectionManager.getConnection();
        if(conn != null){
            ResultSet rs = null;
            Statement stmt = null;
            try {
                // Get the data of that user from database
                String sqlQuery = "SELECT * FROM user WHERE uid = '" + username + "'";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sqlQuery);
                if (rs.next()) {
                    firstname = rs.getString("firstname");
                    middlename = rs.getString("middlename");
                    lastname = rs.getString("lastname");
                    email = rs.getString("email");
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
        
        //Display the resume 
        request.setAttribute("firstnameContent", firstname);
        request.setAttribute("middlenameContent", middlename);
        request.setAttribute("lastnameContent", lastname);
        request.setAttribute("emailContent", email);
        request.getRequestDispatcher("viewProfile.jsp").forward(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
    
}

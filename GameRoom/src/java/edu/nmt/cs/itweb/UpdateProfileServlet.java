/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.io.IOException;
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
public class UpdateProfileServlet extends HttpServlet{
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            response.setContentType("text/html");
            // Get the username from session
            HttpSession session =  request.getSession();
            String username = (String)session.getAttribute("LOGIN_USER");
            
            // Update the profile
            String firstname = request.getParameter("firstname");
            String middlename = request.getParameter("middlename");
            if(middlename == null){
                middlename = "";
            }
            String lastname = request.getParameter("lastname");
            String email = request.getParameter("email");
            
            Connection conn = DBConnectionManager.getConnection();
            if (conn != null) {
                Statement stmt = null;
                try {
                    stmt = conn.createStatement();
                    stmt.executeUpdate("UPDATE user SET firstname='" + 
                                        firstname + "',middlename='" + 
                                        middlename + "',lastname='" +
                                        lastname+"',email='"+ email +"' where uid='" + username + "'");
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
           
            response.sendRedirect("changeProfileSuccess.jsp");
    }
    
     public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

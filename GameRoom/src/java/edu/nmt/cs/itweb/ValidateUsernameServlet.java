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

/**
 *
 * @author huipingyao
 */
public class ValidateUsernameServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setContentType("text/xml");
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);
        
        // Get username value
        String username = req.getParameter("username");
              
        Connection conn = DBConnectionManager.getConnection();
        if(conn != null){
            ResultSet rs = null;
            Statement stmt = null;
            try {
                // Get the data of that user from database
                String sqlQuery = "SELECT * FROM USER WHERE uid = '" + username + "'";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sqlQuery);
                if (rs.next()) {
                    resp.getWriter().write("<msg>true</msg>");
                } else {
                    resp.getWriter().write("<msg>false</msg>");
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
    }
}

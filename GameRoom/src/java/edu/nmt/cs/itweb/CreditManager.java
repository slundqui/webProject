/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author huipingyao
 */
public class CreditManager {
    /**
     * Query the winNum and loseNum of the given user
     * @param username: uid 
     * @return an arrayList with two elements, the first element means winNum, the second means loseNum
     */
    public static ArrayList<Integer> queryWinCredit(String username){
        String winNumStr = "";
        String loseNumStr = "";
        Connection conn = DBConnectionManager.getConnection();
        if(conn != null){
            ResultSet rs = null;
            Statement stmt = null;
            try {
                // Get the data of that user from database
                String sqlQuery = "SELECT * FROM credit WHERE uid = '" + username + "'";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sqlQuery);
                if (rs.next()) {
                    winNumStr = rs.getString("winNum");
                    loseNumStr = rs.getString("loseNum");
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
        
        // Return the winNum and loseNum of the given user
        ArrayList<Integer> res = new ArrayList<Integer>(2);
        res.set(0, Integer.parseInt(winNumStr));
        res.set(1, Integer.parseInt(loseNumStr));
        return res;
    }
    
    /**
     * Update the winNum/loseNum of the two players
     * @param userA: player A
     * @param doAWin: if A win, the value is true; else the value is false
     * @param userB: player A
     */
    public static void updateCredit(String userA, boolean doAWin, String userB){
        String winSql = "";
        String loseSql = "";
        if(doAWin == true){
            winSql = "UPDATE credit SET winNum=winNum+1 where uid='"+userA+"'";
            loseSql = "UPDATE credit SET loseNum=loseNum+1 where uid='"+userB+"'";
        }else{
            winSql = "UPDATE credit SET winNum=winNum+1 where uid='"+userB+"'";
            loseSql = "UPDATE credit SET loseNum=loseNum+1 where uid='"+userA+"'";
        }
        
        //Execute winSql
        Connection conn = DBConnectionManager.getConnection();
        if(conn != null){
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate(winSql);
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
        
        //Execute loseSql
        conn = DBConnectionManager.getConnection();
        if(conn != null){
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate(loseSql);
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

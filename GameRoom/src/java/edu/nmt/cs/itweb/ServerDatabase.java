/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

/**
 * Temporary Database Simulation
 * @author Chen
 */
public class ServerDatabase {
    /**
     * 
     * @param username
     * @param password
     * @return uid if authentication succeed, or -1 if failed.
     */
    public static long getUserAuthentication(String username, String password) {
        //To-do: connect database to check if (username, password) exists
        if( username.equals("chen") && password.equals("123")){
            return 1;
        }
        if( username.equals("hyao") && password.equals("123")) {
            return 2;
        }
        if( username.equals("slundqui") && password.equals("123")) {
            return 3;
        }
        return -1;
    }
}

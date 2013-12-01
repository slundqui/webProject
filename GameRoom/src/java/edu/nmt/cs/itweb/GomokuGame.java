/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

/**
 *
 * @author Chen
 */
public class GomokuGame {
    private int[][] board = new int[15][15];
    
    public void putStone(int x, int y, int stone){
        board[x][y] = stone;
    }
    
}

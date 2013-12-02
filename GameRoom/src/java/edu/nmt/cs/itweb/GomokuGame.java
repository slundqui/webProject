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

    static final int NO_STONE = 0;
    static final int BLACK = 1;
    static final int WHITE = 2;
    
    static final int NOT_FINISHED = 0;
    static final int BLACK_WINS = 1;
    static final int WHITE_WINS = 2;
    static final int TIE = 3;

    private final int[][] board = new int[15][15];
    private final boolean[][][] whiteTable = new boolean[15][15][572];
    private final boolean[][][] blackTable = new boolean[15][15][572];
    private final int[] whiteWins = new int[572];
    private final int[] blackWins = new int[572];
    private int moves = 0;

    public GomokuGame() {
        int x, y, k;
        int count = 0;
        for (x = 0; x < 15; x++) {
            for (y = 0; y < 15; y++) {
                for (k = 0; k < 572; k++) {
                    whiteTable[x][y][k] = false;
                    blackTable[x][y][k] = false;
                }
            }
        }
        for (y = 0; y < 15; y++) {
            for (x = 0; x < 11; x++) {
                for (k = 0; k < 5; k++) {
                    whiteTable[x + k][y][count] = true;
                    blackTable[x + k][y][count] = true;
                }
                count++;
            }
        }
        for (x = 0; x < 15; x++) {
            for (y = 0; y < 11; y++) {
                for (k = 0; k < 5; k++) {
                    whiteTable[x][y + k][count] = true;
                    blackTable[x][y + k][count] = true;
                }
                count++;
            }
        }
        for (y = 0; y < 11; y++) {
            for (x = 0; x < 11; x++) {
                for (k = 0; k < 5; k++) {
                    whiteTable[x + k][y + k][count] = true;
                    blackTable[x + k][y + k][count] = true;
                }
                count++;
            }
        }
        for (y = 0; y < 11; y++) {
            for (x = 14; x >= 4; x--) {
                for (k = 0; k < 5; k++) {
                    whiteTable[x - k][y + k][count] = true;
                    blackTable[x - k][y + k][count] = true;
                }
                count++;
            }
        }
    }

    public int isGameOver() {
        for (int i = 0; i < 572; i++) {
            if (whiteWins[i] == 5) {
                return WHITE_WINS;
            }
            if (blackWins[i] == 5) {
                return BLACK_WINS;
            }
        }
        if (moves == 225) {
            return TIE;
        }
        return NOT_FINISHED;
    }
        
    public boolean putStone(int x, int y, int stone) {
        if(board[x][y] != NO_STONE) {
            return false;
        }
        board[x][y] = stone;
        moves++;
        
        if (stone == BLACK) {
            for (int i = 0; i < 572; i++) {
                if (blackTable[x][y][i] && blackWins[i] < 10) {
                    blackWins[i]++;
                }
                if (whiteTable[x][y][i]) {
                    whiteTable[x][y][i] = false;
                    whiteWins[i] += 10;
                }
            }
        }
        else if(stone == WHITE) {
            for (int i = 0; i < 572; i++) {
                if (whiteTable[x][y][i] && whiteWins[i] < 10) {
                    whiteWins[i]++;

                }
                if (blackTable[x][y][i]) {
                    blackTable[x][y][i] = false;
                    blackWins[i] += 10;
                }
            }
        }
        return true;
    }

}

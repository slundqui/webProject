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
public class WebSocketMessage {
    public static final int ENTER_LOBBY         = 101;
    public static final int EXIT_LOBBY          = 102;
    
    public static final int REQUEST_SEATS_INFO  = 201;
    public static final int RESPONSE_SEATS_INFO = 202;
    
    public static final int TAKE_SEAT_REQUEST   = 301;
    public static final int TAKE_SEAT_SUCCESS   = 302;
    public static final int TAKE_SEAT_FAIL      = 303;
    public static final int LEAVE_SEAT_REQUEST  = 304;
    public static final int LEAVE_SEAT_SUCCESS  = 305;
    
    public static final int READY_FOR_GAME      = 401;
    public static final int UNREADY_FOR_GAME    = 402;
    
    public static final int GAME_START_REQUEST  = 501;
    public static final int GAME_START_SUCCESS  = 502;
    public static final int GAME_END            = 503;
    
    public static final int ENTER_GAME          = 510;
    
    public static final int GOMOKU_ENTER_GAME       = 600;
    public static final int GOMOKU_PLAYERS          = 601;
    public static final int GOMOKU_GAME_READY       = 602;
    public static final int GOMOKU_GAME_OVER        = 603;
    public static final int GOMOKU_PUT_STONE        = 610;
    public static final int GOMOKU_PUT_STONE_REJECT = 611;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Chen
 */
@ServerEndpoint(value="/GameServer", configurator = GetHttpSessionConfigurator.class)
public class GameServer {
    //private static final Set<Session> peers = Collections.synchronizedSet(new HashSet());
    private static final Set<Session>[] peers = new Set[ServerConfig.TABLE_COUNT];
    private static final GomokuGame[] games = new GomokuGame[ServerConfig.TABLE_COUNT];
    
    static 
    { 
        for(int i = 0; i < ServerConfig.TABLE_COUNT; i++) {
            peers[i] = Collections.synchronizedSet(new HashSet());
            games[i] = new GomokuGame();
        }
    }
    
    @OnOpen
    public void open(Session session, EndpointConfig conf) throws IOException { 
        //Connection opened.
        System.out.println("EchoEndpoint on open");
        boolean authorized = false;
        HttpSession httpSession = (HttpSession) conf.getUserProperties().get(HttpSession.class.getName());
        String seat = (String)conf.getUserProperties().get("SeatIndex");
        if(seat != null) {
            int seatIndex = Integer.parseInt(seat) - 1;
            String user = (String)httpSession.getAttribute("LOGIN_USER");
            if(user.equals(WebSocketServer.inGame[seatIndex])) {
                authorized = true;
                peers[getTableIndex(seatIndex)].add(session);
            }
        }
        if(!authorized) {
            session.close();
        }
    }
    
    @OnMessage
    public void onMessage(Session session, String msg) {
        try {
            JsonParser parser = Json.createParser(new StringReader(msg));
            parser.next();
            parser.next();
            parser.next();
            int action = parser.getInt();
            parser.next();
            parser.next();
            int seatIndex = parser.getInt() - 1;
            int tableIndex = getTableIndex(seatIndex);
            parser.next();
            parser.next();
            int x = parser.getInt();
            parser.next();
            parser.next();
            int y = parser.getInt();
            switch (action) {
                case WebSocketMessage.GOMOKU_ENTER_GAME:
                    sendSeatsUser(session, seatIndex);
                    if(peers[tableIndex].size() == 2) {
                        broadcastGameReady(tableIndex);
                    }
                    break;
                case WebSocketMessage.GOMOKU_PUT_STONE:
                    int color = getSeatColor(seatIndex);
                    if(games[tableIndex].putStone(x, y, color)){
                        int result = games[tableIndex].isGameOver();
                        if(GomokuGame.NOT_FINISHED == result){
                            broadcastMessage(tableIndex, msg);
                        }
                        else {
                            broadcastGameOver(seatIndex, tableIndex, result, x, y);
                        }
                    }
                    else {
                        sendPutStoneRejection(session);
                    }
                    break;
                default:
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @OnError
    public void error(Session session, Throwable error) { 
        //Connection error.
        System.out.println("EchoEndpoint on error");
    }

    @OnClose
    public void close(Session session, CloseReason reason) { 
        //Connection closed.
        System.out.println("EchoEndpoint on close");
        for(int i = 0; i < ServerConfig.TABLE_COUNT; i++) {
            if(peers[i].contains(session)){
                peers[i].remove(session);
                break;
            }
        }
    }
    
    private void broadcastMessage(int tableIndex, String msg) {
        for (Session peer : peers[tableIndex]) {
            sendMessage(peer, msg);
        }
    }
    
    private void sendMessage(Session session, String msg){
        try {
            session.getBasicRemote().sendObject(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private int getTableIndex(int seatIndex){
        if(seatIndex % 2 == 0)
            return seatIndex/2;
        else
            return (seatIndex-1)/2;
    }
    
    private int getSeatColor(int seatIndex) { 
        return seatIndex % 2 + 1; // left_seat:black:1,  right_seat:white:2
    }
    
    private String getLeftUser(int seatIndex) {
        if(seatIndex % 2 == 0){
            return WebSocketServer.seatsUser[seatIndex];
        }
        else {
            return WebSocketServer.seatsUser[seatIndex-1];
        }
    }
    
    private String getRightUser(int seatIndex) {
        if(seatIndex % 2 == 0){
            return WebSocketServer.seatsUser[seatIndex+1];
        }
        else {
            return WebSocketServer.seatsUser[seatIndex];
        }
    }
    
    private void sendSeatsUser(Session session, int seatIndex) {
        String leftUser = getLeftUser(seatIndex);
        String rightUser = getRightUser(seatIndex);
        JsonObject jobj = Json.createObjectBuilder()
                .add("action", WebSocketMessage.GOMOKU_PLAYERS)
                .add("left", leftUser)
                .add("right", rightUser)
                .build();
        String msg = jobj.toString();
        sendMessage(session, msg);
    }
    
    private void sendPutStoneRejection(Session session) {
        JsonObject jobj = Json.createObjectBuilder()
                .add("action", WebSocketMessage.GOMOKU_PUT_STONE_REJECT)
                .build();
        String msg = jobj.toString();
        sendMessage(session, msg);
    }
    
    private void broadcastGameReady(int tableIndex) {
        JsonObject jobj = Json.createObjectBuilder()
                .add("action", WebSocketMessage.GOMOKU_GAME_READY)
                .build();
        String msg = jobj.toString();
        broadcastMessage(tableIndex, msg);
    }
    
    private void broadcastGameOver(int seatIndex, int tableIndex, int result, int x, int y) {
        String winner = null;
        switch(result) 
        {
            case GomokuGame.TIE: // TIE = who plays first lose
            case GomokuGame.WHITE_WINS:
                winner = getRightUser(seatIndex);
                break;
            case GomokuGame.BLACK_WINS:
                winner = getLeftUser(seatIndex);
                break;
            default:
        }
        if(winner != null){
            JsonObject jobj = Json.createObjectBuilder()
                    .add("action", WebSocketMessage.GOMOKU_GAME_OVER)
                    .add("winner", winner)
                    .add("x", x)
                    .add("y", y)
                    .add("seat", seatIndex+1)
                    .build();
            String msg = jobj.toString();
            broadcastMessage(tableIndex, msg);
        }
    }
}

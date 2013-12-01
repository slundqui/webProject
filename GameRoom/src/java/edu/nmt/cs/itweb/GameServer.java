/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.json.Json;
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
            int seatIndex = Integer.parseInt(seat);
            String user = (String)httpSession.getAttribute("LOGIN_USER");
            if(user.equals(WebSocketServer.inGame[seatIndex-1])) {
                authorized = true;
                peers[getTableIndex(seatIndex-1)].add(session);
            }
        }
        if(!authorized) {
            session.close();
        }
    }
    
    @OnMessage
    public void onMessage(Session session, String msg) throws IOException, EncodeException {
        JsonParser parser = Json.createParser(new StringReader(msg));
        parser.next();
        parser.next();
        parser.next();
        int action = parser.getInt();
        parser.next();
        parser.next();
        int seatIndex = parser.getInt();
        parser.next();
        parser.next();
        int x = parser.getInt();
        parser.next();
        parser.next();
        int y = parser.getInt();
        switch (action) {
            case WebSocketMessage.GOMOKU_PUT_STONE:
                int tableIndex = getTableIndex(seatIndex - 1);
                games[tableIndex].putStone(x, y, getSeatColor(seatIndex));
                broadcastMessage(tableIndex, msg);
                break;
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
    
    private void broadcastMessage(int tableIndex, String msg) throws IOException, EncodeException {
        for (Session peer : peers[tableIndex]) {
            sendMessage(peer, msg);
        }
    }
    
    private void sendMessage(Session session, String msg) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(msg);
    }
    
    private int getTableIndex(int seatIndex){
        if(seatIndex % 2 == 0)
            return seatIndex/2;
        else
            return (seatIndex-1)/2;
    }
    
    private int getSeatColor(int seatIndex) {
        return seatIndex % 2 + 1;
    }
}

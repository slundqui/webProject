/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.stream.JsonParser;
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
@ServerEndpoint(value="/GameLobby")
public class WebSocketServer {
    
    private static final Set<Session> peers = Collections.synchronizedSet(new HashSet());
    static String[] seatsUser = new String[ServerConfig.SEAT_COUNT];
    private static boolean[] seatsStatus = new boolean[ServerConfig.SEAT_COUNT];
    static String[] inGame = new String[ServerConfig.SEAT_COUNT]; 

    @OnOpen
    public void open(Session session, EndpointConfig conf) { 
        //Connection opened.
        System.out.println("EchoEndpoint on open");
        peers.add(session);
    }

    @OnMessage
    public void onMessage(Session session, String msg) throws IOException, EncodeException {
        //Message received.
        System.out.println("EchoEndpoint on message");
        JsonParser parser = Json.createParser(new StringReader(msg));
        parser.next();
        parser.next();
        parser.next();
        int action = parser.getInt();
        parser.next();
        parser.next();
        String user = parser.getString();
        parser.next();
        parser.next();
        int targetIndex = parser.getInt();
        switch(action) {
            case WebSocketMessage.REQUEST_SEATS_INFO:
                sendSeatsStatus(session);
                break;
            case WebSocketMessage.TAKE_SEAT_REQUEST:
            case WebSocketMessage.LEAVE_SEAT_REQUEST:
            case WebSocketMessage.READY_FOR_GAME:
            case WebSocketMessage.UNREADY_FOR_GAME:
                updateSeatStatus(session, action, user, targetIndex, msg);
                break;
            case WebSocketMessage.GAME_START_REQUEST:
                sendUserToGame(session, user, targetIndex, msg);
                break;
            default:
                broadcastMessage(msg);
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
        peers.remove(session);
        
    }
    
    private void sendMessage(Session session, String msg) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(msg);
    }
    
    private void broadcastMessage(String msg) throws IOException, EncodeException {
        for (Session peer : peers) {
            sendMessage(peer, msg);
        }
    }
    
    private String makeReplyMessage(String msg, int replyAction){
        return msg.substring(0, 10) + replyAction + msg.substring(13);
    }

    private synchronized void updateSeatStatus(Session session, int action, String user, int seatIndex, String msg) throws IOException, EncodeException {
        switch (action) {
            case WebSocketMessage.TAKE_SEAT_REQUEST:
                if (seatsUser[seatIndex - 1] == null) {
                    seatsUser[seatIndex - 1] = user;
                    broadcastMessage(makeReplyMessage(msg, WebSocketMessage.TAKE_SEAT_SUCCESS));
                } else {
                    sendMessage(session, makeReplyMessage(msg, WebSocketMessage.TAKE_SEAT_FAIL));
                }
                break;
            case WebSocketMessage.LEAVE_SEAT_REQUEST:
                seatsUser[seatIndex - 1] = null;
                seatsStatus[seatIndex - 1] = false;
                broadcastMessage(makeReplyMessage(msg, WebSocketMessage.LEAVE_SEAT_SUCCESS));
                break;
            case WebSocketMessage.READY_FOR_GAME:
                seatsStatus[seatIndex - 1] = true;
                broadcastMessage(msg);
                break;
            case WebSocketMessage.UNREADY_FOR_GAME:
                seatsStatus[seatIndex - 1] = false;
                broadcastMessage(msg);
                break;
        }
    }
    
    private void sendSeatsStatus(Session session) throws IOException, EncodeException  {
        JsonArrayBuilder jusrBld = Json.createArrayBuilder();
        JsonArrayBuilder jstsBld = Json.createArrayBuilder();
        for(int i = 0; i < ServerConfig.SEAT_COUNT; ++i) {
            jusrBld.add(seatsUser[i] != null ? seatsUser[i] : "");
            jstsBld.add(seatsStatus[i]);
        }
        JsonObject jobj = Json.createObjectBuilder()
                .add("action", WebSocketMessage.RESPONSE_SEATS_INFO)
                .add("seatsUser", jusrBld.build())
                .add("seatsStatus", jstsBld.build())
                .build();
        String msg = jobj.toString();
        sendMessage(session, msg);
    }
    
    private void sendUserToGame(Session session, String user, int seatIndex, String msg)  throws IOException, EncodeException  {
        //TO-DO: set session attribute or sth to authorize the user entering a certain game.
        inGame[seatIndex - 1] = user;
        sendMessage(session, makeReplyMessage(msg, WebSocketMessage.GAME_START_SUCCESS));
    }
}
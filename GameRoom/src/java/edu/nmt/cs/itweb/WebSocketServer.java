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
        broadcastMessage(msg);
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
    
    private void broadcastMessage(String msg) throws IOException, EncodeException {
        for (Session peer : peers) {
            peer.getBasicRemote().sendObject(msg);
        }
    }
}
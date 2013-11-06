<%-- 
    Document   : lobby
    Created on : Oct 31, 2013, 11:44:10 PM
    Author     : Chen
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="edu.nmt.cs.itweb.WebSocketMessage"%>
<%
    String username = (String)session.getAttribute("LOGIN_USER");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Game Lobby</title>
        <style>
            html, body, p {margin: 0; padding: 0;}
            #page-container {width: 1000px; height: 820px; margin: auto; padding: 0;}
            #topbar {text-align:right; height: 20px;}
            #header {background: #9F9; width: auto; height: 50px; text-align: center;}
            div {text-align: left;}
            #userList {background: #FF6; float: left;  width: 140px; height: 540px; padding: 5px; overflow: auto; overflow-x: hidden;}
            #lobbyContainer {background: #9FF; float: right; width: 840px; height:540px; padding: 5px; }
            #logPanel {background: #333; color: #FFF; clear: both; width: auto; height: 140px; padding: 5px; overflow: auto; overflow-x: hidden;}
            table {margin: auto;border-collapse:separate; border-spacing:50px 70px;}
            input[type="image"] { outline: none;}
        </style>
        <script language="javascript" type="text/javascript">  
            var wsUri = "ws://localhost:8080/GameRoom/GameLobby";
            var userList;
            var logPanel;
            
            var imageSrc = new Array(
                    "img/right_chair.png",
                    "img/left_chair.png",
                    "img/right_chair_with_player.png",
                    "img/left_chair_with_player.png"
            );
            
            var seatStatus = new Array(8);
            
            function init() {
                userList = document.getElementById("userList");
                logPanel = document.getElementById("logPanel");
                startWebSocket();
            }
            
            function startWebSocket() {
                websocket = new WebSocket(wsUri);
                websocket.onopen = function(evt) { onOpen(evt) };
                websocket.onclose = function(evt) { onClose(evt) };
                websocket.onmessage = function(evt) { onMessage(evt) };
                websocket.onerror = function(evt) { onError(evt) };
            }

            function onOpen(evt) {
                doSend(<%=WebSocketMessage.ENTER_LOBBY%>, "<%=username%>");
            }

            function onClose(evt) {
            }

            function onMessage(evt) {
                parseMessage(evt.data);
            }

            function onError(evt) { 
            } 

            function doSend(action, param) {
                websocket.send(JSON.stringify({'action':action, 'param':param}));
            }
            
            function parseMessage(message) {
                var msg = JSON.parse(message);
                switch(msg.action)
                {
                    case <%=WebSocketMessage.ENTER_LOBBY%>:
                        writeToLogPanel("<span style='color:red'>"+msg.param+"</span> has entered the lobby.");
                        break;
                    case <%=WebSocketMessage.EXIT_LOBBY%>:
                        writeToLogPanel("<span style='color:red'>"+msg.param+"</span> has left the lobby.");
                        break;
                    default:
                }
            }

            function writeToLogPanel(message) {
                var pre = document.createElement("p");
                pre.style.wordWrap = "break-word";
                pre.innerHTML = getTimeString() + message;
                logPanel.appendChild(pre);
                logPanel.scrollTop = logPanel.scrollHeight;
            }
            
            function getTimeString() {
                var currentTime = new Date();
                return currentTime.toLocaleTimeString() + " - ";
            }
            
            function logout() {
                doSend(<%=WebSocketMessage.EXIT_LOBBY%>, "<%=username%>");
                websocket.close();
                window.location.replace("logout.jsp");
            }
            
            function seatAt(id) {
                if(seatStatus[id]) {
                    document.getElementById(id).src=imageSrc[parseInt(id.charAt(4)) % 2];
                    seatStatus[id] = false;
                }
                else {
                    document.getElementById(id).src=imageSrc[parseInt(id.charAt(4)) % 2 + 2];
                    seatStatus[id] = true;
                }
            }
            window.addEventListener("load", init, false);
        </script>
    </head>
    <body>
        <div id="page-container">
            <div id="topbar"><a href="#" onclick="logout(); return false;">Logout</a></div>
            <div id="header">Welcome to the Game Lobby!</p>
            <div id="userList">[To-do:list online users]</div>
            <div id="lobbyContainer">
                <table>
                    <tr>
                        <td>
                            <input type="image" id="seat1" src="img/left_chair.png" onclick="seatAt(this.id);" />
                            <input type="image" id="table1" src="img/table.png"/>
                            <input type="image" id="seat2" src="img/right_chair.png" onclick="seatAt(this.id);" />
                        </td>
                        <td>
                            <input type="image" id="seat3" src="img/left_chair.png" onclick="seatAt(this.id);" />
                            <input type="image" id="table2" src="img/table.png"/>
                            <input type="image" id="seat4" src="img/right_chair.png" onclick="seatAt(this.id);" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <input type="image" id="seat5" src="img/left_chair.png" onclick="seatAt(this.id);" />
                            <input type="image" id="table3" src="img/table.png"/>
                            <input type="image" id="seat6" src="img/right_chair.png" onclick="seatAt(this.id);" />
                        </td>
                        <td>
                            <input type="image" id="seat7" src="img/left_chair.png" onclick="seatAt(this.id);" />
                            <input type="image" id="table4" src="img/table.png"/>
                            <input type="image" id="seat8" src="img/right_chair.png" onclick="seatAt(this.id);" />
                        </td>
                    </tr>
                </table>
            </div>
            <div id="logPanel"></div>
            </div>
        </div>
    </body>
</html>

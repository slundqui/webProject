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
            #lobbyTable {margin: auto;border-collapse:separate; border-spacing:50px 70px;}
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
            var mySeat = null;
            
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
                doSend(<%=WebSocketMessage.ENTER_LOBBY%>, "<%=username%>", null);
            }

            function onClose(evt) {
            }

            function onMessage(evt) {
                parseMessage(evt.data);
            }

            function onError(evt) { 
            } 

            function doSend(action, param1, param2) {
                if(param2 === null){
                    websocket.send(JSON.stringify({'action':action, 'param1':param1}));
                }
                else{
                    websocket.send(JSON.stringify({'action':action, 'param1':param1, 'param2':param2}));
                }
            }

            function sitUpdate(id){
                document.getElementById(id).src=imageSrc[parseInt(id.charAt(4)) % 2 + 2];
                seatStatus[id] = true;
            }

            function standUpdate(id){
                document.getElementById(id).src=imageSrc[parseInt(id.charAt(4)) % 2];
                seatStatus[id] = false;
            }
            
            function parseMessage(message) {
                var msg = JSON.parse(message);
                switch(msg.action)
                {
                    case <%=WebSocketMessage.ENTER_LOBBY%>:
                        writeToLogPanel("<span style='color:red'>"+msg.param1+"</span> has entered the lobby.");
                        break;
                    case <%=WebSocketMessage.EXIT_LOBBY%>:
                        writeToLogPanel("<span style='color:red'>"+msg.param1+"</span> has left the lobby.");
                        break;
                    case <%=WebSocketMessage.TAKE_SEAT%>:
                        writeToLogPanel("<span style='color:red'>"+msg.param1+"</span> has taken seat </span>" + msg.param2.charAt(4)+".");
                        sitUpdate(msg.param2);
                        break;
                    case <%=WebSocketMessage.LEAVE_SEAT%>:
                        writeToLogPanel("<span style='color:red'>"+msg.param1+"</span> has left seat </span>" + msg.param2.charAt(4)+".");
                        standUpdate(msg.param2);
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
                doSend(<%=WebSocketMessage.EXIT_LOBBY%>, "<%=username%>", null);
                websocket.close();
                window.location.replace("logout.jsp");
            }


            function takeSeatAt(id) {
                //If in the seat and you're in the seat
                if(seatStatus[id] && mySeat === id) {
                    document.getElementById(id).src=imageSrc[parseInt(id.charAt(4)) % 2];
                    seatStatus[id] = false;
                    mySeat = null;
                    doSend(<%=WebSocketMessage.LEAVE_SEAT%>, "<%=username%>", id);
                }
                else if(seatStatus[id] && mySeat !== id){
                    alert("Seat is taken");
                    return;
                }
                //If not in the seat
                else {
                    if(mySeat !== null){
                        alert("You have already sitten at " + mySeat);
                        return;
                    }
                    document.getElementById(id).src=imageSrc[parseInt(id.charAt(4)) % 2 + 2];
                    seatStatus[id] = true;
                    mySeat = id;
                    doSend(<%=WebSocketMessage.TAKE_SEAT%>, "<%=username%>", id);
                }
            }

            function startTable(id){
                //mySeat must match the table
                if(
                   ((mySeat === "seat1" || mySeat === "seat2") && id === "table1") ||
                   ((mySeat === "seat3" || mySeat === "seat4") && id === "table2") ||
                   ((mySeat === "seat5" || mySeat === "seat6") && id === "table3") ||
                   ((mySeat === "seat7" || mySeat === "seat8") && id === "table4")
                ){
                    alert("Start");
                }
                else{
                    alert("You must be sitting at the table to start");
                }
            }
            window.addEventListener("load", init, false);
        </script>
    </head>
    <body>
        <div id="page-container">
            <div id="topbar"><a href="#" onclick="logout(); return false;">Logout</a></div>
            <div id="header">Welcome to the Game Lobby!</div>
            <div id="userList">[To-do:list online users]</div>
            <div id="lobbyContainer">
                <table id="lobbyTable">
                    <tr>
                        <td>
                            <input type="image" id="seat1" src="img/left_chair.png" onclick="takeSeatAt(this.id);" />
                            <input type="image" id="table1" src="img/table.png" onclick="startTable(this.id);"/>
                            <input type="image" id="seat2" src="img/right_chair.png" onclick="takeSeatAt(this.id);" />
                        </td>
                        <td>
                            <input type="image" id="seat3" src="img/left_chair.png" onclick="takeSeatAt(this.id);" />
                            <input type="image" id="table2" src="img/table.png" onclick="startTable(this.id);"/>
                            <input type="image" id="seat4" src="img/right_chair.png" onclick="takeSeatAt(this.id);" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <input type="image" id="seat5" src="img/left_chair.png" onclick="takeSeatAt(this.id);" />
                            <input type="image" id="table3" src="img/table.png" onclick="startTable(this.id);"/>
                            <input type="image" id="seat6" src="img/right_chair.png" onclick="takeSeatAt(this.id);" />
                        </td>
                        <td>
                            <input type="image" id="seat7" src="img/left_chair.png" onclick="takeSeatAt(this.id);" />
                            <input type="image" id="table4" src="img/table.png" onclick="startTable(this.id);"/>
                            <input type="image" id="seat8" src="img/right_chair.png" onclick="takeSeatAt(this.id);" />
                        </td>
                    </tr>
                </table>
            </div>
            <div id="logPanel"></div>
            </div>
        </div>
    </body>
</html>

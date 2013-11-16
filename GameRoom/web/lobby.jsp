<%-- 
    Document   : lobby
    Created on : Oct 31, 2013, 11:44:10 PM
    Author     : Chen
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="edu.nmt.cs.itweb.WebSocketMessage"%>
<%@page import="edu.nmt.cs.itweb.ServerConfig"%>
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
            #logPanel {background: #333; color: #FFF; clear: both; width: auto; height: 130px; padding: 5px; overflow: auto; overflow-x: hidden;}
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
            
            var seatCount = <%=ServerConfig.SEAT_COUNT%>;
            var seats = new Array(seatCount);
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
                doSend(<%=WebSocketMessage.ENTER_LOBBY%>, "<%=username%>");
                doSend(<%=WebSocketMessage.REQUEST_SEATS_INFO%>, "<%=username%>");
            }

            function onClose(evt) {
            }

            function onMessage(evt) {
                var msg = JSON.parse(evt.data);
                switch(msg.action)
                {
                    case <%=WebSocketMessage.ENTER_LOBBY%>:
                        enterLobby(msg.user);
                        break;
                    case <%=WebSocketMessage.EXIT_LOBBY%>:
                        exitLobby(user);
                        break;
                    case <%=WebSocketMessage.RESPONSE_SEATS_INFO%>:
                        updateSeatsInfo(msg.seatsInfo);
                        break;
                    case <%=WebSocketMessage.TAKE_SEAT_SUCCESS%>:
                        sitUpdate(msg.user, msg.target);
                        break;
                    case <%=WebSocketMessage.LEAVE_SEAT_SUCCESS%>:
                        standUpdate(msg.user, msg.target);
                        break;
                    default:
                }
            }

            function onError(evt) { 
            } 

            function doSend(action, user, target) {
                target = typeof target !== 'undefined' ? target : 0;
                websocket.send(JSON.stringify({'action':action, 'user':user, 'target':target}));
            }
            
            function enterLobby(user) {
                writeToLogPanel("<span style='color:red'>"+user+"</span> has entered the lobby.");
            }
            
            function exitLobby(user) {
                writeToLogPanel("<span style='color:red'>"+user+"</span> has left the lobby.");
            }
            
            function updateSeatsInfo(jSeats) {
                for(var i = 0; i < seatCount; ++i) {
                    var seatIndex = i+1;
                    var seatId = "seat"+seatIndex;
                    if(jSeats[i].length === 0) {
                        seats[seatId] = null;
                    }
                    else {
                        seats[seatId] = jSeats[i];
                        document.getElementById(seatId).src=imageSrc[seatIndex % 2 + 2];
                    }
                }
            }

            function sitUpdate(user, seatIndex){
                var seatId = 'seat'+seatIndex;
                document.getElementById(seatId).src=imageSrc[seatIndex % 2 + 2];
                seats[seatId] = user;
                if("<%=username%>" === user) {
                    mySeat = seatId;
                }
                writeToLogPanel("<span style='color:red'>"+user +"</span> has taken seat </span>" + seatIndex +".");
            }

            function standUpdate(user, seatIndex){
                var seatId = 'seat'+seatIndex;
                document.getElementById(seatId).src=imageSrc[seatIndex % 2];
                seats[seatId] = null;
                writeToLogPanel("<span style='color:red'>"+user+"</span> has left seat </span>" + seatIndex +".");
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

            function seatOnClick(seatId) {
                var seatIndex = getSeatIndex(seatId);
                //If in the seat and you're in the seat
                if(seats[seatId] && mySeat === seatId) {
                    // since LEAVE_SEAT_REQUEST will always success, we can update the UI and seats before sending the message.
                    document.getElementById(seatId).src=imageSrc[seatIndex % 2];
                    seats[seatId] = null;
                    mySeat = null;
                    doSend(<%=WebSocketMessage.LEAVE_SEAT_REQUEST%>, "<%=username%>", seatIndex);
                }
                else if(seats[seatId] && mySeat !== seatId){
                    alert("Seat is taken");
                    return;
                }
                //If not in the seat
                else {
                    if(mySeat !== null){
                        alert("You have already sitten at " + mySeat);
                        return;
                    }
                    doSend(<%=WebSocketMessage.TAKE_SEAT_REQUEST%>, "<%=username%>", seatIndex);
                }
            }
            
            function getSeatIndex(seatId) {
                return parseInt(seatId.charAt(4));
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
                            <input type="image" id="seat1" src="img/left_chair.png" onclick="seatOnClick(this.id);" />
                            <input type="image" id="table1" src="img/table.png" onclick="startTable(this.id);"/>
                            <input type="image" id="seat2" src="img/right_chair.png" onclick="seatOnClick(this.id);" />
                        </td>
                        <td>
                            <input type="image" id="seat3" src="img/left_chair.png" onclick="seatOnClick(this.id);" />
                            <input type="image" id="table2" src="img/table.png" onclick="startTable(this.id);"/>
                            <input type="image" id="seat4" src="img/right_chair.png" onclick="seatOnClick(this.id);" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <input type="image" id="seat5" src="img/left_chair.png" onclick="seatOnClick(this.id);" />
                            <input type="image" id="table3" src="img/table.png" onclick="startTable(this.id);"/>
                            <input type="image" id="seat6" src="img/right_chair.png" onclick="seatOnClick(this.id);" />
                        </td>
                        <td>
                            <input type="image" id="seat7" src="img/left_chair.png" onclick="seatOnClick(this.id);" />
                            <input type="image" id="table4" src="img/table.png" onclick="startTable(this.id);"/>
                            <input type="image" id="seat8" src="img/right_chair.png" onclick="seatOnClick(this.id);" />
                        </td>
                    </tr>
                </table>
            </div>
            <div id="logPanel"></div>
        </div>
    </body>
</html>

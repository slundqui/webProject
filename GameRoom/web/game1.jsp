<%-- 
    Document   : game1
    Created on : Nov 27, 2013, 1:00:43 AM
    Author     : Chen
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="edu.nmt.cs.itweb.WebSocketMessage"%>
<%
    String username = (String) session.getAttribute("LOGIN_USER");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Gomoku</title>
        <style>
            html, body, p {margin: 0; padding: 0;}
            #page-container {width: 1000px; height: 820px; margin: auto; padding: 0;}
            canvas {border:1px solid #c3c3c3;}
            #left_user, #right_user {font-weight:bold; font-size:2em; color:blue;}
            #left_turn, #right_turn {font-size:1.5em; color:green;}
        </style>
        <script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>

    </head>
    <body>
        <div id="page-container">
            <div id="waiting" align="center">Waiting for the other player ...</div>
            <table>
                <tr>
                    <td width="100">
                        <div id="left_user">User1</div>
                        <div id="left_turn">Your turn</div>
                        <input id="left_surrender" type="button" value="Surrender" onclick="gotoLobby();"/>
                    </td>
                    <td width="750">
                        <canvas id="canvas" width="750" height="750">Your browser does not support the HTML5 canvas tag.</canvas>
                    </td>
                    <td width="100">
                        <div id="right_user">User2</div>
                        <div id="right_turn">Your turn</div>
                        <input id="right_surrender" type="button" value="Surrender" onclick="gotoLobby();"/>
                    </td>
                </tr>
            </table>
        </div>
        <script>
            var canvas = document.getElementById("canvas");
            var ctx = canvas.getContext("2d");
            var canvasOffset = $("#canvas").offset();
            var offsetX = canvasOffset.left;
            var offsetY = canvasOffset.top;
            var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
            var scrollY = document.documentElement.scrollTop || document.body.scrollTop;

            var seatIndex = <%=request.getParameter("seat")%>;
            
            var reg = new RegExp("^[1-8]$"); // seat index should be from 1 to 8
            var wsUri;
            if (typeof seatIndex === "undefined" || !reg.test(seatIndex)) {
                wsUri = "ws://localhost:8080/GameRoom/GameServer";
            }
            else {
                wsUri = "ws://localhost:8080/GameRoom/GameServer?" + seatIndex;
            }
            
            var myTurn = false;
            $("#left_turn").css("visibility", "hidden");
            $("#right_turn").css("visibility", "hidden");
            
            var myColor =  getSeatColor(seatIndex);
            
            function updateUserUI(left, right) {
                $("#left_user").html(left);
                $("#right_user").html(right);
            }
            
            function updateTurnUI() {
                if(parseInt(seatIndex)%2 === 1) { //I'm the left one
                    if(myTurn) {
                        $("#left_turn").css("visibility", "visible");
                        $("#right_turn").css("visibility", "hidden");
                    }
                    else {
                        $("#left_turn").css("visibility", "hidden");
                        $("#right_turn").css("visibility", "visible");
                    }
                }
                else { //I'm the right one
                    if(myTurn) {
                        $("#left_turn").css("visibility", "hidden");
                        $("#right_turn").css("visibility", "visible");
                    }
                    else {
                        $("#left_turn").css("visibility", "visible");
                        $("#right_turn").css("visibility", "hidden");
                    }
                }
            }
            
            function getTurn (seat) {
                return (parseInt(seat)%2 === 0) ? false : true;
            }
            
            function getSeatColor (seat) {
                return (parseInt(seat)%2 === 0) ? "white":"black";
            }
            
            function init() {
                drawBoard();
                startWebSocket();
            }

            function drawBoard() {
                for (var i = 0; i < 15; i++) {
                    var grid = 50 * i + 25;
                    ctx.moveTo(25, grid);
                    ctx.lineTo(725, grid);
                    ctx.stroke();
                    ctx.moveTo(grid, 25);
                    ctx.lineTo(grid, 725);
                    ctx.stroke();
                }
            }

            function drawStone(x, y, color) {
                ctx.beginPath();
                ctx.arc(25+x*50, 25+y*50 ,23, 0, 2*Math.PI);
                ctx.stroke();
                ctx.fillStyle=color;
                ctx.fill();
            }           
            
            $("#canvas").mousedown(function(e) {
                if(myTurn) {
                    var rx = e.pageX || e.clientX + scrollX;
                    var ry = e.pageY || e.clientY + scrollY;
                    var x = rx - offsetX;
                    var y = ry - offsetY;
                    var nx = Math.floor((x)/50);
                    var ny = Math.floor((y)/50);
                    //fix the boundary
                    if(nx === 15){
                        nx = 14;
                    }
                    if(ny === 15){
                        ny = 14;
                    }
                    doSend(<%=WebSocketMessage.GOMOKU_PUT_STONE%>, seatIndex, nx, ny);
                    myTurn = false;
                    // delay turn UI updating
                }
                else {
                    alert("not your turn");
                }
            });

            function startWebSocket() {
                websocket = new WebSocket(wsUri);
                websocket.onopen = function(evt) {
                    onOpen(evt);
                };
                websocket.onclose = function(evt) {
                    onClose(evt);
                };
                websocket.onmessage = function(evt) {
                    onMessage(evt);
                };
                websocket.onerror = function(evt) {
                    onError(evt);
                };
            }

            function onOpen(evt) {
                doSend(<%=WebSocketMessage.GOMOKU_ENTER_GAME%>, seatIndex);
            }

            function onClose(evt) {
                alert("You are not in a game.");
                gotoLobby();
            }

            function onMessage(evt) {
                var msg = JSON.parse(evt.data);
                switch (msg.action)
                {
                    case <%=WebSocketMessage.GOMOKU_PLAYERS%>:
                        updateUserUI(msg.left, msg.right);
                        break;
                    case <%=WebSocketMessage.GOMOKU_GAME_READY%>:
                        $("#waiting").hide();
                        myTurn = getTurn(seatIndex);
                        updateTurnUI();
                        break;
                    case <%=WebSocketMessage.GOMOKU_PUT_STONE%>:
                        var color = getSeatColor(msg.seat);
                        drawStone(msg.x, msg.y, color);
                        myTurn = (color !== myColor);
                        updateTurnUI();
                        break;
                    case <%=WebSocketMessage.GOMOKU_PUT_STONE_REJECT%>:
                        myTurn = true;
                        //no need to update turn UI
                        break;    
                    case <%=WebSocketMessage.GOMOKU_GAME_OVER%>:
                        drawStone(msg.x, msg.y, getSeatColor(msg.seat));
                        alert(msg.winner + " wins");
                        gotoLobby();
                        break;
                    default:
                }
            }

            function onError(evt) {
            }

            function doSend(action, seat, x, y) {
                x = typeof x !== 'undefined' ? x : 0;
                y = typeof y !== 'undefined' ? y : 0;
                websocket.send(JSON.stringify({'action': action, 'seat': seat, 'x': x, 'y': y}));
            }

            function gotoLobby() {
                //TO-DO: notify server
                window.location = "lobby.jsp";
            }
            window.addEventListener("load", init, false);
        </script>
    </body>
</html>

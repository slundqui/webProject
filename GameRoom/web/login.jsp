<%-- 
    Document   : login
    Created on : Oct 31, 2013, 10:53:13 PM
    Author     : Chen
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% 
    String message = (String)session.getAttribute("MESSAGE");
    if(message == null) { message = ""; }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Game Room</title>
        <style type="text/css">
        </style>
    </head>
    <body>
        <div align="center">
            <h1>Game Room</h1><br>
            <form action="LoginServlet" method="post">
                <h3>User Login</h3>
                <table border="0">
                    <tr>
                        <td>Username: </td><td><input type="text" name="username" required></td>
                    </tr>
                    <tr>
                        <td>Password: </td><td><input type="password" name="password" required></td>
                    </tr>
                    <tr>
                        <td align="center"><input type="submit" value="Log In"></td>
                        <td align="center"><a href = "./register.jsp"> New User? Register Here</a></td>
                    </tr>
                </table>
            </form>
            <span style = "color:red;"><%=message %></span>
        </div>
    </body>
</html>
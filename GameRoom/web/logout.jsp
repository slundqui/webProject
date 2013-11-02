<%-- 
    Document   : logout
    Created on : Nov 1, 2013, 12:37:45 AM
    Author     : Chen
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% session.invalidate(); %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Logout</title>
    </head>
    <body>
        <h1>You have successfully logged out!</h1>
        <a href="login.jsp">Login</a>
    </body>
</html>

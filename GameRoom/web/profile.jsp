<%-- 
    Document   : profile
    Created on : Dec 2, 2013, 1:52:22 AM
    Author     : huipingyao
 <A HREF="<%=request.getContextPath()%>/DisplayResumeServlet">View Resume</A>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User Profile</title>
    </head>
    <body>
        <div align="center">
            <h3>Your Profile</h3>
            <A HREF="<%=request.getContextPath()%>/ViewProfileServlet">View Profile</A> <br/>
            <A HREF="changePassword.jsp">Change Password</A> <br/> <br/>
            <A HREF="lobby.jsp">Return to lobby</A>
        </div>
    </body>
</html>

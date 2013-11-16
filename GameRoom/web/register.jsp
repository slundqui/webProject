<%-- 
    Document   : register
    Created on : Nov 16, 2013, 2:00:43 PM
    Author     : huipingyao
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>New User Registration</title>
    </head>
    <body>
        <div align="center">
            <h1>Game Room</h1> <br/>
            <form action="RegisterServlet" method="post">
                <h3>New User Registration</h3> <br/>
                <table border="0">
                    <tr>
                        <td>Username: </td>
                        <td><input type="text" name="username" required></td>
                    </tr>
                    <tr>
                        <td>Password: </td>
                        <td><input type="password" name="pwd" required></td>
                    </tr>
                    <tr>
                        <td>Re-enter password: </td>
                        <td><input type="password" name="pwd2" required></td>
                    </tr>
                    <tr>
                        <td colspan="2" align="center"><input type="submit" value="Register"></td>
                    </tr>
                </table>
            </form>
        </div>
    </body>
</html>

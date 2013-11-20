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
        <script type="text/javascript">
            var hasInvalidData = false;
            
            function validateUsername(usernameField){
                hasInvalidData = false;
                
                // Check if username uses at lease 4 characters (letters, numbers, periods only)
                var username = usernameField.value;
                if(username.length < 4){
                    document.getElementById("divId").innerHTML = "<font color='red' size='2.5' face='Arial'>Please enter at least 4 characters.</font>";
                    hasInvalidData = true;
                } else {
                    var validUsername = new RegExp("^[a-zA-Z0-9.]{4,}$");
                    if(!validUsername.test(username.value)){
                        document.getElementById("divId").innerHTML = "<font color='red' size='2.5' face='Arial'>Please remove invalid characters.</font>";
                        hasInvalidData = true;
                    }
                    // Check if the username already exists
                    else{
                        validateUsernameAJAX(usernameField);
                    }
                }
            }
            
            var XMLHttp;
            function validateUsernameAJAX(usernameField){
                // Create XMLHttpRequest Object
                if(window.XMLHttpRequest){
                    XMLHttp = new XMLHttpRequest();
                }else if(window.ActiveXObject){
                    XMLHttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                
                // Create a URL that contains data that can be utilized by the server-side
                var url = "ValidateUsername?username=" + usernameField.value;
                
                // Specify three paremeters: a URL, the HTTP method(GET or POST), whether or notthe interaction is asynchronous 
                XMLHttp.open("GET", url, true); //ture, signifying that the interaction is asynchronous
                
                // If the interaction is set as asynchronous, a callback functin must be specified, which must later be defined
                XMLHttp.onreadystatechange=callBack;
                
                // The HTTP interaction begins when XMLHttp.send() is called
                XMLHttp.send(null);
            }
            
            function callBack(){
                if(XMLHttp.readyState == 4){
                    if(XMLHttp.status == 200){
                        var doc = XMLHttp.responseXML;
                        
                        var msg = doc.childNodes[0].childNodes[0].nodeValue;
                        
                        var divId = document.getElementById("divId");

                        if(msg === "true"){
                            divId.innerHTML = "<font color='red' size='2.5' face='Arial'>Someone already has this username. Please try again.</font>";
                            hasInvalidData = true;
                        }else{
                            divId.innerHTML = "<font color='green' size='2.5' face='Arial'>This username is available for you to use.</font>";
                        }
                    }
                }
            }
            
            function validatePassword(pwdField){
                hasInvalidData = false;
                // Clear the old message/contents
                document.getElementById("pwd_warning").innerHTML = "";
                
                // Check if password is a mix of at least 6 letters, numbers or special characters
                var pwd = pwdField.value;
                
                if(pwd.length < 6){
                    document.getElementById("pwd_warning").innerHTML = "<font color='red' size='2.5' face='Arial'>Please enter at least 6 characters.</font>";
                    hasInvalidData = true;
                }else {
                    var validPwd = new RegExp("^[a-zA-Z0-9!@#$&*]{6,}$");
                    if(!validPwd.test(pwd.value)){
                        document.getElementById("pwd_warning").innerHTML = "<font color='red' size='2.5' face='Arial'>Please remove invalid characters.</font>";
                        hasInvalidData = true;
                    }
                }
            }
            
            function validateReEnterPassword(repeatpwdObject){
                hasInvalidData = false;
                // Clear the old message/contents
                document.getElementById("repwd_warning").innerHTML = "";
                
                if(repeatpwdObject.value != document.getElementsByName("pwd")[0].value){
                    //alert("Your passwords do not match!");
                    document.getElementById("repwd_warning").innerHTML = "<font color='red' size='2.5' face='Arial'>Your passwords do not match.</font>"
                    hasInvalidData = true;
                }
            }
            
            function validateForm(){
                if(hasInvalidData == true){
                    return false;
                }else{
                    return true;
                }
            }
        </script>
    </head>
    <body>
        <div align="center">
            <h1>Game Room</h1> <br/>
            <form action="RegisterServlet" onsubmit="return validateForm()" method="post">
                <h3>New User Registration</h3> <br/>
                <table border="0" width="850">
                    <col width="100">
                    <col width="150">
                    <col width="100">
                    <col width="500">
                    <tr>
                        <td></td>
                        <td>Username: </td>
                        <td><input type="text" name="username" onblur="validateUsername(this)" required></td>
                        <td><div id="divId"><font color='#C0C0C0' size='2.5' face='Arial'>Use at lease 4 characters (letters, numbers, periods only).</font></div></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>Password: </td>
                        <td><input type="password" name="pwd" onblur="validatePassword(this)" required></td>
                        <td><div id="pwd_warning"><font color='#C0C0C0' size='2.5' face='Arial'>Use a mix of at least 6 letters, numbers or special characters (!,@,#,$,&,*).</font></div></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>Re-enter password: </td>
                        <td><input type="password" name="repeatpwd" onblur="validateReEnterPassword(this)"></td>
                        <td><div id="repwd_warning"></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td colspan="2" align="center"><input type="submit" value="Register"></td>
                    </tr>
                </table>
            </form>
        </div>
    </body>
</html>

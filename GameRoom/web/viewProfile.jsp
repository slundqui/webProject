<%-- 
    Document   : viewProfile
    Created on : Dec 2, 2013, 2:20:04 AM
    Author     : huipingyao
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>New User Registration</title>
        <script type="text/javascript">
            var isUsernameValid = true;
            var isPasswordValid = true;
            var isEmailValid = true;
            
            function validateUsername(usernameField){
                isUsernameValid = true;
                
                // Check if username uses at lease 4 characters (letters, numbers, periods only)
                var username = usernameField.value;
                if(username.length < 4){
                    document.getElementById("divId").innerHTML = "<font color='red' size='2.5' face='Arial'>Please enter at least 4 characters.</font>";
                    isUsernameValid = false;
                } else {
                    var validUsername = new RegExp("^[a-zA-Z0-9.]{4,}$");
                    if(!validUsername.test(username.value)){
                        document.getElementById("divId").innerHTML = "<font color='red' size='2.5' face='Arial'>Please remove invalid characters.</font>";
                        isUsernameValid = false;
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
                            isUsernameValid = false;
                        }else{
                            divId.innerHTML = "<font color='green' size='2.5' face='Arial'>This username is available for you to use.</font>";
                        }
                    }
                }
            }
            
            function validatePassword(pwdField){
                isPasswordValid = true;
                // Clear the old message/contents
                document.getElementById("pwd_warning").innerHTML = "";
                
                // Check if password is a mix of at least 6 letters, numbers or special characters
                var pwd = pwdField.value;
                
                if(pwd.length < 6){
                    document.getElementById("pwd_warning").innerHTML = "<font color='red' size='2.5' face='Arial'>Please enter at least 6 characters.</font>";
                    isPasswordValid = false;
                }else {
                    var validPwd = new RegExp("^[a-zA-Z0-9!@#$&*]{6,}$");
                    if(!validPwd.test(pwd.value)){
                        document.getElementById("pwd_warning").innerHTML = "<font color='red' size='2.5' face='Arial'>Please remove invalid characters.</font>";
                        isPasswordValid = false;
                    }
                }
            }
            
            function validateReEnterPassword(repeatpwdObject){
                isPasswordValid = true;
                // Clear the old message/contents
                document.getElementById("repwd_warning").innerHTML = "";
                
                if(repeatpwdObject.value != document.getElementsByName("pwd")[0].value){
                    //alert("Your passwords do not match!");
                    document.getElementById("repwd_warning").innerHTML = "<font color='red' size='2.5' face='Arial'>Your passwords do not match.</font>"
                    isPasswordValid = false;
                }
            }
            
            function validateEmail(emailField){
                document.getElementById("email_warning").innerHTML = "";
                isEmailValid = true;
                at = emailField.value.indexOf("@");
                dot = emailField.value.lastIndexOf(".");
                last = emailField.value.length-1;
                if(at<1 || dot-at<2 || last-dot>3 || last-dot<2) {
                    document.getElementById("email_warning").innerHTML = "<font color='red' size='2.5' face='Arial'>The email is invalid.</font>";
                    isEmailValid = false;
                }
            }
            
            function validateForm(){
                if(isUsernameValid==false || isPasswordValid==false || isEmailValid==false){
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
            <form action="UpdateProfileServlet" onsubmit="return validateForm()" method="post">
                <h3>Your Profile</h3> <br/>
                <table border="1" width="100%">
                    <tr>
			<td>
			Account Profile<br>
			* Required Information
			</td>
                    </tr>
                    <tr>
                        <td>
                             <table border="0" width="100%" style="padding:10px;">
                                <col width="30%">
                                <col width="50%">
                                <col width="20%">
                                <tr>
                                    <td>* First Name: </td>
                                    <td><input type="text" name="firstname" size="20" value='${firstnameContent}'></td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td>Middle Name</td>
                                    <td><input type="text" name="middlename" size="10" value=${middlenameContent}></td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td>* Last Name</td>
                                    <td><input type="text" name="lastname" size="20" value=${lastnameContent}></td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td>* Email</td>
                                    <td><input type="text" name="email" size="40" value=${emailContent} onblur="validateEmail(this)" required></td>
                                    <td><div id="email_warning"></div></td>
                                </tr>
                            </table>
                            <table border="0" width="100%" style="padding:10px;">
                                <tr><td><input type="submit" value="Update Profile"><input type="button" value="Cancel" onclick="history.back()"></td></tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </body>
</html>

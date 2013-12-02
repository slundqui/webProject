<%-- 
    Document   : changePassword
    Created on : Nov 26, 2013, 7:37:29 PM
    Author     : huipingyao
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript">
            var newPwdInvalid = false;
            var confirmPwdInvalid = false;
            function clearWarning(){
                document.getElementById("oldpwd_warning").innerHTML = "";
            }
            
            function validatePassword(newPwdField){
                newPwdValid = false;
                // Clear the old message/contents
                document.getElementById("newpwd_warning").innerHTML = "";
                
                // Check if password is a mix of at least 6 letters, numbers or special characters
                var pwd = newPwdField.value;
                
                if(pwd.length < 6){
                    document.getElementById("newpwd_warning").innerHTML = "<font color='red' size='2.5' face='Arial'>Please enter at least 6 characters.</font>";
                    newPwdValid = true;
                }else {
                    var validPwd = new RegExp("^[a-zA-Z0-9]{4,8}$");
                    if(!validPwd.test(pwd)){
                        document.getElementById("newpwd_warning").innerHTML = "<font color='red' size='2.5' face='Arial'>Please remove invalid characters.</font>";
                        newPwdValid = true;
                    }
                }
            }
            
            function validateReEnterPassword(repeatpwdObject){
                confirmPwdValid = false;
                // Clear the old message/contents
                document.getElementById("repwd_warning").innerHTML = "";
                
                if(repeatpwdObject.value !== document.getElementsByName("newPwd")[0].value){
                    //alert("Your passwords do not match!");
                    document.getElementById("repwd_warning").innerHTML = "<font color='red' size='2.5' face='Arial'>Your passwords do not match.</font>"
                    confirmPwdValid = true;
                }
            }
            
            function validateForm(){
                if(newPwdInvalid == true || newPwdInvalid == true){
                    return false;
                }else{
                    return true;
                }
            }
        </script>
    </head>
    <body>
        <div align="center">
            <form action="ChangePasswordServlet" onsubmit="return validateForm()" method="post">
                <h3>Change Password</h3> <br/>
                <table border="0" width="850">
                    <col width="100">
                    <col width="150">
                    <col width="100">
                    <col width="500">
                    <tr>
                        <td></td>
                        <td>Current Password: </td>
                        <td><input type="password" name="oldPwd" onblur="clearWarning()" required></td>
                        <td><div id="oldpwd_warning"><font color='red' size='2.5' face='Arial'>${oldpwd_warning}</font></div></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>New Password: </td>
                        <td><input type="password" name="newPwd" onblur="validatePassword(this)" required></td>
                        <td><div id="newpwd_warning"><font color='#C0C0C0' size='2.5' face='Arial'>Use 4 to 8 letters or numbers</font></div></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>Confirm Password: </td>
                        <td><input type="password" name="repeatpwd" onblur="validateReEnterPassword(this)"></td>
                        <td><div id="repwd_warning"></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td colspan="2" align="center"><input type="submit" value="Save"><input type='button' value="Cancel" onclick="history.back()"></td>
                    </tr>
                </table>
            </form>
        </div>
    </body>
</html>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>
<html>
<head>
<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
<link rel="SHORTCUT ICON" href="favicon.ico"/>
</head>
<title>REPDOSE database login page</title>
<body bgcolor="white">


<form method="POST" action='<%= response.encodeURL("j_security_check") %>' >
  <table border="0" cellspacing="5">
    <tr>
      <th align="right">Username:</th>
      <td align="left"><input type="text" name="j_username"></td>
    </tr>
    <tr>
      <th align="right">Password:</th>
      <td align="left"><input type="password" name="j_password"></td>
    </tr>
    <tr>
      <td align="right"><input type="submit" value="Log in"></td>
      <td align="left"><input type="reset" value="Clear"></td>
    </tr>
  </table>
</form>

<hr>


</body>
</html>

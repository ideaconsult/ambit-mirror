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
<title>QMRF Login Page</title>
<body bgcolor="white">

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="login"/>
	<jsp:param name="viewmode" value="${param.viewmode}"/>    
</jsp:include>

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
Please note that log in is not required for browsing reviewed and published QMRF documents in read-only mode.
<br>
<a href="forgotten.jsp">Forgotten password?</a>

</body>
</html>


<html>
<head>
<title>QMRF Login </title>
</head>
<body bgcolor="white">
	

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="login"/>
</jsp:include>

<jsp:include page="menuall.jsp" flush="true">
		    <jsp:param name="highlighted" value="login" />
</jsp:include>     

<%
  
    session.invalidate();
%>
<h3>
Invalid username and/or password, please try
<a href='<%= response.encodeURL("protected.jsp") %>'>again</a>.
</h3>

</body>
</html>

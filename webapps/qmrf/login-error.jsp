
<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
<head>
<title>QMRF Login </title>
</head>
<body bgcolor="white">
	

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="login"/>
	<jsp:param name="viewmode" value="${param.viewmode}"/>    
</jsp:include>

<%
  
    session.invalidate();
%>
<p>

Invalid username and/or password, please try 
<a href='<%= response.encodeURL("protected.jsp") %>'>again</a>.

</p>

</body>
</html>

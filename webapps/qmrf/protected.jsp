<%
  
  if (request.getParameter("logoff") == "true") {
    session.invalidate();
    response.sendRedirect("index.jsp");
    return;
  }
  if (request.getParameter("logoff") != null) {
    session.invalidate();
    response.sendRedirect("index.jsp");
    return;
  }  
%>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<html>
<head>
<title>QMRF Login</title>
</head>
<body bgcolor="white">
<%
	
  if (request.getUserPrincipal() != null) 
  	session.setAttribute("username",request.getUserPrincipal().getName());
  if (request.isUserInRole("qmrf_admin")) {  	
  	 session.setAttribute("isadmin","true");
  	 response.sendRedirect("admin.jsp");
  }	 else {
  	session.setAttribute("isadmin","false");
  	response.sendRedirect("user.jsp");
  }	
%>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="login"/>
</jsp:include>







</body>
</html>

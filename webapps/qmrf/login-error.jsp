
<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database: Invalid username/password"/>
</jsp:include>
	

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

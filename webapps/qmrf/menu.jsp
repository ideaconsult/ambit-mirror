<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="header.html"%>

<c:if test="${empty sessionScope.tablecolor}">
	<c:set var="tablecolor" value='#FFFFFF' scope="session"/>
</c:if>
<c:if test="${empty sessionScope.rowcolor}">
	<c:set var="rowcolor" value='#EEEEEE' scope="session"/>
</c:if>

<!-- D6DFF7 -->
<c:if test="${empty sessionScope.querycolor}">
	<c:set var="querycolor" value='#D6DFF7' scope="session"/>
</c:if>
<c:if test="${empty sessionScope.headerColor}">
	<c:set var="headercolor" value='#C5CEE6' scope="session"/>
</c:if>

<div style="text-align:right">

	<%

		String status1 = "<a href='protected.jsp' nobr>Log in&nbsp;</a>";
		String status2 = "<a href='register.jsp' nobr>Register</a>";

		if (session.getAttribute("username")  != null) {
			status1 = "Welcome ";
		  Object isadmin = session.getAttribute("isadmin");
		  if ((isadmin != null) && "true".equals(isadmin))
		  	status1 = status1 + "<font color='#FF4444'>";

		  status1 = status1 + "<b>" + session.getAttribute("username") + "</b>";
		  if ((isadmin != null) && "true".equals(isadmin))
		  	status1 = status1 + "</font>";

			status2 = "<a href='protected.jsp?logoff=true' nobr>&nbsp;Log out</a>";
		}
	%>
	<%= status1%>
	<%= " "%>
	<%= status2%>

</div>
<!-- Search  bgcolor="#356AA0"

<%
		String highlighted = request.getParameter("highlighted");
		if (highlighted == null) highlighted = "search";
    if (highlighted.equalsIgnoreCase("advancedsearch")) {
    } else   {

%>

<form method="POST" action='<%= response.encodeURL("search.jsp") %>' >
<div  id="search">
      <input type="text" name="qmrf_simple" size="80">
      <input type="submit" value="Search">
		<a href="search_substances.jsp" nobr>Advanced</a>

		<a href="help.html" target="_blank">Help</a>
</div>
</form>

<%
		}
%>

-->
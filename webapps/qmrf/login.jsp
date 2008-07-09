<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:requestEncoding value="UTF-8"/>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database Log in page"/>
</jsp:include>

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

<p>
Please note that log in is not required for browsing reviewed and published QMRF documents in read-only mode.
<p>
<a href="forgotten.jsp">Forgotten password?</a>

<div id="hits">
<p>
<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
</jsp:include>
</div>
</body>
</html>

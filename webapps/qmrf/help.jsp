<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<html>
  <head>
    <title>QMRF documents</title>
  </head>
  <body>

  <h2>Help</h2>
<table>

		<c:import var="helpxml" url="help.xml" scope="page"/>
		<x:parse var="doc" xml="${helpxml}"/>

		<c:import var="help2html" url="/WEB-INF/xslt/help2html.xsl"/>
	  <x:transform xml="${doc}" xslt="${help2html}"/>

</table>
  </body>
</html>


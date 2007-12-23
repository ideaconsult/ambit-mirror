<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:requestEncoding value="UTF-8"/>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<html>
<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
  <head>
    <title>QMRF Help</title>
  </head>
  <body>

<table>

		<c:import var="helpxml" url="help.xml" scope="page"/>
		<x:parse var="doc" xml="${helpxml}"/>

		<c:import var="help2html" url="/WEB-INF/xslt/help2anchor.xsl"/>
	  	<x:transform xml="${doc}" xslt="${help2html}">
	  		<x:param name="anchor" value="${param.anchor}"/>
	  	</x:transform>

</table>
  </body>
</html>


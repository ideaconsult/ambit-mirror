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

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database Help"/>
</jsp:include>


<table>

		<c:import var="helpxml" url="help.xml" scope="page"/>
		<x:parse var="doc" xml="${fn:trim(helpxml)}"/>
		
		<c:import var="help2html" url="/WEB-INF/xslt/help2anchor.xsl"/>
	  	<x:transform xml="${doc}" xslt="${fn:trim(help2html)}">
	  		<x:param name="anchor" value="${param.anchor}"/>
	  	</x:transform>
		
</table>

  </body>
</html>


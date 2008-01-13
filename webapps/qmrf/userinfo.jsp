<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:import var="xsl" url="/WEB-INF/xslt/users2html.xsl"/>
<c:import var="xml" url="users_xml.jsp"/>
<x:transform xml="${xml}" xslt="${xsl}">
	<x:param name="header" value=""/>
</x:transform>
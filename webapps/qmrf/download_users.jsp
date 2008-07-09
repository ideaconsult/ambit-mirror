<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin']=='false'}" >
  <c:redirect url="/user.jsp"/>
</c:if>

		
<%response.setHeader("Content-Disposition","attachment;filename=users.html");%>
		<jsp:useBean id="now" class="java.util.Date"/>
		<c:set var="date">
		<fmt:formatDate value="${now}" type="DATE" pattern="yyyy-MM-dd hh:mm"/>
		</c:set>
		<c:set var="u">${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}</c:set>
		
		<c:import var="xsl" url="/WEB-INF/xslt/users2html.xsl"/>
		<c:import var="xml" url="users_xml.jsp"/>
		<x:transform xml="${xml}" xslt="${xsl}">
			<x:param name="header" value="Registered users in QMRF Database [ ${u} ] at ${date}"/>
		</x:transform>

<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%response.setHeader("Content-Disposition","attachment;filename="+request.getParameter("name"));%>

		<jsp:useBean id="now" class="java.util.Date"/>
		<c:set var="date">
		<fmt:formatDate value="${now}" type="DATE" pattern="yyyy-MM-dd hh:mm"/>
		</c:set>
		<c:set var="u">${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}</c:set>

		<c:import var="xsl" url="/WEB-INF/xslt/catalogs2html.xsl"/>
		<c:import var="xml" url="${param.source}"/>
		<x:transform xml="${fn:trim(xml)}" xslt="${xsl}">
			<x:param name="footer" value="QMRF Database [ ${u} ] Report"/>
			<x:param name="reportdate" value="${date}"/>
		</x:transform>

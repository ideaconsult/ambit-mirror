<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%response.setHeader("Content-Disposition","attachment;filename="+request.getParameter("name"));%>
		<c:import var="xsl" url="/WEB-INF/xslt/catalogs2html.xsl"/>
		<c:import var="xml" url="${param.source}"/>
		<x:transform xml="${fn:trim(xml)}" xslt="${xsl}"/>

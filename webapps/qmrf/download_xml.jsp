<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>   
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
response.setHeader("Pragma","no-cache");
response.setHeader("Cache-control","no-cache");
response.setDateHeader("Expires",0);
%>
<c:catch var ="error">
		<sql:update var="updatecount" dataSource="jdbc/qmrf_documents">
			set names utf8;
		</sql:update>		
</c:catch>
<c:choose>
	<c:when test="${empty param.id}">
			<c:import var="src" url="qmrf.xml"/>
			<c:choose>
				<c:when test="${param.action eq 'noattachments'}">
					<c:import var="qmrfToHtml" url="/WEB-INF/xslt/delete_attachments.xsl"/>
					<x:transform xml="${fn:trim(src)}" xslt="${fn:trim(qmrfToHtml)}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd"/>
				</c:when>
				<c:otherwise>
					${src}
				</c:otherwise>
			</c:choose>
	</c:when>
	<c:when test="${empty sessionScope['username']}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,qmrf_number,xml from documents where idqmrf=? and status='published' limit 1
			<sql:param value="${param.id}"/>	
		</sql:query>				
	</c:when>			

	<c:when test="${sessionScope['isadmin'] eq 'true'}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,qmrf_number,xml from documents where idqmrf=? limit 1
			<sql:param value="${param.id}"/>	
		</sql:query>				
	</c:when>	
	<c:otherwise>
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,qmrf_number,xml from documents where idqmrf=? and user_name=? limit 1
			<sql:param value="${param.id}"/>	
			<sql:param value="${sessionScope['username']}"/>
		</sql:query>		
	</c:otherwise>		
</c:choose>

<c:choose>
<c:when test="${rs.rowCount == 0}">
		<QMRF>
		</QMRF>
</c:when>
<c:when test="${param.action eq 'noattachments'}">
	
	<c:catch var="error">
	<c:forEach var="row" items="${rs.rows}">
		<c:import var="qmrfToHtml" url="/WEB-INF/xslt/delete_attachments.xsl"/>
		<x:transform xml="${fn:trim(row.xml)}" xslt="${fn:trim(qmrfToHtml)}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd">
		</x:transform>
	</c:forEach>	
	</c:catch>
</c:when>

<c:when test="${param.action eq 'dbattachments'}">
	<c:forEach var="row" items="${rs.rows}">

		<c:import url="attachments_xml.jsp" var="dba">
				<c:param name="id" value="${param.id}"/>
		</c:import>
		<c:set var="xsl">
			<?xml version="1.0" encoding="UTF-8"?>
			<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/1999/xhtml">
			<xsl:output method="xml"  encoding="utf-8" doctype-system="qmrf.dtd"
				 indent="yes"/>

			<xsl:template match="@*|node()">
				<xsl:copy>
					<xsl:apply-templates select="@*|node()"/>
				</xsl:copy>
			</xsl:template>
			
			<xsl:template match="comment()"/>
			
			<xsl:template match="attachments">
				<xsl:copy>
					<xsl:attribute name="chapter">
						<xsl:value-of select="@chapter" />
					</xsl:attribute>
					<xsl:attribute name="name">
						<xsl:value-of select="@name" />
					</xsl:attribute>
					${dba}
				</xsl:copy>
				
			</xsl:template>
						
			</xsl:stylesheet>
		</c:set>
		<c:catch var="error">
			<x:transform xml="${fn:trim(row.xml)}" xslt="${fn:trim(xsl)}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd"/>
		</c:catch>
		<c:if test="${!empty error}">
			${error}
		</c:if>

	</c:forEach>		
</c:when>

<c:otherwise>
		<c:forEach var="row" items="${rs.rows}">
				${fn:trim(row.xml)}
		</c:forEach>	
</c:otherwise>		
</c:choose>


<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<%
response.setHeader("Pragma","no-cache");
response.setHeader("Cache-control","no-cache");
response.setDateHeader("Expires",0);
	%>
	<c:import url="attachments_xml.jsp" var="dba">
		<c:param name="id" value="${param.id}"/>
	</c:import>

	<c:set var="xsl">
		<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/1999/xhtml">
		<xsl:output method="xml"  encoding="utf-8" doctype-system="/WEB-INF/xslt/qmrf.dtd" indent="yes"/>

		<xsl:template match="@*|node()">
			<xsl:copy>
				<xsl:apply-templates select="@*|node()"/>
			</xsl:copy>
		</xsl:template>

		<xsl:template match="attachments">
			<xsl:copy>
				<xsl:apply-templates select="@"/>
				${dba}
			</xsl:copy>
		</xsl:template>
		</xsl:stylesheet>
	</c:set>

	<x:transform xml="${param.xml}" xslt="${xsl}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd">
	</x:transform>
<!--
	include_attachments
	Replaces <attachments/> in param.xml with attachments from database.
	expects:
	param.id - qmrf_documents.documents.idqmrf
	param.xml - the xml to be transformed
-->
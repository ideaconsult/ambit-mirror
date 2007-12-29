<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<c:set var="idqmrf" value="${sessionScope['qmrf_document']}" />
<c:if test="${!empty param.id}" >
	<c:set var="idqmrf" value="${param.id}" />
</c:if>

<c:choose>
<c:when test="${sessionScope['isadmin']=='true'}" >
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select d.idqmrf,qmrf_number,name,format,description,type,a.updated,idattachment,original_name,imported,status from documents as d join attachments as a using(idqmrf) where d.idqmrf=? order by type
		<sql:param value="${idqmrf}"/>
	</sql:query>

</c:when>
<c:otherwise>
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select d.idqmrf,qmrf_number,name,format,description,type,a.updated,idattachment,original_name,imported,status from documents as d join attachments as a using(idqmrf) where d.idqmrf=? and ((user_name=?) || (status='published')) order by type
	<sql:param value="${idqmrf}"/>
	<sql:param value="${sessionScope['username']}"/>

</sql:query>

</c:otherwise>
</c:choose>

<c:set var="atype" value= ""/>
<c:set var="atag" value= ""/>
<c:set var="btag" value= ""/>

<c:set var="u">${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}</c:set>

		<c:forEach var="row" items="${rs.rows}">
			<c:set var="filename" value="${row.original_name}" />
			<c:set var="delim" value="\//" />
			<c:set value="${fn:split(filename,delim)}" var="paths"/>
			<c:if test="${fn:length(paths)>0}">
				<c:set var="filename" value="${paths[fn:length(paths)-1]}" />
			</c:if>
			<c:if test="${atype ne row.type}">
				<c:if test="${!empty atype}">
					</xsl:element>
				</c:if>
				<c:set var="atype" value= "${row.type}" />
					<c:choose>
					<c:when test="${row.type eq 'data_training'}">
						<c:set var="atag" value= "attachment_training_data" />
						<c:set var="btag" value= "molecules" />
					</c:when>
					<c:when test="${row.type eq 'data_validation'}">
						<c:set var="atag" value= "attachment_validation_data" />
						<c:set var="btag" value= "molecules" />
					</c:when>
					<c:when test="${row.type eq 'document'}">
						<c:set var="atag" value= "attachment_documents" />
						<c:set var="btag" value= "document" />
					</c:when>
					<c:otherwise/>
					</c:choose>
				<xsl:element name="${atag}" xmlns="">
			</c:if>
			<xsl:element name="${btag}">
				<xsl:attribute name="description">${row.description}</xsl:attribute>

				<xsl:attribute name="filetype">${row.format}</xsl:attribute>
				<xsl:attribute name="url">${u}/download_attachment.jsp?name=${row.name}</xsl:attribute>
			</xsl:element>
		</c:forEach>
	<c:if test="${!empty atype}">
		</xsl:element>
	</c:if>


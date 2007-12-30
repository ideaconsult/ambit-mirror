<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %>
<?xml version='1.0' encoding='UTF-8' ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!--
param.url = file  generates absolute file paths
param.url = url generates URL for attachments 
-->
<!--
<!ELEMENT molecules (property*)>
<!ATTLIST molecules embedded (Yes|No) #IMPLIED url CDATA #REQUIRED  filetype CDATA #IMPLIED description CDATA #IMPLIED>

<!ELEMENT property EMPTY>
<!ATTLIST property fieldname CDATA #REQUIRED fieldtype CDATA #REQUIRED newname CDATA #IMPLIED>
-->

<c:catch var = "err">
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT name,description,type FROM attachments join attachments_description using (idattachment) where idattachment=? group by idqmrf
		<sql:param value="${param.idattachment}"/>
	</sql:query>
	<c:choose>
	<c:when test="${rs.rowCount > 0}">
		<c:set var="u">${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}</c:set>
		<c:forEach var="row" items="${rs.rows}">
			<c:choose>
			<c:when test="${param.url eq 'file'}">
				<c:set var="filename" value="${initParam['attachments-dir']}/${row.name}"/>
			</c:when>
			<c:otherwise>
				<c:set var="filename" value="${u}/download_attachment.jsp?name=${row.name}"/>
			</c:otherwise>
			</c:choose>
			<molecules embedded="No" filetype="${row.type}" url="${filename}" description="${row.description}" dataset="${row.name}">
		</c:forEach>
		
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idattachment,fieldname,fieldtype,newname from attachments_description where idattachment=?
			<sql:param value="${param.idattachment}"/>
		</sql:query>	
		<c:forEach var="row" items="${rs.rows}">
			<property fieldname="${row.fieldname}" fieldtype="${row.fieldtype}" newname="${row.newname}" />
		</c:forEach>
		</molecules>
	</c:when>
	<c:otherwise>
		<molecules/>
	</c:otherwise>	
	</c:choose>
</c:catch>		

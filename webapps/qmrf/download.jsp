<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="/ambit" prefix="a" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<c:choose>
	<c:when test="${empty param.id}">
		  <c:redirect url="index.jsp"/>
	</c:when>
	<c:when test="${empty sessionScope['username']}">
		<sql:update dataSource="jdbc/qmrf_documents">
				set names 'utf8'
		</sql:update>
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select idqmrf,qmrf_number,xml from documents where idqmrf=? and status='published' limit 1;
				<sql:param value="${param.id}"/>
		</sql:query>
	</c:when>
	<c:when test="${(sessionScope['isadmin'] eq 'true') || (sessionScope['ismanager'] eq 'true') || (sessionScope['iseditor'] eq 'true')}">
		<sql:update dataSource="jdbc/qmrf_documents">
				set names 'utf8'
		</sql:update>
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select idqmrf,qmrf_number,xml from documents where idqmrf=? limit 1;
				<sql:param value="${param.id}"/>
		</sql:query>
	</c:when>
	<c:otherwise>
		<sql:update dataSource="jdbc/qmrf_documents">
				set names 'utf8'
		</sql:update>
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select idqmrf,qmrf_number,xml from documents where idqmrf=? and ((user_name=?) || (status='published')) limit 1;
				<sql:param value="${param.id}"/>
				<sql:param value="${sessionScope['username']}"/>
		</sql:query>
	</c:otherwise>
</c:choose>

<c:if test="${rs.rowCount == 0}">
	<%response.setHeader("Content-Disposition","attachment;filename=download_qmrf.txt");%>
	No documents found. Are you logged in? 
	<br/>
</c:if>
<c:forEach var="row" items="${rs.rows}">
	<c:set var="filename" value="${row.idqmrf}" />

	<c:choose>
	<c:when test="${param.filetype eq 'html'}">
		<%response.setContentType ("text/html; charset=UTF-8");%>
		<%response.setHeader("Content-Disposition","attachment;filename=download_qmrf.html");%>
		<c:import var="qmrfToHtml" url="/WEB-INF/xslt/qmrf2html.xsl"/>
		 <x:transform xml="${row.xml}" xslt="${qmrfToHtml}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd"/>
	</c:when>
	<c:when test="${param.filetype eq 'pdf'}">
		<%response.setHeader("Content-Disposition","attachment;filename=download_qmrf.pdf");%>
		<c:set var="url">${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}</c:set>
		<a:qmrfexport xml="${row.xml}" type="${param.filetype}"  fontURL="${url}/times.ttf"/>
	</c:when>
	<c:when test="${param.filetype eq 'xls'}">
		<%response.setHeader("Content-Disposition","attachment;filename=download_qmrf.xls");%>
		<a:qmrfexport xml="${row.xml}" type="${param.filetype}" />
	</c:when>
	<c:when test="${param.filetype eq 'xml'}">
		<%response.setContentType ("text/xml; charset=UTF-8");%>
		<%response.setHeader("Content-Disposition","attachment;filename=download_qmrf.xml");%>
		${row.xml}
	</c:when>
	<c:otherwise>
		<%response.setHeader("Content-Disposition","attachment;filename=download_qmrf.txt");%>
		<a:qmrfexport xml="${row.xml}" type="${param.filetype}" />
	</c:otherwise>

	</c:choose>
</c:forEach>


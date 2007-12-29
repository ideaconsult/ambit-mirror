<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<fmt:requestEncoding value="UTF-8"/>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin']=='false'}" >
  <c:redirect url="/user.jsp"/>
</c:if>
<%response.setHeader("Content-Disposition","attachment;filename=qmrf_"+request.getParameter("status")+".html");%>

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select idqmrf,qmrf_number,version,user_name,updated,status,reviewer from documents where status = ? order by updated desc
	<sql:param value="${param.status}"/>
</sql:query>

<table width="95%" rules="groups" frame="box">
<thead>
<tr>
<th>QMRF number</th>
<th>Title</th>
<th>Keywords</th>
<th>Date publication</th>
<th>Summary comments</th>
<th>Attachments</th>
<th>Status</th>
<th>Version</th>
<th>Author</th>
<th>Reviewer</th>
<th>Last modified</th>
<th>Download</th>
</tr>
</thead>

<tbody>
<c:set var="url">${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}</c:set>
<c:forEach var="row" items="${rs.rows}">
	<tr bgcolor="#FFFFFF">
	<sql:query var="subrs" dataSource="jdbc/qmrf_documents">
		select xml from documents where idqmrf=?
		<sql:param value="${row.idqmrf}"/>
	</sql:query>
	<c:forEach var="subrow" items="${subrs.rows}">
		<x:parse xml="${subrow.xml}" var="doc" systemId="/WEB-INF/xslt/qmrf.dtd"/>
		<td>
		<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QMRF_Summary/QMRF_number"/>
		</td>		
		<td>
			<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QSAR_identifier/QSAR_title"/>
		</td>
		<td>
			<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QMRF_Summary/keywords"/>
		</td>		
		<td>
			<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QMRF_Summary/date_publication"/>
		</td>		
		<td>
			<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QMRF_Summary/summary_comments"/>
		</td>						
		
	</c:forEach>

	<sql:query var="subrs" dataSource="jdbc/qmrf_documents">
		SELECT idqmrf,name,description,type from attachments where idqmrf=?
		<sql:param value="${row.idqmrf}"/>
	</sql:query>
	<td>
	<c:forEach var="subrow" items="${subrs.rows}">
		<a href="
		<c:url value="${url}/download_attachment.jsp">
			<c:param name="name" value="${subrow.name}"/>
		</c:url>
		">${subrow.description}</a>(${subrow.type})
		<br>
	</c:forEach>	
	</td>
	<td>
	${row.status}
	</td>	
	<td>
	${row.version}
	</td>		
	<td>
	${row.user_name}
	</td>	
	<td>
	${row.reviewer}
	</td>	
	<td>
	${row.updated}
	</td>		
	<td>
					<a href="
						<c:url value="${url}/download.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
					  <c:param name="filetype" value="html"/>
						</c:url>
					"><img src="${url}/images/html.png"  alt="HTML file" border="0"/></a>

					<a href="
						<c:url value="${url}/download.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
					  <c:param name="filetype" value="pdf"/>
						</c:url>
					"><img src="${url}/images/pdf.png" border="0" alt="Adobe PDF"/></a>


					<a href="
						<c:url value="${url}/download.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
					  <c:param name="filetype" value="xls"/>
						</c:url>
					" ><img src="${url}/images/xls.png" " alt="MS Excel XLS file" border="0"/></a>
					
					<a href="
						<c:url value="${url}/download.jsp">
					  <c:param name="filetype" value="xml"/>						
					  <c:param name="id" value="${row.idqmrf}"/>
					  <c:param name="action" value="dbattachments"/>
						</c:url>
					"><img src="${url}/images/xml.png"  alt="QMRF XML" border="0"/></a>	
	</td>
	
	
	

	</tr>
</c:forEach>
</tbody>
</table>
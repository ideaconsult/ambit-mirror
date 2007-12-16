<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>


<c:set var="thispage" value='admin_status.jsp'/>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin']=='false'}" >
  <c:redirect url="/user.jsp"/>
</c:if>

<jsp:include page="query_settings.jsp" flush="true"/>

<c:set var="startpage" value="${sessionScope.page}"/>
<c:set var="startrecord" value="${sessionScope.page * sessionScope.pagesize}"/>

<c:choose>
	<c:when test="${empty param.source}">
			<c:set var="source" value=""/>
	</c:when>
	<c:otherwise>
		<c:set var="source" value="${param.source}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty param.status}">
			<c:set var="status" value=""/>
	</c:when>
	<c:otherwise>
		<c:set var="status" value="${param.status}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty param.users}">
			<c:set var="users" value=""/>
	</c:when>
	<c:otherwise>
		<c:set var="users" value="${param.users}"/>
	</c:otherwise>
</c:choose>

<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
  <head>
    <title>QMRF documents</title>
  </head>
  <body>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="catalog"/>
</jsp:include>

<jsp:include page="menuall.jsp" flush="true">
		    <jsp:param name="highlighted" value="catalog" />
</jsp:include>

<table width="50%" bgcolor="#FFFFFF"><tr bgcolor="#D6DFF7">
<td>
<a href="
	<c:url value="${thispage}">
	<c:param name="source" value="documents"/>
	</c:url>
">Documents</a>
</td><td>
<a href="
	<c:url value="${thispage}">
	<c:param name="source" value="users"/>	
	</c:url>
">Users</a>
</td><td>
<a href="
	<c:url value="${thispage}">
	<c:param name="source" value="catalogues"/>
	</c:url>
">Catalogues</a>
</td><td>
<a href="
	<c:url value="${thispage}">
	<c:param name="source" value="config"/>
	</c:url>
">Configuration</a>
</td></tr>
</table>

<c:if test="${param.source eq 'documents'}">
		<h3>QMRF Documents</h3>
		
		<c:catch var="exception">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			SELECT count(idqmrf) as no,status  FROM documents d group by status order by status;
		</sql:query>
		</c:catch>
		
		
		<c:choose>
		<c:when test="${!empty exception}">
			<div class="error">
			${exception}
		</div>
		</c:when>
		<c:otherwise>
			<table bgcolor="${tablecolor}">
			<tr bgcolor="${rowcolor}"><th>Document status</th><th>Number of documents</th><th>Report</th></tr>
		
			<c:forEach var="row" items="${rs.rows}">
			<c:set var="mp">
			<fmt:formatNumber type="number" value="${(row.no / pagesize) +0.5}" pattern="###"/>
			</c:set>
				<tr bgcolor="${rowcolor}">
		
		
				<td>${row['status']}</td>
				<td>${row['no']}
					</td>
					<td>
					<a href="
			<c:url value="download_report.jsp">
				<c:param name="status" value="${row['status']}"/>
				<c:param name="source" value="documents"/>
			</c:url>
		"><img src="images/html.png"  alt="HTML file" border="0"/></a>
					
					
					</td>
			</tr>
			</c:forEach>
		</tr>
		</table>
		</c:otherwise>
		</c:choose>
</c:if>

<c:if test="${param.source eq 'users'}">
<h3>QMRF Users</h3>

<c:catch var="exception">
<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	SELECT registration_status,count(user_name) as un FROM users group by registration_status order by registration_status;
</sql:query>
</c:catch>


<c:choose>
<c:when test="${!empty exception}">
	<div class="error">
	${exception}
</div>
</c:when>
<c:otherwise>
	<table bgcolor="#FFFFFF">
	<tr bgcolor="${rowcolor}"><th>Registration status</th><th>Number of users</th><th>Report</th></tr>

	<c:forEach var="row" items="${rs.rows}">
		<tr bgcolor="${rowcolor}">
			<td>
			${row['registration_status']}
			</td>

			<td>${row['un']}</td>
			<td>
			<a href="
				<c:url value="download_users.jsp">
				<c:param name="users" value="${row['registration_status']}"/>
	</c:url>
	">
	<img src="images/html.png"  alt="HTML file" border="0"/></a>
</td>


	</tr>
	</c:forEach>
</tr>
</table>
</c:otherwise>
</c:choose>

</c:if>

<c:if test="${param.source eq 'catalogues'}">
		<h3>QMRF Catalogues</h3>
		
			<table bgcolor="#FFFFFF">
			<tr bgcolor="#D6DFF7">
			<th>Catalogue</th>
			<th>Number of entries</th>
			<th>Report</th>
			</tr>
				<!-- endpoints -->
			<c:catch var="exception">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				SELECT count(*) as no FROM catalog_endpoints c;
			</sql:query>
			</c:catch>
		
		
			<c:choose>
			<c:when test="${!empty exception}">
				<div class="error">
				${exception}
			</div>
			</c:when>
			<c:otherwise>
		
				<tr bgcolor="${rowcolor}"><th>Endpoints</th>
		
				<c:forEach var="row" items="${rs.rows}">
						<td>
						${row['no']}
						</td><td>
						<a href="
					<c:url value="download_catalogs.jsp">
				<c:param name="source" value="endpoints_xml.jsp"/>
				<c:param name="name" value="endpoints.html"/>
					</c:url>
					">
					<img src="images/html.png"  alt="HTML file" border="0"/></a>
				</c:forEach>
			</tr>
		
			</c:otherwise>
			</c:choose>
		
				<!-- algorithms -->
			<c:catch var="exception">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				SELECT count(*) as no FROM catalog_algorithms c;
			</sql:query>
			</c:catch>
		
		
			<c:choose>
			<c:when test="${!empty exception}">
				<div class="error">
				${exception}
			</div>
			</c:when>
			<c:otherwise>
		
				<tr bgcolor="${rowcolor}"><th>Algorithms</th>
		
				<c:forEach var="row" items="${rs.rows}">
		
						<td>
						${row['no']}
						</td><td>
						<a href="
					<c:url value="download_catalogs.jsp">
				<c:param name="source" value="algorithm_xml.jsp"/>
				<c:param name="name" value="algorithms.html"/>
					</c:url>
					">
					<img src="images/html.png"  alt="HTML file" border="0"/></a>
				</c:forEach>
			</tr>
		
			</c:otherwise>
			</c:choose>

<!-- software -->
		<c:catch var="exception">
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT count(*) as no FROM catalog_software c;
	</sql:query>
	</c:catch>


	<c:choose>
	<c:when test="${!empty exception}">
		<div class="error">
		${exception}
	</div>
	</c:when>
	<c:otherwise>

		<tr bgcolor="${rowcolor}"><th>Software packages</th>

		<c:forEach var="row" items="${rs.rows}">

			<td>
				${row['no']}
				</td><td>
				<a href="
			<c:url value="download_catalogs.jsp">
			<c:param name="source" value="software_xml.jsp"/>
			<c:param name="name" value="software.html"/>
			</c:url>
			">
			
			<img src="images/html.png"  alt="HTML file" border="0"/></a>	
		</c:forEach>
	</tr>

	</c:otherwise>
	</c:choose>

	<!-- authors -->
		<c:catch var="exception">
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT count(*) as no FROM catalog_authors c;
	</sql:query>
	</c:catch>


	<c:choose>
	<c:when test="${!empty exception}">
		<div class="error">
		${exception}
	</div>
	</c:when>
	<c:otherwise>

		<tr bgcolor="${rowcolor}"><th>Authors</th>

		<c:forEach var="row" items="${rs.rows}">

				<td>
				${row['no']}
				</td><td>
				<a href="
			<c:url value="download_catalogs.jsp">
				<c:param name="source" value="authors_xml.jsp"/>
				<c:param name="name" value="authors.html"/>
			</c:url>
			">
			<img src="images/html.png"  alt="HTML file" border="0"/></a>
	</td>
		</c:forEach>
	</tr>

	</c:otherwise>
	</c:choose>



	</table>

</c:if>

<c:if test="${source eq 'config'}">
<table bgcolor="#DDDDDD">
<tr bgcolor="#FFFFFF">
<th>Mail server</th>
<td>${initParam["mail-server"]}</td>
<td><i>Required for registration to work properly. Edit web.xml in order to change</i></td>
</tr>
<tr bgcolor="#FFFFFF">
<th>e-mail from</th>
<td>${initParam["mail-from"]}</td>
<td><i>Required in order to send confirmation mails upon registration. Edit web.xml in order to change</i></td>
</tr>
<tr bgcolor="#FFFFFF">
<th>Folder to store attachments</th>
<td>${initParam["attachments-dir"]}</td>
<td><i>This folder is used to store attachments uploaded by users. Please ensure the web application server has write permission. Edit web.xml in order to change</i></td>
</tr>
<tr bgcolor="#FFFFFF">
<th>Support</th>
<td>${initParam["support-email"]}</td>
<td></td>
</tr>
</table>
</c:if>
  </body>
</html>


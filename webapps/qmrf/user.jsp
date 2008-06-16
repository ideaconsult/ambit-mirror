<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="thispage" value='user.jsp'/>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>
<c:if test="${sessionScope['viewmode'] ne 'qmrf_user'}" >
  <c:redirect url="index.jsp"/>
</c:if>
<c:catch var="exception">
		<c:if test="${empty sessionScope['username']}" >
		  <c:redirect url="protected.jsp"/>
		</c:if>

		<c:if test="${empty sessionScope['isauthor']}" >

		  <c:redirect url="index.jsp"/>
		</c:if>

		<c:if test="${sessionScope['isauthor'] eq 'false'}" >
		  <c:redirect url="index.jsp"/>
		</c:if>
</c:catch>

<c:if test="${not empty exception}">
  <c:redirect url="index.jsp"/>
</c:if>

<jsp:include page="query_settings.jsp" flush="true"/>

<c:if test="${!empty param.status && (sessionScope.record_status ne param.status)}">
	<c:set var="page" value="0" scope="session"  />
</c:if>

<c:set var="startpage" value="${sessionScope.page}"/>
<c:set var="startrecord" value="${sessionScope.page * sessionScope.pagesize}"/>

<c:set var="uname" value='${sessionScope["username"]}'/>
<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select user_name,qmrf_number,status,updated from documents where user_name=? order by ?
	<sql:param value="${sessionScope['username']}"/>
  <sql:param value="${order}"/>

</sql:query>

<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
  <head>
    <title>QMRF documents</title>
  </head>
  <body>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="user"/>
	<jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>


<c:catch var="exception">
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select count(*)as no,status from documents where user_name=? and (status = 'draft' || status = 'returned for revision') group by status;
		<sql:param value="${sessionScope['username']}"/>
	</sql:query>
	<c:if test="${rs.rowCount > 0}">
		<div class="center">
		<font color="#FF4444">Pending documents:
		<c:forEach var="row" items="${rs.rows}">
			<b>${row.status}</b>&nbsp;(${row.no})&nbsp;
		</c:forEach>
		</font>
		<a href="help.jsp?anchor=pending" target="help"><img src="images/help.png" alt="help" title="What is it?" border="0"/></a>
		</div>
	</c:if>
</c:catch>

<jsp:include page="records_status.jsp" flush="true">
    <jsp:param name="status" value="${param.status}"/>
    <jsp:param name="status_allowed" value="all,draft,submitted,under review,returned for revision,review completed,published,archived"/>

</jsp:include>

<!-- count max pages -->
<c:if test="${startrecord eq 0}">
	<c:choose>
		<c:when test="${(empty sessionScope.record_status) || (sessionScope.record_status eq 'all')}">
			<c:set var="sql" value="select count(idqmrf) as c from documents where user_name=? and status != 'archived'"/>
		</c:when>
		<c:otherwise>
			<c:set var="sql" value="select count(idqmrf) as c from documents where user_name=? and status = '${sessionScope.record_status}'"/>
		</c:otherwise>
	</c:choose>
	<c:catch var="error">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			${sql}
			<sql:param value="${sessionScope['username']}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<c:set var="maxpages" scope="session">
			<fmt:formatNumber type="number" value="${(row.c / sessionScope.pagesize) +0.5}" pattern="###"/>
			</c:set>
			<c:if test="${sessionScope.page > maxpages}">
				<c:set var="page" value="0" scope="session"  />
				<c:set var="startrecord" value="0" />
			</c:if>
		</c:forEach>
	</c:catch>
	<c:if test="${!empty error}">
		${error}
		<c:set var="maxpages" scope="session" value="10"/>
	</c:if>
</c:if>

<c:set var="sql" value="select idqmrf,qmrf_number,version,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where user_name=? and status = '${sessionScope.record_status}' order by ${sessionScope.order} ${sessionScope.order_direction} limit ${startrecord},${sessionScope.pagesize}"/>
<c:if test="${(empty sessionScope.record_status) || (sessionScope.record_status eq 'all')}">
	<c:set var="sql" value="select idqmrf,qmrf_number,qmrf_title,version,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where user_name=? and status != 'archived' order by ${sessionScope.order} ${sessionScope.order_direction} limit ${startrecord},${sessionScope.pagesize}"/>
</c:if>

<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${sql}"/>

		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="qmrf_title" value="Title"/>		
		<jsp:param name="version" value="Version"/>
		<jsp:param name="user_name" value="Author"/>
		<jsp:param name="lastdate" value="Last updated"/>
		<jsp:param name="status" value="Status"/>
		<jsp:param name="actions" value="user"/>

		<jsp:param name="sqlparam" value="${sessionScope['username']}"/>

		<jsp:param name="paging" value="true"/>
		<jsp:param name="page" value="${startpage}"/>
		<jsp:param name="pagesize" value="${sessionScope.pagesize}"/>
		<jsp:param name="viewpage" value="user.jsp"/>


</jsp:include>

<c:choose>
	<c:when test="${param.history eq 'true'}">
		<jsp:include page="history.jsp" flush="true">
    <jsp:param name="id" value="${param.id}"/>
		</jsp:include>
	</c:when>
	<c:otherwise>
		<jsp:include page="view.jsp" flush="true">
			<jsp:param name="highlighted" value="${param.id}"/>
		</jsp:include>
	</c:otherwise>
</c:choose>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>

  </body>
</html>


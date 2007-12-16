<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="thispage" value='submit.jsp'/>


<c:if test="${empty param.xml}" >
  <c:redirect url="/user.jsp"/>
</c:if>


<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin']=='true'}" >
  <c:redirect url="/admin.jsp"/>
</c:if>

<!-- Not launched from create.jsp -->
<c:if test="${sessionScope.qmrf_create ne 'true'}">
	<c:redirect url="/user.jsp"/>
</c:if>
<c:set var="qmrf_create" value="" scope="session"/>


<c:set var="uname" value='${sessionScope["username"]}'/>
<c:set var="updateCount" value="0"/>

<c:set var="state" value="draft"/>
<!--
<c:choose>
	<c:when test="${empty param.submit_state}">
		<c:set var="state" value="draft"/>
	</c:when>
	<c:when test="${param.submit_state} eq 'submitted'">
		<c:set var="state" value="submitted"/>
	</c:when>
	<c:when test="${param.submit_state} eq 'draft'">
		<c:set var="state" value="draft"/>
	</c:when>
	<c:otherwise>
		<c:set var="state" value="draft"/>
	</c:otherwise>
</c:choose>
-->
<c:catch var="transactionException_update">
<sql:transaction dataSource="jdbc/qmrf_documents">
<sql:update var="updateCount" >
    INSERT INTO documents (xml,user_name,status) VALUES (?,?,?);

	<sql:param value="${param.xml}"/>
	<sql:param value="${sessionScope['username']}"/>
	<sql:param value="${state}"/>


</sql:update>
	<sql:query var="rs" sql="SELECT LAST_INSERT_ID() AS id" />
</sql:transaction>
</c:catch>
<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
  <head>
    <title>QMRF documents</title>
  </head>
  <body>


<jsp:include page="menu.jsp" flush="true"/>

<jsp:include page="menuuser.jsp" flush="true">
	    <jsp:param name="highlighted" value="user"/>
  </jsp:include>

<br>
<c:choose>
<c:when test='${not empty transactionException_update}'>
	<div class="error">
		Error on updating document <br> ${transactionException_update}
	</div>
</c:when>
<c:otherwise>
	<c:forEach var="row" items="${rs.rows}">
		New document added (${updateCount}).
		<c:set var="sql" value="select idqmrf,qmrf_number,version,user_name,updated,status from documents where user_name=? and idqmrf=${row.id}"/>
		<jsp:include page="records.jsp" flush="true">
			<jsp:param name="sql" value="${sql}"/>
			<jsp:param name="qmrf_number" value="QMRF#"/>
			<jsp:param name="version" value="Version"/>
			<jsp:param name="user_name" value="Author"/>
			<jsp:param name="updated" value="Last updated"/>
			<jsp:param name="status" value="Status"/>
			<jsp:param name="actions" value="user"/>
			<jsp:param name="sqlparam" value="${sessionScope['username']}"/>
			<jsp:param name="paging" value="false"/>
			<jsp:param name="viewpage" value="user.jsp"/>
		</jsp:include>
	</c:forEach>	
</c:otherwise>
</c:choose>

  </body>
</html>


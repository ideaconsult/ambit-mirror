<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="thispage" value='submit_update.jsp'/>


<c:if test="${empty param.id}" >
	no id
  <c:redirect url="/create.jsp"/>
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


<c:set var="state" value="draft"/>
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
		<c:set var="state" value="submitted"/>
	</c:otherwise>
</c:choose>

<c:set var="uname" value='${sessionScope["username"]}'/>
<c:set var="updateCount" value="0"/>


		<sql:update var="updateCount" dataSource="jdbc/qmrf_documents">
			    update documents set status="submitted" where idqmrf=? and user_name=?;

			  <sql:param value="${param.id}"/>
				<sql:param value="${sessionScope['username']}"/>

			</sql:update>





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

<p>
<c:out value='${updateCount}'/>	 document updated.

<c:set var="report">
	select idqmrf,qmrf_number,user_name,updated,status from documents where idqmrf = ${param.id}
</c:set>

<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${report}"/>

		<jsp:param name="qmrf_number" value="QMRF#"/>
    <jsp:param name="user_name" value="Author"/>
    <jsp:param name="updated" value="Last updated"/>
		<jsp:param name="status" value="Status"/>
		<jsp:param name="actions" value=""/>

</jsp:include>

  </body>
</html>


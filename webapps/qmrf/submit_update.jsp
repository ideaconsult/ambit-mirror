<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="thispage" value='submit_update.jsp'/>


<c:if test="${empty param.id}" >
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

<!-- Not launched from edit.jsp -->
<c:if test="${sessionScope.qmrf_update ne 'true'}">
	<c:redirect url="/user.jsp"/>
</c:if>
<c:set var="qmrf_update" value="" scope="session"/>

<c:set var="state" value="draft"/>
<!--
<c:choose>
	<c:when test="${empty param.submit_state}">
		<c:set var="state" value="draft"/>
	</c:when>
	<c:when test="${param.submit_state eq 'submitted'}">
		<c:set var="state" value="submitted"/>
	</c:when>
	<c:when test="${param.submit_state eq 'draft'}">
		<c:set var="state" value="draft"/>
	</c:when>
	<c:otherwise>
		<c:set var="state" value="draft"/>
	</c:otherwise>
</c:choose>
-->

<c:set var="uname" value='${sessionScope["username"]}'/>
<c:set var="updateCount" value="0"/>
<c:choose>
	<c:when test="${empty param.xml}" >
			<!-- Only status update -->
			<c:catch var="transactionException_update">
			<sql:transaction dataSource="jdbc/qmrf_documents">
			<sql:update var="updateCount" >
			    update documents set status=? where idqmrf=? and user_name=?;
			  <sql:param value="${state}"/>
			  <sql:param value="${param.id}"/>
				<sql:param value="${sessionScope['username']}"/>

			</sql:update>
		</sql:transaction>
		</c:catch>

	</c:when>
	<c:otherwise>

			<!-- data  update -->
		<c:catch var="transactionException_update">
		
			<c:import url="include_attachments.jsp" var="newxml" >
				<c:param name="id" value="${param.id}"/>
				<c:param name="xml" value="${param.xml}"/>
			</c:import>
			<sql:transaction dataSource="jdbc/qmrf_documents">

			<sql:update var="updateCount" >
				update documents set status=?,xml=?,updated=now() where idqmrf=? and user_name=?;
			<sql:param value="${state}"/>
			<sql:param value="${newxml}"/>
			  <sql:param value="${param.id}"/>
				<sql:param value="${sessionScope['username']}"/>
			</sql:update>
			</sql:transaction>
		</c:catch>
	</c:otherwise>
</c:choose>




<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
	<meta http-equiv="Content-Type" contentType="text/xml;charset=utf-8">
  <head>
    <title>QMRF documents</title>
  </head>
  <body>

<jsp:include page="menu.jsp" flush="true"/>

<jsp:include page="menuuser.jsp" flush="true">
    <jsp:param name="highlighted" value="user"/>
</jsp:include>

<p>

	<c:choose>
<c:when test='${not empty transactionException_update}'>
		<div class="error">
		Error on updating document ${transactionException_update}
	</div>
</c:when>
<c:otherwise>
	<c:out value='${updateCount}'/>	 document (<c:out value="${fn:length(param.xml)}"/> bytes) updated.
</c:otherwise>
</c:choose>

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

<table width="100%" border="0">
<jsp:include page="list_attachments.jsp" flush="true">
		<jsp:param name="id" value="${param.id}"/>
</jsp:include>
</table>
  </body>
</html>


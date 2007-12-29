<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<c:set var="thispage" value='update_status.jsp'/>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>
<c:if test="${sessionScope['viewmode'] ne 'qmrf_user'}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:if test="${empty param.id}" >
	no id
  <c:redirect url="/create.jsp"/>
</c:if>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isauthor']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isauthor']=='false'}" >
  <c:redirect url="/index.jsp"/>
</c:if>


<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
  <head>
    <title>QMRF documents</title>
  </head>
  <body>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="create"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>    
</jsp:include>


<c:set var="uname" value='${sessionScope["username"]}'/>
<c:set var="updateCount" value="0"/>


<c:if test="${(!empty param.verify) && (param.verify eq 'false')}">
	<c:catch var="update_error">
		<sql:update var="updateCount" dataSource="jdbc/qmrf_documents">
			update documents set status="submitted" where idqmrf=? and user_name=? and status='draft';
			<sql:param value="${param.id}"/>
			<sql:param value="${sessionScope['username']}"/>
		</sql:update>
		<blockquote>
		<div class="success">
			${updateCount}  document updated.
		</div>
		</blockquote>
	</c:catch>
	<c:if test="${!empty update_error}">
		<div class="error">
		${update_error}
		</div>
	</c:if>
</c:if>


<c:set var="sql" value="select idqmrf,qmrf_number,version,user_name,updated,status from documents where user_name=? and idqmrf=${param.id}"/>

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
		<jsp:param name="page" value="1"/>
		<jsp:param name="pagesize" value="1"/>
		<jsp:param name="viewpage" value="user.jsp"/>


</jsp:include>

<c:if test="${(empty param.verify) || (param.verify ne 'false')}">
<form>
<table width="90%">
<tr>
<td valign="top">
<div class="success">Check up: The following fields have to be filled in!</div>
	<c:catch var ="error">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,qmrf_number,xml from documents where user_name=? and idqmrf=?
			<sql:param value="${sessionScope['username']}"/>
			<sql:param value="${param.id}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<c:import var="xsl" url="/WEB-INF/xslt/qmrfvalidate.xsl"/>
			<x:transform xml="${row.xml}" xslt="${xsl}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd"/>
		</c:forEach>
	</c:catch>
</td>
<td valign="top">
<div class="success">Submit the document to QMRF Inventory</div>
<br>
<input type="hidden" name="id" value="${param.id}"/>
<input type="hidden" name="verify" value="false"/>
<input type="submit" value="Final submission">
<br>
	<div class="help">
		The document will be submitted for revision.<br> No further editing will be allowed.
	</div>
	<br>
	<div class="help">
	The following fields will be used to assign unique QMRF number:
	<p>
	4.2. Explicit algorithm
	<p>
	2.5. Model developer(s) and contact details
	<p>
	1.3. Software coding the model
	<p>
	If data is not present in these fields the document will not be published!
	</div>
</td>

</tr>
</table>
</form>
</c:if>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>
</body>
</html>


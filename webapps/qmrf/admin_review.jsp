<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<c:set var="thispage" value='admin_review.jsp'/>


<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>
<c:if test="${sessionScope['viewmode'] ne 'qmrf_admin'}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin'] eq 'false'}" >
  <c:redirect url="/user.jsp"/>
</c:if>

<c:if test="${empty param.id}" >
  <c:redirect url="/admin.jsp"/>
</c:if>

<c:catch>
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select user_name from documents where idqmrf=? and user_name != ?
		<sql:param value="${param.id}"/>
		<sql:param value="${sessionScope['username']}"/>
	</sql:query>
	<c:if test="${rs.rowCount eq 0}">
		<c:redirect url="/review_forbidden.jsp">
			<c:param name="id" value="${param.id}"/>
			<c:param name="message" value="Can't review QMRF document"/>
		</c:redirect>
	</c:if>
	</
</c:catch>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF document review"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="review"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>


<c:set var="xslt_url" value="/WEB-INF/xslt/qmrf_adminedit.xsl"/>
<c:set var="title" value="Reviewing"/>
<c:set var="submit_caption" value="Update"/>

<c:if test="${param.status eq 'published'}">
		<c:set var="xslt_url" value="/WEB-INF/xslt/qmrf_adminpublish.xsl"/>
		<c:set var="title" value="Publishing"/>
		<c:set var="submit_caption" value="Publish"/>
</c:if>
<c:if test="${param.status eq 'returned for revision'}">
		<c:set var="submit_caption" value="Return for revision"/>
</c:if>

<c:set var="updateXML" value="false"/>
<c:if test="${!empty param.QMRF_number}">
	<c:set var="updateXML" value="true"/>
</c:if>
<c:if test="${!empty param.date_publication}">
	<c:set var="updateXML" value="true"/>
</c:if>
<c:if test="${!empty param.keywords || (fn:trim(param.keywords) eq '')}">
	<c:set var="updateXML" value="true"/>
</c:if>
<c:if test="${!empty param.summary_comments || (fn:trim(param.summary_comments) eq '')}">
	<c:set var="updateXML" value="true"/>
</c:if>

<c:if test="${pageContext.request.method ne 'POST'}">
	<c:set var="updateXML" value="false"/>
</c:if>

<c:if test="${updateXML eq true}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select xml from documents where idqmrf=?
			<sql:param value="${param.id}"/>
		</sql:query>

		<c:forEach var="row" items="${rs.rows}">
			<catch var="error">
				<c:import var="qmrfToHtml" url="/WEB-INF/xslt/update_keywords_comments.xsl"/>
				<c:set var="docnew" scope="page">
					<x:transform xml="${fn:trim(row.xml)}" xslt="${qmrfToHtml}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd">
							<x:param name="keywords" value="${param.keywords}"/>
							<x:param name="summary_comments" value="${param.summary_comments}"/>
					</x:transform>
				</c:set>
			</catch>
			<c:if test="${!empty error}">
				<div class="error">
					${error}
				</div>
			</c:if>
		</c:forEach>

		<c:choose>
			<c:when test="${param.status eq 'under review'}">
				<c:catch var="error_update">
					<sql:update var="updateCount" dataSource="jdbc/qmrf_documents">
						 update documents set qmrf_number=null,xml=?,updated=now(),status='under review' where idqmrf=? and reviewer=? and (status='submitted' || status='under review')
						<sql:param value="${docnew}"/>
						<sql:param value="${param.id}"/>
						<sql:param value="${sessionScope['username']}"/>
					</sql:update>
				</c:catch>
				<c:if test="${!empty error_update}">
					<div class="error">${error_update}</div>
				</c:if>
			</c:when>
			<c:otherwise>
				<div class="error">Status ${param.status} different than expected!</div>
			</c:otherwise>
		</c:choose>

		<c:choose>
		<c:when test="${!empty error_update}">
			<div class="error">Error updating the document 	<br> ${error_update}</div>
		</c:when>
		<c:otherwise/>
		</c:choose>

</c:if>



<c:set var="report">
	select idqmrf,qmrf_number,qmrf_title,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where idqmrf = ${param.id} and (status = 'submitted' || status = 'under review')
</c:set>

<jsp:include page="records.jsp" flush="true">
	<jsp:param name="sql" value="${report}"/>
	<jsp:param name="qmrf_number" value="QMRF#"/>
	<jsp:param name="qmrf_title" value="Title"/>
    <jsp:param name="user_name" value="Author"/>
    <jsp:param name="lastdate" value="Last updated"/>
	<jsp:param name="status" value="Status"/>
	<jsp:param name="actions" value="admin"/>
</jsp:include>



<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select idqmrf,qmrf_number,xml from documents where idqmrf=? and (status = 'submitted' || status = 'under review')
	<sql:param value="${param.id}"/>
</sql:query>

<c:if test="${rs.rowCount>0}">
	<table width="100%" border="0">
		<jsp:include page="list_attachments.jsp" flush="true">
			<jsp:param name="id" value="${param.id}"/>
		</jsp:include>
	</table>

	<form method="POST" name="qmrfform" action='<%= response.encodeURL("admin_review.jsp") %>'>

		<c:forEach var="row" items="${rs.rows}">
			<c:set var="doc" value="${fn:trim(row.xml)}"/>
			<input type="hidden" name="id" value="${row.idqmrf}">
			<input type="hidden" name="status" value="${param.status}">



				<c:catch var="error">
					<c:import var="xsl" url="${xslt_url}"/>
					<x:transform xml="${doc}" xslt="${xsl}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd" />
				</c:catch>

				<c:if test="${!empty error}">
					<div class="error">ERROR ${error}</div>
				</c:if>



		</c:forEach>
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

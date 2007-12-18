<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<html>
<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
<head>
<meta name="description" content="(Q)MRF database">
<meta name="keywords" content="ambit,qsar,qmrf,structure search">
<meta name="robots"content="index,follow">
<META NAME="GOOGLEBOT" CONTENT="index,FOLLOW">
<meta name="copyright" content="Copyright 2007. Nina Jeliazkova nina@acad.bg">
<meta name="author" content="Nina Jeliazkova">
<meta name="language" content="English">
<meta name="revisit-after" content="7">
<link rel="SHORTCUT ICON" href="favicon.ico"/>
</head>
<title>(Q)SAR Model Reporting Format (QMRF) Inventory</title>
<body bgcolor="#ffffff">

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="welcome"/>
</jsp:include>



<jsp:include page="menuall.jsp" flush="true">
		    <jsp:param name="highlighted" value="welcome" />
</jsp:include>



<c:if test="${!empty sessionScope['username']}" >
		<c:catch var="exception">
				<sql:query var="rs" dataSource="jdbc/qmrf_documents">
					select title,firstname,lastname,user_name from users where user_name=?
					<sql:param value="${sessionScope['username']}"/>
				</sql:query>
		</c:catch>
		<c:if test="${empty exception}">
				<c:forEach var="row" items="${rs.rows}">
						<h3>
  					Welcome <b>${row.title}&nbsp;${row.firstname}&nbsp;${row.lastname}!</b><br/>
						</h3>
				</c:forEach>
		</c:if>
</c:if>


<c:if test="${sessionScope['isadmin']=='false'}" >

	<blockquote>
  <p>
  You are logged in as a QMRF repository user and can create and edit new QMRF documents as well as submit them for reviewing.<br/>
The submitted documents will be approved and published or returned to you for further editing if essential information is missing.<br/>
Your documents are available under the <u>My documents</u> tab. You can create a new document through the <u>New document</u> tab.<br/>
You can view and change your registration details under the <u>My profile</u> tab.
</p>
</blockquote>
</c:if>


<c:if test="${sessionScope['isadmin']=='true'}" >

	<blockquote>
  <p>
  You are logged in as an administrator and can review and publish QMRF documents.
  <br/>
  The new documents to be processed are listed under <u>Pending documents</u> tab.

	</p>
</blockquote>
</c:if>

	<blockquote>

			<c:catch var="exception">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				SELECT count(idqmrf) as no FROM documents d where status='published';
			</sql:query>
			</c:catch>

			<c:set var="no" value="0"/>
			<c:choose>
			<c:when test="${!empty exception}">
				<div class="error">
				${exception}
			</div>
			</c:when>
			<c:otherwise>

				<c:forEach var="row" items="${rs.rows}">
						<c:set var="no" value="${row.no}"/>
				</c:forEach>
			</c:otherwise>
		</c:choose>

  <c:set var="title" value="No published documents available in QMRF repository."/>
  <c:if test="${no > 0}">
  	<c:set var="title" value="All published QMRF documents <b>(${no})</b> are available for download and can be searched either through free text queries or by several predefined fields.<br>All substances, available in the QMRF repository, can be searched by exact or similar structure."/>
  </c:if>
  <p>
  ${title}
  </p>


</blockquote>

<h3>What is QMRF Inventory?</h3>
<blockquote>
<a href="help.html" target="_blank">Help</a>
</blockquote>

<h3>Most recent QMRF documents</h3>


<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="select idqmrf,qmrf_number,user_name,updated,status,xml from documents where status = 'published' order by updated desc limit 3" />

		<jsp:param name="xml" value="xml"/>
		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="updated" value="Last updated"/>

		<jsp:param name="actions" value=""/>
		<jsp:param name="viewpage" value="index.jsp"/>
		<jsp:param name="maxpages" value="10"/>
</jsp:include>

<jsp:include page="view.jsp" flush="true">
<jsp:param name="highlighted" value="${param.id}"/>
<jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>





<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>
</body>
</html>

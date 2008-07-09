<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:requestEncoding value="UTF-8"/>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="(Q)SAR Model Reporting Format (QMRF) Inventory"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="welcome"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
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


<c:if test="${sessionScope['isauthor']=='true'}" >

	<blockquote>
  <p>
  You are logged in as a QMRF Database <b>Author</b>
  <a href="help.jsp?anchor=roles" target="help" rev="help" ><img src="images/help.png" alt="help" title="Author" border="0"/></a>
   and can create and edit new QMRF documents as well as submit them for reviewing.<br/>
The submitted documents will be approved and published or returned to you for further editing if essential information is missing.
<a href="help.jsp?anchor=status" target="help" rev="help" ><img src="images/help.png" alt="help" title="Documents life cycle" border="0"/></a>
<br/>
Your documents are available under the <u>My documents</u> tab. You can create a new document through the <u>New document</u> tab.<br/>
You can view and change your registration details under the <u>My profile</u> tab.
</p>
</blockquote>
</c:if>


<c:if test="${sessionScope['isadmin']=='true'}" >

	<blockquote>
  <p>
  You are logged in as an QMRF Database <b>Reviewer</b>
  <a href="help.jsp?anchor=roles" target="help" rev="help" ><img src="images/help.png" alt="help" title="Author" border="0"/></a>
   and can review and publish QMRF documents.
  <br/>
  The new documents to be processed are listed under <u>Pending documents</u> tab.

	</p>
</blockquote>
</c:if>

<c:if test="${sessionScope['iseditor']=='true'}" >

	<blockquote>
  <p>
  You are logged in as an QMRF Database <b>Editor</b>
  <a href="help.jsp?anchor=roles" target="help" rev="help" ><img src="images/help.png" alt="help" title="Author" border="0"/></a>
   and you can assign reviewers to newly submitted documents <a href="help.jsp?anchor=status" target="help" rev="help" ><img src="images/help.png" alt="help" title="Documents life cycle" border="0"/></a>.
	</p>
</blockquote>
</c:if>


<c:if test="${sessionScope['ismanager']=='true'}" >

	<blockquote>
  <p>
  You are logged in as an QMRF Database <b>Administrator</b>
  <a href="help.jsp?anchor=roles" target="help" rev="help" ><img src="images/help.png" alt="help" title="Author" border="0"/></a>
   which entitles you to running various system reports and statistics.
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

  <c:set var="title" value="No published documents available in QMRF Database."/>
  <c:if test="${no > 0}">
  	<c:set var="title" value="All published QMRF documents <b>(${no})</b> are available for download and can be searched either through free text queries or by several predefined fields.<br>All substances, available in the QMRF Database, can be searched by exact or similar structure."/>
  </c:if>
  <p>
  ${title}
  </p>


</blockquote>

<h3>What is QMRF Database?</h3>
<blockquote>
<a href="help.html" target="_blank">Help</a>
</blockquote>

<h3>How to create an QMRF Document?</h3>
<blockquote>
<ul>
<li>log in into QMRF Database and use the <i>New document</i> tab;
<li>by <a href="applets/qmrfeditor.jnlp">QMRF editor</a>&nbsp;: once started, it will create shortcut on your desktop and can be started later even offline.
</ul>
</blockquote>

<h3>Most recent QMRF documents</h3>


<c:set var="sql">
	select idqmrf,qmrf_number,qmrf_title,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where status = 'published' order by updated desc limit 3
</c:set>

<c:set var="maxpages" scope="session" value="1"/>
<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${sql}" />

		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="qmrf_title" value="Title"/>		
		<jsp:param name="lastdate" value="Last updated"/>

		<jsp:param name="paging" value="false"/>
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
</div>
</body>
</html>

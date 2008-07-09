<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/taglibs-mailer" prefix="mt" %>

<c:set var="thispage" value=''/>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>
<c:if test="${sessionScope['viewmode'] ne 'qmrf_editor'}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['iseditor']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['iseditor']=='false'}" >
  <c:redirect url="/index.jsp"/>
</c:if>

<jsp:include page="query_settings.jsp" flush="true"/>

<c:if test="${!empty param.status && (sessionScope.record_status ne param.status)}">
	<c:set var="page" value="0" scope="session"  />
</c:if>

<c:set var="startpage" value="${sessionScope.page}"/>
<c:set var="startrecord" value="${sessionScope.page * sessionScope.pagesize}"/>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database Editor: Assign a reviewer"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="editor"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>

<c:if test="${(!empty param.id) && (!empty param.reviewer)}">
		<c:catch var ="err">
			<sql:update var="updateCount" dataSource="jdbc/qmrf_documents">
				update documents set reviewer=? where idqmrf=? and (user_name != ?) and ((reviewer is null) || (reviewer != ?))
				<sql:param value="${param.reviewer}"/>
				<sql:param value="${param.id}"/>
				<sql:param value="${param.reviewer}"/>
				<sql:param value="${param.reviewer}"/>
			</sql:update>
		</c:catch>
		<c:if test="${updateCount > 0}">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
					select title,firstname,lastname,email from users where user_name=?
					<sql:param value="${param.reviewer}"/>
			</sql:query>

			<c:set var="subject">Reviewing assignment</c:set>	
			<c:set var="message">
You have been appointed as a reviewer of a QMRF document.
Please perform your reviewing duties at your earliest convenience.
			</c:set>
			<c:forEach var="row" items="${rs.rows}">
			
				<mt:mail server="${initParam['mail-server']}" >
					<mt:from>${initParam['mail-from']}</mt:from>
					<mt:setrecipient type="to">${row.email}</mt:setrecipient>
				<mt:subject>[QMRF Database] ${subject}</mt:subject>

					<mt:message>
<jsp:include page="mail.jsp" flush="true">
	    <jsp:param name="title" value="${row.title}"/>
	    <jsp:param name="firstname" value="${row.firstname}"/>
	    <jsp:param name="lastname" value="${row.lastname}"/>        
	    <jsp:param name="text" value="${message}"/>
</jsp:include>	 					
					</mt:message>
					<mt:send>
						<mt:error id="err">
							<jsp:getProperty name="err" property="error"/>
						</mt:error>
					</mt:send>
				</mt:mail>
				<div class="success">
					<b>${param.reviewer}</b> assigned as a reviewer, an email has been sent to the reviewer at ${row.email}.
				</div>
			</c:forEach>

		</c:if>
		<c:if test="${!empty err}">
			<div class="error">
			${err}
		</div>
		</c:if>
</c:if>

<c:set var="sql" value="select idqmrf,qmrf_number,version,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status,reviewer,xml from documents where  idqmrf=${param.id}"/>
<!--
select idqmrf,qmrf_number,qmrf_title,version,user_name,updated,status from documents where status != 'published' && status != 'draft' && status != 'archived' order by ${sessionScope.order} ${sessionScope.order_direction}
-->
<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${sql}"/>

		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="qmrf_title" value="Title"/>		
		<jsp:param name="version" value="Version"/>
		<jsp:param name="xml" value="xml"/>
		<jsp:param name="user_name" value="Author"/>
		<jsp:param name="lastdate" value="Last updated"/>
		<jsp:param name="status" value="Status"/>
		<jsp:param name="reviewer" value="Reviewer"/>
		<jsp:param name="actions" value="editor"/>
		<jsp:param name="paging" value="false"/>
		<jsp:param name="viewpage" value="assign_reviewer.jsp"/>
		<jsp:param name="viewmode" value="html"/>
</jsp:include>

<c:if test="${!empty param.id}">

	<c:catch var ="err">
		<sql:query var="rs" dataSource="jdbc/tomcat_users">
			SELECT d.user_name,email,title,firstname,lastname,affiliation,email,country,keywords,webpage FROM user_roles join qmrf_documents.users as d using(user_name) where role_name="qmrf_admin"
		</sql:query>
		<form method="POST">
			<table border="0" width="90%" bgcolor="${sessionScope.tablecolor}">
			<tr bgcolor="${sessionScope.headercolor}"><th colspan="7">Reviewers list</th></tr>
			<tr bgcolor="${sessionScope.headercolor}"><th></th>
				<th>Name</th>
				<th>E-mail</th>
				<th>Keywords</th>
				<th>Affiliation</th>
				<th>Country</th>
				<th>WWW</th>

			</tr>

		<c:forEach var="row" items="${rs.rows}">
		<tr bgcolor="${sessionScope.querycolor}">
		<td width="5%">
		<c:set var="checked" value=""/>
		<c:if test="${param.reviewer eq row.user_name}">
			<c:set var="checked" value="checked"/>
		</c:if>

		<input type="radio" name="reviewer" value="${row.user_name}" ${checked}/>
		</td>
		<td>${row.title}&nbsp;${row.firstname}&nbsp;${row.lastname}</td>
		<td>${row.email}</td>
		<td>${row.keywords}</td>
		<td>${row.affiliation}</td>
		<td>${row.country}</td>
		<td>${row.webpage}</td>
		</tr>
		</c:forEach>
		<tr>
			<td colspan="8"><input type="submit" value="Assign a reviewer"/></td>
		</tr>
	</form>
	</c:catch>
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

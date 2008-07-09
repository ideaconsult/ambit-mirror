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

<c:if test="${empty param.id}" >
  <c:redirect url="/editor.jsp"/>
</c:if>

<c:if test="${param.confirm eq 'NO'}" >
  <c:redirect url="/editor.jsp"/>
</c:if>

<jsp:include page="query_settings.jsp" flush="true"/>

<c:if test="${!empty param.status && (sessionScope.record_status ne param.status)}">
	<c:set var="page" value="0" scope="session"  />
</c:if>

<c:set var="startpage" value="${sessionScope.page}"/>
<c:set var="startrecord" value="${sessionScope.page * sessionScope.pagesize}"/>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database: Delete document"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="editor"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>

<!-- TODO delete history -->

<!-- <c:set var="documentdeleted" value="${param.confirm}"/> -->
<c:if test="${(!empty param.id) && (!empty param.confirm)  && (param.confirm eq 'YES')}">
		<!-- remember user and reviewer's contact details -->
		<c:set var="user_title" value=""/>
		<c:set var="user_firstname" value=""/>		
		<c:set var="user_lastname" value=""/>
		<c:set var="user_email" value=""/>				
		<c:set var="qmrf_title" value=""/>				
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select title,firstname,lastname,email,qmrf_title from users join documents using(user_name) where idqmrf=?
			<sql:param value="${param.id}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">		
			<c:set var="user_title" value="${row.title}"/>
			<c:set var="user_firstname" value="${row.firstname}"/>		
			<c:set var="user_lastname" value="${row.lastname}"/>
			<c:set var="user_email" value="${row.email}"/>				
			<c:set var="qmrf_title" value="${row.qmrf_title}"/>							
		</c:forEach>

		<c:set var="reviewer_title" value=""/>
		<c:set var="reviewer_firstname" value=""/>		
		<c:set var="reviewer_lastname" value=""/>
		<c:set var="reviewer_email" value=""/>				
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select title,firstname,lastname,email from users where user_name in (select reviewer  from documents where idqmrf=?)
			<sql:param value="${param.id}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">		
			<c:set var="reviewer_title" value="${row.title}"/>
			<c:set var="reviewer_firstname" value="${row.firstname}"/>		
			<c:set var="reviewer_lastname" value="${row.lastname}"/>
			<c:set var="reviewer_email" value="${row.email}"/>				
		</c:forEach>
		
		<c:catch var ="err">
			<sql:transaction dataSource="jdbc/qmrf_documents">
			<sql:update var="updateCountAtt">
				delete from attachments where idqmrf=?
				<sql:param value="${param.id}"/>
			</sql:update>
			<sql:update var="updateCountDoc">
				delete from documents where idqmrf=?
				<sql:param value="${param.id}"/>
			</sql:update>
			
			</sql:transaction>

		</c:catch>
		
		${updateCountDoc}
		<c:choose>
		<c:when test="${empty err}">
			<c:set var="documentdeleted" value="${param.id}"/>
		</c:when>
		<c:otherwise>
			<div class="error">${err}</div>
		</c:otherwise>		
		</c:choose>
		
		<c:if test="${updateCountDoc > 0}">
			<c:if test="{!empty reviewer_email}">
				<c:set var="subject">Reviewing assignment cancelled</c:set>	
				<c:set var="message">
	The QMRF document you have been appointed as a reviewer is removed ftom the inventory. 
	Your reviewing assignment is cancelled.
				</c:set>
				
				<mt:mail server="${initParam['mail-server']}" >
						<mt:from>${initParam['mail-from']}</mt:from>
						<mt:setrecipient type="to">${reviewer_email}</mt:setrecipient>
				<mt:subject>[QMRF Database] ${subject}</mt:subject>
	
				<mt:message>
					<jsp:include page="mail.jsp" flush="true">
						    <jsp:param name="title" value="${reviewer_title}"/>
						    <jsp:param name="firstname" value="${reviewer_firstname}"/>
						    <jsp:param name="lastname" value="${reviewer_lastname}"/>        
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
					An email has been sent to the reviewer at ${reviewer_email}.
				</div>
			</c:if>
<!-- Notify the author -->			

			<c:if test="{!empty user_email}">
				<c:set var="subject">QMRF document deleted</c:set>	
				<c:set var="message">
	The QMRF document authored by you is removed ftom the inventory. 
				</c:set>
				
				<mt:mail server="${initParam['mail-server']}" >
						<mt:from>${initParam['mail-from']}</mt:from>
						<mt:setrecipient type="to">${user_email}</mt:setrecipient>
				<mt:subject>[QMRF Database] ${subject}</mt:subject>
	
				<mt:message>
					<jsp:include page="mail.jsp" flush="true">
						    <jsp:param name="title" value="${user_title}"/>
						    <jsp:param name="firstname" value="${user_firstname}"/>
						    <jsp:param name="lastname" value="${user_lastname}"/>        
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
					An email has been sent to the author at ${user_email}.
				</div>
			</c:if>			
		</c:if>
		<c:if test="${!empty err}">
			<div class="error">
			${err}
		</div>
		</c:if>
</c:if>
<c:choose>
<c:when test="${empty documentdeleted}">
	<c:set var="sql" value="select idqmrf,qmrf_number,version,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status,reviewer,xml from documents where  idqmrf=${param.id}"/>
	
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
	
			<table border="0" width="50%" bgcolor="${sessionScope.headercolor}">
			<tr bgcolor="${sessionScope.headercolor}">
			<th align="center" colspan="2">The document will be deleted! Please confirm:</th></tr>
			<tr align="center" bgcolor="${sessionScope.headercolor}">
			
			<td align="right">
				<form method="POST">
					<input type="hidden" name="id" value="${param.id}" />
					<input type="hidden" name="confirm" value="YES" />
					<input type="submit" value="   OK   "/>
				</form>			
			</td>
			<td align = "left">
				<form method="POST">
					<input type="hidden" name="id" value="${param.id}" />			
					<input type="hidden" name="confirm" value="NO" />
					<input type="submit" value="Cancel"/>
				</form>			
			</td>
			
			</tr>
			</form>		
		</c:catch>
	</c:if>	
</c:when>
<c:otherwise>
	<div class="success">
	The document ${qmrf_title} is removed from QMRF Database.
	</div>
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


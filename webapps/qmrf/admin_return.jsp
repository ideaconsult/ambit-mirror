<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/taglibs-mailer" prefix="mt" %>
<fmt:requestEncoding value="UTF-8"/>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>
<c:if test="${sessionScope['viewmode'] ne 'qmrf_admin'}" >
  <c:redirect url="index.jsp"/>
</c:if>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database: Return QMRF document for revision"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="review"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>

<c:set var="thispage" value='admin_return.jsp'/>


<c:if test="${empty param.id}" >
  <c:redirect url="/admin.jsp"/>
</c:if>

<c:if test="${empty param.status}" >
  <c:redirect url="/admin.jsp"/>
</c:if>


<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin']=='false'}" >
  <c:redirect url="/user.jsp"/>
</c:if>


<c:set var="sql_report" value="select idqmrf,qmrf_number,qmrf_title,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where idqmrf_origin = ${param.id}"/>

<!-- returned for revision' -->
<!-- copy the document into new one, increment version and set archive status to the odler one -->
<c:choose>
	<c:when test="${empty param.status}">
			Status not defined
</c:when>
	<c:when test="${param.status eq 'review completed'}">
		<c:catch var="transactionException_review">
				<sql:transaction dataSource="jdbc/qmrf_documents">
				<sql:update var="updateCount">
		    		update documents set status='review completed' where idqmrf=?
			  		<sql:param value="${param.id}"/>
				</sql:update>
			</sql:transaction>
		</c:catch>	
		<c:if test="${empty transactionException_review}">
			<c:catch var="transactionException_review">
				<sql:query var="rs" dataSource="jdbc/qmrf_documents">
					SELECT idqmrf,qmrf_title,d.user_name,title,firstname,lastname,email,date_format(updated,'%Y-%c-%d') as qdate,date_format(updated,'%H:%i') as qtime FROM documents as d join users using(user_name) where idqmrf=?
					<sql:param value="${param.id}"/>
				</sql:query>


				<c:forEach var="row" items="${rs.rows}">
					<sql:query var="rseditor" dataSource="jdbc/qmrf_documents">
						SELECT title,user_name,firstname,lastname,email FROM qmrf_documents.users join tomcat_users.user_roles u using(user_name) where role_name='qmrf_editor'
					</sql:query>
					<c:forEach var="editor" items="${rseditor.rows}">
					<mt:mail server="${initParam['mail-server']}" >
						<mt:from>${initParam['mail-from']}</mt:from>
						<mt:setrecipient type="to">${editor.email}</mt:setrecipient>
						<mt:subject>[QMRF Database] Review completed</mt:subject>

						<mt:message>
<jsp:include page="mail_reviewcompleted.jsp" flush="true">
    <jsp:param name="title" value="${editor.title}"/>
    <jsp:param name="firstname" value="${editor.firstname}"/>
    <jsp:param name="lastname" value="${editor.lastname}"/>        
    <jsp:param name="qdate" value="${row.qdate}"/>
    <jsp:param name="qtime" value="${row.qtime}"/>    
</jsp:include>							
						</mt:message>
						<mt:send>
							<mt:error id="err">
						         <jsp:getProperty name="err" property="error"/>

						     </mt:error>
						</mt:send>
					</mt:mail>
					</c:forEach>
				</c:forEach>
			</c:catch>
		</c:if>
		
	</c:when>
	<c:when test="${param.status eq 'returned for revision'}">

				<c:catch var="transactionException_archive">
				<sql:transaction dataSource="jdbc/qmrf_documents">
				<sql:update var="updateCount">
						insert into documents (idqmrf_origin,qmrf_title,user_name,xml,version,status,updated,reviewer)
				    select idqmrf,qmrf_title,user_name,xml,version+1,'returned for revision',now(),reviewer from documents where idqmrf=?;
				  <sql:param value="${param.id}"/>
				  </sql:update>
					<sql:update var="updateCount">
					    update attachments set idqmrf=(SELECT LAST_INSERT_ID()) where idqmrf=?
					  <sql:param value="${param.id}"/>
					</sql:update>

				<sql:update var="updateCount">
		    		update documents set status='archived',qmrf_number=null where idqmrf=?
		  		<sql:param value="${param.id}"/>
				</sql:update>

			</sql:transaction>
			</c:catch>

			<c:if test="${empty transactionException_archive}">
				<c:catch var="transactionException_archive">
					<sql:query var="rs" dataSource="jdbc/qmrf_documents">
						SELECT idqmrf,qmrf_title,d.user_name,title,firstname,lastname,email,date_format(updated,'%Y-%c-%d') as qdate,date_format(updated,'%H:%i') as qtime FROM documents as d join users using(user_name) where idqmrf=?
						<sql:param value="${param.id}"/>
					</sql:query>

					<c:forEach var="row" items="${rs.rows}">

						<mt:mail server="${initParam['mail-server']}" >
							<mt:from>${initParam['mail-from']}</mt:from>
							<mt:setrecipient type="to">${row.email}</mt:setrecipient>
							<mt:subject>[QMRF Database] Document returned for revision</mt:subject>

							<mt:message>
<jsp:include page="mail_return.jsp" flush="true">
    <jsp:param name="title" value="${row.title}"/>
    <jsp:param name="firstname" value="${row.firstname}"/>
    <jsp:param name="lastname" value="${row.lastname}"/>        
    <jsp:param name="qdate" value="${row.qdate}"/>
    <jsp:param name="qtime" value="${row.qtime}"/>    
</jsp:include>							
</mt:message>
						    <mt:send>
						    	<mt:error id="err">
						         <jsp:getProperty name="err" property="error"/>

						       </mt:error>
							</mt:send>
						</mt:mail>
					</c:forEach>
				</c:catch>
			</c:if>

</c:when>
	<c:when test="${param.status eq 'published'}">
		<c:set var="sql_report" value="select idqmrf,qmrf_number,qmrf_title,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where idqmrf = ${param.id}"/>
			<c:set var="updateXML" value="true"/>
			<c:if test="${empty param.A}">
				<c:set var="updateXML" value="false"/>
				<div class="error">
						Field <b>4.2. Explicit algorithm</b> not defined!
				</div>
			</c:if>
			<c:if test="${empty param.B}">
				<c:set var="updateXML" value="false"/>
				<div class="error">
					Field <b>2.5. Model developer(s) and contact details</b> not defined!
				</div>
			</c:if>
			<c:if test="${empty param.C}">
				<c:set var="updateXML" value="false"/>
				<div class="error">
						Field <b>3.2. Endpoint</b> not defined!
				</div>
			</c:if>
			<c:if test="${empty param.D}">
				<c:set var="updateXML" value="false"/>
				<div class="error">
						Field <b>1.3. Software coding the model</b> not defined!
				</div>
			</c:if>
			<c:choose>
			<c:when	 test="${updateXML eq true}">
					<sql:query var="rs" dataSource="jdbc/qmrf_documents">
						select idqmrf,xml from documents where idqmrf=?
						<sql:param value="${param.id}"/>
					</sql:query>

					<jsp:useBean id="now" class="java.util.Date"/>
					<fmt:formatDate value="${now}" type="DATE" pattern="yyyy/MM/dd" var="fmt_now"/>

					<c:set var="qnumber" value="Q${param.A}-${param.B}-${param.D}-${param.C}"/>

					<c:forEach var="row" items="${rs.rows}">
						<c:import var="qmrfToHtml" url="/WEB-INF/xslt/update_number.xsl"/>
					<c:set var="docnew" scope="page">
				  <x:transform xml="${row.xml}" xslt="${qmrfToHtml}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd">
				  	<x:param name="qnumber" value="${qnumber}"/>
						<x:param name="qdate" value="${fmt_now}"/>
				  </x:transform>
					</c:set>

					<c:catch var="transactionException_archive">
						<sql:update var="updateCount" dataSource="jdbc/qmrf_documents">
		   					 update documents set qmrf_number=?,xml=?,updated=now(),status='published' where idqmrf=?
		   			<sql:param value="${qnumber}"/>
		  			<sql:param value="${docnew}"/>
		  			<sql:param value="${param.id}"/>
						</sql:update>
						</c:catch>

					</c:forEach>
		</c:when>
		<c:otherwise>
			<c:set var="sql_report" value="select idqmrf,qmrf_number,qmrf_title,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where idqmrf = ${param.id}"/>
	</c:otherwise>
		</c:choose>

</c:when>
<c:otherwise>
		<c:catch var="transactionException_archive">
		<sql:update var="updateCount" dataSource="jdbc/qmrf_documents">
		    update documents set status=?,updated=now() where idqmrf=?
			<sql:param value="${param.status}"/>
		  <sql:param value="${param.id}"/>

		</sql:update>
		</c:catch>
</c:otherwise>
</c:choose>

<!-- Final check -->
<c:choose>
		<c:when test="${!empty transactionException_archive}">
			<div class="error">
			${param.status}		Error making copy of the document <br> ${transactionException_archive}
			</div>
		</c:when>
<c:otherwise>


	<jsp:include page="records.jsp" flush="true">
	<jsp:param name="sql" value="${sql_report}"/>
		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="qmrf_title" value="Title"/>
		<jsp:param name="user_name" value="Author"/>
		<jsp:param name="lastdate" value="Last updated"/>
		<jsp:param name="status" value="Status"/>
		<jsp:param name="actions" value="admin"/>
	</jsp:include>
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select d.idqmrf,qmrf_number,name,format,description,type,a.updated,idattachment,original_name,imported,status from documents as d join attachments as a using(idqmrf) where d.idqmrf=? and type != 'document' and imported=0;
		<sql:param value="${param.id}"/>
	</sql:query>
	<c:if test="${rs.rowCount > 0}">
		<blockquote>
		Structures from ${rs.rowCount} attachment(s) are not yet imported into database.
		Please click on <img src="images/import.png" height="16" width="16" alt="Import structures" border="0"/>
		on each attachment in order to import the structures.
		On successful import the icon will change to <img src="images/benzene.gif" height="16" width="16" alt="View structures" border="0"/>.
		</blockquote>
	</c:if>
	<table width="100%" border="0">
		<jsp:include page="list_attachments.jsp" flush="true">
			<jsp:param name="id" value="${param.id}"/>
		</jsp:include>
	</table>

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


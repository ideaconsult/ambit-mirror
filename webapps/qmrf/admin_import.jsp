<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ambit" prefix="a" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database: Import structures"/>
</jsp:include>


<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="import"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>


<c:set var="thispage" value='admin_import.jsp'/>


<c:if test="${empty param.id}" >
  <c:redirect url="/admin.jsp"/>
</c:if>

<c:if test="${empty param.idattachment}" >
  <c:redirect url="/admin.jsp"/>
</c:if>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin'] && empty sessionScope['iseditor'] && empty sessionScope['ismanager']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${(sessionScope['isadmin']=='false') && (sessionScope['iseditor']=='false') && (sessionScope['ismanager']=='false')}" >
  <c:redirect url="/user.jsp"/>
</c:if>

<c:set var="ref" value="${pageContext.request.contextPath}/admin_import.jsp" />
<c:if test="${(pageContext.request.method eq 'POST') && (param.action eq 'Import') && (ref eq pageContext.request.requestURI)}">
		<c:import var="xml" url="properties_xml.jsp">
			<c:param name="idattachment" value="${param.idattachment}"/>
			<c:param name="url" value="file"/>			
		</c:import>
  	    <c:catch var='exception'>
			Importing...&nbsp;${row.type}&nbsp;${row.name}...
			<a:importfile xml="${fn:trim(xml)}" user="lri_admin" password="lri" database="ambit_qmrf"/>
			Done.
		</c:catch>
		${exception}
</c:if>


<c:set var="report">
	select idqmrf,qmrf_number,qmrf_title,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where idqmrf = ${param.id}
</c:set>

<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${report}"/>

	<jsp:param name="qmrf_number" value="QMRF#"/>
	<jsp:param name="qmrf_title" value="Title"/>	
    <jsp:param name="user_name" value="Author"/>
    <jsp:param name="lastdate" value="Last updated"/>
	<jsp:param name="status" value="Status"/>
	<jsp:param name="actions" value=""/>
	<jsp:param name="paging" value="false"/>
</jsp:include>

  <table border="0" cellspacing="5" width="95%">
  <c:catch var="err">
<jsp:include page="list_attachments.jsp" flush="true"/>
</c:catch>
</table>
${err}


<table width="95%">
<tr>
<td>
  <jsp:include page="fileproperties.jsp" flush="true">
	<jsp:param name="id" value="${param.id}"/>
	<jsp:param name="idattachment" value="${param.idattachment}"/>	  
  	<jsp:param name="name" value="${param.name}"/>
  </jsp:include>
</td>
<td valign="top" width="15%">
	<form method="POST" name="import" action="">
		<input type="submit" value="Import structures">
		<input type="hidden" name="action" value="Import">
	</form>
</td>
</tr>
</table>




<!--
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select d.idqmrf,type,name from documents as d join attachments as a using(idqmrf) where d.idqmrf=? and a.idattachment=?
		<sql:param value="${param.id}"/>
		<sql:param value="${param.idattachment}"/>
	</sql:query>

	<c:forEach var="row" items="${rs.rows}">
		  <c:catch var='exception'>
			Importing&nbsp;${row.type}&nbsp;${row.name}
			<a:importfile filename="${initParam['attachments-dir']}/${row.name}" datasetname="${row.name}" user="lri_admin" password="lri" database="ambit_qmrf"/>


			<sql:transaction dataSource="jdbc/qmrf_documents">
		    <sql:update>
			    UPDATE attachments set imported=1 where idattachment=?;
				<sql:param value="${param.idattachment}"/>
				 </sql:update>
			</sql:transaction>
			</c:catch>
			<c:choose>
							<c:when test="${!empty exception}">
							<div class="error">
								Error on importing structures <b>${row.name}</b>
									<br> ${exception}
							</div>
	  					</c:when>
	  					<c:otherwise>
	  						<div class="success">
								Structures from ${row.name} imported sucessfully.
	  					  </div>
	  				</c:otherwise>
						</c:choose>
	</c:forEach>
-->


<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true"/>
	</p>
</div>
 </body>
</html>


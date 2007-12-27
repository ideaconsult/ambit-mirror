<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="/ambit" prefix="a" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
  <head>
    <title>QMRF documents</title>
  </head>
  <body>



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

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin']=='false'}" >
  <c:redirect url="/user.jsp"/>
</c:if>



<c:set var="report">
	select idqmrf,qmrf_number,user_name,a.updated,status from documents join attachments as a using(idqmrf) where a.idqmrf = ${param.id} and a.imported=0
</c:set>

<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${report}"/>

		<jsp:param name="qmrf_number" value="QMRF#"/>
    <jsp:param name="user_name" value="Author"/>
    <jsp:param name="updated" value="Last updated"/>
		<jsp:param name="status" value="Status"/>
		<jsp:param name="actions" value=""/>

		<jsp:param name="paging" value="false"/>
</jsp:include>



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

  </body>
</html>


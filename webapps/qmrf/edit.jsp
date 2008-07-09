<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<c:set var="thispage" value='edit.jsp'/>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>
<c:if test="${sessionScope['viewmode'] ne 'qmrf_user'}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isauthor']}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:if test="${sessionScope['isauthor'] eq 'false'}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:if test="${empty param.id}" >
  <c:redirect url="user.jsp"/>
</c:if>

<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
	<meta http-equiv="Content-Type" contentType="text/xml;charset=utf-8">
  <head>
    <title>QMRF documents</title>
<SCRIPT>
function getXML(){
	document.getElementById("qmrfform").xml.value = document.QMRFApplet.getXML();
	return document.qmrfform.xml.value;
}
</SCRIPT>
</head>
<body>

<!--
   document.getElementById("qmrfform").xml.value = document.QMRFApplet.getXML();
   alert(document.qmrfform.xml.value);
   return document.qmrfform.xml.value;

-->

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="create"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>    
</jsp:include>



	<c:set var="qmrf_update" value="true" scope="session"/>

<c:set var="sql" value="select idqmrf,qmrf_number,qmrf_title,version,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where user_name=? and idqmrf=${param.id}"/>

<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${sql}"/>

		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="qmrf_title" value="Title"/>		
		<jsp:param name="version" value="Version"/>
		<jsp:param name="user_name" value="Author"/>
		<jsp:param name="lastdate" value="Last updated"/>
		<jsp:param name="status" value="Status"/>
		<jsp:param name="actions" value="user"/>

		<jsp:param name="sqlparam" value="${sessionScope['username']}"/>

		<jsp:param name="paging" value="false"/>
		<jsp:param name="page" value="1"/>
		<jsp:param name="pagesize" value="1"/>
		<jsp:param name="viewpage" value="user.jsp"/>


</jsp:include>

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select idqmrf,qmrf_number,xml,status from documents where idqmrf=? and user_name=? limit 1
	<sql:param value="${param.id}"/>
	<sql:param value="${sessionScope['username']}"/>

</sql:query>
  <form method="POST" id="qmrfform" name="qmrfform" action='<%= response.encodeURL("submit_update.jsp") %>' onSubmit="return getXML()">
  <table border="0" cellspacing="5" border="1">
	<c:forEach var="row" items="${rs.rows}">
		<c:set var="data" value="${row.xml}"/>

	<tr>

	<td align="left" colspan="2">
		<input type="hidden" name="id" value="${row.idqmrf}">

    <c:set var="u">http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}</c:set>

      <c:set var="dataurl">
      	<c:url value="${u}/download_xml.jsp"> <c:param name="id" value="${row.idqmrf}"/>

				</c:url>
    	</c:set>

				<c:set var="external">
      	<c:url value="${u}/catalogs_xml.jsp"> <c:param name="all" value="true"/>
				</c:url>
    	</c:set>

		<c:set var="dtd">
			<c:url value="${u}/qmrf.dtd"/>
		</c:set>

		<applet code="ambit.applets.QMRFApplet"
			archive="applets/ambit/QMRFApplet.jar"	name="QMRFApplet" width="800" height="650">
				<param  name="xmlcontent" value="${dataurl}"/>
				<param  name="external" value="${external}"/>
				<param name="user" value="user"/>
				<param name="cleancatalogs" value="true"/>
				<param name="readonly-attachments" value="true"/>
			Applet not supported by browser.
		</applet>
		</td>
          <td valign="top">
    	<input type="hidden" id="xml" name="xml" value="" checked>
    	<!--
      <input type="radio" name="submit_state" value="draft" checked>Submit as draft<br>
      <input type="radio" name="submit_state" value="submitted">Final submission (no further editing will be allowed).<br>
      -->

		<div class="success">Edit document by QMRF Editor</div>
      <input type="submit" value="Save as draft">
      <c:if test="${row.status eq 'returned for revision'}">
            <br>
      <div class="error">
      The document has been returned for revision. Please pay attention to Section 10 of the document!
      </div>
      </c:if>
      <br>
      <div class="help">
      Click <u>Save as draft</u> when ready with filling in QMRF document. This is REQUIRED in order to update the QMRF document in the inventory.
      </div>
      <br>
      <div class="help">
      After updating the document, the next screen will provide fields to browse and select files to be attached to this document.
      </div>
		<br>
      <div class="help">
      NOTE: Using <i>File/Save</i> menu from within editor will only save the document on your local machine and will NOT update the QMRF Database.
      </div>
      <br>
      <div class="help">
      NOTE: By using links <u>Edit</u>, <u>Attachments</u> or <u>Submit</u> from this page, the current changes in QMRF document will be LOST!
      </div>


          </td>
    </tr>
    </tr>

  </c:forEach>
  </table>
</form>

  </body>
</html>


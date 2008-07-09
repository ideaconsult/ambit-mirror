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

<c:set var="thispage" value='admin_edit.jsp'/>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>
<c:if test="${sessionScope['viewmode'] ne 'qmrf_admin'}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
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

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="review"/>
</jsp:include>


<c:choose>
	<c:when test="${empty param.xml}" >

	</c:when>
	<c:otherwise>

			<!-- data  update -->
		<c:catch var="transactionException_update">
			<c:catch var="title_err">
				<x:parse xml="${param.xml}" var="doc" systemId="/WEB-INF/xslt/qmrf.dtd"/>
				<c:set var="qmrf_tmp">
					<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QSAR_identifier/QSAR_title"/>
				</c:set>
				<x:parse xml="${fn:trim(qmrf_tmp)}" var="doc"/>
				<c:set var="qmrf_title">
					<x:out select="$doc//body"/>
				</c:set>
				<c:set var="qmrf_title" value="${fn:trim(qmrf_title)}"/>
			</c:catch>
			<c:if test="${!empty title_err}">
				<c:set var="qmrf_title" value=""/>
			</c:if>

			<c:import url="include_attachments.jsp" var="newxml" >
				<c:param name="id" value="${param.id}"/>
				<c:param name="xml" value="${param.xml}"/>
			</c:import>
			<sql:transaction dataSource="jdbc/qmrf_documents">

			<sql:update var="updateCount" >
				update documents set qmrf_title=substring(?,1,128),xml=?,updated=now() where idqmrf=? and status='under review' and reviewer = ?
				<sql:param value="${qmrf_title}"/>
				<sql:param value="${newxml}"/>
			  <sql:param value="${param.id}"/>
				<sql:param value="${sessionScope['username']}"/>
			</sql:update>
			<div class="success">
					Document updated.
			</div>
			</sql:transaction>
		</c:catch>
	</c:otherwise>
</c:choose>

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


		<c:if test="${!empty transactionException_update}">
			<div class="error">
					${transactionException_update}
					<br>
					${newxml}
			</div>
		</c:if>

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select idqmrf,qmrf_number,xml from documents where idqmrf=? and status = 'under review'
	<sql:param value="${param.id}"/>
</sql:query>

<c:if test="${rs.rowCount>0}">
 <form method="POST" id="qmrfform" name="qmrfform" action='<%= response.encodeURL("admin_edit.jsp") %>' onSubmit="return getXML()">
  <table border="0" cellspacing="5" border="1">
	<c:forEach var="row" items="${rs.rows}">
		<c:set var="data" value="${row.xml}"/>

	<tr>

	<td align="left" colspan="2">
		<input type="hidden" name="id" value="${row.idqmrf}">

    <c:set var="u">${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}</c:set>

      <c:set var="dataurl">
      	<c:url value="${u}/download_xml.jsp"> <c:param name="id" value="${row.idqmrf}"/>
					<c:param name="action" value="noattachments"/>
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
				<param name="user" value="admin"/>
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
      <input type="submit" value="Update">
      <c:if test="${row.status eq 'returned for revision'}">
            <br>
      <div class="error">
      The document has been returned for revission. Please pay attention to Section 10 of the document!
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
      NOTE: Using <i>File/Save</i> menu from within editor will only save the document on your local machine and will NOT update QMRF Database.
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
</c:if>

<div id="hits">
	<p>
	<jsp:include page="hits.jsp" flush="true"/>
	</p>
</div>
</body>
</html>

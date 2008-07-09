<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<c:set var="thispage" value='create.jsp'/>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isauthor']}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:if test="${sessionScope['isauthor']=='false'}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:if test="${sessionScope['viewmode'] ne 'qmrf_user'}" >
  <c:redirect url="index.jsp"/>
</c:if>

<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
	<meta http-equiv="Content-Type" contentType="text/xml;charset=utf-8">
  <head>
    <title>QMRF documents</title>
<SCRIPT>
function getXML(){
   document.qmrfform.xml.value = document.QMRFApplet.getXML();

   return trim(document.qmrfform.xml.value);
   }
</SCRIPT>
  </head>
  <body>


<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="create"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>



<c:set var="u">${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}</c:set>

	<c:set var="dataurl">
		<c:url value="${u}/download_xml.jsp">
			<c:param name="action" value="noattachments"/>
		</c:url>
	</c:set>

	<c:set var="external">
		<c:url value="${u}/catalogs_xml.jsp">
			<c:param name="all" value="true"/>
		</c:url>
	</c:set>

	<c:set var="dtd">
		<c:url value="${u}/qmrf.dtd">
		</c:url>
	</c:set>

	<c:set var="qmrf_create" value="true" scope="session"/>


<form method="POST" action='<%= response.encodeURL("submit.jsp") %>' method='POST' name="qmrfform" onSubmit="return getXML()" >
<table border="0" cellspacing="5">
<tr>

      <td align="center" colspan="2">
      	<input type="hidden" NAME="xml">
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

      <td valign="top" align="left">
		<div class="success">Create new document by QMRF Editor</div>
		<br>
		      <input type="submit" value="Save as draft">
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
      <!--
      <input type="radio" name="submit_state" value="draft" checked>Save as draft<br>
      <input type="radio" name="submit_state" value="submitted">Final submission (no further editing will be allowed).<br>
      -->


    </tr>
  </table>
</form>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>
  </body>
</html>


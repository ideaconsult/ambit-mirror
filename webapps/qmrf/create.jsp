<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="thispage" value='create.jsp'/>


<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin']=='true'}" >
  <c:redirect url="admin.jsp"/>
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


<jsp:include page="menu.jsp" flush="true"/>

<jsp:include page="menuuser.jsp" flush="true">
    <jsp:param name="highlighted" value="create"/>
</jsp:include>



<c:set var="u">http://${header["host"]}${pageContext.request.contextPath}</c:set>

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

<div class="success">Create new document by QMRF Editor</div>
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
			Applet not supported by browser.
		</applet>
      	</td>
    
      <td halign="top" align="left">
      <!--
      <input type="radio" name="submit_state" value="draft" checked>Save as draft<br>
      <input type="radio" name="submit_state" value="submitted">Final submission (no further editing will be allowed).<br>
      -->
      <input type="submit" value="Save as draft"><br>
    </tr>
  </table>
</form>

  </body>
</html>


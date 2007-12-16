<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="thispage" value='edit.jsp'/>


<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin'] eq 'true'}" >
  <c:redirect url="admin.jsp"/>
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

<jsp:include page="menu.jsp" flush="true"/>

<jsp:include page="menuuser.jsp" flush="true">
    <jsp:param name="highlighted" value="create"/>
</jsp:include>


<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select idqmrf,qmrf_number,xml from documents where idqmrf=? and user_name=? limit 1
	<sql:param value="${param.id}"/>
	<sql:param value="${sessionScope['username']}"/>

</sql:query>
<div class="success">Edit document by QMRF Editor</div>
	<c:set var="qmrf_update" value="true" scope="session"/>
  <form method="POST" id="qmrfform" name="qmrfform" action='<%= response.encodeURL("submit_update.jsp") %>' onSubmit="return getXML()">
  <table border="0" cellspacing="5">
	<c:forEach var="row" items="${rs.rows}">
		<c:set var="data" value="${row.xml}"/>

	<tr>

	<td align="left" colspan="2">
		<input type="hidden" name="id" value="${row.idqmrf}">

    <c:set var="u">http://${header["host"]}${pageContext.request.contextPath}</c:set>

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
				<param name="user" value="user"/>
				<param name="cleancatalogs" value="true"/>
			Applet not supported by browser.
		</applet>
		</td>
          <td halign="top">
    	<input type="hidden" id="xml" name="xml" value="" checked>
    	<!--
      <input type="radio" name="submit_state" value="draft" checked>Submit as draft<br>
      <input type="radio" name="submit_state" value="submitted">Final submission (no further editing will be allowed).<br>
      -->


      <input type="submit" value="Save as draft">
          </td>
    </tr>
    </tr>

  </c:forEach>
  </table>
</form>

  </body>
</html>


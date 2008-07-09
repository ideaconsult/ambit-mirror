<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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

<p>
This is a list of published documents' attachment(s) that contain structures not yet imported into database.<br>
To import structres, please click on <img src="images/import.png" height="16" width="16" alt="Import structures" border="0"/> 
		on each attachment in order to import the structures.
		On successful import the icon will change to <img src="images/benzene.gif" height="16" width="16" alt="View structures" border="0"/> and the document will disappear from this page.
</p>
<c:set var="sql">
	select d.idqmrf,qmrf_number,qmrf_title,name,format,description,type,date_format(a.updated,'${sessionScope.dateformat}') as lastdate,idattachment,original_name,imported,status from documents as d join attachments as a using(idqmrf) where status='published' and type != 'document' and imported=0  group by idqmrf;
</c:set>
<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${sql}"/>

		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="qmrf_title" value="Title"/>		
		<jsp:param name="version" value="Version"/>
		<jsp:param name="user_name" value="Author"/>
		<jsp:param name="lastdate" value="Last updated"/>
		<jsp:param name="status" value="Status"/>
		<jsp:param name="actions" value="admin"/>

		<jsp:param name="paging" value="true"/>
		<jsp:param name="page" value="${param.startpage}"/>
		<jsp:param name="pagesize" value="${param.pagesize}"/>
		<jsp:param name="viewpage" value="import.jsp"/>

</jsp:include>
<jsp:include page="view.jsp" flush="true">
    <jsp:param name="highlighted" value="${param.id}"/>

</jsp:include>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>
</body>
</html>
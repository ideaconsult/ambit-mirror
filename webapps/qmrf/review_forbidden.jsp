<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%> 
<fmt:requestEncoding value="UTF-8"/> 

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="review"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>    
</jsp:include>

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
</jsp:include>

<blockquote>
<div class="error">
${param.message}
<a href="help.jsp?anchor=review_forbidden" target="help"><img src="images/help.png" alt="help" title="Reviewing a document where reviewer is an author is forbidden!" border="0"/></a>
</div>
</blockquote>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
</div>

  </body>
</html>


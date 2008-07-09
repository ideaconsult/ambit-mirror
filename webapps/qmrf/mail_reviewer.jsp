<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:requestEncoding value="UTF-8"/>


<c:choose>
<c:when test="${mode='reviewer'}">
	<c:set var="text">
	You have been appointed as a reviewer of a QMRF document.
	Please perform your reviewing duties at your earliest convenience.
	</c:set>
</c:when>
<c:when test="${mode='submit'}">
	<c:set var="text">
	We would like to inform you that a new QMRF document has been submitted in the QMRF Database.
	Please perform your editorial duties and assign a reviewer at your earliest convenience.	
	</c:set>
</c:when>
<c:otherwise>
	<c:set var="text"/>
</c:otherwise>
</c:choose>
<jsp:include page="mail.jsp" flush="true">
    <jsp:param name="title" value="${param.title}"/>
    <jsp:param name="firstname" value="${param.firstname}"/>
    <jsp:param name="lastname" value="${param.lastname}"/>        
    <jsp:param name="text" value="${text}"/>
</jsp:include>		
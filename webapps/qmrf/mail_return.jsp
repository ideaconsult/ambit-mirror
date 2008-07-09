<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:requestEncoding value="UTF-8"/>
<c:set var="text">
A QMRF document, which you submitted in the QMRF Database on ${param.qdate} at ${param.qtime}, has been reviewed and returned to you for revision.
Please perform the required actions and then re-submit the revised document in the QMRF Database at your earliest convenience.
</c:set>
<jsp:include page="mail.jsp" flush="true">
    <jsp:param name="title" value="${param.title}"/>
    <jsp:param name="firstname" value="${param.firstname}"/>
    <jsp:param name="lastname" value="${param.lastname}"/>        
    <jsp:param name="text" value="${text}"/>
</jsp:include>		
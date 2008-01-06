<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:requestEncoding value="UTF-8"/>
<c:set var="text">

</c:set>
<jsp:include page="mail.jsp" flush="true">
    <jsp:param name="title" value="${param.title}"/>
    <jsp:param name="firstname" value="${param.firstname}"/>
    <jsp:param name="lastname" value="${param.lastname}"/>        
    <jsp:param name="text" value="${text}"/>
</jsp:include>		
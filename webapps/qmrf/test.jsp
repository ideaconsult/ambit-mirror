<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:requestEncoding value="UTF-8"/>
<jsp:include page="mail_return.jsp" flush="true">
    <jsp:param name="title" value="${row.title}"/>
    <jsp:param name="firstname" value="${row.firstname}"/>
    <jsp:param name="lastname" value="${row.lastname}"/>        
    <jsp:param name="qdate" value="111"/>        
    <jsp:param name="qtime" value="222"/>            
</jsp:include>	
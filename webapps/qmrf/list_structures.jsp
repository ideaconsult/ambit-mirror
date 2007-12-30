<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="(Q)SAR Model Reporting Format (QMRF) Inventory"/>
</jsp:include>

<jsp:include page="structures.jsp" flush="true"/>

<div id="hits">
<p>
<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
</jsp:include>
</div>
</body>
</html>
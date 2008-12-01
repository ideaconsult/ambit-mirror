<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<title>REPDOSE Login </title>
</head>
<body bgcolor="white">

<%

    session.invalidate();
%>
Invalid username and/or password.

<jsp:include page="footer.jsp" flush="true"/>


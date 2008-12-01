<%@page isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>Sorry about the error</title></head>
<body>
<h2>Sorry, We Erred Handling Your Request</h2>

<strong>Here is information about the error:</strong> <br><br>

The servlet name associated with throwing the exception:
<c:out value="${requestScope[\"javax.servlet.error.servlet_name\"]}" />
<br><br>

The type of exception:
  <c:out value=
    "${requestScope[\"javax.servlet.error.exception\"].class.name}" />
<br><br>

The request URI:
<c:out value="${requestScope[\"javax.servlet.error.request_uri\"]}" />
<br><br>

The exception message:
  <c:out value=
    "${requestScope[\"javax.servlet.error.exception\"].message}" />
</body>
</html>

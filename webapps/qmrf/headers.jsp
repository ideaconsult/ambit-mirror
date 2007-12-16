<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/random-1.0" prefix="rand" %>


<html>
<body>
<p/>
<c:forEach var="hname" items="${pageContext.request.headerNames}">
    <c:forEach var="hvalue" items="${headerValues[hname]}">
        <b>${hname}</b>&nbsp;${hvalue}
        <p>
    </c:forEach>
</c:forEach>

<br>
<rand:string id="random1" charset="a-zA-Z0-9! @ # $" length='15'/>
<jsp:getProperty name="random1" property="random" />


</body>
</html>
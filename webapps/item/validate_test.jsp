<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${param.SID eq 'BAD'}">
	<c:redirect url="http://www.acad.bg" />
</c:if>
SID=${param.SID}
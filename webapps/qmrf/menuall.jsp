<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
        <c:when test="${empty sessionScope['isadmin']}">

						<c:choose>
						<c:when test="${param.highlighted eq 'welcome'}">
								<a href="index.jsp"><img src="images/home_over.png" border="0" alt="Welcome"></a>
					  </c:when>
					  <c:otherwise>
					  		<a href="index.jsp"><img src="images/home.png" border="0" alt="Welcome"></a>
						</c:otherwise>
						</c:choose>

						<c:choose>
						<c:when test="${param.highlighted eq 'search'}">
								<a href="search_catalogs.jsp"><img src="images/search_documents_over.png" border="0"></a>
					  </c:when>
					  <c:otherwise>
					  		<a href="search_catalogs.jsp"><img src="images/search_documents.png" border="0"></a>
						</c:otherwise>
						</c:choose>

					<c:choose>
						<c:when test="${param.highlighted eq 'structures'}">
								<a href="search_substances.jsp"><img src="images/search_structures_over.png" border="0"></a>
					  </c:when>
					  <c:otherwise>
					  		<a href="search_substances.jsp"><img src="images/search_structures.png" border="0"></a>
						</c:otherwise>
						</c:choose>
						
						<c:if test="${param.highlighted eq 'register'}">
								<a href="register.jsp"><img src="images/register_over.png" border="0" alt="User registration"></a>
					  </c:if>
						<c:if test="${param.highlighted eq 'login'}">
								<a href="protected.jsp"><img src="images/log_in_over.png" border="0" alt="Log in"></a>
					  </c:if>
						<c:if test="${param.highlighted eq 'advancedsearch'}">
								<a href="search_catalogs.jsp"><img src="images/results_over.png" border="0" alt="Search"></a>
					  </c:if>
        </c:when>
        <c:when test="${sessionScope['isadmin']=='true'}">
						<jsp:include page="menuadmin.jsp" flush="true">
						    <jsp:param name="highlighted" value="${param.highlighted}"/>
						</jsp:include>
        </c:when>
        <c:when test="${sessionScope['isadmin']=='false'}">
						<jsp:include page="menuuser.jsp" flush="true">
						    <jsp:param name="highlighted" value="${param.highlighted}"/>
						</jsp:include>
        </c:when>
        <c:otherwise>
						<a href="index.jsp"><img src="images/home.png" border="0" alt="Welcome"></a>
						<c:choose>
						<c:when test="${param.highlighted eq 'search'}">
								<a href="search_catalogs.jsp"><img src="images/search_over.png" border="0"></a>
					  </c:when>
					  <c:otherwise>
					  		<a href="search_catalogs.jsp"><img src="images/search.png" border="0"></a>
						</c:otherwise>
						</c:choose>

						<c:if test="${param.highlighted eq 'register'}">
								<a href="register.jsp"><img src="images/register_over.png" border="0" alt="User registration"></a>
					  </c:if>
						<c:if test="${param.highlighted eq 'login'}">
								<a href="protected.jsp"><img src="images/log_in_over.png" border="0" alt="Log in"></a>
					  </c:if>
						<c:if test="${param.highlighted eq 'advancedsearch'}">
								<a href="ambit_index.jsp"><img src="images/result_over.png" border="0" alt="Advanced Search" ></a>
					  </c:if>
        </c:otherwise>
</c:choose>
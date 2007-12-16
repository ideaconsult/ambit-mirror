<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="thispage" value='search.jsp'/>

<jsp:include page="query_settings.jsp" flush="true"/>


<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
  <head>
    <title>QMRF documents</title>
  </head>
  <body>

<jsp:include page="menu.jsp" flush="true"/>

<jsp:include page="menuall.jsp" flush="true">
		    <jsp:param name="highlighted" value="advancedsearch" />
</jsp:include>




<c:choose>
        <c:when test="${empty param.qmrf_simple}">

        </c:when>
        <c:otherwise>

        </c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty param.pagesize}">
  	<c:set var="pagesize" value="10"/>
  </c:when>
  <c:otherwise>
  		<c:set var="pagesize" value="${param.pagesize}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty param.page}">
  	<c:set var="startpage" value="0"/>
  	<c:set var="startrecord" value="0"/>
  </c:when>
  <c:otherwise>
  		<c:set var="startpage" value="${param.page}"/>
			<c:set var="startrecord" value="${param.page * param.pagesize}"/>
	</c:otherwise>
</c:choose>

<h3>QMRF Documents matching <i>${param.qmrf_simple}</i></h3>


<c:import url="fulltextsearch.jsp" >
    <c:param name="fulltext" value="${param.qmrf_simple}"/>

    <c:param name="user_name" value="Author"/>
    <c:param name="updated" value="Creation date"/>
		<c:param name="actions" value=""/>

		<c:param name="paging" value="true"/>
		<c:param name="page" value="${startpage}"/>
		<c:param name="pagesize" value="${pagesize}"/>

</c:import>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>
  </body>
</html>


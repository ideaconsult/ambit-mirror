<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

	<c:choose>
	<c:when test="${empty sessionScope['username']}">
			<c:redirect url="protected.jsp"/>
	</c:when>
	<c:when test="${sessionScope['isadmin'] eq 'false'}">
			<c:redirect url="myprofile.jsp"/>
	</c:when>
	<c:when test="${sessionScope['isadmin'] eq 'true'}">
		<!-- OK, can change everything-->
	</c:when>
	<c:otherwise>
			<c:redirect url="myprofile.jsp"/>
	</c:otherwise>
</c:choose>

<html>
<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
<head>
<meta name="description" content="(Q)MRF database">
<meta name="keywords" content="ambit,qsar,qmrf,structure search">
<meta name="robots"content="index,follow">
<META NAME="GOOGLEBOT" CONTENT="index,FOLLOW">
<meta name="copyright" content="Copyright 2007. Nina Jeliazkova nina@acad.bg">
<meta name="author" content="Nina Jeliazkova">
<meta name="language" content="English">
<meta name="revisit-after" content="7">
<link rel="SHORTCUT ICON" href="favicon.ico"/>
</head>
<title>(Q)SAR Model Reporting Format (QMRF) Inventory</title>
<body bgcolor="#ffffff">


<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="welcome"/>
</jsp:include>



<jsp:include page="menuall.jsp" flush="true">
		    <jsp:param name="highlighted" value="profile" />

</jsp:include>


<c:set var="success" value=""/>

<c:if test="${!empty param.user_name}">


<c:choose>

<c:when test="${!empty param.user_role && !empty param.old_role}">
			<c:catch var='transactionException2'>
			<!-- check if there are existing documents -->
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select count(idqmrf) as c from documents where user_name=?
				<sql:param value="${param.user_name}"/>
			</sql:query>

			<c:set var="documents" value="0"/>
			<c:forEach var="row" items="${rs.rows}">
					<c:set var="documents" value="${row.c}"/>
			</c:forEach>

			</c:catch>

			<!-- If there are existing documents the role can't be changed -->
			<c:choose>
			<c:when test="${documents > 0}">

					<c:set var="success" value="There are ${documents} QMRF documents for user name <b>${param.user_name}</b>. The role can not be changed."/>
			</c:when>
			<c:otherwise>

						<sql:setDataSource dataSource="jdbc/tomcat_users"/>

						<c:catch var='transactionException2'>
								<sql:transaction>

										<sql:update var="rs1" >
												update user_roles set role_name=? where user_name=?
												<sql:param value="${fn:trim(param.user_role)}"/>
												<sql:param value="${param.user_name}"/>

										</sql:update>

							</sql:transaction>
							<c:set var="success" value="Role for user <b>${param.user_name}</b> changed successfully to ${param.user_role}."/>


						</c:catch>
						<c:set var="success" value="Role for user <b>${param.user_name}</b> changed successfully to ${param.user_role}."/>
			</c:otherwise>
			</c:choose>




		<c:choose>
		<c:when test='${not empty transactionException2}'>
						<div class="error">
									error ${transactionException2}
						</div>
		</c:when>
		<c:otherwise>
					<div class="success">

						${success}<br/> Back to <a href="myprofile.jsp#${param.user_name}">My profile</a>

			</div>

		</c:otherwise>
		</c:choose>

</c:when>
<c:otherwise>
		<div class="error">
				Role not defined.
	</div>
</c:otherwise>
</c:choose>

<c:if test="${empty success}">

	<form method="POST" name="password_form" action="mypassword.jsp">
		<h3>Change role for user name: <font color="red">${param.user_name}</font></h3>
	<table>
	<tr>
	<th>
		Old role
	</th>
	<td>
		<input type="text" name="old_role" readonly="true" size="16" value="${param.old_role}"/>
	</td>
	</tr>

	<tr>
	<th>
		New Role
	</th>
	<td>
		<input type="text" name="user_role" readonly="true" size="16"  value="${param.user_role}"/>
	</td>
	</tr>

	<tr>
	<th>

	</th>
	<td>
		<input type="hidden" name="user_name" value="${param.user_name}"/>

		<input type="submit" name="Submit"/>
	</td>
	</tr>
	</table>
</form>
</c:if>
</c:if>
<!-- user_name -->





<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value="${isadmin}"/>
		</jsp:include>
	</p>
</div>
</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:requestEncoding value="UTF-8"/>

<!-- June 11, 2008 - changed to set the flag 'verified' upon email validation. The flag 'confirmed' will be set upon admin action-->
<c:if test="${empty param.id}" >
	<jsp:forward page="register.jsp">
		<jsp:param name="registerstatus" value=""/>
			<jsp:param name="email" value="${param.email}"/>
  </jsp:forward>
</c:if>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database Registration page"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="register"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>    
</jsp:include>

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select user_name,(now()-registration_date) as seconds_since_registered from users where registration_id=? and registration_status='commenced' limit 1
  <sql:param value="${param.id}"/>

</sql:query>


<c:choose>
	<c:when test="${rs.rowCount gt 0}">
		<c:forEach var="row" items="${rs.rows}">


				<c:if test="${row.seconds_since_registered ge 172800}">
						<p>
						<p>
						Your registration has been cancelled automatically because we did not receive your confirmation within 48h. Please repeat the registration procedure.

				</c:if>


				<sql:setDataSource dataSource="jdbc/qmrf_documents"/>

				<c:catch var='transactionException1'>
					<sql:transaction>
							<!-- if ok, update statis to confirmed and transfer the users into tomcat table , perhaps delete password from here -->
							<sql:update var="rs">
									update users set registration_status='verified',registration_date=now(),registration_id=null where registration_id=?
									<sql:param value="${param.id}"/>
							</sql:update>
					</sql:transaction>
			  </c:catch>

				<c:if test='${not empty transactionException1}'>
					<div class="error">
							Registration error ${transactionException1}
				</div>

				</c:if>

			<h2>
			Welcome ${row.user_name}!
		</h2>
			<p>

			 Your e-mail is verified and an approval from QMRF Database team is necessary in order to complete the registration. 

		</c:forEach>
	</c:when>
	<c:otherwise>
		<p><p>
			Your confirmation was malformed. Please check whether you have followed the correct URL.

	</c:otherwise>
</c:choose>

<!--
Your registration has been cancelled automatically because we did not receive your confirmation within 48h. Please repeat the registration procedure. (repeat the registration procedure moje da e link kqm kqdeto triabva - miastoto za popqlvane).
Tozi text triabva da izliza pri nadhvqrleni 48H. pri druga greshka (naprimerno nepoznato URL) - drug text.
Your confirmation was malformed. Please check whether you have followed the correct URL.
-->
<!-- else the id is invalid (we could check if it is outdated) and then redirect to registration page -->


<hr>

<h6>
Please note that log in is required only for submitting new (Q)MRF documents.
</h6>

<div id="hits">
<p>
<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
</jsp:include>
</div>

</body>
</html>

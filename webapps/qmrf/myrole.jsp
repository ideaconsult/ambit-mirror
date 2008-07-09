<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:if test="${empty pageContext.request.userPrincipal.name}">
	<c:redirect url="protected.jsp"/>
</c:if>

<c:if test="${sessionScope.viewmode ne 'qmrf_manager'}">
	<c:redirect url="myprofile.jsp"/>
</c:if>

<c:set var="allowed" value="" />
<c:forEach var="a" items="${pageContext.request.userPrincipal.roles}">
	<c:if test="${a eq 'qmrf_manager'}">
		<c:set var="allowed" value="true" />
	</c:if>
</c:forEach>

<c:if test="${empty allowed}">
	<c:redirect url="protected.jsp"/>
</c:if>


<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database: User' role"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
	<jsp:param name="highlighted" value="profile" />
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>
<p>


<c:if test="${!empty param.user_name}">
<h3>Modifying ${param.user_name} roles:</h3>
<c:set var="modify" value="No modification requested."/>
<ul>

<!-- change manager role -->
<c:catch var="err">
	<c:choose>
	<c:when test="${empty param.qmrf_manager}">
		<c:if test="${param.old_qmrf_manager eq 'true'}">
				<sql:update var="rs1" dataSource="jdbc/tomcat_users">
					delete from user_roles where user_name=? and role_name=?
					<sql:param value="${param.user_name}"/>
					<sql:param value="qmrf_manager"/>
				</sql:update>
				<div class="success">
				<li>Administrator role removed for user <b>${param.user_name}</b>
				</div>
				<c:set var="modify" value=""/>
		</c:if>
	</c:when>
	<c:otherwise>
		<c:if test="${param.old_qmrf_manager eq 'false'}">
				<sql:update var="rs1" dataSource="jdbc/tomcat_users">
					insert into user_roles (role_name,user_name) values (?,?)
					<sql:param value="qmrf_manager"/>
					<sql:param value="${param.user_name}"/>
				</sql:update>
				<div class="success"><li>Administrator role added for user <b>${param.user_name}</b></div>
				<c:set var="modify" value=""/>
		</c:if>
	</c:otherwise>
	</c:choose>
</c:catch>

<!-- change editor role -->
<c:catch var="err">
	<c:choose>
	<c:when test="${empty param.qmrf_editor}">
		<c:if test="${param.old_qmrf_editor eq 'true'}">
				<sql:update var="rs1" dataSource="jdbc/tomcat_users">
					delete from user_roles where user_name=? and role_name=?
					<sql:param value="${param.user_name}"/>
					<sql:param value="qmrf_editor"/>
				</sql:update>
				<div class="success">
				<li>Editor role removed for user <b>${param.user_name}</b>
				</div>
				<c:set var="modify" value=""/>
		</c:if>
	</c:when>
	<c:otherwise>
		<c:if test="${param.old_qmrf_editor eq 'false'}">
				<sql:update var="rs1" dataSource="jdbc/tomcat_users">
					insert into user_roles (role_name,user_name) values (?,?)
					<sql:param value="qmrf_editor"/>
					<sql:param value="${param.user_name}"/>
				</sql:update>
				<div class="success"><li>Editor role added for user <b>${param.user_name}</b></div>
				<c:set var="modify" value=""/>
		</c:if>
	</c:otherwise>
	</c:choose>
</c:catch>

<!-- change author role -->
<c:choose>
<c:when test="${empty param.qmrf_user}">
	<c:if test="${param.old_qmrf_user eq 'true'}">
		<c:catch var='err'>
			<!-- check if there are existing documents -->
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select count(idqmrf) as c from documents where user_name=? and status != 'published'
				<sql:param value="${param.user_name}"/>
			</sql:query>
			<c:set var="documents" value="0"/>
			<c:forEach var="row" items="${rs.rows}">
				<c:set var="documents" value="${row.c}"/>

			</c:forEach>
			<c:choose>
			<c:when test="${documents > 0}">

				<div class="error">
				<li>There are ${documents} QMRF documents under preparation for user name <b>${param.user_name}</b>. The Author role can not be removed.
				</div>
			</c:when>
			<c:otherwise>
				<sql:update var="rs1" dataSource="jdbc/tomcat_users">
					delete from user_roles where user_name=? and role_name=?
					<sql:param value="${param.user_name}"/>
					<sql:param value="qmrf_user"/>
				</sql:update>
				<div class="success">
				<li>Author role removed for user <b>${param.user_name}</b>
				</div>
				<c:set var="modify" value=""/>
			</c:otherwise>
			</c:choose>
		</c:catch>
		<c:if test="${!empty err}">
			<div class="error"><li>${err}</div>
			<c:set var="modify" value=""/>
		</c:if>
	</c:if>
</c:when>
<c:otherwise>
	<c:if test="${param.old_qmrf_user eq 'false'}">
		<c:catch var="err">
			<sql:setDataSource dataSource="jdbc/tomcat_users"/>
			<sql:transaction>
				<sql:update var="rs1" >
					insert into user_roles (role_name,user_name) values (?,?)
					<sql:param value="qmrf_user"/>
					<sql:param value="${param.user_name}"/>
				</sql:update>
			</sql:transaction>
			<div class="success"><li>Author role added for user <b>${param.user_name}</b></div>
			<c:set var="modify" value=""/>
		</c:catch>
		<c:if test="${!empty err}">
			<div class="error"><li>${err}</div>
			<c:set var="modify" value=""/>
		</c:if>
	</c:if>
</c:otherwise>
</c:choose>

<!-- change reviewer role -->
<c:choose>
<c:when test="${empty param.qmrf_admin}">
	<c:if test="${param.old_qmrf_admin eq 'true'}">
		<c:catch var='err'>
			<!-- check if there are existing documents -->
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select count(idqmrf) as c from documents where reviewer=? and status != 'published'
				<sql:param value="${param.user_name}"/>
			</sql:query>
			<c:set var="documents" value="0"/>
			<c:forEach var="row" items="${rs.rows}">
				<c:set var="documents" value="${row.c}"/>
			</c:forEach>
			<c:choose>
			<c:when test="${documents > 0}">
				<div class="error">
				<li>There are ${documents} QMRF documents under revision for user name <b>${param.user_name}</b>. The Reviewer role can not be removed.
				</div>
			</c:when>
			<c:otherwise>
				<sql:update var="rs1" dataSource="jdbc/tomcat_users">
					delete from user_roles where user_name=? and role_name=?
					<sql:param value="${param.user_name}"/>
					<sql:param value="qmrf_admin"/>
				</sql:update>
				<div class="success"><li>Reviewer role removed for user <b>${param.user_name}</b></div>
				<c:set var="modify" value=""/>
			</c:otherwise>
			</c:choose>
		</c:catch>
		<c:if test="${!empty err}">
			<div class="error"><li>${err}</div>
			<c:set var="modify" value=""/>
		</c:if>
	</c:if>
</c:when>
<c:otherwise>
	<c:if test="${param.old_qmrf_admin eq 'false'}">
		<c:catch var="err">
			<sql:setDataSource dataSource="jdbc/tomcat_users"/>
			<sql:transaction>
				<sql:update var="rs1" >
					insert into user_roles (role_name,user_name) values (?,?)
					<sql:param value="qmrf_admin"/>
					<sql:param value="${param.user_name}"/>
				</sql:update>
			</sql:transaction>
			<div class="success"><li>Reviewer role added for user <b>${param.user_name}</b></div>
			<c:set var="modify" value=""/>
		</c:catch>
		<c:if test="${!empty err}">
			<div class="error"><li>${err}</div>
			<c:set var="modify" value=""/>
		</c:if>
	</c:if>
</c:otherwise>
</c:choose>
</ul>
</c:if>

<!-- user_name -->
${modify}
<hr>
<a href="myprofile.jsp">Back to user profiles</a>




<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value="${isadmin}"/>
		</jsp:include>
	</p>
</div>
</body>
</html>

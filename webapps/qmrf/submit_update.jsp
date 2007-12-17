<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="thispage" value='submit_update.jsp'/>


<c:if test="${empty param.id}" >
  <c:redirect url="/user.jsp"/>
</c:if>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin']=='true'}" >
  <c:redirect url="/admin.jsp"/>
</c:if>

<!-- Not launched from edit.jsp -->
<c:if test="${sessionScope.qmrf_update ne 'true'}">
	<c:redirect url="/user.jsp"/>
</c:if>
<c:set var="qmrf_update" value="" scope="session"/>

<c:set var="state" value="draft"/>


<c:set var="uname" value='${sessionScope["username"]}'/>
<c:set var="updateCount" value="0"/>
<c:choose>
	<c:when test="${empty param.xml}" >
			<!-- Only status update -->
			<c:catch var="transactionException_update">
			<sql:transaction dataSource="jdbc/qmrf_documents">
			<sql:update var="updateCount" >
			    update documents set status=? where idqmrf=? and user_name=?;
			  <sql:param value="${state}"/>
			  <sql:param value="${param.id}"/>
				<sql:param value="${sessionScope['username']}"/>

			</sql:update>
		</sql:transaction>
		</c:catch>

	</c:when>
	<c:otherwise>

			<!-- data  update -->
		<c:catch var="transactionException_update">

			<c:import url="include_attachments.jsp" var="newxml" >
				<c:param name="id" value="${param.id}"/>
				<c:param name="xml" value="${param.xml}"/>
			</c:import>
			<sql:transaction dataSource="jdbc/qmrf_documents">
			<sql:update>
					set names utf8
			</sql:update>

			<sql:update var="updateCount" >
				update documents set status=?,xml=?,updated=now() where idqmrf=? and user_name=?;
			<sql:param value="${state}"/>
			<sql:param value="${newxml}"/>
			  <sql:param value="${param.id}"/>
				<sql:param value="${sessionScope['username']}"/>
			</sql:update>
			</sql:transaction>
		</c:catch>
	</c:otherwise>
</c:choose>


<c:choose>
<c:when test='${!empty transactionException_update}'>
	<c:redirect url="edit_attachmentsdb.jsp">
	 <c:param name="id" value="${param.id}"/>
	<c:param name="status">
		${transactionException_update}
	</c:param>
	</c:redirect>
</c:when>
<c:otherwise>
	<c:redirect url="edit_attachmentsdb.jsp">
	 <c:param name="id" value="${param.id}"/>
	<c:param name="status">
		Saving as draft successful.
	</c:param>
	</c:redirect>
</c:otherwise>
</c:choose>

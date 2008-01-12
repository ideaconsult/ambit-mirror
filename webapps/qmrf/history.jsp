<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>   

<c:if test="${!empty param.id}">
	<c:set var="version" value=""/>
	<c:catch var="exception_version">
		<sql:query var="rsa" dataSource="jdbc/qmrf_documents">
			select version from documents where idqmrf=?
			<sql:param value="${param.id}"/>
		</sql:query>
		
		<c:forEach var="row" items="${rsa.rows}">
			<c:set var="version" value="${row.version}"/>
		</c:forEach>
	</c:catch>

	<div  class="center" >	
			<b><font color="#4444FF">Previous versions:</font></b>
	</div>								
	<c:set var="thisid" value="${param.id}"/>
	<c:forEach var="v" begin="1" end="${version-1}" step="1">
	
		<c:choose>
			<c:when test="${empty sessionScope['username']}">
				<c:set var="sql">
					select idqmrf,idqmrf_origin,qmrf_number,qmrf_title,version,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status documents where idqmrf=(select idqmrf_origin from documents where idqmrf=${thisid}) and status='published' 
				</c:set>				
				<c:set var="actions" value=""/>
			</c:when>	
			<c:otherwise>
				<c:set var="sql">
					select idqmrf,idqmrf_origin,qmrf_number,qmrf_title,version,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where idqmrf=(select idqmrf_origin from documents where idqmrf=${thisid}) and ((user_name=?) or (status='published'))
				</c:set>				
				<c:set var="actions" value="user"/>
			</c:otherwise>		
		</c:choose>
		
		<set var="noheader" value="false"/>
		<c:if test="${v > 1}">
			<set var="noheader" value="true"/>	
		</c:if>
		${noheader}
		<jsp:include page="records.jsp" flush="true">
		    <jsp:param name="sql" value="${sql}"/>
					    				    	
				<jsp:param name="qmrf_number" value="QMRF#"/>    	
				<jsp:param name="qmrf_title" value="Title"/>    				
				<jsp:param name="version" value="Version"/>    				
		    <jsp:param name="user_name" value="Author"/>    				
		    <jsp:param name="lastdate" value="Last updated"/>    				
				<jsp:param name="status" value="Status"/>    			
				<jsp:param name="actions" value="${actions}"/>      				
					
				<jsp:param name="sqlparam" value="${sessionScope['username']}"/>			
					
				<jsp:param name="paging" value=""/>   							
				<jsp:param name="page" value="${startpage}"/>   										
				<jsp:param name="pagesize" value="${pagesize}"/>   													
				<jsp:param name="viewpage" value="user.jsp"/>  			
				<jsp:param name="noheader" value="${noheader}"/>  			
					
		</jsp:include>  
		
		<c:catch var="exception_version">
			<sql:query var="rsa" dataSource="jdbc/qmrf_documents">
				${sql}
				<c:if test="${!empty sessionScope['username']}">
					<sql:param value="${sessionScope['username']}"/>
				</c:if>				
			</sql:query>
			
			<c:forEach var="row" items="${rsa.rows}">
				<c:set var="thisid" value="${row.idqmrf}"/>
			</c:forEach>
		</c:catch>		
		<c:if test="${!empty exception_version}">
			<div class="error">${exception_version}</div>
		</c:if>
	</c:forEach>

</c:if>
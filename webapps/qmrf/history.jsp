<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>   

<c:if test="${!empty param.id}">

<c:choose>
	<c:when test="${empty sessionScope['username']}">
		<c:set var="sql">
			select idqmrf,qmrf_number,version,user_name,updated,status documents where idqmrf=(select idqmrf_origin from documents where idqmrf=${param.id}) and status='published' 
		</c:set>				
		<c:set var="actions" value=""/>
	</c:when>	
	<c:otherwise>
		<c:set var="sql">
			select idqmrf,qmrf_number,version,user_name,updated,status from documents where idqmrf=(select idqmrf_origin from documents where idqmrf=${param.id}) and ((user_name=?) or (status='published'))
		</c:set>				
		<c:set var="actions" value="user"/>
	</c:otherwise>		
</c:choose>

<div  class="center" >	
	<b><font color="#4444FF">Previous versions:</font></b>
</div>	
<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${sql}"/>
			    				    	
		<jsp:param name="qmrf_number" value="QMRF#"/>    	
		<jsp:param name="version" value="Version"/>    				
    <jsp:param name="user_name" value="Author"/>    				
    <jsp:param name="updated" value="Last updated"/>    				
		<jsp:param name="status" value="Status"/>    			
		<jsp:param name="actions" value="${actions}"/>      				
			
		<jsp:param name="sqlparam" value="${sessionScope['username']}"/>			
			
		<jsp:param name="paging" value=""/>   							
		<jsp:param name="page" value="${startpage}"/>   										
		<jsp:param name="pagesize" value="${pagesize}"/>   													
		<jsp:param name="viewpage" value="user.jsp"/>  			
			
</jsp:include>  



	
</c:if>
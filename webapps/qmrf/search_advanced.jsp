<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="thispage" value='search.jsp'/>

<jsp:include page="query_settings.jsp" flush="true"/>	


<html>
	<link href="styles/search.css" rel="stylesheet" type="text/css">
  <head>
    <title>QMRF documents</title>
	<meta name="description" content="">
	<meta name="copyright" content="Copyright (c) 2005-2007 Nina Jeliazkova">
	<meta name="author" content="Nina Jeliazkova">
<SCRIPT>
function getMol(){
   document.searchForm.mol.value = escape(document.JCPApplet.getMolFile());
   return document.searchForm.mol.value;
   }
function load(){
	 document.searchForm.mol.value = "";
   //document.JCPApplet.setMolFile(null);
   window.status="Page is loaded"
   }   
</SCRIPT>	    
  </head>
  <body>

<%@ include file="header.html"%>

<jsp:include page="menu.jsp" flush="true"/>

<c:choose>
        <c:when test="${empty sessionScope['isadmin']}">
        </c:when>
        <c:when test="${sessionScope['isadmin']=='true'}">
						<jsp:include page="menuadmin.jsp" flush="true">
						    <jsp:param name="highlighted" value="search"/>
						</jsp:include>            
        </c:when>        
        <c:when test="${sessionScope['isadmin']=='false'}">
						<jsp:include page="menuuser.jsp" flush="true">
						    <jsp:param name="highlighted" value="search"/>
						</jsp:include>                        
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
 

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>	
		</jsp:include>
	</p>
</div>
  </body>
</html>


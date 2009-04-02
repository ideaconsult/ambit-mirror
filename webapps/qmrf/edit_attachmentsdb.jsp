<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/xephyrus-fileupload"   prefix="fup" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ambit" prefix="a" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>
<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<c:set var="thispage" value='edit_attachmentsdb.jsp'/>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>
<c:if test="${(sessionScope['viewmode'] ne 'qmrf_user') && (sessionScope['viewmode'] ne 'qmrf_admin')}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:catch var="exception">
		<c:if test="${empty sessionScope['username']}" >
		  <c:redirect url="protected.jsp"/>
		</c:if>

		<c:if test="${empty sessionScope['isauthor']}" >

		  <c:redirect url="index.jsp"/>
		</c:if>

</c:catch>
<c:if test="${not empty exception}">
  <c:redirect url="index.jsp"/>
</c:if>

<jsp:include page="query_settings.jsp" flush="true"/>

<c:if test="${!empty param.id}" >
  <c:set var="qmrf_document" value="${param.id}" scope="session"  />
</c:if>

<c:if test="${!empty param.action}" >
  <c:set var="action" value="${param.action}" scope="page"  />
</c:if>

<c:if test="${empty param.idattachment}" >
  <c:set var="action" value="" scope="page"  />
</c:if>

<c:if test="${empty sessionScope['qmrf_document']}" >
	<c:if test="${sessionScope['isauthor'] eq 'false'}" >
		  <c:redirect url="index.jsp"/>
	</c:if>
	<c:redirect url="user.jsp"/>
</c:if>


<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database: Upload QMRF document attachments"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="create"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>    
</jsp:include>

<c:set var="sql" value="select idqmrf,qmrf_number,qmrf_title,version,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where user_name=? and idqmrf=${sessionScope.qmrf_document}"/>
<c:set var="maxpages" value="1" scope="session"/>
<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${sql}"/>

		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="qmrf_title" value="Title"/>		
		<jsp:param name="version" value="Version"/>
		<jsp:param name="user_name" value="Author"/>
		<jsp:param name="lastdate" value="Last updated"/>
		<jsp:param name="status" value="Status"/>
		<jsp:param name="actions" value="user"/>

		<jsp:param name="sqlparam" value="${sessionScope['username']}"/>

		<jsp:param name="paging" value="false"/>
		<jsp:param name="page" value="1"/>
		<jsp:param name="pagesize" value="1"/>
		<jsp:param name="viewpage" value="user.jsp"/>


</jsp:include>

			 <!-- Note the user should have delete privilege -->
<c:if test="${action eq 'delete'}">


			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				  select idattachment,name,description from attachments join documents using(idqmrf) where user_name=? and idqmrf=? and idattachment=? limit 1
					<sql:param value="${sessionScope['username']}"/>
					<sql:param value="${sessionScope.qmrf_document}"/>
					<sql:param value="${param.idattachment}"/>

			</sql:query>
			<c:forEach var="row" items="${rs.rows}">
				 <c:set var="file_to_delete_page" value="${row.name}" scope="page"/>
				 <c:set var="file_description" value="${row.description}" scope="page"/>
			</c:forEach>

		<c:set value="delete attachments from attachments,documents where attachments.idqmrf=documents.idqmrf and attachments.idqmrf=? and idattachment=? and user_name=?" var="delete_sql"/>
		<c:catch var="transactionException_delete">
			<sql:transaction dataSource="jdbc/qmrf_documents">
			<sql:update>
					${delete_sql}
					<sql:param value="${sessionScope.qmrf_document}"/>
					<sql:param value="${param.idattachment}"/>
					<sql:param value="${sessionScope['username']}"/>
			</sql:update>
		</sql:transaction>
		</c:catch>
			<c:choose>
			<c:when test='${not empty transactionException_delete}'>
				<blockquote>
					<div class="error">
					Error on removing attachment ${param.idattachment} <br> ${transactionException_delete} <br>
					${sessionScope.qmrf_document} <br>  ${sessionScope['username']}
					</div>
				</blockquote>

			</c:when>
			<c:otherwise>
				<a:deletefile filename="${file_to_delete_page}"/>
				
						<sql:transaction dataSource="jdbc/qmrf_documents">
						<sql:query var="rs">
							select idqmrf,xml from documents where idqmrf=? limit 1
							<sql:param value="${sessionScope.qmrf_document}"/>
						</sql:query>
						<c:forEach var="row"  items="${rs.rows}">
							<c:import url="include_attachments.jsp" var="newxml" >
								<c:param name="id" value="${row.idqmrf}"/>
								<c:param name="xml" value="${row.xml}"/>
							</c:import>
							<sql:update>
								update documents set xml=?,updated=now() where idqmrf=?
								<sql:param value="${newxml}"/>
								<sql:param value="${row.idqmrf}"/>
							</sql:update>
						</c:forEach>
						</sql:transaction>
				<blockquote>
					<div class="success">
						Attachment ${file_description} deleted.
					</div>
				</blockquote>

		  </c:otherwise>
		  </c:choose>

</c:if>

<c:if test="${!empty param.status}">
<div class="success">
	${param.status}
</div>
</c:if>


<table width="80%">
	<tr><td>
		<c:catch var="upload_error">
			<fup:parse nonUpload="error" sizeMax="33554432"/>
		</c:catch>
		<c:if test="${empty upload_error}">
		<c:catch var="upload_error">
			<c:choose>
			<c:when test="${empty pageScope.param['description']}">
				<blockquote>
				<div class="error">
				File description should not be empty!
				</div>
				</blockquote>
			</c:when>
			<c:otherwise>
			<fup:file var="one">
				<fup:remotePath var="remotePath"/>
				<c:set var="delim" value="\//" />
				<c:set value="${fn:split(remotePath,delim)}" var="paths"/>
				<c:set var="filename" value="${remotePath}" />
				<c:if test="${fn:length(paths)>0}">
					<c:set var="filename" value="${paths[fn:length(paths)-1]}" />
				</c:if>

				<c:set value="${fn:split(remotePath,'.')}" var="paths"/>
				<c:set var="fileext" value="txt" />
				<c:if test="${fn:length(paths)>0}">
					<c:set var="fileext" value="${fn:toLowerCase(paths[fn:length(paths)-1])}" />
				</c:if>


	              <%-- the '~' means 'the file path to this jsp'
	                   the output of write is the new path
	                   you can assign it to a junk var with something like
	                  <fup:write to="~/uploaded.jpg" var="silent" />
	              --%>
				
				<c:set var="attachment_name" value="qmrf${sessionScope.qmrf_document}_${filename}" scope="page"  />
				<c:set var="attachment_path" value="${initParam['attachments-dir']}/${attachment_name}" scope="page"  />
				<fup:contents var="content"/>

				<c:catch var='transactionException_insert'>
						<fup:write to="${attachment_path}" var="silent" /><br />
						<sql:transaction dataSource="jdbc/qmrf_documents">
						<sql:update>
							INSERT INTO attachments (idqmrf,name,format,type,description,original_name) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=?,format=?,type=?,description=?,original_name=?;
								<sql:param value="${sessionScope.qmrf_document}"/>
								<sql:param value="${attachment_name}"/>
								<sql:param value="${fileext}"/>
								<sql:param value="${pageScope.param['type']}"/>
								<sql:param value="${pageScope.param['description']}"/>
								<sql:param value="${remotePath}"/>
								<sql:param value="${attachment_name}"/>
								<sql:param value="${fileext}"/>
								<sql:param value="${pageScope.param['type']}"/>
								<sql:param value="${pageScope.param['description']}"/>
								<sql:param value="${remotePath}"/>

						</sql:update>
						</sql:transaction>
						<sql:transaction dataSource="jdbc/qmrf_documents">
						<sql:query var="rs">
							select idqmrf,xml from documents where idqmrf=? limit 1
							<sql:param value="${sessionScope.qmrf_document}"/>
						</sql:query>
						<c:forEach var="row"  items="${rs.rows}">
							<c:import url="include_attachments.jsp" var="newxml" >
								<c:param name="id" value="${row.idqmrf}"/>
								<c:param name="xml" value="${row.xml}"/>
							</c:import>
							<sql:update>
								update documents set xml=?,updated=now() where idqmrf=?
								<sql:param value="${newxml}"/>
								<sql:param value="${row.idqmrf}"/>
							</sql:update>
						</c:forEach>
						</sql:transaction>
						
				</c:catch>
				<c:choose>
						<c:when test="${!empty transactionException_insert}">
							<div class="error">
								Error on inserting attachment <b>${remotePath}</b>
								<br> ${transactionException_insert}
							</div>
				  		</c:when>
						<c:otherwise>
							<blockquote>
							<div class="success">
							File <b>${remotePath}</b> <br> File size: <fup:size/> bytes uploaded as attachment ${filename}
							</div>
							</blockquote>
						</c:otherwise>
					</c:choose>

			</fup:file>
			</c:otherwise>
			</c:choose>
		</c:catch>
		<c:if test="${!empty upload_error}">
			<div class="error">${upload_error}</div>
		</c:if>
		</c:if>
      </td></tr></table>

  <table border="0" cellspacing="5" width="95%">
	<!-- here goes the list of attachmennt as read from database -->
  <jsp:include page="list_attachments.jsp" flush="true"/>

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	  select idqmrf,status from documents where user_name=? and idqmrf=? and (status="draft" or status="returned for revision") limit 1
		<sql:param value="${sessionScope['username']}"/>
		<sql:param value="${sessionScope.qmrf_document}"/>

</sql:query>
<c:forEach var="row" items="${rs.rows}">
    <tr  bgColor="#DDDDDD">
    	  <form method="POST" name="qmrfform" action='<%= response.encodeURL("edit_attachmentsdb.jsp") %>' enctype="multipart/form-data">
    	<td>New</td>
    	<td colspan="2"><input type="file" name="up" accept=""/></td>
    	<td><input type="text" name="description" value="" /></td>
    	<td>
				<select name="type">
		  		<option value ="data_training">Training set</option>
					<option value ="data_validation">Validation set</option>
		  		<option value ="document">Other document</option>
				</select>
    		</td>
			<td></td>
    	<td><input type="submit" name="upload" value="Upload" /></td>
    </form>
  	</tr>
</c:forEach>
  </table>
  
<c:if test="${action eq 'properties'}">
  <jsp:include page="fileproperties.jsp" flush="true">
	<jsp:param name="id" value="${param.id}"/>
	<jsp:param name="idattachment" value="${param.idattachment}"/>	  
  	<jsp:param name="name" value="${param.name}"/>
  </jsp:include>
</c:if>  

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true"/>
	</p>
</div>
  </body>
</html>

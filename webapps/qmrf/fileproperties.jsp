<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ambit" prefix="a" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>



<c:catch var="err">
	<c:set var="parse" value="true"/>
	<c:if test="${!empty param.idattachment}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			SELECT count(idattachment) as c FROM attachments_description where idattachment=?
			<sql:param value="${param.idattachment}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<c:set var="parse" value="${row.c eq 0}"/>
		</c:forEach>
		
	</c:if>
	<c:set var="tokens" value="ignore,Chemical Name (IUPAC),Chemical Name (Not IUPAC),CAS Number,SMILES,InChI,Structural Formula,Dependent Variable Value,Descriptor,Experimental data,other"/>

<c:if test="${!empty param.idattachment}">
	<c:choose>
	<c:when test="${empty param.properties}">
		<c:set var="filename" value=""/>
		<c:catch var="err">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				SELECT name FROM attachments where idattachment=?
				<sql:param value="${param.idattachment}"/>
			</sql:query>		
			<c:forEach var="row" items="${rs.rows}">
				<c:set var="filename" value="${row.name}"/>
			</c:forEach>
		</c:catch>
		<c:if test="${!empty err}">
			${err}
			<c:set var="parse" value="true"/>		
		</c:if>
		<c:if test="${parse}">
			<a:fileproperties params="params" recordsToRead="3" filename="${initParam['attachments-dir']}/${filename}"/>
			<c:catch var ="err">
			<sql:transaction dataSource="jdbc/qmrf_documents">
				<sql:update var="updateCount" >
					delete from attachments_description where idattachment=?
					<sql:param value="${param.idattachment}"/>
				</sql:update>		
				<c:forEach var="name" items="${params}">
					<c:if test="${!empty name.key}">
					<sql:update var="updateCount" >
						insert into attachments_description (idattachment,fieldname,fieldtype,newname) values (?,?,?,?)
						<sql:param value="${param.idattachment}"/>					
						<sql:param value="${name.key}"/>
						<sql:param value="ignore"/>
						<sql:param value="${name.value}"/>
					</sql:update>
					</c:if>
				</c:forEach>
			</sql:transaction>
			</c:catch>
		</c:if>
		
	</c:when>
	<c:otherwise>
		<c:catch var ="err">
			<sql:transaction dataSource="jdbc/qmrf_documents">
				<c:forEach var="p" begin="1" end="${param.properties}" step="1">
					
					<c:set var="n" value="name_${p}"/>
					<c:set var="r" value="property_${p}"/>
					<c:set var="w" value="newname_${p}"/>
										
					<sql:update var="updateCount" >
						update attachments_description set fieldtype=?,newname=? where idattachment=? and fieldname=?
						<sql:param value="${param[r]}"/>
						<sql:param value="${param[w]}"/>						
						<sql:param value="${param.idattachment}"/>
						<sql:param value="${param[n]}"/>
					</sql:update>
				</c:forEach>
			</sql:transaction>
		</c:catch>
	</c:otherwise>
	</c:choose>
	
	<c:if test="!empty err">
			${err}
	</c:if>
	
	<c:catch var = "err">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idattachment,fieldname,fieldtype,newname from attachments_description where idattachment=?
			<sql:param value="${param.idattachment}"/>
		</sql:query>	

		<c:set var="r" value="0"/>	
		<form method="POST" action="">
			<table width="95%" rules="groups" frame="box" >
				<thead>
				<tr bgcolor="#FFFF99">
					<th>Attachment</th>
					<th colspan="10">${filename}</th>
				</tr>
				<tr valign="top" bgcolor="#FFFF99">
					<th>#</th>
					<th>Properties
					<a href="help.jsp?anchor=properties" target="help"><img src="images/help.png" alt="help" title="What is it?" border="0"></a></th>
					<th>Import as (type)</th>
					<th>Import as (name)</th>
				</tr>
				</thead>
				<tbody>
				
				<c:forEach var="row" items="${rs.rows}">
					<c:set var="r" value="${r+1}"/>		
					<c:set var="clr" value="#C0C0C0"/>
					<c:if test="${(r % 2)==0}" >
						<c:set var="clr" value="#CCFFFF"/>
					</c:if>
					
					<tr bgcolor="${clr}">
						<td>${r}</td>
						<td width="15%">
						${row.fieldname}
						<input type="hidden"  name="name_${r}" value="${row.fieldname}">
						</td>
						<td>
						<select name="property_${r}">
							<c:forTokens var="t" items="${tokens}" delims=",">
								<c:set var="checked" value=""/>
								<c:if test="${row.fieldtype eq t}">
									<c:set var="checked" value="selected"/>
								</c:if>
								<option value ="${t}" ${checked}>${t}</option>
							</c:forTokens>
						</select>
						
						</td>
						<td>
						<input type="text" size="40" name="newname_${r}" value="${row.newname}"/>
						</td>
					</tr>
				</c:forEach>
				
				</tbody>
				<tfoot>
				<tr>
				<td>
				<input type="hidden" name="properties" value="${r}"/>
				</td>
				<td><input type="submit" value="update properties"/></td>
				<td></td>
				</tr>
				</tfoot>			
			</table>
		</form>	
	</c:catch>
</c:if>


</c:catch>
<c:if test="${!empty err}">
	<blockquote>
	<div class="error">
		${err}
	</div>
	</blockquote>	
</c:if>

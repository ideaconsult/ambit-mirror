<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>   

<c:if test="${!empty param.xml}">

<x:parse xml="${param.xml}" var="doc" systemId="/WEB-INF/xslt/qmrf.dtd"/>
	
<h3>
	<x:out select="$doc//QMRF/QMRF_chapters/QSAR_Miscelaneous/@chapter"/>
		<xsl:text>.</xsl:text>
	<x:out select="$doc//QMRF/QMRF_chapters/QSAR_Miscelaneous/@name"/>	
</h3>		
<table border="0" bgcolor="#FFFFFF">
		<tr bgcolor="#DDDDDD" >
		<th>File name</th>
		<th>File format</th>
		<th>Attachment type format</th>
		<th>Description of the file</th>
		</tr>
		<x:forEach select="$doc//QMRF/QMRF_chapters/QSAR_Miscelaneous/attachments/attachment_training_data">
		<tr>
			<td align="left">
					<x:out select="molecules/@url"/>	
					<x:out select="molecules/@type"/>	
			</td>
			<td align="left">
					<x:out select="molecules/@filetype"/>	
			</td>
			<td>Training data set</td>
			<td align="left">
					<x:out select="molecules/@description"/>
			</td>						
		</tr>
		</x:forEach>
		
				<x:forEach select="$doc//QMRF/QMRF_chapters/QSAR_Miscelaneous/attachments/attachment_validation_data">
		<tr>
			<td align="left">
					<x:out select="molecules/@url"/>	
					<x:out select="molecules/@type"/>	
			</td>
			<td align="left">
					<x:out select="molecules/@filetype"/>	
			</td>
			<td>Validation data set</td>
			<td align="left">
					<x:out select="molecules/@description"/>
			</td>						
		</tr>
		</x:forEach>
						<x:forEach select="$doc//QMRF/QMRF_chapters/QSAR_Miscelaneous/attachments/attachment_documents_data">
		<tr>
			<td align="left">
					<x:out select="molecules/@url"/>	
					<x:out select="molecules/@type"/>	
			</td>
			<td align="left">
					<x:out select="molecules/@filetype"/>	
			</td>
			<td>Supporting info</td>
			<td align="left">
					<x:out select="molecules/@description"/>
			</td>						
		</tr>
		</x:forEach>
</table>	
	
</c:if>

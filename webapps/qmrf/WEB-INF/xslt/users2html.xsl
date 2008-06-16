<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:param name="selected"/>
<xsl:param name="header"/>

<xsl:template match="QMRF">
</xsl:template>

<xsl:template match="users">

<table width="95%" rules="groups" frame="box">
<thead>
<tr valign="top">
<td colspan="14">
<xsl:value-of select="$header"/>
</td>
</tr>
<tr>
<th>User name</th>
<th>E-mail</th>
<th>Title</th>
<th>First name</th>
<th>Last name</th>
<th>Affiliation</th>
<th>Address</th>
<th>Country</th>
<th>Webpage</th>
<th>Role</th>
<th>Available as a reviewer</th>
<th>Keywords</th>
<th>Registration status</th>
<th>Registration Date</th>

</tr>
</thead>
<tbody>
<xsl:apply-templates select="* "/>
</tbody>
</table>
</xsl:template>

<xsl:template match="user">
	<tr>
	<xsl:choose>
		<xsl:when test= "@registration_status = 'verified'">
			<xsl:attribute name="bgcolor">
			<xsl:text>#FF8888</xsl:text>
		</xsl:attribute>
		</xsl:when>
		<xsl:otherwise>

		</xsl:otherwise>	
	</xsl:choose>
	<td>
	<a>
		<xsl:attribute name="href">
			<xsl:text>#</xsl:text>
			<xsl:value-of select="@user_name"/>
		</xsl:attribute>
		<xsl:value-of select="@user_name"/>
	</a>		
	
	</td>
	<td>
	<xsl:value-of select="@email"/>
	</td>


	<td>
	<xsl:value-of select="@title"/>
	</td>
	<td>
	<xsl:value-of select="@firstname"/>
	</td>
	<td>
	<xsl:value-of select="@lastname"/>
	</td>
	<td>
	<xsl:value-of select="@affiliation"/>
	</td>
	<td>
	<xsl:value-of select="@address"/>
	</td>
	<td>
	<xsl:value-of select="@country"/>
	</td>
	<td>
	 		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="@webpage" />
			</xsl:attribute>
			<xsl:value-of select="@webpage"/>
			</a>	
	</td>
	<td>
		<xsl:for-each select="role">
				<xsl:if test="@selected = 'true'">
					<xsl:choose>
						<xsl:when test="@name = 'qmrf_admin'">
							<xsl:text>Reviewer </xsl:text>
						</xsl:when>
						<xsl:when test="@name = 'qmrf_user'">
							<xsl:text>Author </xsl:text>
						</xsl:when>
						<xsl:when test="@name = 'qmrf_editor'">
							<xsl:text>Editor </xsl:text>
						</xsl:when>
						<xsl:when test="@name = 'qmrf_manager'">
							<xsl:text>Administrator </xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>Unknown </xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>	
		</xsl:for-each>
	</td>	
	<td>
	<xsl:value-of select="@reviewer"/>
	</td>
	<td>
	<xsl:value-of select="@keywords"/>
	</td>		
	<td>
		<xsl:choose>
			<xsl:when test= "@registration_status = 'verified'">
				<xsl:element name="a">
					<xsl:attribute name="href">
						<xsl:text>rconfirm.jsp</xsl:text>?user_name=<xsl:value-of select="@user_name"/>
					</xsl:attribute>
					<xsl:attribute name="title">Confirm user registration</xsl:attribute>					
					<xsl:text>Confirm</xsl:text>
				</xsl:element>	
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@registration_status"/>
			</xsl:otherwise>	
		</xsl:choose>
	</td>
	<td>
	<xsl:value-of select="@registration_date"/>
	</td>	
	</tr>

</xsl:template>

<xsl:template match="author1">
		<tr bgcolor="#EEEEEE">
		<td>Author number</td>
		<td>
		<xsl:value-of select="@number"/>
		</td>
		</tr>
		<tr bgcolor="#EEEEEE">
		<td>Name</td>
		<td>
		<xsl:value-of select="@name"/>
		</td>
		</tr>
		<tr bgcolor="#EEEEEE">
		<td>e-mail</td>
		<td>
		<xsl:value-of select="@email"/>
		</td>
		</tr>
		<tr>
		<td>Affiliation</td>
		<td>
		<xsl:value-of select="@affiliation"/>
		</td>
		</tr>
		<tr>
		<td>Contact details</td>
		<td>
		<xsl:value-of select="@contact"/>
		</td>
		</tr>
		<tr>
		<td>WWW</td>
		<td>
	 		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="@url" />
			</xsl:attribute>
			<xsl:value-of select="@url"/>
			</a>
		</td>
		</tr>
</xsl:template>





 </xsl:stylesheet>



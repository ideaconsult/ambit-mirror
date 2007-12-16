<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  indent="yes"/>

<xsl:param name="selected"/>

<xsl:template match="QMRF">
</xsl:template>

<!-- Hide catalogs  -->
<xsl:template match="users">

<xsl:apply-templates select="* "/>

</xsl:template>






<xsl:template match="user">
	<table bgcolor="#DDDDDD">
	<tr>
	<th>
	User name
	</th>
	<td>
	<xsl:value-of select="@user_name"/>
	</td>
	</tr>
	<tr>
	<th>
	email
	</th>
	<td>
	<xsl:value-of select="@email"/>
	</td>
	</tr>
	<tr>

	<th>
	Registration status
	</th>
	<td>
	<xsl:value-of select="@registration_status"/>
	</td>
	</tr>
	<tr>

	<th>
	Registration Date
	</th>
	<td>
	<xsl:value-of select="@registration_date"/>
	</td>
	</tr>
	<tr>

	<th>
	Title
	</th>
	<td>
	<xsl:value-of select="@title"/>
	</td>
	</tr>
	<tr>

	<th>
	First name
	</th>
	<td>
	<xsl:value-of select="@firstname"/>
	</td>
	</tr>
	<tr>

	<th>
	Last name
	</th>
	<td>
	<xsl:value-of select="@lastname"/>
	</td>
	</tr>
	<tr>

	<th>
	Affiliation
	</th>
	<td>
	<xsl:value-of select="@affiliation"/>
	</td>
	</tr>
	<tr>

	<th>
	Address
	</th>
	<td>
	<xsl:value-of select="@address"/>
	</td>
	</tr>
	<tr>

	<th>
	Country
	</th>
	<td>
	<xsl:value-of select="@country"/>
	</td>
	</tr>
	<tr>

	<th>
	WWW
	</th>
	<td>
	<xsl:value-of select="@webpage"/>
	</td>
	</tr>
	<tr bgcolor="#FFFFFF">
	<td colspan="2"></td>
	</tr>
	</table>
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



<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:param name="selected"/>

<xsl:template match="QMRF">
</xsl:template>

<!-- Hide catalogs  -->
<xsl:template match="Catalogs">

<xsl:apply-templates select="* "/>

</xsl:template>

<xsl:template match="endpoints_catalog">
		<table border="0" width="100%" bgcolor="#EEEEEE">
		<tr bgcolor="#D6DFF7">
		<th colspan="3" align="left">Endpoints</th>
		</tr>
				<xsl:apply-templates select="* "/>
		</table>
</xsl:template>

<xsl:template match="endpoint">
		<tr bgcolor="#FFFFFF" align="left">
		<td>
					<xsl:value-of select="@group"/>
						<xsl:text> </xsl:text>
		</td>
		<td>
						<xsl:value-of select="@subgroup"/>
						<xsl:text> </xsl:text>
		</td>
		<td>
						<xsl:value-of select= "@name"/>
		</td>
		</tr>
</xsl:template>

<xsl:template match="algorithms_catalog">
		<table border="0" width="100%" bgcolor="#EEEEEE">
		<tr bgcolor="#D6DFF7" >
		<th colspan="2">Algorithms</th>
		</tr>
		<tr bgcolor="#D6DFF7">
		<th>Description</th>
		<th>Definition</th>
		</tr>
				<xsl:apply-templates select="* "/>
		</table>
</xsl:template>


<xsl:template match="algorithm">
		<tr bgcolor="#FFFFFF">
		<td>
			<xsl:value-of select="@definition"/>
		</td>
		<td>
			<xsl:value-of select="@definition"/>
		</td>

		</tr>

</xsl:template>



<xsl:template match="authors_catalog">
		<table border="0" width="100%" bgcolor="#EEEEEE">
		<tr bgcolor="#D6DFF7" >
		<th colspan="6">Authors</th>
		</tr>
		<tr bgcolor="#D6DFF7">
		<th>Number</th>
		<th>Name</th>
		<th>Affiliation</th>
		<th>Contact</th>
		<th>E-mail</th>
		<th>WWW</th>
		</tr>
				<xsl:apply-templates select="* "/>
		</table>
</xsl:template>



<xsl:template match="author">
		<tr bgcolor="#FFFFFF" align="left">
		<td>
  			<xsl:value-of select="@number"/>
  		</td>
		<td>
  			<xsl:value-of select="@name"/>
  		</td>
		<td>
  			<xsl:value-of select="@affiliation"/>
  		</td>
		<td>
  			<xsl:value-of select="@contact"/>
  		</td>
  		<td>
  			<xsl:value-of select="@email"/>
  		</td>
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



<xsl:template match="software_catalog">
		<table border="0" width="100%" bgcolor="#EEEEEE">
		<tr bgcolor="#D6DFF7" >
		<th colspan="6">Software</th>
		</tr>
		<tr bgcolor="#D6DFF7">
		<th>Number</th>
		<th>Name</th>
		<th>Description</th>
		<th>Contact</th>
		<th>WWW</th>
		</tr>
				<xsl:apply-templates select="* "/>
		</table>

</xsl:template>

<xsl:template match="software">
		<tr bgcolor="#FFFFFF" align="left">
		<td><b>
		<xsl:value-of select="@number"/>
		</b>
		</td>
		<td>
		<xsl:value-of select="@name"/>
		</td>

		<td>
		<xsl:value-of select="@description"/>
		</td>
		<td>
		<xsl:value-of select="@contact"/>
		</td>
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



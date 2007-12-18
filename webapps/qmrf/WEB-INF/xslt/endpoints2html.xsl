<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:template match="QMRF">
</xsl:template>

<!-- Hide catalogs  -->
<xsl:template match="Catalogs">
<xsl:apply-templates select="* "/>
</xsl:template>

<xsl:template match="endpoints_catalog">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<body>
		<h3>Available endpoints:</h3>
		<table width="80%">
		<xsl:apply-templates select="* "/>
		</table>
		</body>
		</html>
</xsl:template>

<xsl:template match="endpoint">
		<tr bgcolor="#EEEEEE">
		<td>
		<xsl:value-of select="@group"/>
		</td>
		<td>
		<xsl:value-of select="@subgroup"/>
		</td>
		<td>
		<xsl:value-of select="@name"/>
		</td>
		</tr>
</xsl:template>

<xsl:template match="algorithms_catalog">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<body>
		<h3>Algorithms list:</h3>
		<table width="80%">
		<xsl:apply-templates select="* "/>
		</table>
		</body>
		</html>
</xsl:template>

<xsl:template match="algorithm">
<tr bgcolor="#EEEEEE">
		<td>Algorithm number</td>
		<td>
		<xsl:value-of select="@id"/>
		</td>
		</tr>
		<tr bgcolor="#EEEEEE">
		<td>Description</td>
		<td>
		<xsl:value-of select="@description"/>
		</td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>Definition</td>
		<td>
		<xsl:value-of select="@definition"/>
		</td>
		</tr>
</xsl:template>

<xsl:template match="authors_catalog">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<body>
		<h3>QMRF authors:</h3>
		<table width="80%">
		<xsl:apply-templates select="* "/>
		</table>
		</body>
		</html>
</xsl:template>

<xsl:template match="author">
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
		<xsl:call-template name="print_href"/>
		</td>
		</tr>
</xsl:template>


<xsl:template match="software_catalog">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<body>
		<h3>Software list:</h3>
		<table width="80%">
		<xsl:apply-templates select="* "/>
		</table>
		</body>
		</html>
</xsl:template>

<xsl:template match="software">
		<tr bgcolor="#EEEEEE">
		<td>Software number</td>
		<td><b>
		<xsl:value-of select="@number"/>
		</b>
		</td>
		</tr>
		<tr bgcolor="#EEEEEE">
		<td>Name</td>
		<td>
		<xsl:value-of select="@name"/>
		</td>
		</tr>
		<tr bgcolor="#EEEEEE">
		<td>Description</td>
		<td>
		<xsl:value-of select="@description"/>
		</td>
		</tr>
		<tr>
		<td>Contact</td>
		<td>
		<xsl:value-of select="@contact"/>
		</td>
		</tr>
		<tr>
		<td>WWW</td>
		<td>
		<xsl:call-template name="print_href"/>
		</td>
		</tr>
		<tr bgcolor="#FFFFFF"><td colspan="2"> </td></tr>
</xsl:template>

 <xsl:template name="print_href">
	 		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="@url" />
			</xsl:attribute>
			<xsl:value-of select="@url"/>
			</a>
 </xsl:template>


 </xsl:stylesheet>



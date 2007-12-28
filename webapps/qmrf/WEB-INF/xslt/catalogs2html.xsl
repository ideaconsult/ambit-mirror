<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:param name="selected"/>
<xsl:param name="footer"/>
<xsl:param name="reportdate"/>

<xsl:template match="QMRF">
</xsl:template>

<!-- Hide catalogs  -->
<xsl:template match="Catalogs">

<xsl:apply-templates select="* "/>

</xsl:template>

<xsl:template match="endpoints_catalog">
		<table width="95%" rules="groups" frame="box">
		<thead>
		<tr >
		<th colspan="3" align="center">Endpoints</th>
		</tr>
		<tr >
		<th colspan="2" align="left">Group</th>
		<th colspan="2" align="left">Name</th>
		</tr>
		</thead>
		<tbody>
				<xsl:apply-templates select="* "/>
		</tbody>
		<tfoot>
			<tr>
			<td colspan="2" align="left"><xsl:value-of select="$footer"/></td>
			<td align="right"><xsl:value-of select="$reportdate"/></td>
			</tr>
		</tfoot>
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
		<table width="95%" rules="groups" frame="box">
		<thead>
		<tr >
		<th colspan="2">Algorithms</th>
		</tr>
		<tr >
		<th align="left">Description</th>
		<th align="left">Definition</th>
		</tr>
		</thead>
		<tbody>
				<xsl:apply-templates select="* "/>
		</tbody>
		<tfoot>
			<tr>
			<td colspan="2" align="left"><xsl:value-of select="$footer"/></td>
			<td align="right"><xsl:value-of select="$reportdate"/></td>
			</tr>
		</tfoot>		
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
		<table width="95%" rules="groups" frame="box">
		<thead>
		<tr >
		<th colspan="6" align="center">Authors</th>
		</tr>
		<tr >
		<th align="left">Number</th>
		<th align="left">Name</th>
		<th align="left">Affiliation</th>
		<th align="left">Contact</th>
		<th align="left">E-mail</th>
		<th align="left">WWW</th>
		</tr>
		</thead>
		<tbody>
				<xsl:apply-templates select="* "/>
		</tbody>
		<tfoot>
			<tr>
			<td colspan="2" align="left"><xsl:value-of select="$footer"/></td>
			<td align="right"><xsl:value-of select="$reportdate"/></td>
			</tr>
		</tfoot>		
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
		<table width="95%" rules="groups" frame="box">
		<thead>
		<tr  >
		<th align="center" colspan="6">Software packages</th>
		</tr>
		<tr >
		<th align="left">Number</th>
		<th align="left">Name</th>
		<th align="left">Description</th>
		<th align="left">Contact</th>
		<th align="left">WWW</th>
		</tr>
		</thead>
		<tbody>
				<xsl:apply-templates select="* "/>
		</tbody>
		<tfoot>
			<tr>
			<td colspan="2" align="left"><xsl:value-of select="$footer"/></td>
			<td align="right"><xsl:value-of select="$reportdate"/></td>
			</tr>
		</tfoot>		
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



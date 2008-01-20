<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:template match="molecules">
	<table width="95%" rules="groups" frame="box" >
		<thead>
		<tr bgcolor="#FFFF99">
			<th>Attachment</th>
			<th colspan="10">
			<xsl:element name="a">
				 <xsl:attribute name="href">
				  <xsl:value-of select="@url" /> 
				  </xsl:attribute>
				  <xsl:value-of select="@description" /> 
		  	</xsl:element>
			</th>
		</tr>
		<tr valign="top" bgcolor="#FFFF99">
			<th>#</th>
			<th>Properties</th>
			<th>Import as (type)</th>
			<th>Import as (name)</th>
		</tr>
		</thead>
		<tbody>
			<xsl:apply-templates select="* "/>
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
</xsl:template>

<xsl:template match="property">
		<tr>
		<td></td>
		<td><xsl:value-of select="@fieldname"/></td>
		<td><xsl:value-of select="@fieldtype"/></td>
		<td><xsl:value-of select="@newname"/></td>
		</tr>
</xsl:template>


</xsl:stylesheet>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:n="http://www.xml-cml.org/schema"
	xmlns:cml="http://www.xml-cml.org/dict/cml"
	xmlns:units="http://www.xml-cml.org/units/units"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:iupac="http://www.iupac.org" 
                 >
<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:template match="n:molecule">
	<table rules="groups" frame="box">
	<xsl:apply-templates select="* "/>
	</table>
</xsl:template>

<xsl:template match="n:name">
	<thead>
	</thead>
	<tbody>
	<tr>
	<td><xsl:value-of select="@convention"/></td>
	<td colspan="2"><xsl:value-of select="text()"/></td>
	</tr>
	</tbody>
</xsl:template>

<xsl:template match="n:identifier">
	<thead>
	</thead>
	<tbody>
	<tr>
	<th><xsl:value-of select="@convention"/></th>
	<td colspan="2"><xsl:value-of select="@value"/></td>
	</tr>
	</tbody>
</xsl:template>

<xsl:template match="n:formula">
	<thead>
	<th>Formula</th><td><xsl:value-of select="@concise"/></td>
	</thead>
</xsl:template>

<xsl:template match="n:propertyList">
	<thead>
	<tr><th colspan="3" align="left">Properties</th></tr>
	</thead>
	<tbody>
	<xsl:apply-templates select="* "/>
	</tbody>
</xsl:template>

<xsl:template match="n:property">
	<tr>
	<td><xsl:value-of select="@title"/></td>
	<xsl:apply-templates select="* "/>	

	</tr>
</xsl:template>

<xsl:template match="n:scalar">
	<td>
	<xsl:value-of select="text()"/>
	</td>
	<td>
	<xsl:value-of select="@units"/>
	</td>
	
</xsl:template>

 </xsl:stylesheet>



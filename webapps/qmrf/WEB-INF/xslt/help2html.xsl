<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:template match="qmrf_help">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<link href="styles/nstyle.css" rel="stylesheet" type="text/css"/>
		<title>QMRF help</title><body>
				<xsl:apply-templates select="* "/>
	  </body></html>
</xsl:template>


<!-- Chapter heading-->
<xsl:template match="*">
			<dl>
			<dt>
			<xsl:choose>

			<xsl:when test="contains(@level,'1')">
				<h1>
				<xsl:call-template name="print_anchor"/>
				</h1>
			</xsl:when>
			<xsl:when test="contains(@level,'2')">
				<h2>
				<xsl:call-template name="print_anchor"/>
				</h2>
			</xsl:when>
			<xsl:otherwise>
				<h3>
				<xsl:call-template name="print_anchor"/>
				</h3>
			</xsl:otherwise>
			</xsl:choose>

				<xsl:apply-templates select="*"/>
				</dt>
				</dl>
</xsl:template>

<xsl:template match="help">
				<dd>
				<xsl:value-of disable-output-escaping="no" select="text()"/>
				</dd>
</xsl:template>



 <xsl:template name="print_anchor">
	 		<a>
			<xsl:attribute name="name">
			<xsl:value-of select="@anchor" />
			</xsl:attribute>
			<xsl:value-of select="@name"/>
			</a>
 </xsl:template>

 </xsl:stylesheet>



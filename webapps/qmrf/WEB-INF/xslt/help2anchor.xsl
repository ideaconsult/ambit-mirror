<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:param name="anchor"/>

<xsl:template match="qmrf_help">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
		<link href="styles/nstyle.css" rel="stylesheet" type="text/css"/>
		<title>QMRF help</title>
		</head>
		<body>
				<xsl:apply-templates select="* "/>
	  </body></html>
</xsl:template>


<!-- Chapter heading-->
<xsl:template match="chapter">
			<xsl:choose>
			<xsl:when test="$anchor = @anchor">	
				<div class="help">
				<h3>
				<xsl:call-template name="print_anchor"/>
				</h3>
				<xsl:for-each select="help">
					<p>
					<xsl:value-of disable-output-escaping="no" select="text()"/>
					</p>
				</xsl:for-each>
				</div>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="*"/>

</xsl:template>

<xsl:template match="help">

</xsl:template>

<xsl:template match="b">
	<b>
	<xsl:value-of disable-output-escaping="no" select="text()"/>
	</b>
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



<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/1999/xhtml">

<xsl:output method="xml"  encoding="utf-8" doctype-system="/WEB-INF/xslt/qmrf.dtd"  indent="yes"/>

<xsl:template match="@*|node()">
	<xsl:copy>
		<xsl:apply-templates select="@*|node()"/>
	</xsl:copy>
</xsl:template>
<xsl:template match="comment()"/>

<xsl:template match="attachments">
	<xsl:copy>
				<xsl:attribute name="chapter">
					<xsl:value-of select="@chapter" />
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="@name" />
				</xsl:attribute>
	</xsl:copy>
</xsl:template>
</xsl:stylesheet>

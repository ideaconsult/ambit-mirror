<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">
<xsl:output method="txt"  encoding="UTF-8" indent="yes"/>

<!-- Hide catalogs  -->
<xsl:template match="Catalogs">
</xsl:template>

<xsl:template match="/">
</xsl:template>
<xsl:template match="//QSAR_title">
					<xsl:value-of disable-output-escaping="yes" select="text()"/>
</xsl:template>

 </xsl:stylesheet>



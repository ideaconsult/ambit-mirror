<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="xml"  encoding="utf-8" doctype-system="/WEB-INF/xslt/qmrf.dtd"
		doctype-public="http://ambit.acad.bg/qmrf/qmrf.dtd" indent="yes"/>

<xsl:param name="keywords"/>
<xsl:param name="summary_comments"/>

<xsl:template match="QMRF_chapters/QMRF_Summary/keywords/text()">
	<xsl:value-of disable-output-escaping="no" select="$keywords" />
</xsl:template>

<xsl:template match="QMRF_chapters/QMRF_Summary/summary_comments/text()">
	<xsl:value-of disable-output-escaping="no" select="$summary_comments" />
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
 </xsl:stylesheet>
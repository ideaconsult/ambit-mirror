<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="xml"  encoding="utf-8" doctype-system="/WEB-INF/xslt/qmrf.dtd" indent="yes"/>

<xsl:param name="qnumber"/>
<xsl:param name="qdate"/>

<xsl:template match="QMRF_chapters/QMRF_Summary/QMRF_number/text()">
 	<xsl:value-of select="$qnumber"/>
</xsl:template>

<xsl:template match="QMRF_chapters/QMRF_Summary/date_publication/text()">
	<xsl:value-of select="$qdate"/>
</xsl:template>




<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
 </xsl:stylesheet>
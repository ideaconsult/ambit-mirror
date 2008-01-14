<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="xml"  encoding="utf-8" doctype-system="/WEB-INF/xslt/qmrf.dtd" indent="yes"/>


<xsl:variable name="amp">&amp;</xsl:variable>
<xsl:template match="*">
  <xsl:copy>
    <xsl:copy-of select="@*"/>

		<xsl:choose>
			<!--  If help attribute exists, add reference to &help entity-->
			<xsl:when test="@help">

			    <xsl:attribute name="help">
				<xsl:text></xsl:text>
			    </xsl:attribute>

			</xsl:when>	
			<xsl:otherwise>

			</xsl:otherwise>						
		</xsl:choose>	    
		

    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>

                 
 </xsl:stylesheet>



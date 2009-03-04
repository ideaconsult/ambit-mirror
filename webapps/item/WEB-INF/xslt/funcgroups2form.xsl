<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  indent="yes"/>

<xsl:param name="selected"/>


<xsl:template match="funcgroups">
		<SELECT NAME="smarts" >
				<xsl:apply-templates select="* "/>
		</SELECT>
</xsl:template>


<xsl:template match="group">
		<OPTION>
				<xsl:attribute name="value">
						<xsl:value-of select="@smarts"/>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="contains(@smarts,$selected)">
							<xsl:attribute name="selected">
							<xsl:text>yes</xsl:text>
							</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>

  			<xsl:value-of select="@name"/>

		</OPTION>

</xsl:template>

 </xsl:stylesheet>



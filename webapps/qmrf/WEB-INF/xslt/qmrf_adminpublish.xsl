<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:template match="QMRF">
 		<xsl:apply-templates select="* "/>



</xsl:template>

<!-- Hide catalogs  -->
<xsl:template match="Catalogs">
</xsl:template>


<!-- Chapter heading-->
<xsl:template name="print_chapters">
			<xsl:choose>

			<xsl:when test="contains(@chapter,'.')">
				<h3>
				<xsl:value-of select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of select="@name"/>
				</h3>
			</xsl:when>

			<xsl:otherwise>
			</xsl:otherwise>
			</xsl:choose>
</xsl:template>

<xsl:template match="*">

			<xsl:choose>

			<xsl:when test="contains(@chapter,'10.1')">
				<h3>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h3>

			</xsl:when>
			<xsl:when test="contains(@chapter,'10.2')">
				<h3>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h3>

			</xsl:when>
			<xsl:when test="contains(@chapter,'10.3')">
				<h3>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h3>

			</xsl:when>
			<xsl:when test="contains(@chapter,'10.4')">
				<h3>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h3>

			</xsl:when>


			<xsl:otherwise>

			</xsl:otherwise>
			</xsl:choose>

			<xsl:apply-templates select="*"/>

</xsl:template>


<xsl:template match="training_set_data">
</xsl:template>

<xsl:template match="validation_set_data">
</xsl:template>

<xsl:template match="attachments">
</xsl:template>


<xsl:template match="QMRF_chapters/QSAR_identifier/QSAR_title">
<tr><th colspan="2"><h3>1. QSAR identifier </h3></th></tr>
	<tr><th>1.1.QSAR identifier (title)</th>
	<td>
  		<xsl:value-of disable-output-escaping="yes" select="text()"/>
	</td>
	</tr>

</xsl:template>
<xsl:template match="QMRF_chapters/QMRF_Summary/QMRF_number">
	<tr><th colspan="2">
	<h3>10. Summary (JRC Inventory)</h3>
	</th>
	</tr>
	<tr><th>10.1.QMRF number</th>
	<td>
	<input type="text" name="QMRF_number" size="80">
		<xsl:attribute name="value">
  		<xsl:value-of select="text()"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>

</xsl:template>
<xsl:template match="QMRF_chapters/QMRF_Summary/date_publication">

	<tr><th>10.2.Publication date</th>
	<td>
	<input type="text" name="date_publication" size="80">
		<xsl:attribute name="value">
  		<xsl:value-of select="text()"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>

</xsl:template>
<xsl:template match="QMRF_chapters/QMRF_Summary/keywords">

	<tr><th>10.3.Keywords</th>
	<td>
  		<xsl:value-of select="text()"/>
	</td>
	</tr>

</xsl:template>
<xsl:template match="QMRF_chapters/QMRF_Summary/summary_comments">

	<tr><th>10.4.Summary comments</th>
	<td>
  		<xsl:value-of select="text()"/>
	</td>
	</tr>

</xsl:template>




<xsl:template match="descriptor_ref">
</xsl:template>


 <xsl:template name="print_href">
	 		<a>
			<xsl:attribute name="href">
			<xsl:value-of select="@url" />
			</xsl:attribute>
			<xsl:value-of select="@url"/>
			</a>
 </xsl:template>




 </xsl:stylesheet>



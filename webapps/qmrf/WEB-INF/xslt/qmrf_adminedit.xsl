<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:template match="QMRF">
	<table bgcolor="#FFFFFF" width="95%">
	<xsl:apply-templates select="* "/>
	</table>


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

		<xsl:apply-templates select="*"/>


</xsl:template>


<xsl:template match="training_set_data">
</xsl:template>

<xsl:template match="validation_set_data">
</xsl:template>

<xsl:template match="attachments">
</xsl:template>


<xsl:template match="QMRF_number">
		<h3>
		<xsl:value-of disable-output-escaping="no" select="@chapter"/>
		<xsl:text>.</xsl:text>
		<xsl:value-of disable-output-escaping="no" select="@name"/>
		</h3>
</xsl:template>

<xsl:template match="date_publication">
		<h3>
		<xsl:value-of disable-output-escaping="no" select="@chapter"/>
		<xsl:text>.</xsl:text>
		<xsl:value-of disable-output-escaping="no" select="@name"/>
		</h3>
</xsl:template>
<xsl:template match="keywords">
		<h3>
		<xsl:value-of disable-output-escaping="no" select="@chapter"/>
		<xsl:text>.</xsl:text>
		<xsl:value-of disable-output-escaping="no" select="@name"/>
		</h3>
</xsl:template>

<xsl:template match="summary_comments">
		<h3>
		<xsl:value-of disable-output-escaping="no" select="@chapter"/>
		<xsl:text>.</xsl:text>
		<xsl:value-of disable-output-escaping="no" select="@name"/>
		</h3>
</xsl:template>

<xsl:template match="QMRF_chapters/QSAR_identifier/QSAR_title">
<tr bgcolor="#CCCCCC"><th colspan="2"><h3>1. QSAR identifier </h3></th>
	<td width="25%" bgcolor="#FFFFFF" valign="top" rowspan="7">
			<div class="success">
			Review QMRF document
			</div>
			<br/>
			<input type="submit" value="Update"/>
			<br/>
			<div class="help">
			Please review the QMRF document (you could download the document from the links above).
			<br/>Enter keywords and comments and click <u>Update</u> button.
			<br/>
			<a>
				<xsl:attribute name="href">
				http://ecb.jrc.it/qsar/qsar-tools/qrf/Guidelines_for_reviewing_the_QMRF.pdf
				</xsl:attribute>
				<xsl:attribute name="target">
				_blank
				</xsl:attribute>
				Guidelines for reviewing QMRFs
			</a>
			</div>
			<br/>
			<div class="help">
			NOTE: Clicking on <u>Review</u> link will result in keywords and comments changes being lost, if those has not been followed by <u>Update</u>
			</div>
			<br/>
			<div class="help">
			NOTE: Click on <u>Publish</u> when the document is ready to be published.
			</div>
			<br/>
			<div class="help">
			NOTE: Click on <u>Return to author</u> when the document has to be returned to the author for revision.
			</div>
	</td>
</tr>
	<tr bgcolor="#CCCCCC"><th>1.1.QSAR identifier (title)</th>
	<td>
  		<xsl:value-of disable-output-escaping="yes" select="text()"/>
	</td>
	</tr>

</xsl:template>

<xsl:template match="QMRF_chapters/QMRF_Summary/QMRF_number">

	<tr bgcolor="#CCCCCC"><th colspan="2"><h3>10. Summary (JRC Inventory)</h3></th></tr>

	<tr bgcolor="#CCCCCC"><th>10.1.QMRF number</th>
	<td>
  		<xsl:value-of select="text()"/>
	</td>
	</tr>

</xsl:template>
<xsl:template match="QMRF_chapters/QMRF_Summary/date_publication">

	<tr bgcolor="#CCCCCC"><th>10.2.Publication date</th>
	<td>
  		<xsl:value-of select="text()"/>
	</td>
	</tr>

</xsl:template>
<xsl:template match="QMRF_chapters/QMRF_Summary/keywords">

	<tr bgcolor="#CCCCCC"><th>10.3.Keywords</th>
	<td>
	<input type="text" name="keywords" size="80">
		<xsl:attribute name="value">
  		<xsl:value-of select="text()"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>

</xsl:template>
<xsl:template match="QMRF_chapters/QMRF_Summary/summary_comments">

	<tr bgcolor="#CCCCCC"><th>10.4.Summary comments</th>
	<td>
	<textarea cols="80" rows="10" name="summary_comments">
  		<xsl:value-of disable-output-escaping="yes" select="text()"/>
	</textarea>
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

<xsl:template match="body">
	<xsl:value-of select="text()"/>
</xsl:template>
<xsl:template match="p">
	<xsl:value-of select="text()"/>
</xsl:template>
<xsl:template match="html">
	<xsl:value-of select="text()"/>
</xsl:template>

 </xsl:stylesheet>



<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  indent="yes"/>

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

<xsl:template match="QMRF_chapters/QSAR_identifier/QSAR_title">
	<h3>1.1.QSAR identifier (title)</h3>
	  		<xsl:value-of disable-output-escaping="yes" select="text()"/>

	</xsl:template>

<xsl:template match="*">

			<xsl:choose>

			<xsl:when test="contains(@chapter,'1.3')">
				<h3>
				<a name="software_form">
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</a>
				</h3>

			</xsl:when>
			<xsl:when test="contains(@chapter,'2.5')">
				<h3>
				<a name="authors_form">
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</a>
				</h3>

			</xsl:when>

			<xsl:when test="contains(@chapter,'3.2')">
				<h3>
				<a name="endpoints_form">
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</a>
				</h3>

			</xsl:when>

			<xsl:when test="contains(@chapter,'4.2')">
				<h3>
				<a name="algorithms_form">
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</a>
				</h3>

			</xsl:when>

			<xsl:when test="contains(@chapter,'4.6')">
				<h3>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h3>

			</xsl:when>
			<xsl:when test="contains(@chapter,'2.2')">
				<h3>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h3>

			</xsl:when>
			<xsl:when test="contains(@chapter,'5.3')">
				<h3>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h3>

			</xsl:when>
			<xsl:when test="@idref">
				<xsl:value-of select="id(@idref)/@name"/>

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


<xsl:template match="software_ref">
	<form method="POST" name="software_form">

	<table bgcolor="#FFB0B0">
	<tr><th>Name</th>
	<td>
	<input type="text" name="software_name" size="80">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@name"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr><th>Version</th>
	<td>
	<input type="text" name="software_version" size="80">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@version"/>
		</xsl:attribute>
	</input>
	</td>
	<td>
	<input type="hidden" name="catalog" value="software" />
	</td>
	</tr>
	<tr><th>Description</th>
	<td>
	<input type="text" name="software_description" size="80">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@description"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr><th>Contact</th>
	<td>
	<input type="text" name="software_contact" size="80">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@contact"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr><th>WWW</th>
	<td>
	<input type="text" name="software_url" size="80">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@url"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr>
	<td colspan="2">
	<input type="submit" value="Retrieve software number from software catalog" />
	</td>
	</tr>
	</table>
	</form>
</xsl:template>


<xsl:template match="algorithm_ref">
	<form method="POST" name="algorithms_form">
	<table bgcolor="#B0FFB0">
	<tr><th>Definition</th>
	<td>
	<input type="text" size="80" name="alg_definition">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@definition"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr><th>Description</th>
	<td>
	<input type="text" size="80" name="alg_description">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@description"/>
		</xsl:attribute>
	</input>
	</td>
	<td>
	<input type="hidden" name="catalog" value="algorithms" />
	</td>
	</tr>
	<tr>
	<td colspan="2">
	<input type="submit" value="Retrieve algorithm number from algorithms catalog" />
	</td>
	</tr>
	</table>
	</form>
</xsl:template>


<xsl:template match="endpoint_ref">

	<form method="POST" name="endpoints_form">
	<table bgcolor="#FFFFB0">
	<tr><th>Group</th>
	<td>
	<input type="text" size="80" name="endpoint_group">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@group"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr><th>Subgroup</th>
	<td>
	<input type="text" size="80" name="endpoint_subgroup">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@subgroup"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr><th>Name</th>
	<td>
	<input type="text" size="80" name="endpoint_name">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@name"/>
		</xsl:attribute>
	</input>
	</td>
	<td>
	<input type="hidden" name="catalog" value="endpoints" />
	</td>
	</tr>
	<tr>
	<td colspan="2">
	<input type="submit" value="Retrieve endpoint number from endpoints catalog" />
	</td>
	</tr>
	</table>
	</form>

</xsl:template>


<xsl:template match="publication_ref">
</xsl:template>

<xsl:template match="author_ref">
	<form method="POST" name="authors_form">
	<table bgcolor="#B0B0FF">
	<tr><th>Name</th>
	<td>
	<input type="text" size="80" name="author_name">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@name"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr><th>Affiliation</th>
	<td>
	<input type="text" size="80" name="author_affiliation">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@affiliation"/>
		</xsl:attribute>
	</input>
	</td>
	<td>
	<input type="hidden" name="catalog" value="authors" />
	</td>
	</tr>
	<tr><th>Contact</th>
	<td>

	<input type="text" size="80" name="author_address">
		<xsl:attribute name="value">
  			<xsl:value-of select="id(@idref)/@contact"/>
		</xsl:attribute>
	</input>

	</td></tr>
	<tr><th>e-mail</th>
	<td>
	<input type="text" size="80" name="author_email">
		<xsl:attribute name="value">
  			<xsl:value-of select="id(@idref)/@email"/>
		</xsl:attribute>
	</input>
	</td></tr>
	<tr><th>WWW</th>
	<td>
			<input type="text" size="80" name="author_www">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@url"/>
		</xsl:attribute>
	</input>
	</td></tr>
	<tr>
	<td colspan="2">
	<input type="submit" value="Retrieve author number from authors catalog" />
	</td>
	</tr>
	</table>
	</form>
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



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

<xsl:template match="QMRF_chapters/QSAR_identifier/QSAR_title">
	<h3>1.1.QSAR identifier (title)

		<xsl:choose>
		<xsl:when test="text()=''">
			<img src="images/warning.png"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>
		</h3>
		<xsl:value-of disable-output-escaping="yes" select="text()"/>
	</xsl:template>

<xsl:template match="*">

			<xsl:choose>


			<xsl:when test="contains(@chapter,'2.5')">
				<h3>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h3>

			</xsl:when>

			<xsl:when test="contains(@chapter,'3.2')">
				<h3>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h3>

			</xsl:when>



			<xsl:when test="contains(@chapter,'4.6')">
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


<xsl:template match="qmrf_authors">
	<h3>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="author_ref">
			<img src="images/ok.png"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
	<xsl:apply-templates select="*"/>
</xsl:template>



<xsl:template match="model_authors">
	<h3>
	<img src="images/user_suit.png" alt="user icon" title="This field is mandatory!" border="0"/>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="author_ref">
			<img src="images/ok.png" alt="ok" title="Author entry found"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png"  alt="warning" title="No author entries!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
	<xsl:apply-templates select="*"/>
</xsl:template>


<xsl:template match="app_domain_software">
	<h3>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="software_ref">
			<img src="images/ok.png" alt="ok" title="Software entry found"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No software entries!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
	<xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="descriptors_generation_software">
	<h3>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="software_ref">
			<img src="images/ok.png" alt="ok" title="Software entry found"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No software entries!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
	<xsl:apply-templates select="*"/>
</xsl:template>


<xsl:template match="model_endpoint">
	<h3>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="endpoint_ref">
			<img src="images/ok.png" alt="ok" title="Endpoint entry found"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No endpoint entries!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
	<xsl:apply-templates select="*"/>
</xsl:template>


<xsl:template match="algorithm_explicit">
	<h3>
	<img src="images/chart_curve.png" alt="This field is mandatory!" border="0"/>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="algorithm_ref">
			<img src="images/ok.png" alt="ok" title="Algorithm entry found"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No algorithm entries!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
	<xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="QSAR_software">
	<h3>
	<img src="images/application_form.png" alt="This field is mandatory!" border="0"/>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="software_ref">
			<img src="images/ok.png" alt="ok" title="Software entry found"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No software name entries!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
	<xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="training_set_data">
</xsl:template>

<xsl:template match="validation_set_data">
</xsl:template>

<xsl:template match="attachments">
</xsl:template>


<xsl:template match="software_ref">
	<table bgcolor="#FFFFFF">
	<tr bgcolor="#D6DFF7"><th>Name</th>
	<td>
	<input type="text" name="software_name" size="80"  readonly='true' >
		<xsl:attribute name="value">
		<xsl:value-of select="id(@idref)/@name"/>
		</xsl:attribute>
	</input>
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@name=''">
			<img src="images/warning.png" alt="warning" title="Empty software name entry!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png" alt="ok"/>
		</xsl:otherwise>
		</xsl:choose>
	</td>
	</tr>
	</table>
</xsl:template>


<xsl:template match="algorithm_ref">
	<table bgcolor="#FFFFFF">
	<tr bgcolor="#D6DFF7"><th>Definition</th>
	<td>
	<input type="text" size="80" name="alg_definition"  readonly='true' >
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@definition"/>
		</xsl:attribute>
	</input>
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@definition=''">
			<img src="images/warning.png" alt="warning" title="Empty algorithm definition entry!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>
	</td>
	</tr>
	</table>
</xsl:template>



<xsl:template match="endpoint_ref">
	<table bgcolor="#FFFFFF">
	<tr  bgcolor="#D6DFF7"><th>Name</th>
	<td>
	<input type="text" size="80" name="endpoint_name"  readonly='true' >
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@name"/>
		</xsl:attribute>
	</input>
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@name=''">
			<img src="images/warning.png" alt="warning" title="Empty endpoint name entry!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>
	</td>
	</tr>

	</table>

</xsl:template>


<xsl:template match="publication_ref">
</xsl:template>

<xsl:template match="author_ref">
	<table bgcolor="#FFFFFF">
	<tr bgcolor="#D6DFF7">
	<th>Name</th>
	<td>
	<input type="text" size="80" name="author_name"  readonly='true' >
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@name"/>
		</xsl:attribute>
	</input>
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@name=''">
			<img src="images/warning.png" alt="warning" title="Empty author name entry!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>
	</td>
	</tr>
	<tr bgcolor="#D6DFF7">
	<th>e-mail</th>
	<td>
	<input type="text" size="80" name="author_email"  readonly='true' >
		<xsl:attribute name="value">
  			<xsl:value-of select="id(@idref)/@email"/>
		</xsl:attribute>
	</input>
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@email=''">
			<img src="images/warning.png" alt="warning" title="Empty author email entry!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>
	</td></tr>

	</table>
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



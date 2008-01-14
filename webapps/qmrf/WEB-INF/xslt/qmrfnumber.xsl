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
	<h3>1.1.QSAR identifier (title)</h3>
	  		<xsl:value-of disable-output-escaping="yes" select="text()"/>

	</xsl:template>

<xsl:template match="*">

			<xsl:choose>


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
			<img src="images/ok.png" alt="OK" title="QMRF author entry found."/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No QMRF author available!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
	<xsl:apply-templates select="*"/>
</xsl:template>



<xsl:template match="model_authors">

	<h3>
	<xsl:element name="a">
		<xsl:attribute name="name">model_authors</xsl:attribute>
		<img src="images/user_suit.png" alt="user icon" title="This field is mandatory!" border="0"/>
	</xsl:element>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="author_ref">
			<img src="images/ok.png" alt="OK" title="Model author entry found."/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No model author available!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
		<xsl:choose>
		<xsl:when test="author_ref">
		</xsl:when>
		<xsl:otherwise>
			<div class="error">
				No model developer defined!<br/>
				Return to the author for revision!
			</div>
		</xsl:otherwise>
		</xsl:choose>
	<xsl:apply-templates select="*"/>
</xsl:template>


<xsl:template match="app_domain_software">
	<h3>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="software_ref">
			<img src="images/ok.png" alt="OK" title="Applicability domain software entry found."/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No applicability domain software defined!"/>
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
			<img src="images/ok.png" alt="OK" title="Description generation software entry found."/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No descriptor generation software defined!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
	<xsl:apply-templates select="*"/>
</xsl:template>


<xsl:template match="model_endpoint">
	<h3>
	<xsl:element name="a">
		<xsl:attribute name="name">model_endpoint</xsl:attribute>
		<xsl:value-of disable-output-escaping="no" select="@chapter"/>
		<xsl:text>.</xsl:text>
		<xsl:value-of disable-output-escaping="no" select="@name"/>
	</xsl:element>

		<xsl:choose>
		<xsl:when test="endpoint_ref">
			<img src="images/ok.png" alt="OK" title="Endpoint entry found."/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No endpoint defined!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
		<xsl:choose>
		<xsl:when test="endpoint_ref">
		</xsl:when>
		<xsl:otherwise>
			<div class="error">
				No endpoint defined!<br/>
				Return to the author for revision!
			</div>
		</xsl:otherwise>
		</xsl:choose>

	<xsl:apply-templates select="*"/>
</xsl:template>


<xsl:template match="algorithm_explicit">
	<h3>
	<xsl:element name="a">
		<xsl:attribute name="name">algorithm_explicit</xsl:attribute>		
		<img src="images/chart_curve.png" alt="algorithm icon" title="This field is mandatory!" border="0"/>
	</xsl:element>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="algorithm_ref">
			<img src="images/ok.png" alt="OK" title="Algorithm entry found."/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No algorithm defined"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
		<xsl:choose>
		<xsl:when test="algorithm_ref">
		</xsl:when>
		<xsl:otherwise>
			<div class="error">
				No algorithm defined!<br/>
				Return to the author for revision!
			</div>
		</xsl:otherwise>
		</xsl:choose>
	<xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="QSAR_software">
	<h3>
	<img src="images/application_form.png" alt="software icon" title="This field is mandatory!" border="0"/>
	<xsl:value-of disable-output-escaping="no" select="@chapter"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of disable-output-escaping="no" select="@name"/>
		<xsl:choose>
		<xsl:when test="software_ref">
			<img src="images/ok.png" alt="OK" title="QSAR software entry found."/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/warning.png" alt="warning" title="No QSAR software defined!"/>
		</xsl:otherwise>
		</xsl:choose>
	</h3>
		<xsl:choose>
		<xsl:when test="software_ref">
		</xsl:when>
		<xsl:otherwise>
			<div class="error">
				No software coding the model defined!<br/>
				Return to the author for revision!
			</div>
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

	<table bgcolor="#FFFFFF">
	<tr bgcolor="#D6DFF7"><th>Name</th>
	<td>
	<input type="text" name="software_name" size="80" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@name"/>
		</xsl:attribute>
	</input>
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@name=''">
			<img src="images/warning.png" alt="warning" title="Empty name!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>
				<input type="hidden" name="catalog" value="software" />
		
	</td>
	</tr>

	<tr bgcolor="#D6DFF7"><th>Description</th>
	<td>
	<input type="text" name="software_description" size="80" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@description"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr bgcolor="#D6DFF7"><th>Contact</th>
	<td>
	<input type="text" name="software_contact" size="80" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@contact"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr bgcolor="#D6DFF7"><th>WWW</th>
	<td>
	<input type="text" name="software_url" size="80" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@url"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr bgcolor="#FFFFFF">
	<td colspan="2">
	<input type="submit" value="Retrieve software number from software catalog" />
	</td>
	</tr>
	</table>
	</form>
</xsl:template>


<xsl:template match="algorithm_ref">
	<form method="POST" name="algorithms_form">
	<table bgcolor="#FFFFFF">
	<tr bgcolor="#D6DFF7"><th>Definition</th>
	<td>
	<input type="text" size="80" name="alg_definition" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@definition" disable-output-escaping="no"/>
		</xsl:attribute>
	</input>
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@definition=''">
			<img src="images/warning.png" alt="warning" title="Empty definition!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>
	</td>
	</tr>
	<tr bgcolor="#D6DFF7"><th>Description</th>
	<td>
	<xsl:element name="input">
		<xsl:attribute name="name">alg_description</xsl:attribute>	
		<xsl:attribute name="readonly">true</xsl:attribute>	
		<xsl:attribute name="size">80</xsl:attribute>	
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@description" disable-output-escaping="no"/>
  		</xsl:attribute>	
	</xsl:element>
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
	<table bgcolor="#FFFFFF">
	<tr bgcolor="#D6DFF7"><th>Group</th>
	<td>
	<input type="text" size="80" name="endpoint_group" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@group"/>
		</xsl:attribute>
	</input>
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@name=''">
			<img src="images/warning.png" alt="warning" title="Empty endpoint group!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>

	</td>
	</tr>
	<tr bgcolor="#D6DFF7"><th>Subgroup</th>
	<td>
	<input type="text" size="80" name="endpoint_subgroup" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@subgroup"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr bgcolor="#D6DFF7"><th>Name</th>
	<td>
	<input type="text" size="80" name="endpoint_name" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@name"/>
		</xsl:attribute>
	</input>
			<input type="hidden" name="catalog" value="endpoints" />
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@name=''">
			<img src="images/warning.png" alt="warning" title="Empty endpoint name!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>

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
	<table bgcolor="#FFFFFF">
	<tr bgcolor="#D6DFF7"><th>Name</th>
	<td>
	<input type="text" size="80" name="author_name" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@name"/>
		</xsl:attribute>
	</input>
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@name=''">
			<img src="images/warning.png" alt="warning" title="Empty name!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>
	</td>
	</tr>
	<tr  bgcolor="#D6DFF7"><th>Affiliation</th>
	<td>
	<input type="text" size="80" name="author_affiliation" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@affiliation"/>
		</xsl:attribute>
	</input>
	<input type="hidden" name="catalog" value="authors" />
	</td>

	</tr>
	<tr  bgcolor="#D6DFF7"><th>Contact</th>
	<td>

	<input type="text" size="80" name="author_address" readonly="true">
		<xsl:attribute name="value">
  			<xsl:value-of select="id(@idref)/@contact"/>
		</xsl:attribute>
	</input>

	</td></tr>
	<tr  bgcolor="#D6DFF7"><th>e-mail</th>
	<td>
	<input type="text" size="80" name="author_email" readonly="true">
		<xsl:attribute name="value">
  			<xsl:value-of select="id(@idref)/@email"/>
		</xsl:attribute>
	</input>
	</td>
	<td bgcolor="#FFFFFF">
		<xsl:choose>
		<xsl:when test="id(@idref)/@email=''">
			<img src="images/warning.png" alt="warning" title="Empty email!"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="images/ok.png"/>
		</xsl:otherwise>
		</xsl:choose>
	</td>
	</tr>
	<tr  bgcolor="#D6DFF7"><th>WWW</th>
	<td>
			<input type="text" size="80" name="author_www" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="id(@idref)/@url"/>
		</xsl:attribute>
	</input>
	</td></tr>
	<tr  bgcolor="#FFFFFF">
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



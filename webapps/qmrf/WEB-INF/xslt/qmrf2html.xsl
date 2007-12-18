<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html" encoding="UTF-8" indent="yes"/>

<xsl:template match="QMRF">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<link href="styles/nstyle.css" rel="stylesheet" type="text/css"/>
		<meta http-equiv="Content-Type" contentType="text/xml;charset=utf-8"/>
		<title>QMRF</title><body>
       	 <h2>QSAR Model Reporting Format</h2>
       	 <table>
       	 <tr>
       	 <td>Version:</td><td> <xsl:value-of select="@version" /> </td>
         </tr>
         <tr>
       	 <td>Name:</td><td> <xsl:value-of select="@name" /> </td>
         </tr>
         <tr>
         <td>Author:</td><td> <xsl:value-of select="@author" /></td>
         </tr>
         <tr>
		 <td>Date:</td><td><xsl:value-of select="@date" /> </td>
         </tr>
         <tr>
         <td>Contact:</td><td><xsl:value-of select="@contact" /> </td>
         </tr>
         <tr>
		 <td>e-mail:</td><td><xsl:value-of select="@email" /> </td>
         </tr>
         <tr>
         <td>www:</td><td>
       		<xsl:call-template name="print_href"/>
         </td>
         </tr>
         </table>
  			<xsl:apply-templates select="* "/>


	    </body></html>
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
				<xsl:text> </xsl:text>
				<xsl:value-of select="@name"/>
				</h3>
			</xsl:when>
			<xsl:when test="@chapter">
				<h2>
				<xsl:value-of select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of select="@name"/>
				</h2>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
			</xsl:choose>
</xsl:template>

<xsl:template match="*">

			<xsl:choose>

			<xsl:when test="contains(@chapter,'.')">
				<h3>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text> </xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h3>
			</xsl:when>
			<xsl:when test="@chapter">
				<h2>
				<xsl:value-of disable-output-escaping="no" select="@chapter"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of disable-output-escaping="no" select="@name"/>
				</h2>
			</xsl:when>

			<xsl:when test="@idref">
				<xsl:value-of select="id(@idref)/@name"/>

			</xsl:when>



			<xsl:otherwise>
			</xsl:otherwise>
			</xsl:choose>
				<blockquote>
				<xsl:value-of select="@answer"/>


				<xsl:value-of disable-output-escaping="yes" select="text()"/>
				</blockquote>




			<xsl:apply-templates select="*"/>

</xsl:template>


<xsl:template match="training_set_data">
	<xsl:call-template name="print_chapters"/>
	<blockquote>
	<b>Chemname:</b> <xsl:value-of select="@chemname"/><br/>
	<b>SMILES:</b> <xsl:value-of select="@smiles"/><br/>
	<b>CAS RN:</b> <xsl:value-of select="@cas"/><br/>
	<b>InChI:</b> <xsl:value-of select="@inchi"/><br/>
	<b>MOL file:</b> <xsl:value-of select="@mol"/><br/>
	<b>Formula:</b> <xsl:value-of select="@formula"/><br/>
	</blockquote>
</xsl:template>

<xsl:template match="validation_set_data">
	<xsl:call-template name="print_chapters"/>
	<blockquote>

	<b>Chemname:</b> <xsl:value-of select="@chemname"/><br/>
	<b>SMILES:</b> <xsl:value-of select="@smiles"/><br/>
	<b>CAS RN:</b> <xsl:value-of select="@cas"/><br/>
	<b>InChI:</b> <xsl:value-of select="@inchi"/><br/>
	<b>MOL file:</b> <xsl:value-of select="@mol"/><br/>
	<b>Formula:</b> <xsl:value-of select="@formula"/><br/>

	</blockquote>
</xsl:template>

<xsl:template match="attachments">

	<xsl:call-template name="print_chapters"/>
<blockquote>
            <table>
            <tr>
            <td>Training data set</td>
            <td>
 	        <xsl:for-each select="attachment_training_data">
 	      	        <xsl:for-each select="molecules">
 	      	        <blockquote>
						  <xsl:call-template name="print_href_attachment"/>
					</blockquote>
	 		     </xsl:for-each>
 		     </xsl:for-each>
 		    </td>
            </tr><tr>
            <td>Validation data set</td>
            <td>
 	        <xsl:for-each select="attachment_validation_data">
 	      	        <xsl:for-each select="molecules">
 	      	        	  <blockquote>
						  <xsl:call-template name="print_href_attachment"/>
						  </blockquote>
	 		     </xsl:for-each>
 		     </xsl:for-each>
 		     </td>
            </tr><tr>
            <td> Other documents</td>
            <td>
 	        <xsl:for-each select="attachment_documents">
 	      	        <xsl:for-each select="document">
 	      	        <blockquote>
						  <xsl:call-template name="print_href_attachment"/>
					</blockquote>
	 		     </xsl:for-each>
 		     </xsl:for-each>
 		     </td>
 		     </tr>
 		     </table>
</blockquote>
</xsl:template>


<xsl:template match="software_ref">
	<blockquote>
	<b>
	<xsl:value-of disable-output-escaping="yes" select="id(@idref)/@name"/>
	</b>
	<xsl:value-of disable-output-escaping="yes" select="id(@idref)/@version"/>
	<br/>
	<xsl:value-of select="id(@idref)/@description"/>
	<br/>
	<xsl:value-of select="id(@idref)/@contact"/>
	<br/>
	<xsl:value-of select="id(@idref)/@url"/>
	</blockquote>
</xsl:template>


<xsl:template match="algorithm_ref">
	<blockquote>
	<xsl:value-of select="id(@idref)/@definition"/>
	<br/>
	<xsl:value-of select="id(@idref)/@description"/>
	</blockquote>
</xsl:template>

<xsl:template match="endpoint_ref">
	<blockquote>
	<xsl:value-of select="id(@idref)/@group"/>.
	<xsl:value-of select="id(@idref)/@subgroup"/>.
	<xsl:value-of select="id(@idref)/@name"/>
	</blockquote>
</xsl:template>

<xsl:template match="publication_ref">
	<blockquote>
	<xsl:value-of select="id(@idref)/@title"/>
	</blockquote>
</xsl:template>

<xsl:template match="author_ref">
	<blockquote>
	<xsl:value-of select="id(@idref)/@name"/>
	<br/>
	<xsl:value-of select="id(@idref)/@affiliation"/>
	<br/>
	<xsl:value-of select="id(@idref)/@contact"/>
	<br/>
	<xsl:value-of select="id(@idref)/@email"/>
	<br/>
	<xsl:value-of select="id(@idref)/@url"/>
	<br/>
	</blockquote>
</xsl:template>


<xsl:template match="descriptor_ref">
	<blockquote>
	<b>
	<xsl:value-of select="id(@idref)/@name"/>,
	<xsl:value-of select="id(@idref)/@units"/>
	</b>
		<blockquote>
		<xsl:value-of select="id(@idref)/@description"/>
		</blockquote>
	</blockquote>
</xsl:template>

<!--
<xsl:key name="authors" match="author" use="@id" />
<xsl:template match="author_ref">
  <font color="blue">
         <xsl:value-of select="key('author', @idref)"/>
         <xsl:value-of select="."/>
  </font>
</xsl:template>

<xsl:template match="authors_catalog/author">
   <h2 id="{@id}"><xsl:apply-templates select="author/@name" /></h2>
</xsl:template>

-->

 <xsl:template name="print_href">
	 		<a>
			<xsl:attribute name="href">
			<xsl:value-of select="@url" />
			</xsl:attribute>
			<xsl:value-of select="@url"/>
			</a>
 </xsl:template>


 <xsl:template name="print_href_attachment">
	 		<a>
			<xsl:attribute name="href">
			<xsl:value-of select="@url" />
			</xsl:attribute>
			<xsl:value-of select="@description"/>
			</a>
 </xsl:template>


 </xsl:stylesheet>



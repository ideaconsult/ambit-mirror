<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:param name="selected"/>

<xsl:template match="QMRF">
</xsl:template>

<!-- Hide catalogs  -->
<xsl:template match="Catalogs">

<xsl:apply-templates select="* "/>

</xsl:template>

<xsl:template match="endpoints_catalog">
		<SELECT NAME="endpoints_catalog" MAXLENGTH="40">
				<xsl:apply-templates select="* "/>
		</SELECT>

</xsl:template>

<xsl:template match="endpoint">
		<OPTION >
				<xsl:attribute name="value">
						<xsl:value-of select="@id"/>
				</xsl:attribute>

				<xsl:choose>
					<xsl:when test="contains(@id,$selected)">
							<xsl:attribute name="selected">
							<xsl:text>SELECTED</xsl:text>
							</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
					<xsl:value-of select="@group"/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="substring(@subgroup,1,30)"/>
						<xsl:text> </xsl:text>
						<xsl:value-of select= "substring(@name,1,65)"/>
		</OPTION>
</xsl:template>

<xsl:template match="algorithms_catalog">
		<SELECT NAME="algorithms_catalog" >
				<xsl:apply-templates select="* "/>
		</SELECT>
</xsl:template>


<xsl:template match="algorithm">
		<OPTION >
				<xsl:attribute name="value">
						<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="contains(@id,$selected)">
							<xsl:attribute name="selected">
							<xsl:text>SELECTED</xsl:text>
							</xsl:attribute>
					</xsl:when>
										<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>

  			<xsl:value-of select="substring(@description,1,80)"/>

		</OPTION>

</xsl:template>



<xsl:template match="authors_catalog">
		<SELECT NAME="authors_catalog">
				<xsl:apply-templates select="* "/>
		</SELECT>
</xsl:template>



<xsl:template match="author">
		<OPTION >
				<xsl:attribute name="value">
						<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="contains(@id,$selected)">
							<xsl:attribute name="selected">
							<xsl:text>SELECTED</xsl:text>
							</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>

  			<xsl:value-of select="@name"/>
  			<!-- 
  			<xsl:text> (e-mail </xsl:text>
  			<xsl:value-of select="@email"/>
  			<xsl:text> ) </xsl:text>
  			 -->
		</OPTION>

</xsl:template>

<xsl:template match="author1">
		<tr bgcolor="#EEEEEE">
		<td>Author number</td>
		<td>
		<xsl:value-of select="@number"/>
		</td>
		</tr>
		<tr bgcolor="#EEEEEE">
		<td>Name</td>
		<td>
		<xsl:value-of select="@name"/>
		</td>
		</tr>
		<tr bgcolor="#EEEEEE">
		<td>e-mail</td>
		<td>
		<xsl:value-of select="@email"/>
		</td>
		</tr>
		<tr>
		<td>Affiliation</td>
		<td>
		<xsl:value-of select="@affiliation"/>
		</td>
		</tr>
		<tr>
		<td>Contact details</td>
		<td>
		<xsl:value-of select="@contact"/>
		</td>
		</tr>
		<tr>
		<td>WWW</td>
		<td>
		<xsl:call-template name="print_href"/>
		</td>
		</tr>
</xsl:template>


<xsl:template match="software_catalog">
		<SELECT NAME="software_catalog">
				<xsl:apply-templates select="* "/>
		</SELECT>

</xsl:template>

<xsl:template match="software">
		<OPTION >
				<xsl:attribute name="value">
						<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="contains(@id,$selected)">
							<xsl:attribute name="selected">
							<xsl:text>SELECTED</xsl:text>
							</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>

				<xsl:value-of select="@name"/>
				<xsl:text> </xsl:text>
  			<xsl:value-of select="@description"/>
		</OPTION>

</xsl:template>
<xsl:template match="software1">
		<tr bgcolor="#EEEEEE">
		<td>Software number</td>
		<td><b>
		<xsl:value-of select="@number"/>
		</b>
		</td>
		</tr>
		<tr bgcolor="#EEEEEE">
		<td>Name</td>
		<td>
		<xsl:value-of select="@name"/>
		</td>
		</tr>
		<tr bgcolor="#EEEEEE">
		<td>Description</td>
		<td>
		<xsl:value-of select="@description"/>
		</td>
		</tr>
		<tr>
		<td>Contact</td>
		<td>
		<xsl:value-of select="@contact"/>
		</td>
		</tr>
		<tr>
		<td>WWW</td>
		<td>
		<xsl:call-template name="print_href"/>
		</td>
		</tr>
		<tr bgcolor="#FFFFFF"><td colspan="2"> </td></tr>
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



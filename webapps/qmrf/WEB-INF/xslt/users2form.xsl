<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="html"  encoding="UTF-8" indent="yes"/>

<xsl:param name="admin"/>

<xsl:template match="QMRF">
</xsl:template>

<!-- Hide catalogs  -->
<xsl:template match="users">

<xsl:apply-templates select="* "/>

</xsl:template>


<xsl:template match="user">

	<table bgcolor="#FFFFFF">

	<tr bgcolor="#D6DFF7">

	<th>
	<img src="images/user.png" border="0"/>
	User name
	</th>
	<td>
				<a>
							<xsl:attribute name="name">
		  					<xsl:value-of select="@user_name"/>
								</xsl:attribute>
						<xsl:value-of select="@user_name"/>
				</a>

	</td>
	<th width="5%" bgcolor="#FFFFFF" rowspan="14"></th>
	<th bgcolor="#C5CEE6"></th>
	
	</tr>

	<tr bgcolor="#D6DFF7">

	<th>
	email
	</th>
	<td>

  		<xsl:value-of select="@email"/>
		</td>
		<td rowspan="1">
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:text>author.jsp</xsl:text>?name=<xsl:value-of select="@user_name"/>
				</xsl:attribute>
				Verify
			</xsl:element>
	<xsl:text>if a member of QMRF and model authors list.</xsl:text>
	
	<a href="help.jsp?anchor=verify_author" target="help"><img src="images/help.png" alt="help" title="What is QMRF and model authors list?" border="0"/></a>
	</td>
	</tr>
	<tr bgcolor="#D6DFF7">
	<th>

	Registration Date
	</th>
	<td>
	<xsl:value-of select="@registration_date"/>
	</td>
	</tr>
<form method="POST" name="password_form_@user_name" action="mypassword.jsp">
	<tr bgcolor="#C5CEE6">
	<th>
	Password
	</th>
	<td>
		<input type="hidden" name="user_name" readonly="true" >
				<xsl:attribute name="value">
  		<xsl:value-of select="@user_name"/>
		</xsl:attribute>
	</input>
		<input type="submit" name="password" value="Change password"/>

	</td>
	</tr>
</form>

<form method="POST" name="role_form_@user_name" action="myrole.jsp">
<tr bgcolor="#C5CEE6">
	<th>
	Role
	</th>
	<td>
		<xsl:element name="input">
			<xsl:attribute name="name">
				<xsl:text>user_name</xsl:text>
			</xsl:attribute>		
			<xsl:attribute name="value">
				<xsl:value-of select="@user_name"/>
			</xsl:attribute>
			<xsl:attribute name="type">
				<xsl:text>hidden</xsl:text>
			</xsl:attribute>			
		</xsl:element>	
	<xsl:for-each select="role">
		<xsl:choose>
		<xsl:when test="$admin = 'true'">
			<xsl:element name="input">
				<xsl:attribute name="type">CHECKBOX</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="@name"/>
				</xsl:attribute>
				<xsl:if test="@selected = 'true'">
					<xsl:attribute name="checked">
					</xsl:attribute>
				</xsl:if>			
				<xsl:choose>
					<xsl:when test="@name = 'qmrf_admin'">
						<xsl:text>Reviewer </xsl:text>
					</xsl:when>
					<xsl:when test="@name = 'qmrf_editor'">
						<xsl:text>Editor </xsl:text>
					</xsl:when>					
					<xsl:when test="@name = 'qmrf_user'">
						<xsl:text>Author </xsl:text>
					</xsl:when>				
					<xsl:when test="@name = 'qmrf_manager'">
						<xsl:text>Administrator </xsl:text>
					</xsl:when>									
					<xsl:otherwise>
						<xsl:text>Unknown </xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>	

			<xsl:element name="input">
				<xsl:attribute name="type">hidden</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:text>old_</xsl:text>
					<xsl:value-of select="@name"/>
				</xsl:attribute>

				<xsl:attribute name="value">
					<xsl:value-of select="@selected"/>
				</xsl:attribute>

			</xsl:element>	
		</xsl:when>
		<xsl:otherwise>
			<xsl:if test="@selected = 'true'">
				<xsl:choose>
					<xsl:when test="@name = 'qmrf_admin'">
						<xsl:text>Reviewer </xsl:text>
					</xsl:when>
					<xsl:when test="@name = 'qmrf_user'">
						<xsl:text>Author </xsl:text>
					</xsl:when>		
					<xsl:when test="@name = 'qmrf_editor'">
						<xsl:text>Editor </xsl:text>
					</xsl:when>							
					<xsl:when test="@name = 'qmrf_manager'">
						<xsl:text>Administrator </xsl:text>
					</xsl:when>									
					<xsl:otherwise>
						<xsl:text>Unknown </xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each> 
	
	<xsl:if test="$admin = 'true'">
		<input type="submit" name="role" value="Update roles"/>	
	</xsl:if>
	<a href="help.jsp?anchor=roles" target="help"><img src="images/help.png" alt="help" title="How to add/change roles?" border="0"/></a>
	</td>
</tr>
</form>

<tr height="5"></tr>
<form method="POST" name="profile_form_@user_name" >
	<tr bgcolor="#D6DFF7">
	<th>
	Title
	</th>
	<td>
		<input type="hidden" name="user_name" readonly="true">
		<xsl:attribute name="value">
  		<xsl:value-of select="@user_name"/>
		</xsl:attribute>
	</input>

	<input type="text" name="title" size="10">
		<xsl:attribute name="value">
  		<xsl:value-of select="@title"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr bgcolor="#D6DFF7">
	<th>
	First name
	</th>
	<td>

	<input type="text" name="firstname" size="40">
		<xsl:attribute name="value">
  		<xsl:value-of select="@firstname"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr bgcolor="#D6DFF7">
	<th>
	Last name
	</th>
	<td>

	<input type="text" name="lastname" size="40">
		<xsl:attribute name="value">
  		<xsl:value-of select="@lastname"/>
		</xsl:attribute>
	</input>
	</td>
	</tr>
	<tr bgcolor="#D6DFF7">
	<th>
	Affiliation
	</th>
	<td>
	<input type="text" name="affiliation" size="40">
		<xsl:attribute name="value">
  		<xsl:value-of select="@affiliation"/>
		</xsl:attribute>
	</input>

	</td>
	</tr>
	<tr bgcolor="#D6DFF7">
	<th>
	Address
	</th>
	<td>
		<input type="text" name="address" size="40">
		<xsl:attribute name="value">
  		<xsl:value-of select="@address"/>
		</xsl:attribute>
	</input>

	</td>
	</tr>
	<tr bgcolor="#D6DFF7">
	<th>
	Country
	</th>
	<td>
	<input type="text" name="country" size="40">
		<xsl:attribute name="value">
  		<xsl:value-of select="@country"/>
		</xsl:attribute>
	</input>

	</td>
	</tr>
	<tr bgcolor="#D6DFF7">
	<th>
	WWW
	</th>
	<td>

	<input type="text" name="webpage" size="40">
		<xsl:attribute name="value">
  		<xsl:value-of select="@webpage"/>
		</xsl:attribute>
	</input>

	</td>
	</tr>





	<tr bgcolor="#C5CEE6">
		<th>

	</th>
	<td>

	<input type="submit" value="Update"/>

	</td>
	</tr>
	<tr height="20"></tr>
</form>





</table>


</xsl:template>


 </xsl:stylesheet>



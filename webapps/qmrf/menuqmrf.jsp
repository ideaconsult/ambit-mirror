<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<c:set var="xsl">
	<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="">
	<xsl:output method="html"  encoding="utf-8" indent="no"/>
		<xsl:param name="highlighted"/>

		<xsl:template match="@*|node()">
			<xsl:apply-templates select="*"/>
		</xsl:template>

		<xsl:template match="tab">
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="@ref" />
				</xsl:attribute>
				<xsl:element name="IMG">
					<xsl:attribute name="src">
						<xsl:choose>
							<xsl:when test="$highlighted = @name">
								<xsl:text>images/</xsl:text><xsl:value-of select="@image"/><xsl:text>_over.png</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>images/</xsl:text><xsl:value-of select="@image"/><xsl:text>.png</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:attribute name="border">
						<xsl:text>0</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="alt">
						<xsl:value-of select="@name"/>
					</xsl:attribute>
					<xsl:attribute name="title">
						<xsl:value-of select="@hint"/>
					</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:text> </xsl:text>

		</xsl:template>
	</xsl:stylesheet>
</c:set>

<c:set var="menu">
	<?xml version="1.0" encoding="UTF-8"?>
	<menu>
	<tab name="welcome" image="home" ref="index.jsp" hint="QMRF Database home page"/>
	<tab name="search" image="search_documents" ref="search_catalogs.jsp" hint="Search documents by QMRF No., free text, endpoints, authors, algorithms, sofware"/>
	<tab name="structures" image="search_structures" ref="search_substances.jsp" hint="Search chemical substances by CAS No, chemical name, similarity"/>
	</menu>
</c:set>

<div id="bartext">

	<x:transform xml="${menu}" xslt="${xsl}">
		<x:param name="highlighted" value="${param.highlighted}"/>
	</x:transform>

	<c:if test="${param.highlighted eq 'login'}">
		<c:set var="menulogin">
		<?xml version="1.0" encoding="UTF-8"?>
		<menu>
			<tab name="login" image="log_in" ref="protected.jsp" hint="QMRF Database log in page"/>
		</menu>
		</c:set>
		<x:transform xml="${menulogin}" xslt="${xsl}">
			<x:param name="highlighted" value="${param.highlighted}"/>
		</x:transform>
	</c:if>

	<c:if test="${param.highlighted eq 'register'}">
		<c:set var="menulogin">
		<?xml version="1.0" encoding="UTF-8"?>
		<menu>
			<tab name="register" image="register" ref="register.jsp" hint="QMRF Database registration in page"/>
		</menu>
		</c:set>
		<x:transform xml="${menulogin}" xslt="${xsl}">
			<x:param name="highlighted" value="${param.highlighted}"/>
		</x:transform>
	</c:if>

	<c:if test="${!empty pageContext.request.userPrincipal.name}">

		<c:forEach var="a" items="${pageContext.request.userPrincipal.roles}">
			<c:if test="${sessionScope.viewmode eq a}">
				<c:choose>
				<c:when test="${a eq 'qmrf_user'}">
					<c:set var="menu_user">
						<?xml version="1.0" encoding="UTF-8"?>
						<menu>
						<tab name="user" image="my_documents" ref="user.jsp" hint="My QMRF documents"/>
						<tab name="create" image="new_document" ref="create.jsp" hint="Create new QMRF document"/>
						<tab name="profile" image="my_profile" ref="myprofile.jsp" hint="Edit user details, change password"/>
						</menu>
					</c:set>
					<x:transform xml="${menu_user}" xslt="${xsl}">
						<x:param name="highlighted"value="${param.highlighted}"/>
					</x:transform>
				</c:when>
				<c:when test="${a eq 'qmrf_admin'}">
					<c:set var="menu_admin">
						<?xml version="1.0" encoding="UTF-8"?>
						<menu>
						<tab name="admin" image="pending_qmrfs" ref="admin.jsp" hint="QMRF documents to be reviewed"/>
						<tab name="review" image="review_qmrf" ref="admin_edit.jsp" hint="Review selected QMRF document"/>
						<tab name="import" image="import_structures" ref="import.jsp" hint="Import chemical structures from uploaded attachments into structure searchable database"/>
						<tab name="profile" image="my_profile" ref="myprofile.jsp" hint="Edit user details, change password"/>
						</menu>
					</c:set>
					<x:transform xml="${menu_admin}" xslt="${xsl}">
						<x:param name="highlighted"value="${param.highlighted}"/>
					</x:transform>
				</c:when>
				<c:when test="${a eq 'qmrf_manager'}">
					<c:set var="menu_manager">
						<?xml version="1.0" encoding="UTF-8"?>
						<menu>
						<tab name="catalog" image="statistics" ref="admin_status.jsp" hint="Reports and various system statistics"/>
						<tab name="profile" image="user_profiles" ref="myprofile.jsp" hint="Edit user details, change password"/>
						</menu>
					</c:set>
					<x:transform xml="${menu_manager}" xslt="${xsl}">
						<x:param name="highlighted"value="${param.highlighted}"/>
					</x:transform>
				</c:when>
				<c:when test="${a eq 'qmrf_editor'}">
					<c:set var="menu_editor">
						<?xml version="1.0" encoding="UTF-8"?>
						<menu>
						<tab name="editor" image="pending_qmrfs" ref="editor.jsp" hint="Assign reviewers"/>
						</menu>
					</c:set>
					<x:transform xml="${menu_editor}" xslt="${xsl}">
						<x:param name="highlighted"value="${param.highlighted}"/>
					</x:transform>
				</c:when>
				<c:otherwise>
					${a}
				</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
	</c:if>

</div>

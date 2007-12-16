<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
<title>QMRF Registration Page</title>
<body bgcolor="white">

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="register"/>
</jsp:include>


<jsp:include page="menuall.jsp" flush="true">
		    <jsp:param name="highlighted" value="register" />
</jsp:include>

<c:if test="${!empty param.registerstatus}">
		<div style="color:#FF0000;text-align:center">
				${param.registerstatus}
		</div>
</c:if>

<form method="POST" action='<%= response.encodeURL("register_action.jsp") %>' >
  <table border="0" cellspacing="5">
    <tr>
      <th align="right">Username:</th>
      <td align="left"><input type="text" name="username" value="${param.username}">
      	<font color="#FF0000">*</font>
      </td>
    </tr>
    <tr>
      <th align="right">E-mail:</th>
      <td align="left"><input type="text" name="email" value="${param.email}">
      	<font color="#FF0000">*</font>
      	</td>
    </tr>

    <tr>
      <th align="right">Title:</th>
      <td align="left"><input type="text" name="title"></td>
    </tr>

    <tr>
      <th align="right">First name:</th>
      <td align="left"><input type="text" name="firstname">
      	<font color="#FF0000">*</font></td>
    </tr>

    <tr>
      <th align="right">Last name:</th>
      <td align="left"><input type="text" name="lastname">
      	<font color="#FF0000">*</font></td>
    </tr>

    <tr>
      <th align="right">Affiliation:</th>
      <td align="left"><input type="text" name="affiliation"></td>
    </tr>

    <tr>
      <th align="right">Contact details:</th>
      <td align="left"><input type="text" name="address"></td>
    </tr>

    <tr>
      <th align="right">Country:</th>
      <td align="left"><input type="text" name="country"></td>
    </tr>

    <tr>
      <th align="right">Password:</th>
      <td align="left"><input type="password" name="password1">
      	<font color="#FF0000">*</font>
      	</td>
    </tr>
    <tr>
      <th align="right">Password (confirm again):</th>
      <td align="left"><input type="password" name="password2">
      	<font color="#FF0000">*</font>
      	</td>
    </tr>
    <tr>
      <td align="right"><input type="submit" value="Register"></td>
      <td align="left"><input type="reset"></td>
    </tr>
        <tr>
      <td align="left" colspan="2"><font color="#FF0000">*</font> mandatory fields.</td>

    </tr>
  </table>

</form>

<hr>
<h6>

Please note that log in is required only for submitting new (Q)MRF documents.
</h6>

</body>
</html>

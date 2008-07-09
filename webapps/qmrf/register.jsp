<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:requestEncoding value="UTF-8"/>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database Registration page"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="register"/>
	<jsp:param name="viewmode" value="${param.viewmode}"/>    
</jsp:include>

<c:if test="${!empty param.registerstatus}">
		<div class="error">
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
      <td> 
      <c:if test="${!empty(param.status_username)}">
      <div class="error">${param.status_username}</div>
      </c:if>
      </td>
    </tr>
    <tr>
      <th align="right">E-mail:
      <a href="help.jsp?anchor=email" target="help">
	  <img src="images/help.png" alt="help" title="Please provide a valid e-mail" border="0" /></a>      
      </th>
      <td align="left"><input type="text" name="email" value="${param.email}" >
      	<font color="#FF0000">*</font>
      	</td>
      <td> 
      <c:if test="${!empty(param.status_email)}">
      <div class="error">${param.status_email}</div>
      </c:if>
      </td>
    </tr>

    <tr>
      <th align="right">Title:</th>
      <td align="left"><input type="text" name="title" value="${param.title}"></td>
    </tr>

    <tr>
      <th align="right">First name:</th>
      <td align="left"><input type="text" name="firstname" value="${param.firstname}">
      	<font color="#FF0000">*</font></td>
      <td> 
      <c:if test="${!empty(param.status_firstname)}">
      <div class="error">${param.status_firstname}</div>
      </c:if>
      </td>      	
    </tr>

    <tr>
      <th align="right">Last name:</th>
      <td align="left"><input type="text" name="lastname" value="${param.lastname}">
      	<font color="#FF0000">*</font></td>
      <td> 
      <c:if test="${!empty(param.status_lastname)}">
      <div class="error">${param.status_lastname}</div>
      </c:if>
      </td>      	
    </tr>

    <tr>
      <th align="right">Affiliation:</th>
      <td align="left"><input type="text" name="affiliation" value="${param.affiliation}" size="60"></td>
    </tr>

    <tr>
      <th align="right">Contact details:</th>
      <td align="left"><input type="text" name="address" value="${param.address}" size="60"></td>
    </tr>

    <tr>
      <th align="right">Country:</th>
      <td align="left"><input type="text" name="country" value="${param.country}" size="60"></td>
    </tr>
    <tr>
      <th align="right">WWW page:</th>
      <td align="left"><input type="text" name="webpage" value="${param.webpage}" size="60"></td>
    </tr>    
    <tr>
      <th align="right">Reviewer:
      <a href="help.jsp?anchor=roles" target="help">
	  <img src="images/help.png" alt="help" title="Check this box if you would like to become a reviewer of QMRF documents." border="0" /></a>
      </th>
      <td align="left">
      <input type="checkbox" name="reviewer" value="checked" ${param.reviewer}></td>
    </tr>      
    <tr>
      <th align="right">Keywords:
      <a href="help.jsp?anchor=user_keywords" target="help">
	  <img src="images/help.png" alt="help" title="If you would like to become a reviewer of QMRF documents,please enter keywords of the scientific field you would like to review" border="0" /></a>
      </th>
      <td align="left"><input type="text" name="keywords" value="${param.keywords}" size="60"></td>
    </tr>    
    <tr>
      <th align="right">Password:</th>
      <td align="left"><input type="password" name="password1">
      	<font color="#FF0000">*</font>
      	</td>
      <td> 
      <c:if test="${!empty(param.status_password)}">
      <div class="error">${param.status_password}</div>
      </c:if>
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

<div id="hits">
<p>
<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
</jsp:include>
</div>
</body>
</html>

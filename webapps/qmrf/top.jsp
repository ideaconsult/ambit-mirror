<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html> 
<head>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta name="description" content="(Q)MRF database">
	<meta name="keywords" content="ambit,qsar,qmrf,structure search">
	<meta name="robots" content="index,follow">
	<META NAME="GOOGLEBOT" CONTENT="index,FOLLOW">
	<meta name="copyright" content="Copyright 2007. Ideaconsult Ltd. nina@acad.bg">
	<meta name="author" content="Nina Jeliazkova">
	<meta name="language" content="English">
	<meta name="revisit-after" content="7">
	<link rel="SHORTCUT ICON" href="favicon.ico">
	<title>
	<c:choose>
	<c:when test="${!empty param.title}">
		${param.title}
	</c:when>
	<c:otherwise>
	(Q)SAR Model Reporting Format (QMRF) Inventory
	</c:otherwise>
	</c:choose>
	</title>
<SCRIPT TYPE="text/javascript">
<!--
function popup(mylink, windowname)
{
if (! window.focus)return true;
var href;
if (typeof(mylink) == 'string')
   href=mylink;
else
   href=mylink.href;
window.open(href, windowname, 'width=600,height=200,scrollbars=yes');
return false;
}
//-->
</SCRIPT>	
</head>

<body bgcolor="#ffffff">
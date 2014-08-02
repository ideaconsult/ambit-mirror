	<!-- Basic Page Needs
  ================================================== -->
	<title>AMBIT</title>
	<meta name="robots" content="index,follow"><META NAME="GOOGLEBOT" CONTENT="index,FOLLOW">
	<meta charset="utf-8">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="description" content="AMBIT">
	<meta name="author" content="${creator}">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" >

	<!-- Mobile Specific Metas
  ================================================== -->
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

	<!-- CSS
  ================================================== -->
	<link rel="stylesheet" href="${ambit_root}/style/skeleton/base.css" type="text/css">
	<link rel="stylesheet" href="${ambit_root}/style/skeleton/skeleton-fluid.css" type="text/css">
	<link rel="stylesheet" href="${ambit_root}/style/skeleton/layout.css" type="text/css">
	<link rel="stylesheet" href="${ambit_root}/style/skeleton/toplinks.css" type="text/css">

	<!--[if lt IE 9]>
		<script src="${ambit_root}/jquery/html5.js"></script>
	<![endif]-->
	
	<!-- JQuery
	================================================== -->
	<script src="${ambit_root}/jquery/jquery-1.10.2.js"></script>
	<script src="${ambit_root}/jquery/jquery-ui-1.10.4.custom.min.js"></script>
	
	<script type="text/javascript" src="${ambit_root}/jquery/jquery.cookie.js"></script>
	<script type='text/javascript' src='${ambit_root}/jquery/jquery.validate.min.js'></script>
	<script type='text/javascript' charset='utf8' src='${ambit_root}/jquery/jquery.dataTables-1.9.4.min.js'></script>
	<script type='text/javascript' charset='utf8' src='${ambit_root}/jquery/jquery.FastEllipsis.js'></script>

	<link rel="stylesheet" href="${ambit_root}/style/jquery-ui-1.10.4.custom.min.css" type="text/css">
	<link rel="stylesheet" href="${ambit_root}/style/jquery.ui.theme.css" type="text/css">
	
	<link rel="stylesheet" href="${ambit_root}/style/jquery.dataTables.css" type="text/css">
		
	<!-- Favicons
	================================================== -->
	<link rel="shortcut icon" href="${ambit_root}/images/favicon.ico">
	<link rel="apple-touch-icon" href="${ambit_root}/style/images/apple-touch-icon.png">
	<link rel="apple-touch-icon" sizes="72x72" href="${ambit_root}/style/images/apple-touch-icon-72x72.png">
	<link rel="apple-touch-icon" sizes="114x114" href="${ambit_root}/style/images/apple-touch-icon-114x114.png">


	
	<!-- AMBIT JS
	================================================== -->
	<script type='text/javascript' src='${ambit_root}/scripts/jambit.js'></script>
	<script type='text/javascript' src='${ambit_root}/scripts/jlayout.js'></script>
	<script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>
	<script type='text/javascript' src='${ambit_root}/scripts/oecdcategories.js'></script>
	<script type='text/javascript' src='${ambit_root}/jquery/jquery.base64.min.js'></script>

	<script type='text/javascript' src='${ambit_root}/scripts/help.js'></script>
	<script type='text/javascript' src='${ambit_root}/scripts/footer.js'></script>
	<!--
	<script type='text/javascript' src='${ambit_root}/jmol/Jmol.js'></script>
	-->			

	<!-- jqBreadCrumbs
	================================================== -->
	<link rel="stylesheet" href="${ambit_root}/style/jqBreadCrumbBase.css" type="text/css">
    <link rel="stylesheet" href="${ambit_root}/style/jqBreadCrumb.css" type="text/css">
    <script src="${ambit_root}/jquery/jquery.jBreadCrumb.1.1.js" type="text/javascript" language="JavaScript"></script>
    
    <!-- smartmenus
	================================================== -->
    <script src="${ambit_root}/jquery/jquery.smartmenus.min.js" type="text/javascript" language="JavaScript"></script>
    <link rel="stylesheet" href="${ambit_root}/style/sm-core-css.css" type="text/css">
    <link rel="stylesheet" href="${ambit_root}/style/sm-mint.css" type="text/css">
     
<!-- Uncomment to enable the confirmation message when clicking external links	
<script type='text/javascript'>
	$(document).ready(function() {
		$(".qxternal").click(function(event) {
		   var confirmation = confirm("You are leaving the AMBIT2 website and entering an external link.");
		   if (!confirmation) {
		     event.preventDefault();
		   }
		 });
	});
 </script>
 -->
 <#include "/ga.ftl" >
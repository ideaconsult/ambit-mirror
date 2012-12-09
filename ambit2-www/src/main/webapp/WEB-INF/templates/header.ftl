	<!-- Basic Page Needs
  ================================================== -->
	<title>AMBIT</title>
	<meta name="robots" content="index,follow"><META NAME="GOOGLEBOT" CONTENT="index,FOLLOW">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="description" content="AMBIT">
	<meta name="author" content="${creator}">

	<!-- Mobile Specific Metas
  ================================================== -->
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

	<!-- CSS
  ================================================== -->
	<link rel="stylesheet" href="${ambit_root}/style/skeleton/base.css" type="text/css">
	<link rel="stylesheet" href="${ambit_root}/style/skeleton/skeleton-fluid.css" type="text/css">
	<link rel="stylesheet" href="${ambit_root}/style/skeleton/layout.css" type="text/css">

	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	
	<link rel="stylesheet" href="${ambit_root}/style/jquery-ui-1.8.18.custom.css" type="text/css">
	<link rel="stylesheet" href="${ambit_root}/style/jquery.dataTables.css" type="text/css">
	<link rel="stylesheet" href="${ambit_root}/style/skeleton/ambit2.css" type="text/css">
		
	<!-- Favicons
	================================================== -->
	<link rel="shortcut icon" href="${ambit_root}/images/favicon.ico">
	<link rel="apple-touch-icon" href="${ambit_root}/style/images/apple-touch-icon.png">
	<link rel="apple-touch-icon" sizes="72x72" href="${ambit_root}/style/images/apple-touch-icon-72x72.png">
	<link rel="apple-touch-icon" sizes="114x114" href="${ambit_root}/style/images/apple-touch-icon-114x114.png">


		<!-- JQuery
	================================================== -->
	<script type='text/javascript' src='${ambit_root}/jquery/jquery-1.7.1.min.js'></script>
	<script type='text/javascript' src='${ambit_root}/jquery/jquery-ui-1.8.18.custom.min.js'></script>
	<script type='text/javascript' src='${ambit_root}/jquery/jquery.validate.min.js'></script>
	<script type='text/javascript' charset='utf8' src='${ambit_root}/jquery/jquery.dataTables-1.9.0.min.js'></script>
	<script type="text/javascript" src="${ambit_root}/jquery/jquery.cookies.2.2.0.min.js"></script>
	<script type='text/javascript' src='${ambit_root}/jquery/jquery.base64.min.js'></script>
	<script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>
		
	<!-- AMBIT JS
	================================================== -->
	<script type='text/javascript' src='${ambit_root}/scripts/oecdcategories.js'></script>
	<script type='text/javascript' src='${ambit_root}/scripts/jlayout.js'></script>
	
	<!-- Footer
	================================================== -->
<script type="text/javascript">
<!-- show the footer when the mouse is near -->
	$(document).ready( function () {
    	$('div#footer-in').mouseenter( function () {
    		$('div#footer').stop().animate({bottom: '15px'}, 'fast');
    	});
    	$('div#footer-out').mouseleave( function () {
    		$('div#footer').stop().animate({bottom: '-17px'}, 'slow');
    	});
    });
</script>


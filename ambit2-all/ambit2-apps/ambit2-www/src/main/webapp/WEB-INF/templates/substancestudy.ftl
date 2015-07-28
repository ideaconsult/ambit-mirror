<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
<script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/config/ce.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/npo.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/bao.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/toxcast.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config-study.js'></script>

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

	<script type='text/javascript'>
	
	$(document).ready(function() {
	  	loadHelp("${ambit_root}","substance");
	  	$( "#selectable" ).selectable( "option", "distance", 18);
	  	downloadForm("${ambit_request}");
	  	var purl = $.url();
		$('#search').attr('value',purl.param('search')===undefined?'':purl.param('search'));
		var typeToSelect = purl.param('type')===undefined?'':purl.param('type');
        $("#selecttype option").each(function (a, b) {
	            if ($(this).val() == typeToSelect ) $(this).attr("selected", "selected");
	    });		
	    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substance study: phys-chem, eco tox, environmental fate and toxicity data">Substance</a></li>');
	    <#if substanceUUID??>
	  		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance/${substanceUUID}" title="Substance">${substanceUUID}</a></li>');
	  		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance/${substanceUUID}/study" title="Substances">Study</a></li>');
	  	</#if>
	  	jQuery("#breadCrumb").jBreadCrumb();
	});
	</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	<#include "/searchmenu/menu_substance.ftl">

<!-- help-->		
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
	<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
	</div>
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
	</div>		
</div>

<div class="fourteen columns remove-bottom" style="padding:0;" >


		<!-- Page Content
		================================================== -->
<div class="jtox-toolkit remove-bottom" 
	data-kit="study" 
	data-tab="P-CHEM"
	data-substance-uri="${ambit_root}/substance/${substanceUUID}"
	data-cross-domain="false"
	data-configuration="config_study"	
	data-on-error="errorHandler" 
	data-jsonp="false"></div>

<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

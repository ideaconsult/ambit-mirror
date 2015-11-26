<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
<script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/config/i5.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/ce.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/npo.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/bao.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/toxcast.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config-study.js'></script>

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

	<script type='text/javascript'>
	
	$(document).ready(function() {
		<#if menu_profile?? && menu_profile=='enanomapper'>		        
	     	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Nanomaterials">Search nanomaterials by identifiers</a></li>');
	     	loadHelp("${ambit_root}","nanomaterial");
	 		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/substance'><input type='radio' checked name='type' id='type_name' value='name' title='Name (starting with a string)'>Name <input type='radio' name='type' id='type_' value=''  title='Experiment reference, e.g. DOI'>External identifier <input type='radio' name='type' id='type_citation' checked value='citation'>Experiment reference <input name='search' class='search' value='' id='search'> <input type='submit' value='Search'></form>");
	 		$("#header_substance").text("Nanomaterials");
	 	<#else>
    		<#if menu_profile?? && menu_profile=='lri'>
	    		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substances: Mono-constituent, multiconstituent, additives, impurities.">Search substances by identifiers</a></li>');
	    		loadHelp("${ambit_root}","substance");
	    		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/substance'><input type='radio' checked name='type' id='type_name' value='name' title='Name (starting with a string)'>Name <input type='radio' name='type' id='type_uuid' value='uuid'>UUID <input type='radio' name='type' id='type_regexp'  value='regexp'>Name (regexp) <input type='radio' name='type' id='type_' value=''>External identifier <input name='search' class='search' value='' id='search'> <input type='submit' value='Search'></form>");
    		<#else>
        		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substances: Mono-constituent, multiconstituent, additives, impurities.">Search substances by identifiers</a></li>');
        		loadHelp("${ambit_root}","substance");
        		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/substance'><input type='radio' checked name='type' id='type_name' value='name' title='Name (starting with a string)'>Name <input type='radio' name='type' id='type_like' value='like'>Name (pattern matching) <input type='radio' name='type' id='type_regexp'  value='regexp'>Name (regexp) <input name='search' class='search' value='' id='search'> <input type='submit' value='Search'></form>");
    		</#if>
        </#if>
     
	  	$( "#selectable" ).selectable( "option", "distance", 18);
	  	downloadForm("${ambit_request}");
	  	var purl = $.url();
		$('.search').attr('value',purl.param('search')===undefined?'':purl.param('search'));
		var typeToSelect = purl.param('type')===undefined?'':purl.param('type');
        $("#selecttype option").each(function (a, b) {
	            if ($(this).val() == typeToSelect ) $(this).attr("selected", "selected");
	    });
        $("#type_"+typeToSelect).prop("checked", true);
        

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

</div>

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<!-- help-->		
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
</div>
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
</div>

<#include "/searchmenu/menu_substance.ftl">

</div>


<div class='row add-bottom' style="height:140px;">&nbsp;</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

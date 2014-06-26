<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>
$(document).ready(function() {
	defineStudySearchFacets("${ambit_root}",
				"${ambit_root}/admin/stats/protocol_applications?topcategory=P-CHEM&media=application/json",
				"#facet_pchem");
	defineStudySearchFacets("${ambit_root}",
			"${ambit_root}/admin/stats/protocol_applications?topcategory=TOX&media=application/json",
			"#facet_tox");
	defineStudySearchFacets("${ambit_root}",
			"${ambit_root}/admin/stats/protocol_applications?topcategory=ECOTOX&media=application/json",
			"#facet_ecotox");
	defineStudySearchFacets("${ambit_root}",
			"${ambit_root}/admin/stats/protocol_applications?topcategory=ENV%20FATE&media=application/json",
			"#facet_envfate");	

	$( "#accordion" ).accordion( {
		heightStyle: "content"
	});	
	downloadForm("${ambit_request}");
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="Admin">Admin</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin/stats" title="Statistics">Statistics</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="${facet_tooltip}">${ambit_request}</a></li>');
});
</script>
<style>
	#accordion .ui-accordion-content { padding: 2px; }
</style>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="five columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >

<form action='${ambit_root}/substance' method='GET'>
	<input class='half-bottom' type='submit' value='Search'>
	<div id="accordion" style="padding:0;margin:0">
		<h3>P-Chem</h3>
		  <table id='facet_pchem' class='facet .jtox-toolkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead><tr><th ></th><th ></th></tr></thead>
			<tbody></tbody>
		   </table>
		<h3>Env Fate</h3>
		  <table id='facet_envfate' class='facet .jtox-toolkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead><tr><th ></th><th ></th></tr></thead>
			<tbody></tbody>
		   </table>
		<h3>Eco Tox</h3>
		  <table id='facet_ecotox' class='facet .jtox-toolkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead><tr><th ></th><th ></th></tr></thead>
			<tbody></tbody>
		   </table>
		<h3>Tox</h3>
		  <table id='facet_tox' class='facet .jtox-toolkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead><tr><th ></th><th ></th></tr></thead>
			<tbody></tbody>
		   </table>
		</div>
		
		
</form>			
			
</div>
 		
		<div class="eleven columns remove-bottom" style="padding:0;" >

		Show here the substances
		</div> 
		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
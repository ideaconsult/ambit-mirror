<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<script type='text/javascript' src='${ambit_root}/scripts/ont.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>

<script type='text/javascript'>
$(document).ready(function() {
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="">Study results</a></li>');
	jQuery("#breadCrumb").jBreadCrumb();
	downloadForm("${ambit_request}");
	//loadHelp("${ambit_root}","feature");
	var oTable = defineInvestigationTable("${ambit_root}","${ambit_request_json}","#investigation",false,"<Fif>rtp");
	
	$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/investigation'><select id='type' name='type'><option value='byinvestigation'>Investigation</option><option value='bysubstance'>Substance UUID</option><option value='byprovider'>Data provider</option><option value='bycitation'>Reference</option><option value='bystudytype'>Study type</option><option value='bystructure_inchikey'>Study type and InChI key</option><option value='bystructure_smiles'>Study type and SMILES</option> <option value='bystructure_name'>Study type and name</option><option value='bysubstance_name'>Study type and substance name</option> <option value='bysubstance_type'>Study type and substance type</option></select><input name='search' value='' id='search'><input name='id' value='' id='id'><input type='submit' value='Search'></form>");
	
	var purl = $.url();
		$('.search').attr('value',purl.param('search')===undefined?'':purl.param('search'));
		
		var typeToSelect = purl.param('type')===undefined?'':purl.param('type');
	
        $("#selecttype option").each(function (a, b) {
	          if ($(this).val() == typeToSelect ) $(this).attr("selected", "selected");
	    });
        $("#"+typeToSelect).prop("selected", true);

});
</script>
</head>
<body>

<div class="container columns" style="margin:20;padding:10;">
<!-- banner -->
<#include "/banner_crumbs.ftl">

	<div class="row" style="margin:20;padding:10;" >
			<table id='investigation'  class='fourteen columns ' cellpadding='0' border='0' width='90%' cellspacing='0' style="margin:20;padding:10;"  ></table>
	</div>	
			

		
	<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
	<a href='#' id='csv' target=_blank><img src='${ambit_root}/images/csv64.png' alt='csv' title='Download as CSV'></a>
<!--	
	<a href='#' id='xlsx' target=_blank><img src='${ambit_root}/images/xlsx.png' alt='xlsx' title='Download as XLSX'></a>
	-->
	</div>

</div>

		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
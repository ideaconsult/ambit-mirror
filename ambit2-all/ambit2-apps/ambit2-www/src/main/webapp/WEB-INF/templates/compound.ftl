<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<#assign w=250/>
<#assign h=250/>
<script type='text/javascript' src='${ambit_root}/scripts/jcompound.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/dataset/ui-query.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/dataset/config-dataset.js'></script>

<#if cmpid??>
<script type='text/javascript'>
	
function atomNumber(num) {
	//console.log(num);
}	
	 
$(document)
	.ready(function() {
			$( "#tabs" ).tabs();
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/_search" title="AMBIT Structure search">Structure search</a></li>');
			
			var cmpURI = "${ambit_root}/compound/${cmpid}";
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/compound/${cmpid}" title="Compound.">Compound ${cmpid}</a></li>');			
			<#if strucid??>
				cmpURI = "${ambit_root}/compound/${cmpid}/conformer/${strucid}";
				jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/compound/${cmpid}/conformer/${strucid}" title="Compound.">Structure ${strucid}</a></li>');
			</#if>

    		jQuery("#breadCrumb").jBreadCrumb();
    		jQuery("#welcome").html("&nbsp;");
    		
    		var dataset_uri =  "${ambit_root}/query/compound/url/all?media=application/json&search=" + encodeURIComponent(cmpURI);
    	  	var ds = new jToxCompound($(".jtox-toolkit")[0],config_dataset);
            ds.queryDataset(dataset_uri);
            
    		/*
			$.ajax({
				  url: "${ambit_root}/query/compound/url/all?media=application/json&search=" + encodeURIComponent(cmpURI),
				  dataType:"json",
			      success: function(data, status, xhr) {
			    	  $('#identifiers').html(data);
			      }
				});
			
			var pTable = definePropertyValuesTable("${ambit_root}","${ambit_request_json}","#properties");
			var cmpURI_datasets = cmpURI + "/dataset?media=application/json";
			try {
				var oTable = defineDatasetsTable("${ambit_root}",cmpURI_datasets,false,false);
			} catch (err) {

			}
			*/
            console.log(cmpURI);
			
            try {
            	$('#structype_${cmpid}').load(cmpURI + '/comparison');
            } catch (err) {}
            try {
			$('#consensus_${cmpid}').load(cmpURI + '/consensus');
            } catch (err) {}
            
			loadHelp("${ambit_root}","about");

			/*
			modelVarsAutocomplete(".modeluri","${ambit_root}/model",100);
			*/
			

	 });
</script>
</#if>
<script type='text/javascript'>
	//var cmp = readCompound("${ambit_root}","${ambit_request_json}");
	 
$(document)
	.ready(function() {
			$( "#selectable" ).selectable( "option", "distance", 18);
	 });
	 
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">


<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	<#include "/compound_menu.ftl">

</div>

<div class="thirteen columns remove-bottom" style="padding:0;" >


		<!-- Page Content
		================================================== -->

		<div class="jtox-toolkit" data-kit="compound" 
		data-configuration="config_dataset" 
		data-cross-domain="false"	
		data-show-export="yes"
		data-on-details="onDetailedRow"
		data-tabs-folded="true"
		data-on-error="errorHandler" 
		data-jsonp="false">
		</div>

<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

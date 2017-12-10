<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<#setting url_escaping_charset='utf-8'>


<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui-misc.js'></script>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/depict" title="Depiction">Depict</a> </li>');
					jQuery("#breadCrumb").jBreadCrumb();
					loadHelp("${ambit_root}","depict");
					
					<#assign _smiles='${(depict_smiles)!""}'>
					<#assign _smarts='${(depict_smarts)!""}'>
					
					<#if depict_option?? && depict_option == "standardize">
					 jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/depict/standardize" title="Standardization">Standardization</a> </li>');
					 <#assign _neutralise='${(neutralise)!"ON"}'>
					 <#assign _tautomer='${(tautomer)!"ON"}'>
					 <#assign _fragment='${(fragment)!"ON"}'>
					$("#_searchdiv").html("<form action='#'><b>SMILES or InChI</b> <input name='search' value='${_smiles}' id='search'>	 <b>Neutralise</b> <input name='neutralise' type='checkbox'  id='neutralise' checked='${_neutralise}'> <b>Tautomerisation</b> <input name='tautomer' type='checkbox' id='tautomer' checked='${_tautomer}'> <b>Split fragments</b> <input name='fragment' type='checkbox' id='fragment' checked='${_fragment}'> <input type='submit' value='Search'></form>");
					<#else>
					$("#_searchdiv").html("<form action='#'><b>SMILES or InChI</b> <input name='search' value='${_smiles}' id='search'> <b>SMARTS (optional)</b> <input name='smarts' id='smarts' value='${_smarts}'> <input type='submit' value='Search'></form>");
					</#if>
					
					
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<div id="logger" class="jtox-toolkit jtox-widget hidden" data-kit="log" data-resend="false" rightSide="true"></div>

	<!-- banner -->
	<#include "/banner_crumbs.ftl">

	
<#if depict_option?? && depict_option != "all">


	<#if depict_option == "standardize">
		
		<div class="row" >

		<div class="one column" style="padding:0;" >&nbsp;</div>
		<div class="seven columns" style="padding:0;" >
		
			<div class='ui-widget-header ui-corner-top'>&nbsp;CDK</div>
			<div class='ui-widget-content ui-corner-bottom'>
				<a href='${ambit_root}/depict/cdk?search=${depict_smiles?url}' title='CDK depiction'>
				<img id='cdk' src='${ambit_root}/depict/cdk?search=${depict_smiles?url}&media=image/png' onError='hideDiv("cdk")'>
				</a>
			</div>
		</div>
		
		
		<div class="seven columns" style="padding:0;" >
		
		<div class='ui-widget-header ui-corner-top'>&nbsp;Standardized</div>
		<div class='ui-widget-content ui-corner-bottom'>	
				<img src='${ambit_request}&media=image/png'>			
		</div>
		</div>
		
		</div>
		

	<#else>
			<div class="fourteen columns" style="padding:0;" >
		
		<div class='ui-widget-header ui-corner-top'>&nbsp;Depict ${depict_option}</div>
		<div class='ui-widget-content ui-corner-bottom'>
				<img src='${ambit_request}&media=image/png'>
				</div>
		<div class='row add-bottom' style="height:140px;">&nbsp;</div>
		
		</div>

	</#if>
				


<#else>

<#if depict_smiles??>

<div class="row" >

<div class="one column" style="padding:0;" >&nbsp;</div>
<div class="seven columns" style="padding:0;" >

	<div class='ui-widget-header ui-corner-top'>&nbsp;CDK</div>
	<div class='ui-widget-content ui-corner-bottom'>
		<a href='${ambit_root}/depict/cdk?search=${depict_smiles?url}&smarts=${depict_smarts?url}' title='CDK depiction'>
		<img id='cdk' src='${ambit_root}/depict/cdk?search=${depict_smiles?url}&smarts=${depict_smarts?url}&media=image/png' onError='hideDiv("cdk")'>
		</a>
	</div>
</div>


<div class="seven columns" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;OpenBabel</div>
<div class='ui-widget-content ui-corner-bottom'>	
	<img id='obabel' src='${ambit_root}/depict/obabel?search=${depict_smiles?url}&smarts=${depict_smarts?url}&media=image/png' onError='hideDiv("obabel")'>			
</div>
</div>

</div>

<div class="row" >
<div class="one column" style="padding:0;" >&nbsp;</div>
<div class="seven columns" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;PubChem</div>
<div class='ui-widget-content ui-corner-bottom'>	
	<img id='pubchem' src='${ambit_root}/depict/pubchem?search=${depict_smiles?url}&smarts=${depict_smarts?url}&media=image/png' onError='hideDiv("pubchem")'>			
</div>
</div>

<div class="seven columns" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Chemical Identifier Resolver</div>
<div class='ui-widget-content ui-corner-bottom'>	

	<img id='cactvs' src='${ambit_root}/depict/cactvs?search=${depict_smiles?url}&smarts=${depict_smarts?url}&media=image/png' onError='hideDiv("cactvs")'>			
</div>
</div>

</div>

</#if>

</#if>



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>


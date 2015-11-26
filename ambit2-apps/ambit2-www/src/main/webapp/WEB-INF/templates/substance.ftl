<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
<script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>

	<script type='text/javascript'>
	
	$(document).ready(function() {
		 $( "#tabs" ).tabs();
        <#if menu_profile?? && menu_profile=='enanomapper'>		        
        	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Nanomaterials">Search nanomaterials by identifiers</a></li>');
        	loadHelp("${ambit_root}","nanomaterial");
    		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/substance'><input type='radio' name='type' id='type_name' value='name' title='Name (starting with a string)'>Name <input type='radio' name='type' id='type_' value=''  title='External identifier of NM, e.g. NM-111'>Identifier <input type='radio' name='type' id='type_citation' title='Experiment reference, e.g. DOI' checked value='citation'>Reference <input type='radio' name='type' id='type_substancetype' checked value='substancetype'>NM type <input name='search' class='search' value='' id='search'> <input type='submit' value='Search'></form>");
    		$("#header_substance").text("Nanomaterials");
		<#else>
    		<#if menu_profile?? && menu_profile=='lri'>
	    		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substances: Mono-constituent, multiconstituent, additives, impurities.">Search substances by identifiers</a></li>');
	    		loadHelp("${ambit_root}","substance");
	    		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/substance'><input type='radio' name='type' id='type_name' value='name' title='Name (starting with a string)'>Name <input type='radio' name='type' id='type_uuid' value='uuid'>UUID <input type='radio' name='type' id='type_regexp'  value='regexp'>Name (regexp) <input type='radio' name='type' id='type_' value=''>External identifier <input name='search' class='search' value='' id='search'> <input type='submit' value='Search'></form>");
    		<#else>
        		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substances: Mono-constituent, multiconstituent, additives, impurities.">Search substances by identifiers</a></li>');
        		loadHelp("${ambit_root}","substance");
        		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/substance'><input type='radio' name='type' id='type_name' value='name' title='Name (starting with a string)'>Name <input type='radio' name='type' id='type_like' value='like'>Name (pattern matching) <input type='radio' name='type' id='type_regexp'  value='regexp'>Name (regexp) <input name='search' class='search' value='' id='search'> <input type='submit' value='Search'></form>");
    		</#if>
        </#if>
		var purl = $.url();
		$('.search').attr('value',purl.param('search')===undefined?'':purl.param('search'));
		
		var typeToSelect = purl.param('type')===undefined?'':purl.param('type');
	
        $("#selecttype option").each(function (a, b) {
	          if ($(this).val() == typeToSelect ) $(this).attr("selected", "selected");
	    });
        $("#type_"+typeToSelect).prop("checked", true);

        
        jQuery("#breadCrumb").jBreadCrumb();
	  	downloadForm("${ambit_request}");		
	  		
 		var ds = new jToxSubstance($(".jtox-toolkit")[0], {crossDomain: false, selectionHandler: "query", embedComposition: true, showDiagrams: true } );
        ds.querySubstance('${ambit_request_json}');	  		



	});
	</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div id="tabs" class="fourteen columns remove-bottom"  style="padding:0;">
<ul>
<li><a href="#tabs_substance" id="header_substance">Substances</a></li>
<li><a href="#tabs_search">Advanced search</a></li>
<li><a href="#download">Download</a></li>
</ul>

	<div id='download' >  <!-- tabs_download -->
<!--	
	<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
-->	
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json64.png' alt='json' title='Download as JSON'></a>
	<a href='#' id='csv' target=_blank><img src='${ambit_root}/images/csv64.png' alt='CSV' title='Download as CSV'></a>
	
	</div>
	
	<div id="tabs_search">
	<#include "searchmenu/menu_substance.ftl" >
	</div>
	
	<div id="tabs_substance">

		<!-- Page Content
		================================================== -->
		<div class="jtox-toolkit" data-kit="substance" data-manual-init="true" data-on-error="errorHandler" ></div>
		
  	<div class="jtox-template">
<!--[[ jT.templates['all-substance'] -->
	  <div id="jtox-substance" class="jtox-substance" data-show-diagrams="true" data-on-error="errorHandler" >
	    <div class="jtox-controls">
	      Showing from <span class="data-field from-field" data-field="pagestart"> ? </span> to <span class="data-field" data-field="pageend"> ? </span> in pages of <select class="data-field" data-field="pagesize">
          <option value="10" selected="yes">10</option>
          <option value="20">20</option>
          <option value="50">50</option>
          <option value="100">100</option>
          <option value="200">200</option>
          <option value="500">500</option>
        </select> substances
	      <a class="paginate_disabled_previous prev-field" tabindex="0" role="button">Previous</a><a class="paginate_enabled_next next-field" tabindex="0" role="button">Next</a>
	      <input type="text" class="filterbox" placeholder="Filter..." />
	    </div>
	    <div>
        <table></table>
	    </div>
	  </div>
<!--  end of #jtox-substance ]]-->
  	</div> <!-- jtox-template -->
	</div>   <!-- tabs_substance -->



</div><!-- tabs -->
<#include "chelp.ftl" >

<div class='row add-bottom' style="height:140px;">&nbsp;</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

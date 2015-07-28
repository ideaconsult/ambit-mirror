<#include "/html.ftl">
<head>
  <#include "/header_updated.ftl">
  
<link rel="stylesheet" href="${ambit_root}/scripts/dataset/ui-query.css"/>
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
<link rel="stylesheet" href="${ambit_root}/style/ketcher.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/ketcher.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/dataset/ui-query.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/dataset/config-dataset.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/config/ce.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/npo.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/bao.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/toxcast.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config-study.js'></script>

<script language="JavaScript">
  var common_config = $.extend(true,{}, config_dataset, config_study);
</script>
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<style>
.ui-icon-folder-collapsed {
        zoom: 1.25;
}
.ui-icon-folder-open {
        zoom: 1.25;
}
</style>
<script type='text/javascript'>  
$(document)
		.ready(
				function() {
					    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/_search" title="AMBIT Structure search">Search structures and associated data</a></li>');
    					jQuery("#breadCrumb").jBreadCrumb();
    					jQuery("#welcome").html("&nbsp;");
    					loadHelp("${ambit_root}","search");
				});
</script>  

</head>
<body>

<div class="container" style="margin:0;padding:0;">


<#include "/banner_crumbs.ftl">
	
		<!-- Page Content
		================================================== -->
	<div class="sixteen columns remove-bottom" style="padding:0;" >

  <div class="jtox-toolkit" data-kit="query" data-initial-query="yes" data-cross-domain="false" 
  		data-configuration="common_config"  
  		data-hide-options="context"
  		data-base-url="${ambit_root}">
 		
    <div id="searchbar" class="jtox-toolkit jtox-widget" data-kit="search"></div>
    <div id="sidebar" class="hidden">
      <div>
        <span class="side-open-close ui-icon ui-icon-carat-2-e-w"></span>
        <div class="side-title">Select data sources/models</div>
      </div>
      <div class="content">
        <div class="jtox-foldable folded">
          <div class="title">
            <h5>Datasets (0/0)</h5>
          </div>
          <div class="content">
            <a href="#" class="select-all">select all</a>&nbsp;<a href="#" class="unselect-all">unselect all</a>
            <div class="jtox-toolkit jtox-widget" data-kit="dataset" data-short-stars="true" data-s-dom="rt" data-selectable="true" data-selection-handler="onSelectedUpdate" data-on-loaded="onSideLoaded" data-load-on-init="true"></div>
          </div>
        </div>
        <div class="jtox-foldable folded">
          <div class="title">
            <h5>Models (0/0)</h5>
          </div>
          <div class="content">
            <a href="#" class="select-all">select all</a>&nbsp;<a href="#" class="unselect-all">unselect all</a>
            <div class="jtox-toolkit jtox-widget" data-kit="model" data-short-stars="true" data-s-dom="rt" data-selectable="true" data-selection-handler="onSelectedUpdate" data-on-loaded="onSideLoaded" data-load-on-init="true" ></div>
          </div>
        </div>
      </div>
    </div>
	  <div id="browser" class="jtox-toolkit" data-kit="compound" data-remember-checks="true" 
	  data-hide-empty="true" data-details-height="500px" data-tabs-folded="true" 
	  data-on-error="errorHandler" 
	  <#if ajaxtimeout??>
	  data-timeout="${ajaxtimeout}"	
	  </#if>		  
	  data-on-details="onDetailedRow" data-show-diagrams="true"></div>
  </div>

   </div>

		
<div class='row add-bottom'>&nbsp;</div>

<#include "/footer.ftl" >

</div> <!-- container -->
</body>
</html>
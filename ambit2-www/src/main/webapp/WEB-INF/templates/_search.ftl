<#include "/html.ftl">
<head>
  <#include "/header_updated.ftl">
  
<link rel="stylesheet" href="${ambit_root}/style/sidebar.css"/>
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
<link rel="stylesheet" href="${ambit_root}/style/ketcher.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/ketcher.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/dataset/dataset-details.js'></script>

<script type='text/javascript'>  
$(document)
		.ready(
				function() {
					    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/_search" title="AMBIT Structure search">Structure search</a></li>');
    					jQuery("#breadCrumb").jBreadCrumb();
    					jQuery("#welcome").html("&nbsp;");
    					loadHelp("${ambit_root}","search");
				});
</script>  
 <script language="JavaScript">

    $(document).ready(function(){
      var toggleBar = function () {
        $(this).parents('#sidebar').toggleClass('hidden');
      };
      $('#sidebar span.ui-icon').on('click', toggleBar);
      $('#sidebar div.side-title').on('click', toggleBar);
      
      $('#sidebar a.select-all').on('click', function () {
        $('input[type="checkbox"]', this.parentNode).each(function () { this.checked = true;});
        onSelectedUpdate(this);
      });
      $('#sidebar a.unselect-all').on('click', function () {
        $('input[type="checkbox"]', this.parentNode).each(function () { this.checked = false;});
        onSelectedUpdate(this);
      });
    });
</script>
  
<script type='text/javascript' src='${ambit_root}/scripts/config-dataset.js'></script>

</head>
<body>

<div class="container" style="margin:0;padding:0;">


<#include "/banner_crumbs.ftl">
	
		<!-- Page Content
		================================================== -->
	<div class="sixteen columns remove-bottom" style="padding:0;" >

  <div class="jtox-toolkit" data-kit="query" data-initial-query="yes" data-cross-domain="true" data-configuration="config_dataset" 
  		data-base-url="${ambit_root}">
    <div id="searchbar" class="jtox-toolkit jtox-widget" data-kit="search"></div>
    <div id="sidebar" class="hidden">
      <div>
        <span class="side-open-close ui-icon ui-icon-carat-2-e-w"></span>
        <div class="side-title">Data sources</div>
      </div>
      <div class="content">
        <div class="jtox-foldable folded">
          <div class="title">
            <h5>Datasets (0/0)</h5>
          </div>
          <div class="content">
            <a href="#" class="select-all">select all</a>&nbsp;<a href="#" class="unselect-all">unselect all</a>
            <div class="jtox-toolkit jtox-widget" data-kit="dataset" data-short-stars="true" data-s-dom="rt" data-selectable="true" data-selection-handler="checked" data-on-loaded="onSideLoaded" data-load-on-init="true"></div>
          </div>
        </div>
        <div class="jtox-foldable folded">
          <div class="title">
            <h5>Models (0/0)</h5>
          </div>
          <div class="content">
            <a href="#" class="select-all">select all</a>&nbsp;<a href="#" class="unselect-all">unselect all</a>
            <div class="jtox-toolkit jtox-widget" data-kit="model" data-short-stars="true" data-s-dom="rt" data-selectable="true" data-selection-handler="checked" data-on-loaded="onSideLoaded" data-load-on-init="true" ></div>
          </div>
        </div>
      </div>
    </div>
	  <div id="browser" class="jtox-toolkit" data-kit="compound" data-remember-checks="true" data-on-details="onDetailedRow" data-details-height="500px" data-tabs-folded="true"></div>
  </div>

   </div>

		
<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/facet/substance.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/config/ce.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/toxcast.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/npo.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/bao.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config-study.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>


<script type='text/javascript'>
$(document).ready(function() {
	facet.defineStudySearchFacets("${ambit_root}",
				"${ambit_root}/query/study?topcategory=P-CHEM&media=application/json",
				"#facet_pchem");
	facet.defineStudySearchFacets("${ambit_root}",
			"${ambit_root}/query/study?topcategory=TOX&media=application/json",
			"#facet_tox");
	facet.defineStudySearchFacets("${ambit_root}",
			"${ambit_root}/query/study?topcategory=ECOTOX&media=application/json",
			"#facet_ecotox");
	facet.defineStudySearchFacets("${ambit_root}",
			"${ambit_root}/query/study?topcategory=ENV%20FATE&media=application/json",
			"#facet_envfate");	

	$( "#accordion" ).accordion( {
		heightStyle: "content"
	});	
	var ds = new jToxSubstance($(".jtox-toolkit")[0], {
								crossDomain: false, selectionHandler: "query",
								timeout: 30000, 
								embedComposition: true, showDiagrams: true } );
	facet.substanceComponent = ds;
	facet.root = "${ambit_root}";
	//ds.querySubstance('${ambit_root}/substance');	  	
	
	downloadForm("${ambit_request}");

	jQuery("#breadCrumb ul").append('<li><a href="#" onClick="facet.searchStudy();">Search substances by endpoint data</a></li>');
	jQuery("#breadCrumb ul").append('<li id="hits" style="display:none"><a href="#" onClick="facet.searchStudy();">Hit list</a></li>');
	jQuery("#submit").show();
	jQuery("#breadCrumb").jBreadCrumb();

});
</script>
<style>
	#accordion .ui-accordion-content { padding: 2px; }
</style>

<style>
	#sidebar {
  	background-color: #fafafa;
  	border: 1px solid #ccc;
  	-moz-transition: left 0.5s ease;
  	-webkit-transition: left 0.5s ease;
  	-o-transition: left 0.5s ease;
  	border-radius: 7px;
  	box-shadow: 3px 3px 7px #999;
	}


	</style>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">
<!-- banner -->

<#include "/banner_crumbs.ftl">

<form action='${ambit_root}/substance' id='fsearchForm' name='fsearchForm' method='GET' autocomplete='off' >
<div class="four columns" id="sidebar" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	<input type='button' class='remove-bottom' value='Update results' onClick='facet.searchStudy();'>
	<div id="accordion" style="padding-top:0;padding-left:1px;padding-right:1px;padding-bottom:2px;margin:0;font-size:80%">
		<h3>P-Chem</h3>
		  <table id='facet_pchem' class='facet .jtox-toolkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead><tr><th ></th><th ></th></tr></thead>
			<tbody></tbody>
		   </table>
		<h3>Env Fate</h3>
		  <table id='facet_envfate' class='facet ' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead><tr><th ></th><th ></th></tr></thead>
			<tbody></tbody>
		   </table>
		<h3>Eco Tox</h3>
		  <table id='facet_ecotox' class='facet ' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead><tr><th ></th><th ></th></tr></thead>
			<tbody></tbody>
		   </table>
		<h3>Tox</h3>
		  <table id='facet_tox' class='facet ' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead><tr><th ></th><th ></th></tr></thead>
			<tbody></tbody>
		   </table>
		</div>
		<input type='button' class='remove-bottom'  value='Update results' onClick='facet.searchStudy();'>
		
</form>			
			
</div>
 		
		<div class="twelve columns remove-bottom" style="padding:0;" >

 		
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
<!-- // end of #jtox-substance ]]-->

		
		</div> 
		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl">

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jontobucket.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>

<script type='text/javascript'>
	
$(document).ready(function() {
 	var oTable = defineOntoBucketTable("${ambit_root}","${ambit_request_json}");
});
</script>

<script type='text/javascript'>

$(document)
	.ready(function() {
		$( "#tabs" ).tabs();
		var purl = $.url();
		$('#search').attr('value',purl.param('search')===undefined?'toxicity':purl.param('search'));
		$('#selecttype').val(purl.param('type')===undefined?'all':purl.param('type'));	
		$('#qe').val(purl.param('qe')===undefined?'all':purl.param('qe'));
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ontobucket" title="Search">Free text search</a></li>');
		jQuery("#breadCrumb").jBreadCrumb();
		jQuery("#welcome").html("");		
		
		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/ontobucket'><input type='radio' name='type' id='type_all' value='all' title='All'>All <input type='radio' name='type' id='type_endpoint' value='endpoint' title='Endpoint'>Endpoint <input type='radio' name='type' id='type_protocol' checked value='protocol' title='Protocol'>Protocol <input type='radio' name='type' id='type_substancetype' value='substancetype' title='NM type'>NM type <input name='search' class='search' value='' id='search'> <input type='submit' value='Search'></form>");
		var purl = $.url();
		$('.search').attr('value',purl.param('search')===undefined?'':purl.param('search'));
		
		var typeToSelect = purl.param('type')===undefined?'all':purl.param('type');
	
        $("#selecttype option").each(function (a, b) {
	          if ($(this).val() == typeToSelect ) $(this).attr("selected", "selected");
	    });
        $("#type_"+typeToSelect).prop("checked", true);
        
        var qe = purl.param('qe')===undefined?'true':purl.param('qe');
    	
        $("#qe option").each(function (a, b) {
	          if ($(this).val() == typeToSelect ) $(this).attr("selected", "selected");
	    });
        
		loadHelp("${ambit_root}","ontobucket");
		downloadForm("${ambit_request}");
	}
);
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">


<#include "/banner_crumbs.ftl">

<div id="tabs" class="fourteen columns remove-bottom"  style="padding:0;">
<ul>
<li><a href="#tabs_substance" id="header_substance">Substances</a></li>
<li><a href="#tabs_search">Advanced search</a></li>
<li><a href="#download">Download</a></li>
</ul>

		<div id='tabs_search' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
		<form method='GET' name='searchform' id='searchform' action='${ambit_root}/ontobucket' style='padding:0;margin:0;'>
		<label>Search term</label><a href='#' class='chelp hterm'>?</a>		
		<input type='text'  id='search' name='search' value='' tabindex='1' >
		<label>Entries</label><a href='#' class='chelp hentries'>?</a>				
		<select id='selecttype' name="type">
				  	 <option value="label">Name</option>
				  	 <option value="subclass">Ontology</option>
				  	 <option value="endpoint">Endpoint</option>
					 <option value="hash">Conditions</option>
					 <option value="protocol">Protocol</option>
					 <option value="substancetype">Substance type</option>
					 <option value="reference">Reference</option>
					 <option value="target">Target</option>
					 <option value="all">All</option>
				</select>
		<label>Query expansion</label><a href='#' class='chelp hqe'>?</a>		
		<select id='qe' name="qe">
				  	 <option value="true">with</option>
				  	 <option value="false">without</option>
				</select>		
		<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		</form>
		</div>

	<div  id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
		<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf64.png' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
		<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf64.png' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
		<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json64.png' alt='json' title='Download as JSON'></a>
	</div>
	
	<div id="tabs_substance" >

		<!-- Page Content
		================================================== -->
			<table id='ontobucket'  class='jtoxkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>Term</th>
			<th>Title</th>
			<th>Related to </th>			
			<th>Hit relevance</th>
			<th title='Click to view studies'>Find studies</th>				
			</tr>
			</thead>
			<tbody></tbody>
			</table>
	</div>	
		
</div> <!-- tabs -->


<#include "/chelp.ftl" >

<div class='row add-bottom' style="height:140px;">&nbsp;</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

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
		var purl = $.url();
		$('#search').attr('value',purl.param('search')===undefined?'toxicity':purl.param('search'));
		$('#selecttype').val(purl.param('type')===undefined?'all':purl.param('type'));	
		$('#qe').val(purl.param('qe')===undefined?'all':purl.param('qe'));
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ontobucket" title="Search">Free text search</a></li>');
		jQuery("#breadCrumb").jBreadCrumb();
		jQuery("#welcome").html("");		
		loadHelp("${ambit_root}","ontobucket");
		downloadForm("${ambit_request}");
	}
);
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">


<#include "/banner_crumbs.ftl">

<div class="three columns remove-bottom" style="padding:0;" >

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
<label>Free text search</label>
</div>
<div class='row remove-bottom' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
<form method='GET' name='searchform' id='searchform' action='${ambit_root}/ontobucket' style='padding:0;margin:0;'>
<input type='text'  id='search' name='search' value='' tabindex='1' >
<label>Entries</label>				
<select id='selecttype' name="type">
		  	 <option value="label">Name</option>
		  	 <option value="subclass">Ontology</option>
		  	 <option value="endpoint">Endpoint</option>
			 <option value="hash">Conditions</option>
			 <option value="protocol">Protocol</option>
			 <option value="all">All</option>
		</select>
<label>Query expansion</label>		
<select id='qe' name="qe">
		  	 <option value="true">with</option>
		  	 <option value="false">without</option>
		</select>		
<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
</form>
</div>

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
</div>

<!-- help-->	
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>

</div>
	
<div class="thirteen columns remove-bottom" style="padding:0;" >

 		<div class="row remove-bottom ui-widget-header ui-corner-top">
 		&nbsp;
 		</div>

		<!-- Page Content
		================================================== -->
		<div class="row" style="padding:0;" >
			<table id='ontobucket'  class='ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>Term</th>
			<th>Title</th>
			<th>Related to </th>			
			<th>Hit importance</th>
			<th>Find studies</th>				
			</tr>
			</thead>
			<tbody></tbody>
			</table>
		</div>
		
</div>


		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>

</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

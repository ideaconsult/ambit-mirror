<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jcompound.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/ambit_structures.js'></script>

<script type='text/javascript'>
    _ambit.query_service = '${ambit_root}';
	var purl = $.url();
	
	_ambit.selectedModels = purl.param('model_uri');
	_ambit.selectedDatasets = purl.param('dataset_uri');
	var option = purl.param('option')===undefined?'auto':purl.param('option');
	var params = {
			'threshold'	:purl.param('threshold')===undefined?'0.8':purl.param('threshold'),
			'media'		:'application/json',
			//'search'	:purl.param('search')===undefined?'50-00-0':purl.param('search'),
			'b64search'	:purl.param('search')===undefined?$.base64.encode('50-00-0'):$.base64.encode(purl.param('search')),
			'pagesize'	:purl.param('pagesize')===undefined?'10':purl.param('pagesize'),
			'type' : 'smiles'	
			};

	$(function() {
		initSearchForm();
		_ambit.models = initTable(null,'${ambit_root}/model?max=10','#models',_ambit.models,"model_uri[]",_ambit.selectedModels,"selectModel");
		_ambit.datasets = initTable('${ambit_root}','${ambit_root}/dataset?max=10','#datasets',_ambit.datasets,"dataset_uri[]",_ambit.selectedDatasets,"selectDataset");
		$("#searchform").validate();
		$("#accordion" ).accordion();
	});	
	

	$(document)
			.ready(
					function() {
												
						//ajax
						$.ajaxSetup({
							cache : _ambit.cache  //while dev
						});
						var url;
						var queryService = '${ambit_root}';
						var description = null;
						switch(option)
						{
						case 'similarity':
						  if ((purl.param('search')!= undefined) && (purl.param('search').indexOf('http')>=0)) {
						  	params['type'] = 'url';
						  	params['search']  = purl.param('search');
						  	delete params.b64search;
						  	description = "URI";	
						  }
						  $('#qtype').text('Similarity search');	
						  $('#qthreshold').text('Tanimoto >= '+ purl.param('threshold'));
						  url = queryService	+ "/query/similarity?";
						  break;
						case 'smarts':
						  delete params.threshold;
						  if ((purl.param('search')!= undefined) && (purl.param('search').indexOf('http')>=0)) {
						  	params['type'] = 'url';
						  	params['search']  = purl.param('search');
						  	delete params.b64search;
						  	description = "URI";	
						  }
						  $('#qtype').text('Substructure search');
						  $('#qthreshold').text('');
  						  url = queryService	+ "/query/smarts?";
						  break;
						default: //auto
						  delete params.threshold;
						  if ((purl.param('search')!= undefined) && (purl.param('search').indexOf(queryService)>=0)) {
							url = purl.param('search');
						  	delete params.b64search;
						  	delete params.threshold;
						  	delete params.type;
						  	delete params.search;
						  	url = url + "?" ;
						  	$('#qtype').text('Search by URI');
						  	description = "URI";	
						  } else {						
						  	$('#qtype').text('Search by identifier');
						  	$('#qvalue').text("");
						  	url = queryService + "/query/compound/search/all?";
						  }
						  $('#qthreshold').text('');
						}
						
						if ((description==null) && (purl.param('search')!= undefined)) {
							if (purl.param('search').length>60) description = purl.param('search').substring(0,60)+" ...";
							else  description = purl.param('search');
						} else description="";
							
						$('#qvalue').text(description);
						
						$('#qvalue').attr('title',purl.param('search'));
						
						$('#quri').attr('href',url);
						$('#quri').attr('title','AMBIT Search URI: ' + url);
						
						_ambit['query_uri'] = url;
						_ambit['data_uri'] = null; 
						_ambit['query_params'] = params;
						var qurl = url +  $.param(params,false);
						downloadFormUpdate(null);

						var oTable = defineStructuresTable(qurl, queryService,purl.param('option')=='similarity');
						
					});

	$(function() {
		$(".ambit_search").button();
	});
	$('#structures').css('class','remove-bottom');

</script>

<script type="text/javascript" >
		$.cookie('ambit2.search', purl.param('search')===undefined?'50-00-0':purl.param('search'), { expires: 1 });
		$.cookie('ambit2.threshold', purl.param('threshold')===undefined?'0.8':purl.param('threshold'), { expires: 1 });
		$.cookie('ambit2.option', purl.param('option')===undefined?'auto':purl.param('option'), { expires: 1 });
		$.cookie('ambit2.pagesize', purl.param('pagesize')===undefined?'10':purl.param('pagesize'), { expires: 1 });
</script>

</head>

<body>



<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/ui' style='padding:0;margin:0;'>

<#include "/banner.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	<div class='ui-widget help half-bottom' style='fill:auto;'>
		<div class='ui-widget-header ui-corner-top'>Query</div>
		<div class='ui-widget-content ui-corner-bottom remove-bottom' style='margin:0;padding: 2px;'>
			<span id='qtype' ></span>
			<br>
			<span id='qthreshold'></span>
			<br>
			<b><span id='qvalue'></span></b>
			<span id='description' style='display:none;'></span>
			<a href='#' id='quri' title='#'>@</a>
			<br>
			<span>Max number of hits</span>
			<input type='text' size='3' name='pagesize' value='10' style='width:5em;height:1.5em;margin-bottom:0;padding: 2px;'>
		</div>
	</div>
	<input class='ambit_search' id='submit2' type='submit' value='Refresh' tabindex='3'/>

	<#include "/select_features.ftl">
	

	<br>
	<span id='download' class='help'>Download:<br>
	<a href='#' id='sdf'><img src='${ambit_root}/images/sdf.jpg' alt='SDF' title='Download as SDF' /></a>
	<a href='#' id='csv'><img src='${ambit_root}/images/excel.png' alt='CSV' title='Download as CSV (Comma delimited file)'/></a>
	<a href='#' id='cml'><img src='${ambit_root}/images/cml.jpg' alt='CML' title='Download as CML (Chemical Markup Language)'/></a>
	<a href='#' id='arff'><img src='${ambit_root}/images/weka.jpg' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'/></a>
	<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'/></a>
	<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'/></a>
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'/></a>
	</span>

</div>

</form>


<form action="${ambit_root}/ui" method="get">  
<div class="eleven columns remove-bottom" style="padding:0;" >

	
		<table id='structures' class='structable' style='margin:0;' width='100%'>
					<thead>
						<th>
						<a href="#" id='selectall' class='help' title='Click to select all records' onClick='selecturi(true);'><u>Select</u></a><br>
						<a href="#" id='unselect' class='help' title='Click to unselect all records'  onClick='selecturi(false);'><u>Unselect</u></a>						
						</th>
						<th>CAS</th>
						<th>Structure</th>
						<th>Name</th>
						<th>Similarity</th>
						<th>SMILES</th>
						<th>InChI</th>
						<th>InChI Key</th>
					</thead>
					<tbody></tbody>
		</table>


</div>

<div class="two columns" style="margin:0;padding:0;" >
<!--
	<input  class='ambit_search' type='submit' value='Do XXX'>
-->

	<span class='help'>OpenTox API<br>
		<a href='${ambit_root}/dataset' target='_blank'>Datasets</a>
		<a href='${ambit_root}/feature' target='_blank'>Features</a>
		<a href='${ambit_root}/compound' target='_blank'>Chemicals</a>
		<a href='${ambit_root}/algorithm' target='_blank'>Algorithms</a>
		<a href='${ambit_root}/model' target='_blank'>Models</a>
		<a href='${ambit_root}/task' target='_blank'>Tasks</a>
		<a href='${ambit_root}/query' target='_blank'>Query</a>
		<a href='${ambit_root}/query/compound/search/all' target='_blank'>Exact search</a>
		<a href='${ambit_root}/query/similarity?search=c1ccccc1' target='_blank'>Similarity</a>
		<a href='${ambit_root}/query/smarts?search=c1ccccc1' target='_blank'>Substructure</a>
	</span>
	<span class='help'>Demos<br>
		<a href='${ambit_root}/depict?search=c1ccccc1' target='_blank'>2D depiction</a>
		<a href='${ambit_root}/depict/reaction?search=c1ccccc1' target='_blank'>Reactions (SMIRKS)</a>
		<a href='${ambit_root}/depict/tautomer?search=NC%3D1N%3DCN%3DC2N%3DCNC2%3D1' target='_blank'>Tautomers</a>		
	</span>
	<span class='help'>Applications<br>
		<a href='http://toxpredict.org' target='_blank'>ToxPredict</a>
	</span>	
</div>

</form>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

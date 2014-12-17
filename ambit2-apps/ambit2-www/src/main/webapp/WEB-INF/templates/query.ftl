<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<script type='text/javascript' src='${ambit_root}/scripts/ketcher_editor.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jcompound.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/ambit_structures.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui-substance.js'></script>

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<script type='text/javascript'>
    _ambit.query_service = '${ambit_root}';
	var purl = $.url();
	
	_ambit.selectedModels = purl.param('model_uri');
	_ambit.selectedDatasets = purl.param('dataset_uri');
	var option = purl.param('option')===undefined?'auto':purl.param('option');
	var b64 = purl.param('b64search');
	var psearch = "";
	if (b64===undefined || b64=="") 
		psearch = purl.param('search')===undefined?'50-00-0':purl.param('search');
	else psearch="";
	
	$.cookie('ambit2.search', psearch, { expires: 1 });
	$.cookie('ambit2.b64search', b64===undefined?'':b64, { expires: 1 });
	$.cookie('ambit2.type', purl.param('type')===undefined?'smiles':purl.param('type'), { expires: 1 });
	$.cookie('ambit2.threshold', purl.param('threshold')===undefined?'0.8':purl.param('threshold'), { expires: 1 });
	$.cookie('ambit2.option', purl.param('option')===undefined?'auto':purl.param('option'), { expires: 1 });
	$.cookie('ambit2.pagesize', purl.param('pagesize')===undefined?'10':purl.param('pagesize'), { expires: 1 });

	var params = {
			'threshold'	:purl.param('threshold')===undefined?'0.8':purl.param('threshold'),
			'media'		:'application/json',
			//'search'	:purl.param('search')===undefined?'50-00-0':purl.param('search'),
			'b64search'	:(b64===undefined || b64=="")?(psearch===undefined || psearch==""?$.base64.encode('50-00-0'):$.base64.encode(psearch)):b64,
			'pagesize'	:purl.param('pagesize')===undefined?'10':purl.param('pagesize'),
			'type' : purl.param('type')===undefined?'smiles':purl.param('type')	
			};
	$(function() {
		initSearchForm();
		initTable("model",null,'${ambit_root}/model?max=20','#models',"models","model_uri[]",_ambit.selectedModels,"selectModel");
		initTable("dataset",'${ambit_root}','${ambit_root}/dataset?max=20','#datasets',"datasets","dataset_uri[]",_ambit.selectedDatasets,"selectDataset");
		$("#searchform").validate();
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
						  var t = purl.param('threshold');
						  $('#qthreshold').text('Tanimoto >= '+ (t===undefined?'0.8':t));
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
						$('#quri').attr('title','AMBIT search URI '+url);
						
						_ambit['query_uri'] = url;
						_ambit['data_uri'] = null; 
						_ambit['query_params'] = params;
						var qurl = url +  $.param(params,false);

						downloadFormUpdate(null);
						$('#quri').attr('href',queryService + "/ui/_dataset?dataset_uri=" + encodeURIComponent(qurl));
						var oTable = defineStructuresTable(qurl, queryService,purl.param('option')=='similarity',"${ambit_root}");
						
					});

	$(function() {
		$(".ambit_search").button();
	});
	$('#structures').css('class','remove-bottom');

</script>

</head>
<body>

<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/ui/query' style='padding:0;margin:0;'>

<#include "/banner.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >

	<ul id='selectable'>
	<li class="ui-selectee">
	<a href="${ambit_root}/ui"><span class="ui-icon ui-icon-home" style="float: left; margin-right: .3em;"></span>Home</a>
	</li>
	<li class="ui-selected">
	<a href=""><span class="ui-icon ui-icon-search" style="float: left; margin-right: .3em;"></span>
	Query</a>
	</li>	
	</ul>
	<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
		<span id='qtype' ></span>	
		<br/>
		<a href='#' id='quri' title='#'>New view mode</a>
		<br/>
		<span id='qthreshold'></span>
		<br/>
		<b><span id='qvalue'></span></b>
		<span id='description' style='display:none;'></span>
		<span>Max number of hits</span>
		<input type='text' size='3' name='pagesize' value='10' style='width:5em;height:1.5em;margin-bottom:0;padding: 2px;'>
		<input class='ambit_search remove-bottom' id='submit2' type='submit' value='Refresh' tabindex='3'>
	</div>

	<#include "/select_features.ftl">
	

	<ul id="lselectable">
	<li class="ui-selectee" >
	<span class="ui-icon ui-icon-disk" style="float: left; margin-right: .3em;" ></span>
	Download
	</li>
	</ul>
	<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='sdf'><img src='${ambit_root}/images/sdf.jpg' alt='SDF' title='Download as SDF' ></a>
	<a href='#' id='csv'><img src='${ambit_root}/images/excel.png' alt='CSV' title='Download as CSV (Comma delimited file)'></a>
	<a href='#' id='cml'><img src='${ambit_root}/images/cml.jpg' alt='CML' title='Download as CML (Chemical Markup Language)'></a>
	<a href='#' id='arff'><img src='${ambit_root}/images/weka.png' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'></a>
	<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
	<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
	</div>


</div>

</form>


<form action="${ambit_root}/ui/query" method="get">  
<div class="thirteen columns remove-bottom" style="padding:0;" >
 		<div class="row remove-bottom ui-widget-header ui-corner-top"  style="padding:0;" >
 		&nbsp;
 		</div>
	<div class="row " style="padding:0;" >
		<table id='structures' class='structable ambit2' style='margin:0;' width='100%'>
					<thead>
					<tr>
						<th>
						<a href="#" id='selectall' class='help' title='Click to select all records' onClick='selecturi(true);'><u>Select</u></a><br>
						<a href="#" id='unselect' class='help' title='Click to unselect all records'  onClick='selecturi(false);'><u>Unselect</u></a>						
						</th>
						<th>CAS</th>
						<th>EC</th>
						<th>Structure</th>
						<th>Name</th>
						<th>Similarity</th>
						<th>SMILES</th>
						<th>InChI</th>
						<th>InChI Key</th>
					</tr>	
					</thead>
					<tbody> </tbody>
		</table>
		</div>

</div>

<!--
<div class="one column" style="margin:0;padding:0;" >

	<input  class='ambit_search' type='submit' value='Do XXX'>
<#include "/help.ftl">

	
</div>
-->

</form>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jcompound.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/ambit_structures.js'></script>

<script type='text/javascript'>
	var purl = $.url();
	var option = purl.param('option')===undefined?'auto':purl.param('option');
	var params = {
			'threshold'	:purl.param('threshold')===undefined?'0.8':purl.param('threshold'),
			'media'		:'application/json',
			//'search'	:purl.param('search')===undefined?'50-00-0':purl.param('search'),
			'b64search'	:purl.param('search')===undefined?$.base64.encode('50-00-0'):$.base64.encode(purl.param('search')),
			'pagesize'	:purl.param('pagesize')===undefined?'10':purl.param('pagesize')					
			};

	$(function() {
		initSearchForm();
		$("#searchform").validate();
	});	
	
	$(document)
			.ready(
					function() {
												
						//ajax
						$.ajaxSetup({
							cache : false  //while dev
						});
						var url;
						var queryService = '${ambit_root}';
						switch(option)
						{
						case 'similarity': 
						  $('#qtype').text('Similarity search');	
						  $('#qthreshold').text('Tanimoto >= '+purl.param('threshold'));
						  url = queryService	+ "/query/similarity?" + $.param(params,false);
						  break;
						case 'smarts':
						  $('#qtype').text('Substructure search');
						  $('#qthreshold').text('');
  						  url = queryService	+ "/query/smarts?" + $.param(params,false);
						  break;
						default: //auto
						  $('#qtype').text('Search by identifier');
						  $('#qthreshold').text('');
						  url = queryService + "/query/compound/search/all?"+ $.param(params,false);
						}
						
						if (purl.param('search').length>60) $('#qvalue').text(purl.param('search').substring(0,60)+" ...");
						else  $('#qvalue').text(purl.param('search'));
						$('#qvalue').attr('title',purl.param('search'));
						
						$('#download #json').attr('href',url);
						
						var oTable = defineStructuresTable(url, queryService,purl.param('option')=='similarity');
						
						
					});

	$(function() {
		$(".ambit_search").button();
	});
	$('#structures').css('class','remove-bottom');

</script>

<script>
		$.cookie('ambit2.search', purl.param('search')===undefined?'50-00-0':purl.param('search'), { expires: 1 });
		$.cookie('ambit2.threshold', purl.param('threshold')===undefined?'0.8':purl.param('threshold'), { expires: 1 });
		$.cookie('ambit2.option', purl.param('option')===undefined?'auto':purl.param('option'), { expires: 1 });
		$.cookie('ambit2.pagesize', purl.param('pagesize')===undefined?'10':purl.param('pagesize'), { expires: 1 });
</script>

</head>

<body>

<form method='GET' name='searchform' id='searchform' method='get' action='${ambit_root}/ui'>
<div class="container columns" style="margin:0;padding:0;">

<#include "/banner.ftl">

<div class="two columns remove-bottom" style="padding:2px;margin-right:0;" >
	<div class='ui-widget help' style='fill:auto;'>
		<div class='ui-widget-header ui-corner-top'>Query</div>
		<div class='ui-widget-content ui-corner-bottom'>
			<span id='qtype' ></span>
			<br/>
			<span id='qthreshold'></span>
			<br/>
			<b><span id='qvalue'></span></b>
			<span id='description' style='display:none;'></span>
		</div>
	</div>
	<br/>
	<div class='ui-widget help remove-bottom' style='fill:auto;'>
		<div class='ui-widget-header ui-corner-top'>More options</div>
		<div class='ui-widget-content ui-corner-bottom'>
			<span>Max number of hits</span>
			<input type='text' size='3' name='pagesize' value='10' style='width:3em;height:1em;'>
			<br>
		</div>
	</div>
	<br/>
	<input class='ambit_search' id='submit2' type='submit' value='Refresh' tabindex='3'/>
	<br/>
	<span id='download' class='help'>Download:<br/>
	<a href='' id='json'><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'/></a>
	</span>
</div>

</form>
<div class="twelve columns remove-bottom" style="padding:0;" >


<form action="${ambit_root}/ui" method="get">  
	
		<table id='structures' class='structable' style='margin:0;' width='100%'>
					<thead>
						<th></th>
						<th>
						<a href="#" id='selectall' title='Click to select all records' onClick='selecturi(true);'><u>Select</u></a><br>
						<a href="#" id='unselect' title='Click to unselect all records'  onClick='selecturi(false);'><u>Unselect</u></a>						
						</th>
						<th>#</th>
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
	</form>
	<div class='help'>OpenTox API<br/>
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
	</div>
	<div class='help'>Demos<br/>
		<a href='${ambit_root}/depict?search=c1ccccc1' target='_blank'>2D depiction</a>
		<a href='${ambit_root}/depict/reaction?search=c1ccccc1' target='_blank'>Reactions (SMIRKS)</a>
		<a href='${ambit_root}/depict/tautomer?search=NC%3D1N%3DCN%3DC2N%3DCNC2%3D1' target='_blank'>Tautomers</a>		
	</div>
	<div class='help'>Applications<br/>
		<a href='http://toxpredict.org' target='_blank'>ToxPredict</a>
	</div>	
</div>




<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

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

<div class="container columns" style="margin:0;padding:0;">

<#include "/banner.ftl">

<div class="two columns" style="padding:0;" >
left menu
</div>

<div class="twelve columns remove-bottom" style="padding:0;" >


<form action="${ambit_root}/ui" method="get">  
	
		<table id='structures' class='structable' style='margin:0;' width='100%'>
					<thead>
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
	Query:  <span id='qtype'></span>&nbsp;<span id='qthreshold'></span>
	<br>
	<b><span id='qvalue'></span></b>
	<span id='description' style='display:none;'></span>
	<br>
	<input  class='ambit_search' type='submit' value='Do XXX'>
</div>


</div> <!-- container -->
</form>

<#include "/footer.ftl" >


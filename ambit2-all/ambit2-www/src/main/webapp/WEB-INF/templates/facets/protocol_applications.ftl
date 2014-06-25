<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>
$(document).ready(function() {
	var oTable = defineStudySearchFacets("${ambit_root}","${ambit_request_json}","#facet");
	$('#facet tbody').on('click','td.details-control span',function() {
					var nTr = $(this).parents('tr')[0];
					if (oTable.fnIsOpen(nTr)) {
						$(this).removeClass("ui-icon-folder-open");
						$(this).addClass("ui-icon-folder-collapsed");
						this.title='Click to show endpoints';
						oTable.fnClose(nTr);
					} else {
						$(this).removeClass("ui-icon-folder-collapsed");
						$(this).addClass("ui-icon-folder-open");
						this.title='Click to close endpoints';
						oTable.fnOpen(nTr, endpointFormatDetails(oTable,nTr,"${ambit_root}"),	'details');
												       
					}
			});
	downloadForm("${ambit_request}");
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="Admin">Admin</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin/stats" title="Statistics">Statistics</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="${facet_tooltip}">${ambit_request}</a></li>');
});
</script>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="six columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >

<form action='${ambit_root}/substance' method='GET'>
		<input class='half-bottom' type='submit' value='Search'>
		<table id='facet' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
		<thead>	
		<tr>
		<th ></th>
		<th >Endpoint</th>
		</tr>
		</thead>
		<tbody></tbody>
		</table>
</form>			
			
</div>
 		
		<div class="ten columns remove-bottom" style="padding:0;" >

		Show here the substances
		</div> 
		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
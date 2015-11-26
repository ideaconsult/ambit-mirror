<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>
$(document).ready(function() {
	var oTable = defineProtocolApplicationSummaryTable("${ambit_root}","${ambit_request_json}","#facet");

	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="Admin">Admin</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin/stats" title="Statistics">Statistics</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="${facet_tooltip}">${ambit_request}</a></li>');
	jQuery("#breadCrumb").jBreadCrumb();
});
</script>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="one column" style="padding:0 2px 2px 2px 0;margin-right:0;" >&nbsp;
</div>
 		
		<div class="fifteen columns remove-bottom" style="padding:0;" >

		
		<table id='facet' class='jtoxkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
		<thead>	
		<tr>
		<th ></th>
		<th >Type</th>
		<th>Protocol</th>
		<th>Result</th>
		<th >Number of entries</th>

		
		</tr>
		</thead>
		<tbody></tbody>
		</table>
			
		</div> 
		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
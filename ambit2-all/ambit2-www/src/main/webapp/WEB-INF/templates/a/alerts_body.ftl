<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/myprofile.js'></script>
<script type='text/javascript' src='${ambit_root}/jquery/jquery.validate.min.js'></script>

<script type='text/javascript'>
$(document).ready(function() {
	var oTable = defineAlertsTable("${ambit_root}","${ambit_request_json}");
});
</script>


<script type="text/javascript">
       jQuery(document).ready(function()
       {
      		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/myaccount" title="My AMBIT account">My account</a></li>');
           jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/myaccount/alert" title="AMBIT Home">Alerts</a></li>');
           jQuery("#breadCrumb").jBreadCrumb();
           loadHelp("${ambit_root}","alert");
       })
</script>
  
</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
&nbsp;
</div>

		
		<!-- Page Content
		================================================== -->
		<div class="eleven columns ui-widget-content ui-corner-all" style="padding:0;" >
		
		<table id='alerts'  cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
		<thead>
		<tr>
		<th>Created</th>
		<th>Alert</th>
		<th>Recurrence</th>
		<th>Sent at</th>
		<th>Delete</th>
		</tr>
		</thead>
		<tbody></tbody>
		</table>
		
		</div>
		
		<!-- Right column and footer
		================================================== -->

<#include "/chelp.ftl" >

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
  
<script type="text/javascript">
jQuery(document).ready(function()
{
	var oTable = defineRolesTable("${ambit_root}","${ambit_request_json}","#roles");
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="AMBIT admin">Admin</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/user" title="AMBIT admin">Users</a></li>');
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin/roles" title="REST policy">Roles</a></li>');
    jQuery("#breadCrumb").jBreadCrumb();
    loadHelp("${ambit_root}","users");

});
</script>  
</head>
<body>

<div class="container" style="margin:0;padding:0;">

	<!-- banner -->
	<#include "/banner_crumbs.ftl">
	

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	<#include "/searchmenu/menu_admin.ftl">
</div>
	
		<!-- Page Content
		================================================== -->
		<div class="twelve columns ui-widget-content ui-corner-all add-bottom" style="padding:0;" >
		
		<table id='roles' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
		<thead>
		<tr>
		<th>Role</th>	
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
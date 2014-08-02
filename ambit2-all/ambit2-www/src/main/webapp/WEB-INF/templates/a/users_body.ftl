<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<#include "/a/users_head.ftl" >

<#if ambit_admin?? && ambit_admin>

<script type='text/javascript' src='${ambit_root}/jquery/jquery.jeditable.js'></script>
<script type='text/javascript' src='${ambit_root}/jquery/jquery.dataTables.editable.js'></script>

</#if>	

<script type="text/javascript">
jQuery(document).ready(function()
{
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="AMBIT admin">Admin</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/user" title="AMBIT users">Users</a></li>');
    jQuery("#breadCrumb").jBreadCrumb();
    loadHelp("${ambit_root}","users");
    
	var oTable = defineUsersTable("${ambit_root}","${ambit_request_json}","#users");	
	<#if ambit_admin?? && ambit_admin>
		try {
		makeEditableUsersTable("${ambit_root}",oTable);
		} catch (err) {
			console.log(err);
		}
	</#if>
})
</script>

</head>
<body>

<div class="container columns" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	<#include "/searchmenu/menu_admin.ftl">
</div>

		
		<!-- Page Content
		================================================== -->
		<div class="twelve columns ui-widget-content ui-corner-all add-bottom" style="padding:0;" >
		
		<table id='users' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
		<thead>
		<tr>
		<th>Username</th>	
		<th>Name</th>
		<th>e-mail</th>
		<th>Affiliation</th>
		<th>Keywords</th>
		<th>Would like data upload enabled<a href='#' class='chelp hcurator'></a></th>
		<th>Data manager Role</th>
		<th>Admin Role</th>
		<th>Status</th>
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
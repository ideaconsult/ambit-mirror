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
    
	<#if ambit_admin?? && ambit_admin>
		var oTable = defineUsersTable("${ambit_root}","${ambit_request_json}","#users",true);
		try {
			makeEditableUsersTable("${ambit_root}",oTable);
		} catch (err) {
			console.log(err);
		}
		adminPwdForm("#adminPwdForm");
	<#else>
		var oTable = defineUsersTable("${ambit_root}","${ambit_request_json}","#users",false);
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
		
		<br/>
		<#if ambit_admin?? && ambit_admin>		
		<div class='ui-widget-header ui-corner-top pwdchange' style="margin:5px;padding:5px;display:none;"> 
			<span>Password change</span>
			<a href="#" onClick="$('.pwdchange').hide();" style="float:right;"><span class="ui-icon ui-icon-close" style="float: right; margin-right: .3em;"></span></a>
		</div>		
	    <div class='ui-widget-content ui-corner-bottom pwdchange' style="margin:5px;padding:5px;display:none;">					
			<form action="${ambit_root}/admin/user/reset?method=PUT" id="adminPwdForm"  method="POST" autocomplete="off">
			<div class='row remove-bottom' style="margin:5px;padding:5px;"> 	
				<label class='three columns omega' for="username">User name</label>
				<input class="five columns omega" type='text' readonly size='40' id='username' name='username' value='' required/>
				<input class="five columns omega" type='hidden' readonly size='40' id='uri' name='uri' value='' required/>
			</div>	
		
			<div class='row remove-bottom' style="margin:5px;padding:5px;"> 	
				<label class='three columns alpha' for="pwd1">New password</label>
				<input class="five columns omega " type='password' size='40' id='pwd1' name='pwd1' value=''/>
				<div class="eight columns omega">&nbsp;</div>
			</div>		
			<div class='row remove-bottom' style="margin:5px;padding:5px;"> 	
				<label class='three columns alpha' for="pwd2">Confirm new password</label>
				<input class="five columns omega"  type='password' size='40' id='pwd2' name='pwd2' value=''/>
				<div class="eight columns omega">&nbsp;</div>
			</div>		
			<div class='row remove-bottom' style="margin:5px;padding:5px;"> 	
				<label class='three columns alpha' >&nbsp;</label>
				<input class="three columns omega" id='updatepwd' name='updatepwd' type='submit' class='submit' value='Update'>
				<div class="eleven columns omega">&nbsp;</div>
			</div>				
			</form>		
		</div>
		</#if>				
			
		</div> 
		
		<!-- Right column and footer
		================================================== -->
		<#include "/chelp.ftl" >


		
<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
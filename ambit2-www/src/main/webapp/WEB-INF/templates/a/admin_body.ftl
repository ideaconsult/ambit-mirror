<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >


<script type="text/javascript">
jQuery(document).ready(function()
{
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="Admin tasks">Admin</a></li>');
    jQuery("#breadCrumb").jBreadCrumb();
     loadHelp("${ambit_root}","admin");
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
		<div class="eleven columns add-bottom" >

    	<div class="row remove-bottom ui-widget-header ui-corner-top">&nbsp;AMBIT admin tasks</div>
    	<div class="half-bottom ui-widget-content ui-corner-bottom" >
    			
		<div class='row' style="margin:5px;padding:5px;"> 	
		<div class="three columns">Database admin</div>
		<div class="thirteen columns"><a href='${ambit_root}/admin/database'>Create or inspect the database</a><a href='#' class='chelp database'></a></div>
		</div>
		
		<div class='row' style="margin:5px;padding:5px;">
		<div class="three columns">Users</div>
		<div class="thirteen columns"><a href='${ambit_root}/user'>Browse registered users</a><a href='#' class='chelp user'></a></div>
		</div>
		
		<div class='row' style="margin:5px;padding:5px;">
		<div class="three columns">Organisations</div>
		<div class="thirteen columns"><a href='${ambit_root}/organisation'>Browse users affiliations</a><a href='#' class='chelp affiliation'></a></div>
		</div>
		
		<div class='row' style="margin:5px;padding:5px;">
		<div class="three columns">System jobs</div>
		<div class="thirteen columns"><a href='${ambit_root}/task'>Browse jobs status</a><a href='#' class='chelp task'></a></div>
		</div>		
		
		<div class='row' style="margin:5px;padding:5px;">
		<div class="three columns">Saved searches</div>
		<div class="thirteen columns">
		<a href='${ambit_root}/admin/notification'>Active notifications</a><a href='#' class='chelp notification'></a><br>
		Browse users:&nbsp;
		<a href='${ambit_root}/admin/notification?search=daily'>with defined daily alerts</a><a href='#' class='chelp daily'></a> |
		<a href='${ambit_root}/admin/notification?search=weekly'>with defined weekly alerts</a><a href='#' class='chelp weekly'></a> |
		<a href='${ambit_root}/admin/notification?search=monthly'>with defined monthly alerts</a><a href='#' class='chelp monthly'></a> 
		</div>
		</div>		
				
		</div>
		<!-- twelve columns  -->
		</div> 
		
		<!-- Right column and footer
		================================================== -->

<#include "/chelp.ftl" >

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
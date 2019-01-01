<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >


<script type="text/javascript">
jQuery(document).ready(function()
{
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="AMBIT admin">Admin</a></li>');
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin/database" title="AMBIT Database">Database</a></li>');
    jQuery("#breadCrumb").jBreadCrumb();
    loadHelp("${ambit_root}","admin_db");
    loadDBInfo("${ambit_root}");
});
</script>  
</head>
<body>

<div class="container" style="margin:0;padding:0;">

	<!-- banner -->
	<#include "/banner_crumbs.ftl">
	
	<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	<#include "/menu.ftl">
	</div>
		
		<!-- Page Content
		================================================== -->
		<div class="eleven columns add-bottom" style="padding:0;" >
		
	    <div class="row remove-bottom ui-widget-header ui-corner-top">Database info</div>
    	<div class="half-bottom ui-widget-content ui-corner-bottom">
		<div style='margin:5px;padding:5px;'>
		
		<div class="row add-bottom" >		
			<label class='three columns alpha'>Current database</label>	
			<div class='ten columns alpha' id="dbinfo">&nbsp;</div>
			<div class='three columns alpha'><a href='#' class='chelp database'></a></div>
		</div>
				
		<div class="row add-bottom"  id="dbcreate" style="display:none;">		
			<label class='three columns alpha'>How to create the database:</label>	
			<div class='ten columns alpha'>
	
	    Use the following <a href="http://curl.haxx.se/">cURL</a> command 
	<pre>
		curl -X POST \ 
		-d "dbname=AMBIT" \ 
		-d "user=mysqladminuser" \ 
		-d "pass=mysqladminpass" \
        ${ambit_root}/admin/database 
	</pre>
	 Database tables will be created, only if the database exists,
    is empty and the database name is the same as configured server side.
    
			</div>
			<div class='three columns alpha'><a href='#' class='chelp create'></a></div>
		</div>
						
		</div>
		</div>				
		</div>
		<!-- Right column and footer
		================================================== -->

<#include "/chelp.ftl" >

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/toxauth.js'></script>

 <script language="JavaScript">
  function errorHandler(service, status, jhr) {
    var message = '(' + jhr.status + ') ' + jhr.statusText;
	console.log(jhr);
    try {
      var obj = JSON.parse(jhr.responseText);
      if (obj != null) {
        if (!!obj.error)
          message = obj.error;
        else if (obj.task != null && obj.task.length > 0 && !!obj.task[0].error)
          message = obj.task[0].error;
      }
    }
    catch (e) { ; }
      
    console.log("Auth error on [" + service + "]: " + message);
  }
  </script>
  
<script type="text/javascript">
jQuery(document).ready(function()
{
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="AMBIT admin">Admin</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/user" title="AMBIT admin">Users</a></li>');
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin/restpolicy" title="REST policy">Access rights</a></li>');
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
		<div class="twelve columns add-bottom" style="padding:0;" >

	<div class="jtox-toolkit" data-kit="policy" data-base-url="${ambit_root}" data-load-on-init="true" data-on-error="errorHandler"></div>
  <div class="jtox-template">
<!--[[ jT.templates['all-model'] -->
	  <div id="jtox-policy"  class="jtox-auth">
      <table></table>
	  </div>
<!-- // end of #jtox-model ]]-->
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
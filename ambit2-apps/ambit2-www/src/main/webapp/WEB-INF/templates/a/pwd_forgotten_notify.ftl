<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type="text/javascript">
jQuery(document).ready(function()
{
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/forgotten" title="AMBIT password reset">Password reset</a></li>');
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/forgotten/notify" title="AMBIT password reset">Notification</a></li>');
    jQuery("#breadCrumb").jBreadCrumb();
     loadHelp("${ambit_root}","pwd_forgotten");
})
</script>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">

	<!-- banner -->
	<#include "/banner_crumbs.ftl">
	
	<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	&nbsp;
	</div>
		
		<!-- Page Content
		================================================== -->
		<div class="eleven columns add-bottom" style="padding:0;" >	
		
		<div class='ui-widget-header ui-corner-top'>&nbsp;AMBIT password reset</div>
	    <div class='ui-widget-content ui-corner-bottom'>	
	    	<div class='row'></div>
			<div  class='row' style="text-align:center;">
			<span class="ui-icon ui-icon-info" style="display:inline-block"></span>
			Please follow the instructions in the password reset mail.
			</div>	
			<div class='row'></div>
		</div>
	

		<!-- twelve -->
		</div>
		
		
		<!-- Right column and footer
		================================================== -->
<#include "/chelp.ftl" >

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
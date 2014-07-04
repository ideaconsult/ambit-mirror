<#include "/html.ftl">
<head>
  <#include "/header_updated.ftl">
  
<script type='text/javascript'>
$(document).ready(function() {
	$.ajaxSetup({cache:false});//while dev
	
	<#if ambit_reg_confirmed??>
	    $.ajax({
	        dataType: "json",
	        async: false,
	        url: "${ambit_root}/register/confirm?code=${ambit_reg_confirmed}&media=application%2Fjson",
	        success: function(data, status, xhr) {
	           	$.each(data["confirmation"],function(index, entry) {
	        		if ('confirmed' == entry["status"]) { 
	        			$("#failure").hide();
	        			$("#success").show();
	        		} else {
	        			$("#failure").hide();
	        			$("#success").show();	        			
	        		}
	        	});
	        },
	        statusCode: {
	            404: function() {
	        		$("#failure").show();
	        		$("#success").hide();
	            }
	        },
	        error: function(xhr, status, err) {
	       		$("#failure").show();
        		$("#success").hide();
        		$("#progress").hide();
	        },
	        complete: function(xhr, status) {
	        	$("#progress").hide();
	        }
	     });		
	</#if>
});
</script>
<script type="text/javascript">
jQuery(document).ready(function()
{
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/login" title="AMBIT log in">Log in</a></li>');
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/register" title="AMBIT registration">Register</a></li>');
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/register/confirm" title="AMBIT confirm registration">Confirm</a></li>');
    jQuery("#breadCrumb").jBreadCrumb();
    loadHelp("${ambit_root}","register");
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
		<div class="eleven columns add-bottom" style="padding:0;" >	

		<div class='ui-widget-header ui-corner-top' >&nbsp;AMBIT Registration</div>
	    <div class='ui-widget-content ui-corner-bottom'>	
	    	<div class='row'></div>
			<#if ambit_reg_confirmed??>
			<div class='row' id='success' style='display:none;text-align:center;'>
			&nbsp;<span class="ui-icon ui-icon-info" style="display:inline-block"></span>
			Your registration is now confirmed and you can <a href="${ambit_root}/login">log in</a> into AMBIT.
			</div>

			<span id='failure' class='row' style='display:none'>
				<strong>Invalid confirmation code!</strong>
			</span>
			
			<img id='progress' src="${ambit_root}/images/progress.gif" alt="Please wait..." title="Please wait...">
			
			<#else>
			<div  class='row' style="text-align:center;">
			<span class="ui-icon ui-icon-info" style="display:inline-block"></span>
			Please follow the instructions in the confirmation mail, in order to complete the registration procedure.
			</div>
			</#if>
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
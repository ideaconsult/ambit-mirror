<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<#if status_code == 403>
	<#assign status_error_description="You are not allowed to view this page">
<#elseif status_code == 401>
	<#assign status_error_description="You are not allowed to view this page">
</#if>
				
<script type='text/javascript' src="${ambit_root}/scripts/jopentox.js"></script>
<script type='text/javascript'>

$(document)
		.ready(function() {
			jQuery("#breadCrumb ul").append('<li>Error</li>');
			jQuery("#breadCrumb").jBreadCrumb();
			jQuery("#welcome").html("");	
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">


<#include "/banner_crumbs.ftl">

<div class="two columns remove-bottom" style="padding:0;" >&nbsp;
</div>

<div class="ten columns remove-bottom" style="padding:0;" >
<#escape x as x?html>
		<div class="ui-widget-header ui-corner-top">
		<span class='ui-icon ui-icon-alert' style='float: left; margin-right: .3em;' title='' id='status_name'>${status_name}</span>
		<span id='error_name'>${status_error_name}</span>
		</div>
		
		<div class='ui-widget-content ui-corner-bottom '>
			<div class='row' style='text-align:center;margin:10px;'>
				<span id='error_description'>${status_error_description}</span>
			</div>
			<div class='row help' style='text-align:right;margin:10px;'>
					<a href='#' style='background-color: #fff; padding: 5px 10px;' onClick="$('#details').toggle(); return false;">Details</a>
			</div>
			<div class='row help' style='display: none;text-align:right;margin:10px;' id='details'>
				<#if status_details??>
					<span id='details_description'>[${status_code}]&nbsp;<a href="${status_uri}" target=_blank>${status_details}</a></span>
					<#else>
					<span id='details_description'>[${status_code}]&nbsp;<a href="${status_uri}" target=_blank>${status_name}</a></span>
				</#if>

			</div>
		</div>
</#escape>
		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<div class="one column">&nbsp;</div>
<#include "/chelp.ftl" >


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

<#include "/html.ftl" >
<head>
<#include "/header.ftl" >
<script type='text/javascript' src="${ambit_root}/scripts/jopentox.js"></script>
<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#selectable" ).selectable( "option", "distance", 18);
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="seven columns alpha">
			<div class="remove-bottom h3">
					Error
			</div>
		    <div class='h6'>An error has occured while accessing the web page requested</div>			
		</div>
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">
</div>

<div class="eleven columns remove-bottom" style="padding:0;" >
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
<div class="two columns" style="margin:0;padding:0;" >
<#include "/help.ftl" >
</div>



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

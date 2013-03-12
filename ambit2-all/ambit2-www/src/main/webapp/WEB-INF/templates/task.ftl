<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>

<#if taskid??>
	<script type='text/javascript'>
		var task = readTask("${ambit_root}","${ambit_request_json}");
	</script>
<#else>
	<script type='text/javascript'>
	
	$(document).ready(function() {
	  	var oTable = defineTaskTable("${ambit_root}","${ambit_request_json}");
	});
	</script>
</#if>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#selectable" ).selectable( "option", "distance", 18);
					loadHelp("${ambit_root}","task");
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
			<h3 class="remove-bottom">
					Tasks status
			</h3>
		    <h6>Long running computing or data import procedures</h6>			
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


		<!-- Page Content
		================================================== -->
		<#if taskid??>
		<div class="row" style="padding:0;" >			
			<div class="ui-widget-header ui-corner-top"><a href='${ambit_root}/task/${taskid}'>Job</a> started <span id=task_started></span> &nbsp;</div>
			<div class="ui-widget-content ui-corner-bottom">
			<span id=task_status>
			<script>
				checkTask('${ambit_root}/task/${taskid}','result', 'status', '${ambit_root}/images/tick.png', '${ambit_root}/images/cross.png');
			</script>
			</span>
			<p>Name:&nbsp;<strong id=task_name></strong></p>
			<p>Status:&nbsp;<a href='#' id='result'></a>&nbsp;<img src='${ambit_root}/images/24x24_ambit.gif' id='status'>
			<span id=task_errorreport></span>
			</p>
			</div>
		<#else>
		<div class="row " style="padding:0;" >
			<table id='task'  cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<th>Job status</th>
			<th>Description</th>
			<th>Started at</th>
			<th>Completed at</th>
			</thead>
			<tbody></tbody>
			</table>
		
		</#if>
		
		</div>
		


<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>
<#include "/chelp.ftl" >


</form>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

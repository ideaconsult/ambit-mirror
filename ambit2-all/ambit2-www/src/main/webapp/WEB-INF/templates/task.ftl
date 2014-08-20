<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl">

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

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
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/task" title="Long running computing or data import procedures">Task status</a></li>');
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="${ambit_request}">${ambit_request}</a></li>');
					jQuery("#breadCrumb").jBreadCrumb();
					jQuery("#welcome").html("Long running computing or data import procedures");		
					loadHelp("${ambit_root}","task");
					downloadForm("${ambit_request}");
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">


<#include "/banner_crumbs.ftl">

<div class="one column remove-bottom" style="padding:0;" >&nbsp;
</div>
	
<div class="twelve columns remove-bottom" style="padding:0;" >


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
					<br/>
					<span id="task_errorreport"></span>
					<br/>
					<#if username??>
						<#if openam_token??>
							<textarea id="task_json"  cols='40' style="display:none;height: auto;"></textarea>
						<#else>
							<#if ambit_admin?? && ambit_admin>
								<textarea id="task_json" cols='40' style="display:none;height: auto;"></textarea>
							</#if>		
						</#if>
					</#if>	
					</p>
				</div>
			</div>
		<#else>
	 		<div class="row remove-bottom ui-widget-header ui-corner-top">
	 		&nbsp;
	 		</div>		
			<div class="row " style="padding:0;" >
				<table id='task' class='ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
				<thead>
				<tr>
				<th>Job status</th>
				<th>Description</th>
				<th>Started at</th>
				<th>Completed at</th>
				</tr>
				</thead>
				<tbody></tbody>
				</table>
			</div>
		</#if>
		
		
</div>		

<div class="two columns remove-bottom" style="padding:0;" >

	<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
	<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
	<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
	</div>

<!-- help-->	
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
	<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>
	
</div>

<div class='row add-bottom' style="height:140px;">&nbsp;</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

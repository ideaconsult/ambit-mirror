<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl">

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>


<script type='text/javascript'>

$(document)
		.ready(
				function() {
				
					var purl = $.url();
					var taskuri = purl.param('task_uri');
					
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/task" title="Long running computing or data import procedures">Task status</a></li>');
					if (taskuri===undefined || taskuri==null) {
					} else {
						jQuery("#breadCrumb ul").append('<li><a href="' + taskuri + '" >' + taskuri + '</a></li>');
						downloadForm(taskuri);
						var task = readTask("${ambit_root}",taskuri+"?media=application/json");
						$("#taskref").attr("href",taskuri);
						
					}
					jQuery("#breadCrumb").jBreadCrumb();
					loadHelp("${ambit_root}","task");
					
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">


<#include "/banner_crumbs.ftl">

<div class="one column remove-bottom" style="padding:0;" >&nbsp;
</div>
	
<div class="thirteen columns remove-bottom" style="padding:0;" >


		<!-- Page Content
		================================================== -->
		<div class="row" style="padding:0;" >			
			<div class="ui-widget-header ui-corner-top"><a href='#' id='taskref'>Job</a> started <span id=task_started></span> &nbsp;</div>
			<div class="ui-widget-content ui-corner-bottom">
			<span id=task_status>
			</span>
			<p>Name:&nbsp;<strong id=task_name></strong></p>
			<p>Status:&nbsp;<a href='#' id='result'></a>&nbsp;<img src='${ambit_root}/images/24x24_ambit.gif' id='status'>
			<span id="task_errorreport"></span>
			<#if username??>
				<#if openam_token??>
					<textarea id="task_json"  cols='40' style="display:none;height: auto;"></textarea>
				<#else>
					<#if ambit_admin?? && ambit_admin>
						<textarea id="task_json"  cols='40' style="display:none;height: auto;"></textarea>
					</#if>		
				</#if>
			</#if>			
			
			</p>
			</div>
		
		
		</div>
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

<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					loadHelp("${ambit_root}","model");
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/model" title="Models">Models</a></li>');
				});
</script>
</head>
<body>


<div class="container" style="margin:0;padding:0;">

<#include "/banner_crumbs.ftl">
	
<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/searchmenu/model_menu.ftl">

</div>

<div class="eleven columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;How to run predictive models</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">	
	 	<h5>Structures</h5>
		Please use the <a href='${ambit_root}/ui/query'>Structure search</a> page to retrieve chemical compounds.
		<br/>
		<h5>Predictions</h5>
		At the <a href='${ambit_root}/ui/query'>Structure search</a> page, please use the <img src='${ambit_root}/images/run.png'> button 
		at the prediction menu to launch calculations.
		<br/>
		<br/>
		<span class='help'>Example:
		<ul id="selectable" style="width:200px" title='This is an example. Please go to the Structure search page.'>
		<li class="ui-selectee">
		<span class="ui-icon ui-icon-folder-open" style="float: left; margin-right: .3em;" id='ctrl_model'></span>
		Predictions
		</li>
		<li class="ui-selectee">
		<img src='${ambit_root}/images/run.png'>Molecular weight
		</li>
		</ul>
		</span>
		<br/>
		<br/>
		<h5>Predictive models</h5>
		All available models are listed at the <a href='${ambit_root}/model'>Models</a> page.
		In general, models are built using the available algorithms at the <a href='${ambit_root}/algorithm'>Algorithm</a> page.
		Most algorithms require a training dataset, but some are predefined set of rules as  
		<a href='${ambit_root}/algorithm?search=ToxTree'>Toxtree</a> and do not need a training set.
		Just use the <a href='${ambit_root}/algorithm/toxtreecramer'>Toxtree: Cramer rules</a> <b>Run</b> button in order to obtain a model.
		It will then appear in the <a href='${ambit_root}/ui/query'>Structure search</a> page.
		<br/>
		<br/>
		<h5>Troubleshooting</h5>
		After the initial launch of the system the list of <a href='${ambit_root}/model'>models</a> is empty. Click
		<a href="#" onClick="runAlgorithms('${ambit_root}','#status');">here</a> to initialize AMBIT with ToxTree prediction modules.
		<span class='help' id='status'></span>
		
	</div>			
</div>
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>
<div class="two columns" style="margin:0;padding:0;" >
<#include "/help.ftl" >
</div>



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>

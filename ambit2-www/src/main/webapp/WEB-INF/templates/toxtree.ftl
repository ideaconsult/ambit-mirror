<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<link rel="stylesheet" href="${ambit_root}/style/toxtree.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/toxtree/common.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/toxtree/dataset.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/toxtree/toxtree.js'></script>

<script>
	$(document).ready(function() {
		try {
			var url = parseURL(document.location);
			$("#query-needle").attr('value',url.params.search);
		} catch (err) { $("#query-needle").attr('value','');}
		
		initToxtree("${ambit_root}");
		jQuery("#breadCrumb ul").append('<li><a href="#" title="Demo">Demo</a></li>');
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/toxtree" title="Toxtree - Toxic Hazard Estimation by decision tree approach">Toxtree - Toxic Hazard Estimation by decision tree approach</a></li>');
		loadHelp("${ambit_root}","toxtree");

	});

</script>
</head>
<body>
	<div class="container" style="margin:0;padding:0;">

	<!-- banner -->
	<#include "/banner_crumbs.ftl">
	<div class="row remove-bottom">
		<div class="column"><img title='Toxtree logo' src="http://toxtree.sourceforge.net/images/toxtree-logo.gif"></div>
		<div class="eleven columns">
			<input id="query-needle" placeholder="Identifier (CAS, Name, EINECS) or SMILES or InChI" title="Enter any identifier (CAS, Name, EINECS) or SMILES or InChI. Toxtree will guess the input type automatically."/>
		</div>
		<div class="two columns">
			<button id="query-button">QUERY</button>
		</div>
		<div class="two columns">&nbsp</div>
	</div>	
	<div class="row">
		<div class="column">&nbsp</div>
		<div class="five columns">
			<h5>Available structure attributes</h5>
			<div class="toxtree-features panel">
				<table>
					<tbody class="body"></tbody>
					<tfoot class="template">
						<tr class="row-blank">
							<td class="two columns nomargin data-field" data-field="name"> _key_ </td>
							<td class="separator"></td>
							<td class="three columns nomargin data-field" data-field="value"> _value_ </td>
						</tr>
						<tr class="header-blank">
							<td colspan="3"><strong class="data-field" data-field="header"> _header_ </strong></td>
						</tr>
					</tfoot>
				</table>
			</div>
			<h5>Structure diagram</h5>
			<div class="panel"><img class="toxtree-diagram" src="${ambit_root}/images/empty.png" alt="Compoung diagram"/></div>
		</div>
		<div class="eight columns">
			<h5>Toxicity prediction modules</h5>
			<div class="toxtree-models">
				<ul class="body">
					<li class="row-blank template panel-hovering">
						<div class="inlined show-hide">
							<h5 class="data-field" data-field="name"> _name_ </h5>
						</div>
						<div class="inlined"><input class="auto" type="checkbox"/>Auto</div>
						<div class="inlined"><button class="imaged run"><img src="${ambit_root}/images/toxtree/run.png" alt="Run prediction"/></button></div>
						<div class="inlined class-result"><div class="class-blank template"><span class="data-field" data-field="name"></span><span class="data-field go-right" data-field="answer"></span></div></div>
						<div class="info hidden">
							<div class="explanation"> _explanation_ </div>
						</div>
					</li>
				</ul>
			</div>
		</div>
		<#include "/chelp.ftl">
	</div>	
		<#include "/footer.ftl" >
	</div>
	<div class="status-bar">
		<h6><span id="connection-baseuri"></span> : <img src="${ambit_root}/images/ok.png" id="connection-status" alt="Ok"/> <span id="connection-error"></span></h6>
	</div>
</body>
</html>

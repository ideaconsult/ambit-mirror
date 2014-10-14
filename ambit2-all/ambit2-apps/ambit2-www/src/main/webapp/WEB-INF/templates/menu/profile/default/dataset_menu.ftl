<ul>
<li ><a href="${ambit_root}/dataset?page=0&pagesize=100" title="Datasets: Chemical structures and properties">All datasets</a></li>
<li ><a href="${ambit_root}/query/ndatasets_endpoint?feature_sameas=http%3A%2F%2Fwww.opentox.org%2FechaEndpoints.owl&condition=startswith" title="Datasets by endpoints">Datasets by endpoints</a></li>
<li ><a href="${ambit_root}/ui/uploadstruc" title="Upload a dataset of chemical structures and properties. Supported formats are SDF, MOL, SMI, CSV, TXT, XLS, ToxML (.xml)">Import dataset</a>
	<ul>
		<li ><a href="${ambit_root}/ui/uploadstruc" title="Upload a dataset of chemical structures and properties. Supported formats are SDF, MOL, SMI, CSV, TXT, XLS, ToxML (.xml)">Import a dataset</a></li>
		<li ><a href="${ambit_root}/ui/uploadprops" title="Import properties for compounds already in the database">Import properties</a></li>
		<li ><a href="${ambit_root}/ui/createstruc" title="Import properties for compounds already in the database">Add structure</a></li>
	</ul>
</li>
<li>
	<a href="#">Dataset search</a>
	<ul class="mega-menu">
		<li>
		<div style="width:400px;max-width:100%;">
		<form method='GET' name='searchform' id='searchform' action='${ambit_root}/ui/_dataset' style='padding:0;margin:0;'>
		<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
			Enter dataset URI
		</div>
		<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
		<input type='text' class='dataseturi remove-bottom' id='dataseturi' name='dataset_uri' value='' tabindex='1' title='Type first letters of a dataset name in the box to get a list of datasets.'>
		</div>
		<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
		<input class='ambit_search ' id='submit' type='submit' value='Browse' tabindex='2'>
		</div>
		</form>
		</div>
		</li>
	</ul>
</li>
</ul>
<#if custom_structurequery??>
	<#assign custom_structurequery=custom_structurequery?url('UTF-8')>
</#if>

<ul id='smartmenu' class="sm sm-mint">
<li>
	<a href="${ambit_root}/">Home</a>
</li>	
<li>
	<a href="#" title="Search nanomaterials by chemical structure of components">Search</a>
	<ul>
		<li ><a href="${ambit_root}/substance?type=name&search=Silica&page=0&pagesize=20" title="Search for nanomaterials by identifiers or reference">Search nanomaterials by name</a></li>
		<li ><a href="${ambit_root}/substance?type=&search=NM-111&page=0&pagesize=20" title="Search for nanomaterials by identifiers">Search nanomaterials by identifier</a></li>
		<li ><a href="${ambit_root}/substance?type=citation&search=10.1073&page=0&pagesize=20" title="Search for nanomaterials by paper reference">Search nanomaterials by citation</a></li>
		<li><a href="${ambit_root}/query/study" title="Search substances by physico-chemical parameters or biological effects">Search nanomaterials by physchem parameters or biological effects</a></li>
		<li><a href="${ambit_root}/ui/_search?search=${custom_structurequery!'SiO2'}" title="Search nanomaterials by chemical structure of components">Search nanomaterials by composition</a></li>
		<!--
		<li><a href="${ambit_root}/ontobucket?search=cytotoxicity&type=protocol&qe=true" title="Free text search (experimental)">Free text search</a></li>
		-->
		<li><a href="https://search.data.enanomapper.net/" title="Free text search"><br/>Free text search</a></li>
	</ul>
</li>
<li>
	<a href="${ambit_root}/substance?page=0&amp;pagesize=100">Data collections</a>
	<#include "/menu/profile/enanomapper/substance_menu.ftl">
</li>
<li>
	<a href="${ambit_root}/substance?page=0&amp;pagesize=100">Data upload</a>
	<ul>
			<li ><a href="${ambit_root}/ui/uploadenm" title='Upload eNanoMapper files'>eNanoMapper Turtle upload</a>
			<li ><a href="${ambit_root}/ui/uploadsubstance1" title='Excel template with JSON configuration upload'>Spreadsheet upload</a>
			<li ><a href="${ambit_root}/ui/uploadsubstance" title='Upload of several *.i5z , *.rdf or *.csv file'>Multiple files upload</a>
			<li ><a href="${ambit_root}/ui/updatesubstancei5">Retrieve substance(s) from IUCLID5 server</a>
			
		</ul>
</li>
<li>
	<a href="#" title="For developers">For developers</a>
    <ul>
	<li><a href='http://enanomapper.github.io/API/' target=_blank title="API documentation in Swagger JSON format">API Documentation (eNanoMapper services)</a></li>
	<li><a href="#">Resources</a>		
	<ul>
	<li>
		<a href="${ambit_root}/dataset?page=0&pagesize=100" title="Datasets: Chemical structures and properties">Datasets</a>
		<#include "/menu/profile/enanomapper/dataset_menu.ftl">
	</li>
	<li>
		<a href="${ambit_root}/algorithm" title="Descriptor calculations, model building and data processing algorithms">Algorithms</a>
		<#include "/menu/profile/enanomapper/algorithm_menu.ftl">
	</li>
	<li>
		<a href="${ambit_root}/model" title="Regression, classification, clustering, structural alerts, applicability domain, structure optimisation.">Models</a>
		<#include "/menu/profile/enanomapper/model_menu.ftl">
	</li>	
	</ul>
	<li><a href="#">Overview</a>		
	<ul>
	<li>
		<a href="${ambit_root}/query/experiment_endpoints" title="Endpoints overview">Endpoints</a>
	</li>
	<li>
		<a href="${ambit_root}/query/study" title="Study overview">Studies</a>
	</li>
	<li>
		<a href="${ambit_root}/query/data_availability" title="Data availability">Data availability</a>
	</li>			
	</ul>
	</ul>
</li>
<li>
	<a href="${ambit_root}/help">Help</a>
	<#include "/menu/profile/enanomapper/help_menu.ftl">
</li>				
</ul>


<ul id="menu_substance">

	<li ><a href="${ambit_root}/substance?page=0&pagesize=10" title="Mono-constituent, multiconstituent, additives, impurities.">All substances</a></li>
	<li ><a href="${ambit_root}/substance?type=CompTox&search=Ambit+Transfer" title='Substances with external identifier set to "other:CompTox=Ambit Transfer"'>Substances with CompTox label</a></li>
	<#if (ambit_datasetmgr?? && ambit_datasetmgr) || (ambit_admin?? && ambit_admin)>
		<li ><a href="${ambit_root}/ui/uploadsubstance">Import substance</a>
			<ul>
				<li ><a href="${ambit_root}/ui/uploadsubstance">Multiple files upload</a>
				<li ><a href="${ambit_root}/ui/uploadsubstance1">Single file upload</a>
				<li ><a href="${ambit_root}/ui/updatesubstancei5">Retrieve substance(s) from IUCLID5 server</a>
			</ul>
		</li>
	</#if>	
	<li ><a href="${ambit_root}/substanceowner">Substance owners</a></li>
		
	<li ><a href="${ambit_root}/bundle?page=0&pagesize=10" title="Datasets of substances.">Datasets of substances and studies</a></li>
	
</ul>

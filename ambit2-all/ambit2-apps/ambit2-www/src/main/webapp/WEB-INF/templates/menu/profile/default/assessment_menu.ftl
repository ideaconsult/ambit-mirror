<ul>
	<li><a href="${ambit_root}/bundle" title="Browse/Edit substance and study datasets">All datasets</a></li>
	
<#if username??>
	<li><a href="#" title="Compile a new dataset of substances and studies">Compile a new dataset</a>
		<ul>
		<li><a href="${ambit_root}/ui/assessment" title="Empty dataset (add substances and endpoints later)">Create an empty dataset</a></li>
		<li><a href="${ambit_root}/ui/assessment_copy" title="Browse/Edit and select a dataset to copy">Use of existing dataset</a></li>
		</ul>
	</li>
	<li><a href="${ambit_root}/myaccount/bundle" title="Browse/Edit datasets">Own datasets</a></li>	
<#else>
</#if>	

</ul>

<ul>
<#if username??>
	<li><a href="#" title="Create new read across or category formation workflow">New assessment</a>
		<ul>
		<li><a href="${ambit_root}/ui/assessment" title="Create new read across or category formation workflow">Use of empty template</a></li>
		<li><a href="${ambit_root}/ui/assessment_copy" title="Browse/Edit read across or category formation workflow">Use of existing assessment</a></li>
		</ul>
	</li>
	<li><a href="${ambit_root}/myaccount/bundle" title="Browse/Edit read across or category formation workflow">Own assessments</a></li>	
<#else>
</#if>	
	<li><a href="${ambit_root}/bundle" title="Browse/Edit read across or category formation workflow">All assessments</a></li>
</ul>

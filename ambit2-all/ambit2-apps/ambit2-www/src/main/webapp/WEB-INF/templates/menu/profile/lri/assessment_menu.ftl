<ul>
<#if username??>
	<li><a href="#" title="Create new read across or category formation workflow">New assessment</a>
		<ul>
		<li><a href="${ambit_root}/ui/assessment" title="Create new read across or category formation workflow">Use of empty template</a></li>
		<li><a href="${ambit_root}/ui/assessment_copy" title="Browse/Edit read across or category formation workflow">Use of existing assessment</a></li>
		</ul>
	</li>

<#else>
</#if>	
<li><a href="${ambit_root}/bundle" title="Browse/Edit read across or category formation assessments">Existing assessments</a>
<ul>
<#if username??>
	<li><a href="${ambit_root}/myaccount/bundle" title="browse/edit assessments created by ${username}">Own assessments</a></li>
<#else>
</#if>
	<li><a href="${ambit_root}/bundle" title="Browse finalized assessments">All assessments</a>
	<ul>
		<li><a href="${ambit_root}/bundle" title="Browse finalized assessments">Finalized assessments</a></li>
		<li><a href="${ambit_root}/dir/bd" title="Browse draft assessments">Draft assessments</a></li>
	</ul>
	</li>
</ul>	
</li>
</ul>



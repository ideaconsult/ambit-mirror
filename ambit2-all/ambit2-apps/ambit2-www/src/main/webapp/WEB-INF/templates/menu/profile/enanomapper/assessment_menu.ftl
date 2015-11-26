<ul>
<#if username??>
	<li><a href="#" title="Create new bundle">New bundle</a>
		<ul>
		<li><a href="${ambit_root}/ui/assessment" title="Create new bundle">Use of empty template</a></li>
		<li><a href="${ambit_root}/ui/assessment_copy" title="Browse/Edit bundles">Use of existing bundles</a></li>
		</ul>
	</li>

<#else>
</#if>	
<li><a href="#" >Existing bundles</a>
<ul>
<#if username??>
	<li><a href="${ambit_root}/myaccount/bundle" title="Browse/Edit">Own bundles</a></li>
<#else>
</#if>
	<li><a href="${ambit_root}/bundle" title="Browse/Edit">All bundles</a></li>
</ul>	
</li>
</ul>



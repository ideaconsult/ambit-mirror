
<ul id="selectable">
<li class="ui-selectee">
<a href="${ambit_root}/ui"><span class="ui-icon ui-icon-home" style="float: left; margin-right: .3em;"></span>Home</a>
</li>

<li class="ui-selectee">
<a href="${ambit_root}/ui/query"><span class="ui-icon ui-icon-search" style="float: left; margin-right: .3em;"></span>Structure search</a>
</li>

<li class="ui-selectee">
	<a href="${ambit_root}/dataset?page=0&pagesize=100">
	<span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>All datasets</a>
</li> 

<li class="ui-selectee"><span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>
<a href="${ambit_root}/ui/createstruc">Add new structure</a>
</li>

<li class="ui-selectee"><span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>
<a href="${ambit_root}/ui/uploadstruc">Import a new dataset</a>
</li>

<li class="ui-selectee"><span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>
<a href="${ambit_root}/ui/uploadprops">Import properties</a>
</li>

<li class="ui-selectee">
<a href="${ambit_root}/ui/predict"><span class="ui-icon ui-icon-calculator" style="float: left; margin-right: .3em;"></span>Predict</a>
</li>

<li class="ui-selectee">
<a href="${ambit_root}/algorithm"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>Algorithms</a>
</li>

<li class="ui-selectee">
<a href="${ambit_root}/model"><span class="ui-icon ui-icon-calculator" style="float: left; margin-right: .3em;"></span>Models</a>
</li>

<#if openam_token??>

<#if username??>
<li class="ui-selectee">
<a href="${ambit_root}/bookmark/${username}"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>My workspace</a>
</li>
</#if>
<li class="ui-selectee">
<a href="${ambit_root}/admin"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>Admin</a>
</li>
<li class="ui-selectee">
<a href="${ambit_root}/admin/policy"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>View/Define access rights</a>
</li>
</#if>

</ul>
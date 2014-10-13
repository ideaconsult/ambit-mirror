<form method='post' action='?method=put'>
<div class="ui-widget-content ui-corner-bottom" >
<div style="margin:5px;padding:5px;">
	<div class="row half-bottom">
		<label class="three columns alpha">Dataset name</label>
		<input class="ten columns omega" type='text' size='60' name='title' id='title' value='' >
		<a href='#' class='chelp derek' id='derekhelp' style='display:none;'>?</a>
	</div>
	
	<div class="row half-bottom">
		<label class="three columns alpha camelCase" id='license_type'>License/Rights</label>
		 
		<select class="five columns omega" name='licenseOptions'>
<option value='Unknown' selected='selected'>License not defined</option>
<option value='http://www.opendatacommons.org/licenses/pddl/' >Public Domain Dedication and License (PDDL) - 'Public Domain for data/databases'</option>
<option value='http://www.opendatacommons.org/licenses/by/' >Open Data Commons Attribution (ODC-By) - 'Attribution for data/databases'</option>
<option value='http://www.opendatacommons.org/licenses/odbl/' >Open Database License (ODC-ODbL) - 'Attribution Share-Alike for data/databases'</option>
<option value='http://creativecommons.org/publicdomain/zero/1.0/' >CC0 1.0 Universal - 'Creative Commons public domain waiver'</option>
<option value='http://creativecommons.org/licenses/by-sa/3.0/' >Creative Commons Attribution-ShareAlike (CC-BY-SA)</option>
<option value='http://www.gnu.org/copyleft/fdl.html' >GNU Free Documentation License (GFDL)</option>
<option value='http://opendatacommons.org/licenses/odbl/1.0/' >Open Database License v1.0</option>
<option value='http://opendatacommons.org/licenses/by/1.0/' >http://opendatacommons.org/licenses/by/1.0/</option>
<option value='Other' >Other</option>
		</select>		

		<input class="five columns omega" type='text' size='60' name='license' id='license'  title='Enter license URI if not within the list' value='' >		
	</div>
	
	<div class="row half-bottom">
		<label class="three columns alpha">Rights holder</label>
		<input  class="ten columns omega" type='text' size='60' title='Rights holder (URI)' name='rightsHolder' id='rightsHolder' value=''  >
		<div class="three columns omega">
			<input align='bottom' type="submit" value="Update">
		</div>
	</div>	

	
	<hr class="row add-bottom">
	</hr>
	
	<div class="row half-bottom">
		<label class="three columns alpha">Source</label>
		<div class="ten columns omega" id="source"></div>
	</div>
	
	<div class="row half-bottom">
		<label class="three columns alpha">See also</label>
		<div class="ten columns omega" id="seealso"></div>
	</div>

	
	<div class="row half-bottom">
		<label class="three columns alpha">Dataset URI</label>
		<div class="ten columns omega"><a href='${ambit_root}/ui/_dataset?dataset_uri=${ambit_root}/dataset/${datasetid}' target='_blank'>Browse</a></div>

	</div>	
	
		
	<div class="row half-bottom">
		<label class="three columns alpha">Search within the dataset</label>
		<div class="ten columns omega">
	<a href='${ambit_root}/dataset/${datasetid}/similarity?search=c1ccccc1' target='_blank'>Search for similar compounds within this dataset</a>&nbsp;|
	<a href='${ambit_root}/dataset/${datasetid}/smarts?search=c1ccccc1' target='_blank'>Search compounds by SMARTS</a>&nbsp;
		</div>
	</div>		
	
		
	<div class="row half-bottom">
		<label class="three columns alpha">Compounds</label>
		<div class="ten columns omega">
		<a href='${ambit_root}/dataset/${datasetid}/compounds'>Browse chemical structures only</a>&nbsp;
		</div>
	</div>	
	
	<div class="row half-bottom">
		<label class="three columns alpha">Dataset features</label>
		<div class="ten columns omega">
		<a href='${ambit_root}/dataset/${datasetid}/feature'>Browse the dataset features</a>
		</div>
	</div>		

	<#if username??>
		<#if openam_token??>
			<div class="row half-bottom">
				<label class="three columns alpha">Number of structures</label>
				<div class="ten columns omega">
				<span id='D1'></span><script>loadStats('${ambit_root}','${ambit_root}/dataset/${datasetid}','#D1');</script>
				</div>
			</div>	
			
			<div class="row half-bottom">
				<label class="three columns alpha">Fingerprint statistics</label>
				<div class="ten columns omega">
				<a href='${ambit_root}/chart/bar?dataset_uri=${ambit_root}/dataset/${datasetid}&param=sk1024'  target='_blank'>Structure fragments bar chart</a>&nbsp;
				<a href='${ambit_root}/chart/bar?dataset_uri=${ambit_root}/dataset/${datasetid}&param=fp1024' target='_blank'>Hashed fingerprints bar chart</a>&nbsp;
				</div>
			</div>				
		<#else>
		<#if ambit_admin?? && ambit_admin>
			<div class="row half-bottom">
				<label class="three columns alpha">Number of structures</label>
				<div class="ten columns omega">
				<span id='D1'></span><script>loadStats('${ambit_root}','${ambit_root}/dataset/${datasetid}','#D1');</script>
				</div>
			</div>	
			
			<div class="row half-bottom">
				<label class="three columns alpha">Fingerprint statistics</label>
				<div class="ten columns omega">
				<a href='${ambit_root}/chart/bar?dataset_uri=${ambit_root}/dataset/${datasetid}&param=sk1024'  target='_blank'>Structure fragments bar chart</a>&nbsp;
				<a href='${ambit_root}/chart/bar?dataset_uri=${ambit_root}/dataset/${datasetid}&param=fp1024' target='_blank'>Hashed fingerprints bar chart</a>&nbsp;
				</div>
			</div>				
			</#if>		
		</#if>
	</#if>

	<div class="row half-bottom">
		<label class="three columns alpha">Statistics</label>
		<div class="ten columns omega">
		<a href='${ambit_root}/dataset/${datasetid}/query/type_strucs' target='_blank'>Structure type statistics</a>&nbsp;|
		<a href='${ambit_root}/dataset/${datasetid}/query/label_compounds' target='_blank'>Consensus label statistics</a>&nbsp;|
		<a href='${ambit_root}/dataset/${datasetid}/query/label_strucs' target='_blank'>Structure quality label statistics</a>&nbsp;
		</div>
	</div>		

	



</div>
</div>
</form>
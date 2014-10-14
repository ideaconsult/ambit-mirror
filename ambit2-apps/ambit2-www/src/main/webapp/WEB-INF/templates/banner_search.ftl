<!-- thirteen-->
<div class="thirteen columns remove-bottom" id="query">
<div class="seven columns alpha">
	<div class="remove-bottom h3">
			Structure search	
	</div>
	<div class="remove-bottom h6">
	<input type='radio' class='h6' checked value='auto' name='option' title='Exact structure or search by an identifier. CAS, Chemical name, SMILES or InChI. The input type is guessed automatically.' size='20' onClick='clickAuto();'>Exact structure
	<input type='radio' class='h6' name='option' value='similarity' title='Enter SMILES or draw structure' onClick='clickSimilarity();'>Similarity&nbsp;
	<input type='radio' class='h6' name='option' value='smarts' title='Enter or draw a SMARTS query' size='20' onClick='clickSmarts();'>Substructure&nbsp;
	<input type='hidden' class='h6' name='type' value='smiles'>	
	<br class='h6'>
	<span class='help' id='strucSearch'></span>
	</div>
</div>
<div class="four columns omega">
	<div id='thresholdSpan' style='display:none;' class="remove-bottom">
		<div class="remove-bottom h3">
			&nbsp;
		</div>
		<div class="remove-bottom h6">	
			<select title ='Tanimoto similarity threshold' id='threshold' name='threshold' style='width:6em;' class="remove-bottom" >
					<option value='0.9' selected>0.9</option>
			   		<option value='0.8' >0.8</option>
			   		<option value='0.7' >0.7</option>
			   		<option value='0.6' >0.6</option>
			   		<option value='0.5' >0.5</option>
			   		<option value='0.4' >0.4</option>
			   		<option value='0.3' >0.3</option>
			   		<option value='0.2' >0.2</option>
			   		<option value='0.1' >0.1</option>
			</select>	
			<span class='help'>Similarity threshold</span>
		</div>
	</div>
	<div id='funcgroupsSpan' style='display:none;' class="remove-bottom">
		<div class="remove-bottom h3">
			&nbsp;
		</div>
		<div class="remove-bottom h6">	
		    <select size='1' title ='Predefined functional groups' id='funcgroups' name='funcgroups' class="remove-bottom" >
			</select>
			<span class='help'>Functional groups</span>
		</div>
	</div>
</div>
<div class="three columns omega">
	<div class="remove-bottom h3">
		&nbsp;
	</div>
	<div class="remove-bottom h6">
		<input type='text' style='margin:0;padding-bottom:0;width: 40em;height:1em;'  id='search' name='search' value='' tabindex='1' > 
		<input type='hidden' style='margin:0;padding-bottom:0;width: 40em;height:1em;'  id='b64search' name='b64search' value='' tabindex='2' >
		<a href='#' class='help' style='margin:0;padding:0;' onClick='startEditor("${ambit_root}","searchform");'>Draw (sub)structure</a>
		<span id="molQuery"></span>		
	</div>	
</div>
<div class="one column omega">
	<div class="remove-bottom h3">
		&nbsp;
	</div>
	<div class="remove-bottom h6">
		<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
	</div>
</div>		

</div>	


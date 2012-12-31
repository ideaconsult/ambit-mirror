<!-- thirteen-->
<div class="thirteen columns" id="query">
<div class="seven columns alpha">
	<h3 class="remove-bottom">
			Structure search	
	</h3>
	<h6 class="remove-bottom">
	<input type='radio' checked value='auto' name='option' title='Exact structure or search by an identifier. CAS, Chemical name, SMILES or InChI. The input type is guessed automatically.' size='20' onClick='clickAuto();'>Exact structure
	<input type='radio' name='option' value='similarity' title='Enter SMILES or draw structure' onClick='clickSimilarity();'>Similarity&nbsp;
	<input type='radio' name='option' value='smarts' title='Enter or draw a SMARTS query' size='20' onClick='clickSmarts();'>Substructure&nbsp;
	<input type='hidden' name='type' value='smiles'>	
	<br>
	<span class='help' id='strucSearch'></span>
	</h6>
</div>
<div class="four columns omega">
	<span id='thresholdSpan' style='display:none;' class="remove-bottom">
		<span class="remove-bottom help">
			&nbsp;
		</span>
		<h6 class="remove-bottom">	
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
		</h6>
	</span>
	<span id='funcgroupsSpan' style='display:none;' class="remove-bottom">
		<h3 class="remove-bottom">
			&nbsp;
		</h3>
		<h6 class="remove-bottom">	
		    <select size='1' title ='Predefined functional groups' id='funcgroups' name='funcgroups' class="remove-bottom" >
			</select>
			<span class='help'>Functional groups</span>
		</h6>
	</span>
</div>
<div class="three columns omega">
	<h3 class="remove-bottom">
		&nbsp;
	</h3>
	<h6 class="remove-bottom">
		<input type='text' style='margin:0;padding-bottom:0;width: 40em;height:1em;'  id='search' name='search' value='' tabindex='1' >
		<a href='#' class='help' style='margin:0;padding:0;' onClick='startEditor("${ambit_root}","searchform");'>Draw (sub)structure</a>
	</h6>	
</div>
<div class="one column omega">
	<h3 class="remove-bottom">
		&nbsp;
	</h3>
	<h6 class="remove-bottom">
		<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
	</h6>
</div>		

</div>	

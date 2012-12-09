<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript'>

	
	$(document)
			.ready(
					function() {
						searchFormValidation('#searchform');
					//	var s= document.getElementById('funcgroups');
						$.each(funcgroups,function(index,val) {
								$('<option>',
										{
										    value: val["smarts"],
										    title: val["hint"]===undefined?
										    		val["family"] + " SMARTS: " + val["smarts"]:
										    		val["family"]+" " + 
										    		val["hint"]===undefined?"":val["hint"] + 
										    		" SMARTS: " + val["smarts"],
										    text: val["name"]
										}).appendTo("#funcgroups");
						});
						$('#funcgroups').change(function(){ 
							$('#search').attr("value",$(this).val());
							$('input:radio[name=option][value=smarts]').click();
						});						
						
						document.form.search.value = $.cookie('repdose.search')==null?"50-00-0":$.cookie('repdose.search');
						document.form.pagesize.value = $.cookie('repdose.pagesize')==null?"10":$.cookie('repdose.pagesize');
						//option
						try {
							if ($.cookie('repdose.option')==null) 
								$radios.filter('[value=auto]').attr('checked', true);
							else {
								var $radios = $('input:radio[name=option]');
					        	$radios.filter('[value='+$.cookie('repdose.option')+']').attr('checked', true);
							}
						} catch (err) {
							$radios.filter('[value=auto]').attr('checked', true);
						}
						//select
						try {
							$('#threshold').val($.cookie('repdose.threshold'));
						} catch (err) {
							$('#threshold').val(0.9);
						}
						//func groups
						try {
							$('#funcgroups').val($.cookie('repdose.search')==null?"":$.cookie('repdose.search'));
						} catch (err) {
							$('#funcgroups').val("");
						}						
					});
	$(function() {
		$(".repdose").button();
		$("#searchform").validate();
		//$( document ).tooltip(); this is broken in IE
	});
</script>
<script language="JavaScript">

function submitSmiles() {
  var smiles = document.JME.smiles();
  var jme = document.JME.jmeFile();
  if (smiles == "") {
    alert("Please use the JME Molecular Editor to draw the structure and then click the 'Submit molecule' link! It will copy the molecule SMILES to the search field.");
  }
  else {
	document.form.type.value = "smiles"; 
	document.form.search.value = smiles; 	
  }
}

function openHelpWindow() {
  window.open("${ambit_root}/jme/jme_help.html","","toolbar=no,menubar=no,scroolbars=no,resizable=yes,width=500,height=600")
}

</SCRIPT>
</head>

<body>

<div class="container columns" style="margin:0;padding:0;">

<#include "/banner.ftl">

<div class="two columns" style="padding:0;" >
menu
</div>
<div class="thirteen columns" style="padding:0;" >
	<div class='search ui-widget'>
	<form method='GET' name='form' id='searchform' method='get' action='${ambit_root}/query'>
	<!--  two columns  -->		
	<div id="container2">
	<div id="container1">
		<div id="col1">
			<p><label for="search" title='Enter any chemical compound identifier: CAS, Chemical name, SMILES, InChI or SMARTS in case of Substructure search. The input type is guessed automatically. The SMILES or SMARTS may be entered manually into the text field or alternatively use the JME editor to draw the structure.'>
				Chemical compound identifier<em>*</em></label>
				<input type='text' id='search' name='search' size='40' value='' tabindex='1'>
			</p>
			<p><label for="option">Search options<em>*</em></label>
			<input type='radio' checked value='auto' name='option' title='CAS, Chemical name, SMILES or InChI. The input type is guessed automatically.' size='20'>Exact structure or search by an identifier
			</p>
			<p>
			<label>&nbsp;</label>
			<input type='radio' name='option' value='similarity' title='Enter SMILES or draw structure'>Similarity&nbsp;
				<select title ='Tanimoto similarity threshold' id='threshold' name='threshold'>
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
			</p>
			<p>
			<label>&nbsp;</label>
			<input type='radio' name='option' value='smarts' title='Enter or draw a SMARTS query' size='20'>Substructure&nbsp;

			</p>
			<p>
			<label>&nbsp;</label>
			 <select size='1' title ='Predefined functional groups' id='funcgroups' name='funcgroups'>
			  </select>
			</p>
			<p>
			<label>Max number of hits</label>
			<input type='text' size='3' name='pagesize' value='10'>
			</p>
			<p>
			<label>&nbsp;</label>
			 <input type='hidden' name='type' value='smiles'>
			<input tabindex='2' class='repdose' id='submit' type='submit' value='Search'/>
			</p>		
					
		</div>
		<div id="col2" style="text-align:left;">
			<applet code="JME.class" name="JME" archive="${ambit_root}/jme/JME.jar" width="450" height="350">
					<param name="options" value="query,nohydrogens,polarnitro,nocanonize">
					You have to enable Java and JavaScritpt on your machine ! 
					</applet>		   	
					<br>
					<a href='#' onClick = "submitSmiles()" title='This will transfer the molecule into the search field as SMILES'>Submit Molecule</a>
					<a href="http://www.molinspiration.com/jme/index.html" target=_blank title='JME Editor courtesy of Peter Ertl, Novartis'><img border='0' src='${ambit_root}/images/help.png'></a></a>
		</div>
	</div>
	</div>
				</form> 
				
				
				</div>		
				
</div>
<div class="one column" style="padding:0;" >
right menu
</div>

</div> <!-- container -->

</body>
</html>


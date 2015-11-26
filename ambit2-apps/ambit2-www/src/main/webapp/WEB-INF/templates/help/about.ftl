<div class='helptitle' style='font-weight:bold;'>About</div>
<div class='helpcontent'>
The open source chemoinformatic system <a href='http://ambit.sf.net' class='qxternal'>AMBIT<a/> provides the following functions:<br/>
Search for structure(s) and meta data <a href='#' class='chelp stra'>?</a><br/>
Assessment tools for read-across and category formation <a href='#' class='chelp ra'>?</a><br/>
Predictions <a href='#' class='chelp toxtree'>?</a> and tools for data analysis<a href='#' class='chelp dataanalysis'>?</a><br/>
Data management <a href='#' class='chelp upload'>?</a><br/>
REST Web services API<a href='#' class='chelp api'>?<a><br/>
</div>

<div id="keys" style="display:none;">
  <ul>
	<li><a href="#smt">&nbsp;</a></li>
    <li><a href="#api">REST API</a></li>
    <li><a href="#toxtree">Predictions</a></li>
    <li><a href="#dataanalysis">Data analysis</a></li>
    <li><a href="#upload">Data analysis</a></li>
    <li><a href="#ra">Read Across support</a></li>
    <li><a href="#stra">Structure search</a></li>
    <li><a href="#substancesearch">Chemical substances</a></li>
  </ul>

	<div id="toxtree">
		<a href='http://toxtree.sf.net' class='qxternal'>Toxtree</a> - Toxic Hazard Estimation by decision tree approach.
		<br/>
		 Toxtree modules (Cramer rules, Protein binding, Carcinogenicity and mutagenicity, pKa, Log Kow, etc.)
	 	 are <a href='${ambit_root}/ui/toxtree'>available online</a>.
	</div>	
	
	<div id="stra">
	The <i>Search/Search structures and associated data</i> menu, allows to search chemical structures by exact structure, similarity or substructure.
	<br/>
	The exact search option accepts any chemical identifier (SMILES, InChI, chemical name, CAS, EINECS) or a structure drawn through the structure diagram editor (the rightmost buton with a pen icon). If the search string submitted is not a chemical identifier (this is automatically determined), AMBIT searches for available properties with value equal to this string.
	<br/>
		The similarity search option retrieves chemical structures based on Tanimoto similarity with hashed fingerprints. The default similarity threshold is 0.9 and can be selected through a dropdown box. The similarity search text box accepts (SMILES, InChI, or chemical name), or structure .
		<br/>
	The substructure search query can be defined by drawing the structure, selecting a SMARTS from the predefined list of SMARTS, or entering a SMARTS, SMILES or chemical name in the text box.
	</div>
	
	<div id="ra">
			Workflow for read across and category formation. <a href="http://echa.europa.eu/support/grouping-of-substances-and-read-across" target="qxternal" class="qxternal">REACH guidance</a>  <br/>
			The <a href='${ambit_root}/bundle'>assessment</a> workflow is organized in five main tabs: 
			<i>Assessment identifier</i>,<i>Collect structures</i>,<i>Endpoint data used</i>,<i>Assessment details</i>,<i>Endpoint data used</i>,<i>Report</i>.
	</div>
	
	<div id="dataanalysis">
		<a href='${ambit_root}/algorithm?type=Regression'>Regression</a> 
		<a href='${ambit_root}/algorithm?type=Classification'>Classification</a> 
			<a href='${ambit_root}/algorithm?type=Clustering'>Clustering</a> 
				<a href='${ambit_root}/algorithm'>More algoritms</a>
	</div>
	<div id="upload">
	Meta data management: <a href='${ambit_root}/ui/uploadsubstance'>substances data</a>, 
	flexible creation of dataset with chemical structures and properties by <a href='${ambit_root}/ui/uploadstruc'>import</a>, etc.,
	</div>
	  <div id="substancesearch">
		  Chemical substance, a material with a definite chemical composition.
	<a href='http://echa.europa.eu/documents/10162/13643/nutshell_guidance_substance_en.pdf' class='qxternal'  target='_blank' title='Identification and naming of 
	substances under REACH and CLP'>REACH guide</a> 
	<br/>
	Support for mono-constituent and multi-constituent substances, additives, impurities.
	<br/>
	The endpoint data is assigned to substances. 
	<br/>
	Calculated descriptors are assigned to chemical structures.
  </div>

	  <div id="api">
	  		 Data exchange, data analysis and predictions <a href='http://ideaconsult.github.io/examples-ambit/apidocs/'  target=_blank class='qxternal'>REST API</a>
	  </div>
	  
</div>      


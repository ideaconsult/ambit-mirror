	

	<div class='row remove-bottom'>
		<label class='five columns alpha' for="alg_title">Name</label>
		<span class='eight columns alpha half-bottom' id='alg_title'></span>
		<div class='three columns omega'></div>
	</div>

	<div class='row remove-bottom'>
		<label class='five columns alpha' for="alg_implementation">Implementation of</label>
		<a class='eleven columns alpha half-bottom' target=_blank href='#' id='alg_implementation'></a>
	</div>	
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="alg_requires">Requires</label>
		<span class='eight columns alpha half-bottom' id='alg_requires'></span>
		<div class='three columns omega'></div>
	</div>		

	<div class='row remove-bottom'>
		<label class='five columns alpha' for="alg_type">Type</label>
		<span class='eight columns alpha half-bottom' id='alg_type'></span>
		<div class='three columns omega'></div>
	</div>	
		
	<div class='row remove-bottom'  id='predictsEndpoint'>
		<label class='five columns alpha' for="alg_endpoint">Predicts endpoint</label>
		<span class='eight columns alpha half-bottom' id='alg_endpoint'></span>
		<div class='three columns omega'></div>
	</div>	
			
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="alg_dataset">Action</label>
		<span class='eight columns alpha half-bottom' id='alg_dataset'></span>
		<div class='three columns omega help' id='help_action'></div>
	</div>		
	
	
<#switch algid>
	<#case 'ambit2.mopac.MopacShell'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="mopac_commands">MOPAC options</label>
		<input class='eight columns alpha half-bottom' type="text" id='mopac_commands' value='PM3 NOINTER MMOK BONDS MULLIK GNORM=1.0 T=30.00M' name='mopac_commands' title='Enter MOPAC options'>
		<div class='three columns omega'><a href='#' class='chelp hmopac'>?</a></div>
	</div>

	  <#break>	  
	<#case 'ambit2.mopac.MopacShellBalloon'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="mopac_commands">MOPAC options</label>
		<input class='eight columns alpha half-bottom' type="text" id='mopac_commands' value='PM3 NOINTER MMOK BONDS MULLIK GNORM=1.0 T=30.00M' name='mopac_commands' title='Enter MOPAC options'>
		<div class='three columns omega'><a href='#' class='chelp hmopac'>?</a></div>
	</div>
	<div class='row remove-bottom'>
		<a href='${ambit_root}/algorithm?type=Structure'>MOPAC with different starting structure generators</a>
	</div>	    	
	  <#break>	  
	<#case 'ambit2.mopac.MopacShellOB'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="mopac_commands">MOPAC options</label>
		<input class='eight columns alpha half-bottom' type="text" id='mopac_commands' value='PM3 NOINTER MMOK BONDS MULLIK GNORM=1.0 T=30.00M' name='mopac_commands' title='Enter MOPAC options'>
		<div class='three columns omega'><a href='#' class='chelp hmopac'>?</a></div>
	</div>
	<div class='row remove-bottom'>
		<a href='${ambit_root}/algorithm?type=Structure'>MOPAC with different starting structure generators</a>
	</div>	    	
	  <#break>	  	  
  <#case 'finder'>
    <div id='finder'>
	<div class='row remove-bottom' id='requiresDataset'>
		<label class='five columns alpha' for="dataset_uri">Dataset URI </label>
		<input class='eight columns alpha half-bottom' type="text" id='dataset_uri' value='' name='dataset_uri' title='Enter dataset uri'>
		<div class='three columns omega'><a href='#' class='chelp hdataset'>?</a></div>
	</div>	    	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_uris[]">Dataset column, containing the identifier (OpenTox Feature URI) </label>
		<input class='eight columns alpha half-bottom featureuri' type='text' title='URI of the dataset feature (e.g. http://host/ambit2/feature/2), containing the identifier (e.g. CAS)' name='feature_uris[]' value=''>
		<div class='three columns omega help'><a href='#' class='chelp hfeature'>?</a></div>
	</div>		
	<div class='row remove-bottom' >
		<label class='five columns alpha' >Web site</label>
		<select class='eight columns alpha half-bottom'  name='search'>
		<option value='CIR' selected  title='http://cactus.nci.nih.gov/chemical/structure'>Chemical Identifier Resolver</option>
		<option value='CHEMIDPLUS'   title='http://chem.sis.nlm.nih.gov/chemidplus'>ChemIDplus</option>
		<option value='PUBCHEM_NAME'   title='http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/%s/SDF'>Search PubChem by name</option>
		<option value='PUBCHEM_SID'   title='http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/sid/%s/SDF'>Search PubChem by SID</option>
		<option value='PUBCHEM_CID'   title='http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/%s/SDF'>Search PubChem by CID</option>
		<option value='PUBCHEM_INCHIKEY'   title='http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/inchikey/%s/SDF'>Search PubChem by InChIKey</option>
		<option value='NAME2STRUCTURE'   title='http://www-ucc.ch.cam.ac.uk/products/software/opsin'>Chemical Name to Structure converter (OPSIN) </option>
		<option value='CHEBI'  disabled title='http://www.ebi.ac.uk/chebi'>Chemical Entities of Biological Interest (ChEBI)</option>
		<option value='CHEMBL'   title='https://www.ebi.ac.uk/chembldb/index.php/ws/'>ChEMBL Web Services</option>
		<option value='OPENTOX'   title='https://apps.ideaconsult.net/ambit2'>OpenTox</option>
		<option value='TBWIKI'   title='http://wiki.toxbank.net/wiki/Special:SPARQLEndpoint'>ToxBank WIKI</option>
		</select>
		<div class='three columns omega'></div>
	</div>		
	<div class='row remove-bottom' >
		<label class='five columns alpha' >How to process and store the results</label>
		<select class='eight columns alpha half-bottom'  name='mode'>
		<option value='emptyonly' >Lookup only empty structures and replace the current structure representation</option>
		<option value='replace' >Lookup all structures and replace the current structure representation</option>
		<option value='add' >Lookup all structures and add the result as additional structure representation</option>
		<option value='emptyadd' selected>Lookup only empty structures and add the result as additional structure representation</option>
		</select>
		<div class='three columns omega'></div>
	</div>		
	</div>
    <#break>
  <#case 'superbuilder'>
  	<div id='superBuilder'>
     <div class='row remove-bottom' >
		<label class='five columns alpha' for="dataset_uri">Dataset URI <em>*</em></label>
		<input class='eight columns alpha half-bottom dataseturi' type="text" id='dataset_uri' value='' name='dataset_uri' title='Enter dataset URI' size='100'>
		<div class='three columns omega'><a href='#' class='chelp hdataset'>?</a></div>
	</div>	  
    <div class='row remove-bottom' >
		<label class='five columns alpha' for="prediction_feature">Prediction feature URI </label>
		<input class='eight columns alpha half-bottom featureuri' type="text" id='prediction_feature' value='' name='prediction_feature' title='Enter feature uri' size='100'>
		<div class='three columns omega'><a href='#' class='chelp htarget'>?</a></div>
	</div>	
    <div class='row remove-bottom' >
		<label class='five columns alpha' for="model_learning">Learning algorithm URI 

		</label>
		<input class='eight columns alpha half-bottom alguri' type="text" id='model_learning' value='' name='model_learning' title='Enter algorithm uri'>
		<div class='three columns omega'><a href='#' class='chelp hlearning'>?</a></div>
	</div>		
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation1">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom descuri' type="text" id='feature_calculation1' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'><a href='#' class='chelp hdescriptor'>?</a></div>
	</div>	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation2">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom descuri' type="text" id='feature_calculation2' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'><a href='#' class='chelp hdescriptor'>?</a></div>
	</div>	
			<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation3">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom descuri' type="text" id='feature_calculation3' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'><a href='#' class='chelp hdescriptor'>?</a></div>
	</div>	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation4">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom descuri' type="text" id='feature_calculation4' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'><a href='#' class='chelp hdescriptor'>?</a></div>
	</div>	
			<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation5">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom descuri' type="text" id='feature_calculation5' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'><a href='#' class='chelp hdescriptor'>?</a></div>
	</div>	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation6">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom descuri' type="text" id='feature_calculation6' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'><a href='#' class='chelp hdescriptor'>?</a></div>
	</div>	
			<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation7">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom descuri' type="text" id='feature_calculation7' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'><a href='#' class='chelp hdescriptor'>?</a></div>
	</div>	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="dataset_service">Dataset service URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='dataset_service' value='' name='dataset_service' title='Enter dataset service uri'>
		<div class='three columns omega'><a href='#' class='chelp hdatasetservice'>?</a></div> 
	</div>			
	</div>
	
    <#break>
  <#case 'superservice'>
  	<div  id='superService'>
    <div class='row remove-bottom' >
		<label class='five columns alpha' for="dataset_uri">Dataset URI </label>
		<input class='eight columns alpha half-bottom dataseturi' type="text" id='dataset_uri' value='' name='dataset_uri' title='Enter dataset URI'>
		<div class='three columns omega'><a href='#' class='chelp hdataset'>?</a></div>
	</div>	  
    <div class='row remove-bottom' >
		<label class='five columns alpha' for="model_uri">Model URI</label>
		<input class='eight columns alpha half-bottom modeluri' type="text" id='model_uri' value='' name='model_uri' title='Enter model uri'>
		<div class='three columns omega'><a href='#' class='chelp hmodel'>?</a></div>
	</div>		
    <div class='row remove-bottom' >
		<label class='five columns alpha' for="algorithm_uri1">Algorithm URI</label>
		<input class='eight columns alpha half-bottom descuri' type="text" id='algorithm_uri1' value='' name='algorithm_uri' title='Enter algorithm uri'>
		<div class='three columns omega'><a href='#' class='chelp hdescriptor'>?</a></div>
	</div>		
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="algorithm_uri2">Algorithm URI</label>
		<input class='eight columns alpha half-bottom descuri' type="text" id='algorithm_uri2' value='' name='algorithm_uri' title='Enter algorithm uri'>
		<div class='three columns omega'><a href='#' class='chelp hdescriptor'>?</a></div>
	</div>
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="algorithm_uri3">Algorithm URI</label>
		<input class='eight columns alpha half-bottom descuri' type="text" id='algorithm_uri3' value='' name='algorithm_uri' title='Enter algorithm uri'>
		<div class='three columns omega'><a href='#' class='chelp hdescriptor'>?</a></div>
	</div>	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="dataset_service">Dataset service URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='dataset_service' value='' name='dataset_service' title='Enter dataset service uri'>
		<div class='three columns omega'><a href='#' class='chelp hdatasetservice'>?</a></div>
	</div>		
	</div>
    <#break>
  <#case 'selectstructure'>
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="dataset_uri">Enter a dataset URI </label>
		<input class='eight columns alpha half-bottom dataseturi' type="text" value='' name='dataset_uri' title='Enter dataset uri'>
		<div class='three columns omega'><a href='#' class='chelp hdataset'>?</a></div>
	</div>		
    <#break>
  <#case 'toxtreecarc'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/carc.html' target=_blank class='eight columns alpha half-bottom'>
		Benigni/ Bossa rulebase (for mutagenicity and cancerogenicity)</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' > Toxtree</a> module</div>
	</div>	
	  <#break>
  <#case 'toxtreecramer'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/cramer.html' target=_blank class='eight columns alpha half-bottom'>
		Cramer rules</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>	
	  <#break>
  <#case 'toxtreecramer2'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/cramer2.html' target=_blank class='eight columns alpha half-bottom'>
		Extended Cramer rules</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>		
	  <#break>
  <#case 'toxtreemic'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/mic.html' target=_blank class='eight columns alpha half-bottom'>
		Structure Alerts for the in vivo micronucleus assay in rodents
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>	
	  <#break>	 
  <#case 'toxtreeissfunc'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/issfunc.html' target=_blank class='eight columns alpha half-bottom'>
		Structural Alerts for Functional Group Identification (ISSFUNC)
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>		
	  <#break>
  <#case 'toxtreeverhaar'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/verhaar.html' target=_blank class='eight columns alpha half-bottom'>
		Verhaar scheme for predicting toxicity mode of action
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>		
	  <#break>
  <#case 'toxtreeames'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/carc.html' target=_blank class='eight columns alpha half-bottom'>
		ToxTree: In vitro mutagenicity (Ames test) alerts by ISS
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>		
	  <#break>	  
  <#case 'toxtreeverhaar2'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/verhaar2.html' target=_blank class='eight columns alpha half-bottom'>
		Verhaar scheme for predicting toxicity mode of action (modified)
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>	
	  <#break>
	 <#case 'toxtreesmartcyp'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/smartcyp.html' target=_blank class='eight columns alpha half-bottom'>
		SMARTCyp - Cytochrome P450-Mediated Drug Metabolism
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>		
	  <#break>
	 <#case 'toxtreebiodeg'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/start.html' target=_blank class='eight columns alpha half-bottom'>
		START biodegradation and persistence
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>	
	  <#break>
	<#case 'toxtreeskinsens'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/skinsensitisation.html' target=_blank class='eight columns alpha half-bottom'>
		Skin sensitisation alerts
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>	
	  <#break>
	<#case 'toxtreeskinirritation'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/skin.html' target=_blank class='eight columns alpha half-bottom'>
		A decision tree for estimating skin irritation and corrosion potential
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>	
	  <#break>
	<#case 'toxtreeeye'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/eye.html' target=_blank class='eight columns alpha half-bottom'>
		A decision tree for estimating eye irritation and corrosion potential
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>	
	  <#break>	
	<#case 'toxtreeproteinbinding'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/skinsensitisation.html' target=_blank class='eight columns alpha half-bottom'>
		Protein binding
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>	
	  <#break>
	<#case 'toxtreednabinding'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/skinsensitisation.html' target=_blank class='eight columns alpha half-bottom'>
		Protein binding
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>	
	  <#break>	
	<#case 'toxtreeames'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="doc">Documentation and references</label>
		<a href='http://toxtree.sourceforge.net/carc.html' target=_blank class='eight columns alpha half-bottom'>
		Ames mutagenicity
		</a>
		<div class='three columns omega help'>A <a href='http://toxtree.sf.net' class='qxternal' >Toxtree</a> module</div>
	</div>	
	  <#break>
	  
	<#case 'ambit2.mopac.MopacOriginalStructure'>
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="mopac_commands">MOPAC options</label>
		<input class='eight columns alpha half-bottom' type="text" id='mopac_commands' value='PM3 NOINTER MMOK BONDS MULLIK GNORM=1.0 T=30.00M' name='mopac_commands' title='Enter MOPAC options'>
		<div class='three columns omega'><a href='#' class='chelp hmopac'>?</a></div>
	</div>	
		  
  <#default>
	
	<div class='row remove-bottom' id='requiresDataset'>
		<label class='five columns alpha' for="dataset_uri">Enter a dataset URI </label>
		<input class='eight columns alpha half-bottom dataseturi' type="text" id='dataset_uri' value='' name='dataset_uri' title='Enter dataset uri'>
		<div class='three columns omega'><a href='#' class='chelp hdataset'>?</a></div>
	</div>		
	
	<div class='row remove-bottom' id='requiresTarget'>
		<label class='five columns alpha' for="prediction_feature">Enter URI of the target variable</label>
		<input class='eight columns alpha half-bottom featureuri' type="text" id='prediction_feature' value='' name='prediction_feature' title='Enter feature uri'>
		<div class='three columns omega'><a href='#' class='chelp htarget'>?</a></div>
	</div>
	
			
</#switch>

	
	<div class='row'>
		<label class='five columns alpha'>&nbsp;</label>
		<input class='three columns alpha submit' type='submit' value='Run'>
		<div class='eight columns omega'></div>
	</div>

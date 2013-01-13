	

	<div class='row remove-bottom'>
		<label class='five columns alpha' for="title">Name</label>
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
		<div class='three columns omega'></div>
	</div>		
	
	
<#switch algid>
  <#case 'finder'>
    <div id='finder'>
	<div class='row remove-bottom' id='requiresDataset'>
		<label class='five columns alpha' for="dataset_uri">Dataset URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='dataset_uri' value='' name='dataset_uri' title='Enter dataset uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/dataset?max=25' target=_blank>Datasets</a></div>
	</div>	    	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_uris[]">Dataset column, containing the identifier (OpenTox Feature URI)</label>
		<input class='eight columns alpha half-bottom' type='text' title='URI of the dataset feature (e.g. http://host/ambit2/feature/2), containing the identifier (e.g. CAS)' name='feature_uris[]' value=''>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/feature?max=25' target=_blank>Features</a></div>
	</div>		
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_uri">Web site</label>
		<select class='eight columns alpha half-bottom'  name='search'>
		<option value='CIR' selected  title='http://cactus.nci.nih.gov/chemical/structure'>Chemical Identifier Resolver</option>
		<option value='CHEMIDPLUS'   title='http://chem.sis.nlm.nih.gov/chemidplus'>ChemIDplus</option>
		<option value='PUBCHEM_NAME'   title='http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/%s/SDF'>Search PubChem by name</option>
		<option value='PUBCHEM_CID'   title='http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/%s/SDF'>Search PubChem by CID</option>
		<option value='PUBCHEM_INCHIKEY'   title='http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/inchikey/%s/SDF'>Search PubChem by InChIKey</option>
		<option value='NAME2STRUCTURE'   title='http://www-ucc.ch.cam.ac.uk/products/software/opsin'>Chemical Name to Structure converter (OPSIN) </option>
		<option value='CHEBI'  disabled title='http://www.ebi.ac.uk/chebi'>Chemical Entities of Biological Interest (ChEBI)</option>
		<option value='CHEMBL'   title='https://www.ebi.ac.uk/chembldb/index.php/ws/'>ChEMBL Web Services</option>
		<option value='OPENTOX'   title='http://apps.ideaconsult.net:8080/ambit2'>OpenTox</option>
		</select>
		<div class='three columns omega'></div>
	</div>		
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_uri">How to process and store the results</label>
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
		<label class='five columns alpha' for="dataset_uri">Dataset URI<em>*</em></label>
		<input class='eight columns alpha half-bottom' type="text" id='feature_uri' value='' name='dataset_uri' title='Enter dataset URI'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/dataset?max=25' target=_blank>Datasets</a></div>
	</div>	  
    <div class='row remove-bottom' >
		<label class='five columns alpha' for="prediction_feature">Prediction feature URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='prediction_feature' value='' name='prediction_feature' title='Enter feature uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/feature?max=25' target=_blank>Features</a></div>
	</div>	
    <div class='row remove-bottom' >
		<label class='five columns alpha' for="model_learning">Learning algorithm URI (e.g. regression)
		</label>
		<input class='eight columns alpha half-bottom' type="text" id='model_learning' value='' name='model_learning' title='Enter algorithm uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm?type=Regression' target=_blank>Regression</a>,
		<a href='${ambit_root}/algorithm?type=Classification' target=_blank>Classification</a>.
		If no learning algorithm specified, it will only build a dataset with all features</div>
	</div>		
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='feature_calculation' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm?type=DescriptorCalculation' target=_blank>Descriptor calculation algorithms</a></div>
	</div>	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='feature_calculation' value='' name='feature_calculation' title='Enter algorithm uri'>
				<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm?type=DescriptorCalculation' target=_blank>Descriptor calculation algorithms</a></div>
	</div>	
			<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='feature_calculation' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm?type=DescriptorCalculation' target=_blank>Descriptor calculation algorithms</a></div>
	</div>	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='feature_calculation' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm?type=DescriptorCalculation' target=_blank>Descriptor calculation algorithms</a></div>
	</div>	
			<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='feature_calculation' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm?type=DescriptorCalculation' target=_blank>Descriptor calculation algorithms</a></div>
	</div>	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='feature_calculation' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm?type=DescriptorCalculation' target=_blank>Descriptor calculation algorithms</a></div>
	</div>	
			<div class='row remove-bottom' >
		<label class='five columns alpha' for="feature_calculation">Descriptor calculation algorithm URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='feature_calculation' value='' name='feature_calculation' title='Enter algorithm uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm?type=DescriptorCalculation' target=_blank>Descriptor calculation algorithms</a></div>
	</div>	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="dataset_service">Dataset service URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='dataset_service' value='' name='dataset_service' title='Enter datset service uri'>
		<div class='three columns omega help'>Optional, will use <a href='${ambit_root}/dataset' target=_blank>the default</a> if not specified.</div> 
	</div>			
	</div>
	
    <#break>
  <#case 'superservice'>
  	<div  id='superService'>
    <div class='row remove-bottom' >
		<label class='five columns alpha' for="dataset_uri">Dataset URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='feature_uri' value='' name='dataset_uri' title='Enter dataset URI'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/dataset?max=25' target=_blank>Datasets</a></div>
	</div>	  
    <div class='row remove-bottom' >
		<label class='five columns alpha' for="model_uri">Model URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='model_uri' value='' name='model_uri' title='Enter model uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/model' target=_blank>Models</a></div>
	</div>		
    <div class='row remove-bottom' >
		<label class='five columns alpha' for="algorithm_uri">Algorithm URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='algorithm_uri' value='' name='algorithm_uri' title='Enter algorithm uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm' target=_blank>Algorithms</a></div>
	</div>		
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="algorithm_uri">Algorithm URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='algorithm_uri' value='' name='algorithm_uri' title='Enter algorithm uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm' target=_blank>Algorithms</a></div>
	</div>
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="algorithm_uri">Algorithm URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='algorithm_uri' value='' name='algorithm_uri' title='Enter algorithm uri'>
				<div class='three columns omega help'>e.g. <a href='${ambit_root}/algorithm' target=_blank>Algorithms</a></div>
	</div>	
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="dataset_service">Dataset service URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='dataset_service' value='' name='dataset_service' title='Enter datset service uri'>
		<div class='three columns omega help'>Optional, will use <a href='${ambit_root}/dataset' target=_blank>the default</a> if not specified.</div>
	</div>		
	</div>
    <#break>
  <#case 'selectstructure'>
	<div class='row remove-bottom' >
		<label class='five columns alpha' for="dataset_uri">Enter a dataset URI</label>
		<input class='eight columns alpha half-bottom' type="text" value='' name='dataset_uri' title='Enter dataset uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/dataset?max=25' target=_blank>Datasets</a></div>
	</div>		
    <#break>
  <#default>
	
	<div class='row remove-bottom' id='requiresDataset'>
		<label class='five columns alpha' for="dataset_uri">Enter a dataset URI</label>
		<input class='eight columns alpha half-bottom' type="text" id='dataset_uri' value='' name='dataset_uri' title='Enter dataset uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/dataset?max=25' target=_blank>Datasets</a></div>
	</div>		
	
	<div class='row remove-bottom' id='requiresTarget'>
		<label class='five columns alpha' for="feature_uri">Enter URI of the target variable</label>
		<input class='eight columns alpha half-bottom' type="text" id='feature_uri' value='' name='feature_uri' title='Enter feature uri'>
		<div class='three columns omega help'>e.g. <a href='${ambit_root}/feature?max=25' target=_blank>Features</a></div>
	</div>		
</#switch>

	
	<div class='row'>
		<label class='five columns alpha'>&nbsp;</label>
		<input class='three columns alpha' type='submit' class='submit' value='Run'>
		<div class='eight columns omega'></div>
	</div>

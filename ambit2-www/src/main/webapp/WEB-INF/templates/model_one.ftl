	

	<div class='row remove-bottom'>
		<label class='three columns alpha' for="model_title">Name</label>
		<span class='eight columns alpha half-bottom' id='model_title'></span>
		<div class='five columns omega'>
		<img src='' border='0' id='model_img'>
		</div>
	</div>

	<div class='row remove-bottom'>
		<label class='three columns alpha' for="model_algorithm">Training algorithm</label>
		<a class='eleven columns alpha half-bottom' target=_blank href='#' id='model_algorithm'></a>
	</div>	

	<div class='row remove-bottom'>
		<label class='three columns alpha' for="model_training">Training dataset</label>
		<a class='eight columns alpha half-bottom' target=_blank href='#' id='model_training'>N/A</a>
		<div class='one column alpha'><a href='#' class='chelp hdataset'>?</a></div>
		<div class='four columns omega help'>
			<a href="#" onClick="self.location='${ambit_root}/algorithm/superbuilder?dataset_uri='+$(model_training).text();">
			Build another model with this dataset</a>
		</div>		
	</div>	

	<div class='row remove-bottom'>
		<label class='three columns alpha'>Variables</label>
		<a class='three columns alpha half-bottom' href='${ambit_root}/model/${modelid}/independent'>Independent</a>
		<a class='three columns alpha half-bottom'  href='${ambit_root}/model/${modelid}/dependent'>Dependent</a>
		<a class='three columns alpha half-bottom' href='${ambit_root}/model/${modelid}/predicted'>Predicted</a>
		<div class='five columns omega'></div>
	</div>	

			
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="alg_dataset">Action</label>
		<span class='eight columns alpha half-bottom' >Predict properties
		<div class='help' id='help_action'></div>
		</span>
		<div class='one column alpha'><a href='#' class='chelp haction'>?</a></div>
		<div class='four columns omega help'>
			<#if modelid??>
			In order to calculate descriptors automatically, use the 
			<a href="${ambit_root}/algorithm/superservice?model_uri=${ambit_root}/model/${modelid}">Superservice</a>
			</#if>
		</div>
		
	</div>		
	
	<div class='row remove-bottom' id='requiresDataset'>
		<label class='three columns alpha' for="dataset_uri">Dataset URI</label>
		<input class='eight columns alpha half-bottom dataseturi' type="text" id='dataset_uri' value='' name='dataset_uri' title='Enter dataset uri'>
		<div class='one column alpha'><a href='#' class='chelp hdataset'>?</a></div>
		<div class='four columns omega'>
		<a href='#' onClick="$('#dataset_uri').attr('value',$('#model_training').text());">Use training dataset</a></label>

		</div>
	</div>	
	
	<div class='row'>
		<label class='three columns alpha'>&nbsp;</label>
		<input class='six columns alpha' type='submit' class='submit' value='Predict'>
		<div class='eight columns omega'></div>
	</div>

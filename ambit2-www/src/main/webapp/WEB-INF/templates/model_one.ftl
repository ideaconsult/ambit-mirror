	

	<div class='row remove-bottom'>
		<label class='four columns alpha' for="model_title">Name</label>
		<span class='eight columns alpha half-bottom' id='model_title'></span>
		<div class='four columns omega'>
		<img src='' border='0' id='model_img'>
		</div>
	</div>

	<div class='row remove-bottom'>
		<label class='four columns alpha' for="model_algorithm">Training algorithm</label>
		<a class='twelve columns alpha half-bottom' target=_blank href='#' id='model_algorithm'></a>
	</div>	

	<div class='row remove-bottom'>
		<label class='four columns alpha' for="model_training">Training dataset <a href='#' class='chelp hdataset'>?</a></label>
		<a class='eight columns alpha half-bottom' title='Click to browse the dataset' target=_blank href='#' id='model_training'>N/A</a>
		<div class='four columns omega'>
			<a href="#" title='Build another model with this dataset' onClick="self.location='${ambit_root}/algorithm/superbuilder?dataset_uri='+$(model_training).text();">
			Build another model</a>
		</div>
	</div>	
	
	<div class='row remove-bottom'>
		<label class='four columns alpha'>Independent variables (X) <a href='#' class='chelp hfeature'>?</a></label>
		<select class='eight columns alpha half-bottom' type="text" id='vindependent'></select>
		<div class='four columns omega '><a href='${ambit_root}/model/${modelid}/independent' target=_blank>Browse</a></div>
	</div>	
	<div class='row remove-bottom'>
		<label class='four columns alpha'>Dependent variables (Yobs) <a href='#' class='chelp hfeature'>?</a></label>
		<select class='eight columns alpha half-bottom' type="text" id='vdependent'></select>
		<div class='four columns omega '><a href='${ambit_root}/model/${modelid}/dependent' target=_blank>Browse</a></div>
	</div>	
	<div class='row remove-bottom'>
		<label class='four columns alpha'>Predicted (Ypred) <a href='#' class='chelp hfeature'>?</a></label>
		<select class='eight columns alpha half-bottom' type="text" id='vpredicted'></select>
		<div class='four columns omega '><a href='${ambit_root}/model/${modelid}/predicted' target=_blank>Browse</a></div>
	</div>
	<div class='row remove-bottom'>
		<label class='four columns alpha'>Representation and Statistics</label>
		<div class='eight columns omega '><a href='${ambit_root}/model/${modelid}?media=text/plain' target=_blank>View text</a></div>
		<div class='four columns omega '></div>
	</div>				
	
			
	<div class='row remove-bottom'>
		<label class='four columns alpha' for="alg_dataset">Action <a href='#' class='chelp haction'>?</a></label>
		<span class='eight columns alpha half-bottom' >Predict properties
		<div class='help' id='help_action'></div>
		</span>
		<div class='four column omega'></div>		
	</div>		
	
	<div class='row remove-bottom' id='requiresDataset'>
		<label class='four columns alpha' for="dataset_uri">Dataset URI <a href='#' class='chelp hdataset'>?</a></label>
		<input class='eight columns alpha half-bottom dataseturi' type="text" id='dataset_uri' value='' name='dataset_uri' title='Enter dataset uri'>
		<div class='four columns omega'>
		
		<a href='#' onClick="$('#dataset_uri').attr('value',$('#model_training').text());">Use training dataset</a></label>
		</div>
	</div>	
	
	<div class='row'>
		<label class='four columns alpha'>&nbsp;</label>
		<input class='six columns alpha' type='submit' class='submit' value='Predict'>
		<div class='two columns alpha'></div>
		<div class='four columns omega'>
			
			<#if modelid??>
			Consider using the 
			<a href="${ambit_root}/algorithm/superservice?model_uri=${ambit_root}/model/${modelid}">Superservice</a>
			in order to calculate descriptors automatically.
			</#if>
		</div>
	</div>

	<div class='row remove-bottom' >
		<label class='four columns alpha' for="dataset_uri">
		Scatter plots <a href='#' class='chelp hchart'>?</a>
		<br>
		<a href='#' onClick="loadChart('${ambit_root}','#dataset_uri','#vdependent','#vpredicted','#chart');">Observed / Predicted </a> |
		<a href='#' onClick="loadChart('${ambit_root}','#dataset_uri','#vindependent','#vdependent','#chart');">X / Observed </a> |
		<a href='#' onClick="loadChart('${ambit_root}','#dataset_uri','#vindependent','#vpredicted','#chart');">X / Predicted </a>		
		<hr class='remove-bottom'/>
		Histograms <a href='#' class='chelp hchart'>?</a>
		<br/>
		<a href='#' onClick="loadHistogramChart('${ambit_root}','#dataset_uri','#vindependent','#chart');">X </a> | 
		<a href='#' onClick="loadHistogramChart('${ambit_root}','#dataset_uri','#vdependent','#chart');">Observed </a> |
		<a href='#' onClick="loadHistogramChart('${ambit_root}','#dataset_uri','#vpredicted','#chart');">Predicted </a>		
		<hr class='remove-bottom'/>
		Pie chart <a href='#' class='chelp hchart'>?</a>
		<br/>
		<a href='#' onClick="loadPieChart('${ambit_root}','#dataset_uri','#vindependent','#chart');">X </a>
		<br/>
		<a href='#' onClick="loadPieChart('${ambit_root}','#dataset_uri','#vdependent','#chart');">Observed </a>
		<br/>
		<a href='#' onClick="loadPieChart('${ambit_root}','#dataset_uri','#vpredicted','#chart');">Predicted </a>
		</label>
		<div class='twelve columns alpha half-bottom' >
		<img src='' id='chart'>		
		</div>
	</div>		
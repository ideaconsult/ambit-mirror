var _ambit = {
	'query_service' : null,	
	'cache' : false,	
	'search' : { 
		'uri': null, 
		'result': null,
		'options' : {
			'imageInTable': true,
			'showProperties' : false,
			'showRegistry' : true,
			'showSMILES' : false,
			'showInChI' : false,
			'showEndpoints' : true,
			'showCalculated' : true,
			'showNames' : true,
			'showSimilarity' : false			
		}
	},		
	'datasets' : [
	],
	'models' : [
	],
	'selectedDatasets' : [],
	'selectedModels' : []
};

function initTable(root, query_service, tableSelector, anArray, checkbox, checked, clickHandler) {
	var uri = query_service + "&media=application%2Fjson";
	$.ajax({
		async: false,
		dataType : "json",
		url : uri,
		success : function(data1, status, xhr) {
			anArray = data1;
		},
		error : function(xhr, status, err) {
			anArray = [];
		},
		complete : function(xhr, status) {}
	});	
	
	$(tableSelector).dataTable({
			"aaData" : anArray,
			"aoColumnDefs" : [
			  	{ //0
			  		"aTargets": [ 0 ],	
			  		"bSortable" : false,
			  		"mDataProp" : null,
			  		sWidth : "1em",
			  		"bUseRendered" : "false",
			  		"fnRender" : function(o,val) {
	  						var uri = o.aData["URI"];
	  						var isChecked = jQuery.inArray(uri,checked)>=0?"checked":"";
	  						
	  						var prm = {'option': 'auto', 'type':'url', 'search':uri, 'pagesize' : 10};
	  						
							var browseURI =  "";
							if (root != null) {
								browseURI = root + "/ui?" + $.param(prm,false);
								browseURI = "<br/><a href='"+browseURI+"' style='margin-left:2px;'><img src='"+
											root+"/images/table.png' title='Browse the dataset'></a>";
							}
		  					return "<input class='selecturi' type='checkbox' "+ isChecked +" name='"+checkbox+"' onClick='"+
		  						clickHandler+"(event);' title='Select "+ 
		  						o.aData.URI +"' value='"+o.aData.URI+"'>"+browseURI;
		  			}
			  	},			                  
				{ //1
				"aTargets": [ 1 ],	
				"sClass" : "left",
				"bSortable" : true,
				"mDataProp" : "title",
				"bUseRendered" : "false",
				"fnRender" : function(o,val) {
					return val + 
					" <a href='"+o.aData.URI+"?max=10' class='help' title='AMBIT URI: "+o.aData.URI+"' target=_blank title='"+o.aData.URI+"'>@</a>";
				}
					
				}
			],
			//"sScrollXInner": "110%",
			"bScrollCollapse": true,
			"aaSorting" : [[ 1, 'asc' ] ],
			"sWidth": "100%",
			"sDom": 'Trtp',
			'bJQueryUI': false, 
			'bPaginate': true,
			'bAutoWidth': true,
			fnDrawCallback: function(){
				  var wrapper = this.parent();
				  var rowsPerPage = this.fnSettings()._iDisplayLength;
				  var rowsToShow = this.fnSettings().fnRecordsDisplay();
				  var minRowsPerPage = this.fnSettings().aLengthMenu[0][0];
				  if ( rowsToShow <= rowsPerPage || rowsPerPage == -1 ) {
				    $('.dataTables_paginate', wrapper).css('visibility', 'hidden');
				  }
				  else {
				    $('.dataTables_paginate', wrapper).css('visibility', 'visible');
				  }
				  if ( rowsToShow <= minRowsPerPage ) {
				    $('.dataTables_length', wrapper).css('visibility', 'hidden');
				  }
				  else {
				    $('.dataTables_length', wrapper).css('visibility', 'visible');
				  }
			}		
	
		});	
	
	return anArray;
}

function selectDataset(event) {
	event = event || window.event;
	if (_ambit.selectedDatasets===undefined) _ambit.selectedDatasets = [];
	if (event.srcElement.checked) {
		if (jQuery.inArray(event.srcElement.value,_ambit.selectedDatasets)<0)
			_ambit.selectedDatasets.push(event.srcElement.value);
	} else 
		_ambit.selectedDatasets.splice(jQuery.inArray(event.srcElement.value,_ambit.selectedDatasets),1);
}

function selectModel(event) {
	event = event || window.event;
	if (_ambit.selectedModels===undefined) _ambit.selectedModels = [];
	if (event.srcElement.checked) {
		if (jQuery.inArray(event.srcElement.value,_ambit.selectedModels)<0)
			_ambit.selectedModels.push(event.srcElement.value);
	} else 
		_ambit.selectedModels.splice(jQuery.inArray(event.srcElement.value,_ambit.selectedModels),1);

}
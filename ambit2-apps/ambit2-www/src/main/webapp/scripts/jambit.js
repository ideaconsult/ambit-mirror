/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012-2013, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 */
var _ambit = {
	'query_service' : null,	
	'query_uri' : null,
	'query_params' : null,
	'data_uri' : null,
	'cache' : true,	
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
	'selectedModels' : [],
	'downloads' : [
	           	  {id:"sdf",img:"sdf.jpg",alt:"SDF",title:'Download as SDF',mime:'chemical/x-mdl-sdfile'},
	           	  {id:"csv",img:"excel.png",alt:"CSV",title:'Download as CSV (Comma delimited file)',mime:'text/csv'},
	           	  {id:"cml",img:"cml.jpg",alt:"CML",title:'Download as CML (Chemical Markup Language)',mime:'chemical/x-cml'},
	           	  {id:"arff",img:"weka.png",alt:"SDF",title:'Download as SDF',mime:'text/x-arff'},
	           	  {id:"rdfxml",img:"rdf.gif",alt:"RDF/XML",title:'Download as RDF XML',mime:'application/rdf+xml'},
	           	  {id:"rdfn3",img:"rdf.gif",alt:"RDF N3",title:'Download as RDF N3',mime:'text/n3'},
	           	  {id:"json",img:"json.png",alt:"JSON",title:'Download as JSON',mime:'application/json'},
	           	  {id:"uri",img:"link.jpg",alt:"URI",title:'Download as URI list',mime:'text/uri-list'},
	           	  {id:"xlsx",img:"csv.jpg",alt:"XLSX",title:'Download as XLSX',mime:'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'}
	           	 ],
	'runModel' : function(modelURI,statusSelector) {
		if ((this.search.result==null) || (this.search.result.dataEntry===undefined) || (this.search.result.dataEntry==null)) 
			alert('The results list is empty. No compounds to apply the model!');
		else {
			$.each(this.search.result.dataEntry,function(index,entry) {
				$(statusSelector).show();
				$(statusSelector).text('running');
				$.ajax({
					contentType :'application/x-www-form-urlencoded; charset=UTF-8',
				    headers: { 
				        Accept : "text/uri-list; charset=utf-8"
				    },
					data : "dataset_uri="+entry.compound.URI, 
					type: "POST",
					url : modelURI,
					success : function(data1, status, xhr) {
						$(statusSelector).text('Completed');
					},
					error : function(xhr, status, err) {
						$(statusSelector).text(err);
					},
					complete : function(xhr, status) {
						$(statusSelector).text(status);
					}
				});	
			});
			
		}
	}           	 
	
};

function initTable(object,root, query_service, tableSelector, arrayName, checkbox, checked, clickHandler) {
	var uri = query_service + "&media=application%2Fjson";
	$(tableSelector).dataTable({
		dataType : "json",
		"sAjaxDataProp" : object,
		"sAjaxSource": uri,	
		"aoColumnDefs" : [
		  	{ //0
		  		"aTargets": [ 0 ],	
		  		"bSortable" : false,
		  		"mData" : null,
		  		"bUseRendered" : "false",
		  		"fnRender" : function(o,val) {
	  					var uri = o.aData["URI"];
	  					var isChecked = jQuery.inArray(uri,checked)>=0?"checked":"";
	  					
	  					var prm = {'option': 'auto', 'type':'url', 'search':uri, 'pagesize' : 10};
	  					
  		        	    var shortURI = o.aData.URI;
  		        	    var pos =  shortURI.lastIndexOf("/");
  		        	    if (pos>=0) shortURI = shortURI.substring(pos+1); 

	  					
						var browseURI =  "";
						if (root != null) {
							browseURI = root + "/ui/query?" + $.param(prm,false);
							browseURI = "<a href='"+browseURI+"' style='margin-left:2px;'><img src='"+
										root+"/images/table.png' border='0' title='Browse the dataset'></a>";
							shortURI = "D"+shortURI;
						} else {
							browseURI = "<a href='#' style='margin-left:2px;'><img src='"+
										_ambit["query_service"]+
										"/images/run.png' border='0' title='Run predictions for all structures in the result list' onClick='_ambit.runModel(\""+o.aData.URI+"\",\"#m"+o.aData["id"]+"\");'></a>";
							shortURI = "M"+shortURI;
						}
	  					
	  					var link = "<a href='"+o.aData.URI+"?max=10' title='AMBIT URI: "+o.aData.URI+"' target=_blank title='"+o.aData.URI+"'><span class='ui-icon ui-icon-link' style='float:left;margin:0;'></span>"+shortURI+"</a><br/>";
	  					
	 					return link + "<input class='selecturi' type='checkbox' "+ isChecked +" name='"+checkbox+"' onClick='"+
	 						clickHandler+"(event);' title='Select "+ 
	 						o.aData.URI +"' value='"+o.aData.URI+"'>"+ browseURI;
		 			}
		  	},			                  
			{ //1
		  		"aTargets": [ 1 ],	
				"sClass" : "left",
				"bSortable" : true,
				"mDataProp" : "title",
				"bUseRendered" : "false",
				"fnRender" : function(o,val) {
					return "<div class='long'>" + val + "</div><span class='help' style='display:none;' title='Check the model and do not forget to refresh the page to view the results!'  id='m"+o.aData["id"]+"'></span>";
				}
					
				}
			],
			//"sScrollXInner": "110%",
			"bScrollCollapse": true,
			"bScrollInfinite": true,
			"sScrollY": "200px",
			"aaSorting" : [[ 1, 'asc' ] ],
			//"sWidth": "100%",
			"sDom": 'Trtp',
			'bJQueryUI': false, 
			'bPaginate': false,
			//'bAutoWidth': true,
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
			},
			"fnServerData" : function(sSource, aoData, fnCallback,
					oSettings) {
			     $.getJSON( sSource, aoData, function (json) {
			         fnCallback(json);
			         try { _ambit[arrayName] = json[object]; } catch (err) { _ambit[arrayName] = [];}
			     });
			}			
	
		});	

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

function downloadFormUpdate(features_uri) {
	$.each(_ambit.downloads,function(index,value) {
		_ambit.query_params['media'] = value.mime;
		var durl = _ambit.query_uri +  $.param(_ambit.query_params,false) + 
				(((features_uri===undefined)|| (features_uri==null))?"":("&" + features_uri));
		
		$('#download #'+value.id).attr('href',durl);
	});
	delete _ambit.query_params.media;
}

function toggleSelectionDiv(id,control) {
	$(id).toggle();
	if( $(id).is(':visible') ) {
		$(control).removeClass("ui-icon-folder-collapsed").addClass("ui-icon-folder-open");
	}
	else {
		$(control).removeClass("ui-icon-folder-open").addClass("ui-icon-folder-collapsed");
	}
}


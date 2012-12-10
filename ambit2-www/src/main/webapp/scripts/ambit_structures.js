function defineStructuresTable(url, query_service, similarity) {
	var imgSize = 150;
	var oTable = $('#structures')
			.dataTable(
					{
						"aoColumnDefs" : [
								{ //0
									"aTargets": [ 0 ],	
									"sClass" : "center",
									"bSortable" : false,
									"mDataProp" : null,
									sWidth : "16px",
									"bUseRendered" : "true",
									"fnRender" : function(o,val) {
										var uri = o.aData["compound"]["URI"];
										return "<input class='selecturi' type='checkbox' checked name='uri[]' title='Select "+ 
													uri +"' value='"+uri+"'><br/>" + 
													"<span class='zoomstruc'>"+
													"<img src='"+query_service+"/images/zoom_in.png' alt='zoom in' title='Click to show compound details'>"+
													"</span>";
									}
								},						                  
								{
									"mDataProp" : "compound.cas",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 1 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : false,
									"sWidth" : "10%",
									"sClass" : "cas",
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData, "cas");
										else
											return val;
									},
									"bVisible" : true
								},			
								{
									"mDataProp" : "compound.URI",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 2 ],
									"bSearchable" : true,
									"bUseRendered" : false,
									"bSortable" : true,
									"sHeight" : imgSize+"px",
									"sWidth" : imgSize+"px",
									"fnRender" : function(o, val) {
										var cmpURI = val;
										if (val.indexOf("/conformer") >= 0) {
											cmpURI = val.substring(0, val
													.indexOf("/conformer"));
										}
										// if ((opentox["model_uri"]==null) ||
										// (opentox["model_uri"] == undefined))
										// {
										cmpURI = cmpURI + "?media=image/png";
										// } else {
										// cmpURI = opentox["model_uri"] +
										// "?dataset_uri=" + cmpURI +
										// "&media=image/png";
										// }
										/*
										 * "<a
										 * href=\"%s%s/%d?headless=true&details=false&media=text/html\"
										 * title=\"Molecule\">Molecule</a>",
										 */
										return '<img class="ui-widget-content" title="Structure diagram" border="0" src="'
												+ cmpURI + '&w='+imgSize+'&h='+imgSize+'">';
									}
								},
								{
									"mDataProp" : "compound.name",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 3 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : false,
									"sClass" : "names",
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData,
													"names");
										else
											return val;
									},
									"bVisible" : true
								},									
								{
									"mDataProp" : "compound.metric",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 4 ],
									"sTitle" : "Similarity",
									"sClass" : "similarity",
									"bSearchable" : true,
									"bSortable" : true,
									"sWidth" : "5%",
									"bVisible" : similarity
								},
								{
									"mDataProp" : null,
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 5 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : true,
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData,
													"smiles");
										else
											return val;
									},
									"bVisible" : true
								},
								{
									"mDataProp" : null,
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 6 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : true,
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData,
													"inchi");
										else
											return val;
									},
									"bVisible" : false
								},
								{
									"mDataProp" : null,
									"asSorting" : [ "asc", "desc" ],
									"sClass" : "inchikey",
									"aTargets" : [ 7 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : true,
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData,
													"inchikey");
										else
											return val;
									},
									"bVisible" : true
								} ],

						"bProcessing" : true,
						"bServerSide" : false,
						"bStateSave" : false,
						"bJQueryUI" : true,
						"bPaginate" : true,
						"sPaginationType": "full_numbers",
						"sPaginate" : ".dataTables_paginate _paging",
						"bDeferRender": true,
						"bSearchable": true,
						//"sDom" : 'R<"clear"><"fg-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix remove-bottom"ip>',
						"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
						"sAjaxSource": url,
						"sAjaxDataProp" : "dataEntry",
						"fnServerData" : function(sSource, aoData, fnCallback,
								oSettings) {
							
							oSettings.jqXHR = $.ajax({
								"type" : "GET",
								"url" : sSource,
								"data" : aoData,
								"dataType" : "json",
								/*
								 * useless - with datatype jsonp no custom headers are sent!
						        'beforeSend': function(xhrObj){
					                xhrObj.setRequestHeader("Content-Type","application/x-javascript");
					                xhrObj.setRequestHeader("Accept","application/x-javascript");
						        },								
								"headers": { 
								        "Accepts" : "application/x-javascript",
								        "Content-Type": "application/x-javascript"
								},						
								"accepts" : {
									jsonp: "application/x-javascript",
									json : "application/json"
								},
								*/
								"contentType" : "application/json",
								"success" : function(json) {
									try {
										$('#description').text(json['query']['summary']);
									} catch (err) { $('#description').text('');}
									_ambit['search']['result']=json;
									identifiers(json);
									fnCallback(json);
								},
								"cache" : false,
								"error" : function(xhr, textStatus, error) {
									oSettings.oApi._fnProcessingDisplay(
											oSettings, false);
								}
							});
						},
						"oLanguage" : {
							"sProcessing" : "<img src='images/24x24_ambit.gif' border='0'>",
							"sLoadingRecords" : "No records found."  
						},
						"fnRowCallback" : function(nRow, aData, iDisplayIndex) {
			              //  $('td:eq(2)', nRow).html(iDisplayIndex +1);

			                // retrieve identifiers
							id_uri = query_service
									+ "/query/compound/url/all?search="
									+ encodeURIComponent(aData.compound.URI)
									+ "?max=1&media=application%2Fjson";
							$.ajax({
										dataType : "json",
										url : id_uri,
										success : function(data, status, xhr) {
											identifiers(data,aData);
											$.each(data.dataEntry,function(index,entry) {
														aData.compound.cas = formatValues(entry,"cas");																
														aData.compound.name = formatValues(entry,"names");																
														$('td:eq(3)',nRow).html(aData.compound.name);
														$('td:eq(1)',nRow).html(aData.compound.cas);
														aData.compound['smiles'] = formatValues(entry,"smiles");
														var offset = similarity?1:0;
														$('td:eq(' + (4 + offset) + ')',nRow)
																.html(aData.compound['smiles']);
														aData.compound['inchi'] = formatValues(entry,"inchi");
														$('td:eq(' + (5 + offset) + ')',nRow)
																.html(aData.compound['inchi']);
														aData.compound['inchikey'] = formatValues(
																entry,"inchikey");
														$('td:eq(' + (5 + offset) + ')',nRow)
																.html(aData.compound['inchikey']);
													});
											
											//datasets
											var features_uri = "";
											$.each(_ambit.datasets,function(index,dataset) {
												if (jQuery.inArray(dataset.URI,_ambit.selectedDatasets)>=0) {
													var dataset_uri = dataset.URI + '/feature';
													var feature_uri =
																'&feature_uris[]=' + 
																((dataset_uri.indexOf('http')==0)?
																encodeURIComponent(dataset_uri):
																encodeURIComponent(query_service+dataset_uri));	
													features_uri += feature_uri;
												}
											});
											$.each(_ambit.models,function(index,model) {
												if (jQuery.inArray(model.URI,_ambit.selectedModels)>=0) {													
													var feature_uri =
																'&feature_uris[]=' + 
																((model.predicted.indexOf('http')==0)?
																encodeURIComponent(model.predicted):
																encodeURIComponent(query_service+model.predicted));	
													features_uri += feature_uri;
												}
											});
											
											var dataset_uri = aData.compound.URI + "?media=application%2Fjson" + features_uri;
											$.ajax({
												dataType : "json",
												url : dataset_uri,
												success : function(data1, status, xhr) {
													identifiers(data1,aData);
												},
												error : function(xhr, status, err) {},complete : function(xhr, status) {}
											});											
										},
										error : function(xhr, status, err) {
										},
										complete : function(xhr, status) {
										}
									});

						}
					});
	
	$('#structures tbody td .zoomstruc img').live(
			'click',
			function() {
				var nTr = $(this).parents('tr')[0];
				if (oTable.fnIsOpen(nTr)) {
					this.src = query_service + "/images/zoom_in.png";
					this.alt = "Zoom in";
					this.title='Click to show compound details';
					oTable.fnClose(nTr);
				} else {
				    this.alt = "Zoom out";
					this.src = query_service + "/images/zoom_out.png";
					this.title='Click to close compound details panel';
					var id = 'values'+getID();
					oTable.fnOpen(nTr, fnFormatDetails(nTr,id),
							'details');
					
				       $('#'+ id).dataTable({
				    		'bJQueryUI': true, 
				    		'bPaginate': false,
				    		'bAutoWidth': true,
							"sScrollY": "200px",
							//"sScrollXInner": "110%",
							"bScrollCollapse": true,
							"sWidth": "90%",
							"sHeight": "10em",
				    		"sDom": 'T<"clear"><"fg-toolbar ui-helper-clearfix"lfr>t<"fg-toolbar ui-helper-clearfix"ip>',
				    		"aaSorting" : [ [ 0, 'desc' ], [ 1, 'asc' ] ],
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
				}
			});
	
	function fnFormatDetails(nTr, id) {
		var dataEntry = oTable.fnGetData(nTr);
		var sOut = '<div class="ui-widget details" style="margin-top: 5x;" >';
		sOut += '<div">';
		sOut += '<table width="100%" bgcolor="#fafafa" border="2"><thead><th>Type</th><th>Name</th><th>Value</th><th>Endpoint</th></thead><tbody>';//outer table, can't get the style right with divs
		/*
		$.each(dataEntry.lookup.cas, function(k, value) {
			sOut += renderValue(null,"[RN]","CAS RN","",dataEntry.values[value],"");
		});
		*/
		$.each(dataEntry.lookup.einecs, function(k, value) {
			sOut += renderValue(null,"[RN]","EC","",dataEntry.values[value],"");
		});	
		$.each(dataEntry.lookup.inchi, function(k, value) {
			sOut += renderValue(null,"[Struc]","InChI","",dataEntry.values[value],"");
		});		
		/*
		$.each(dataEntry.lookup.inchikey, function(k, value) {
			sOut += renderValue(null,"[Struc]","InChIKey","",dataEntry.values[value],"");
		});
		$.each(dataEntry.lookup.names, function(k, value) {
			sOut += renderValue(null,"[Name]","Name","",dataEntry.values[value],"");
		});
		*/
		$.each(dataEntry.lookup.reachdate, function(k, value) {
			sOut += renderValue(null,"[Data]","REACH date","",dataEntry.values[value],"");
		});
		$.each(dataEntry.lookup.smiles, function(k, value) {
			sOut += renderValue(null,"[Struc]","SMILES","",dataEntry.values[value],"");
		});
		$.each(dataEntry.lookup.misc, function(k, value) {
			var feature = _ambit.search.result.feature[value];
			sOut += renderValue(value,(feature.source.type=='Dataset')?"[Data]":"<a href='" + feature.source.URI +"' target=_blank>[Calc]</a>",
								feature.title,feature.units,dataEntry.values[value],feature.sameAs);
		});
		sOut += '</tbody></table></div></div>\n';		
		return sOut;
	}
	
	function renderValue(url, type, title, units, value, sameas) {
		if (value != undefined) {
			var endpoint = sameas.indexOf("http://www.opentox.org/echaEndpoints.owl#")>=0?sameas.replace("http://www.opentox.org/echaEndpoints.owl#",""):"";
			var sOut = "<tr bgcolor='#ffffff'><th>"+ (type==null?"":type) +"</th><th bgcolor='#fafafa'>" +
					(url==null?(title + " " + units):(title + " <i>" + units + "</i>  <a href='"+url+"' target='_blank'>?</a>" )) +
					"</th><td>" + value + "</td><td bgcolor='#fafafa'>"+
							endpoint 
							+ "</td></tr>";
			return sOut;
		} else return "";
	}
	return oTable;
}




function getID() {
	return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
}

function formatValues(dataEntry, tag) {
	var sOut = "";
	var delimiter = "";
	$.each(dataEntry.lookup[tag], function(index, value) {
		if (dataEntry.values[value] != undefined) {
			$.each(dataEntry.values[value].split("|"), function(index, v) {
				if (v.indexOf(".mol") == -1) {
					if ("" != v) {
						sOut += delimiter;
						sOut += v;
						delimiter = "<br>";
					}
				}
			});
			// sOut += dataEntry.values[value];
		}
	});
	return sOut;
}

function selecturi(value) {
	$('.selecturi').prop('checked', value);
}

/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012-2013, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 */
function defineStructuresTable(url, query_service, similarity,root) {
	var imgSize = 150;
	var oTable = $('#structures')
			.dataTable(
					{
						"aoColumnDefs" : [
								{ //0
									"aTargets": [ 0 ],	
									"sClass" : "center",
									"bSortable" : false,
									"mData" : null,
									sWidth : "24px",
									"bUseRendered" : "true",
									"fnRender" : function(o,val) {
										var uri = o.aData["compound"]["URI"];
										
										var prm = {'option': 'similarity', 'type':'url', 'search':uri};
										var searchURI = query_service + "/ui/_search?" + $.param(prm,false);
										prm = {'option': 'smarts', 'type':'url', 'search':uri, 'pagesize' : 10};
										var smartsURI = query_service + "/ui/_search?" + $.param(prm,false);
										
										var prm = {'option': 'url', 'type':'url', 'search':uri};
										var substance_uri = query_service + "/ui/_search?" + $.param(prm,false);
										
										return "<input class='selecturi' type='checkbox' checked name='uri[]' title='Select "+ 
													uri +"' value='"+uri+"'><br/>" +
													"<a href='"+uri+"' class='help' title='AMBIT Compound URI' target=_blank><span class='ui-icon ui-icon-link' style='float: left; margin-right: .1em;'></span></a> " +
													"<a href='"+searchURI+"' class='help' title='Find similar'><span class='ui-icon ui-icon-heart' style='float: left; margin: .1em;' title='Find similar chemical structures'></span></a> " +
													"<a href='"+smartsURI+"' class='help' title='Find substructure'><span class='ui-icon ui-icon-search' style='float: left; margin: .1em;' title='Substructure search with this chemical structure'></span></a> " +
													"<a href='"+substance_uri+"' class='help' title='Find substances'><span class='ui-icon ui-icon-search' style='float: left; margin: .1em;' title='Substances containing this chemical structure'></span></a> " +
													"<span class='ui-icon ui-icon-folder-collapsed zoomstruc' style='float: left; margin: .1em;' title='Click to show compound details'></span>";
									}
								},						                  
								{
									"mDataProp" : "compound.cas",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 1 ],
									"bSearchable" : true,
									"bSortable" : true,
									"sWidth" : "8%",
									"bUseRendered" : false,
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
									"mDataProp" : "compound.einecs",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 2 ],
									"bSearchable" : true,
									"bSortable" : true,
									"sWidth" : "7%",
									"bUseRendered" : false,
									"sClass" : "cas",
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData, "einecs");
										else
											return val;
									},
									"bVisible" : true
								},								
								{
									"mDataProp" : "compound.URI",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 3 ],
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
										
										var prm = {'option': 'auto', 'type':'url', 'search':cmpURI, 'pagesize': 1};
										var searchURI = query_service + "/ui/query?" + $.param(prm,false);
										
										cmpURI = cmpURI + "?media=image/png";
										return '<a href="'+searchURI+'" >' +
											   '<img class="ui-widget-content" title="Click to display this compound only" border="0" src="'
												+ cmpURI + '&w='+imgSize+'&h='+imgSize+'" onError="this.style.display=\'none\'">' 
												+ "</a>";
									}
								},
								{
									"mDataProp" : "compound.name",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 4 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : false,
									"sClass" : "names wrap",
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
									"aTargets" : [ 5 ],
									"sTitle" : "Similarity",
									"sClass" : "similarity",
									"bSearchable" : true,
									"bSortable" : true,
									"sWidth" : "5%",
									"bVisible" : similarity
								},
								{
									"mData" : null,
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [6 ],
									"bSearchable" : true,
									"sWidth" : "10%",
									"bSortable" : true,
									"bUseRendered" : true,
									"fnRender" : function(o, val) {
										var smiles = val;
										if ((val === undefined) || (val == "")) {
											smiles = formatValues(o.aData,"smiles");
										} 
										return smiles;
									},
									"bVisible" : true
								},
								{
									"mData" : null,
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 7 ],
									"bSearchable" : true,
									"bSortable" : true,
									"sWidth" : "10%",
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
									"mData" : null,
									"asSorting" : [ "asc", "desc" ],
									"sClass" : "inchikey",
									"aTargets" : [ 8 ],
									"bSearchable" : true,
									"sWidth" : "10%",
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
								"contentType" : "application/json",
								"success" : function(json) {
									try {
										$('#description').text(json['query']['summary']);
									} catch (err) { $('#description').text('');}
									_ambit['search']['result']=json;
									identifiers(json);
									fnCallback(json);
								},
								"cache" : true,
								"error" : function(xhr, textStatus, error) {
									switch (xhr.status) {
									case 403: {
							        	alert("Restricted data access. You are not authorized to access the requested data.");
										break;
									}
									case 404: {
										//not found
										break;
									}
									default: {
							        	alert("Error loading data " + xhr.status + " " + error);
									}
									}
									oSettings.oApi._fnProcessingDisplay(oSettings, false);
								}
							});
						},
						"oLanguage" : {
							"sProcessing" : "<img src='" + root + "/images/24x24_ambit.gif' border='0'>",
							"sLoadingRecords" : "No structures found.",
							"sEmptyTable" : "No structures found.",
				            "sInfo": "Showing _TOTAL_ structures (_START_ to _END_)",
				            "sLengthMenu": 'Display <select>' +
				              '<option value="10">10</option>' +
				              '<option value="20">20</option>' +
				              '<option value="50">50</option>' +
				              '<option value="100">100</option>' +
				              '<option value="-1">all</option>' +
				              '</select> structures.'	   				            
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
														aData.compound.einecs = formatValues(entry,"einecs");	
														aData.compound.name = formatValues(entry,"names");																
														$('td:eq(4)',nRow).html(aData.compound.name);
														$('td:eq(1)',nRow).html(aData.compound.cas);
														$('td:eq(2)',nRow).html(aData.compound.einecs);
														aData.compound['smiles'] = formatValues(entry,"smiles");
														var offset = similarity?1:0;
														$('td:eq(' + (5 + offset) + ')',nRow)
																.html(aData.compound['smiles']);
														aData.compound['inchi'] = formatValues(entry,"inchi");
														$('td:eq(' + (6 + offset) + ')',nRow)
																.html(aData.compound['inchi']);
														aData.compound['inchikey'] = formatValues(
																entry,"inchikey");
														$('td:eq(' + (6 + offset) + ')',nRow)
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
											var cmpURI = aData.compound.URI;
											if (cmpURI.indexOf("/conformer") >= 0) {
												cmpURI = aData.compound.URI.substring(0, aData.compound.URI.indexOf("/conformer"));
											}
											if ((_ambit["data_uri"] == null) || (_ambit["data_uri"]===undefined)) 
												downloadFormUpdate(features_uri);
											
											_ambit["data_uri"] = features_uri;
											
											var dataset_uri = cmpURI + "?media=application%2Fjson" + features_uri;
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
											//console.log(status + err);
										},
										complete : function(xhr, status) {
										}
									});

						}
					});
	
	$('#structures tbody').on('click','td .zoomstruc',
			function() {
				var nTr = $(this).parents('tr')[0];
				if (oTable.fnIsOpen(nTr)) {
					$(this).removeClass("ui-icon-folder-open");
					$(this).addClass("ui-icon-folder-collapsed");
					this.title='Click to show compound details';
					oTable.fnClose(nTr);					
				} else {
					$(this).removeClass("ui-icon-folder-collapsed");
					$(this).addClass("ui-icon-folder-open");
					this.title='Click to close compound details panel';
					var id = 'values'+getID();
					var newtable = fnFormatDetails(nTr,id);
					oTable.fnOpen(nTr, newtable,'details');
					
					dataEntry = oTable.fnGetData(nTr);
					if (dataEntry!=null) {
						var cmpUri = root+"/substance?media="+encodeURIComponent("application/json") + "&compound_uri=" + encodeURIComponent(dataEntry.compound.URI);
	
						substance.defineSubstanceTable(root,cmpUri,("#c"+id),false,'Trt','Trt',false);
					}
					
					
				       $('.'+ id).dataTable({
				    		'bJQueryUI': false, 
				    		'bPaginate': false,
				    		'bAutoWidth': true,
							"sScrollY": "200px",
							//"sScrollXInner": "110%",
							"bScrollCollapse": true,
							"sWidth": "90%",
							//"sHeight": "10em"
				    		"sDom": 'T<"clear"><"fg-toolbar ui-helper-clearfix"r>t<"fg-toolbar ui-helper-clearfix"p>',
				    		"aaSorting" : [ [ 0, 'asc' ], [ 4, 'asc' ] ],
				    		fnDrawCallback: function() {
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
				       $('#_tabs'+ id).tabs();
				}
			});
	
	function fnFormatDetails(nTr, id) {
		var dataEntry = oTable.fnGetData(nTr);
		var sOut = ""; //'<div class="ui-widget details" style="margin-top: 5x;" >';
		sOut += '<div id="_tabs'+id+'" class="details" style="margin-top: 5x;" >';
		sOut += '<ul>';
		sOut += '<li><a href="#tabs-id">Identifiers</a></li>'+
	        	'<li><a href="#tabs-data">Data</a></li>'+	
	        	'<li><a href="#tabs-predictions">Predictions</a></li>'+
	        	'<li><a href="#tabs-composition">Composition</a></li>'+
	        	'</ul>\n';
		
		sOut += '<div id="tabs-id">';
		sOut += '<table class="'+id+'" width="100%" bgcolor="#fafafa" border="2"><thead><tr><th>Name</th><th>Value</th></tr></thead><tbody>';

		/*
		$.each(dataEntry.lookup.inchikey, function(k, value) {
			sOut += renderValue(null,"[Struc]","InChIKey","",dataEntry.values[value],"");
		});
		*/
		var sOutData = '<div id="tabs-data">';
		sOutData += '\n<table class="'+id+'" width="100%" bgcolor="#fafafa" border="2"><thead><tr><th>Data source</th><th>Property</th><th>Value</th><th>Endpoint</th></tr></thead><tbody>';

		var sOutCalc = '<div id="tabs-predictions">';
		sOutCalc += '\n<table class="'+id+'" width="100%" bgcolor="#fafafa" border="2"><thead><tr><th>Prediction</th><th>Property</th><th>Value</th><th>Endpoint</th></tr></thead><tbody>';

		var sOutComposition = '<div id="tabs-composition">';
		sOutComposition += '\n<table id="c'+id+'" class="jtox-toolkit dataTable" width="100%" bgcolor="#fafafa" border="2"><thead><tr><th></th><th>Substance Name</th><th>Substance UUID</th><th>Type Substance</th><th>Public name</th><th>Reference substance UUID</th><th>Owner</th><th>Study</th><th>&nbsp;</th></tr></thead><tbody></tbody></table></div>';
		/*
		sOutComposition += '\n<table id="c'+id+'" width="100%" bgcolor="#fafafa" border="2"><thead>';
		sOutComposition += '<tr><th>Public name</th><th>Reference substance UUID</th></tr>';
		sOutComposition += '</thead><tbody></tbody></table></div>';
		*/
		if ((dataEntry===undefined) || (dataEntry==null)) {
			
		} else {
			sOut += renderIdentifiers(null,"CAS",dataEntry.lookup.cas,dataEntry);
			sOut += renderIdentifiers(null,"EC",dataEntry.lookup.einecs,dataEntry);
			sOut += renderIdentifiers(null,"Name",dataEntry.lookup.names,dataEntry);
			sOut += renderIdentifiers(null,"Trade name",dataEntry.lookup.tradenames,dataEntry);
			sOut += renderIdentifiers(null,"REACH date",dataEntry.lookup.reachdate,dataEntry);
			sOut += renderIdentifiers(null,"SMILES",dataEntry.lookup.smiles,dataEntry);
			sOut += renderIdentifiers(null,"IUCLID5 UUID",dataEntry.lookup.i5uuid,dataEntry);
			sOut += renderIdentifiers(null,"InChI",dataEntry.lookup.inchi,dataEntry);
			
			$.each(dataEntry.lookup.misc, function(k, value) {
				var feature = _ambit.search.result.feature[value];
				switch (feature.source.type) {
				case 'Dataset': {
					sOutData += renderValue(value,feature.title,feature.units,dataEntry.values[value],feature.sameAs,feature.source);
					break;
				}
				default: {
					sOutCalc += renderValue(value,feature.title,feature.units,dataEntry.values[value],feature.sameAs,feature.source);
					break;
				}	
				}
			});
		}

		sOut += '</tbody></table></div>\n';
		

		sOutData += '</tbody></table></div>\n';
		sOutCalc += '</tbody></table></div>\n';
		sOut += sOutData + sOutCalc + sOutComposition + '</div>\n';
		//sOut += '</div>\n';
		return sOut;
	}
	
	function renderIdentifiers(url,title,lookup,dataEntry) {
		var sOut = "";
		var cache = {};
		$.each(lookup, function(k, value) {
			if ((dataEntry.values[value]===undefined)||
				(dataEntry.values[value]==null)||
				(dataEntry.values[value]=='')||
				(dataEntry.values[value]=='.')) return;
			var lv = dataEntry.values[value];
			
			try  { lv = lv.toLowerCase();} catch (e) {}
			if (cache[lv]==null) {
				if ("Trade name"==title) {
					$.each(dataEntry.values[value].split("|"), function(index, v) {
						var t = title;
						if ((/^TRA/).test(v)) t = "Trade identifier";
						sOut += renderValue(url,t,"",v,"",null);
					});
					
				} else
					sOut += renderValue(url,title,"",dataEntry.values[value],"",null);
				cache[lv] = true;
			} 
		});		
		return sOut;
	}
	
	function renderValue(url, title, units, value, sameas, source) {
		if (value != undefined) {
			
			if (url==null) {  //name & value only
				return "<tr bgcolor='#ffffff'><th bgcolor='#fafafa'>" +
						(title + " " + units) +
						"</th><td><span class='wrap'>" + value + "</span></td></tr>";
			}
			
			var src = source != null?source.type:"";
			if ((source != null) && (source.type=='Dataset')){ 
				src = source.type;
				var p1 = source.URI.lastIndexOf("/");
				var p2 = source.URI.lastIndexOf(".");
				if (p1>=0) src =  unescape(source.URI.substring(p1+1,p2>=0?p2:undefined)).replace(/\+/g,' ');
			}
			var prefix = "http://www.opentox.org/echaEndpoints.owl#";
			var searchURI = "";
			var endpoint =  sameas==null?"":sameas;
			if (sameas.indexOf(prefix)>=0) {
				searchURI = " <a class='qxternal' href='https://apps.ideaconsult.net/ontology/query?uri="+
				encodeURIComponent(sameas) +
				"' target=_blank><span class='ui-icon ui-icon-link' style='float: left; margin-right: .3em;'></span></a>";
				endpoint = sameas.replace(prefix,"");
			} 
			var sOut = "<tr bgcolor='#ffffff'>" 
					+ "<td>" 
					+ (source==null?"":"<a href='"+source.URI+"' target=_blank class='help' title='"+source.URI+"'>" + src +"</a>") 
					+ "</td>" 
					+ "<th bgcolor='#fafafa'>" 
					+ (url==null?(title + " " + units):(title + " <i>" + units + "</i>  <a href='"+url+"' target='_blank'><span class='ui-icon ui-icon-link' style='float: left; margin-right: .3em;'></span></a>" )) 
					+ "</th><td>" + value + "</td><td bgcolor='#fafafa'>"
					+ endpoint + searchURI + "</td></tr>";
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
	var cache = {};
	$.each(dataEntry.lookup[tag], function(index, value) {
		var vals = dataEntry.values[value];
		if ((vals != undefined) && (vals!=null) && !(""==vals)) {
			try {
				$.each(vals.split("|"), function(index, v) {
					if ($.inArray(v,".mol") == -1) {
						if ("" != v) {
							var lv = v.toLowerCase();
							if (cache[lv]==null) {
								sOut += delimiter;
								sOut += v;
								delimiter = "<br>";
								cache[lv] = true;
							}
						}
					}
				});
			} catch (e) {
				var lv = vals;
				if (cache[lv]==null) {
					sOut += delimiter;
					sOut += vals;
					delimiter = "<br>";
					cache[lv] = true;	
				}
			}
			// sOut += dataEntry.values[value];
		}
	});
	cache = null;
	switch (tag) {
	case 'smiles': {
		if (sOut.length>20) 
			return "<span title='"+sOut+"'>" + sOut.substring(0,20)+ " ...</span>";
		break;
	}
	case 'inchikey' : {
		if (sOut!="" )
			sOut = "<a class='help' class='qxternal' href='http://www.google.com/search?q="+sOut + "' target=_blank>"+sOut+"</a>";
		break;
	}
	}
	return sOut;
	
}

function selecturi(value) {
	$('.selecturi').prop('checked', value);
}

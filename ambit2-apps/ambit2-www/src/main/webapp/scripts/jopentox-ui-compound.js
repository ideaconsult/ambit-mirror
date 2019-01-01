/**
 * jOpenToxUI JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012, IDEAconsult Ltd. http://www.ideaconsult.net/
 * 
 * TODO: Licence.
 */


var opentox = {
	"URI": null,
	"dataEntry" : [],
	"feature":{},
	"imageInTable": true,
	"showProperties" : false,
	"showRegistry" : true,
	"showSMILES" : false,
	"showInChI" : false,
	"showEndpoints" : true,
	"showCalculated" : true,
	"showNames" : true,
	"showSimilarity" : false,
	/*
showRegistry,registry
showSMILES,smiles
showInChI,inchi
showProperties,property
showEndpoints,endpoint
showCalculated,calculated
showNames,name
	 */
	"oTable":null
};

/**
 * Models rendering
 */
$(document)
		.ready(
				function() {
					try {
						if (baseref===undefined) baseref = "/ambit2";
					} catch (err) { baseref="/ambit2"; }
					//$.fn.dataTableExt.sErrMode = 'throw';
					//images
					opentox["imageInTable"] = true;
					/* Initialize */
					var columnDefs = [
										{ //0
											"aTargets": [ 0 ],	
											"sClass" : "center",
											"bSortable" : false,
											"mData" : null,
											sWidth : "16px",
											"bUseRendered" : "true",
											"fnRender" : function(o,
													val) {
												return "<span class='zoomstruc'><img  src='"+ baseref +"/images/zoom_in.png' alt='zoom in' title='Click to show compound details'></span>";
											}
										},
										{ //1
											"aTargets": [ 1 ],	
											"sClass" : "left",
											"bSortable" : true,
											"mData" : null,
											sWidth : "24px",
											"bUseRendered" : "true",
											"fnRender" : function(o,
													val) {
												return (o.iDataRow+1)+"." ;
											}
										},	
										{ //2
											"aTargets": [ 2 ],
											"sClass" : "left",
											"bSortable" : true,
											"bSearchable" : true,
											"mDataProp" : "compound.URI",
											"bVisible": opentox.imageInTable,
											sWidth : "150px",
											
											"bUseRendered" : false,
											"fnRender" : function(o,
													val) {
												var cmpURI = val;
												if (val.indexOf("/conformer")>=0) {
													cmpURI = val.substring(0,val.indexOf("/conformer"));
												}								
												if ((opentox["model_uri"]==null) || (opentox["model_uri"] == undefined)) {
													cmpURI = cmpURI + "?media=image/png";
												} else {
													cmpURI = opentox["model_uri"] + "?dataset_uri=" + cmpURI + "&media=image/png";
												}
												return '<a href="'+val+'" title="'+cmpURI+'"><img src="'+cmpURI+'&w=150&h=150" border="0"></a>';
											}
										},													
										{ //3
											"aTargets": [ 3 ],
											"sClass" : "left registry",
											"sTitle" : "CAS",
											"mDataProp" : "compound.cas",
											"bUseRendered" : "true",
											"bSortable" : true,
											"bSearchable" : true,
											"bVisible": false,
											"fnRender" : function(o,val) {
												if (opentox.cas.length>0) {
													var name = opentox.dataEntry[o.iDataRow].values[opentox.cas[0]] ;
													if (name !== undefined) return name;
												}
												return null;
											}
										},	
										{//4
											"aTargets": [ 4 ],	
											"sClass" : "left registry",
											"sTitle" : "EINECS",
											"mDataProp" :  "compound.einecs",
											"bUseRendered" : "true",
											"bVisible": false,
											"bSortable" : true,
											"bSearchable" : true,
											"fnRender" : function(o,val) {
												if (opentox.einecs.length>0) {
													var name = opentox.dataEntry[o.iDataRow].values[opentox.einecs[0]] ;
													if (name !== undefined) return name;
												}
												return null;
											}
										},													
										{//5
											"aTargets": [ 5 ],
											"sClass" : "left name",
											"sTitle" : "Name",
											"mDataProp" : "compound.name",
											"bUseRendered" : "true",
											"bVisible": false,
											"bSortable" : true,
											"bSearchable" : true,
											"fnRender" : function(o,val) {
												var maxlen = 300;
												if (opentox.names.length>0) {
													var name = opentox.dataEntry[o.iDataRow].values[opentox.names[0]] ;
													if (name !== undefined) {
														if (name.length>maxlen) {
															var id = "r"+o.iDataRow + "c5";
															return '<a href="#" title="'
																	+ name
																	+'" onClick="javascript:toggleDiv(\''+id+'\');">' 
																	+ name.substring(0,maxlen) + 
																	'</a><span id="' + id + '"  style="display: none;">'+name.substring(maxlen)+'</span>';
														} else return name;
													}
												}
												return null;
											}
										},
										{//6
											"aTargets": [ 6 ],	
											"sTitle" : "REACH registration date",
											"sClass" : "left registry",
											"mData" : null,
											"bUseRendered" : "false",
											"bVisible": false,
											"bSortable" : true,
											"fnRender" : function(o,val) {
												if (opentox.reachdate.length>0) {
													var name = opentox.dataEntry[o.iDataRow].values[opentox.reachdate[0]] ;
													if (name !== undefined) return name;
												}
												return null;
											}
										}
										
								];
					//find identifiers and adds property columns
					identifiers(opentox,columnDefs);

					
					/* Initialize columns */
					opentox["oTable"] = $(".compoundtable")
							.dataTable(
									{
										'bProcessing' : true,
										'bJQueryUI' : true,
										'bAutoWidth': true,
										"bSaveState": false,    // Save viewstate in cookie
										"sCookiePrefix":"OTDATA_",
										"sScrollX": "100%",
										//"sScrollXInner": "110%",
										"bScrollCollapse": true,
										"aaData" : opentox.dataEntry,
										"aoColumnDefs" : columnDefs,
										"aaSorting" : [ [ 1, 'asc' ] ],
										"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
										"sSearch": "Filter:",
										"bSearchable": true,
										"bPaginate" : true,
										"sPaginationType": "full_numbers",
										"sPaginate" : ".dataTables_paginate _paging",										
										"oLanguage": {
								            "sLoadingRecords": "No structures found.",
								            "sZeroRecords": "No structures found.",
								            "sInfo": "Showing _TOTAL_ structures (_START_ to _END_)",
								            "sLengthMenu": 'Display <select>' +
							              '<option value="10">10</option>' +
							              '<option value="20">20</option>' +
							              '<option value="50">50</option>' +
							              '<option value="100">100</option>' +
							              '<option value="-1">all</option>' +
							              '</select> entries.'	            
								    }
									});
					
					$('.compoundtable tbody').on('click','td .zoomstruc img',
							function() {
								var nTr = $(this).parents('tr')[0];
								if (opentox.oTable.fnIsOpen(nTr)) {
									this.src = baseref + "/images/zoom_in.png";
									this.alt = "Zoom in";
									this.title='Click to show compound details';
									opentox.oTable.fnClose(nTr);
								} else {
								    this.alt = "Zoom out";
									this.src = baseref + "/images/zoom_out.png";
									this.title='Click to close compound details panel';
									var id = 'values'+getID();
									opentox.oTable.fnOpen(nTr, fnFormatDetails(nTr,id),
											'details');
									
								       $('#'+ id).dataTable({
								    		'bJQueryUI': false, 
								    		'bPaginate': false,
								    		'bAutoWidth': true,
											"sScrollY": "200px",
											//"sScrollXInner": "110%",
											"bScrollCollapse": true,
											"sWidth": "90%",
								    		"sDom": 'T<"clear"><"fg-toolbar ui-helper-clearfix"lfr>t<"fg-toolbar ui-helper-clearfix"ip>',
								    		"aaSorting" : [ [ 0, 'desc' ] ],
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

				       
					/* Formating function for row details */
					function fnFormatDetails(nTr, id) {
						var dataEntry = opentox.oTable.fnGetData(nTr);
						var sOut = '<div class="ui-widget" style="margin-top: 5x;" >';
						sOut += '<div style="min-height:250px">';
						
						sOut += '<table width="100%"  style="min-height:250px"><tbody><tr>';//outer table, can't get the style right with divs

						//structure
						sOut += '<td  valign="top" style="min-height:250px;max-width:260px">';
						sOut += '<a href="'+dataEntry.compound.URI+'">';
						
						var cmpURI = dataEntry.compound.URI;
						if (dataEntry.compound.URI.indexOf("/conformer")>=0) {
							cmpURI = dataEntry.compound.URI.substring(0,dataEntry.compound.URI.indexOf("/conformer"));
						}	
						sOut += '<img class="ui-widget-content ui-corner-top ui-corner-bottom" style="min-height:250px;min-width:250px" src="' 
									+ cmpURI + '?media=image/png" title="'+cmpURI+'" border="0">';
						sOut += '</a><br>';
						
						var identifier = false;
						//names
					    $.each(opentox.feature, function(k, value) {
						        if (value.sameAs == "http://www.opentox.org/api/1.1#ChemicalName") {
						        	if (dataEntry.values[k]) {
						        		sOut += '<strong>' + dataEntry.values[k] + '</strong>';
						        		sOut += '<br>';
						        		identifier=true;
						        	}
						        };   
						    });
						//cas
					    if (!identifier)
					    $.each(opentox.feature, function(k, value) {
						        if (value.sameAs == "http://www.opentox.org/api/1.1#CASRN") {
						        	if (dataEntry.values[k]) {
						        		sOut += '<strong>CAS RN</strong> ' + dataEntry.values[k];
						        		sOut += '<br>';
						        		identifier=true;
						        	}
						        };   
						    });
						//einecs
					    if (!identifier)
					    $.each(opentox.feature, function(k, value) {
						        if (value.sameAs == "http://www.opentox.org/api/1.1#EINECS") {
						        	if (dataEntry.values[k]) {
						        		sOut += '<strong>EINECS</strong> ' + dataEntry.values[k];
						        		sOut += '<br>';
						        		identifier=true;
						        	}
						        };   
						    });
					    
					    
						//reach
					    if (!identifier)
					    $.each(opentox.feature, function(k, value) {
						        if (value.sameAs == "http://www.opentox.org/api/1.1#REACHRegistrationDate") {
						        	if (dataEntry.values[k]) {
						        		sOut += '<strong>REACH registration date:</strong> ' + dataEntry.values[k];
						        		sOut += '<br>';
						        		identifier=true;
						        	}
						        };   
						    });
					    
						sOut += '</td>';
						
						//properties
						sOut += '<td>';
						sOut += '<table id="'+ id +'" class="values" >';
						sOut += '<thead><tr><th>Type</th><th>Calculated</th><th>Property</th><th>Value</th></tr></thead>';
						sOut += '<tbody>';
						for (key in dataEntry.values) 
							if (dataEntry.values[key]) {
								sOut += '<tr>';

								//type (sameas)
								var sameAs = opentox.feature[key]["sameAs"]; 	
								sOut += '<td title="Same as the OpenTox ontology entry defined by '+sameAs+'">';
								if (sameAs.indexOf("http")>=0) {
									var hash = sameAs.lastIndexOf("#");
									if (hash>0) sOut += sameAs.substring(hash+1).replace("_"," ").substring(0,30);
									else sOut += sameAs.substring(0,30);
								}
								sOut += '</td>';
								//calculated
								var source = opentox.feature[key]["source"]["type"];
								var hint = "Imported from a dataset";
								if (source=="Algorithm" || source=="Model") {
									hint = "Calculated by " + source;
									source = '<a href="'+opentox.feature[key]["source"]["URI"]+'">Yes</a>';
							    } else source="";
			
								sOut += '<td title="'+hint+'">';					
								sOut += source;
								sOut += '</td>';
			
								//name, units
								sOut += '<td title="OpenTox Feature URI: '+key+'">';
								sOut += '<a href="' + key + '">';
								sOut += opentox.feature[key]["title"];
								sOut += '</a>';
								sOut += opentox.feature[key]["units"];
								sOut += '</td>';
								
								hint = 'Numeric:&nbsp;'+opentox.feature[key]["isNumeric"];
								hint += '\nNominal:&nbsp;'+opentox.feature[key]["isNominal"];
								hint += '\nSource:&nbsp;'+opentox.feature[key]["source"]["URI"];
								hint += '\nSource type:&nbsp;'+opentox.feature[key]["source"]["type"];
								sOut += '<td title="' + hint + '">';
								//for handling the broken Toxtree html output...
								sOut += decodeEntities(dataEntry.values[key]);
								
								sOut += '</td>';
								sOut += '</tr>\n';
						}
						sOut += '</tbody></table>\n';							
						sOut += '</td>';
						

						//outer table
						sOut += '</tr></tbody></table>';
						//sOut += '</div>\n';//widget header
						sOut += '</div>\n';
						
						return sOut;
					}
					
					function getID() {
						   return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
						}
					
				});
/**
 *  Input: 
 *  opentox object, with opentox.feature array nonempty
 */
function identifiers(opentox, columnDefs) {
	if (opentox.cas==null) opentox.cas = [];
	if (opentox.names==null) opentox.names = [];
	if (opentox.einecs==null) opentox.einecs = [];
	if (opentox.reachdate==null) opentox.reachdate = [];
	if (opentox.i5uuid==null) opentox.i5uuid = [];
	//names
	var count = [0,0,0,0,6,0];
	//opentox['feature'].sort(function(a,b){return a['order'] -b['order']});
	
    $.each(opentox.feature, function(k, value) {
    		if (value.sameAs == "http://www.opentox.org/api/1.1#IUPACName") {
    			if (opentox.feature[k]) { opentox.names.push(k); count[0]++; columnDefs[5].bVisible=true;}
    		} else if (value.sameAs == "http://www.opentox.org/api/1.1#ChemicalName") {
	        	if (opentox.feature[k]) { opentox.names.push(k); count[0]++; columnDefs[5].bVisible=true;}
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#CASRN") { 
	        	if (opentox.feature[k]) { opentox.cas.push(k); count[1]++;  columnDefs[3].bVisible=true; }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#EINECS") { 
	        	if (opentox.feature[k]) {opentox.einecs.push(k); count[2]++; columnDefs[4].bVisible=true; }	        
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#REACHRegistrationDate") { 
	        	if (opentox.feature[k]) {opentox.reachdate.push(k); count[3]++;  columnDefs[6].bVisible=true;}
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#IUCLID5_UUID") { 
	        	if (opentox.feature[k]) {opentox.i5uuid.push(k);  count[5]++; }	        	
	        } else {
	        	//console.log(k);
	        	count[4]++;
	        	if (count[4]<=500) { //TODO check how it works with many columns
		        	var thclass = "property";
		        	var visible = false;
		        	if (value.sameAs == "http://www.opentox.org/api/1.1#SMILES") { thclass = " smiles"; visible = false; }
		        	else if (value.sameAs == "http://www.opentox.org/api/1.1#InChI") { thclass = " inchi"; visible = false; }
		        	else if (value.sameAs.indexOf("http://www.opentox.org/echaEndpoints.owl")>=0) { thclass += " endpoint"; visible = opentox.showEndpoints; }	
					var source = opentox.feature[k]["source"]["type"];
					if (source=="Algorithm" || source=="Model") { thclass += " calculated"; visible |= opentox.showCalculated; }	
	
					columnDefs.push(
							{
								"sClass" : k + thclass,
								"aTargets": [ count[4] ],	
								"sTitle" : value.title.replace("_"," ") + " " + value.units,
								"mData" : null, //TODO upgrade to 1.9 and use function here
								"bUseRendered" : "true",
								"bVisible": visible,
								"bSortable" : true,
								"fnRender" : function(o,val) {
									//try {
										if (opentox.dataEntry[o.iDataRow].values[k] !== undefined) {
											var cellClass = "";
											if (isNaN(opentox.dataEntry[o.iDataRow].values[k])) {
												if (opentox.dataEntry[o.iDataRow].values[k].length>30) {
													var id = "r"+o.iDataRow + "c"+ count[4];
													return '<a href="#" title="'
															+ opentox.dataEntry[o.iDataRow].values[k]
															+'" onClick="javascript:toggleDiv(\''+id+'\');">' 
															+ opentox.dataEntry[o.iDataRow].values[k].substring(0,30) + 
															'</a><span id="' + id + '"  style="display: none;">'+opentox.dataEntry[o.iDataRow].values[k].substring(30)+'</span>';
												} else {
													var val = opentox.dataEntry[o.iDataRow].values[k].toLowerCase();
													if (val == "no") cellClass= " tag_no";
													else if (val == "yes") cellClass= " tag_yes";
													else if (val == "a") cellClass= " tag_green";
													else if (val == "active") cellClass= " tag_active";
													else if (val == "mutagen") cellClass= " tag_active";
													else if (val == "tumour") cellClass= " tag_active";
													else if (val.indexOf("increased")>=0) cellClass= " tag_yes";
													else if (val.indexOf("decreased")>=0) cellClass= " tag_no";
													else if (val == "carcinogen") cellClass= " tag_active";
													else if (val == "noncarcinogen") cellClass= " tag_inactive";
													else if (val == "nonmutagen") cellClass= " tag_inactive";
													else if (val == "inactive") cellClass= " tag_inactive";													
													return '<span class="'+ cellClass +'" title="'+opentox.dataEntry[o.iDataRow].values[k]+'">'+opentox.dataEntry[o.iDataRow].values[k]+'</span>'; //
												}
											} else
												return opentox.dataEntry[o.iDataRow].values[k];
										}
									//} catch (err) {
									//	console.log(err);
									//}
									return null;
								}
							}		
							);
	        	}
	        }
	        //be quick, we only need something to display in the table, detailed info will be on expand
	        //if (count[0]>0 && count[1]>0 && count[2]>0 && count[3]>0) 	return;
	    });
    //finally column for the similarity metric
    columnDefs.push(
		{ //last
			"sTitle" : "Similarity",
			"aTargets": [  count[4]+1 ],	
			"sClass" : "similarity",
			"bSortable" : true,
			"bVisible" : false,
			"mDataProp" : "compound.metric"
		});
}

function toggleImagesInTable(e) {
    var el = e.srcElement;
    opentox["imageInTable"] = el.checked;
   	opentox.oTable.fnSetColumnVis( 2, el.checked);

}

function showNames(e) {
    var el = e.srcElement;
    opentox["showNames"] = el.checked;
    
    $.each(opentox.oTable.fnSettings().aoColumns, function(k, value) {
    	if (value["sClass"].indexOf("name")>=0) {
    		 opentox.oTable.fnSetColumnVis( k, el.checked);
    	}
    });
}

function showRegistry(e) {
    var el = e.srcElement;
    opentox["showRegistry"] = el.checked;
    
    $.each(opentox.oTable.fnSettings().aoColumns, function(k, value) {
    	if (value["sClass"].indexOf("registry")>=0) {
    		 opentox.oTable.fnSetColumnVis( k, el.checked);
    	}
    });
}

function showSMILES(e) {
    var el = e.srcElement;
    opentox["showSMILES"] = el.checked;
    
    $.each(opentox.oTable.fnSettings().aoColumns, function(k, value) {
    	if (value["sClass"].indexOf("smiles")>=0) {
    		 opentox.oTable.fnSetColumnVis( k, el.checked);
    	}
    });
}


function showInChI(e) {
    var el = e.srcElement;
    opentox["showInChI"] = el.checked;
    
    $.each(opentox.oTable.fnSettings().aoColumns, function(k, value) {
    	if (value["sClass"].indexOf("inchi")>=0) {
    		 opentox.oTable.fnSetColumnVis( k, el.checked);
    	}
    });
}


function showProperties(e) {
    var el = e.srcElement;
    opentox["showProperties"] = el.checked;
    
    $.each(opentox.oTable.fnSettings().aoColumns, function(k, value) {
    	if (value["sClass"].indexOf("property")>=0) {
    		 opentox.oTable.fnSetColumnVis( k, el.checked);
    	}
    });
}

function showEndpoints(e) {
    var el = e.srcElement;
    opentox["showEndpoints"] = el.checked;
    
    $.each(opentox.oTable.fnSettings().aoColumns, function(k, value) {
    	if (value["sClass"].indexOf("endpoint")>=0) {
    		 opentox.oTable.fnSetColumnVis( k, el.checked);
    	}
    });
}

function showCalculated(e) {
    var el = e.srcElement;
    opentox["showCalculated"] = el.checked;
    
    $.each(opentox.oTable.fnSettings().aoColumns, function(k, value) {
    	if (value["sClass"].indexOf("calculated")>=0) {
    		 opentox.oTable.fnSetColumnVis( k, el.checked);
    	}
    });
}


function showSimilarity(e) {
    var el = e.srcElement;
    opentox["showSimilarity"] = el.checked;
    
    $.each(opentox.oTable.fnSettings().aoColumns, function(k, value) {
    	if (value["sClass"].indexOf("similarity")>=0) {
    		 opentox.oTable.fnSetColumnVis( k, el.checked);
    	}
    });
}
/**
 * HTML decode
 * @param input
 * @returns
 */
function decodeEntities(input) {
	  var y = document.createElement('textarea');
	  y.innerHTML = input;
	  return y.value;
}
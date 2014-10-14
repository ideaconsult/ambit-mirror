var substance = {
		data : {},
		"defineSubstanceTable" : function (root,url,selector,jQueryUI,dom,compositionDom,removeVisible) {
			var oTable = $(selector).dataTable( {
				"sAjaxDataProp" : "substance",
				"sAjaxSource": url,	
				"bJQueryUI" : jQueryUI,
				"bSearchable": true,
				"bProcessing" : true,
				"sDom" : (dom==null)?'<"help remove-bottom"lf><"help">Trt<"help"ip>':dom,
				"sSearch": "",
				"bPaginate" : true,
				"sPaginationType": "full_numbers",
				"sPaginate" : ".dataTables_paginate _paging",
				"oLanguage": {
			            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
			            "sLoadingRecords": "No substances found.",
			            "sZeroRecords": "No substances found.",
			            "sEmptyTable": "No substances available.",
			            "sInfo": "Showing _TOTAL_ substance(s) (_START_ to _END_)",
			            "sLengthMenu": 'Display <select>' +
			          '<option value="10">10</option>' +
			          '<option value="20">20</option>' +
			          '<option value="50">50</option>' +
			          '<option value="100">100</option>' +
			          '<option value="-1">all</option>' +
			          '</select> substances.'	            
			    },	
			    "aoColumnDefs": [
				    			{ //2
				    				"aTargets": [ 0 ],	
				    				"sClass" : "center",
				    				"bSortable" : false,
				    				"bSearchable" : false,
				    				"mData" : null,
				    				"sWidth" : "24px",
				    				"bUseRendered" : false,	
				    				"fnRender" : function(o,val) {
				    					return "<span class='ui-icon ui-icon-folder-collapsed zoomstruc' style='float: left; margin: .1em;' title='Click to show substance composition'></span>";
				    				}
				    			},				                     
			    				{ //2
			    					"aTargets": [ 1 ],	
			    					"sClass" : "camelCase",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "name",
			    					"sWidth" : "25%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},	    	
			    				{ //1
			    					"aTargets": [ 2 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "i5uuid",
			    					"sWidth" : "20%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var sOut = "<a href='"+o.aData["URI"]+"'>"+ val+ "</a>"
			    						return val===undefined?"":(val==null)?"":sOut;
			    					}
			    				},		
			    				{ //1
			    					"aTargets": [ 3 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "substanceType",
			    					"sWidth" : "15%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},			    				
			    				{ //3
			    					"aTargets": [ 4 ],	
			    					"sClass" : "camelCase",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "publicname",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},
			    				{ //1
			    					"aTargets": [ 5 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "referenceSubstance.i5uuid",
			    					"sWidth" : "20%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var sOut = "<a href='"+root + "/query/compound/search/all?search=" + val +"'>"+ val+ "</a>"
			    						return val===undefined?"":(val==null)?"":sOut;
			    					}
			    				},
			    				{ //1
			    					"aTargets": [6 ],	
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "ownerName",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return "<a href='"+root+"/substanceowner/"+o.aData["ownerUUID"]+"' title='Click to see summary for the substance owner' target='substanceowner'>"+val+"</a>";

			    					}
			    				},			    				
			    				{ //1
			    					"aTargets": [7 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "i5uuid",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var dataset_uri=root+"/substance/"+val+"/structure";
			    						var sOut = "<a href='"+root+"/ui/_dataset?dataset_uri="+encodeURIComponent(dataset_uri)+"' title='Composition as a Dataset'>Structures</a> ";
			    						sOut += "<a href='"+root+"/substance/"+val+"/study' title='Study'>Study</a>";
			    						
			    						return sOut;

			    					}
			    				},
			      	  			{  
			        	  			  "mData": null , 
			        	  			  "aTargets": [8 ],	
			        	  			  "sWidth" : "32px",
			        	  			  "bVisible" : removeVisible,
			        		  	      "bUseRendered" : false,	
			        			       "fnRender": function ( o, val ) {
			        			    	   val = o.aData["URI"];
			        			    	   var statusSelector= 's'+ o['iDataRow'] ;
			        			    	   var sOut = "";
			       		                	sOut = "<a href='#' onClick='deleteSubstance(\""+o.aData.URI + "\"," + "\""+encodeURIComponent(o.aData.name) + "\"," +
			       		                			"\"#" + statusSelector+  "\");'><span class='ui-icon ui-icon-trash' style='float: left; margin: .1em;' title='Remove substance "+ 
			       		                		o.aData.URI + "'></span></a><br/><span class='msg' id='" + statusSelector + "'></span>";
			        			    	   return sOut;
			        			      }
			        		  		}				    				
			    				],
			      "fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
						oSettings.jqXHR = $.ajax({
							"type" : "GET",
							"url" : sSource,
							"data" : aoData,
							"dataType" : "json",
							"contentType" : "application/json",
							"success" : function(json) {
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
			 	  "aaSorting": [[1, 'desc']]
				});
				
		    	$(selector + ' tbody').live('click','td .zoomstruc',function() {
					var nTr = $(this).parents('tr')[0];
					if (oTable.fnIsOpen(nTr)) {
						$(this).removeClass("ui-icon-folder-open");
						$(this).addClass("ui-icon-folder-collapsed");
						this.title='Click to show substance composition';
						oTable.fnClose(nTr);
					} else {
						$(this).removeClass("ui-icon-folder-collapsed");
						$(this).addClass("ui-icon-folder-open");
						this.title='Click to close substance composition panel';
						var id = getID();
						oTable.fnOpen(nTr, fnFormatDetails(nTr,id),"substancedetails");
						
						var composition = oTable.fnGetData(nTr);
						substance.defineCompositionTable(root,
									composition["URI"]+"/composition?media=" +encodeURIComponent("application/json") ,
									"#t_"+id,
									null,
									compositionDom==null?'Trt':compositionDom);
									//compositionDom==null?'<"help remove-bottom"><"help">Trt<"help">':compositionDom);
									
					}
		    	});
		    	
		    	function getID() {
		    		return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
		    	}
				function fnFormatDetails( nTr,id ) {
					var compositionTable = 
						"<div id='c_"+id+"' class='details' style='margin-top: 5x;' >"+						
						"<table id='t_"+id+"' class='compositiontable' cellpadding='0' border='0' width='100%' cellspacing='0' style='margin:0;padding:0;' >"+
						"<thead><tr><th>Composition ID</th><th>Type</th><th>Name</th><th>EC No.</th><th>CAS No.</th><th>Typical concentration</th><th>Real concentration (lower)</th><th>Real concentration (upper)</th>"+
						"<th>Other related substances</th></tr></thead><tbody></tbody></table></div>";

				    return compositionTable;
				}
		    	
				return oTable;			
		},
		"defineCompositionTable" : function (root,url,selector,jQueryUI,dom) {
			var oTable = $(selector).dataTable( {
				"sAjaxDataProp" : "composition",
				"sAjaxSource": url,	
				"sSearch": "Filter:",
				"bJQueryUI" : jQueryUI,
				"bSearchable": true,
				"bProcessing" : true,
				"sDom" : dom==null?'<"help remove-bottom"i><"help"p>Trt<"help"lf>':dom,
				"bPaginate" : true,
				"sPaginationType": "full_numbers",
				"sPaginate" : ".dataTables_paginate _paging",
				"oLanguage": {
			            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
			            "sLoadingRecords": "No substances found.",
			            "sZeroRecords": "No substances found.",
			            "sEmptyTable": "No substances available.",
			            "sInfo": "Showing _TOTAL_ substance(s) (_START_ to _END_)",
			            "sLengthMenu": 'Display <select>' +
			          '<option value="10">10</option>' +
			          '<option value="20">20</option>' +
			          '<option value="50">50</option>' +
			          '<option value="100">100</option>' +
			          '<option value="-1">all</option>' +
			          '</select> substances.'	            
			    },	
			    "aoColumnDefs": [
								{ //1
									"aTargets": [ 0 ],	
									"sClass" : "left",
									"bSortable" : true,
									"bSearchable" : true,
									"sWidth" : "20%",
									"mDataProp" : "compositionUUID",
									"bUseRendered" : false,	
									"fnRender" : function(o,val) {
										var s = val.split("-");
										return "<span title='"+val+"' style='font-weight:bold;'>" + s[1] + "</span> <br/>" + 
											   "<span title='Composition name'>" + o.aData["compositionName"] + "</span> " ;
										
									}
								},	
			    				{ //1
			    					"aTargets": [ 1 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "relation",
			    					"bUseRendered" : false,	
			    					"sWidth" : "10%",
			    					"fnRender" : function(o,val) {
			    						var sOut = "<span class='camelCase'>"+ val.replace("HAS_","").toLowerCase() + "</span>";
			    						var func = ("HAS_ADDITIVE" == val)?o.aData["proportion"]["function_as_additive"]:"";
			    						return sOut + " " + (((func===undefined) || (func==null) || (""==func))?"":("("+func+")"));
			    					}
			    				},	    
			    				{ //2
			    					"aTargets": [ 2 ],	
			    					"sClass" : "camelCase left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "component.compound.name",
			    					"sWidth" : "20%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var sOut = "<a href='"+o.aData["component"]["compound"]["URI"]+"' target=_blank title='Click to view the compound'><span class='ui-icon ui-icon-link' style='float: left; margin-right: .3em;''></span></span></a> " + val;
			    						return sOut;
			    					}
			    				},	    	
			    				{ //3
			    					"aTargets": [ 3 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "component.compound.einecs",
			    					"sWidth" : "10%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},
			    				{ //3
			    					"aTargets": [ 4 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "component.compound.cas",
			    					"sWidth" : "10%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},
			    				{ //4
			    					"aTargets": [ 5 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "proportion.typical.value",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var precision = o.aData["proportion"]["typical"]["precision"];
			    						var sOut = ((precision===undefined) || (precision==null) || ("="==precision))?"":precision;
			    						return sOut + " " + val + " " + o.aData["proportion"]["typical"]["unit"] ;
			    					}
			    				},
			    				{ //4
			    					"aTargets": [ 6 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "proportion.real",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var precision = val["lowerPrecision"];
			    						var sOut = ((precision===undefined) || (precision==null) || ("="==precision))?"":precision;
			    						sOut +=  val["lowerValue"] + " ";
			    						sOut += val["unit"]==null?"":(val["unit"]) ;
			    						return sOut;
			    					}
			    				},
			    				{ //4
			    					"aTargets": [ 7 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "proportion.real",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var precision = val["upperPrecision"];
			    						var sOut = ((precision===undefined) || (precision==null) || ("="==precision))?"":precision;
			    						sOut += val["upperValue"] + " ";
			    						sOut += val["unit"]==null?"":(val["unit"]) ;
			    						return sOut;
			    					}
			    				},			    				
			    				{ //5
			    					"aTargets": [ 8 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "component.compound.URI",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						if ((val===undefined) || (val==null)) return "";
			    						var sOut = "<a href='"+root+"/substance?type=related&compound_uri="+encodeURIComponent(val)+"' target=_blank>Also contained in...</span></a>"
			    						return sOut;
			    					}
			    				}		    				
			    				],
					"fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
									oSettings.jqXHR = $.ajax({
										"type" : "GET",
										"url" : sSource,
										"data" : aoData,
										"dataType" : "json",
										"contentType" : "application/json",
										"success" : function(json) {
											//names
											$.each(json.composition, function(k, cmp) {
												cmp.component.compound["name"] = [];
												cmp.component.compound["cas"] = [];
												cmp.component.compound["einecs"] = [];
												$.each(cmp.component.values,function(fURI,value) {
													var feature = json.feature[fURI];
													if ((feature!=null) && (value!=null)) {
												 		if (feature.sameAs == "http://www.opentox.org/api/1.1#IUPACName") {
												 			substance.formatValues(cmp.component.compound["name"],value.trim().toLowerCase());
											    		} else if (feature.sameAs == "http://www.opentox.org/api/1.1#ChemicalName") {
											    			substance.formatValues(cmp.component.compound["name"],value.trim().toLowerCase());
												        } else if (feature.sameAs == "http://www.opentox.org/api/1.1#CASRN") { 
												        	substance.formatValues(cmp.component.compound["cas"],value.trim().toLowerCase());
												        } else if (feature.sameAs == "http://www.opentox.org/api/1.1#EINECS") {
												        	substance.formatValues(cmp.component.compound["einecs"],value.trim().toLowerCase());
												        } 
													}
												});
											});	
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
			 	  "aaSorting": [[0, 'asc'],[1, 'asc']]
				});
				return oTable;			
		},
		"formatValues" : function(array, value) {
			$.each(value.split("|"), function(index, v) {
				if ("" != v) {
					if (array.length==0) array.push(v);
					else {
						var filtered = array.filter(function(element, index, array) {
							 return (element == v);
						});
						if (filtered.length==0)	array.push(v);
					}
				}
			});
		},
		"defineSubstanceStudyTable" : function (root,url,selector,jQueryUI,dom,compositionDom) {
			var oTable = $(selector).dataTable( {
				"sAjaxDataProp" : "study",
				"sAjaxSource": url,	
				"bJQueryUI" : jQueryUI,
				"bSearchable": true,
				"bProcessing" : true,
				"sDom" : (dom==null)?'<"help remove-bottom"i><"help"p>Trt<"help"lf>':dom,
				"sSearch": "Filter:",
				"bPaginate" : true,
				"sPaginationType": "full_numbers",
				"sPaginate" : ".dataTables_paginate _paging",
				"oLanguage": {
			            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
			            "sLoadingRecords": "No substances found.",
			            "sZeroRecords": "No substances found.",
			            "sEmptyTable": "No substances available.",
			            "sInfo": "Showing _TOTAL_ substance(s) (_START_ to _END_)",
			            "sLengthMenu": 'Display <select>' +
			          '<option value="10">10</option>' +
			          '<option value="20">20</option>' +
			          '<option value="50">50</option>' +
			          '<option value="100">100</option>' +
			          '<option value="-1">all</option>' +
			          '</select> substances.'	            
			    },	
			    "aoColumnDefs": [
			    				{ //1
			    					"aTargets": [ 0 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "uuid",
			    					"sWidth" : "20%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},		
			    				{ //1
			    					"aTargets": [ 1 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "protocol.category",
			    					"sWidth" : "15%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},			    				
			    				{ //3
			    					"aTargets": [ 2 ],	
			    					"sClass" : "camelCase",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "protocol.endpoint",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},
			    				{ //1
			    					"aTargets": [ 3 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "protocol.guideline",
			    					"sWidth" : "20%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},
			    				{ //1
			    					"aTargets": [ 4 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "parameters",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var sOut = "";
			    						$.each(val, function(key,n) {
			    							sOut += key + " = " + n;
			    							sOut += "<br/>";
			    						});
			    						return sOut;
			    					}
			    				},		
			    				{ //1
			    					"aTargets": [ 5 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "effects",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var sOut = "";
			    						$.each(val, function(key,effect) {
			    							sOut = effect.endpoint + "=";
			    							sOut += (effect.result["loValue"]===undefined?effect.result["upValue"]:effect.result["loValue"]);
			    							sOut += " " +((effect.result["unit"]===undefined)||(effect.result["unit"]==null)?"":effect.result["unit"]);
			    							return sOut;
			    						});
			    						return sOut;
			    					}
			    				},
				    			{ //2
				    				"aTargets": [6 ],	
				    				"sClass" : "center",
				    				"bSortable" : false,
				    				"bSearchable" : false,
				    				"mData" : null,
				    				"bUseRendered" : false,	
				    				"fnRender" : function(o,val) {
				    					return "<span class='ui-icon ui-icon-folder-collapsed zoomstruc' style='float: left; margin: .1em;' title='Click to show study results'></span>";
				    				}
				    			}			    				
			    				],
			      "fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
						oSettings.jqXHR = $.ajax({
							"type" : "GET",
							"url" : sSource,
							"data" : aoData,
							"dataType" : "json",
							"contentType" : "application/json",
							"success" : function(json) {
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
			 	  "aaSorting": [[1, 'desc']]
				});

		    	$(selector + ' tbody').live('click','td .zoomstruc',function() {
					var nTr = $(this).parents('tr')[0];
					if (oTable.fnIsOpen(nTr)) {
						$(this).removeClass("ui-icon-folder-open");
						$(this).addClass("ui-icon-folder-collapsed");
						this.title='Click to show study results';
						oTable.fnClose(nTr);
					} else {
						$(this).removeClass("ui-icon-folder-collapsed");
						$(this).addClass("ui-icon-folder-open");
						this.title='Click to close study results panel';
						var id = getID();
						var dataEntry = oTable.fnGetData(nTr);
						oTable.fnOpen(nTr, fnFormatDetails(dataEntry,id),"studyresults");
									
					}
		    	});
		    	function getID() {
		    		return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
		    	}
				function fnFormatDetails( dataEntry,id ) {
					var effectsTable = 
						"<div id='c_"+id+"' class='details' style='margin-top: 5x;' >"+						
						"<table id='t_"+id+"' class='compositiontable' cellpadding='0' border='0' width='100%' cellspacing='0' style='margin:0;padding:0;' >"+
						"<thead><tr><th>Endpoint</th><th>Value (lower)</th><th>Value (upper)</th><th>Unit</th><th>Conditions</th>"+
						"</tr></thead><tbody>";
						$.each(dataEntry.effects, function(key,effect) {
							effectsTable += "<tr>";
							effectsTable += "<td>"+ effect.endpoint +"</td>";
							effectsTable += "<td>"+ (effect.result["loQualifier"]===undefined?"":effect.result["loQualifier"]) + 
													(effect.result["loValue"]===undefined?"":effect.result["loValue"]) +"</td>";
							effectsTable += "<td>"+ (effect.result["upQualifier"]===undefined?"":effect.result["upQualifier"]) + 
													(effect.result["upValue"]===undefined?"":effect.result["upValue"]) +"</td>";
							effectsTable += "<td>"+ (effect.result["unit"]==null?"":effect.result["unit"]) +"</td>";
							var sOut = "";
							$.each(effect["conditions"], function(name,value) {
								sOut += name + "=" + value + "<br/>";
							});
							effectsTable += "<td>"+ sOut +"</td>";
							effectsTable += "</tr>";
						});
						effectsTable += "</tbody></table></div>";
				    return effectsTable;
				}

				return oTable;			
		}		
}
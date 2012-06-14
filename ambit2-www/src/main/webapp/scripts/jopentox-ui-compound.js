/**
 * jOpenToxUI JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012, IDEAconsult Ltd. http://www.ideaconsult.net/
 * 
 * TODO: Licence.
 */


var cmpArray = {
	"dataEntry" : [],
	"feature":[]
};

/**
 * Models rendering
 */
$(document)
		.ready(
				function() {

					/* Initialize */
					oTable = $(".compoundtable")
							.dataTable(
									{
										'bProcessing' : true,
										'bJQueryUI' : true,
										'bPaginate' : true,
										"sDom" : 'T<"clear"><"fg-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
										"aaData" : cmpArray.dataEntry,
										"aoColumns" : [
												{
													"sClass" : "center",
													"bSortable" : false,
													"mDataProp" : null,
													sWidth : "5%",
													"bUseRendered" : "true",
													"fnRender" : function(o,
															val) {
														return "<img src='/ambit2/images/zoom_in.png'>";
													}
												},
												{
													"sTitle" : "URI",
													"mDataProp" : "compound.URI",
													sWidth : "50%",
													"bUseRendered" : "false",
													"fnRender" : function(o,
															val) {
														return val;
													}
												}

										],
										"aaSorting" : [ [ 1, 'asc' ] ]
									});

					/* event listener */
					$('.compoundtable tbody td img').live(
							'click',
							function() {
								var nTr = $(this).parents('tr')[0];
								if (oTable.fnIsOpen(nTr)) {
									/* This row is already open - close it */
									this.src = "/ambit2/images/zoom_in.png";
									oTable.fnClose(nTr);
								} else {
									/* Open this row */
									this.src = "/ambit2/images/zoom_out.png";
									var id = 'values'+getID();
									oTable.fnOpen(nTr, fnFormatDetails(nTr,id),
											'details');
									
								       $('#'+ id).dataTable({
								    		'bJQueryUI': false, 
								    		'bPaginate': true,
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
						var dataEntry = oTable.fnGetData(nTr);
						var sOut = '<div class="ui-widget" style="margin-top: 5x;" >';
						sOut += '<div style="min-height:200px">';
						
						sOut += '<table width="100%"  style="min-height:200px"><tbody><tr>';//outer table, can't get the style right with divs

						//structure
						sOut += '<td  valign="top" >';
						sOut += '<img class="ui-widget-content ui-corner-top ui-corner-bottom" style="min-height:200px;min-width:200px" src="' 
									+ dataEntry.compound.URI + '?media=image/png">';
						sOut += '<br>';
						sOut += "TODO";
						sOut += '</td>';
						
						//properties
						sOut += '<td>';
						sOut += '<table id="'+ id +'" class="values" >';
						sOut += '<thead><th>Type</th><th>Calculated</th><th>Property</th><th>Value</th></thead>';
						sOut += '<tbody>';
						for (key in dataEntry.values) {
								sOut += '<tr>';

								//type (sameas)
								var sameAs = cmpArray.feature[key]["sameAs"]; 	
								sOut += '<td title="Same as the OpenTox ontology entry defined by '+sameAs+'">';
								if (sameAs.indexOf("http")>=0) {
									var hash = sameAs.lastIndexOf("#");
									if (hash>0) sOut += sameAs.substring(hash+1).replace("_"," ");
									else sOut += sameAs;
								}
								sOut += '</td>';
								//calculated
								var source = cmpArray.feature[key]["source"]["type"];
								var hint = "Imported from a dataset";
								if (source=="Algorithm" || source=="Model") {
									hint = "Calculated by " + source;
									source = '<a href="'+cmpArray.feature[key]["source"]["URI"]+'">Yes</a>';
							    } else source="";
			
								sOut += '<td title="'+hint+'">';					
								sOut += source;
								sOut += '</td>';
			
								//name, units
								sOut += '<td title="OpenTox Feature URI: '+key+'">';
								sOut += '<a href="' + key + '">';
								sOut += cmpArray.feature[key]["title"];
								sOut += '</a>';
								sOut += cmpArray.feature[key]["units"];
								sOut += '</td>';
								
								hint = 'Numeric:&nbsp;'+cmpArray.feature[key]["isNumeric"];
								hint += '\nNominal:&nbsp;'+cmpArray.feature[key]["isNominal"];
								hint += '\nSource:&nbsp;'+cmpArray.feature[key]["source"]["URI"];
								hint += '\nSource type:&nbsp;'+cmpArray.feature[key]["source"]["type"];
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


	function decodeEntities(input) {
	  var y = document.createElement('textarea');
	  y.innerHTML = input;
	  return y.value;
	}
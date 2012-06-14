/**
 * jOpenToxUI JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012, IDEAconsult Ltd. http://www.ideaconsult.net/
 * 
 * TODO: Licence.
 */

var cmpArray = {
	"dataEntry" : []
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
								    		"sDom": 'T<"clear"><"fg-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
											"aaSorting" : [ [ 1, 'asc' ] ]						    		
								      });
								}
							});



				       
					/* Formating function for row details */
					function fnFormatDetails(nTr, id) {
						var dataEntry = oTable.fnGetData(nTr);
						var sOut = '<div class="ui-widget" style="margin-top: 5x; padding: 0 .1em;" >';
						sOut += '<div class="ui-widget-header ui-corner-top"><p><b>Compound: </b>' 
							          + dataEntry.compound.URI + '</p></div>';
						sOut += '<div class="ui-widget-content ui-corner-bottom " style="min-height:200px">';
						
						sOut += '<table width="100%"  style="min-height:200px"><tbody><tr>';//outer table

						//structure
						sOut += '<td>';
						sOut += '<img src="' + dataEntry.compound.URI + '?media=image/png">';
						sOut += '</td>';
						
						//properties
						sOut += '<td>';
						sOut += '<table id="'+ id +'" class="values" >';
						sOut += '<thead><th>Property</th><th>Value</th></thead>';
						sOut += '<tbody>';
						for (i in dataEntry.values) 
							if (dataEntry.values[i].value) {
								sOut += '<tr>';
								sOut += '<td>' + dataEntry.values[i].feature + '</td>';
								sOut += '<td>' + dataEntry.values[i].value + '</td>';
								sOut += '</tr>\n';
							}
						sOut += '</tbody></table>\n';							
						sOut += '</td>';
						

						//outer table
						sOut += '</tr></tbody></table>';
						sOut += '</div></div>\n';
						
						return sOut;
					}
					
					function getID() {
						   return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
						}
					
				});

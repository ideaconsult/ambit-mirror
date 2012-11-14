function defineStructuresTable(url, query_service, similarity) {

	var oTable = $('#structures')
			.dataTable(
					{
						"bProcessing" : true,
						"bServerSide" : false,
						"bStateSave" : false,
						"aoColumnDefs" : [
								{ // 0
									"mDataProp" : "compound.cas",
									"aTargets" : [ 0 ],
									"sClass" : "center",
									"bSortable" : false,
									"bSearchable" : false,
									"mDataProp" : null,
									sWidth : "32px",
									"fnRender" : function(o, val) {
										var cas = formatValues(o.aData, "cas");
										if ((val === undefined) || (val == ""))
											return "<input class='selectcas' type='checkbox' name='cas[]' title='Select "+ cas +"' value='"+cas+"'>\n";
										else
											return val;											
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
									"sHeight" : "205px",
									"sWidth" : "205px",
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
												+ cmpURI + '&w=200&h=200">';
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
									"bVisible" : false
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
									"bVisible" : false
								} ],
						"bJQueryUI" : true,
						"bPaginate" : true,
						"sPaginationType": "full_numbers",
						"bDeferRender" : true,
						"bSearchable" : true,
						"sAjaxSource" : url,
						"sAjaxDataProp" : "dataEntry",
						"fnServerData" : function(sSource, aoData, fnCallback,
								oSettings) {
							oSettings.jqXHR = $.ajax({
								"type" : "GET",
								"url" : sSource,
								"data" : aoData,
								"dataType" : "jsonp",
								"contentType" : "application/x-javascript",
								"success" : function(json) {
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
							"sProcessing" : "<img src='images/progress.gif' border='0'>",
							"sLoadingRecords" : "No records found."
						},
						"fnRowCallback" : function(nRow, aData, iDisplayIndex) {
							// retrieve identifiers
							id_uri = query_service
									+ "/query/compound/url/all?search="
									+ encodeURIComponent(aData.compound.URI)
									+ "?max=1&media=application%2Fx-javascript";
							$
									.ajax({
										dataType : "jsonp",
										url : id_uri,
										success : function(data, status, xhr) {
											identifiers(data);
											$
													.each(
															data.dataEntry,
															function(index,
																	entry) {
								
																aData.compound.cas = formatValues(
																		entry,"cas");																
																$('td:eq(0)',
																		nRow)
																		.html("<input class='selectcas' type='checkbox' name='cas[]' title='Select "+aData.compound.cas+"' value='"+aData.compound.cas+"'>\n");
																aData.compound.name = formatValues(
																		entry,
																		"names");																
																$('td:eq(3)',
																		nRow)
																		.html(aData.compound.name);
																$('td:eq(1)',
																		nRow)
																		.html(aData.compound.cas);
																aData.compound['smiles'] = formatValues(
																		entry,
																		"smiles");
																$('td:eq(6)',
																		nRow)
																		.html(
																				aData.compound['smiles']);
																aData.compound['inchi'] = formatValues(
																		entry,
																		"inchi");
																$('td:eq(7)',
																		nRow)
																		.html(
																				aData.compound['inchi']);
																aData.compound['inchikey'] = formatValues(
																		entry,
																		"inchikey");
																$('td:eq(8)',
																		nRow)
																		.html(
																				aData.compound['inchikey']);
															});

										},
										error : function(xhr, status, err) {
										},
										complete : function(xhr, status) {
										}
									});

						}
					});
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
					sOut += delimiter;
					sOut += v;
					delimiter = "<br>";
				}
			});
			// sOut += dataEntry.values[value];
		}
	});
	return sOut;
}

function selectcas(value) {
	$('.selectcas').prop('checked', value);
}
/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012-2013, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 * 
 */

function defineAlgorithmTable(root,url,viscols) {
	var oTable = $('#algorithm').dataTable( {
		"sAjaxDataProp" : "algorithm",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
		 		{ "mData": null , "asSorting": [ "asc", "desc" ],
	 			  "aTargets": [ 0 ],	
	 			  "sWidth" : "1em",
	 			  "bSearchable" : false,
	 			  "bUseRendered" : false,
	 			  "bVisible" : (viscols==null) || viscols[0],
 				  "bSortable" : false,
				  "fnRender" : function(o,val) {
					  var sOut='<input type="checkbox" checked="" name="alg_uri[]" title="Select '+val+'" value="'+val+'">';
					  return sOut;
				  }	  
				},		                 
				{ "mDataProp": "uri" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 1 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "sWidth" : "25%",
				  "fnRender" : function(o,val) {
					  if (o.aData["id"]==null) return "Algorithm";
					  var sOut = "<a href='"+val+"' title='Click to view the algorithm at "+
					  		val+
					  		" and (optionally) launch the processing'><span class='ui-icon ui-icon-link' style='float: left; margin: .1em;' ></span>"+
					  		o.aData["id"]+"</a><br/>" +o.aData["name"];
					  return sOut;
					  
				  }
				},
				{ "mDataProp": "endpoint" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 2 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bVisible" : (viscols==null) || viscols[2],
				  "bSortable" : true,
				  "sWidth" : "20%",
				  "fnRender" : function(o,val) {
					  //create link to the ontology server
					  var p = val.indexOf("#");
					  if (p>0) return val.substring(p+1);
					  else return val;
				  }
				},
				{ "mDataProp": "isDataProcessing" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 3 ],
				  "bSearchable" : true,
				  "bVisible" : (viscols==null) || viscols[3],
				  "bSortable" : true,
				  "sWidth" : "22%",
				  "bUseRendered" : false,
				  "fnRender" : function(o,val) {
					  
					  var icon = '<span class="ui-icon ui-icon-pin-s" style="float: left; margin-right: .1em;"></span>';
					  var sOut = icon + (val?"Processes a dataset":"Builds a model.");
					  sOut += o.aData["requiresDataset"]?("<br>"+icon+"Requires input dataset"):"";
					  sOut += o.aData["requires"]==""?"":"<br>"+icon+"Requires " + o.aData["requires"];
					  sOut += o.aData["isSupevised"]?("<br>"+icon+"Requires target variable"):"";
					  return sOut;
				  }
				},
				{ "mDataProp": "type" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 4 ],
					  "bSearchable" : true,
					  "bVisible" : (viscols==null) || viscols[4],
					  "bSortable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "12%",
					  "fnRender" : function(o,val) {
						  var sOut = "";
						  $.each(val, function(index, value) {
							  var p = value.indexOf("#");
							  var name = value;  if (p>0) name =  value.substring(p+1);
							  sOut += "<a href='"+val+"'>"+name+"</a>";
							  sOut += " ";
							});
						  return sOut;
					  }
				},
				{ "mDataProp": "uri" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 5 ],
					  "bSearchable" : true,
					  "bVisible" : (viscols==null) || viscols[5],
					  "bSortable" : true,
					  "bUseRendered" : false,
					  "fnRender" : function(o,val) {
						  
						  return  " <a href='"+root+"/model?algorithm="+ encodeURIComponent(val) +"&max=100' title='Click to view models using "
		            		+val+" algorithm'><span class='ui-icon ui-icon-calculator' style='float: left; margin-right: .3em;'></span>View</a>";
					  }
				},				
				{ "mDataProp": "implementationOf" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 6],
					  "bSearchable" : true,
					  "bVisible" : (viscols==null) || viscols[6],
					  "bSortable" : true,
					  "sWidth" : "12%",
					  "bUseRendered" : false,
					  "fnRender" : function(o,val) {
						  var p = val.indexOf("#");
						  var name = val;  if (p>0) name =  val.substring(p+1);
						  return "<a class='qxternal'  href='http://apps.ideaconsult.net:8080/ontology?uri=" + encodeURIComponent(val) +"' target=_blank>" +name +"</a>";
					  }
				}
					
			],
		"bJQueryUI" : true,
		"bSearchable": true,
		"sAjaxSource": url,
		"sDom" : '<"help remove-bottom"lf><"help remove-bottom">Trt<"help remove-bottom"lp>',
		"sSearch": "Filter:",
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"oLanguage": {
	            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No algorithms found.",
	            "sZeroRecords": "No algorithms found.",
	            "sInfo": "Showing _TOTAL_ algorithms (_START_ to _END_)",
	            "sLengthMenu": 'Display <select>' +
              '<option value="10">10</option>' +
              '<option value="20">20</option>' +
              '<option value="50">50</option>' +
              '<option value="100">100</option>' +
              '<option value="-1">all</option>' +
              '</select> algorithms.'	            
	    },
	    "fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
			oSettings.jqXHR = $.ajax({
				"type" : "GET",
				"url" : sSource,
				"data" : aoData,
				"dataType" : "json",
				"contentType" : "application/json",
				"cache" : true,
				"success": function(result){fnCallback(result);},
				"error" : function(xhr, textStatus, error) {
					switch (xhr.status) {
					case 403: {
			        	alert("Restricted access. You are not authorized to access the requested algorithms.");
						break;
					}
					case 404: {
						//not found
						break;
					}
					default: {
						//console.log(xhr.status + " " + xhr.statusText + " " + xhr.responseText);
			        	alert("Error loading algorithms " + xhr.status + " " + error);
					}
					}
					oSettings.oApi._fnProcessingDisplay(oSettings, false);
				}
			});
		}	    
	} );
	return oTable;
}

/**
 * Read single algorithm by json
 * @param root
 * @param url
 */
function readAlgorithm(root,url) {
	  $.ajax({
	        dataType: "json",
	        url: url,
	        cache: false, //otherwise stupid broken IE8 clears the page on refresh...
            data: {
	              media:"application/json"
	        },	        
	        success: function(data, status, xhr) {
	        	$.each(data["algorithm"],function(index, entry) {
	        		try {
	        			renderAlgorithm(entry,root,null);
	        		} catch (err) {
	        		}
	        	});
	        },
	        error: function(xhr, status, err) {
	        	renderAlgorithm(null,root,err);
	        },
	        complete: function(xhr, status) {
	        }
	     });
}

/**
 * 
 * @param entry
 * @param root
 * @param err
 */
function renderAlgorithm(entry,root,err) {
	if (entry==null) {
		$("#alg_title").text("N/A");
		return;
	}
	var icon = '<span class="ui-icon ui-icon-pin-s" style="float: left; margin-right: .1em;"></span>';
	$("#alg_title").text(entry["name"]);
	$("#alg_title").prop("title",entry["content"]);
	if ((entry["endpoint"]===undefined) || (entry["endpoint"]==null) ||(entry["endpoint"]=="")) {
		$("#predictsEndpoint").hide();
	} else {
		var val = entry["endpoint"];
		var p = val.indexOf("#");
		if (p>0) val = val.substring(p+1);
		$("#alg_endpoint").text(val);
		$("#predictsEndpoint").show();
	}
	
	$("#alg_implementation").html(entry["implementationOf"]);
		
	$("#alg_implementation").prop('href','http://apps.ideaconsult.net:8080/ontology?uri='+encodeURIComponent(entry["implementationOf"]));
	
	$("#alg_requires").text(entry["requires"]=="structure"?"Chemical structure":entry["requires"]);
	$("#alg_dataset").text(entry["isDataProcessing"]?"Processes a dataset":"Builds a model");
	$("#help_action").html(entry["isDataProcessing"]?
		"The result is a dataset, identified by a <a href='"  + root + "/dataset' target='dataset'>dataset URI</a>.":
		"Once a model is built, it is assigned a <a href='" + 
		root + "/model' target='model'>model URI</a> and can be applied to <a href='" + 
		root + "/dataset' target='dataset'>datasets</a> and <a href='" +
		root + "/compound' target='compound'>compounds</a>.");
	if (entry["requiresDataset"]) $("#requiresDataset").show(); else $("#requiresDataset").hide();
	if (entry["isSupevised"]) $("#requiresTarget").show(); else $("#requiresTarget").hide();
	var sOut = "";
	var isFinder = false;
	var isSuperBuilder = false;
	var isSuperService = false;
	$.each(entry["type"], function(index, value) {
		  var p = value.indexOf("#");
		  var name = value;  if (p>0) name =  value.substring(p+1);
		  sOut += name;
		  sOut += "&nbsp;|&nbsp;";
		  if ("Finder"==name) isFinder = true;
		  else if ("SuperService"==name) isSuperService = true;
		  else if ("SuperBuilder"==name) isSuperBuilder = true;
	});
	sOut += "</ul>";
	$("#alg_type").html(sOut);
	
	if (isFinder) {
		$("#alg_dataset").text("Finds chemical structures by querying online services by a compound identifier");
		$("#requiresDataset").show();
		$("#finder").show();
	}  else $("#finder").hide();
	if (isSuperBuilder) {
		$("#superBuilder").show();
		$("#alg_dataset").text("Calculate descriptors, prepares a dataset and builds the model");
	}  else $("#superBuilder").hide();
	if (isSuperService) {
		$("#alg_dataset").text("Calculate descriptors, prepares a dataset and runs the model");
		$("#superService").show(); 
	} else $("#superService").hide();

}

/**
 * Models table
 * @param root
 * @param url
 * @returns
 */
function defineModelTable(root,url) {
	var oTable = $('.modeltable').dataTable( {
	"sAjaxDataProp" : "model",
	"sAjaxSource": url,	
	"sSearch": "Filter:",
	"bJQueryUI" : true,
	"bSearchable": true,
	"bProcessing" : true,
	"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
	"sSearch": "Filter:",
	"bPaginate" : true,
	"sPaginationType": "full_numbers",
	"sPaginate" : ".dataTables_paginate _paging",
	"oLanguage": {
            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
            "sZeroRecords": "No models found.",
            "sInfo": "Showing _TOTAL_ models (_START_ to _END_)",
            "sLoadingRecords": "No models found.",
            "sLengthMenu": 'Display <select>' +
          '<option value="10">10</option>' +
          '<option value="20">20</option>' +
          '<option value="50">50</option>' +
          '<option value="100">100</option>' +
          '<option value="-1">all</option>' +
          '</select> models.'	            
    },	
    "aoColumnDefs": [
     	  			{ 
      	  			  "mDataProp":"URI", 
      	  			  "aTargets": [ 0 ],
      	  			  "bSortable" : true,
      	  			  "bSearchable" : true,
      		          "bUseRendered" : false,	
      		          "fnRender": function ( o, val ) {
      		        	    var shortURI = o.aData.URI;
      		        	    pos =  shortURI.lastIndexOf("/");
      		        	    if (pos>=0) shortURI = shortURI.substring(pos+1); 
      		                var sOut = "<a href='"+o.aData.URI +"' title='Click to view the model at " + 
      		                		o.aData.URI+" and (optionally) run predictions'><span class='ui-icon ui-icon-link' style='float: left; margin: .1em;' ></span>M" +
      		                		shortURI + "</a>" 
      		                sOut += "<br><span class='ui-icon ui-icon-folder-collapsed zoomstruc' style='float: left; margin: .1em;' title='Click to show model details'></span>";
      		                return sOut;
      		          }
      		  		},                
      		  		/*
    				{ //0
    					"aTargets": [ 0 ],	
    					"mDataProp":"algorithm.img",
    					"sClass" : "center",
    					"bSortable" : true,
    					"bSearchable" : false,
    					"mData" : null,
    					"bUseRendered" : false,
    					"fnRender" : function(o,val) {
    						 return "<img style='float: left; margin: .1em;' src='"+root + o.aData.algorithm.img +"' title='"+o.aData.algorithm.img+"'><br/>" + 
    						 "<span class='ui-icon ui-icon-folder-collapsed zoomstruc' style='float: left; margin: .1em;' title='Click to show model details'></span>";			
    					}
    				},	   
    				*/  	            
    	  			{ 
     	              "mDataProp":"stars",
     	              "aTargets": [ 1 ],
     	              "bSortable" : true,
     	              "bUseRendered" : false,
     	              "bUseRendered" : false,
	  					"fnRender" : function(o,val) {
							 return  "<span class='ui-icon ui-icon-star' style='display:inline-block' title='Models star rating'></span>" + val;			
						}
    	  			},     	 
    	  			{ "sTitle": "Title", 
    	  			  "mDataProp":"title", 
    	  			  "aTargets": [ 2 ],
    	  			  "bSortable" : true,
    	  			  "bSearchable" : true,
    	  			  sWidth : "25%",
    		          "bUseRendered" : false,	
    		          "fnRender": function ( o, val ) {
    		        	    return val; 
    		          }
    		  		},
    	  			{ "sTitle": "Training Dataset", 
      		  		  "mDataProp":"trainingDataset",
      		  		  "aTargets": [ 3 ],	
      			      "bUseRendered" : false,	
    	  			  "bSortable" : true,
    	  			  "bSearchable" : true,
      			      "fnRender": function ( o, val ) {
      			      	  	shortURI = val;
  			        		pos = shortURI.lastIndexOf("/");
  			        		if (pos>=0) shortURI = val.substring(pos+1);
  			        		var sOut = "<a href='"+ val +"?max=100' title='Click to browse the training dataset "+val+"' target='dataset'>"+shortURI+"</a> ";
    			            sOut += "<a href='"+root+"/model?dataset="+ encodeURIComponent(val) +"&max=100' title='Click to view models using this training dataset "
    			            		+val+"'><span class='ui-icon ui-icon-calculator' style='float: right; margin: .1em;' ></span></a>";
    			            return sOut;
      			       }
      		  		},    		  		
    	  			{ "sTitle": "Algorithm", 
    	  			  "mDataProp":"algorithm.URI" , 
    	  			  "aTargets": [ 4 ],	
    	  			  "bSortable" : true,
    	  			  "bSearchable" : true,
    		  	      "bUseRendered" : false,	
    			       "fnRender": function ( o, val ) {
    			        	    uri = val;
    			        		pos = val.lastIndexOf("/");
    			        		if (pos>=0) val = val.substring(pos+1); 
        			      	  	var shortURI = val;
        			      	  	pos = shortURI.lastIndexOf(".");
    			        		if (pos>=0) shortURI = shortURI.substring(pos+1); 
        			      	  	if (shortURI.length > 20) shortURI = shortURI.substring(val,20) + "..."; 	
        			      	  	var sOut = "<a href='"+ uri +"' title='Click to view the algorithm at "+uri+"' target='algorithm'>"+shortURI;
        			      	  	sOut += "<img style='float: left; margin: .1em;' src='"+root + o.aData.algorithm.img +"'> </a> ";
        			            sOut += "<a href='"+root+"/model?algorithm="+ encodeURIComponent(uri) +"&max=100' title='Click to view models using "
			            		+uri+" algorithm'><span class='ui-icon ui-icon-calculator' style='float: right; margin: .1em;' ></span></a>";
        			            return sOut;
    			      }
    		  		},    	  			
    	  			{ "sTitle": "RMSE (TR)", 
      		  		  "mData":null,
      		  		  "aTargets": [ 5 ],	
      		  		  "bSortable" : true,
      			      "bUseRendered" : true,	
      			      "fnRender": function ( o, val ) {
      			    	  try {
      			    		  var val = o.aData["ambitprop"]["evaluations"]["evaluation_training"]["rmse"];
      			    		  return val===undefined?"":val;
      			    	  } catch (err) {return "";}	  
      			       }
      		  		},
    	  			{ "sTitle": "RMSE (CV)", 
        		  		  "mData":null,
        		  		  "aTargets": [ 6 ],	
        		  		  "bSortable" : true,
        			      "bUseRendered" : true,	
        			      "fnRender": function ( o, val ) {
        			    	  try {
        			    		  var val = o.aData["ambitprop"]["evaluations"]["crossvalidation"]["rmse"];
        			    		  return val===undefined?"":val;
        			    	  } catch (err) {return "";}	  
        			       }
        		  	},
    	  			{ "sTitle": "Correct % (TR)", 
      		  		  "mData":null,
      		  		  "aTargets": [ 7 ],	
      		  		  "bSortable" : true,
      			      "bUseRendered" : true,	
      			      "fnRender": function ( o, val ) {
      			    	  try {
      			    		  var val = o.aData["ambitprop"]["evaluations"]["evaluation_training"]["pctcorrect"];
      			    		  return val===undefined?"":val;
      			    	  } catch (err) {return "";}	  
      			       }
    	  			},
    	  			{ "sTitle": "Correct % (CV)", 
        		  		  "mData":null,
        		  		  "aTargets": [8 ],	
        		  		  "bSortable" : true,
        			      "bUseRendered" : true,	
        			      "fnRender": function ( o, val ) {
        			    	  try {
        			    		  var val = o.aData["ambitprop"]["evaluations"]["crossvalidation"]["pctcorrect"];
        			    		  return val===undefined?"":val;
        			    	  } catch (err) {return "";}	  
        			       }
      	  			},     	  			
    	  			{ "sClass": "center", 
      		  		  "mData":null, 
      		  		  "aTargets": [ 9 ],	
      		  		  sWidth: "5%",
    		              "bUseRendered" : false,	"bSortable": true,
    		              "fnRender": function ( o, val ) {
		            		  var id = getID();
		            		  var sOut = "";
    		            	  try {
    		            		  var val = o.aData["ambitprop"]["evaluations"]["crossvalidation"]["content"];
    		            		  if (val != undefined)
    		            			  sOut += "<a href='#' title='Crossvalidation' onClick='$(\"#t"+id+"\").toggle();'>CV</a><textarea style='display:none;' id='t"+id+"'>"+
    		            		  		   val +"</textarea> ";
    		            	  } catch (err) { }
    		            	  try {
    		            		  var val = o.aData["ambitprop"]["evaluations"]["evaluation_training"]["content"];
    		            		  if (val != undefined)
    		            			  sOut += "<a href='#' title='Training' onClick='$(\"#c"+id+"\").toggle();'>Training</a><textarea style='display:none;' id='c"+id+"'>"+
    		            			  val + "</textarea>";
    		              	  } catch (err) { }
    		              	  try {
    		            		  var val = o.aData["ambitprop"]["evaluations"]["evaluation"]["content"];
    		            		  if (val != undefined)
    		            			  sOut += "<a href='#' title='Stats' onClick='$(\"#e"+id+"\").toggle();'>Stats</a><textarea style='display:none;' id='e"+id+"'>"+
    		            			  val + "</textarea>";
    		            	  } catch (err) { }
		            		  return sOut;

    		                }
      	  			}    		  		
     				],
 	  "aaSorting": [[1, 'asc']],
 	  "fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
			oSettings.jqXHR = $.ajax({
				"type" : "GET",
				"url" : sSource,
				"data" : aoData,
				"dataType" : "json",
				"contentType" : "application/json",
				"cache" : false,
				"success": function(result){fnCallback(result);},
				"error" : function(xhr, textStatus, error) {
					switch (xhr.status) {
					case 403: {
			        	alert("Restricted access. You are not authorized to access the requested models.");
						break;
					}
					case 404: {
						//not found
						break;
					}
					default: {
						//console.log(xhr.status + " " + xhr.statusText + " " + xhr.responseText);
			        	alert("Error loading models " + xhr.status + " " + error);
					}
					}
					oSettings.oApi._fnProcessingDisplay(oSettings, false);
				}
			});
		}
	});
	return oTable;
}

function statsDialog(content) {
	$("#dialog").text(content); 
}
/**
 * Models row detais
 * @param oTable
 * @param nTr
 * @param root
 * @returns {String}
 */
function modelFormatDetails( oTable, nTr ,root ) {
    var model = oTable.fnGetData( nTr );
    var sOut = '<div class="ui-widget" >';
   // sOut += '<div class="ui-widget-header ui-corner-top"><b>Model: </b>' + model.title + '</div>';
   // sOut += '<div class="ui-widget-content ui-corner-bottom ">';
    
    sOut += '<table cellpadding="5" cellspacing="0" width="100%" style="padding-left:50px;">';
    sOut += '<tbody>';

    sOut += '<tr><td colspan="2"></td><td rowspan="7">';
    if (model.algorithm.URI.toLowerCase().indexOf('toxtree')>=0) {
    	sOut += '<img src="'+model.ambitprop.legend+'">';
	}
    sOut += '</td></tr>\n';
    
    sOut += '<tr><td>Model name</td><td><a href=\"' + model.uri + '\">' + model.title + '</a></td></tr>';
    
    if (model.trainingDataset.indexOf("http")>=0) {
	    sOut += '<tr><th>Training dataset</th><td><a href="' + model.trainingDataset + '">' + model.trainingDataset + '</a></td></tr>';
    }
	
    sOut += '<tr><td>Training algorithm</td><td><a href="' + model.algorithm.URI + '">' + model.algorithm.URI + '</a></td></tr>';
    sOut += '<tr><td>Variables</td><td>';
    sOut += '<a href="' + model.independent + '" target="vars">Independent</a>|&nbsp;';
    sOut += '<a href="' + model.dependent + '" target="vars">Dependent</a>|&nbsp;';
    sOut += '<a href="' + model.predicted + '" target="vars">Predicted</a>&nbsp;';
    sOut += '</td></tr>\n';
    
    if (model.ambitprop.content.lastIndexOf("http", 0) == 0) { //starts with http
    	sOut += '<tr><td>Model content</td><td><a href="'+model.ambitprop.content+'" target="content">Text</a></td></tr>';	
    } else {
    	sOut += '<tr><td>Model content</td><td>'+model.ambitprop.content+' ['+model.ambitprop.mimetype+']</td></tr>';
    }
    
    sOut += '<tr><td>Model URI</td><td><a href=\"' + model.URI + '\">' + model.URI + '</a></td></tr>';
    sOut += '</tbody></table>';
    //form to apply the model
    sOut += '<div class="row ui-widget-content ui-corner-all" style="padding:5px;">';
    sOut += '<form action="'+ model.URI + '" method="POST" >'
    sOut += '<label class="five columns alpha">Enter <a href="'+root+'/dataset" target="dataset">dataset</a> or <a href="'+root+'/compound" target="compound">compound URI</a> <br> to apply the model</label>'
    sOut += '<input class="eight columns omega" type="text" name="dataset_uri" value="">';
    sOut += '<input class="three columns omega" type="submit" class="ui-button ui-widget ui-state-default ui-corner-all"  aria-disabled="false" role="button" value="Predict">';
    sOut += '</form></div>';
     
    return sOut;
}

/**
 * Read single model
 */
function readModel(root,url) {
	  $.ajax({
	        dataType: "json",
	        url: url,
	        cache: false, //to cope with IE8
            data: {
	              media:"application/json"
	        },	       	        
	        success: function(data, status, xhr) {
	        	$.each(data["model"],function(index, entry) {
	        		renderModel(entry,root,null);
	        	});
	        },
	        error: function(xhr, status, err) {
	        	renderModel(null,root,err);
	        },
	        complete: function(xhr, status) {
	        }
	     });
}

/**
 * 
 * @param entry
 * @param root
 * @param err
 */
function renderModel(entry,root,err) {
	if (entry==null) {
		$("#model_title").text("N/A");
		return;
	}
	var icon = '<span class="ui-icon ui-icon-pin-s" style="float: left; margin-right: .1em;"></span>';
	$("#model_title").text(entry["title"]);
	$("#model_title").prop("title",entry["ambitprop"]["content"]);

	$("#model_algorithm").html(entry["algorithm"]["URI"]);
	$("#model_algorithm").prop('href',entry["algorithm"]["URI"]);
	
	if ((entry["trainingDataset"]===undefined) || (entry["trainingDataset"]=="") || (entry["trainingDataset"]==null)) {
		$("#model_training").html("N/A");
	} else {
		$("#model_training").html(entry["trainingDataset"]);
		$("#model_training").prop('href',
				root + "/ui/_dataset?dataset_uri=",
				encodeURIComponent(entry["trainingDataset"])
		);
	}
	
	$("#model_img").prop('src',root + entry["algorithm"]["img"]);
	
	$("#help_action").html(
		"Once a model is built, it is assigned a <a href='" + 
		root + "/model' target='model'>model URI</a> and can be applied to <a href='" + 
		root + "/dataset' target='dataset'>datasets</a> and <a href='" +
		root + "/compound' target='compound'>compounds</a>." +
		"The result is a dataset, identified by a <a href='"  + root + "/dataset' target='dataset'>dataset URI</a>.");

	loadFeatureList(entry.URI+"/predicted","#vpredicted",100);
	loadFeatureList(entry.URI+"/independent","#vindependent",100);
	loadFeatureList(entry.URI+"/dependent","#vdependent",100);
	
}

function loadDatasetMeta(root,url,deleteVisible) {
		
    $.ajax({
        url: url + "?media=application/json",
        dataType: "json",
        data: {
          media:"application/json"	            	
        },
        success: function( data ) {
        	$.each(data.dataset,function(index,value) {
        		
        		try {
        			if (value.title.toLowerCase().indexOf("derek")>=0) $("#derekhelp").show(); else $("#derekhelp").hide();
        		} catch (err) { $("#derekhelp").hide();}
        		$("#title").val(value.title);
        		$("#rightsHolder").val(value.rightsHolder);

        		$("#license_type").text(value.rights.type);
        		$("#seealso").text(value.seeAlso);
        		if ((value.source != null) && (value.source.indexOf('http')==0))
        			$("#source").html("<a href='"+value.source+"'>"+value.source+"</a>");
        		else	
        			$("#source").text(value.source);
        		
        		$("#license").val(value.rights.URI);
        		 $('[name=licenseOptions] option').filter(function() { 
        		        return ($(this).val() == value.rights.URI); 
        		    }).prop('selected', true);
        	});
        	
        }
      });
}

function defineDatasetsTable(root,url,deleteVisible) {
	var oTable = $('.datasetstable').dataTable( {
	"sAjaxDataProp" : "dataset",
	"sAjaxSource": url,	
	"sSearch": "Filter:",
	"bJQueryUI" : true,
	"bSearchable": true,
	"bProcessing" : true,
	"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
	"sSearch": "Filter:",
	"bPaginate" : true,
	"sPaginationType": "full_numbers",
	"sPaginate" : ".dataTables_paginate _paging",
	"oLanguage": {
            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
            "sLoadingRecords": "No datasets found.",
            "sZeroRecords": "No datasets found.",
            "sEmptyTable": "No datasets available.",
            "sInfo": "Showing _TOTAL_ datasets (_START_ to _END_)",
            "sLengthMenu": 'Display <select>' +
          '<option value="10">10</option>' +
          '<option value="20">20</option>' +
          '<option value="50">50</option>' +
          '<option value="100">100</option>' +
          '<option value="-1">all</option>' +
          '</select> datasets.'	            
    },	
    "aoColumnDefs": [
    				{ //0
    					"aTargets": [ 0 ],	
    					"sClass" : "center",
    					"bSortable" : false,
    					"bSearchable" : false,
    					"mData" : null,
    					"bUseRendered" : false,	
    					sWidth : "24px",
    					"fnRender" : function(o,val) {
     		               	var sOut = "<a href='"+o.aData.URI +"/similarity?search=c1ccccc1'><span class='ui-icon ui-icon-heart' style='float: left; margin: .1em;' title='Similarity search within the dataset'></span></a>&nbsp;";
     		                sOut += "<a href='"+o.aData.URI +"/smarts?search=c1ccccc1'><span class='ui-icon ui-icon-search' style='float: left; margin: .1em;' title='Substructure search within the dataset'></span></a> ";
    						return sOut;
    					}
    				},	     	            
    	  			{  
    				  "bSortable" : true,
     	              "mDataProp":"stars",
     	              "aTargets": [ 1 ],
     	              "bUseRendered" : false,	
     	              sWidth : "2em",
	  					"fnRender" : function(o,val) {
							 return  "<span class='ui-icon ui-icon-star' style='display:inline-block' title='Dataset quality stars rating (worst) 1-10 (best)'></span>"+val;			
						}     	              
    	  			},     	 
    	  			{ "sTitle": "Title", 
    	  			  "mDataProp":"title", 
    	  			  "aTargets": [ 2 ],	
    		          "bUseRendered" : false,	
    		          "fnRender": function ( o, val ) {
  		        	    	var shortURI = o.aData.URI;
  		        	    	pos =  shortURI.lastIndexOf("/");
  		        	    	if (pos>=0) shortURI = shortURI.substring(pos+1); 
  		        	    	var href = root + "/ui/_dataset?dataset_uri="+encodeURIComponent(o.aData.URI);
    		        	    var sOut = "<a target='table' href='"+ href +
    		        	   		"' title='Click to view the dataset at "+ o.aData.URI+" as a table'><span class='ui-icon ui-icon-link' style='float: left; margin: .1em;'></span>D"+
    		        	   		shortURI+"</a> " ;
    		        	    
    		        	    sOut += "<br/>"+val;
    		        	    
    		               var seeAlso =  o.aData["seeAlso"];
    		               if ((seeAlso != undefined) && (seeAlso != null)) {
    		            	   if (seeAlso.indexOf('http')==0)
    		            		   sOut += ("<br/><a href='" + seeAlso + "' target=_blank title='"+seeAlso+"'>Source</a>");
    		               }
    		               var rights =  o.aData["rights"];
    		               if ((rights != undefined) && (rights != null)) {
    		            	   if (rights["URI"].indexOf('http')==0)
    		            		   sOut += (" | " + "<a href='" + rights["URI"] + "' target=_blank title='"+rights["URI"]+"'>"+rights["type"] +"</a>");
    		            	   else sOut += " | " + rights["URI"];
    		               }
    		               sOut += " | <a href='"+o.aData.URI +"/metadata'>Metadata</a>";
   		                   sOut += "<br/><a href='"+href+"'>Browse structures and properties</a>";
   		                   sOut += " | <a href='"+ root + "/ui/_dataset?dataset_uri="+encodeURIComponent(o.aData.URI+"/compounds") +"'>Structures only</a>";
   		                   sOut += " | <a href='"+o.aData.URI +"/feature'>Properties list</a>";
   		                   sOut += " | <a href='"+root +"/admin/policy?search="+ o.aData.URI  + "' target='policy'>OpenAM access rights</a>";
   		                   //sOut += " | <a href='"+root +"/algorithm/toxtreecramer?dataset_uri="+ o.aData.URI  + "' target='predict'>Predict</a>";
    		               return sOut;
    		          }
    		  		},   
    	  			{  
      				  "bSortable" : true,
       	              "mData":null,
       	              "aTargets": [ 3 ],
       	              "bUseRendered" : false,	
       	              sWidth : "10%",
  	  					"fnRender" : function(o,val) {
	  						 var sOut = "<a href='"+root + "/model?dataset=" + encodeURIComponent(o.aData.URI) +"' title='Browse models, using this datasets as a training dataset'>View</a>";
	   		                 sOut += "  <a href='"+root +"/algorithm/superbuilder?dataset_uri="+ o.aData.URI  + "' target='build' title='Build new model with this dataset as a training dataset'>Build</a>";
	  						 return sOut;
  						}     	              
      	  			},      		  		
    	  			{ "sTitle": "Download", 
    	  			  "mData":null , 
    	  			  "aTargets": [ 4 ],	
    	  			  "bSortable" : false,
    	  			  "bSearchable" : false,
    	  			  sWidth: "5%",
    		  	      "bUseRendered" : false,	
    			       "fnRender": function ( o, val ) {
    			    	   val = o.aData["URI"];
    			    	   var sOut = "<a href='"+getMediaLink(val,"chemical/x-mdl-sdfile")+"' id='sdf'><img src='"+root+"/images/sdf.jpg' alt='SDF' title='Download as SDF' ></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/csv")+"' id='csv'><img src='"+root+"/images/excel.png' alt='CSV' title='Download as CSV (Comma delimited file)'/></a> ";
    			    	   var id = "d" + getID();
    			    	   sOut += "<span style='display:none;' id='"+id+"'>"; 
    			    	   sOut += "<a href='"+getMediaLink(val,"text/plain")+"' id='txt'><img src='"+root+"/images/excel.png' alt='TXT' title='Download as TXT'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-cml")+"' id='cml'><img src='"+root+"/images/cml.jpg' alt='CML' title='Download as CML (Chemical Markup Language)'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-daylight-smiles")+"' id='smiles'><img src='"+root+"/images/smi.png' alt='SMILES' title='Download as SMILES'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-inchi")+"' id='inchi'><img src='"+root+"/images/inchi.png' alt='InChI' title='Download as InChI'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/x-arff")+"' id='arff'><img src='"+root+"/images/weka.png' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/x-arff-3col")+"' id='arff3col'><img src='"+root+"/images/weka.png' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"application/rdf+xml")+"' id='rdfxml'><img src='"+root+"/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/n3")+"' id='rdfn3'><img src='"+root+"/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"application/json")+"' id='json' target=_blank><img src='"+root+"/images/json.png' alt='json' title='Download as JSON'></a>";
    			    	   sOut += "</span>";
    			    	   sOut += " <a href='#' onClick='$(\"#"+id+"\").toggle();' title='Click here for more download formats (CML, RDF, ARFF, JSON, etc.)'>...</a> ";
    			    	   return sOut;
    			      }
    		  		},
    	  			{  
      	  			  "mDataProp":"stars" , 
      	  			  "aTargets": [ 5 ],	
      	  			  "sWidth" : "32px",
      	  			  "bVisible" : deleteVisible,
      		  	      "bUseRendered" : false,	
      			       "fnRender": function ( o, val ) {
      			    	   val = o.aData["URI"];
      			    	   var statusSelector= 's'+ o['iDataRow'] ;
      			    	   var sOut = "";
    		               if (o.aData.stars<=5) {
     		                	sOut = "<a href='#' onClick='deleteDataset(\""+o.aData.URI +
     		                		"\",\"#" + statusSelector+  "\");'><span class='ui-icon ui-icon-trash' style='float: left; margin: .1em;' title='Remove dataset "+ 
     		                		o.aData.URI + "'></span></a><br/><span class='msg' id='" + statusSelector + "'></span>";
     		                		
     		               }
      			    	   return sOut;
      			      }
      		  		}	    		  		
     				],
 	  "aaSorting": [[1, 'desc']]		  
	});
	return oTable;
}

function getMediaLink(uri, media) {
	return uri + "?media=" + encodeURIComponent(media);
}

function getDownloadLinksCompound(root,uri) {
	   val = uri;
	   var sOut = "<a href='"+getMediaLink(val,"chemical/x-mdl-sdfile")+"' id='sdf'><img src='"+root+"/images/sdf.jpg' alt='SDF' title='Download as SDF' ></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/csv")+"' id='csv'><img src='"+root+"/images/excel.png' alt='CSV' title='Download as CSV (Comma delimited file)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/plain")+"' id='txt'><img src='"+root+"/images/excel.png' alt='TXT' title='Download as TXT'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"chemical/x-cml")+"' id='cml'><img src='"+root+"/images/cml.jpg' alt='CML' title='Download as CML (Chemical Markup Language)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"chemical/x-daylight-smiles")+"' id='smiles'><img src='"+root+"/images/smi.png' alt='SMILES' title='Download as SMILES'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"chemical/x-inchi")+"' id='inchi'><img src='"+root+"/images/inchi.png' alt='InChI' title='Download as InChI'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/x-arff")+"' id='arff'><img src='"+root+"/images/weka.png' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/x-arff-3col")+"' id='arff3col'><img src='"+root+"/images/weka.png' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"application/rdf+xml")+"' id='rdfxml'><img src='"+root+"/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/n3")+"' id='rdfn3'><img src='"+root+"/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"application/json")+"' id='json' target=_blank><img src='"+root+"/images/json.png' alt='json' title='Download as JSON'></a>";
	   return sOut;
}

function definePropertyValuesTable(root,url,tableSelector) {
	var oTable = $(tableSelector).dataTable( {
		"sAjaxSource": url,		
		"sAjaxDataProp" : "",
		"bProcessing" : true,
		"bServerSide" : false,
		"bStateSave" : false,
		"bJQueryUI" : true,
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"bDeferRender": true,
		"bSearchable": true,
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
		"aoColumnDefs" : [
		     {
		    	 "aTargets": [ 0 ],	
		    	 "mDataProp" : "name",
				 "fnRender" : function(o,val) {
				     return  "<span class='ui-icon ui-icon-link' style='display:inline-block' title='Click to show property details'></span><a href='"+o.aData["uri"]+"' target=_blank>"+val+"</a>";			
				 }   		    	 
		     },
		     {
		    	 "aTargets": [ 1 ],	
		    	 "mDataProp" : "units"
		     },		     
		     {
		    	 "aTargets": [ 2 ],	
		    	 "mDataProp" : "value"
		     },		     
		     {
		    	 "aTargets": [ 3 ],	
		    	 "mDataProp" : "sameAs"
		     },		     
		     {
		    	 "aTargets": [ 4 ],	
		    	 "mDataProp" : "source",
				 "fnRender" : function(o,val) {
				     return  "<span class='ui-icon ui-icon-link' style='display:inline-block' title='Click to show property details'></span><a href='"+val+"'>"+o.aData["sourceType"]+"</a>";			
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
					identifiers(json);
					$.each(json.dataEntry[0].lookup.misc, function(index, object) {
						if (object==undefined) return;
						var feature = json.feature[object];
						var value = json.dataEntry[0].values==null?"":json.dataEntry[0].values[object];
						if (value == null) value="";
		                aoData.push({
		                	"uri":object,
		                	"name":feature.title,
		                	"units":feature.units,
		                	"sameAs":feature.sameAs.replace("http://www.opentox.org/echaEndpoints.owl#","Endpoint:"),
		                	"source":feature.source==null?"":feature.source["URI"],
		                	"sourceType":feature.source==null?"":feature.source["type"],
		                	"value":value
		                });
		            });			
					fnCallback(aoData);
				},
				"cache" : false,
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
						//console.log(xhr.status + " " + xhr.statusText + " " + xhr.responseText);
			        	alert("Error loading data " + xhr.status + " " + error);
					}
					}
					oSettings.oApi._fnProcessingDisplay(oSettings, false);
				}
			});
		}
	});
	return oTable;
}

/**
 * Autocomplete for dataset URI field
 * @param id
 */
function datasetAutocomplete(id,datasetroot,maxhits) {
	$( id ).autocomplete({
	      source: function( request, response ) {
	          $.ajax({
	            url: datasetroot,
	            dataType: "json",
	            data: {
	              media:"application/json",	            	
	              max: maxhits,
	              search: "^"+request.term
	            },
	            success: function( data ) {
	              response( $.map( data.dataset, function( item ) {
		        	var shortURI = item.URI;
		        	pos =  shortURI.lastIndexOf("/");
		        	if (pos>=0) shortURI = "D" + shortURI.substring(pos+1) + ": "; 
		        	else shortURI = "";
	                return {
	                  label: shortURI + item.title,
	                  value: item.URI
	                }
	              }));
	            }
	          });
	        },
	        minLength: 2,
	        open: function() {
		        $('.ui-autocomplete').css('width', '450px');
		    } 	        
	        /*
	        select: function( event, ui ) {
	          console.log( ui.item ?
	            "Selected: " + ui.item.label :
	            "Nothing selected, input was " + this.value);
	        },
	        open: function() {
	          $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
	        },
	        close: function() {
	          $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
	        }	
	        */		        		
	});
}

/**
 * Autocomplete for feature URI field
 * @param id
 */
function featureAutocomplete(id,iddataset,featureroot,maxhits) {
	$( id ).autocomplete({
	      source: function( request, response ) {
	    	  var featureLookup = featureroot;
	    	  $(iddataset).each(function(a,b) {
	    		 try {  
	    			 var datauri = $(b).attr('value');
	    			 if ((datauri != undefined) && ("" != datauri)) { 
	    				 featureLookup=$(b).attr('value')+"/feature"; maxhits=0;
	    			 }
	    		 } catch (err) {featureLookup = featureroot;} 
	    	  });
	          $.ajax({
	            url: featureLookup,
	            dataType: "json",
	            data: {
	              media:"application/json",
	              max: maxhits,
	              search: "^"+request.term
	            },
	            success: function( data ) {
	              response( $.map( data.feature, function( item , index) {
			        var shortURI = index;
			        pos =  shortURI.lastIndexOf("/");
			        if (pos>=0) shortURI = "F" + shortURI.substring(pos+1) + ": "; 
			        else shortURI = "";
			        
	                return {
	                  label: shortURI + item.title + " " + item.units,
	                  value: index
	                }
	              }));
	            }
	          });
	        },
	        minLength: 1,
	        open: function() {
	        	  $('.ui-autocomplete').css('width', '450px');
		    }       
	});
}

/**
 * Autocomplete for learning algorithm URI field
 * @param id
 */
function algorithmAutocomplete(id,algroot,algtype,maxhits) {
	$( id ).autocomplete({
	      source: function( request, response ) {
	          $.ajax({
	            url: algroot,
	            dataType: "json",
	            data: {
	              media:"application/json",
	              max: maxhits,
	              //search: "^"+request.term,
	              type: algtype
	            },
	            success: function( data ) {
	            	response( $.map( data.algorithm, function( item ) {
				        var shortURI = item.uri;
				        pos =  shortURI.lastIndexOf("/");
				        if (pos>=0) shortURI = "["+ shortURI.substring(pos+1) + "] "; 
				        else shortURI = "";
				        
		                return {
		                  label: shortURI + item.name,
		                  value: item.uri
		                }
		              }));
	            }
	          });
	        },
	        minLength: 1,
	        open: function() {
	        	  $('.ui-autocomplete').css('width', '450px');
		    }       
	});
}

/**
 * Autocomplete for learning algorithm URI field
 * @param id
 */
function modelAutocomplete(id,modelroot,algtype,maxhits) {
	$.widget( "custom.catcomplete", $.ui.autocomplete, {
		_renderMenu: function(ul, items){
	        var self = this;
	        var currentCategory = "";
	        $.each(items, function(index, item){
	            if(item.category != currentCategory){
	                ul.append(" <li class='ui-autocomplete-category'>" + item.category + "</li>");
	                currentCategory = item.category;
	            }
	            self._renderItem(ul, item);
	        });
	    }
	  });	
	$( id ).catcomplete({
	      source: function( request, response ) {
	          $.ajax({
	            url: modelroot,
	            dataType: "json",
	            data: {
	              media:"application/json",
	              max: maxhits,
	              search: "^"+request.term
	            },
	            success: function( data ) {
	            	response( $.map( data.model, function( item ) {
				        var shortURI = item.URI;
				        pos =  shortURI.lastIndexOf("/");
				        if (pos>=0) shortURI = "[M"+ shortURI.substring(pos+1) + "] "; 
				        else shortURI = "";
				        
		                return {
		                  label: shortURI + item.title,
		                  value: item.URI,
		                  category: (item.title.indexOf("ToxTree")>=0)?"ToxTree":item.algorithm.URI
		                }
		              }));
	            }
	          });
	        },
	        minLength: 1,
	        open: function() {
	        	  $('.ui-autocomplete').css('width', '450px');
		    }
	});
}

/**
 * Autocomplete for learning algorithm URI field
 * @param id
 */
function modelVarsAutocomplete(id,modelroot,maxhits) {
	$.widget( "custom.catcomplete", $.ui.autocomplete, {
		_renderMenu: function(ul, items){
	        var self = this;
	        var currentCategory = "";
	        $.each(items, function(index, item){
	            if(item.category != currentCategory){
	                ul.append(" <li class='ui-autocomplete-category'>" + item.category + "</li>");
	                currentCategory = item.category;
	            }
	            self._renderItem(ul, item);
	        });
	    }
	  });	
	$( id ).catcomplete({
	      source: function( request, response ) {
	          $.ajax({
	            url: modelroot,
	            dataType: "json",
	            data: {
	              media:"application/json",
	              max: maxhits,
	              search: "^"+request.term
	            },
	            success: function( data ) {
	            	response( $.map( data.model, function( item ) {
				        var shortURI = item.URI;
				        pos =  shortURI.lastIndexOf("/");
				        if (pos>=0) shortURI = "["+ shortURI.substring(pos+1) + "] "; 
				        else shortURI = "";
				        
		                return {
		                  label: shortURI + item.title,
		                  value: item.URI + "/predicted",
		                  category: (item.title.indexOf("ToxTree")>=0)?"ToxTree":item.algorithm.URI
		                }
		              }));
	            }
	          });
	        },
	        minLength: 1,
	        open: function() {
	        	  $('.ui-autocomplete').css('width', '450px');
		    }
	});
}

/**
 * Lists OpenTox Features, as in /feature
 * @param root
 * @param url
 * @returns
 */
function defineFeatureTable(root,url) {

	var oTable = $('#feature').dataTable( {
		"sAjaxDataProp" : "feature",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
				{ "mDataProp": "URI" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 0 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
  		        	    var shortURI = o.aData.URI;
		        	    pos =  shortURI.lastIndexOf("/");
		        	    if (pos>=0) shortURI = shortURI.substring(pos+1); 
		                return "<a href='"+o.aData.URI +"' title='Click to view the feature at " + 
		                		o.aData.URI+" '><span class='ui-icon ui-icon-link' style='float: left; margin: .1em;' ></span>F" +
		                		shortURI + "</a>";
					  }
				},
				{ "mDataProp": "feature.title" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 1 ],	
					  "sWidth" : "25%",
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
			                var sOut = val;
			                sOut += " <a href='"+root+"/feature?search="+encodeURIComponent(val)+
			                	"' title='Click to view the features with the same name'><span class='ui-icon ui-icon-search' style='float: left; margin: .1em;' ></span></a>";
			                return sOut;
					  }
				},				
				{ "mDataProp": "feature.units" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 2 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "5%",
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return val;
					  }
				},
				{ "mDataProp": "feature.sameAs" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 3 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "sWidth" : "15%",
					  "fnRender" : function(o,val) {
						  var ont = "<a class='qxternal' href='http://apps.ideaconsult.net:8080/ontology?uri=" + encodeURIComponent(val) +"' target=_blank title='"+val+"'>owl:sameAs</a>";
						  return	(val.replace("http://www.opentox.org/echaEndpoints.owl#","Endpoint: ")
						  			.replace("http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#","Algorithm: ")
						  			.replace("http://www.opentox.org/api/1.1#","OpenTox: ")) + "<br>" + ont;;
					  }
				},
				{ "mDataProp": "feature.source.URI" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 4 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "20%",
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  var uri = (val.indexOf("dataset")>0)?(val+"/metadata"):val;
						  return "<a href='"+uri+"' title='"+val+"' target='origin'>"+o.aData.feature.source.type+"</a>";
					  }
				},
				{ "mDataProp": "feature.isNumeric" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 5 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "10%",
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
					  	  try {
						  return boolean2checkbox(val,"Numeric","String");
						  } catch (err) {
						  	return val;
						  }
					  }
				},
				{ "mDataProp": "feature.isNominal" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 6 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "10%",
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
					  	  try {
						  return boolean2checkbox(val,"Yes","No");
						  } catch (err) {
						  	return val;
						  }
					  }
				},
				{ "mDataProp": "URI" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [7 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return "<a href='"+val+"/annotation' target='annotation'>More</a>";
					  }
				}					
			],
		"sSearch": "Filter:",
		"bJQueryUI" : true,
		"bSearchable": true,
		"sAjaxSource": url,
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"oLanguage": {
	            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No properties found.",
	            "sZeroRecords": "No properties found.",
	            "sInfo": "Showing _TOTAL_ properties (_START_ to _END_)",
	            "sLengthMenu": 'Display <select>' +
              '<option value="10">10</option>' +
              '<option value="20">20</option>' +
              '<option value="50">50</option>' +
              '<option value="100">100</option>' +
              '<option value="-1">all</option>' +
              '</select> properties.'	            
	    },
	    "fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
			oSettings.jqXHR = $.ajax({
				"type" : "GET",
				"url" : sSource,
				"data" : aoData,
				"dataType" : "json",
				"contentType" : "application/json",
				"cache" : true,
				"success": function(result){
		              var mapped = $.map( result.feature, function( item , index) {
			                return {
			                  URI : index,
			                  feature : item
			                }
			              });
					  fnCallback({"feature":mapped});
				},
				"error" : function(xhr, textStatus, error) {
					switch (xhr.status) {
					case 403: {
			        	alert("Restricted access. You are not authorized to access the requested properties.");
						break;
					}
					case 404: {
						//not found
						break;
					}
					default: {
						//console.log(xhr.status + " " + xhr.statusText + " " + xhr.responseText);
			        	alert("Error loading properties " + xhr.status + " " + error);
					}
					}
					oSettings.oApi._fnProcessingDisplay(oSettings, false);
				}
			});
		}	    
	} );
	return oTable;
}

function defineFeatureAnnotationTable(root,url) {

	var oTable = $('#annotation').dataTable( {
		"sAjaxDataProp" : "annotation",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
				{ "mDataProp": "type" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 0 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
  		        	    return val;
					  }
				},
				{ "mDataProp": "p" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 1 ],	
					  "sWidth" : "33%",
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
			                return val;
					  }
				},				
				{ "mDataProp": "o" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 2 ],	
					  "sWidth" : "33%",
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
			                return val;
					  }
				}				
			],
		"sSearch": "Filter:",
		"bJQueryUI" : true,
		"bSearchable": true,
		"sAjaxSource": url,
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"oLanguage": {
	            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No annotations found.",
	            "sZeroRecords": "No annotations found.",
	            "sInfo": "Showing _TOTAL_ properties (_START_ to _END_)",
	            "sLengthMenu": 'Display <select>' +
              '<option value="10">10</option>' +
              '<option value="20">20</option>' +
              '<option value="50">50</option>' +
              '<option value="100">100</option>' +
              '<option value="-1">all</option>' +
              '</select> annotations.'	            
	    }
	   	    
	} );
	return oTable;
}


function boolean2checkbox(val,yes,no) {
    try {
		var istrue = false;
		if(typeof val === 'boolean') istrue = val;
		else istrue =  (val.toLowerCase() === 'true');
		return "<input type='checkbox' "+ (istrue?"checked":"") + " title="  + (istrue?yes:no) + " disabled> " + (istrue?yes:no) ;
	} catch (err) {
		return val;
	}
}
/**
 * Features list box
 * @param root
 * @param selectTag
 * @param allelesTag
 */
function loadFeatureList(featureLookup,selectTag,maxhits) {
	  //clear the list	
  //$(selectTag).html("");
	  //get all features
  
  $.ajax({
      url: featureLookup,
      dataType: "json",
      data: {
        media:"application/json",
        max: maxhits
      },
      success: function( data ) {
    	  $.map( data.feature, function( item , index) {
    		  $("<option value='" + index + "'>" + item.title + "&nbsp;" +item.units + "</option>").appendTo(selectTag);
        	  return item.title;
          }
        );
      }
    });
  
}	

function loadChart(root,datasetselector,xselector,yselector,chartselector) {
	if ($(datasetselector).attr("value")=="") alert("No dataset defined!");
	else {
	var uri = root + "/chart/xy?dataset_uri="+encodeURIComponent($(datasetselector).attr("value"))+"&feature_uris[]="+encodeURIComponent($(xselector).val())+"&feature_uris[]="+encodeURIComponent($(yselector).val());
	$(chartselector).attr('src',uri);
	}
}

function loadPieChart(root,datasetselector,xselector,chartselector) {
	if ($(datasetselector).attr("value")=="") alert("No dataset defined!");
	else {	
	var uri = root + "/chart/pie?dataset_uri="+encodeURIComponent($(datasetselector).attr("value"))+"&feature_uris[]="+encodeURIComponent($(xselector).val());
	$(chartselector).attr('src',uri);
	}
}

function loadHistogramChart(root,datasetselector,xselector,chartselector) {
	if ($(datasetselector).attr("value")=="") alert("No dataset defined!");
	else {
	var uri = root + "/chart/histogram?w=800&h=250&dataset_uri="+encodeURIComponent($(datasetselector).attr("value"))+"&feature_uris[]="+encodeURIComponent($(xselector).val());
	$(chartselector).attr('src',uri);
	}
}

function downloadForm(query_uri) {
	$.each(_ambit.downloads,function(index,value) {
		var durl = query_uri +  ((query_uri.indexOf("?")<0)?"?":"&") + "media="+ encodeURIComponent(value.mime);
		$('#download #'+value.id).attr('href',durl);
	});
}

function defineFacetsTable(root,url,selector) {
	var oTable = $(selector).dataTable( {
		"sAjaxDataProp" : "facet",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
 				{ "mData": "value" , 
 				  "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "sWidth" : "45%",
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  var sOut = (o.aData["value"]===undefined)? o.aData["uri"]:o.aData["value"];
					  return "<a href='"+o.aData["uri"]+"' title='"+o.aData["uri"]+"'>"+sOut+"</a>";
				  }
				},
 				{ "mData": "subcategory" , 
	 				  "asSorting": [ "asc", "desc" ],
					  "aTargets": [1 ],
					  "sWidth" : "45%",
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return val;
					  }
					},				
				{ "mDataProp": "count" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 2 ],
				  "sWidth" : "10%",
				  "bSearchable" : true,
				  "bSortable" : true
				}			
			],
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',	
		"bJQueryUI" : true,
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"bDeferRender": true,
		"bSearchable": true,
		"sAjaxSource": url,
		"oLanguage": {
				"sSearch": "Filter:",
				"sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No records found."
	    }
	} );
	return oTable;
}


function defineSubstanceOwnerTable(root,url,selector) {
	var oTable = $(selector).dataTable( {
		"sAjaxDataProp" : "facet",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
      
 				{ "mData": "subcategory" , 
 				  "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  var sOut = (o.aData["subcategory"]===undefined)? o.aData["value"]:o.aData["subcategory"];
					  return sOut;
				  }
				},
 				{ "mData": "value" , 
	 				  "asSorting": [ "asc", "desc" ],
					  "aTargets": [1 ],
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return "<a href='" +root + "/substanceowner/" + val + "/substance' title='"+o.aData["uri"]+"'>Substances ["+o.aData["count"]+"]</a>";
					  }
					},			
	 				{ "mData": "value" , 
		 				  "asSorting": [ "asc", "desc" ],
						  "aTargets": [2 ],
						  "bSearchable" : true,
						  "bUseRendered" : false,
						  "bSortable" : true,
						  "fnRender" : function(o,val) {
							  return "<a href='" +root + "/substanceowner/" + val + "/dataset' title='"+o.aData["uri"]+"'>Substances and data</a><br/>" +
							  		 "<a href='"+o.aData["uri"]+"' title='"+o.aData["uri"]+"'>Chemical structures</a>";
						  }
						}		,
		 				{ "mData": "value" , 
			 				  "asSorting": [ "asc", "desc" ],
							  "aTargets": [3 ],	
							  "bSearchable" : true,
							  "bUseRendered" : false,
							  "bSortable" : true,
							  "fnRender" : function(o,val) {
								  return "<a href='" +root + "/substanceowner/" + val + "' title='"+o.aData["uri"]+"'>"+val+"</a>" ;
							  }
							}								
 						
			],
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',	
		"bJQueryUI" : true,
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"bDeferRender": true,
		"bSearchable": true,
		"sAjaxSource": url,
		"oLanguage": {
				"sSearch": "Filter:",
				"sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No records found."
	    }
	} );
	return oTable;
}




function definePolicyTable(root,url,selector) {
	var oTable = $(selector).dataTable( {
		"sAjaxDataProp" : "policy",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
 				{ "mData": "uri" , 
 				  "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  return val;
				  }
				},
 				{ "mData": "id" , 
	 				  "asSorting": [ "asc", "desc" ],
					  "aTargets": [1 ],
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return "<a href='"+root+"/admin/policy/"+ encodeURIComponent(val)+"'>"+val + "</a>";
					  }
					},				
				{ "mDataProp": "xml" ,
				  "aTargets": [ 2 ],
				  "sWidth" : "10%",
				  "bSortable" : false,
				  "bSearchable" : true
				},
				{ "mData": null , 
	 				  "asSorting": [ "asc", "desc" ],
					  "aTargets": [3 ],
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return "<form action='"+root+"/admin/policy/"+o.aData['id']+"?method=DELETE' method='POST'><input type='submit' value='Delete policy'></a>";
					  }
					}					
			],
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',	
		"bJQueryUI" : true,
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"bDeferRender": true,
		"bSearchable": true,
		"sAjaxSource": url,
		"oLanguage": {
				"sSearch": "Filter:",
				"sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No policies found."
	    }
	} );
	return oTable;
}

function defineRolesTable(root,url,selector) {
	var oTable = $(selector).dataTable( {
		"sAjaxDataProp" : "roles",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
 				{ 
 				  "mData": "role" ,  						
 				  "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  return val;
				  }
				}
 								
			],
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',	
		"bJQueryUI" : true,
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"bDeferRender": true,
		"bSearchable": true,
		"sAjaxSource": url,
		"oLanguage": {
				"sSearch": "Filter:",
				"sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No roles found."
	    },
	    "fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
			oSettings.jqXHR = $.ajax({
				"type" : "GET",
				"url" : sSource,
				"data" : aoData,
				"dataType" : "json",
				"contentType" : "application/json",
				"cache" : true,
				"success": function(result) {
						var roles = $.map( result.roles, function( value,key ) {
							 	return {"role" : value};
							});
						console.log(roles);
						fnCallback({"roles" : roles});
				},
				"error" : function(xhr, textStatus, error) {
					switch (xhr.status) {
					case 403: {
			        	alert("Restricted access. You are not authorized to access the requested algorithms.");
						break;
					}
					case 404: {
						//not found
						break;
					}
					default: {
						//console.log(xhr.status + " " + xhr.statusText + " " + xhr.responseText);
			        	alert("Error loading algorithms " + xhr.status + " " + error);
					}
					}
					oSettings.oApi._fnProcessingDisplay(oSettings, false);
				}
			});
	    }
	} );
	return oTable;
}


function loadMetadata(root,dataseturi) {
$.ajax({
    url: dataseturi,
    dataType: "json",
    data: {
      media:"application/json",
      max: maxhits
    },
    success: function( data ) {
  	  
    }
  });

}	


function defineBundlesTable(root,url,deleteVisible,profile) {
	if ("lri" == profile) return defineBundlesTable_lri(root,url,deleteVisible);
	
	var oTable = $('.datasetstable').dataTable( {
	"sAjaxDataProp" : "dataset",
	"sAjaxSource": url,	
	"sSearch": "Filter:",
	"bJQueryUI" : true,
	"bSearchable": true,
	"bProcessing" : true,
	"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
	"sSearch": "Filter:",
	"bPaginate" : true,
	"sPaginationType": "full_numbers",
	"sPaginate" : ".dataTables_paginate _paging",
	"oLanguage": {
            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
            "sLoadingRecords": "No substance datasets found.",
            "sZeroRecords": "No substance datasets found.",
            "sEmptyTable": "No substance datasets available.",
            "sInfo": "Showing _TOTAL_ substance datasets (_START_ to _END_)",
            "sLengthMenu": 'Display <select>' +
          '<option value="10">10</option>' +
          '<option value="20">20</option>' +
          '<option value="50">50</option>' +
          '<option value="100">100</option>' +
          '<option value="-1">all</option>' +
          '</select> substance datasets.'	            
    },	
    "aoColumnDefs": [
   	     	            
    	  			{  
    				  "bSortable" : true,
     	              "mDataProp":"stars",
     	              "aTargets": [ 0 ],
     	              "bUseRendered" : false,	
     	               "sWidth" : "2em",
	  					"fnRender" : function(o,val) {
							 return  "<span class='ui-icon ui-icon-star' style='display:inline-block' title='Dataset quality stars rating (worst) 1-10 (best)'></span>"+val;			
						}     	              
    	  			},     	 
    	  			{ "sTitle": "Title", 
    	  			  "mDataProp":"title", 
    	  			  "aTargets": [ 1 ],	
    		          "bUseRendered" : false,	
    		          "fnRender": function ( o, val ) {
  		        	    	var shortURI = o.aData.URI;
  		        	    	pos =  shortURI.lastIndexOf("/");
  		        	    	if (pos>=0) shortURI = shortURI.substring(pos+1); 
  		        	    	var href = o.aData.URI;
    		        	    var sOut = "<a target='table' href='"+ root + "/ui/assessment?bundle_uri=" + encodeURIComponent(href) +
    		        	   		"' title='Click to view the substance dataset at "+ o.aData.URI+" as a table'><span class='ui-icon ui-icon-link' style='float: left; margin: .1em;'></span>Substance dataset#"+
    		        	   		shortURI+"</a> " ;
    		        	    
    		        	    sOut += "<br/>"+val+ "<br/>";
    		        	   sOut += " <a href='"+o.aData.URI +"/metadata'>Metadata</a>";
     		               sOut += " <a href='"+o.aData.URI +"/compound'>Structures</a>";
    		               sOut += " | <a href='"+o.aData.URI +"/substance'>Substances</a>";
    		               sOut += " | <a href='"+o.aData.URI +"/dataset'>Substances and studies</a>";
   		                   sOut += " | <a href='"+o.aData.URI +"/property'>Properties list</a>";
   		                   sOut += " | <a href='"+o.aData.URI +"/summary'>Summary</a>";
   		                   sOut += " | <a href='"+o.aData.URI +"/matrix'>Working matrix</a>";
    		               var seeAlso =  o.aData["seeAlso"];
    		               if ((seeAlso != undefined) && (seeAlso != null)) {
    		            	   if (seeAlso.indexOf('http')==0)
    		            		   sOut += (" | <a href='" + seeAlso + "' target=_blank title='"+seeAlso+"'>Source</a>");
    		               }
    		               var rights =  o.aData["rights"];
    		               if ((rights != undefined) && (rights != null)) {
    		            	   if (rights["URI"].indexOf('http')==0)
    		            		   sOut += (" | " + "<a href='" + rights["URI"] + "' target=_blank title='"+rights["URI"]+"'>"+rights["type"] +"</a>");
    		            	   else sOut += " | " + rights["URI"];
    		               }

   		                   //sOut += " | <a href='"+root +"/algorithm/toxtreecramer?dataset_uri="+ o.aData.URI  + "' target='predict'>Predict</a>";
    		               return sOut;
    		          }
    		  		},   
    	  			{ "sTitle": "Download", 
    	  			  "mData":null , 
    	  			   "sWidth" : "10%",
    	  			  "aTargets": [2 ],	
    	  			  "bSortable" : false,
    	  			  "bSearchable" : false,
    		  	      "bUseRendered" : false,	
    			       "fnRender": function ( o, val ) {
    			    	   val = o.aData["URI"];
    			    	   var sOut = "<a href='"+getMediaLink(val,"chemical/x-mdl-sdfile")+"' id='sdf'><img src='"+root+"/images/sdf.jpg' alt='SDF' title='Download as SDF' ></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/csv")+"' id='csv'><img src='"+root+"/images/excel.png' alt='CSV' title='Download as CSV (Comma delimited file)'/></a> ";
    			    	   var id = "d" + getID();
    			    	   sOut += "<span style='display:none;' id='"+id+"'>"; 
    			    	   sOut += "<a href='"+getMediaLink(val,"text/plain")+"' id='txt'><img src='"+root+"/images/excel.png' alt='TXT' title='Download as TXT'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-cml")+"' id='cml'><img src='"+root+"/images/cml.jpg' alt='CML' title='Download as CML (Chemical Markup Language)'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-daylight-smiles")+"' id='smiles'><img src='"+root+"/images/smi.png' alt='SMILES' title='Download as SMILES'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-inchi")+"' id='inchi'><img src='"+root+"/images/inchi.png' alt='InChI' title='Download as InChI'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/x-arff")+"' id='arff'><img src='"+root+"/images/weka.png' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/x-arff-3col")+"' id='arff3col'><img src='"+root+"/images/weka.png' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"application/rdf+xml")+"' id='rdfxml'><img src='"+root+"/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/n3")+"' id='rdfn3'><img src='"+root+"/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"application/json")+"' id='json' target=_blank><img src='"+root+"/images/json.png' alt='json' title='Download as JSON'></a>";
    			    	   sOut += "</span>";
    			    	   sOut += " <a href='#' onClick='$(\"#"+id+"\").toggle();' title='Click here for more download formats (CML, RDF, ARFF, JSON, etc.)'>...</a> ";
    			    	   return sOut;
    			      }
    		  		},
    	  			{  
      	  			  "mDataProp":"stars" , 
      	  			  "aTargets": [ 3 ],	
      	  			  "sWidth" : "32px",
      	  			  "bVisible" : deleteVisible,
      		  	      "bUseRendered" : false,	
      			       "fnRender": function ( o, val ) {
      			    	   val = o.aData["URI"];
      			    	   var statusSelector= 's'+ o['iDataRow'] ;
      			    	   var sOut = "";
    		               if (o.aData.stars<=5) {
     		                	sOut = "<a href='#' onClick='deleteBundle(\""+o.aData.URI +
     		                		"\",\"#" + statusSelector+  "\");'><span class='ui-icon ui-icon-trash' style='float: left; margin: .1em;' title='Remove dataset "+ 
     		                		o.aData.URI + "'></span></a><br/><span class='msg' id='" + statusSelector + "'></span>";
     		                		
     		               }
      			    	   return sOut;
      			      }
      		  		}	    		  		
     				],
 	  "aaSorting": [[1, 'desc']]		  
	});
	return oTable;
}

function defineBundlesTable_lri(root,url,deleteVisible) {
	var oTable = $('.datasetstable').dataTable( {
	"sAjaxDataProp" : "dataset",
	"sAjaxSource": url,	
	"sSearch": "Filter:",
	"bJQueryUI" : true,
	"bSearchable": true,
	"bProcessing" : true,
	"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
	"sSearch": "Filter:",
	"bPaginate" : true,
	"sPaginationType": "full_numbers",
	"sPaginate" : ".dataTables_paginate _paging",
	"oLanguage": {
            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
            "sLoadingRecords": "No assessments.",
            "sZeroRecords": "No assessments.",
            "sEmptyTable": "No assessments.",
            "sInfo": "Showing _TOTAL_ assessments (_START_ to _END_)",
            "sLengthMenu": 'Display <select>' +
          '<option value="10">10</option>' +
          '<option value="20">20</option>' +
          '<option value="50">50</option>' +
          '<option value="100">100</option>' +
          '<option value="-1">all</option>' +
          '</select> assessments.'	            
    },	
    "aoColumnDefs": [
                     
     	  			{  
      				  "bSortable" : true,
       	              "mDataProp":"number",
       	              "aTargets": [ 0 ],
       	              "bUseRendered" : false,	
  	  					"fnRender" : function(o,val) {
    		        	    var sOut = "<a target='table' href='"+ root + "/ui/assessment?bundle_uri=" + encodeURIComponent(o.aData.URI) +
    		        	   		"' title='Click to view the assessments at "+ o.aData.URI+" as a table'>"+val+"</a> " ;
    		               return sOut;
  						}     	              
      	  			},     	  
     	  			{  
        				  "bSortable" : true,
         	              "mDataProp":"version",
         	              "aTargets": [ 1 ],
         	              "sWidth" : "5%",
         	              "bUseRendered" : false,	
    	  					"fnRender" : function(o,val) {
        		        	    var sOut = "<a target='table' href='"+ o.aData.URI+"/version" +
        		        	   		"' title='Click to view other versions "+ o.aData.URI+" as a table'>"+val+"</a> " ;
        		               return sOut;
    						}     	              
        	  		},      	  			
     	  			{  
        				  "bSortable" : true,
         	              "mDataProp":"source",
         	              "aTargets": [ 2 ],
         	             "sWidth" : "15%",
         	              "bUseRendered" : false,	
    	  					"fnRender" : function(o,val) {
    							 return  val;			
    						}     	              
     	  			},         	  			
    	  			{ "sTitle": "Title", 
    	  			  "mDataProp":"title", 
    	  			  "aTargets": [ 3],	
    		          "bUseRendered" : false,	
    		          "fnRender": function ( o, val ) {
   		        	   		return val==null?o.aData.id:o.aData.title;
    		          }
    	  			 },
       	  			  {  
        				  "bSortable" : true,
         	              "mDataProp":"status",
         	              "aTargets": [ 4 ],
         	              "sWidth" : "5%",
         	              "bUseRendered" : false,	
    	  					"fnRender" : function(o,val) {
    							 return  val;			
    						}     	              
     	  			  },  
     	  			  {  
      				  "bSortable" : true,
       	              "mDataProp":"maintainer",
       	              "aTargets": [ 5 ],
       	              "sWidth" : "15%",
       	              "bUseRendered" : false,	
  	  					"fnRender" : function(o,val) {
  							 return  val;			
  						}     	              
    		  		}   
     				],
 	  "aaSorting": [[0, 'desc']]		  
	});
	return oTable;
}
function defineAlgorithmTable(root,url) {

	var oTable = $('#algorithm').dataTable( {
		"sAjaxDataProp" : "algorithm",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
				{ "mDataProp": "uri" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "sWidth" : "25%",
				  "fnRender" : function(o,val) {
					  if (o.aData["id"]==null) return "Algorithm";
					  return "<a href='"+val+"' title='"+o.aData["id"]+"'>"+o.aData["name"]+"</a>";
				  }
				},
				{ "mDataProp": "endpoint" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 1 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
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
				  "aTargets": [ 2 ],
				  "bSearchable" : true,
				  "bSortable" : true,
				  "sWidth" : "25%",
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
					  "aTargets": [ 3 ],
					  "bSearchable" : true,
					  "bSortable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "15%",
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
				{ "mDataProp": "implementationOf" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 4 ],
					  "bSearchable" : true,
					  "bSortable" : true,
					  "sWidth" : "15%",
					  "bUseRendered" : false,
					  "fnRender" : function(o,val) {
						  var p = val.indexOf("#");
						  var name = val;  if (p>0) name =  val.substring(p+1);
						  return "<a href='http://apps.ideaconsult.net:8080/ontology?uri=" + encodeURIComponent(val) +"' target=_blank>" +name +"</a>";
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
	            "sLoadingRecords": "No algorithms found.",
	            "sLengthMenu": 'Display <select>' +
              '<option value="10">10</option>' +
              '<option value="20">20</option>' +
              '<option value="50">50</option>' +
              '<option value="100">100</option>' +
              '<option value="-1">all</option>' +
              '</select> algorithms.'	            
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
	        success: function(data, status, xhr) {
	        	$.each(data["algorithm"],function(index, entry) {
	        		renderAlgorithm(entry,root,null);
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
	if ((entry["endpoint"]===undefined) ||(entry["endpoint"]=="")) {
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
	"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
	"sSearch": "Filter:",
	"bPaginate" : true,
	"sPaginationType": "full_numbers",
	"sPaginate" : ".dataTables_paginate _paging",
	"oLanguage": {
            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
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
    				{ //0
    					"aTargets": [ 0 ],	
    					"sClass" : "center",
    					"bSortable" : false,
    					"bSearchable" : false,
    					"mDataProp" : null,
    					sWidth : "32px",
    					"fnRender" : function(o,val) {
    						 return  "<span class='ui-icon ui-icon-folder-collapsed zoomstruc' style='float: left; margin: .1em;' title='Click to show model details'></span>";			
    					}
    				},	     	            
    	  			{ "sTitle": "Stars", 
     	              "mDataProp":"stars",
     	              "aTargets": [ 1 ]
    	  			},     	 
    	  			{ "sTitle": "Title", 
    	  			  "mDataProp":"title", 
    	  			  "aTargets": [ 2 ],	
    	  			  sWidth: "50%",
    		          "bUseRendered" : "false",	
    		          "fnRender": function ( o, val ) {
    		                return "<a href='"+o.aData.URI +"'>" +val + "</a>";
    		          }
    		  		},   
    	  			{ "sTitle": "Algorithm", 
    	  			  "mDataProp":"algorithm.URI" , 
    	  			  "aTargets": [ 3 ],	
    	  			  sWidth: "40%",
    		  	      "bUseRendered" : "false",	
    			       "fnRender": function ( o, val ) {
    			        	    uri = val;
    			        		pos = val.lastIndexOf("/");
    			        		if (pos>=0) val = val.substring(pos+1); 
    			                return "<a href='"+ uri +"'>"+val+"</a>";
    			      }
    		  		},    	  			
    	  			{ "sTitle": "Training Dataset", 
    		  		  "mDataProp":"trainingDataset",
    		  		  "aTargets": [ 4 ],	
    		  		  sWidth: "40%",
    			      "bUseRendered" : "false",	
    			      "fnRender": function ( o, val ) {
    			      	  	shortURI = val;
    			      	  	if (val.length > 60) shortURI = val.substring(val,60) + "..."; 	
    			                 return "<a href='"+ val +"'>"+shortURI+"</a>";
    			       }
    		  		},
    	  			{ "sClass": "center", 
      		  		  "mDataProp":"algorithm.algFormat", 
      		  		  "aTargets": [ 5 ],	
      		  		  sWidth: "5%",
    		              "bUseRendered" : "false",	"bSortable": true,
    		              "fnRender": function ( o, val ) {
    		                  return "<img src='"+root + o.aData.algorithm.img +"'>";
    		                }
      	  			}    		  		
     				],
 	  "aaSorting": [[1, 'asc']]		  
	});
	return oTable;
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
    sOut += '<a href="' + model.independent + '">Independent</a>|&nbsp;';
    sOut += '<a href="' + model.dependent + '">Dependent</a>|&nbsp;';
    sOut += '<a href="' + model.predicted + '">Predicted</a>&nbsp;';
    sOut += '</td></tr>\n';
    sOut += '<tr><td>Model content</td><td>'+model.ambitprop.content+' ['+model.ambitprop.mimetype+']</td></tr>';
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
		$("#model_training").prop('href',entry["trainingDataset"]);
	}
	
	$("#model_img").prop('src',root + entry["algorithm"]["img"]);
	
	$("#help_action").html(
		"Once a model is built, it is assigned a <a href='" + 
		root + "/model' target='model'>model URI</a> and can be applied to <a href='" + 
		root + "/dataset' target='dataset'>datasets</a> and <a href='" +
		root + "/compound' target='compound'>compounds</a>." +
		"The result is a dataset, identified by a <a href='"  + root + "/dataset' target='dataset'>dataset URI</a>.");

	
}

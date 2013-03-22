/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012-2013, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 * 
 */

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
	            "sInfo": "Showing _TOTAL_ algorithms (_START_ to _END_)",
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
    	  			{ 
     	              "mDataProp":"stars",
     	              "aTargets": [ 1 ],
     	              "bUseRendered" : false,
	  					"fnRender" : function(o,val) {
							 return  "<span class='ui-icon ui-icon-star' style='display:inline-block' title='Click to show model details'></span>" + val;			
						}
    	  			},     	 
    	  			{ "sTitle": "Title", 
    	  			  "mDataProp":"title", 
    	  			  "aTargets": [ 2 ],	
    	  			  sWidth: "50%",
    		          "bUseRendered" : false,	
    		          "fnRender": function ( o, val ) {
    		                return "<a href='"+o.aData.URI +"'>" +val + "</a>";
    		          }
    		  		},   
    	  			{ "sTitle": "Algorithm", 
    	  			  "mDataProp":"algorithm.URI" , 
    	  			  "aTargets": [ 3 ],	
    	  			  sWidth: "40%",
    		  	      "bUseRendered" : false,	
    			       "fnRender": function ( o, val ) {
    			        	    uri = val;
    			        		pos = val.lastIndexOf("/");
    			        		if (pos>=0) val = val.substring(pos+1); 
        			      	  	var shortURI = val;
        			      	  	if (val.length > 20) shortURI = val.substring(val,20) + "..."; 	
    			                return "<a href='"+ uri +"' title='"+uri+"'>"+shortURI+"</a>";
    			      }
    		  		},    	  			
    	  			{ "sTitle": "Training Dataset", 
    		  		  "mDataProp":"trainingDataset",
    		  		  "aTargets": [ 4 ],	
    		  		  sWidth: "40%",
    			      "bUseRendered" : false,	
    			      "fnRender": function ( o, val ) {
    			      	  	shortURI = val;
    			      	  	if (val.length > 20) shortURI = val.substring(val,20) + "..."; 	
    			                 return "<a href='"+ val +"' title='"+val+"'>"+shortURI+"</a>";
    			       }
    		  		},
    	  			{ "sClass": "center", 
      		  		  "mDataProp":"algorithm.algFormat", 
      		  		  "aTargets": [ 5 ],	
      		  		  sWidth: "5%",
    		              "bUseRendered" : false,	"bSortable": true,
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

	loadFeatureList(entry.URI+"/predicted","#vpredicted",100);
	loadFeatureList(entry.URI+"/independent","#vindependent",100);
	loadFeatureList(entry.URI+"/dependent","#vdependent",100);
	
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
    					"mDataProp" : null,
    					"bUseRendered" : false,	
    					sWidth : "48px",
    					"fnRender" : function(o,val) {
     		               	var sOut = "<a href='"+o.aData.URI +"?page=0&pagesize=100'><span class='ui-icon ui-icon-link' style='float: left; margin: .1em;' title='Click to browse the dataset'></span></a>&nbsp;";
     		                sOut += "<a href='"+o.aData.URI +"/similarity?search=c1ccccc1'><span class='ui-icon ui-icon-heart' style='float: left; margin: .1em;' title='Similarity search within the dataset'></span></a>&nbsp;";
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
    		        	   var sOut = val;
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
   		                   sOut += "<br/><a href='"+root + "/ui/query?option=auto&type=url&search=" + encodeURIComponent(o.aData.URI) +"&page=0&pagesize=100'>Browse structures and properties</a>";
   		                   sOut += " | <a href='"+o.aData.URI +"/compounds'>Structures only</a>";
   		                   sOut += " | <a href='"+o.aData.URI +"/feature'>Properties list</a>";
   		                   sOut += " | <a href='"+root +"/admin/policy?search="+ o.aData.URI  + "' target='policy'>OpenAM access rights</a>";
   		                   //sOut += " | <a href='"+root +"/algorithm/toxtreecramer?dataset_uri="+ o.aData.URI  + "' target='predict'>Predict</a>";
    		               return sOut;
    		          }
    		  		},   
    	  			{  
      				  "bSortable" : true,
       	              "mDataProp":null,
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
    	  			  "mDataProp":null , 
    	  			  "aTargets": [ 4 ],	
    	  			  "bSortable" : false,
    	  			  "bSearchable" : false,
    	  			  sWidth: "5%",
    		  	      "bUseRendered" : false,	
    			       "fnRender": function ( o, val ) {
    			    	   val = o.aData["URI"];
    			    	   var sOut = "<a href='"+getMediaLink(val,"chemical/x-mdl-sdfile")+"' id='sdf'><img src='"+root+"/images/sdf.jpg' alt='SDF' title='Download as SDF' /></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/csv")+"' id='csv'><img src='"+root+"/images/excel.png' alt='CSV' title='Download as CSV (Comma delimited file)'/></a> ";
    			    	   var id = "d" + getID();
    			    	   sOut += "<span style='display:none;' id='"+id+"'>"; 
    			    	   sOut += "<a href='"+getMediaLink(val,"text/plain")+"' id='txt'><img src='"+root+"/images/excel.png' alt='TXT' title='Download as TXT'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-cml")+"' id='cml'><img src='"+root+"/images/cml.jpg' alt='CML' title='Download as CML (Chemical Markup Language)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-daylight-smiles")+"' id='smiles'><img src='"+root+"/images/smi.png' alt='SMILES' title='Download as SMILES'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-inchi")+"' id='inchi'><img src='"+root+"/images/inchi.png' alt='InChI' title='Download as InChI'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/x-arff")+"' id='arff'><img src='"+root+"/images/weka.jpg' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/x-arff-3col")+"' id='arff3col'><img src='"+root+"/images/weka.jpg' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"application/rdf+xml")+"' id='rdfxml'><img src='"+root+"/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/n3")+"' id='rdfn3'><img src='"+root+"/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"application/json")+"' id='json' target=_blank><img src='"+root+"/images/json.png' alt='json' title='Download as JSON'/></a>";
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
	   var sOut = "<a href='"+getMediaLink(val,"chemical/x-mdl-sdfile")+"' id='sdf'><img src='"+root+"/images/sdf.jpg' alt='SDF' title='Download as SDF' /></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/csv")+"' id='csv'><img src='"+root+"/images/excel.png' alt='CSV' title='Download as CSV (Comma delimited file)'/></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/plain")+"' id='txt'><img src='"+root+"/images/excel.png' alt='TXT' title='Download as TXT'/></a> ";
	   sOut += "<a href='"+getMediaLink(val,"chemical/x-cml")+"' id='cml'><img src='"+root+"/images/cml.jpg' alt='CML' title='Download as CML (Chemical Markup Language)'/></a> ";
	   sOut += "<a href='"+getMediaLink(val,"chemical/x-daylight-smiles")+"' id='smiles'><img src='"+root+"/images/smi.png' alt='SMILES' title='Download as SMILES'/></a> ";
	   sOut += "<a href='"+getMediaLink(val,"chemical/x-inchi")+"' id='inchi'><img src='"+root+"/images/inchi.png' alt='InChI' title='Download as InChI'/></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/x-arff")+"' id='arff'><img src='"+root+"/images/weka.jpg' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'/></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/x-arff-3col")+"' id='arff3col'><img src='"+root+"/images/weka.jpg' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'/></a> ";
	   sOut += "<a href='"+getMediaLink(val,"application/rdf+xml")+"' id='rdfxml'><img src='"+root+"/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'/></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/n3")+"' id='rdfn3'><img src='"+root+"/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'/></a> ";
	   sOut += "<a href='"+getMediaLink(val,"application/json")+"' id='json' target=_blank><img src='"+root+"/images/json.png' alt='json' title='Download as JSON'/></a>";
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
	              max: maxhits,
	              search: "^"+request.term
	            },
	            success: function( data ) {
	              response( $.map( data.dataset, function( item ) {
	                return {
	                  label: item.title,
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
	                return {
	                  label: item.title + " " + item.units,
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
		                return {
		                  label: item.name,
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
		                return {
		                  label: item.title,
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
	var uri = root + "/chart/histogram?w=650&h=250&dataset_uri="+encodeURIComponent($(datasetselector).attr("value"))+"&feature_uris[]="+encodeURIComponent($(xselector).val());
	$(chartselector).attr('src',uri);
	}
}
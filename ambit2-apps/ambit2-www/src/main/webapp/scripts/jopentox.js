/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012-2013, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 * TODO: License.
 */

function runTask(modelURI, datasetURI, resultDOM, statusDOM, imgRunning, imgReady, imgError) {
	
	var request = new XMLHttpRequest();
	var reqBody = 'dataset_uri=' + datasetURI;
	
	// 'true' is for async
	request.open('POST', modelURI, true);
	
	request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
	request.setRequestHeader('Accept', 'text/uri-list');
	
	request.onreadystatechange = function() {
		if (request.readyState != 4) { return false; }
		switch (request.status) {
			case 200:
				document.getElementById(resultDOM).href = request.responseText;
				document.getElementById(statusDOM).src = imgReady;
				document.getElementById(resultDOM).style.display = 'inline';
				document.getElementById(statusDOM).style.display = 'inline';
				break;
			case 201: // fall down
			case 202:
				checkTask(request.responseText, resultDOM, statusDOM, imgReady, imgError);
				document.getElementById(statusDOM).src = imgRunning;
				document.getElementById(statusDOM).style.display = 'inline';
				break;
			default:
				document.getElementById(resultDOM).innerHTML = request.status + ' ' + request.statusText;
				document.getElementById(statusDOM).src = imgError;
				document.getElementById(resultDOM).style.display = 'inline';
				document.getElementById(statusDOM).style.display = 'inline';
				break;
			}
	};
	request.onerror = function() {
		document.getElementById(resultDOM).innerHTML = request.status + ' ' + request.statusText;
		document.getElementById(statusDOM).src = imgError;
		document.getElementById(resultDOM).style.display = 'inline';
		document.getElementById(statusDOM).style.display = 'inline';		
	}
	request.send(reqBody);
}

function getResponseTitle(request, description) {
	return '<span title=\"' + request.status + ' ' + request.statusText + '\">'+description+'</span>';
}

/**
 * Wrap the dataset URI with the new /ui/_dataset?dataset_uri= 
 * @param uri
 * @returns
 */

function wrapDatasetURI(uri) {
	if (uri===undefined) return "";
	if (uri==null) return uri;
	uri = $.trim(uri);
	var p = uri.indexOf("/dataset/");
	if (p>0) {
		var wrapped = uri.substring(0,p)+
		"/ui/_dataset?dataset_uri="+
		encodeURIComponent(uri);
		return wrapped;
	} else return uri;
}

function checkTask(taskURI, resultDOM, statusDOM, imgReady, imgError) {
	
	var request = new XMLHttpRequest();
	
	// 'true' is for async
	request.open('GET', taskURI+'?media=text%2Furi-list', true);
	//replace with json to get more detailed error message
	request.setRequestHeader('Accept', 'text/uri-list');
	
	request.onreadystatechange = function() {
		var dResult = document.getElementById(resultDOM);
		var sResult = document.getElementById(statusDOM);
		if (request.readyState != 4) { return false; }
		switch (request.status) {
			case 200:
				if (dResult!=null) {
					dResult.innerHTML = getResponseTitle(request,"Ready. Results available. ");
					dResult.href = wrapDatasetURI(request.responseText);
					dResult.style.display = 'inline';
					dResult.target = null;
				}
				if (sResult !=null) {
					sResult.style.display = 'inline';
					sResult.src = imgReady;
				}
				if (request.responseText.indexOf("register/notify")>=0) {
					//ok, redundant, but just in case 
					window.location.href = request.responseText; 
				} else if (request.responseText.indexOf("http")>=0) {
					//don't want redirect
					//window.location.href = request.responseText; 
				} else { 
					//smth wrong with the result URI 
				}
				break;
			case 201:
				taskURI = request.responseText; // and then fall down
				if (dResult!=null) 
					dResult.target = null;
			case 202:
				if (dResult!=null) {
					dResult.target = null;
					dResult.innerHTML =  getResponseTitle(request,"Waiting for results ...");
					dResult.href = request.responseText;
				}
				var taskTimer = window.setTimeout(function() {
					checkTask(taskURI, resultDOM, statusDOM, imgReady, imgError);
				}, 1000);
				break;
			case 303:
				window.location.href = request.responseText; 
				break;
			
			case 500: 
				if (dResult!=null) {
					dResult.innerHTML = getResponseTitle(request,request.status + " " + request.statusText );
					dResult.href = taskURI;
					dResult.target = "error";
					dResult.style.display = 'inline';
				}
				if (sResult != null) {
					sResult.src = imgError;
					sResult.style.display = 'inline';
				}
				break;
			case 502:
				if (dResult!=null) {
					dResult.innerHTML = getResponseTitle(request,request.status + " " + request.statusText );
					dResult.href = taskURI;
					dResult.target = "error";
					dResult.style.display = 'inline';
				}
				if (sResult != null) {
					sResult.src = imgError;
					sResult.style.display = 'inline';
				}
				break;				
			default:
				if (dResult!=null) {
					dResult.innerHTML = getResponseTitle(request,request.status + " " + request.statusText);
					dResult.href = taskURI;
					dResult.target = "error";
					dResult.style.display = 'inline';
				}
				if (sResult!=null) {
					sResult.style.display = 'inline';
					sResult.src = imgError;
				}
				break;
		}
	};
	request.send(null);
}

function renderTask(entry,root) {

	try {
		$("#task_started").text(new Date(entry["started"]).toUTCString());} 
	catch (error) {}
	$("#task_name").text(entry["name"]);
	$("#result").prop("href",entry["result"]);
	var img = "24x24_ambit.gif";
	switch (entry["status"]) {
	case "Completed" : {
		img = "tick.png";
		break;
	}
	case "Error" : {
		img = "cross.png";
		break;
	}
	default : {
		img = "24x24_ambit.gif";	
	}
	}
	$("#status").prop("src",root + "/images/" + img);
	$("#task_errorreport").text(entry["error"]);
	if (entry["errorCause"]!=null) {
		$("#task_json").text(entry["errorCause"]);
		$("#task_json").show();
	} else {
		$("#task_json").text("");
		$("#task_json").hide();
	}
}

function readTask(root,url) {

	  $.ajax({
	        dataType: "json",
	        url: url,
	        success: function(data, status, xhr) {
	        	$.each(data["task"],function(index, entry) {
	        		renderTask(entry,root);
	        	});
	        },
	        error: function(xhr, status, err) {
	        	try {
	        		if (xhr.responseText != undefined) {
			        	var task = jQuery.parseJSON(xhr.responseText);
			        	if (task["task"].length > 0) {
			        		renderTask(task["task"][0],root);
			        	}
	        		}
	        	} catch (err) {
	        		$("#task_errorreport").text(err);
	        	}
	        },
	        complete: function(xhr, status) {
	        }
	     });
}
function defineTaskTable(root,url) {

	var oTable = $('#task').dataTable( {
		"sAjaxDataProp" : "task",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
				{ "mDataProp": "id" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  if (o.aData["id"]==null) return "Job";
					  return "<a href='"+root + "/task/" + o.aData["id"]+"' title='"+o.aData["id"]+"'>"+o.aData["status"]+"</a>";
				  }
				},
				{ "mDataProp": "name" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 1 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "sWidth" : "50%",
				  "fnRender" : function(o,val) {
					  	if (o.aData["status"]=='Completed') {
					  		return val + "<a href='"+o.aData["result"]+"'>Ready. Results available.</a>";
					  	} else if (o.aData["status"]=='Error') {
					  		return val + o.aData["error"];

					  	} else {
					  		var id = getID();
					  		var sOut=val +"<br/><a href='#' id='r"+id+"'></a>&nbsp; <img src='"+root+"/images/24x24_ambit.gif' id='s"+id+"'>";
						  	checkTask(root + "/task/" + o.aData["id"],
						  			"r"+id, "s"+id,  
						  			root + "/images/tick.png",
						  			root + "/images/cross.png");
						  	return sOut;
					  	}  	
				  }
				},
				{ "mDataProp": "started" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 2 ],
				  "bSearchable" : true,
				  "bSortable" : true,
				  "bUseRendered" : false,
				  "fnRender" : function(o,val) {
					  return (val==undefined)?"":(val==null)?"":new Date(val).toUTCString();
				  }
				},
				{ "mDataProp": "completed" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 3 ],
					  "bSearchable" : true,
					  "bSortable" : true,
					  "bUseRendered" : false,
					  "fnRender" : function(o,val) {
						  return (val==undefined)?"":(val==null)?"":(val<0)?"-":new Date(val).toUTCString();
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
	            "sLoadingRecords": "No tasks found.",
	            "sLengthMenu": 'Display <select>' +
              '<option value="10">10</option>' +
              '<option value="20">20</option>' +
              '<option value="50">50</option>' +
              '<option value="100">100</option>' +
              '<option value="-1">all</option>' +
              '</select> tasks.'	            
	    }
	} );
	return oTable;
}

function getID() {
	   return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}			

function runAlgorithms(root,statusSelector) {
	var algs = [
    "toxtreecramer","toxtreecramer2","toxtreeverhaar","toxtreeverhaar2",
	"toxtreeeye","toxtreeskinirritation","toxtreemic","toxtreeames",
	"toxtreeskinsens","toxtreednabinding","toxtreeproteinbinding",
	"toxtreecarc","toxtreebiodeg","toxtreesmartcyp","pka","ambit2.descriptors.MolecularWeight",
	"org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor",
	"org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor",
	"org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptor",
	"tautomers"    ];
	$.each(algs,function(index,alg) {
		$.ajax({
			contentType :'application/x-www-form-urlencoded; charset=UTF-8',
		    headers: { 
		        Accept : "text/uri-list; charset=utf-8"
		    },
			data : "dataset_uri="+root+"/compound/1", 
			type: "POST",
			url : root + "/algorithm/" + alg,
			success : function(data1, status, xhr) {
				$(statusSelector).text('Completed');
			},
			error : function(xhr, status, err) {
				$(statusSelector).text(err);
			},
			complete : function(xhr, status) {
				$(statusSelector).text(status);
			}
		});	
	});

}


function runStructureIndexing(root,statusSelector) {
	var algs = [
    "fingerprints","smartsprop","struckeys" ];
	$.each(algs,function(index,alg) {
		$.ajax({
			contentType :'application/x-www-form-urlencoded; charset=UTF-8',
		    headers: { 
		        Accept : "text/uri-list; charset=utf-8"
		    },
			data : "dataset_uri=", 
			type: "POST",
			url : root + "/algorithm/" + alg,
			success : function(data1, status, xhr) {
				$(statusSelector).text('Completed');
			},
			error : function(xhr, status, err) {
				$(statusSelector).text(err);
			},
			complete : function(xhr, status) {
				$(statusSelector).text(status);
			}
		});	
	});

}

/**
 * Deletes an Opentox dataset
 * @param uri
 */
function deleteDataset(uri,statusSelector) {
	$.ajax({
		contentType :'application/x-www-form-urlencoded; charset=UTF-8',
	    headers: { 
	        Accept : "text/uri-list; charset=utf-8"
	    },
		data : "dataset_uri=" + encodeURIComponent(uri), 
		type: "POST",
		url : uri + "?method=delete",
		success : function(data1, status, xhr) {
			var sOut = "Deleted.<br/><a href='#' onClick='document.location.reload(true);'>Click to refresh the page.</a>";
			$(statusSelector).html(sOut);
			
		},
		error : function(xhr, status, err) {
			$(statusSelector).text(status + " " + err);
		},
		complete : function(xhr, status) {
			
		}
	});	
}

/**
 * Deletes a substance
 * @param uri
 */
function deleteSubstance(uri,name,statusSelector) {
	var r=confirm("Do you really want to delete the substance \n\n"+ decodeURIComponent(name) +"\n\n at "+uri+"\n\n and ALL related studies?");
	if (r==true)  
	$.ajax({
		contentType :'application/x-www-form-urlencoded; charset=UTF-8',
	    headers: { 
	        Accept : "text/uri-list; charset=utf-8"
	    },
		data : "substance_uri=" + encodeURIComponent(uri), 
		type: "POST",
		url : uri + "?method=delete&substance_uri=" + encodeURIComponent(uri), 
		success : function(data1, status, xhr) {
			var sOut = "Deleted.<br/><a href='#' onClick='document.location.reload(true);'>Click to refresh the page.</a>";
			$(statusSelector).html(sOut);
			
		},
		error : function(xhr, status, err) {
			$(statusSelector).text(status + " " + err);
		},
		complete : function(xhr, status) {
			
		}
	});	
}

function lookup(root,title,selector,callback,errorcallback) {
	$("#error").text("");
	var value = $(selector).attr('value');
	if (value===undefined) return;
	if (value==null) return;
	if (value=="") return;
	runSearchURI(root + "/query/compound/search/all?media="+
					encodeURIComponent("application/json")+
					"&max=1&search="+encodeURIComponent(value),
					null,callback,errorcallback);
}

function lookup64(root,title,selector,callback,errorcallback) {
	$("#error").text("");
	var value = $(selector).attr('value');
	if (value===undefined) return;
	if (value==null) return;
	if (value=="") return;
	runSearchURI(root + "/query/compound/search/all?media="+
				encodeURIComponent("application/json")+"&max=1&b64search="+
				encodeURIComponent($.base64.encode(value)),null,callback,errorcallback);
}

function runSearchURI(sSource,results,callback, errorcallback) {
	$(results).empty(); 
	$.ajax( {
	        "type": "GET",
	        "url": sSource ,
	        "dataType": "json", 
	        "crossDomain": false,  //bloody IE
	        "contentType" : "application/x-javascript",
	        "success": function(json) {
	        	callback(json);
	        },
	        "cache": false,
	        "statusCode" : {
	            400: function() {
	              alert("Not found");
	            },
	            404: function() {
		              alert("Not found");
		        }
	        },
	        "error" : function( xhr, textStatus, error ) {
	        	if (errorcallback!=undefined) errorcalback(xhr, textStatus, error );
	        }
	      } );
}

function runSearchMOL(sSource,callback, errorcallback) {
	$.ajax( {
	        "type": "GET",
	        "url": sSource ,
	        "crossDomain": false,  //bloody IE
	        "contentType" : "chemical/x-mdl-molfile",
	        "success": function(mol) {
	        	callback(mol);
	        },
	        "cache": false,
	        "statusCode" : {
	            400: function() {
	              alert("Not found");
	            },
	            404: function() {
		              alert("Not found");
		        }
	        },
	        "error" : function( xhr, textStatus, error ) {
	        	if (errorcallback!=undefined) errorcalback(xhr, textStatus, error );
	        }
	      } );
}

function loadStats(root,dataseturi,selector) {
	var url = root + "/admin/stats/chemicals_in_dataset?dataset_uri=" + encodeURIComponent(dataseturi);
	$.ajax({
	    url: url,
	    dataType: "json",
	    data: {
	      media:"application/json"
	    },
	    success: function( data ) {
	  	  try {
	  	  $(selector).text(data.facet[0].count);
	  	  } catch (err) {($selector).text(url);}
	    }
	  });	

}
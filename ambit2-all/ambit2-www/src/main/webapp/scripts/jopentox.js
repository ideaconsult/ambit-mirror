/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 * TODO: Licence.
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
	
	request.send(reqBody);
}

function getResponseTitle(request, description) {
	return '<span title=\"' + request.status + ' ' + request.statusText + '\">'+description+'</span>';
}


function checkTask(taskURI, resultDOM, statusDOM, imgReady, imgError) {
	
	var request = new XMLHttpRequest();
	
	// 'true' is for async
	request.open('GET', taskURI, true);
	//replace with json to get more detailed error message
	request.setRequestHeader('Accept', 'text/uri-list');
	
	request.onreadystatechange = function() {
		if (request.readyState != 4) { return false; }
		switch (request.status) {
			case 200:
				document.getElementById(resultDOM).innerHTML = getResponseTitle(request,"Ready. Results available.");
				document.getElementById(resultDOM).href = request.responseText;
				document.getElementById(statusDOM).src = imgReady;
				document.getElementById(resultDOM).style.display = 'inline';
				document.getElementById(statusDOM).style.display = 'inline';
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
			case 202:
				document.getElementById(resultDOM).innerHTML =  getResponseTitle(request,"Waiting ...");
				document.getElementById(resultDOM).href = request.responseText;
				var taskTimer = window.setTimeout(function() {
					checkTask(taskURI, resultDOM, statusDOM, imgReady, imgError);
				}, 1000);
				break;
			case 303:
				window.location.href = request.responseText; 
				break;
			case 500: 
				document.getElementById(resultDOM).innerHTML = getResponseTitle(request,request.status + " " + request.statusText );
				document.getElementById(resultDOM).href = request.responseText;
				document.getElementById(statusDOM).src = imgError;
				document.getElementById(resultDOM).style.display = 'inline';
				document.getElementById(statusDOM).style.display = 'inline';				
				break;
			default:
				document.getElementById(resultDOM).innerHTML = getResponseTitle(request,request.status + " " + request.statusText);
				document.getElementById(statusDOM).src = imgError;
				document.getElementById(resultDOM).style.display = 'inline';
				document.getElementById(statusDOM).style.display = 'inline';
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
		img = "error.png";
		break;
	}
	default : {
		img = "24x24_ambit.gif";	
	}
	}
	$("#status").prop("src",root + "/images/" + img);
	$("#task_errorreport").text(entry["error"]);
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
			        	if (task.length > 0) {
			        		renderTask(task[0],root);
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

					  	} else
						  	return checkTask(root + "/task/" + o.aData["id"],
						  			'result', 'status',  
						  			root + "/images/tick.png",
						  			root + "/images/cross.png");
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

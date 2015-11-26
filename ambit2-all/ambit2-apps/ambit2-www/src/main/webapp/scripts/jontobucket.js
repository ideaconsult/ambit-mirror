
function defineOntoBucketTable(root,url,viscols) {
	var oTable = $('#ontobucket').dataTable( {
		"sAjaxDataProp" : "ontobucket",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [

				{ "mDataProp": "recalls" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "sWidth" : "10%",
	  				"fnRender" : function(o,val) {
	  					if (val === undefined) return "";
	  					else 
						return "<a href='" + root + "/ontobucket?search=" + encodeURIComponent(val) + "'>" +val + " </a>";
					}				  
					  
				},
				
				{ "mDataProp": "title" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 1 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
		  				"fnRender" : function(o,val) {
		  					if (val === undefined) return "";
		  					else 
							return "<a href='" + root + "/ontobucket?search=" + encodeURIComponent(val) + "'>" +val + " </a>";
						}				  
					},
					
				{ "mDataProp": "hastopic" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [2 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "sWidth" : "10%",
	  				"fnRender" : function(o,val) {
	  					if (val === undefined) return "";
	  					else 
						return "<a href='" + root + "/ontobucket?search=" + encodeURIComponent(val) + "'>" +val + " </a>";
					}				  				  

				},

				{ "mDataProp": "relevance" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 3 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "sWidth" : "10%",
					},
					{ "mDataProp": "relation" , "asSorting": [ "asc", "desc" ],
						  "aTargets": [ 4 ],	
						  "sWidth" : "10%",
						  "bSearchable" : true,
						  "bUseRendered" : false,
						  "bSortable" : true,
			  				"fnRender" : function(o,val) {
			  					var search = o.aData["title"];
			  					var type = undefined;
			  					var descr = val;
			  					if ("protocol" == val) { type = "guideline"; descr = "by protocol"; }
			  					else if ("endpoint" == val) { type = "endpoint"; descr = "by endpoint"; }
			  					else if ("reference" == val) { type = "citation"; descr = "by reference";  search = o.aData["recalls"];}
			  					else if ("hash" == val) {   type = "endpointhash"; descr = "by endpoint parameters"; search = o.aData["uuid"]; }
			  					else if ("substancetype" == val) { type = "substancetype"; descr = "by substance type"; search = o.aData["hastopic"];}

			  					if (type === undefined) return descr;
			  					else 
								return "<a href='" + root + "/substance?type=" + encodeURIComponent(type) + "&search=" +  encodeURIComponent(search)  + "'>" +descr + " </a>";
							}	
						  
						}						
			],
		"bJQueryUI" : true,
		"aaSorting" : [ [ 3, 'desc' ] ],
		"bSearchable": true,
		"sAjaxSource": url,
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
		"sSearch": "Filter:",
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"oLanguage": {
	            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No entries found.",
	            "sZeroRecords": "No entries found.",
	            "sInfo": "Showing _TOTAL_ entries (_START_ to _END_)",
	            "sLengthMenu": 'Display <select>' +
              '<option value="10">10</option>' +
              '<option value="20">20</option>' +
              '<option value="50">50</option>' +
              '<option value="100">100</option>' +
              '<option value="-1">all</option>' +
              '</select> entries.'	            
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
			        	alert("Restricted access.");
						break;
					}
					case 404: {
						//not found
						break;
					}
					default: {
						//console.log(xhr.status + " " + xhr.statusText + " " + xhr.responseText);
			        	alert("Error loading  " + xhr.status + " " + error);
					}
					}
					oSettings.oApi._fnProcessingDisplay(oSettings, false);
				}
			});
		}	    
	} );
	return oTable;
}
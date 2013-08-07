var substance = {
		data : {},
		"defineSubstanceTable" : function (root,url,deleteVisible) {
			var oTable = $('.substancetable').dataTable( {
				"sAjaxDataProp" : "substance",
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
			          '</select> qmaps.'	            
			    },	
			    "aoColumnDefs": [
			    				{ //1
			    					"aTargets": [ 0 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "i5uuid",
			    					"bUseRendered" : false,	
			    					"sWidth" : "50%",
			    					"fnRender" : function(o,val) {
			    						var sOut = "<a href='"+o.aData["URI"]+"'>"+ val+ "</a>"
			    						return sOut;
			    					}
			    				},	    
			    				{ //2
			    					"aTargets": [ 1 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "name",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},	    	
			    				{ //3
			    					"aTargets": [ 2 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "publicname",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				}
			    				],
			    				    				
			 	  "aaSorting": [[1, 'desc']]
				});
				return oTable;			
		}
}
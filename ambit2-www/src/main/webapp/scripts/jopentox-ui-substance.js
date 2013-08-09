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
			    				{ //2
			    					"aTargets": [ 0 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "name",
			    					"sWidth" : "25%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},	    	
			    				{ //1
			    					"aTargets": [ 1 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "i5uuid",
			    					"sWidth" : "20%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var sOut = "<a href='"+o.aData["URI"]+"'>"+ val+ "</a>"
			    						return val===undefined?"":(val==null)?"":sOut;
			    					}
			    				},		
			    				{ //1
			    					"aTargets": [ 2 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "substanceType",
			    					"sWidth" : "15%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},			    				
			    				{ //3
			    					"aTargets": [ 3 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "publicname",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				},
			    				{ //1
			    					"aTargets": [ 4 ],	
			    					"sClass" : "left",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "referenceSubstance.i5uuid",
			    					"sWidth" : "20%",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return val;
			    					}
			    				}				    				
			    				],
			    				    				
			 	  "aaSorting": [[1, 'desc']]
				});
				return oTable;			
		},
		"defineCompositionTable" : function (root,url,deleteVisible) {
			var oTable = $('.compositiontable').dataTable( {
				"sAjaxDataProp" : "composition",
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
			    					"mDataProp" : "relation",
			    					"bUseRendered" : false,	
			    					"sWidth" : "15%",
			    					"fnRender" : function(o,val) {
			    						var sOut = "<span class='camelCase'>"+ val.replace("HAS_","").toLowerCase() + "</span>";
			    						return sOut;
			    					}
			    				},	    
			    				{ //2
			    					"aTargets": [ 1 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : null,
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var sOut = "<a href='"+o.aData["structure"]+"' target=_blank>Name-todo</span></a>"
			    						return sOut;
			    					}
			    				},	    	
			    				{ //3
			    					"aTargets": [ 2 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : null,
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return "TODO";
			    					}
			    				},
			    				{ //3
			    					"aTargets": [ 3 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : null,
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return "TODO";
			    					}
			    				},
			    				{ //4
			    					"aTargets": [ 4 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "proportion.typical.value",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var precision = o.aData["proportion"]["typical"]["precision"];
			    						var sOut = ((precision===undefined) || (precision==null) || ("="==precision))?"":precision;
			    						return sOut + " " + val + " " + o.aData["proportion"]["typical"]["unit"] ;
			    					}
			    				},
			    				{ //4
			    					"aTargets": [ 5 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : "proportion.real",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var precision = val["lowerPrecision"];
			    						var sOut = ((precision===undefined) || (precision==null) || ("="==precision))?"":precision;
			    						sOut += " (" + val["lowerValue"] + " , ";
			    						precision = val["upperPrecision"];
			    						sOut += ((precision===undefined) || (precision==null) || ("="==precision))?"":precision;
			    						sOut += " " + val["upperValue"] + ")";
			    						sOut += val["unit"]==null?"":(" "+val["unit"]) ;
			    						return sOut;
			    					}
			    				},			    				
			    				{ //5
			    					"aTargets": [ 6 ],	
			    					"sClass" : "center",
			    					"bSortable" : true,
			    					"bSearchable" : true,
			    					"mDataProp" : null,
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						return "Related";
			    					}
			    				}		    				
			    				],
			    				    				
			 	  "aaSorting": [[1, 'desc']]
				});
				return oTable;			
		}
}
var substance = {
		data : {},
		"defineSubstanceTable" : function (root,url,selector,jQueryUI,dom,compositionDom) {
			var oTable = $(selector).dataTable( {
				"sAjaxDataProp" : "substance",
				"sAjaxSource": url,	
				"sSearch": "Filter:",
				"bJQueryUI" : jQueryUI,
				"bSearchable": true,
				"bProcessing" : true,
				"sDom" : (dom==null)?'<"help remove-bottom"i><"help"p>Trt<"help"lf>':dom,
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
			          '</select> substances.'	            
			    },	
			    "aoColumnDefs": [
				    			{ //2
				    				"aTargets": [ 0 ],	
				    				"sClass" : "center",
				    				"bSortable" : true,
				    				"bSearchable" : true,
				    				"mDataProp" : null,
				    				"bUseRendered" : false,	
				    				"fnRender" : function(o,val) {
				    					return "<span class='ui-icon ui-icon-folder-collapsed zoomstruc' style='float: left; margin: .1em;' title='Click to show substance composition'></span>";
				    				}
				    			},				                     
			    				{ //2
			    					"aTargets": [ 1 ],	
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
			    					"aTargets": [ 2 ],	
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
			    					"aTargets": [ 3 ],	
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
			    					"aTargets": [ 4 ],	
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
			    					"aTargets": [ 5 ],	
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
		    	$(selector + ' tbody td .zoomstruc').live('click',function() {
					var nTr = $(this).parents('tr')[0];
					if (oTable.fnIsOpen(nTr)) {
						$(this).removeClass("ui-icon-folder-open");
						$(this).addClass("ui-icon-folder-collapsed");
						this.title='Click to show substance composition';
						oTable.fnClose(nTr);
					} else {
						$(this).removeClass("ui-icon-folder-collapsed");
						$(this).addClass("ui-icon-folder-open");
						this.title='Click to close substance composition panel';
						var id = getID();
						console.log($(this));
						oTable.fnOpen(nTr, fnFormatDetails(nTr,id),"details");
						
						var composition = oTable.fnGetData(nTr);
						substance.defineCompositionTable(root,composition["URI"]+"/composition","#t_"+id,
									null,
									compositionDom==null?'Trt':compositionDom);
									//compositionDom==null?'<"help remove-bottom"><"help">Trt<"help">':compositionDom);
									
					}
		    	});
		    	function getID() {
		    		return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
		    	}
				function fnFormatDetails( nTr,id ) {
					var compositionTable = 
						"<div id='c_"+id+"' class='details' style='margin-top: 5x;' >"+						
						"<table id='t_"+id+"' class='compositiontable' cellpadding='0' border='0' width='100%' cellspacing='0' style='margin:0;padding:0;' >"+
						"<thead><tr><th>Type</th><th>Name</th><th>EC No.</th><th>CAS No.</th><th>Typical concentration</th><th>Real concentration</th>"+
						"<th>Other related substances</th></tr></thead><tbody></tbody></table></div>";

				    return compositionTable;
				}
		    	
				return oTable;			
		},
		"defineCompositionTable" : function (root,url,selector,jQueryUI,dom) {
			var oTable = $(selector).dataTable( {
				"sAjaxDataProp" : "composition",
				"sAjaxSource": url,	
				"sSearch": "Filter:",
				"bJQueryUI" : jQueryUI,
				"bSearchable": true,
				"bProcessing" : true,
				"sDom" : dom==null?'<"help remove-bottom"i><"help"p>Trt<"help"lf>':dom,
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
			          '</select> substances.'	            
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
			    					"mDataProp" : "compound.URI",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						var sOut = "<a href='"+val+"' target=_blank>Name-todo</span></a>"
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
			    					"mDataProp" : "compound.URI",
			    					"bUseRendered" : false,	
			    					"fnRender" : function(o,val) {
			    						if ((val===undefined) || (val==null)) return "";
			    						var sOut = "<a href='"+root+"/substance?compound_uri="+val+"' target=_blank>Also contained in...</span></a>"
			    						return sOut;
			    					}
			    				}		    				
			    				],
			    				    				
			 	  "aaSorting": [[1, 'desc']]
				});
				return oTable;			
		}
}
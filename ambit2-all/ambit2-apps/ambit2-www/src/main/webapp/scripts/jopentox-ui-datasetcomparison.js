var comparison = {
		root : null,
		datasets : [],
		tableSelector : "#matrix",
		"defineDatasetComparisonTable" : function (root,dataseturl) {
			
		},
		"datasetComparisonMatrix" : function (root,datasetURL,callback,tableSelector) {
			this.root = root;
			this.tableSelector = tableSelector;
			$.getJSON(datasetURL, callback);
		},
		"matrix" : function(data) {
			datasets = data.dataset;
			var header = "<thead>";
			header += "<th>Datasets</th>";
			$.each( datasets, function(i, val ) {
				header += "<th>" + val.title + "</th>";
			});		
			header += "</thead><tbody/>";
			$(comparison.tableSelector).append(header); 
			var aoColumnDefs = [];
			aoColumnDefs.push(
					{
					"sTitle" : "Datasets",
					"aTargets": [ 0 ], 
					"mDataProp" : "URI",
					"bUseRendered" : false,
    	  			"bSortable" : true,
      	  			"bSearchable" : true,
      		        "bUseRendered" : false,
					"fnRender" :   function(o,val) {
						return o.aData["title"];
					}      		        
					}
			    );			
			var data = [];
			$.each( datasets, function( i, val ) {
				aoColumnDefs.push(
					{
					"sTitle" : val.title,
					"aTargets": [ i+1 ], 
					"mDataProp" : val.URI,
					"bUseRendered" : false,
    	  			"bSortable" : true,
      	  			"bSearchable" : true,
      		        "bUseRendered" : false,						
					"fnRender" :   function(o,val) {
						var href = comparison.root + "/admin/stats/dataset_intersection"+
								   "?dataset_uri=" + encodeURIComponent(o.aData["URI"])+ 
								   "&dataset_uri=" + encodeURIComponent(o.mDataProp);
						return "<a href='"+href+"' target='comparison'>"+val.count+"</a>";
					}
					}
			    );
					
				var row = { "title" : val.title ,"URI" : val.URI };
				$.each( datasets, function( i,  val) {
					row[val.URI] = {"count" : null };
				});
				data.push(row);
			});			
			var oTable = $(comparison.tableSelector).dataTable( {
				"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
				"sPaginationType": "full_numbers",
				"bJQueryUI": true,
		        "aaData": data,
		        "aoColumnDefs": aoColumnDefs,
		        "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		        	
		        	$.each( aData, function( index,  val) {
		        		if (("title" != index) && ("URI" != index) && (val.count == null)) {
			        		console.log(val);
							var href = comparison.root + "/admin/stats/dataset_intersection"+
							   "?dataset_uri=" + encodeURIComponent(aData.URI)+ 
							   "&dataset_uri=" + encodeURIComponent(index);
							try {
								$.getJSON(href,  function(data) {
									aData[index] = data.facet[0];
									console.log(aData.URI);
									console.log(aData);
								});
							} catch (err) {	console.log(err);	}
		        		}
		        	});
		         }
		    } );   
		} 
}		
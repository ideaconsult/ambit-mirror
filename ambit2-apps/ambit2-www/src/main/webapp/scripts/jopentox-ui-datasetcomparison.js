var comparison = {
		datasets : [],
		tableSelector : "#matrix",
		"defineDatasetComparisonTable" : function (root,dataseturl) {
			
		},
		"datasetComparisonMatrix" : function (root,datasetURL,callback,tableSelector) {
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
						//console.log(o);
						return val;
					}
					}
			    );
					
				var row = { "title" : val.title ,"URI" : val.URI };
				$.each( datasets, function( i,  val) {
					//console.log(val);
					row[val.URI] = i;
				});
//				console.log(row);
				data.push(row);
			});			
			$(comparison.tableSelector).dataTable( {
				"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
				"sPaginationType": "full_numbers",
				"bJQueryUI": true,
		        "aaData": data,
		        "aoColumnDefs": aoColumnDefs 
		    } );   
		} 
}		
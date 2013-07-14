var qmap = {
		data : {},
		"defineMetadataTable" : function (root,url,deleteVisible) {
			var oTable = $('.qmaptable').dataTable( {
			"sAjaxDataProp" : "qmap",
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
		            "sLoadingRecords": "No qmaps found.",
		            "sZeroRecords": "No qmaps found.",
		            "sEmptyTable": "No qmaps available.",
		            "sInfo": "Showing _TOTAL_ qmaps (_START_ to _END_)",
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
		    					"sClass" : "center",
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "dataset",
		    					"bUseRendered" : false,	
		    					"sWidth" : "33%",
		    					"fnRender" : function(o,val) {
		    						var sOut = "<a href='"+ val.URI+"' target=_blank>"+ (val.title==""?val.URI:val.title) +"</a>";
		    						return sOut;
		    					}
		    				},	    
		    				{ //2
		    					"aTargets": [ 1 ],	
		    					"sClass" : "center",
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "activity",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						var sOut = "<a href='"+ val.featureURI+"' target='feature'>"+ val.feature.title+"</a> " + val.feature.units ;
		    						return sOut;
		    					}
		    				},	    	
		    				{ //3
		    					"aTargets": [ 2 ],	
		    					"sClass" : "center",
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "activity.threshold",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						return val;
		    					}
		    				},
		    				{ //4
		    					"aTargets": [ 3 ],	
		    					"sClass" : "center",
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "similarity.threshold",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						return val;
		    					}
		    				},		    	
		    				{ //0
		    					"aTargets": [ 4 ],	
		    					"sClass" : "center",
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "URI",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						var sOut =  "<a href='"+root+"/toxmatch?qmap_uri="+ val+"' target=_blank>Explore</a>"; 
		    						sOut += "| <a href='"+ val+"' target=_blank>Compounds</a>";
		    						return sOut;
		    					}
		    				}			    				
		     				],
		 	  "aaSorting": [[1, 'desc']],
			   "fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
					oSettings.jqXHR = $.ajax({
						"type" : "GET",
						"url" : sSource,
						"data" : aoData,
						"dataType" : "json",
						"contentType" : "application/json",
						"cache" : true,
						"success": function(result){
							 var datasets = {};
							 $.each(result['dataset'], function(index, value) {
								 var uri = value["URI"]; 
								 datasets[uri] = value;
							 });
							 $.each(result['qmap'], function(index, value) {
								 var feature = value["activity"]["featureURI"];
								 value["activity"]["feature"] = result["feature"][feature];
								 value["dataset"] = datasets[value["dataset"]["URI"]];
								 
							 });
							fnCallback(result);
						},
						"error" : function(xhr, textStatus, error) {
							switch (xhr.status) {
							case 403: {
					        	alert("Restricted access. You are not authorized to access the requested qmaps.");
								break;
							}
							case 404: {
								//not found
								break;
							}
							default: {
					        	alert("Error loading qmaps " + xhr.status + " " + error);
							}
							}
							oSettings.oApi._fnProcessingDisplay(oSettings, false);
						}
					});
				}	 		 	  
			});
			return oTable;
		},
//end defineMetadataTable
//start table for nodes (compound, activity, g2)		
		"defineNodesTable" : function (root,url,dataCallback) {
			var imgSize = 150;
			var oTable = $('.qnodestable').dataTable( {
			"sAjaxDataProp" : "nodes",
			"sAjaxSource": url,	
			"sSearch": "Filter:",
			"bJQueryUI" : true,
			"bSearchable": true,
			"bProcessing" : true,
			"sDom" : '<"help remove-bottom"f><"help"p>Trt<"help"li>',
			"sSearch": "Filter:",
			"bPaginate" : true,
			"sPaginationType": "full_numbers",
			"sPaginate" : ".dataTables_paginate _paging",
			"oLanguage": {
		            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
		            "sLoadingRecords": "No structures found.",
		            "sZeroRecords": "No structures found.",
		            "sEmptyTable": "No structures available.",
		            "sInfo": "Showing _TOTAL_ structures (_START_ to _END_)",
		            "sLengthMenu": 'Display <select>' +
		          '<option value="10">10</option>' +
		          '<option value="20">20</option>' +
		          '<option value="50">50</option>' +
		          '<option value="100">100</option>' +
		          '<option value="-1">all</option>' +
		          '</select> structures.'	            
		    },	
		    "aoColumnDefs": [
		    				{ //1
		    					"aTargets": [ 0 ],	
		    					"sClass" : "center",
		    					"asSorting" : [ "asc", "desc" ],
								"bSearchable" : true,
								"bSortable" : true,
		    					"mDataProp" : "URI",
		    					"bUseRendered" : false,	
								"sHeight" : imgSize+"px",
								"sWidth" : imgSize+"px",
		    					"fnRender" : function(o,val) {
									var cmpURI = val;
									if (val.indexOf("/conformer") >= 0) {cmpURI = val.substring(0, val.indexOf("/conformer"));}
									var prm = {'option': 'auto', 'type':'url', 'search':cmpURI, 'pagesize': 1};
									var searchURI = root + "/ui/query?" + $.param(prm,false);
									cmpURI = cmpURI + "?media=image/png";
									var sOut =  '<a href="'+searchURI+'" target=_blank>' +
										   '<img class="ui-widget-content" title="'+val+'"  border="0" src="' + cmpURI + '&w='+imgSize+'&h='+imgSize+'" onError="this.style.display=\'none\'">'
										   + "</a>";
									return sOut;
		    						
		    					}
		    				},	    
		    				{ //2
		    					"aTargets": [ 1 ],	
		    					"sClass" : "center",
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "activity",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						return val;
		    					}
		    				},	    	
		    				{ //3
		    					"aTargets": [ 2 ],	
		    					"sClass" : "center",
		    					"asSorting" : [ "desc" ],
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "g2",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						return val;
		    					}
		    				},
		    				{ //0
		    					"aTargets": [ 3 ],	
		    					"sClass" : "center",
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "qmap",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						var sOut =  "<a href='"+root+"/toxmatch?qmap_uri="+ val+"' target=_blank>Explore</a>"; 
		    						sOut += "<br> <a href='"+ val+"' target=_blank>Compounds</a>";
		    						return sOut;		    						
		    					}
		    				}			    				
		     				],
		 	  "aaSorting": [[2, 'desc'],[1, 'asc']],
			   "fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
					oSettings.jqXHR = $.ajax({
						"type" : "GET",
						"url" : sSource,
						"data" : aoData,
						"dataType" : "json",
						"contentType" : "application/json",
						"cache" : true,
						"success": function(result){
							fnCallback(result);
							dataCallback(root,result);
						},
						"error" : function(xhr, textStatus, error) {
							switch (xhr.status) {
							case 403: {
					        	alert("Restricted access. You are not authorized to access the requested informatoin.");
								break;
							}
							case 404: {
								//not found
								break;
							}
							default: {
					        	alert("Error loading qmaps " + xhr.status + " " + error);
							}
							}
							oSettings.oApi._fnProcessingDisplay(oSettings, false);
						}
					});
				}	 		 	  
			});
			return oTable;
		},
		//end
		"defineChart" : function (root,results) {
			var qmaps = {};
			var property = null;
			results.qmap.forEach(function(map) {
				
				 map['name'] = "Activity difference " + map.activity.threshold + " Similarity " + map.similarity.threshold; 
				 qmaps[map.URI] = map;
				 property = map.activity.featureURI;
			});
			
			var margin = {top: 20, right: 20, bottom: 30, left: 40},
		    width = 640 - margin.left - margin.right,
		    height = 480 - margin.top - margin.bottom;

			var x = d3.scale.linear()
			    .range([0, width]);
	
			var y = d3.scale.linear()
			    .range([height, 0]);
	
			var color = d3.scale.category10();
	
			var xAxis = d3.svg.axis()
			    .scale(x)
			    .orient("bottom");
	
			var yAxis = d3.svg.axis()
			    .scale(y)
			    .orient("left");
	
			var svg = d3.select("#qchart").append("svg")
			    .attr("width", width + margin.left + margin.right)
			    .attr("height", height + margin.top + margin.bottom)
			  .append("g")
			    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
			var data = results.nodes.filter(function(element, index, array) {
				//no sense to draw different properties at the same chart!
				return (element.g2>0) && (qmaps[element.qmap].activity.featureURI = property);
			});	

			
			/*
			  data.forEach(function(d) {
			    d.activity = +d.activity;
			    d.g2 = +d.g2;
			    console.log(d);
			  });
			 */ 
			  
	
			  x.domain(d3.extent(data, function(d) { return d.activity; })).nice();
			  y.domain(d3.extent(data, function(d) { return d.g2; })).nice();
	
			  svg.append("g")
			      .attr("class", "x axis")
			      .attr("transform", "translate(0," + height + ")")
			      .call(xAxis)
			    .append("text")
			      .attr("class", "label")
			      .attr("x", width)
			      .attr("y", -6)
			      .style("text-anchor", "end")
			      .text(results.feature[property].title);
	
			  svg.append("g")
			      .attr("class", "y axis")
			      .call(yAxis)
			    .append("text")
			      .attr("class", "label")
			      .attr("transform", "rotate(-90)")
			      .attr("y", 6)
			      .attr("dy", ".71em")
			      .style("text-anchor", "end")
			      .text("G2")
	
			  svg.selectAll(".dot")
			      .data(data)
			    .enter().append("circle")
			      .attr("class", "dot")
			      .attr("r", 3.5)
			      .attr("cx", function(d) { return x(d.activity); })
			      .attr("cy", function(d) { return y(d.g2); })
			      .style("fill", function(d) { return color(qmaps[d.qmap].name); });
	
			  var legend = svg.selectAll(".legend")
			      .data(color.domain())
			    .enter().append("g")
			      .attr("class", "legend")
			      .attr("transform", function(d, i) { return "translate(-50," + i * 20 + ")"; });
	
			  legend.append("rect")
			      .attr("x", width - 18)
			      .attr("width", 18)
			      .attr("height", 18)
			      .style("fill", color);
	
			  legend.append("text")
			      .attr("x", width - 24)
			      .attr("y", 9)
			      .attr("dy", ".35em")
			      .style("text-anchor", "end")
			      .text(function(d) { return d; });
	
			
		}
}




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
			var results = null;
			var imgSize = 150;
			var oTable = $('.qnodestable').dataTable( {
			"sAjaxDataProp" : "nodes",
			"sAjaxSource": url,	
			"sSearch": "Filter:",
			"bJQueryUI" : true,
			"bSearchable": true,
			"bProcessing" : true,
			"sDom" : '<"help remove-bottom"l><"help remove-bottom"f>Trt<"help"ip>',
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
									var prm = {'option': 'similarity', 'type':'url', 'search':cmpURI, 'pagesize': 1};
									var searchURI = root + "/ui/query?" + $.param(prm,false);
									var id = qmap.getCompoundID(root,cmpURI);
									cmpURI = cmpURI + "?media=image/png";
									var sOut =  '<a href="'+searchURI+'" target=_blank>' +
										   '<img class="ui-widget-content" title="'+val+'"  border="0" src="' + cmpURI + '&w='+imgSize+'&h='+imgSize+'" onError="this.style.display=\'none\'">'
										   + "</a>";
									return sOut;
		    					}
		    				},	
		    				{ //2
		    					"aTargets": [ 1 ],	
		    					"bSortable" : true,
		    					"sClass" : 'top',
		    					"bSearchable" : true,
		    					"mDataProp" : "URI",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						var id = qmap.getCompoundID(root,val);
		    						return id;
		    									    						
		    					}
		    				},			    				
		    				{ //2
		    					"aTargets": [ 2 ],	
		    					"sClass" : "center",
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "g2",
		    					"bUseRendered" : true,	
		    					"fnRender" : function(o,val) {
		    						return val;
		    					}
		    				},	    	
		    				{ //3
		    					"aTargets": [ 3 ],	
		    					"asSorting" : [ "desc" ],
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "activity",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						return val;
		    					}
		    				},
		    				{ //3
		    					"aTargets": [ 4 ],	
		    					"sClass" : "names wrap",
		    					"asSorting" : [ "desc" ],
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "qmap",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						var qmaps = qmap.getQmapIndex(root,results);
		    						var map = val;
		    						var sOut = " <a href='"+root+"/toxmatch?qmap_uri="+ map+"' target=_blank>"+qmaps[val].name+"</a> "; 
		    						//sOut += "<a href='"+ map+"' target=_blank>Compounds</a>";
		    						return sOut;
		    					}
		    				}		    				
		     				],
		 	  "aaSorting": [[2, 'desc'],[3, 'asc']],
			   "fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
					oSettings.jqXHR = $.ajax({
						"type" : "GET",
						"url" : sSource,
						"data" : aoData,
						"dataType" : "json",
						"contentType" : "application/json",
						"cache" : true,
						"success": function(result){
							results = result;
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
		"getQmapIndex" : function (root,results) {
			if (results['qmapsindex']===undefined) {
				var qmaps = {};
				results.qmap.forEach(function(map) {
					 map['name'] = "Activity difference " + map.activity.threshold + " Similarity " + map.similarity.threshold; 
					 qmaps[map.URI] = map;
				});
				results['qmapsindex'] = qmaps;
			}
			return results['qmapsindex'];
		},
		//end
		"defineChart" : function (root,results,selector,w,h) {
			var qmaps = this.getQmapIndex(root,results);
			var property = null;
			results.qmap.forEach(function(map) {
				 property = map.activity.featureURI;
			});
			
			var margin = {top: 20, right: 20, bottom: 30, left: 40},
		    width = w - margin.left - margin.right,
		    height = h - margin.top - margin.bottom;

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
	
			var svg = d3.select(selector).append("svg")
			    .attr("width", width + margin.left + margin.right)
			    .attr("height", height + margin.top + margin.bottom)
			  .append("g")
			    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
			var data = results.nodes.filter(function(element, index, array) {
				//no sense to draw different properties at the same chart!
				return (element.g2>0) && (qmaps[element.qmap].activity.featureURI = property);
			});	
	
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
	
			
		},
		"defineBubbleChart" : function (root,results,selector,w,h) {
			var qmaps = this.getQmapIndex(root,results);
			var diameter = w,
		    format = d3.format(",d"),
		    //color = d3.scale.category20c();
			color = d3.scale.category10();
			
			var bubble = d3.layout.pack()
			    .sort(null)
			    .size([diameter, diameter])
			    .padding(1.5);
	
			var svg = d3.select(selector).append("svg")
			    .attr("width", diameter)
			    .attr("height", diameter)
			    .attr("class", "bubble");
	
			 $.each(results.nodes, function(index, value) {
				 value['value'] = value.g2;
			 });
			 var n = {children: results.nodes};

			  var node = svg.selectAll(".node")
			      .data(bubble.nodes(n)
			      .filter(function(d) { return !d.children;  }))
			    .enter().append("g")
			      .attr("class", "node")
			      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
	
			  node.append("title")
			      .text(function(d) {
			    	  var property = qmaps[d.qmap].activity.featureURI;
			    	  return qmaps[d.qmap].name + " [g2=" + d.g2 + "] [ "+ results.feature[property].title + " = "+d.activity+"] " + d.URI; }
			      );
	
			  node.append("circle")
			      .attr("r", function(d) { return d.r; })
			      .style("fill", function(d) { return color(qmaps[d.qmap].name); });
	
			  node.append("text")
			      .attr("dy", ".3em")
			      .style("text-anchor", "middle")
			      .text(function(d) { 
			    	  return qmap.getCompoundID(root,d.URI).substring(0, d.r / 3);
			    	  /*
			    	  var i = d.URI.indexOf("/compound/");
			    	  if (i >= 0) 
			    		  return d.URI.substring(i+10, d.URI.length).substring(0, d.r / 3); 
			    	  else
			    		  return d.qmap.substring(0, d.r / 3);
			    		  */ 
			       });

	
			d3.select(self.frameElement).style("height", diameter + "px");

			
		},
//end defineBubbleChart		
		"condenseNodes"  : function (root,results) {
			
		},
		"getSimilar" : function (root) {
			/**
			http://localhost:8080/ambit2/dataset/112/similarity?type=url&threshold=0.59&search=http://localhost:8080/ambit2/compound/12466/conformer/17513&feature_uris[]=http://localhost:8080/ambit2/feature/274
			*/
		},
		"getCompoundID" : function (root,cmpURI) {
			 var i = cmpURI.indexOf("/compound/");
	    	  if (i >= 0) 
	    		  return cmpURI.substring(i+10, cmpURI.length); 
	    	  else
	    		  return cmpURI; 
		}
}




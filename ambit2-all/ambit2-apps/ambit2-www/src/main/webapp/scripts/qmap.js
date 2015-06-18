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
		    					"sClass" : "left",
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "dataset",
		    					"bUseRendered" : false,	
		    					"sWidth" : "50%",
		    					"fnRender" : function(o,val) {
		    						var sOut = (val.title==""?val.URI:val.title);
		    						sOut += " <a href='"+ val.URI+"/metadata' target=_blank title='Dataset metadata' >Metadata</a>";
		    						sOut += "| <a href='" + root + "/ui/_dataset?dataset_uri=" + encodeURIComponent(val.URI)+"' target=_blank title='Browse the dataset' >Browse</a>";
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
		    						var sOut = "<a href='"+ val.featureURI+"' title='Browse this feature' target='feature'>"+ val.feature.title+"</a> " + val.feature.units ;
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
		    					"sWidth" : "5%",
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
		    					"sWidth" : "5%",
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
		    						var sOut =  "<a href='"+root+"/toxmatch?qmap_uri="+ val+"' target=_blank title='Interactive visualisation'>This QMap</a>"; 
		    						sOut += "| <a href='" + root + "/ui/_dataset?dataset_uri=" + encodeURIComponent(val) +"' target=_blank title='Ranked compounds'>Compounds</a>";
		    						return sOut;
		    					}
		    				},
		    				{ //0
		    					"aTargets": [ 5 ],	
		    					"sClass" : "center",
		    					"bSortable" : true,
		    					"bSearchable" : true,
		    					"mDataProp" : "dataset",
		    					"bUseRendered" : false,	
		    					"fnRender" : function(o,val) {
		    						var sOut =  "<a href='"+root+"/toxmatch?dataset_uri="+ val.URI+"' target=_blank title='All QMaps for this dataset'>All QMaps</a>";
		    						sOut +=  " | <a href='"+root+"/toxmatch?dataset_uri="+ val.URI+"&feature_uris[]="+o.aData.activity.featureURI+"' target=_blank title='QMaps for this dataset and this activity'>This activity QMaps</a>";
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
			//"sDom" : '<"help remove-bottom"l><"help remove-bottom"f>Trt<"help"ip>',
			"sDom" : '<"help remove-bottom"><"help"p>Trt<"help"ilf>',
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
		          '<option value="3">3</option>' +		            
		          '<option value="10">10</option>' +
		          '<option value="20">20</option>' +
		          '<option value="50">50</option>' +
		          '<option value="100">100</option>' +
		          '<option value="-1">all</option>' +
		          '</select> structures.'	            
		    },	
		    "iDisplayLength": 3,
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
		    						return "<span style='font-weight:bold;'>" + id + "</span>";
		    									    						
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
							dataCallback(root,result,oTable);
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
		"getCompoundIndex" : function (root,results) {
			if (results['cmpindex']===undefined) {
				var cmpx = {};
				results.nodes.forEach(function(cmp,index,array) {
					if (cmpx[cmp.qmap]===undefined) cmpx[cmp.qmap] = {};
					var id = qmap.getCompoundID(root,cmp.URI);
					cmpx[cmp.qmap][id] = index;
				});
				results['cmpindex'] = cmpx;
			}
			return results['cmpindex'];
		},		
		/** Links between the nodes, qmap specific */
		"getLinks" : function (root,results,qmapuri) {
			if (results['links']===undefined) results['links'] = {};
			
			if (results['links'][qmapuri]===undefined) results['links'][qmapuri] = [];
			return results['links'][qmapuri];
		},			
		"getQmapIndex" : function (root,results) {
			if (results['qmapsindex']===undefined) {
				var qmaps = {};
				results.qmap.forEach(function(map) {
					 map['name'] = "Activity difference >=" + map.activity.threshold + " Similarity >=" + map.similarity.threshold; 
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
			      .style("fill", function(d) { return (d.a==0.0)?"silver":color(qmaps[d.qmap].name); });

	
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

		"defineBubbleChart" : function (root,results,selector,w,h,nodesTable) {
			var qmaps = this.getQmapIndex(root,results);
			var diameter = w,
		    format = d3.format(",d");
		    //color = d3.scale.category20c();
			var color = d3.scale.category10();
/*
			//color = d3.scale.linear().domain([1,10]).range(['red', 'blue']);
			var cmpx = qmap.getCompoundIndex(root,results);
			//var color = d3.scale.linear().domain([0,results.nodes.length]).range(['red', 'blue']).interpolate(d3.interpolateHcl);

			//this will make an unique color for every node
			var hue = d3.scale.linear().domain([0,results.nodes.length]).range([0,360]);
			var color = function(index) {return d3.hsl(hue(index),0.5,0.5);	};
*/			
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
			      .attr("uri", function(d) { return d.URI; })
			      .attr("qmap", function(d) { return d.qmap; })
			      .style("fill", function(d) { return (d.a==0.0)?"silver":color(qmaps[d.qmap].name); })
/*
			      .style("fill", function(d) {
			    	  var id = qmap.getCompoundID(root,d.URI);
			    	  return color(cmpx[qmaps[d.qmap].URI][id]);
			      })
*/
			      .on("click", function(){
			    	  //Filter the nodes table to show only the selected compound (by URI)
			    	  nodesTable.fnFilter(d3.select(this).attr("uri"));
			    	  qmap.getSimilar(root,results,d3.select(this).attr("qmap"),d3.select(this).attr("uri"),"#cliffs","#notcliffs");
			    	  qmap.getTautomersByCompound(root,results,d3.select(this).attr("qmap"),d3.select(this).attr("uri"),"#tautomers");
			      }
			      );

	
			  node.append("text")
			      .attr("dy", ".3em")
			      .attr("uri", function(d) { return d.URI; })
			      .attr("qmap", function(d) { return d.qmap; })
			      .style("text-anchor", "middle")
			      .text(function(d) { 
			    	  return qmap.getCompoundID(root,d.URI).substring(0, d.r / 3);
			       })
			      .on("click", function(){  
			    	  nodesTable.fnFilter(d3.select(this).attr("uri"));
			    	  qmap.getSimilar(root,results,d3.select(this).attr("qmap"),d3.select(this).attr("uri"),"#cliffs","#notcliffs");
			      }
			      );
	
			d3.select(self.frameElement).style("height", diameter + "px");

			
		},
//end defineBubbleChart		
		"getSimilar" : function (root,results,qmapuri,cmpuri,cliffs,notcliffs) {

			var targetSelector = notcliffs;
			$(cliffs).empty();
			$(notcliffs).empty();
			$(cliffs).append('<li>Retrieving activity cliffs...</li>');
			$(notcliffs).append('<li>Retrieving the smooth landscape part ...</li>');

			var qmaps = this.getQmapIndex(root,results);
			var map = qmaps[qmapuri];
			var feature = results.feature[map.activity.featureURI];
			$("#simtitle").html(
					"<a href='"+root+"/toxmatch?qmap_uri=" + qmapuri + "' title='" +qmapuri + "' target=_blank>QMap</a> : " +
					"[" + map.name + "] " +
					"Activity: <a href='" + map.activity.featureURI + "' title='" + map.activity.featureURI + "' target=_blank>"+ feature.title + " " + feature.units +"</a> " + 
					"Dataset: <a href='" + map.dataset.URI + "/metadata' title='" + map.dataset.URI + "' target=_blank>"+map.dataset.URI +"</a> " 					
					
					);
			if ((map===undefined) || (map==null)) return;
			var query = map.dataset.URI + "/similarity?type=url&threshold="+map.similarity.threshold+"&search=" + cmpuri + "&media=application/json";
			/**
			http://localhost:8080/ambit2/dataset/112/similarity?type=url&threshold=0.59&search=http://localhost:8080/ambit2/compound/12466/conformer/17513&feature_uris[]=http://localhost:8080/ambit2/feature/274
			*/
			var cmpx = qmap.getCompoundIndex(root,results);
			var myid = qmap.getCompoundID(root,cmpuri);
			var myactivity = results.nodes[cmpx[qmapuri][myid]].activity;
			
			var links = qmap.getLinks(root,results,qmapuri);

			$.ajax( {
				url: query,
		        "dataType": "json", 
		        "contentType" : "application/json",
		        "success": function(json) {
	    			$(cliffs).empty();
	    			$(notcliffs).empty();
					json.dataEntry.forEach(function img(element, index, array) {
						var id = qmap.getCompoundID(root,element.compound.URI);
						var node = results.nodes[cmpx[qmapuri][id]];
						var activity = "";
						if (node === undefined) {
							//means these are not within the cliffs
							targetSelector = notcliffs;
						} else {
							activity = node.activity;
							var nameid = qmap.getID();
							var style = "";
							

							if (id==myid) {
								targetSelector = cliffs;
								style = "color:red;";
							} else if (Math.abs(myactivity-activity)>= map.activity.threshold) {
								//now these are the cliffs
								targetSelector = cliffs;
								style = "color:blue;";

								var id1,id2;
								if (myid<id) {id1 = myid; id2 = id;	} else { id1 = id; id2 = myid;}
								
								var thislink = links.filter(function(element, index, array) {
									return (element.source == cmpx[qmapuri][id1]) && (element.target == cmpx[qmapuri][id2]);
								});
								if (thislink.length==0) {
									links.push({"source":cmpx[qmapuri][id1],"target":cmpx[qmapuri][id2],"value": element.compound.metric});
								}	
							} else {
								targetSelector = notcliffs;
							}	
							
						}		
						$(targetSelector).append('<li>'+qmap.cmp2image(element.compound.URI)+
							  		  '<div style="margin-top:5px;">Tanimoto='+element.compound.metric+'</div>'+
							  		  '<div style="margin-top:5px;'+style+'">Activity='+activity+'</div>'+
									  '<div style="margin-top:5px;font-weight:bold;'+style+'">ID ='+id+'</div>'+
									  '<div style="margin-top:5px;" id="'+nameid+'"></div></li>');
						qmap.loadStructureIds("",root,element.compound.URI,qmap.renderStructureIds,'#'+nameid);
						
       				});
		        },
		        "cache": false,
		        "statusCode" : {
		            400: function() {
		    			$(cliffs).empty();
		    			$(notcliffs).empty();
		            	$(targetSelector).append('<li class="ui-state-default" >Not found</li>');
		            },
		            404: function() {
		    			$(cliffs).empty();
		    			$(notcliffs).empty();
		            	$(targetSelector).append('<li class="ui-state-default" >Not found</li>');
			        }
		        },
		        "error" : function( xhr, textStatus, error ) {
					$(cliffs).empty();
					$(notcliffs).empty();
		        	$(targetSelector).append('<li class="ui-state-default" >'+error + '</li>');
		        }
		      } );			
			
		},
		"getTautomersByCompound" : function (root,results,qmapuri,cmpuri,targetSelector) {
			
			var cmpx = qmap.getCompoundIndex(root,results);
			var myid = qmap.getCompoundID(root,cmpuri);
			var node = results.nodes[cmpx[qmapuri][myid]];
			if (node.tautomerOf === undefined) return;
			
			$(targetSelector).empty();
			$(targetSelector).show();
			$(targetSelector).append('<li>Retrieving tautomers ...</li>');

			var qmaps = this.getQmapIndex(root,results);
			var map = qmaps[qmapuri];
			if ((map===undefined) || (map==null)) return;


			var myactivity = results.nodes[cmpx[qmapuri][myid]].activity;
			
			var query = root + "/query/relation/compound/has_tautomer?dataset_uri="+node.tautomerOf.URI + "&media=application/json"; 
			$.ajax( {
				url: query,
		        "dataType": "json", 
		        "contentType" : "application/json",
		        "success": function(json) {
	    			$(targetSelector).empty();
					json.dataEntry.forEach(function img(element, index, array) {
						var id = qmap.getCompoundID(root,element.compound.URI);
						var node = results.nodes[cmpx[qmapuri][id]];
						var activity = "";
						if (node === undefined) {
							//means these are not within the cliffs
						} else {
							activity = node.activity;
							var nameid = qmap.getID();
							var style = "";
							

							if (id==myid) {
								style = "color:red;";
							} else if (Math.abs(myactivity-activity)>= map.activity.threshold) {
								//now these are the cliffs
								style = "color:blue;";
							}  
								
							
						}		
						$(targetSelector).append('<li>'+qmap.cmp2image(element.compound.URI)+
							  		  '<div style="margin-top:5px;">Tautomer rank='+element.compound.metric+'</div>'+
							  		  '<div style="margin-top:5px;'+style+'">Activity='+activity+'</div>'+
									  '<div style="margin-top:5px;font-weight:bold;'+style+'">ID ='+id+'</div>'+
									  '<div style="margin-top:5px;" id="'+nameid+'"></div></li>');
						qmap.loadStructureIds("",root,element.compound.URI,qmap.renderStructureIds,'#'+nameid);
						
       				});
		        },
		        "cache": false,
		        "statusCode" : {
		            400: function() {
		            	$(targetSelector).append('<li class="ui-state-default" >Not found</li>');
		            },
		            404: function() {
		            	$(targetSelector).append('<li class="ui-state-default" >Not found</li>');
			        }
		        },
		        "error" : function( xhr, textStatus, error ) {
		        	$(targetSelector).append('<li class="ui-state-default" >'+error + '</li>');
		        }
		      } );			
			
		},		
		"getTautomers" : function (root,results,dataseturi,targetselector,callback) {
			var query = root + "/query/relation/dataset/has_tautomer?dataset_uri="+dataseturi + "&media=application/json";
			$(targetselector).empty();

			
			$.ajax( {
				url: query,
		        "dataType": "json", 
		        "contentType" : "application/json",
		        "success": function(json) {
	    			$(targetselector).empty();
	    			var cmpx = qmap.getCompoundIndex(root,results);
    				var tautomers = {};
    				var tkeys = [];
	    			json.links.forEach(function img(element, index, array) {
		    			var source = qmap.getCompoundID(root,element.source);
		    			var target = qmap.getCompoundID(root,element.target);
						 $.each(results['qmap'], function(index, value) {
							 var qmapuri = value.URI;
							 if ((cmpx[qmapuri][source] != undefined) && (cmpx[qmapuri][target] != undefined)) {
								 //this is a bit inefficient TODO: get only tautomers of g2  > 0 & a > 0
								 var tautomerOf = {};
								 tautomerOf["URI"] = element.source;
								 tautomerOf["rank"] = element.value;
								 results.nodes[cmpx[qmapuri][target]]["tautomerOf"] = tautomerOf;
								 if (tautomers[source] === undefined) {
									 tautomers[source] = tkeys.length;
									 tkeys.push(source);
								 }
							 }	 
						});
	    			});
	    			callback(tautomers,tkeys.length);
		        },
		        "cache": false,
		        "statusCode" : {
		            400: function() {
		            	$(targetselector).append('<li class="ui-state-default" >Not found</li>');
		            },
		            404: function() {
		            	$(targetselector).append('<li class="ui-state-default" >Not found</li>');
			        }
		        },
		        "error" : function( xhr, textStatus, error ) {
		        	$(targetselector).append('<li class="ui-state-default" >'+error + '</li>');
		        }
		      });	
		},	
		"renderStructureIds" : function (nameSelector,names,cas,smiles,inchi,inchikey) {
			try {
				$(nameSelector).html(names.toLowerCase());
			} catch (err) {
				$(nameSelector).html(names);
			}
		},		
		"loadStructureIds" : function (prefix,query_service,compoundURI,callback,nameSelector) {

			var id_uri = query_service + "/query/compound/url/all?search=" + encodeURIComponent(compoundURI) + "&max=1&media=application%2Fx-javascript";
			$.ajax({
			         dataType: "jsonp",
			         "crossDomain": false, 
			         url: id_uri,
			         success: function(data, status, xhr) {
			        	identifiers(data);
			        	$(nameSelector).html("&nbsp;");
			        	$.each(data.dataEntry,function(index, entry) {
			        		callback(nameSelector,
			        				qmap.formatValues(entry,"names"),
			        				qmap.formatValues(entry,"cas"),
			        				qmap.formatValues(entry,"smiles"),
			        				qmap.formatValues(entry,"inchi"),
			        				qmap.formatValues(entry,"inchikey"));
			        	});
			         },
			         error: function(xhr, status, err) {
			        	 $(nameSelector).html("&nbsp;");
			         },
			         complete: function(xhr, status) { }
			});	
		},
		"getCompoundID" : function (root,cmpURI) {
			 var i = cmpURI.indexOf("/compound/");
			 var j = cmpURI.indexOf("/conformer/");
			 if (j<=0) j = cmpURI.length; 
	    	  if (i >= 0) 
	    		  return cmpURI.substring(i+10, j); 
	    	  else
	    		  return cmpURI; 
		},
		"getID" : function() {
			   return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
		},
		"cmp2image" : function(val) {
			var cmpURI = val;
			if (val.indexOf("/conformer")>=0) {
				cmpURI = val.substring(0,val.indexOf("/conformer"));
			}								
			cmpURI = cmpURI + "?media=image/png";
			var id= cmpURI.replace(/:/g,"").replace(/\//g,"").replace(/\./g,"");
			return '<a href="'+val+'" target="cmp">' + 
			'<img border="0" style="background-color:#ffffff" alt="'+val+'" src="'+cmpURI+'&w=150&h=150" >'+
			'</a>';
			
	  },
	  "formatValues" : function (dataEntry,tag) {
			var sOut = "";
			var line = 0;
			var id = "n" + qmap.getID();
			var cache = {};
			$.each(dataEntry.lookup[tag], function(index, value) { 
			  if (dataEntry.values[value] != undefined) {
				  $.each(dataEntry.values[value].split("|"), function (index, v) {
					  if (v.indexOf(".mol")==-1) {
						if ("" != v) {  
							var lv = v.toLowerCase();
							if (cache[lv]==null) {
								switch(line) {
								case 0:
									sOut += "<span class='long' title='"+v+"'>"+v+"</span>";
								    break;
								case 1:
									sOut += "<span style='display:none;' id='"+id+"'>";
									sOut += v;
								    break;
								default:
								  	sOut += "<br/>";
									sOut += v;
								}
							  	line++;
							  	cache[lv] = true;
							}
						}
				  	  }
				  });
			  }
			});
			
			switch(line) {
			case 0: 
				sOut += "&nbsp;";
				break;
			case 1: 
				break;
			default:  
			   sOut += "</span>";
			   sOut += " <a href='#' onClick='$(\"#"+id+"\").toggle();' title='Click here for more synonyms'>&raquo;</a> ";
			}
			return sOut;
		},
		"colorByMap" : function (root,results) {
			var qmaps = this.getQmapIndex(root,results);
			var color = d3.scale.category10();
			d3.selectAll("circle")
			.transition()
            .style("fill", function(d) {
            	return (d.a==0.0)?"silver":color(qmaps[d.qmap].name); 
            });	  	
		},
		"colorByTautomer" : function(root,result) {
				if (result["tautomers"] === undefined) return;
  				
  				var cmpx = qmap.getCompoundIndex(root,result);
  				var qmaps = qmap.getQmapIndex(root,result);
  				var hue = d3.scale.linear().domain([0,result.ntautomers]).range([0,360]);
  				var color = function(index) {return d3.hsl(hue(index),0.5,0.5);	};

				d3.selectAll("circle")
					.transition()
                    .style("fill", function(d) {
                     if (d.a==0.0) return "silver";
			    	  if ((d.tautomerOf === undefined) || (d.tautomerOf.URI === undefined) || (d.URI == d.tautomerOf.URI)) {
			    		  var id = qmap.getCompoundID(root,d.URI);
				    	  return color(result.tautomers[id]);
			    	  } else {
			    		  var id = qmap.getCompoundID(root,d.tautomerOf.URI);
				    	  return color(result.tautomers[id]);		    		  
			    	  }
                    });	  	
		},
		<!-- network graph -->
		"drawGraph":function (root,data,qmapuri,chartselector,width,height,nodesTable) {
			  var qmaps = this.getQmapIndex(root,data);
			  var nodes = data.nodes; //data.nodes.filter(function(node) { return node.qmap==qmapuri;  }); indexes will be different...
			  var color = d3.scale.category10();

			  var fisheye = d3.fisheye.circular().radius(120);

			  var force = d3.layout.force()
			      .charge(-120)
			      .linkDistance(30)
			      .size([width, height]);

			  $(chartselector).empty();
			  var svg = d3.select(chartselector).append("svg")
			      .attr("width", width)
			      .attr("height", height);

			  svg.append("rect")
			      .attr("class", "background")
			      .attr("width", width)
			      .attr("height", height);

			    var n = nodes.length;

			    force.nodes(nodes).links(data.links[qmapuri]);

			    // Initialize the positions deterministically, for better results.
			    nodes.forEach(function(d, i) { d.x = d.y = width / n * i; });

			    // Run the layout a fixed number of times.
			    // The ideal number of times scales with graph complexity.
			    // Of course, don't run too long�you'll hang the page!
			    force.start();
			    for (var i = n; i > 0; --i) force.tick();
			    force.stop();

			    // Center the nodes in the middle.
			    var ox = 0, oy = 0;
			    data.nodes.forEach(function(d) { ox += d.x, oy += d.y; });
			    ox = ox / n - width / 2, oy = oy / n - height / 2;
			    data.nodes.forEach(function(d) { d.x -= ox, d.y -= oy; });

			    var link = svg.selectAll(".link")
			        .data(data.links[qmapuri])
			      .enter().append("line")
			        .attr("class", "link")
			        .attr("x1", function(d) { return d.source.x; })
			        .attr("y1", function(d) { return d.source.y; })
			        .attr("x2", function(d) { return d.target.x; })
			        .attr("y2", function(d) { return d.target.y; })
			        .style("stroke-width", function(d) { return Math.sqrt(d.value*20); });

			/*
			    var node = svg.selectAll(".node")
			        .data(data.nodes)
			      .enter().append("circle")
			        .attr("class", "node")
			        .attr("cx", function(d) { return d.x; })
			        .attr("cy", function(d) { return d.y; })
			        .attr("r", 10)
			        .style("fill", function(d) { return color(d.group); })
			        .call(force.drag);
			*/

			  var node = svg.selectAll(".node")
			      .data(data.nodes)
			      .enter().append("g")
			        .attr("cx", function(d) { return d.x; })
			        .attr("cy", function(d) { return d.y; })
			        .call(force.drag);

			  
			var circle =  node.append("circle")
				   .attr("class", "node")
			        .attr("cx", function(d) { return d.x; })
			        .attr("cy", function(d) { return d.y; })
			        .attr("r", 10)
			        .attr("uri", function(d) { return d.URI; })
			        .style("fill", function(d) { return color(d.qmap); })
				    .on("click", function(){
				    	  //Filter the nodes table to show only the selected compound (by URI)
				    	nodesTable.fnFilter(d3.select(this).attr("uri"));
				    })			        
			        .call(force.drag);

			  node.append("title")
		      .text(function(d) {
		    	  var property = qmaps[d.qmap].activity.featureURI;
		    	  return qmaps[d.qmap].name + " [g2=" + d.g2 + "] [ "+ data.feature[property].title + " = "+d.activity+"] " + d.URI; }
		      );
			  

			var text = node.append("text")
			      .attr("x", function(d) { return d.x; })
			      .attr("y", function(d) { return d.y; })
			      .attr("uri", function(d) { return d.URI; })
			      .style("color","red")
			      .attr("text-anchor", "end")
			      .text(function(d) { 
			    	  return qmap.getCompoundID(root,d.URI);
			      })
			      .on("click", function(){
				   	  //Filter the nodes table to show only the selected compound (by URI)
				   	nodesTable.fnFilter(d3.select(this).attr("uri"));
				  });	

			    svg.on("mousemove", function() {
			      fisheye.focus(d3.mouse(this));
			      circle.each(function(d) { d.fisheye = fisheye(d); })
			          .attr("cx", function(d) { return d.fisheye.x; })
			          .attr("cy", function(d) { return d.fisheye.y; })
			          .attr("r", function(d) { return d.fisheye.z * 10;});

			      text.each(function(d) { d.fisheye = fisheye(d); })
			          .attr("x", function(d) { return d.fisheye.x; })
			          .attr("y", function(d) { return d.fisheye.y; });

			      link.attr("x1", function(d) { return d.source.fisheye.x; })
			          .attr("y1", function(d) { return d.source.fisheye.y; })
			          .attr("x2", function(d) { return d.target.fisheye.x; })
			          .attr("y2", function(d) { return d.target.fisheye.y; });
			    });
			  
			}

}




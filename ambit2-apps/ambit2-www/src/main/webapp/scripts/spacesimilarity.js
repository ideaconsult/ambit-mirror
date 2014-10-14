/**
 * @param oSpace
 * @param iMax
 * @returns
 */
function spaceMatrix(oSpace,iMax,method) {
		
	var mColor = "similarity";
	var selectedRow = 0;
	//var selectedRow = 0;
	//matrix starts
	var margin = {top: 40, right: 0, bottom: 10, left: 120},
	    width = 760,
	    height = 760;

	var maxRange = Math.abs(oSpace.range[oSpace.range.length-1]-oSpace.range[0]);
	var step = Math.abs(oSpace.range[1]-oSpace.range[0]);
	
	var dActivity = function(cell) {
		var left = cell.x, right = cell.y;
		if (left > right) {left = cell.y; right = cell.x; }
		return (oSpace.range[right]+step-oSpace.range[left])/maxRange;
	};
	
	var datasetLink = d3.select("#dataset");
	datasetLink.attr("href",oSpace["datasetURI"]);
	datasetLink.attr("title",oSpace["datasetURI"]);
	
	var selectBox = d3.select("#order");
	selectBox.append("option").attr("value","name").text("by Activity").attr("selected","on");
	//selectBox.append("option").attr("value","count").text("by Frequency");
	//selectBox.append("option").attr("value","group1").text("by Similarity");
	selectBox.append("option").attr("value","group2").text("by Ridges");
	
	var colorBox = d3.select("#mcolor");
	colorBox.append("option").attr("value","similaritygeo").text("Similarity (Geo map)").attr("title","Colors as in geographic map").attr("selected","on");
	colorBox.append("option").attr("value","similaritygray").text("Similarity (Gray)");
	colorBox.append("option").attr("value","edge").text("Highlight ridges");
	colorBox.append("option").attr("value","ridge").text("Ridges only");
	
	var rangesBox = d3.select("#yaxis");
	rangesBox.append("option").attr("value","similarity").text("Similarity");
	rangesBox.append("option").attr("value","dactivity").attr("selected","on").text("Absolute activity difference");
	rangesBox.append("option").attr("value","logdactivity").text("log(Absolute Activity difference)");
	//rangesBox.append("option").attr("value","dactivitylog").text("Difference log(Activity)");
	//rangesBox.append("option").attr("value","activityLeft").text("Activity (left)");
	//rangesBox.append("option").attr("value","activityRight").text("Activity (right)");
	rangesBox.append("option").attr("value","activityMiddle").text("Activity");
	rangesBox.append("option").attr("value","binsdiff").attr("title","Equivalent to a nonlinear transformation of the activity").text("Number of bins difference");
	rangesBox.append("option").attr("value","bins").attr("title","Equivalent to a nonlinear transformation of the activity").text("Activity bins");
	rangesBox.append("option").attr("value","SALI").text("SALI");
	rangesBox.append("option").attr("value","logSALI").text("log(SALI)");
	rangesBox.append("option").attr("value","edge").text("Ridges");
	
	var xAxisBox = d3.select("#xaxis");
	xAxisBox.append("option").attr("value","similarity").attr("selected","on").text("Similarity");
	xAxisBox.append("option").attr("value","dactivity").text("Absolute activity difference");
	xAxisBox.append("option").attr("value","logdactivity").text("log(Absolute Activity difference)");
	//rangesBox.append("option").attr("value","dactivitylog").text("Difference log(Activity)");
	//xAxisBox.append("option").attr("value","activityLeft").text("Activity (left)");
	//xAxisBox.append("option").attr("value","activityRight").text("Activity (right)");
	xAxisBox.append("option").attr("value","activityMiddle").text("Activity");
	xAxisBox.append("option").attr("value","binsdiff").attr("title","Equivalent to a nonlinear transformation of the activity").text("Number of bins difference");
	xAxisBox.append("option").attr("value","bins").attr("title","Equivalent to a nonlinear transformation of the activity").text("Activity bins");
	xAxisBox.append("option").attr("value","SALI").text("SALI");
	xAxisBox.append("option").attr("value","logSALI").text("log(SALI)");
	xAxisBox.append("option").attr("value","edge").text("Ridges");
	
	d3.select("#cmp1list").on("change", function() {
    	d3.select("#cmp1").attr("title",this.value).attr("src",this.value + "?media=image/png");
    });
	d3.select("#cmp2list").on("change", function() {
    	d3.select("#cmp2").attr("title",this.value).attr("src",this.value + "?media=image/png");
    });
    
	var svg = d3.select("#matrix").append("svg")
	    .attr("width", width + margin.left + margin.right)
	    .attr("height", height + margin.top + margin.bottom)
	    .style("margin-left", -margin.left + "px")
	    .append("g")
	    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	var nodes = [];
	
	
	var left = null;
	oSpace.range.forEach(function(node) {
		if (left != null) {
			nodes.push({"name":left,"group1":node,"group2":left,"count":0,"index":nodes.length,"uri": [] });
		}	
		left = node;
 
	});
	
	n = nodes.length;
	var matrix = new Array(n);
	for (var i = 0; i < n; i++) matrix[i] = new Array(n);
	  
	var maxValue = null;  
	oSpace.matrix.forEach(function(node,i) {
		matrix[node[0]][node[1]] = {x: node[1], y: node[0], z: node[2]};
		if ("g2row"!=method)
			matrix[node[1]][node[0]] = {x: node[0], y: node[1], z: node[2]};
	//	matrix[node[1]][node[1]].z += node[2];
	//	matrix[node[0]][node[0]].z += node[2];
		if ((maxValue == null) || (maxValue<node[2])) 
			maxValue = node[2];
			
	});
	
	console.log("maxValue " + maxValue);

	var sims = oSpace.matrix.map(function(node) { return node[2] });
	
	var x = d3.scale.ordinal().rangeBands([0, width]),
    	z = d3.scale.linear().domain( [0,        0.125,    0.25,     0.375,    0.5,      0.625,    0.75,     0.825,  0.99, 1]).
    						range(["#000080","#00008B","#6495ED","#CAE1FF","#00C78C","#66FF33","#FFFF99","#FFEC8B","#CC6600","#B32D00"]),
		//zz = d3.scale.sqrt().domain([0,1,maxValue/8,2*maxValue/8,3*maxValue/8,4*maxValue/8,5*maxValue/8,6*maxValue/8,7*maxValue/8,maxValue]).	
		//				range(["#000080","#00008B","#6495ED","#CAE1FF","#00C78C","#66FF33","#FFFF99","#FFEC8B","#CC6600","#B32D00"]),
    	zz = d3.scale.linear().domain([0,maxValue]).range(["blue","red"]),
    			//range(["#000080","#00008B","#6495ED","#CAE1FF","#00C78C","#66FF33","#FFFF99","#FFEC8B","#CC6600","#B32D00"]),
    	//zgray = d3.scale.linear().domain([0,maxValue]).range(["black","white"]),
    	//zgray = d3.scale.quantile().domain(sims).range([1,0.95,0.9,0.85,0.8,0.75,0.7,0.65,0.6,0.55,0.5,0.45,0.4,0.35,0.3,0.25,0.2,0.15,0.1,0.05,0]),
    	
    	zgray = d3.scale.quantile().domain(sims).range([0,0.05,0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5,0.55,0.6,0.65,0.7,0.75,0.8,0.85,0.9,0.95,1]),
    	
		//zgray = d3.scale.sqrt().domain([0,maxValue]).range([1,0]);
		//c = d3.scale.linear().domain([0, (1-maxValue.z)/dActivity(maxValue)]);
    	c = d3.scale.linear().domain([0, 1]).clamp();
		
		//console.log(zgray.quantiles());
	  
	  var selectedPointsColor = function(d) {
		  return (selectedRow==(d[0]<d[1]?d[0]:d[1]))?"red":z(d[2]);
	  };
	  var selectedPointsSize = function(d) {
		  return (selectedRow==(d[0]<d[1]?d[0]:d[1]))?4:2;
	  };
	  
	//finding edges
	  matrix.forEach(function (row,i) {
		 row.forEach(function (cell,j) {
			  matrix[i][j]["edge"] = convolution(matrix,matrix.length-1,matrix.length-1,i,j);
		 });
	  });

	  
  // Precompute the orders.
  var orders = {
    name: d3.range(n).sort(function(a, b) { return d3.ascending(nodes[a].name, nodes[b].name); }),
    count: d3.range(n).sort(function(a, b) { return nodes[b].count - nodes[a].count; }),
    group1: d3.range(n).sort(function(a, b) { return nodes[b].group1 - nodes[a].group1; }),
    group2: d3.range(n).sort(function(a, b) { try {
    		return matrix[b][b].edge - matrix[a][a].edge;
    	} catch (err) {
    		return 1; 
    	};
    })
  };
  
  // The default sort order.
  x.domain(orders.name);

  svg.append("rect")
      .attr("class", "background")
      .attr("width", width)
      .attr("height", height);

  var row = svg.selectAll(".row")
      .data(matrix)
    .enter().append("g")
      .attr("class", "row")
      .attr("transform", function(d, i) { return "translate(0," + x(i) + ")"; })
      .each(row);

  row.append("line")
      .attr("x2", width);

  row.append("text")
      .attr("x", -6)
      .attr("y", x.rangeBand() / 2)
      .attr("dy", ".32em")
      .attr("text-anchor", "end")
      .text(function(d, i) { return nodes[i].name; });

  var column = svg.selectAll(".column")
      .data(matrix)
    .enter().append("g")
      .attr("class", "column")
      .attr("transform", function(d, i) { return "translate(" + x(i) + ")rotate(-90)"; });

  column.append("line")
      .attr("x1", -width);

  column.append("text")
      .attr("x", 6)
      .attr("y", x.rangeBand() / 2)
      .attr("dy", ".32em")
      .attr("text-anchor", "start")
      .text(function(d, i) { return nodes[i].name; });
  /*
  /*similarity = opacity  return d.z
  /*diff in activity - color return return z(dActivity(d)/maxRange);
  */
  d3.select("#mcolor").on("change", function() {
	  mColor = this.value;
	  var t = svg.transition().duration(100);
	    t.selectAll(".cell")
	      .style("fill-opacity", function(d) {
	    	  return cellTransparency(d) ;
        }) 
        .style("fill", function(d) {
	    	  return cellColor(d);        	
       	});
  }); 
  //fill
  function cellColor(d) {
  	switch (mColor) {
	case "edge": 
		return  z(d.z);   
		break;
	case "ridge": 
		return  1;   
		break;		
	case "similaritygray": 
		 return 1;
		break;			
	default:   //similarity geo
		return  zz(d.z);       		
	}
  }
  //fill-opacity
  function cellTransparency(d) {
  	switch (mColor) {
	case "edge": 
		 return d.edge>1?0.075:d.edge>0.9?0.1:(1-d.edge);
		break;  	
	case "ridge": 
		 return d.edge>1?1:d.edge; //d.edge>1?0.075:d.edge>0.9?0.1:(d.edge);
		break;
	case "similaritygray": 
		// return 1-d.z;
		 return zgray(d.z);
		break;		
	default:  //similarity geo
		return  1;        		
	} 
  }
  function row(row) {
	  
    var cell = d3.select(this).selectAll(".cell")
        .data(row.filter(function(d) {return d.z; })) //this will filter out cells with d.z = 0
    //	.data(row)
        .enter().append("rect")
        .attr("class", "cell")
        .attr("x", function(d) { return x(d.x); })
        .attr("width", x.rangeBand())
        .attr("height", x.rangeBand())
        .style("fill-opacity", function(d) {
        	return cellTransparency(d);
        }) 
        .style("fill", function(d) {
        	return cellColor(d);      	
       	})
        .on("mouseover", mouseover)
        .on("mouseout", mouseout)
    	.on("click", mouseclick);
    
  }
  
  function cmpURI(uri) {
		if (uri.indexOf("/conformer")>=0) {
			return uri.substring(0,uri.indexOf("/conformer"));
		}	
		return uri;
  }
  
  function mouseclick(p) {

	//try {
	    document.getElementById("cmp1list").innerHTML = "";
		var cmp1list = d3.select("#cmp1list");
		if (cmp1list.length>0) cmp1list.splice(0, cmp1list.length-1);
		d3.select("#cmp1").attr("src","#");
		nodes[p.x].uri.forEach(function (item) {
			var val = cmpURI(item.compound.URI);
			//cmp1list.append("option").attr("value",val).text("Activity = " + item.values[oSpace.featureURI]);
			//cmp1list.append("li").text("Activity = " + item.values[oSpace.featureURI]);
			var div = cmp1list.append("li").attr("class","ui-state-default").append("div");
			div.text("Activity = " + item.values[oSpace.featureURI]);
			div.append("img").attr("src",val + "?media=image/png").
			attr("weigth","200").attr("height","200").
			attr("title","Activity = " + item.values[oSpace.featureURI] + "\n" + val).attr("alt",val);
		});
	   // d3.select("#cmp1").attr("src",cmpURI(nodes[p.x].uri[0].compound.URI) + "?media=image/png");
	    
		document.getElementById("cmp2list").innerHTML = "";
		var cmp2list = d3.select("#cmp2list");
		d3.select("#cmp2").attr("src","#");
		nodes[p.y].uri.forEach(function (item) {
			var val = cmpURI(item.compound.URI);
			var div = cmp2list.append("li").attr("class","ui-state-default").append("div");
			div.text("Activity = " + item.values[oSpace.featureURI]);
			div.append("img").attr("src",val + "?media=image/png").
				attr("weigth","200").attr("height","200").
				attr("title","Activity = " + item.values[oSpace.featureURI] + "\n" + val).attr("alt",val);

			//text("Activity = " + item.values[oSpace.featureURI]);
		});
	 //   d3.select("#cmp2").attr("src",cmpURI(nodes[p.y].uri[0].compound.URI) + "?media=image/png");

	    //sasmap
		d3.selectAll(".dot circle")
	    	  .attr("r",function(d) {  return selectedPointsSize(d);  } )
	    	  .attr("fill",function(d) {
	    		  return selectedPointsColor(d);
	    		  } );
	    
	// } catch (err) {
	//	  console.log(err);
	// }
	  /*
	  try {
	    d3.select("#cmp1header").text("["+nodes[p.x].group1 + "," + nodes[p.x].group2+")");
	    d3.select("#cmp2header").text("["+nodes[p.y].group1 + "," + nodes[p.y].group2+")");
	  } catch (err) {
		  
	  } ; 
	  */
	  d3.select("#sh_similarity").text(p.z===undefined?"":p.z.toExponential(3));
      d3.select("#sh_dactivity").text(p.z===undefined?"":dActivity(p).toExponential(3));
	  d3.select("#sh_ratio").text(p.z===undefined?"":((1-p.z)/dActivity(p)).toExponential(3));
	  d3.select("#sh_edge").text(p.edge===undefined?"":p.edge.toExponential(3));
	    /*
	    d3.select("#simheader").text(p.z===undefined?"":p.z.toExponential(3)  + " | " + 
	    			dActivity(p).toExponential(3)+
	    			" | "+
	    			((1-p.z)/dActivity(p)).toExponential(3) +
	    			" | "+
	    			p.edge.toExponential(3)
	    			);
	    */
	    
	    selectedRow = p.y;
	    
	    d3.selectAll(".bar rect").data(matrix[selectedRow])
	    	.style("fill-opacity", function(d) {
	    		return cellTransparency(d);
	    	}) 
	    	.style("fill", function(d) {
	    		return cellColor(d);      	
	    	})
	    	.attr("x", 1)
	    	/*
	    	.attr("transform", function(d) {
	    		console.log(d);
	    		return "translate(" + 0 + "," + d.z + ")"; 
	    	})
	    	*/
	    	;
 
  }
  
  

  function mouseover(p) {
    d3.selectAll(".row text").classed("active", function(d, i) { return i == p.y; });
    d3.selectAll(".column text").classed("active", function(d, i) { return i == p.x; });
  }

  function mouseout() {
    d3.selectAll("text").classed("active", false);
  }

  d3.select("#order").on("change", function() {
    //clearTimeout(timeout);
    order(this.value);
  });

  
  function order(value) {
    x.domain(orders[value]);

    var t = svg.transition().duration(2500);

    t.selectAll(".row")
        .delay(function(d, i) { return x(i) * 1; })
        .attr("transform", function(d, i) { return "translate(0," + x(i) + ")"; })
      .selectAll(".cell")
        .delay(function(d) { return x(d.x) * 1; })
        .attr("x", function(d) { return x(d.x); });

    t.selectAll(".column")
        .delay(function(d, i) { return x(i) * 1; })
        .attr("transform", function(d, i) { return "translate(" + x(i) + ")rotate(-90)"; });
  }

//bar chart
  var barHeight = 30;
  var barY = d3.scale.linear().domain([0, 1]).range([barHeight, 0]);
  var barX = d3.scale.linear().domain([0, matrix.length]).range([0, width]);
  barChart(matrix);
  
 
  function barChart(matrix) {
	
		var margin = {top: 1, right: 0, bottom: 0, left: 0};
	    
		// A formatter for counts.
		var formatCount = d3.format(",.0f");
		
		var xAxis = d3.svg.axis().scale(x).orient("bottom");
		var svg = d3.select("#bar svg")
	    	.attr("width", width + margin.left + margin.right)
	    	.attr("height", barHeight + margin.top + margin.bottom)
	    	.append("g")
	    	.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
		
		  
		var bar = svg.selectAll(".bar svg")
	    .data(function () { return matrix[selectedRow];})
	    .enter().append("g")
	    .attr("class", "bar")
	    .attr("transform", function(d) { return "translate(" + barX(d.x) + "," + barY(1) + ")"; });
	    //.attr("transform", function(d) { return "translate(" + barX(d.x) + "," + barHeight + ")"; });
	
		  
		bar.append("rect")
	    .attr("x", 1)
	    .attr("class","barcell")
	    .attr("width", barX(1)-1)
	   // .attr("height", function(d) { return barHeight - barY(d.z) -1; })
	    .attr("height", function(d) { return barHeight; })
	    .style("fill-opacity", function(d) {
        	return cellTransparency(d);
        }) 
        .style("fill", function(d) {
        	return cellColor(d);      	
       	})
	    ;

		svg.append("g")
		    .attr("class", "x axis")
		    .attr("transform", "translate(0," + barHeight + ")")
		    .call(xAxis);
		

  }
  
//scatter chart
  /**
   * Map mode:
   * bins
   * dActivity - absolute activity difference
   * SALI  
   */  
  sasMap(oSpace.matrix,"similarity","dactivity");

  function sasMap(data,xbins,bins) {
	  
	  selectedRow = -1;
	  
  var margin = {top: 40, right: 40, bottom: 40, left:40},
      width = 300,
      height = 300;

  var binsRange = d3.scale.linear()
  		.domain(d3.range(oSpace.range.length))
  		.range(oSpace.range);
  
  function dActivity(d,mode) {
	  //return bins?binsRange(d[1])-binsRange(d[0]):oSpace.range[d[1]]-oSpace.range[d[0]];
	  switch(mode)
	  {
	  case "binsdiff":
		return Math.abs(d[1]-d[0]);
	    break;
	  case "bins":
			return d[0]<d[1]?d[0]:d[1];
		    break;	    
	  case "SALI":
		if (d[1]==d[0]) return 0;
	    return Math.abs(oSpace.range[d[1]]-oSpace.range[d[0]])/(1-d[2]);
	    break;
	  case "logSALI":
		if (d[1]==d[0]) return 0;
		var value =  Math.log(Math.abs(oSpace.range[d[1]]-oSpace.range[d[0]])/(1-d[2]));
		return isNaN(value)?0:value;
		break;	    
	  case "logdactivity":
		if (d[1]==d[0]) return 0;  
		var value = Math.log(Math.abs(oSpace.range[d[1]]-oSpace.range[d[0]]));
		return isNaN(value)?0:value;
	    break;	
	  case "dactivitylog": 
		if (d[1]==d[0]) return 0;  
		var left = oSpace.range[0];
		//should be positive
		try {
			var value = Math.log(oSpace.range[d[1]]-left)-Math.log(oSpace.range[d[0]]-left);
			return isNaN(value)?0:value;
		} catch (err) {
			return 0;
		}
		break;			  
	  case "activityLeft": 
		    return oSpace.range[d[0]<d[1]?d[0]:d[1]];
			break;	
	  case "activityRight": 
		  return oSpace.range[(d[0]<d[1]?d[0]:d[1])+1];
			break;		
	  case "activityMiddle": 
		    var index = d[0]<d[1]?d[0]:d[1];
	    	return (oSpace.range[index+1]+oSpace.range[index])/2;
			break;				
	  case "similarity": 
			return d[2];
			break;
	  case "edge": 
		  try {
			return oSpace.matrix[d[0]][d[1]].edge;
			break;
		  } catch (err) {return 1;}	
	  default: //dActivity
	    return Math.abs(oSpace.range[d[1]]-oSpace.range[d[0]]);
	  } 
  }

  
  var x = pad(d3.scale.linear()
      .domain(d3.extent(data, function(d) 
    		  { return dActivity(d,xbins); }))
      .range([0,width - margin.left - margin.right]), 40);

  
  var y = pad(d3.scale.linear()
      .domain(d3.extent(data, function(d)	{ 
    	  return dActivity(d,bins); 
    	}))
      .range([0,height - margin.top - margin.bottom]),40);
  
  var xAxis = d3.svg.axis()
      .scale(x)
      .orient("top")
      .tickPadding(8);

  var yAxis = d3.svg.axis()
      .scale(y)
      .orient("left")
      .tickPadding(8);

  var svg = d3.select("#chart svg")
      .attr("width", width)
      .attr("height", height)
      .attr("class", "dot chart")
    .append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

 
  svg.selectAll(".dot")
      .data(data)
    .enter().append("circle")
      .attr("class", "dot")
      .attr("fill",function(d) {
    	  return selectedPointsColor(d);
	   } )
      .attr("cx", function(d) { 
    	  return x(dActivity(d,xbins)); 
      })
    	  // return Math.abs(d[0]-d[1]); })
    	//  .attr("cy", function(d) { return y(oSpace.range[d[1]]-oSpace.range[d[0]]); })
     // .attr("cy", function(d) { return y((1-d[2])/Math.abs(oSpace.range[d[1]]-oSpace.range[d[0]])); })
      .attr("cy", function(d) { return y(dActivity(d,bins)); })
	  .attr("r",function(d) {  return selectedPointsSize(d);  } )
	  ;
 	   
  svg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + y.range()[0] + ")")
      .call(xAxis);

  svg.append("g")
      .attr("class", "y axis")
      .call(yAxis);

	  d3.select("#yaxis").on("change", function() {
		  bins = this.value;
		  y.domain(d3.extent(data, function(d)	{ 
	    	  return dActivity(d,bins); 
	    	}));
		   var t = svg.transition().duration(750);
		   t.select(".y.axis").call(yAxis).attr("fill",bins=="dActivity"?"black":"#344049");
		   t.selectAll(".dot circle")
		    	  .attr("cy", function(d) { return y(dActivity(d,bins)); })
		    	  .attr("r",function(d) {  return selectedPointsSize(d);  } )
		    	  .attr("fill",function(d) {
		    		  return selectedPointsColor(d);
		    		  } );
	  });  
	  
	  d3.select("#xaxis").on("change", function() {
		  xbins = this.value;
		  x.domain(d3.extent(data, function(d)	{ 
	    	  return dActivity(d,xbins); 
	    	}));
		   var t = svg.transition().duration(750);
		   t.select(".x.axis").call(xAxis).attr("fill",xbins=="similarity"?"black":"#344049");
		   t.selectAll(".dot circle")
		    	  .attr("cx", function(d) { return x(dActivity(d,xbins)); })
		    	  .attr("r",function(d) {  return selectedPointsSize(d);  } )
		    	  .attr("fill",function(d) {
		    		  return selectedPointsColor(d);
		    		  } );

		    	  
	  });  	  
	
	  function pad(scale, k) {
		    var range = scale.range();
		    if (range[0] > range[1]) k *= -1;
		    return scale.domain([range[0] - k, range[1] + k].map(scale.invert)).nice();
		  }	

	// convolution(matrix, matrix.length,  matrix.length, 1,1);
  }
  
  
	
  function convolution(matrix, nrows, ncols, row, col) {
	  var sharrx = [[3,0,-3],[10,0,-10],[3,0,-3]];
	  var sharry = [[3,10,3],[0,0,0],[-3,-10,-3]];
	  //Prewitt
	  //var sharrx = [[-1,0,1],[-1,0,1],[-1,0,1]];
      //var sharry = [[1,1,1],[0,0,0],[-1,-1,-1]];
	  //Sobel
	  //var sharrx = [[-1,0,1],[-2,0,2],[-1,0,1]];
      //var sharry = [[1,2,1],[0,0,0],[-1,-2,-1]];   
	  //Kirsch
	 // var sharrx = [[5,5,-3],[5,0,-3],[-3,-3,-3]];
	  //var sharry = [[5,-3,-3],[5,0,-3],[5,-3,-3]];
	  
	  var sum = 0;
	  sharrx.forEach(function (r,i) {
		 r.forEach(function (c,j) {
			 try {
				 sum += matrix[row+i-1][col+j-1].z * c;
				// console.log("sharrx [" + (row+i-1) + "," + (col+j-1) + "] = " +  matrix[row+i-1][col+j-1].z + " *** (" + i + "," + j + ") = " + c);
			 } catch (err) { 
				 //ignore boundaries
				 sum += c;
			 }			 
		 });
	  });
	  var newpixel = sum*sum;
	  sum = 0;
	  sharry.forEach(function (r,i) {
			 r.forEach(function (c,j) {
				 try {
					 sum += matrix[row+i-1][col+j-1].z * c;
					// console.log("sharry [" + (row+i-1) + "," + (col+j-1) + "] = " +  matrix[row+i-1][col+j-1].z + " *** (" + i + "," + j + ") = " + c);
				 } catch (err) { 
					 sum += c;
					 //ignore boundaries
				 }
			 });
		  });	  
	  newpixel = Math.sqrt(newpixel + sum*sum);
	  //console.log("z[" + row + "," + col + "]= " +  matrix[row][col].z + " " + newpixel);
	  return newpixel>1?1:newpixel;
  }

  compounds();
  function compounds() {
	  try {
		  d3.json(oSpace.datasetURI+"?feature_uris[]="+oSpace["featureURI"],function(jsonData) {
			  nodes.forEach(function(node) {
				 node.group1 = null; 
			  });
			  jsonData.dataEntry.forEach(function(item) {
				  var found = false;
				  for(i = 1; i <  oSpace.range.length; i++) {
					   if ((item.values[oSpace.featureURI]>=oSpace.range[i-1]) && (item.values[oSpace.featureURI]<oSpace.range[i])) {
						    nodes[i-1]["uri"].push(item);
						    found = true;
							break;
				  	   }
			  	  }
				 if (!found) nodes[nodes.length-1]["uri"].push(item);
			  });
	 	  });

	  } catch (err) {
		  throw err;
	  }
  }

  return oSpace;	 
}

$(function() {
	$( ".spacegrid" ).selectable();
});
$(function() {
	$( ".spacelist" ).selectable({
		stop: function() {
			var result = $( "#select-result" ).empty();
			$( ".ui-selected", this ).each(function() {
				var index = $( "#selectable li" ).index( this );
				result.append( " #" + ( index + 1 ) );
			});
		}
	});
});
function drawMatrix(oSimilarityMatrix,iMax) {
		
	var margin = {top: 40, right: 0, bottom: 10, left: 120},
	    width = 760,
	    height = 760;
	
	var x = d3.scale.ordinal().rangeBands([0, width]),
	    z = d3.scale.linear().domain([0, 1]).clamp(true),
	    c = d3.scale.category10().domain(10);
	
	var datasetLink = d3.select("#dataset");
	datasetLink.attr("href",oSimilarityMatrix["datasetURI"]);
	datasetLink.attr("title",oSimilarityMatrix["datasetURI"]);
	
	var selectBox = d3.select("#order");
	selectBox.append("option").attr("value","name").text("by Name");
	selectBox.append("option").attr("value","count").text("by Frequency");
	selectBox.append("option").attr("value","group1").text("by Group1");
	selectBox.append("option").attr("value","group2").text("by Group2");
	
	var svg = d3.select("#matrix").append("svg")
	    .attr("width", width + margin.left + margin.right)
	    .attr("height", height + margin.top + margin.bottom)
	    .style("margin-left", -margin.left + "px")
	    .append("g")
	    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	var matrix = [];
	var nodes = [];
	var hashNodes = new Object();
	
	oSimilarityMatrix.links.forEach(function(link) { 
		var index = hashNodes[link.source];
		if (typeof index === 'undefined') {
			var p = link.source.lastIndexOf("/");
			var sName = link.source;
			if (p>0) sName = link.source.substring(p+1);
			hashNodes[link.source] = nodes.length;
			nodes.push({"name":sName,"group1":null,"group2":2,"count":0,"index":nodes.length,"uri":link.source});
		}
		var index = hashNodes[link.target];
		if ((nodes.length<iMax) && (typeof index === 'undefined')) {
			var p = link.target.lastIndexOf("/");
			var sName = link.target;
			if (p>0) sName = link.target.substring(p+1);
			hashNodes[link.target] = nodes.length;
			nodes.push({"name":sName,"group1":null,"group2":3,"count":0,"index":nodes.length,"uri":link.target});
		}	
	});
	n = nodes.length;
	// Compute index per node.
	nodes.forEach(function(node, i) {
	  node.index = i;
	  node.count = 0;
	  matrix[i] = d3.range(n).map(function(j) { return {x: j, y: i, z: 0}; });
	});
	
	oSimilarityMatrix.links.forEach(function(link) { 
		var i = hashNodes[link.source];
		if (typeof i === 'undefined') {}
		else {
			var j = hashNodes[link.target];
			if (typeof j === 'undefined') {}
			else {
			    var value = link.value;
			    matrix[i][j].z += value;
			    matrix[j][i].z += value;
			    matrix[i][i].z += value;
			    matrix[j][j].z += value;
			    nodes[i].count += value;
			    nodes[j].count += value;
			}
		}
	});


  // Precompute the orders.
  var orders = {
    name: d3.range(n).sort(function(a, b) { return d3.ascending(nodes[a].name, nodes[b].name); }),
    count: d3.range(n).sort(function(a, b) { return nodes[b].count - nodes[a].count; }),
    group1: d3.range(n).sort(function(a, b) { return nodes[b].group1 - nodes[a].group1; }),
    group2: d3.range(n).sort(function(a, b) { return nodes[b].group2 - nodes[a].group2; })
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

  function row(row) {
    var cell = d3.select(this).selectAll(".cell")
        .data(row.filter(function(d) { return d.z; }))
      .enter().append("rect")
        .attr("class", "cell")
        .attr("x", function(d) { return x(d.x); })
        .attr("width", x.rangeBand())
        .attr("height", x.rangeBand())
        .style("fill-opacity", function(d) { return z(d.z); })
        .style("fill", function(d) { 
        	return nodes[d.x].group1 == nodes[d.y].group1 ? c(nodes[d.x].group1) : null; 
        	})
        .on("mouseover", mouseover)
        .on("mouseout", mouseout)
    	.on("click", mouseclick);
    
    
  }
  
  function mouseclick(p) {
	    d3.select("#cmp1").attr("src",nodes[p.x].uri + "?media=image/png");
	    d3.select("#cmp2").attr("src",nodes[p.y].uri + "?media=image/png");
	    
	    d3.select("#cmp1header").text(nodes[p.x].name);
	    d3.select("#cmp2header").text(nodes[p.y].name);
	    
	    d3.select("#simheader").text(p.z  + " / " + Math.abs(nodes[p.x].group1-nodes[p.y].group1) + " / " + Math.abs(nodes[p.x].group1-nodes[p.y].group1)/(1-p.z));
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

  /*
  var timeout = setTimeout(function() {
    order("group1");
    d3.select("#order").property("selectedIndex", 2).node().focus();
  }, 5000);
  */
  
  try {
	  d3.json(oSimilarityMatrix.datasetURI,function(jsonData) {
		  nodes.forEach(function(node) {
			 node.group1 = null; 
		  });
		  var minValue = null;
		  var maxValue = null;
		  jsonData.dataEntry.forEach(function(item) { 
			  var index = hashNodes[item.compound.URI];
			  if (typeof index != 'undefined') {
				  for (var key in item.values) {
					  nodes[index].group1 = item.values[key];
					  if ((minValue==null) || (minValue>nodes[index].group1)) minValue= nodes[index].group1;
					  if ((maxValue==null) || (maxValue<nodes[index].group1)) maxValue= nodes[index].group1;
				  }
			  }
		  });
		  orders.group1 = d3.range(n).sort(function(a, b) { return nodes[b].group1 - nodes[a].group1; });

		  c = d3.scale.linear().domain([0,2*Math.abs(maxValue-minValue)]).range(["green","black"]).interpolate(d3.interpolateHsl);
		  c2 = d3.scale.linear().domain([minValue,(maxValue+minValue)/2,maxValue]).range(["blue","white","red"]).interpolate(d3.interpolateHsl);
		  var cells = svg.selectAll(".cell").style("fill",function(d){
			  
			  return (nodes[d.x].group1==null) || (nodes[d.y].group1==null)?null:
				     d.x==d.y?c2(nodes[d.x].group1):
				     c(Math.abs(nodes[d.x].group1-nodes[d.y].group1)/(1-d.z));
		  });
	
		  order("group1");
		  
	  });

  } catch (err) {
	  throw err;
  }
	
  return matrix;
}
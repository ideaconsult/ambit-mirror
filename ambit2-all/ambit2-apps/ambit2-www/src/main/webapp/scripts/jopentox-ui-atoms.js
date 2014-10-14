/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012-2013, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 */
function createImageMap(cmpURI, cmpid, imgselector, mapselector) {
	$.getJSON(cmpURI, function(data) {
	   	  //var items = [];
	 	
	   	  var map = $(mapselector);
	   	  var r = 0; var c = 0;
	   	  if ((data==undefined) || (data["a"]===undefined)) {
	   		  map.text('');
	   		  return;
	   	  }
	   	  
	   	  $.each(data["a"], function(key, val) {
	   		 r += val["w"];
	   		 c++;
	   	  });
	   	  if (c>0) { r = r/c;} else r = 15;
	  	  $.each(data["a"], function(key, val) {
	  	  		var atomno = val["i"];
	        	map.append("<area shape='circle' coords='"+val["x"]+","+val["y"]+","+r+"' href='#' title='"+atomno+"' atomnumber='"+atomno+"' onClick='atomNumber("+atomno+")'>\n");
	      });
	  	  
	     $(imgselector).mapster({
	    	fillColor: '0000ff',
	        fillOpacity: 0.3,
	        mapKey: 'atomnumber',
	        stroke: false,
	        render_highlight: {
	            strokeWidth: 2
	        }
	     })
	     //.mapster('set',true,'1')
	     ;			  
		});
}
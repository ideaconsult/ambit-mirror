function stats(url){
	$.ajax({
	  url: "http://194.141.0.136:8080/ambit2/stats/structures",
	  success: function(data) {
	  	$("#count_struc").html(data);
	  }
	});
};
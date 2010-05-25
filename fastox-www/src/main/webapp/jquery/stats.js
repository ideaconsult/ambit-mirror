function stats(url){
	$.ajax({
	  url: url,
	  success: function(data) {
	  	$("#count_struc").html(data);
	  }
	});
};
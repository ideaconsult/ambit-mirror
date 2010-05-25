function stats(url,target){
	$.ajax({
	  url: url,
	  success: function(data) {
	  	$(target).html(data);
	  }
	});
};
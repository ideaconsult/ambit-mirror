/**
 * Parses HTML help file. Context help entries should be arranged as for http://jqueryui.com/tabs/
 * @param root
 * @param topic
 */
function loadHelp(root,topic) {
	var helpURI =  root + "/chelp/" + (topic===undefined?"":topic) + "?media=text/html";
	$.get(helpURI,function(data) {
		$('#pagehelp').append(data);	
		$('#keys ul li').map(function(el, value) {
			try {
				var key = $(value.innerHTML).attr('href');
			    var content = $(key).html();
			    var title = $(value).text();
			    try {key = key.replace('#','');} catch (err) {}
			    $('a.chelp.'+key)
				 .attr('title','Click for more detailed help')
				 .html('<span id="info-link" class="ui-icon ui-icon-info" style="display: inline-block;"></span>')
				 .click(function() {
					 $('#keytitle').text(title);
					 $('#keycontent').html(content);
			     });
			} catch (err) {}    
		});
	});
}

	$(document).ready( function () {
    	$('div#footer-in').mouseenter( function () {
    		$('div#footer').stop().animate({bottom: '15px'}, 'fast');
    	});
    	$('div#footer-out').mouseleave( function () {
    		$('div#footer').stop().animate({bottom: '-17px'}, 'slow');
    	});
    	    	
    });
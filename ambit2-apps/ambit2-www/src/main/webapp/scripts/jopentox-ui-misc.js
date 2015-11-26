/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012-2013, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 */

$(function() {$( ".datatable" ).dataTable({
	'bJQueryUI': true, 
	'bPaginate': true,
	"sDom": 'T<"clear"><"fg-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>' 
	});
});

$(document)
.ready(
		function() {

			$( ".tabs" ).tabs({cache: true});
		});

$(function() {$( "#selectable" ).selectable();});

$(function() {$( "input:submit, button" ).button();});

function toggleDiv(divId) {$('#'+divId).toggle();}

function hideDiv(divId) {$('#'+divId).hide();}

function changeImage(img,src)  {    
	document.getElementById(img).src=src;
} 
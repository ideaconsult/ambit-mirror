/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 * TODO: Licence.
 */

$(function() {$( ".accordion" ).accordion({autoHeight: false,navigation: true});});

$(function() {$( ".tabs" ).tabs({cache: true});});

$(function() {$( "#selectable" ).selectable();});

$(function() {$( "input:submit, button" ).button();});

function toggleDiv(divId) {$('#'+divId).toggle();}

function hideDiv(divId) {$('#'+divId).hide();}

function changeImage(img,src)  {    
	document.getElementById(img).src=src;
} 
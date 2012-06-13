/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 * TODO: Licence.
 */

function runTask(modelURI, datasetURI, resultDOM, statusDOM, imgRunning, imgReady, imgError) {
	
	var request = new XMLHttpRequest();
	var reqBody = 'dataset_uri=' + datasetURI;
	
	// 'true' is for async
	request.open('POST', modelURI, true);
	
	request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
	request.setRequestHeader('Accept', 'text/uri-list');
	
	request.onreadystatechange = function() {
		if (request.readyState != 4) { return false; }
		switch (request.status) {
			case 200:
				document.getElementById(resultDOM).href = request.responseText;
				document.getElementById(statusDOM).src = imgReady;
				document.getElementById(resultDOM).style.display = 'inline';
				document.getElementById(statusDOM).style.display = 'inline';
				break;
			case 201: // fall down
			case 202:
				checkTask(request.responseText, resultDOM, statusDOM, imgReady, imgError);
				document.getElementById(statusDOM).src = imgRunning;
				document.getElementById(statusDOM).style.display = 'inline';
				break;
			default:
				document.getElementById(resultDOM).innerHTML = request.status + ' ' + request.statusText;
				document.getElementById(statusDOM).src = imgError;
				document.getElementById(resultDOM).style.display = 'inline';
				document.getElementById(statusDOM).style.display = 'inline';
				break;
			}
	};
	
	request.send(reqBody);
}

function checkTask(taskURI, resultDOM, statusDOM, imgReady, imgError) {
	
	var request = new XMLHttpRequest();
	
	// 'true' is for async
	request.open('GET', taskURI, true);
	
	request.setRequestHeader('Accept', 'text/uri-list');
	
	request.onreadystatechange = function() {
		if (request.readyState != 4) { return false; }
		switch (request.status) {
			case 200:
				document.getElementById(resultDOM).innerHTML = '<span title=\"' + request.status + ' ' + request.statusText + '\">Ready. Results available.</span>';
				document.getElementById(resultDOM).href = request.responseText;
				document.getElementById(statusDOM).src = imgReady;
				document.getElementById(resultDOM).style.display = 'inline';
				document.getElementById(statusDOM).style.display = 'inline';
				break;
			case 201:
				taskURI = request.responseText; // and then fall down
			case 202:
				document.getElementById(resultDOM).innerHTML = '<span title=\'' + request.status + ' ' + request.statusText + '\'>Waiting ...</span>';
				document.getElementById(resultDOM).href = request.responseText;
				var taskTimer = window.setTimeout(function() {
					checkTask(taskURI, resultDOM, statusDOM, imgReady, imgError);
				}, 1000);
				break;
			default:
				document.getElementById(resultDOM).innerHTML = '<span title=\'' + request.status + ' ' + request.statusText + '\'>Error</span>';
				document.getElementById(statusDOM).src = imgError;
				document.getElementById(resultDOM).style.display = 'inline';
				document.getElementById(statusDOM).style.display = 'inline';
				break;
		}
	};
	
	request.send(null);
}
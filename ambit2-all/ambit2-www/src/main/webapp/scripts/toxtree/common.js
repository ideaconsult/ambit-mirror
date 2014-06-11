function initToxtree(sRoot){
	// some behavioural setup
	// now attach the handler for clicking on the line which opens / hides it.
	var showhideInfo = function(row, force){
		var info = row.getElementsByClassName('info')[0];
		if (row.classList.contains('visible') || !force && force !== undefined){
			row.classList.remove('visible');
			info.classList.add('hidden');
		}
		else{
			row.classList.add('visible');
			info.classList.remove('hidden');
		}
	}

	var serverEl = document.getElementById('connection-baseuri');
	var statusEl = document.getElementById('connection-status');
	var errorEl = document.getElementById('connection-error');
	var fadeTimeout = null
	var setResult = function(status, error){
		statusEl.src = sRoot + "/images/toxtree/" + status + ".png";
		statusEl.title = localMessage[status];

		if (!error)
			error = '';
			
		errorEl.classList.remove('fading');
		errorEl.innerHTML = error;
		if (fadeTimeout)
			clearTimeout(fadeTimeout);
		fadeTimeout = setTimeout(function() { errorEl.classList.add('fading'); }, 5000);
	};

	// initialization of ToxMan - passing all necessary parameters and configuration.
	ToxMan.init({ 
		prefix: "toxtree",
//		jsonp: true,
		server : sRoot,
		forceCreate: true,
		queryParams: {"language": "en"},
		onmodeladd: function(row, idx){
			row.getElementsByClassName('show-hide')[0].onclick = function(e) { showhideInfo(this.parentNode); };
			
			// then put good id to auto checkboxes so that runAutos() can recognizes
			var auto = row.getElementsByClassName('auto')[0].id = ToxMan.prefix + "-auto-" + idx;
		},
		onrun: function (row, idx, e){
			e.stopPropagation();
		},
		onpredicted: function(row, idx){
			showhideInfo(row, true);
		},
		onclear: function(row, idx){
			showhideInfo(row, false);
		},
		onconnect : function(service){
			statusEl.src = sRoot + "/images/waiting_small.gif";
			statusEl.title = localMessage.waiting;
		},
		onsuccess: function(code, mess){
			setResult('ok', '');
		},
		onerror: function(code, mess){
			setResult('error', '(' + code + '): ' + mess);
		}
	});
	
	serverEl.innerHTML = ToxMan.server;
	localMessage = languages[ToxMan.queryParams.language !== undefined ? ToxMan.queryParams.language : 'en'];
	ToxMan.listModels();
	
	// now attach the query button
	var needle = document.getElementById('query-needle');
	var query = document.getElementById('query-button');

	if (query && needle){
		needle.onchange = query.onclick = function(e){
			if (needle.value.length > 0){
				ToxMan.query(needle.value);
			}
		}
	}
	
	// finally - check for search parameter in the URL
	if (ToxMan.queryParams.search !== undefined){
		ToxMan.query(ToxMan.queryParams.search);
		needle.value = ToxMan.queryParams.search;
	}
}

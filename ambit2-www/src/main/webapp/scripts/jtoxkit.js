var jToxStudy = {
  rootElement: null,
  
  getFormatted: function (data, type, format) {
    var value = null;
    if (typeof format === 'function' )
      value = format.call(this, data, type);
    else if (typeof format === 'string' || typeof format === 'number')
      value = data[format];
    else
      value = data[0];
    
    return value;
  },
  
  renderMulti: function (data, type, full, format) {
    var self = this;
    var dlen = data.length;
    if (dlen < 2)
      return self.getFormatted(data[0], type, format);

    var height = 100 / dlen;
    var df = '<table>';
    for (var i = 0, dlen = data.length; i < dlen; ++i) {
      df += '<tr class="' + (i % 2 == 0 ? 'even' : 'odd') + '"><td style="height: ' + height + '%">' + self.getFormatted(data[i], type, format) + '</td></tr>';
    }
    
    df += '</table>';
    return df;
  },
  
  formatResult: function (data, type) {
    var out = "";
    data = data.result;
    if (data.loValue !== undefined && data.upValue !== undefined) {
      out += (data.loQualifier == ">=") ? "[" : "(";
      out += data.loValue + ", " + data.upValue;
      out += (data.upQualifier == "<=") ? "]" : ") ";
    }
    else // either of them is non-undefined
    {
      var fnFormat = function (q, v) {
        return ((q !== undefined) ? q : "=") + " " + v;
      };
      
      out += (data.loValue !== undefined) ? fnFormat(data.loQualifier, data.loValue) : fnFormat(data.upQualifier, data.upValue);
    }
    
    if (!!data.unit)
      out += data.unit;
    return out.replace(/ /g, "&nbsp;");
  },
  
  createCategory: function(tab, category, name) {
    var self = this;

    var theCat = $('.' + category + '.jtox-study', tab)[0];
    if (!theCat) {
      theCat = jToxKit.getTemplate('#jtox-study');
      tab.appendChild(theCat);
      $(theCat).addClass(category);
      
      // install the click handler for fold / unfold
      var titleEl = $('.jtox-study-title', theCat);
      $(titleEl).click(function() {
        $(theCat).toggleClass('folded');
      });
    }
    
    jToxKit.fillTree(titleEl[0], { title: "" + name + " (0)"});
    return theCat;
  },

  updateCount: function(str, count) {
    if (count === undefined)
      count = 0;
    return str.replace(/(.+)\s\(([0-9]+)\)/, "$1 (" + count + ")");
  },
  
  
  ensureTable: function (tab, study) {
    var self = this;

    var theTable = $('.' + study.protocol.category + ' .jtox-study-table')[0];
    if (!$(theTable).hasClass('dataTable')) {

      var colDefs = [
        { "sClass": "center", "sWidth": "10%", "mData": "protocol.endpoint" } // The name (endpoint)
      ];
      
      // start filling it
      var headerRow = theTable.getElementsByClassName('jtox-header')[0];
      var before = headerRow.firstElementChild;
      var parCount = 0;

      // this function takes care to add as columns all elements from given array
      var putAGroup = function(group, fProcess) {
        var count = 0;
        for (var p in group) {
          var val = fProcess(p);
          if (val === undefined)
            continue;
          colDefs.push(val);
          
          var th = document.createElement('th');
          th.innerHTML = p;
          headerRow.insertBefore(th, before);
          before = th.nextElementSibling;
          count++;
        }
        return count;
      }

      // use it to put parameters...
      parCount += putAGroup(study.parameters, function(p) {
        return study.effects[0].conditions[p] === undefined  && study.effects[0].conditions[p + " unit"] === undefined ? 
        { 
          "sClass" : "center middle", 
          "mData" : "parameters." + p, 
          "mRender" : function (data, type, full) { return data !== undefined ? (data + ((full[p + " unit"] !== undefined) ? "&nbsp;" + full[p + " unit"] : "")) : "-"; }
        } : undefined;
      });
      // .. and conditions
      parCount += putAGroup(study.effects[0].conditions, function(c){
        return study.effects[0].conditions[c + " unit"] === undefined ?
        { "sClass" : "center middle jtox-multi", 
          "mData" : "effects", 
          "mRender" : function(data, type, full) { return self.renderMulti(data, type, full, function(data, type) { return data.conditions[c] !== undefined ? (data.conditions[c] + (data.conditions[c + " unit"] !== undefined ? "&nbsp;" + data.conditions[c + " unit"] : "")) : "-"; } )} 
        } : undefined;
      });
      
      // now fix the colspan of 'Conditions' preheader cell
      var preheaderCell = theTable.getElementsByClassName('jtox-preheader')[0].firstElementChild.nextElementSibling;
      if (parCount > 0)
        preheaderCell.setAttribute('colspan', parCount);
      else
        preheaderCell.parentNode.removeChild(preheaderCell);
      
      // add also the "default" effects columns
      colDefs.push(
        { "sClass": "center middle jtox-multi", "sWidth": "5%", "mData": "effects", "mRender": function (data, type, full) { return self.renderMulti(data, type, full, "endpoint");  } },   // Effects columns
        { "sClass": "center middle jtox-multi", "mData" : "effects", "mRender": function (data, type, full) { return self.renderMulti(data, type, full, self.formatResult) } }
      );

      // jump over those two - they are already in the DOM      
      before = before.nextElementSibling.nextElementSibling;
      
      // now is time to put interpretation columns..
      parCount = putAGroup(study.interpretation, function(i){
        return { "sClass" : "center middle jtox-multi", "mData" : "interpretation." + i, "sDefaultContent": "-"};
      });

      // jump over Effects preheader-column      
      preheaderCell = preheaderCell.nextElementSibling.nextElementSibling;
      if (parCount > 0)
        preheaderCell.setAttribute('colspan', parCount);
      else
        preheaderCell.parentNode.removeChild(preheaderCell);
      
      // finally put the protocol entries
      colDefs.push(
        { "sClass": "center", "sWidth": "10%", "mData": "protocol.guidance", "mRender" : "[,]", "sDefaultContent": "?"  },    // Protocol columns
        { "sClass": "center", "sWidth": "5%", "mData": "owner.company.name", "mRender" : function(data, type, full) { return type != "display" ? '' + data : '<div class="shortened">' + data + '</div>' }  }, 
        { "sClass": "center", "sWidth": "5%", "mData": "uuid", "bSearchable": false, "mRender" : function(data, type, full) { return type != "display" ? '' + data : '<div class="shortened">' + data + '</div>' }  }
      );
      
      // READYY! Go and prepare THE table.
      $(theTable).dataTable( {
        "bPaginate": true,
        "bProcessing": true,
        "bLengthChange": false,
//        "sPaginationType": "full_numbers",
        "sDom" : "rt<Fip>",
        "aoColumns": colDefs,
        "fnInfoCallback": function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
          var el = $('.jtox-study-title .data-field', $(this).parentsUntil('.jtox-study').parent())[0];
          el.innerHTML = self.updateCount(el.innerHTML, iTotal);
          return sPre;
        }
      });
    }
    else
      $(theTable).dataTable().fnClearTable();
      
    return theTable;
  },
  
  processSummary: function (summary) {
    var self = this;
    var typeSummary = [];
    
    // first - clear all existing tabs
    var catList = self.rootElement.getElementsByClassName('jtox-study');
    while(catList.length > 0) {
      catList[0].parentNode.removeChild(catList[0]);
    }
    
    // create the groups on the corresponding tabs
    for (var s in summary) {
      var sum = summary[s];
      var top = sum.topcategory.title;
      if (!top)
        continue;
      var top = top.replace(/ /g, "_");
      var tab = $('.jtox-study-tab.' + top, self.rootElement)[0];
      
      var catname = sum.category.title;
      if (!catname) {
        typeSummary[top] = sum.count;
      }
      else {
        var cat = self.createCategory(tab, catname, catname);
        $(cat).data('jtox-uri', sum.category.uri);
      }
    }
    
    // update the number in the tabs...
    $('ul li a', self.rootElement).each(function (i){
      var data = $(this).data('type');
      if (!!data){
        var cnt = typeSummary[data];
        var el = $(this)[0];
        el.innerHTML = (self.updateCount(el.innerHTML, cnt));
      }
    });
    
    // now install the filter box handler. It delays the query a bit and then spaws is to all tables in the tab.
    var filterTimeout = null;
    var fFilter = function (ev) {
      if (!!filterTimeout)
        clearTimeout(filterTimeout);
  
      var field = ev.currentTarget;
      var tab = $(this).parentsUntil('.jtox-study-tab')[0].parentNode;
      
      filterTimeout = setTimeout(function() {
        var tabList = tab.getElementsByClassName('jtox-study-table');
        for (var t = 0, tlen = tabList.length; t < tlen; ++t) {
          $(tabList[t]).dataTable().fnFilter(field.value);
        }
      }, 300);
    };
    
    var tabList = document.getElementsByClassName('jtox-study-tab');
    for (var t = 0, tlen = tabList.length;t < tlen; t++){
      var filterEl = tabList[t].getElementsByClassName('jtox-study-filter')[0].onkeydown = fFilter;
    }
  },
  
  processStudies: function (tab, study, map) {
    var self = this;
    var cats = [];
    
    // first swipe to map them to different categories...
    for (var i = 0, slen = study.length; i < slen; ++i) {
      var ones = study[i];
      if (map) {
        if (cats[ones.protocol.category] === undefined) {
          cats[ones.protocol.category] = [ones];
        }
        else {
          cats[ones.protocol.category].push(ones);
        }
      }
    }

    // add this one, if we're not remapping. map == false assumes that all passed studies will be from
    // one category.    
    if (!map)
      cats[study[0].protocol.category] = study;
    
    // now iterate within all categories (if many) and initialize the tables
    for (var c in cats) {
      var onec = cats[c];
      if ($('.' + c + '.jtox-study', tab).length < 1)
        continue;

      var theTable = self.ensureTable(tab, onec[0]);
      $(theTable).dataTable().fnAddData(onec);
    }
    
    // we need to fix columns height's because of multi-cells
    $('#' + theTable.id + ' .jtox-multi').each(function(index){
      this.style.height = '' + this.offsetHeight + 'px';
    });
  },
  
  querySummary: function(substanceURI) {
    var self = this;
    var subId = substanceURI.replace(/.+\/(.+)/, "$1");
    jToxKit.fillTree($('#jtox-composition .data-field', self.rootElement)[0], {substanceID: subId});
    
    jToxKit.call(substanceURI + "/studysummary", function(summary) {
      if (!!summary && !!summary.facet)
        self.processSummary(summary.facet);
    });
  },
  
  init: function(root, settings) {
    var self = this;
    this.rootElement = root;

    // inject the template, if not already there
    if (self.htmlTemplate !== undefined) {
      var temp = document.createElement('div');
      root.appendChild(temp);
      temp.outerHTML = self.htmlTemplate;
    }

    var tree = jToxKit.getTemplate('#jtox-studies');
    root.appendChild(tree);
    var loadPanel = function(panel){
      if (panel){
        $('.jtox-study.unloaded', panel).each(function(i){
          var table = this;
          jToxKit.call($(table).data('jtox-uri'), function(study){
            $(table).removeClass('unloaded folded');
            $(table).addClass('loaded');
            self.processStudies(panel, study.study, true); // TODO: must be changed to 'false', when the real summary is supplied
            $('.dataTable', table).dataTable().fnAdjustColumnSizing();
          });  
        });
      }
    };
    $(tree).tabs({
      "select" : function(event, ui) {
        loadPanel(ui.panel);
      },
      "beforeActivate" : function(event, ui) {
        if (ui.newPanel)
          loadPanel(ui.newPanel[0]);
      }
    });
    
    if (settings['substanceUri'] !== undefined){
      self.querySummary(settings['substanceUri']);
    }
  }
};
window.jToxKit = {
	templateRoot: null,
	server: null,

	/* A single place to hold all necessary queries. Parameters are marked with <XX> and formatString() (common.js) is used
	to prepare the actual URLs
	*/
	queries: {
		taskPoll: "/task/<1>",
	},
	
	templates: { },        // html2js routine will fill up this variable

	/* SETTINGS. The following parametes can be passed in settings object to jToxKit.init(), or as data-XXX - with the same names. Values set here are the defaults.
	*/
	settings: {
  	jsonp: false,					// whether to use JSONP approach, instead of JSON.
  	server: null,					// the server actually used for connecting. Part of settings. If not set - attempts to get 'server' parameter of the query, if not - get's current server.
  	timeout: 5000,				// the timeout an call to the server should be wait before the attempt is considered error.
  	pollDelay: 200,				// after how many milliseconds a new attempt should be made during task polling.
  },
	
	// some handler functions that can be configured from outside with the settings parameter.
	onconnect: function(s){ },		    // function (service): called when a server request is started - for proper visualization. Part of settings.
	onsuccess: function(c, m) { },		// function (code, mess): called on server request successful return. It is called along with the normal processing. Part of settings.
	onerror: function (c, m) { },			// function (code, mess): called on server reques error. Part of settings.
  
  mergeSettings: function (settings) {
    if (settings !== undefined)
    	for (var s in settings)
      	this.settings[s] = settings[s];
  },
  
	init: function(settings) {
  	var self = this;
  	
  	self.initTemplates();

    // scan the query parameter for settings
		var url = this.parseURL(document.location);
		var queryParams = url.params;
		queryParams.host = url.host;
  	
  	// now scan all insertion divs
  	if (!settings) {
    	$('.jtox-toolkit').each(function(i) {
      	var dataParams = $(this).data();
      	if (!dataParams.manualInit || settings !== undefined){
          // this order determines the priority..
          self.mergeSettings(dataParams);
          self.mergeSettings(queryParams);
          self.mergeSettings(settings);
          
        	if (self.settings.kit == "study")
        	  jToxStudy.init(this, self.settings);
        }
    	});
  	}

  	self.initConnection();
	},
	
	initTemplates: function() {
	  var self = this;

    var root = document.getElementsByClassName('jtox-template')[0];
    if (root === undefined) {
  	  var html = '';
    	for (var t in self.templates) {
      	html += self.templates[t];
    	}
    	
    	root = document.createElement('div');
    	root.className = 'jtox-template';
    	root.innerHTML = html;
    	document.body.appendChild(root);
    }
    
  	self.templateRoot = root;
	},
	
	getTemplate: function(selector) {
  	var el = $(selector, this.templateRoot)[0];
  	if (!!el){
    	var el = $(selector, this.templateRoot)[0].cloneNode(true);
      el.removeAttribute('id');
    }
    return el;
	},

	/* Function setObjValue(obj, value)Set a given to the given element (obj) in the most appropriate way - be it property - the necessary one, or innetHTML
  */
  setObjValue: function (obj, value){
  	if ((value === undefined || value === null) && obj.dataset.default !== undefined)
  		value = obj.dataset.default;
  
    if (obj.nodeName == "INPUT" || obj.nodeName == "SELECT")
      obj.value = value;
    else if (obj.nodeName == "IMG")
      obj.src = value;
    else if (obj.nodeName == "BUTTON")
  		obj.dataset.value = value;
    else
      obj.innerHTML = value;
  },
  
  // given a root DOM element and an JSON object it fills all (sub)element of the tree
  // which has class 'data-field' and their name corresponds to a property in json object.
  // If prefix is given AND json has id property - the root's id set to to prefix + json.id
  fillTree: function(root, json, prefix, filter) {
    var self = this;
  	if (!filter)
  		filter = 'data-field';
    var dataList = root.getElementsByClassName(filter);
    var dataCnt = dataList.length;
  	
  	var processFn = function(el, json){
  	  var field = $(el).data('field');
      if (json[field] !== undefined) {
        var value = json[field];
        var format = $(el).data('format');
        if ( !!format && (typeof window[format] == 'function') ) {
          value = window[format](value, json);
        }
        self.setObjValue(el, value);
      }
  	}
  	
  	if (root.classList.contains(filter))
  		processFn(root, json);
  
    for (var i = 0; i < dataCnt; ++i)
    	processFn(dataList[i], json);
  
    if (prefix && json.id !== undefined) {
      root.id = prefix + json.id;
    }
  },
  

	/* formats a string, replacing [<number>] in it with the corresponding value in the arguments
  */
  formatString: function(format) {
    for (var i = 1;i < arguments.length; ++i) {
      format = format.replace('<' + i + '>', arguments[i]);
    }
    return format;
  },
  
  parseURL: function(url) {
    var a =  document.createElement('a');
    a.href = url;
    return {
      source: url,
      protocol: a.protocol.replace(':',''),
      host: a.hostname,
      port: a.port,
      query: a.search,
      params: (function(){
        var ret = {},
          seg = a.search.replace(/^\?/,'').split('&'),
          len = seg.length, i = 0, s;
        for (;i<len;i++) {
          if (!seg[i]) { continue; }
          s = seg[i].split('=');
          ret[s[0]] = (s.length>1)?decodeURIComponent(s[1].replace(/\+/g,  " ")):'';
        }
        return ret;
      })(),
      file: (a.pathname.match(/\/([^\/?#]+)$/i) || [,''])[1],
      hash: a.hash.replace('#',''),
      path: a.pathname.replace(/^([^\/])/,'/$1'),
      relative: (a.href.match(/tps?:\/\/[^\/]+(.+)/) || [,''])[1],
      segments: a.pathname.replace(/^\//,'').split('/')
    };
  },
	
	/* Poll a given taskId and calls the callback when a result from the server comes - 
	be it "running", "completed" or "error" - the callback is always called.
	*/
	pollTask : function(task, callback) {
		var self = this;
		if (task === undefined || task.task === undefined || task.task.length < 1){
			self.onerror('-1', localMessage.taskFailed);
			return;
		}
		task = task.task[0];
		if (task.completed == -1){ // i.e. - running
			setTimeout(function(){
				self.call(task.result, function(newTask){
					self.pollTask(newTask, callback);
				});
			}, self.pollDelay);
		}
		else if (!task.error){
			callback(task.result);
		}
		else { // error
			self.onerror('-1', task.error);
		}
	},
	
	/* Initialized the necessary connection data. Same settings as in ToxMan.init() are passed.
	*/
	initConnection: function(){
	  var settings = this.settings;
		if (!settings.server) {
		  settings.server = settings.host;
		}
		  
		this.server = settings.server;
					
    if (settings.onerror !== undefined)
		  this.onerror = settings.onerror;
		if (settings.onsuccess !== undefined)
		  this.onsuccess = settings.onsuccess;
		if (settings.onconnect !== undefined)
		  this.onconnect = settings.onconnect;
	},
	
	/* Makes a server call with the provided method. If none is given - the internally stored one is used
	*/
	call: function (service, callback, adata){
		var self = this;
		self.onconnect(service);
		var method = 'GET';
		var accType = self.jsonp ? "application/x-javascript" : "application/json";	
		
		if (adata !== undefined){
			method = 'POST';
			if (typeof adata == "boolean")
				adata = {};
		}
		else
			adata = { };

		// on some queries, like tasks, we DO have server at the beginning
		if (service.indexOf("http") != 0)	
			service = self.server + service;
		// now make the actual call
		$.ajax(service, {
			dataType: self.jsonp ? 'jsonp' : 'json',
			headers: { Accept: accType },
			crossDomain: true,
			timeout: self.timeout,
			type: method,
			data: adata,
			jsonp: self.jsonp ? 'callback' : false,
			error: function(jhr, status, error){
				self.onerror(status, error);
				callback(null);
			},
			success: function(data, status, jhr){
				self.onsuccess(status, jhr.statusText);
				callback(data);
			}
		});
	}
};

$(document).ready(function(){
  jToxKit.init();
});
jToxKit.templates['all-studies']  = 
"	  <div id=\"jtox-studies\">" +
"	    <ul>" +
"	      <li><a href=\"#jtox-composition\">Composition</a></li>" +
"	      <li><a href=\"#jtox-pchem\" data-type=\"P-CHEM\">P-Chem (0)</a></li>" +
"	      <li><a href=\"#jtox-envfate\" data-type=\"ENV_FATE\">Env Fate (0)</a></li>" +
"	      <li><a href=\"#jtox-ecotox\" data-type=\"ECOTOX\">Eco Tox (0)</a></li>" +
"	      <li><a href=\"#jtox-tox\" data-type=\"TOX\">Tox (0)</a></li>" +
"	    </ul>" +
"	    <div id=\"jtox-composition\">" +
"	      <p>Substance: <span class=\"data-field\" data-field=\"substanceID\"></span></p>" +
"	    </div>" +
"	    <div id=\"jtox-pchem\" class=\"jtox-study-tab P-CHEM\">" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\"></input></p>" +
"      </div>" +
"	    <div id=\"jtox-envfate\" class=\"jtox-study-tab ENV_FATE\">" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\"></input></p>" +
"	    </div>" +
"	    <div id=\"jtox-ecotox\" class=\"jtox-study-tab ECOTOX\">" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\"></input></p>" +
"	    </div>" +
"	    <div id=\"jtox-tox\" class=\"jtox-study-tab TOX\">" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\"></input></p>" +
"	    </div>" +
"	  </div>" +
""; // end of #jtox-studies 

jToxKit.templates['one-study']  = 
"    <div id=\"jtox-study\" class=\"jtox-study jtox-foldable folded unloaded\">" +
"      <div class=\"jtox-study-title\"><p class=\"data-field\" data-field=\"title\">? (0)</p></div>" +
"      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"jtox-study-table\" width=\"100%\">" +
"        <thead>" +
"          <tr class=\"jtox-preheader\">" +
"            <th rowspan=\"2\">Name</th>" +
"            <th>Conditions</th>" +
"            <th colspan=\"2\">Effects</th>" +
"            <th>Interpretation</th>" +
"            <th colspan=\"3\">Protocol</th>" +
"          </tr>" +
"          <tr class=\"jtox-header\">" +
"            <th>Endpoint</th>" +
"            <th>Result</th>" +
"            <th>Guidance</th>" +
"            <th>Owner</th>" +
"            <th>UUID</th>" +
"          </tr>" +
"        </thead>" +
"        <tbody></tbody>" +
"      </table>" +
"    </div>" +
""; // end of #jtox-study 


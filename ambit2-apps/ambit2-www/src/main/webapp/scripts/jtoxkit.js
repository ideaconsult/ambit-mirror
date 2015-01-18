var ccLib = {
  extendArray: function (base, arr) {
    // initialize, if needed
    if (base === undefined || base == null)
      base = [];
    else if (!jQuery.isArray(base))
      base = [base];
    
    // now proceed with extending
    if (arr !== undefined && arr !== null){
      for (var i = 0, al = arr.length; i < al; ++i){
        var v = arr[i];
        if (base.indexOf(v) < 0 && v !== undefined && v != null)
          base.push(v);
      }
    }
    return base;
  },
  
  extendNew: function (base) {
    var deep = false;
    var arr = null;
    if (typeof base == 'boolean') {
      deep = base;
      base = arguments[1];
      arr = Array.prototype.slice.call(arguments, 1);
    }
    else
      arr = arguments;
    
    for (var i = 1, al = arr.length;i < al; ++i) {
      var obj = arr[i];
      if (typeof obj != 'object')
        continue;
      for (var key in obj) {
        if (!base.hasOwnProperty(key)) 
          base[key] = (typeof obj[key] == 'object') ? window.jQuery.extend({}, obj[key]) : obj[key];
        else if (deep && typeof base[key] == 'object' && typeof obj[key] == 'object')
          this.extendNew(true, base[key], obj[key]);
      }
    }
    return base;
  },
  
  joinDeep: function (data, field, jn) {
    var arr = [];
    for (var i = 0, dl = data.length;i < dl; ++i)
      arr.push(this.getJsonValue(data[i], field));
    return arr.join(jn);
  },
  
  fireCallback: function (callback, self) {
    if (!jQuery.isArray(callback))
      callback = [callback];
      
    var ret = true;
    for (var i = 0, cl = callback.length; i < cl; ++i) {
      var callone = callback[i];
      if (typeof callone != 'function')
        callone = window[callone];
      ret = (typeof callone == 'function') ? (callone.apply((self !== undefined && self != null) ? self : document, Array.prototype.slice.call(arguments, 2))) : undefined;
    }
    return ret;
  },
  
  /* Function setObjValue(obj, value)Set a given to the given element (obj) in the most appropriate way - be it property - the necessary one, or innetHTML
  */
  setObjValue: function (obj, value){
  	if ((value === undefined || value === null) && jQuery(obj).data('default') !== undefined)
  		value = jQuery(obj).data('default');
  
    if (obj.nodeName == "INPUT" && obj.type == 'checkbox')
      obj.checked = !!value;
    else if (obj.nodeName == "INPUT" || obj.nodeName == "SELECT")
      obj.value = value;
    else if (obj.nodeName == "IMG")
      obj.src = value;
    else if (obj.nodeName == "BUTTON")
  		jQuery(obj).data('value', value);
    else
      obj.innerHTML = value;      
  },

  getObjValue: function (obj){
    if (obj.nodeName == "INPUT" && obj.type == 'checkbox')
      return obj.checked;
    else if (obj.nodeName == "INPUT" || obj.nodeName == "SELECT")
      return obj.value;
    else if (obj.nodeName == "IMG")
      return obj.src;
    else if (obj.nodeName == "BUTTON")
  		return jQuery(obj).data('value');
    else
      return obj.innerHTML;
  },

  isNull: function(obj) {
    return obj === undefined || obj == null;
  },
  
  isEmpty: function(obj) {
    var empty = true;
    if (obj !== undefined || obj != null) {
      if (typeof obj == 'object') {
        for (var i in obj) {
          if (obj.hasOwnProperty(i)) {
            empty = false;
            break;
          }
        } 
      }
      else if (typeof obj == 'string')
        empty = obj.trim().length == 0;
      else if (jQuery.isArray(obj))
        empty = obj.length == 0;
      else
        empty = false;
    }
    return empty;
  },
  
  enumObject: function(obj, fn, idx, level) {
    if (level == null)
      level = 0;
    if (typeof obj != "object")
      fn(obj, idx, level);
    else if (jQuery.isArray(obj)) // array
      for (var i = 0, l = obj.length; i < l; ++i)
        this.enumObject(obj[i], fn, i, level + 1);
    else // normal object
      for (var i in obj)
        this.enumObject(obj[i], fn, i, level + 1);
  },
  
  setJsonValue: function (json, field, val) {
    if (field != null){
      try {
        eval("json." + field + " = val");
      }
      catch(e){
        var arr = field.split('.');
        for (var i = 0, al = arr.length; i < al - 1; ++i)
          json = json[arr[i]] = {};

        json[arr[i]] = val;
      }
    }  
  },
  
  getJsonValue: function (json, field){
    var value = json[field];
    if (value === undefined && field != null) {
      try {
        eval("value = json." + field);
      } 
      catch(e){
        ;
      }
    }
    return value;
  },
  
  // given a root DOM element and an JSON object it fills all (sub)element of the tree
  // which has class 'data-field' and their name corresponds to a property in json object.
  // If prefix is given AND json has id property - the root's id set to to prefix + json.id
  fillTree: function (root, json, prefix, filter) {
    var self = this;
    if (json == null)
      return;
  	if (!filter)
  		filter = 'data-field';
  	
  	var processFn = function(el, json){
  	  var value = self.getJsonValue(json, jQuery(el).data('field'));
      if (value !== undefined) {
        var format = jQuery(el).data('format');
        if ( !!format && (typeof window[format] == 'function') ) {
          value = window[format](value, json);
        }
        if (jQuery(el).hasClass('attribute'))
          jQuery(el).attr(jQuery(el).data('attribute'), value);
        else // the 'normal' value
          self.setObjValue(el, value);
      }
  	}
	
  	if (jQuery(root).hasClass(filter))
  		processFn(root, json);
  
    jQuery('.' + filter, root).each(function (i) { processFn(jQuery(this)[0], json); } );
  
    if (prefix && json.id !== undefined) {
      root.id = prefix + json.id;
    }
  },
  
  populateData: function (root, template, data, enumFn) {
    if (data == null || typeof data != 'object')
      return;
      
    var temp = $(template)[0];
    var oldDisp = root.style.display;
    root.style.display = 'none';
    for (var i = 0, dl = data.length; i < dl; ++i) {
      var el = temp.cloneNode(true);
      el.removeAttribute('id');
      if (this.fireCallback(enumFn, el, data[i]) === false)
        continue;
    
      root.appendChild(el);
      this.fillTree(el, data[i]);
    }
    
    root.style.display = oldDisp;
  },
  
  // Prepare a form so that non-empty fields are checked before submit and accumuater fields
  // are accumulated. Call it after you've set submit behavior, etc.
  prepareForm: function (form) {
  	var self = this;
	  var $ = window.jQuery;

		// first - attach the accumulators handler.	  
	  $('.accumulate', form).on('change', function (e) {
		  var target = $(this).data('accumulate');
		  if (!!target) {
		  	target = this.form[target];
		  	var val = target.value.replace(new RegExp('(' + this.value + ')'), '');
		  	if (this.checked)
			  	val += ',' + this.value;

			  target.value = val.replace(/,,/g, ',').replace(/^,/g, '').replace(/,$/g, ''); // change double commas with one, and replaces commas at the beginning and at the end
		  }
			return false;
	  });
  },
  
  packData: function (data) {
    var out = {};
    for (var i in data) {
      if (!data.hasOwnProperty(i))
        continue;
      var o = data[i];
      out[o.name] = o.value;
    }
      
    return out;
  },
  
  serializeForm: function (form) {
    return this.packData(window.jQuery(form).serializeArray());
  },
	 
  flexSize: function (root) {
    var $ = jQuery;
    $('.cc-flex', root).each(function () {
      var el = this;
      var sum = 0;
      var horiz = $(el).hasClass('horizontal');
      $('.cc-fixed', el.offsetParent).each (function () {
        for (var fixed = this; fixed != el.offsetParent; fixed = fixed.parentNode)
          if ($(fixed).hasClass('cc-flex'))
            break;
        if (fixed == el.offsetParent)
          sum += horiz ? this.offsetWidth : this.offsetHeight;
      });
      el.style[horiz ? 'width' : 'height'] = (this.offsetParent[horiz ? 'clientWidth' : 'clientHeight'] - sum) + 'px';
    });
  },
	 
	 // Check if the form is not-empty according to non-empty fields
  validateForm: function (form, callback) {
  	var self = this;
	  var ok = true;
	  jQuery('.validate', form).each(function () {
		  if (!self.fireCallback(callback, this))
		  	ok = false;
	  });
	  
	  return ok;
  },
  /*
  Passed a HTML DOM element - it clears all children folowwing last one. Pass null for clearing all.
  */
  clearChildren: function(obj, last) {
    while (obj.lastChild && obj.lastChild != last) {
      obj.removeChild(obj.lastChild);
    }
  },
    
	/* formats a string, replacing {number | property} in it with the corresponding value in the arguments
  */
  formatString: function(format, pars) {
    for (var i in pars)
      format = format.replace('{' + i + '}', pars[i]);
    return format;
  },
  
  trim: function(obj) {
    if (obj === undefined || obj == null)
      return '';
    if (typeof obj == "string")
      return obj.trim();
    else
      return obj;
  },
  
  copyToClipboard: function(text, prompt) {
    if (!prompt) {
      prompt = "Press Ctrl-C (Command-C) to copy and then Enter.";
    }
    window.prompt(prompt, text);
  },
  
  equalizeHeights: function() {
    var tabs = [];
    for (var i = 0;i < arguments.length; ++i) {
      tabs[i] = arguments[i].firstElementChild;
    }    
    
    for (;;) {
      var height = 0;
      for (i = 0;i < tabs.length ; ++i) {
        if (tabs[i] == null)
          continue;
          
        if (!jQuery(tabs[i]).hasClass('lock-height') && tabs[i].style.height != '')
          tabs[i].style.height = "auto";

        if (tabs[i].offsetHeight > height)
          height = tabs[i].offsetHeight;
      }
      
      if (height == 0)
        break;
        
      for (i = 0;i < tabs.length ; ++i) {
        if (tabs[i] != null) {
          jQuery(tabs[i]).height(height);
          tabs[i] = tabs[i].nextElementSibling;
        }
      }
    }
  },
  
  positionTo: function (el, parent) {
    var ps = { left: -parent.offsetLeft, top: -parent.offsetTop };
    parent = parent.offsetParent;
    for (;!!el && el != parent; el = el.offsetParent) {
      ps.left += el.offsetLeft;
      ps.top += el.offsetTop;
    }
    return ps;
  },
  
  addParameter: function (url, param) {
    return url + (("&?".indexOf(url.charAt(url.length - 1)) == -1) ?  (url.indexOf('?') > 0 ? "&" : "?") : '') + param;
  },
  
  removeParameter: function (url, param) {
    return url.replace(new RegExp('(.*\?.*)(' + param + '=[^\&\s$]*\&?)(.*)'), '$1$3');
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
        len = seg.length, i = 0, s, v, arr;
        for (;i<len;i++) {
          if (!seg[i]) { continue; }
          s = seg[i].split('=');
          v = (s.length>1)?decodeURIComponent(s[1].replace(/\+/g,  " ")):'';
          if (s[0].indexOf('[]') == s[0].length - 2) {
            arr = ret[s[0].slice(0, -2)];
            if (arr === undefined)
              ret[s[0].slice(0, -2)] = [v];
            else
              arr.push(v);
          }
          else
            ret[s[0]] = v;
        }
        return ret;
      })(),
      file: (a.pathname.match(/\/([^\/?#]+)$/i) || [,''])[1],
      hash: a.hash.replace('#',''),
      path: a.pathname.replace(/^([^\/])/,'/$1'),
      relative: (a.href.match(/tps?:\/\/[^\/]+(.+)/) || [,''])[1],
      segments: a.pathname.replace(/^\//,'').split('/')
    };
  }    
};

function ccNonEmptyFilter(v) {
  return v !== undefined && v != null && v != '';  
}
window.jT = window.jToxKit = {
	templateRoot: null,

	callId: 0,
	
	templates: { },        // html2js routine will fill up this variable
	tools: { },            // additional, external tools added with html2js

	/* SETTINGS. The following parametes can be passed in settings object to jT.init(), or as data-XXX - with the same names. Values set here are the defaults.
	*/
	settings: {
	  plainText: false,     // whether to expect result as plain text, and not JSON. Used in special cases like ketcher.
  	jsonp: false,         // whether to use JSONP approach, instead of JSON.
  	crossDomain: false,   // should it expect cross-domain capabilities for the queries.
  	baseUrl: null,        // the baseUrl for the server that loaded the page.
  	fullUrl: null,        // the url as it is on loading the page - this is parsed one, i.e. parseUrl() processed.
  	timeout: 15000,       // the timeout of call to the server should be wait before the attempt is considered error.
  	pollDelay: 250,       // after how many milliseconds a new attempt should be made during task polling.
  	onConnect: null,		  // function (service): called when a server request is started - for proper visualization. Part of settings.
  	onSuccess: null,	    // function (code, mess): called on server request successful return. It is called along with the normal processing. Part of settings.
  	onError: function (s, c, j) { if (!!console && !!console.log) console.log("jT call error [" + c + "]: " + j.responseText + " from request: [" + s + "]"); },		// called on server request error.
  },
  
  // these are used in setting inheritance, so that non-inheritable settings are blanked...
  blankSettings: {
    onDetails: null,
    noInterface: false,
    selectionHandler: null
  },
	
	// form the "default" baseUrl if no other is supplied
	formBaseUrl: function(url) {
    return !!url.host ? url.protocol + "://" + url.host + (url.port.length > 0 ? ":" + url.port : '') + '/' + url.segments[0] : null;
	},
    
  // initializes one kit, based on the kit name passed, either as params, or found within data-XXX parameters of the element
  initKit: function(element) {
    var self = this;

  	var dataParams = self.$(element).data();
  	var kit = dataParams.kit;
  	var topSettings = self.$.extend(true, {}, self.settings);
  	var parent = null;

  	// we need to traverse up, to collect some parent's settings...
  	self.$(self.$(element).parents('.jtox-toolkit').toArray().reverse()).each(function(){
  	  parent = self.kit(this);
    	if (!self.$(this).hasClass('jtox-widget') && parent != null) {
      	topSettings = self.$.extend(true, topSettings, parent.settings);
    	}
  	});
  	
  	// make us ultimate parent of all
  	if (!parent)
  	  parent = self;
  	  
    dataParams = self.$.extend(true, topSettings, self.blankSettings, dataParams);
    dataParams.baseUrl = self.fixBaseUrl(dataParams.baseUrl);

	  // the real initialization function
    var realInit = function (params) {
    	if (!kit)
    		return null;
      // add jTox if it is missing AND there is not existing object/function with passed name. We can initialize ketcher and others like this too.
    	if (!window[kit] && kit.indexOf('jTox') != 0)
  	  	kit = 'jTox' + kit.charAt(0).toUpperCase() + kit.slice(1);
  
    	var fn = window[kit];
    	var obj = null;
      if (typeof fn == 'function')
    	  obj = new fn(element, params);
      else if (typeof fn == "object" && typeof fn.init == "function")
        obj = fn.init(element, params);
      
      if (obj != null) {
        if (fn.kits === undefined)
          fn.kits = [];
        fn.kits.push(obj);
        obj.parentKit = parent;
      }
      else
        console.log("jToxError: trying to initialize unexistend jTox kit: " + kit);

      return obj;
    };

	  // first, get the configuration, if such is passed
	  if (!ccLib.isNull(dataParams.configFile)) {
	    // we'll use a trick here so the baseUrl parameters set so far to take account... thus passing 'fake' kit instance
	    // as the first parameter of jT.call();
  	  self.call({ settings: dataParams}, dataParams.configFile, function(config){
    	  if (!!config)
    	    dataParams['configuration'] = self.$.extend(true, dataParams['configuration'], config);
        self.$(element).data('jtKit', realInit(dataParams));
  	  });
	  }
	  else {
	    if (!!window[dataParams.configuration] && typeof dataParams.configuration == "string") {
	      var config = window[dataParams.configuration];
	      dataParams.configuration = (typeof config != 'function' ? config : config(kit));
      }
  	  
      self.$(element).data('jtKit', realInit(dataParams));
	  }
  },
  
  // the jToxKit initialization routine, which scans all elements, marked as 'jtox-toolkit' and initializes them
	init: function(root) {
  	var self = this;
  	
  	if (!root) {
    	self.initTemplates();
  
      // make this handler for UUID copying. Once here - it's live, so it works for all tables in the future
      self.$(document).on('click', '.jtox-toolkit span.ui-icon-copy', function () { ccLib.copyToClipboard(self.$(this).data('uuid')); return false;});
      // install the click handler for fold / unfold
      self.$(document).on('click', '.jtox-foldable>.title', function() { self.$(this).parent().toggleClass('folded'); });
      // install diagram zooming handlers
      self.$(document).on('click', '.jtox-diagram span.ui-icon', function () { 
        self.$(this).toggleClass('ui-icon-zoomin').toggleClass('ui-icon-zoomout');
        self.$('img', this.parentNode).toggleClass('jtox-smalldiagram'); 
      });
      
      // scan the query parameter for settings
  		var url = self.settings.fullUrl = ccLib.parseURL(document.location);
  		var queryParams = url.params;
  		if (!queryParams.baseUrl)
  		  queryParams.baseUrl = self.formBaseUrl(url);
  		else
    		queryParams.baseUrl = self.fixBaseUrl(queryParams.baseUrl);
  	
      self.settings = self.$.extend(true, self.settings, queryParams); // merge with defaults
      root = document;
  	}

  	// now scan all insertion divs
  	self.$('.jtox-toolkit', root).each(function(i) { if (!self.$(this).data('manualInit')) self. initKit(this); });
	},
	
	kit: function (element) {
  	return $(element).data('jtKit');
	},
	
	parentKit: function(name, element) {
	  var self = this;
    var query = null;
    if (typeof name == 'string')
      name = window[name];
    self.$(element).parents('.jtox-toolkit').each(function() {
      var kit = self.kit(this);
      if (!kit || !!query)
        return;
      if (!name || kit instanceof name)
        query = kit;
    });
    
    return query;
  },
	
	initTemplates: function() {
	  var self = this;

    var root = self.$('.jtox-template')[0];
    if (!root) {
    	root = document.createElement('div');
    	root.className = 'jtox-template';
    	document.body.appendChild(root);
    }
    
	  var html = root.innerHTML;
  	for (var t in self.templates) {
    	html += self.templates[t];
  	}
  	
  	root.innerHTML = html;
  	self.templateRoot = root;
	},
	
	getTemplate: function(selector) {
  	var el = this.$(selector, this.templateRoot)[0];
  	if (!!el){
    	var el = el.cloneNode(true);
      el.removeAttribute('id');
    }
    return el;
	},
	
	insertTool: function (name, root) {
	  var html = this.tools[name];
	  if (!ccLib.isNull(html)) {
  	  root.innerHTML = html;
  	  this.init(root); // since we're pasting as HTML - we need to make re-traverse and initiazaltion of possible jTox kits.
    }
    return root;
	},
  
	/* Poll a given taskId and calls the callback when a result from the server comes - 
	be it "running", "completed" or "error" - the callback is always called.
	*/
	pollTask : function(kit, task, callback, jhr) {
		var self = this;
		if (task == null || task.task == null || task.task.length < 1 || !!task.task.error){
		  ccLib.fireCallback(callback, kit, task, jhr);
			return;
		}
		task = task.task[0];
		if (task.completed == -1){ // i.e. - running
			setTimeout(function(){
				self.call(kit, task.result, function(newTask, jhr){
					self.pollTask(kit, newTask, callback, jhr);
				});
			}, kit.settings.pollDelay);
		}
		else
  	  ccLib.fireCallback(callback, kit, task, jhr);
	},
	
	/* Fix the baseUrl - remove the trailing slash if any
	*/
	fixBaseUrl: function (url) {
    if (url != null && url.charAt(url.length - 1) == '/')
      url = url.slice(0, -1);
  	return url;
	},
	/* Deduce the baseUrl from a given Url - either if it is full url, of fallback to jToxKit's if it is local
	Passed is the first "non-base" component of the path...
	*/
	grabBaseUrl: function(url){
    if (!ccLib.isNull(url) && url.indexOf('http') == 0)
      return this.formBaseUrl(ccLib.parseURL(url));
    else
      return this.settings.baseUrl;
	},

  /* Grab the paging information from the given URL and place it into the settings of passed
  kit, as <kit>.settings.pageStart and <kit>.settings.pageSize. Pay attention that it is 'pageStart' 
  and not 'pageNo'.
  */
  grabPaging: function (kit, url) {
    var urlObj = ccLib.parseURL(url);
    if (urlObj.params['pagesize'] !== undefined) {
      var sz = parseInt(urlObj.params['pagesize']);
      if (sz > 0)
        kit.settings.pageSize = kit.pageSize = sz;
      url = ccLib.removeParameter(url, 'pagesize');
    }
    
    if (urlObj.params['page'] !== undefined) {
      var beg = parseInt(urlObj.params['page']);
      if (beg >= 0)
        kit.settings.pageStart = kit.pageStart = beg * kit.settings.pageSize;
      url = ccLib.removeParameter(url, 'page');
    }
    
    return url;
  },
	
	/* Makes a server call for provided service, with settings form the given kit and calls 'callback' at the end - always.
	The 'params', if passed, can have following attributes:
		'method': the HTTP method to be used
		'data': the data to be passed to the server with the request.
	*/
	call: function (kit, service, params, callback){
	  var self = this;
		if (typeof params != 'object') {
			callback = params; // the params parameters is obviously omitted
			params = {};
		}
		else if (params == null)
		  params = {};
		
	  var settings = self.$.extend({}, this.settings, params);
		if (kit == null)
		  kit = self;
		else 
  		settings = self.$.extend(settings, kit.settings);

		var accType = settings.plainText ? "text/plain" : (settings.jsonp ? "application/x-javascript" : "application/json");
		
		if (!params.data){
			params.data = {};
			if (settings.jsonp)
				params.data.media = accType;
				
			if (!params.method)
				params.method = 'GET';
		}
		else if (!params.method)
				params.method = 'POST';

		// on some queries, like tasks, we DO have baseUrl at the beginning
		if (service.indexOf("http") != 0)
			service = settings.baseUrl + service;

    var myId = self.callId++;
		ccLib.fireCallback(settings.onConnect, kit, service, params, myId);
			
		// now make the actual call
		self.$.ajax(service, {
			dataType: params.dataType || (settings.plainText ? "text": (settings.jsonp ? 'jsonp' : 'json')),
			headers: self.$.extend({ Accept: accType }, params.headers),
			crossDomain: settings.crossDomain || settings.jsonp,
			timeout: parseInt(settings.timeout),
			type: params.method,
			data: params.data,
			jsonp: settings.jsonp ? 'callback' : false,
			error: function(jhr, status, error){
			  ccLib.fireCallback(settings.onError, kit, service, status, jhr, myId);
			  ccLib.fireCallback(callback, kit, null, jhr);
			},
			success: function(data, status, jhr){
			  ccLib.fireCallback(settings.onSuccess, kit, service, status, jhr, myId);
			  ccLib.fireCallback(callback, kit, data, jhr);
			}
		});
	},
	
	/* Encapsulates the process of calling certain service, along with task polling, if needed.
  	*/
	service: function (kit, service, params, callback) {
  	var self = this;
  	var fnCB = !params || params.method === 'GET' || params.method === 'get' || (!params.data && !params.method) ? callback : function (data, jhr) {
      if (!data)
        ccLib.fireCallback(callback, kit, data, jhr);
      else
        self.pollTask(kit, data, function (task, jhr) { ccLib.fireCallback(callback, kit, !!task && !task.error ? task.result : null, jhr); });
  	};
  	
  	this.call(kit, service, params, fnCB);
  }
};

/* UI related functions of jToxKit are put here for more convenient usage
*/
window.jT.ui = {
  shortenedData: function (content, message, data) {
    var res = '';
    
    if (ccLib.isNull(data))
      data = content;
    if (data.toString().length <= 5) {
      res += content;
    }
    else {
      res += '<div class="shortened">' + content + '</div>';
      if (message != null)
        res += '<span class="ui-icon ui-icon-copy" title="' + message + '" data-uuid="' + data + '"></span>';
    }
    return res;
  },
  
  changeTabsIds: function (root, suffix) {
    jT.$('ul li a', root).each(function() {
      var id = jT.$(this).attr('href').substr(1);
      var el = document.getElementById(id);
      id += suffix;
      el.id = id;
      jT.$(this).attr('href', '#' + id);
    })  
  },
  
  addTab: function(root, name, id, content) {
    // first try to see if there is same already...
    if (document.getElementById(id) != null)
      return;
  
    // first, create and add li/a element
    var li = document.createElement('li');
    var a = document.createElement('a');
    li.appendChild(a);
    a.href = '#' + id;
    a.innerHTML = name;
    jT.$('ul', root)[0].appendChild(li);
    
    // then proceed with the panel, itself...
    if (typeof content == 'function')
      content = content(root);
    else if (typeof content == 'string') {
      var div = document.createElement('div');
      div.innerHTML = content;
      content = div;
    }
      
    content.id = id;
    root.appendChild(content);
    $(root).tabs('refresh');
    return { 'tab': a, 'content': content };
  },
  
  modifyColDef: function (kit, col, category, group) {
    if (col.sTitle === undefined || col.sTitle == null)
      return null;
      
	  var name = col.sTitle.toLowerCase();
	  
	  // helper function for retrieving col definition, if exists. Returns empty object, if no.          
	  var getColDef = function (cat) {
	    var catCol = kit.settings.configuration.columns[cat];
	    if (!ccLib.isNull(catCol)) {
	      if (!!group) {
	        catCol = catCol[group];
  	      if (!ccLib.isNull(catCol))
  	        catCol = catCol[name];
        }
        else
	        catCol = catCol[name];
	    }
	
	    if (ccLib.isNull(catCol))
	      catCol = {};
	    return catCol;
	  };
	  // now form the default column, if existing and the category-specific one...
	  // extract column redefinitions and merge them all.
	  col = jT.$.extend(col, (!!group ? getColDef('_') : {}), getColDef(category));
	  return ccLib.isNull(col.bVisible) || col.bVisible ? col : null;
  },
  
  sortColDefs: function (colDefs) {
    for (var i = 0, l = colDefs.length; i < l; ++i)
      colDefs[i].iNaturalOrder = i;
	  colDefs.sort(function(a, b) {
  	  var res = (a.iOrder || 0) - (b.iOrder || 0);
  	  if (res == 0) // i.e. they are equal
  	    res = a.iNaturalOrder - b.iNaturalOrder;
  	  return res; 
    });
  },
  
  processColumns: function (kit, category) {
    var colDefs = [];
    var catList = kit.settings.configuration.columns[category];
    for (var name in catList) {
      var col = this.modifyColDef(kit, catList[name], category);
      if (col != null)
        colDefs.push(col);
    }
      
    this.sortColDefs(colDefs);
    return colDefs;
  },
  
  renderMulti: function (data, type, full, render) {
    var dlen = data.length;
    if (dlen < 2)
      return render(data[0], type, full);

    var df = '<table>';
    for (var i = 0, dlen = data.length; i < dlen; ++i) {
      df += '<tr class="' + (i % 2 == 0 ? 'even' : 'odd') + '"><td>' + render(data[i], type, full, i) + '</td></tr>';
    }
    
    df += '</table>';
    return df;
  },
  
  inlineChanger: function (location, breed, holder, handler) {
    if (handler == null)
      handler = "changed";
      
    if (breed == "select")
      return function (data, type, full) {
        return type != 'display' ? (data || '') : '<select class="jt-inlineaction jtox-handler" data-handler="' + handler + '" data-data="' + location + '" value="' + (data || '') + '">' + (holder || '') + '</select>';
      };
    else if (breed == "checkbox") // we use holder as 'isChecked' value
      return function (data, type, full) {
        return type != 'display' ? (data || '') : '<input type="checkbox" class="jt-inlineaction jtox-handler" data-handler="' + handler + '" data-data="' + location + '"' + (((!!holder && data == holder) || !!data) ? 'checked="checked"' : '') + '"/>';
      };
    else if (breed =="text")
      return function (data, type, full) {
        return type != 'display' ? (data || '') : '<input type="text" class="jt-inlineaction jtox-handler" data-handler="' + handler + '" data-data="' + location + '" value="' + (data || '') + '"' + (!holder ? '' : ' placeholder="' + holder + '"') + '/>';
      };
  },
    
  installMultiSelect: function (root, callback, parenter) {
    if (parenter == null)
      parenter = function (el) { return el.parentNode; };
    $('a.select-all', root).on('click', function (e) {
      $('input[type="checkbox"]', parenter(this)).each(function () { this.checked = true; if (callback == null) jT.$(this).trigger('change'); });
      if (callback != null)
        callback.call(this, e);
    });
    $('a.unselect-all', root).on('click', function (e) {
      $('input[type="checkbox"]', parenter(this)).each(function () { this.checked = false; if (callback == null) jT.$(this).trigger('change');});
      if (callback != null)
        callback.call(this, e);
    });
  },
  
  installHandlers: function (kit, root) {
    if (root == null)
      root = kit.rootElement;
      
    jT.$('.jtox-handler', root).each(function () {
      var name = jT.$(this).data('handler');
      var handler = null;
      if (kit.settings.configuration != null && kit.settings.configuration.handlers != null)
        handler = kit.settings.configuration.handlers[name];
      handler = handler || window[name];
      
      if (!handler)
        console.log("jToxQuery: referring unknown handler: " + name);
      else if (this.tagName == "INPUT" || this.tagName == "SELECT" || this.tagName == "TEXTAREA")
        jT.$(this).on('change', handler).on('keydown', jT.ui.enterBlur);
      else // all the rest respond on click
        jT.$(this).on('click', handler);
    });
  },
  
  enterBlur: function (e) {
    if (e.keyCode == 13)
      this.blur();
  },
  
  rowData: function (el) {
    var row = $(el).closest('tr')[0];
    var table = $(row).closest('table')[0];
    return $(table).dataTable().fnGetData(row);
  },
  
  rowIndex: function (el) {
    var row = $(el).closest('tr')[0];
    var table = $(row).closest('table')[0];
    return $(table).dataTable().fnGetPosition(row);
  },
  
  rowInline: function (el, base) {
    var row = $(el).closest('tr')[0];
    var data = $.extend({}, base);
    $('.jt-inlineaction', row).each(function () {
      var loc = $(this).data('data');
      if (loc != null)
        ccLib.setJsonValue(data, loc, ccLib.getObjValue(this));
    });
    
    return data;
  },
  
  columnData: function (cols, data, type) {
    var out = new Array(data.length);
    if (type == null)
      type = 'display';
    for (var i = 0, dl = data.length; i < dl; ++i) {
      var entry = {};
      var d = data[i];
      for (var c = 0, cl = cols.length; c < cl; ++c) {
        var col = cols[c];
        var val = ccLib.getJsonValue(d, col.mData) || col.sDefaultValue;
        entry[col.sTitle] = typeof col.mRender != 'function' ? val : col.mRender(val, type, d);
      }
      
      out[i] = entry;
    }
    
    return out;
  },
  
  queryInfo: function (aoData) {
    var info = {};
    for (var i = 0, dl = aoData.length; i < dl; ++i)
      info[aoData[i].name] = aoData[i].value;
  
    if (info.iSortingCols > 0) {
      info.iSortDirection = info.sSortDir_0.toLowerCase();
      info.sSortData = info["mDataProp_" + info.iSortCol_0];
    }
    else {
      info.iSortDirection = 0;
      info.sSortData = "";
    }
    
    return info;
  },
  
  putTable: function (kit, root, config, settings) {
    var onRow = kit.settings.onRow;
    if (onRow === undefined && settings != null)
      onRow = settings.onRow;
      
    var opts = jT.$.extend({
      "bPaginate": false,
      "bProcessing": true,
      "bLengthChange": false,
  		"bAutoWidth": false,
      "sDom" : kit.settings.sDom,
  		"oLanguage": kit.settings.oLanguage,
      "bServerSide": false,
      "fnCreatedRow": function( nRow, aData, iDataIndex ) {
        // call the provided onRow handler, if any
        if (typeof onRow == 'function') {
          var res = ccLib.fireCallback(onRow, kit, nRow, aData, iDataIndex);
          if (res === false)
            return;
        }
        
        // equalize multi-rows, if there are any
        ccLib.equalizeHeights.apply(window, jT.$('td.jtox-multi table tbody', nRow).toArray());
        
        // handle a selection click.. if any
        jT.ui.installHandlers(kit, nRow);
        if (typeof kit.settings.selectionHandler == "function")
          jT.$('input.jt-selection', nRow).on('change', kit.settings.selectionHandler);
        // other (non-function) handlers are installed via installHandlers().

        if (!!kit.settings.onDetails) {
          jT.$('.jtox-details-toggle', nRow).on('click', function(e) {  
            var root = jT.ui.toggleDetails(e, nRow);
            if (!!root) {
              ccLib.fireCallback(kit.settings.onDetails, kit, root, aData, this);
            }
          });
        }
      }
    }, settings);
    
    if (opts.aoColumns == null)
      opts.aoColumns = jT.ui.processColumns(kit, config);
    if (opts.oLanguage == null)
      delete opts.oLanguage;

    var table = jT.$(root).dataTable(opts);
    jT.$(table).dataTable().fnAdjustColumnSizing();
    return table;
  },
  
  renderRelation: function (data, type, full) {
    if (type != 'display')
      return ccLib.joinDeep(data, 'relation', ',');
      
    var res = '';
    for (var i = 0, il = data.length; i < il; ++i)
      res += '<span>' + data[i].relation.substring(4).toLowerCase() + '</span>' + jT.ui.putInfo(full.URI + '/composition', data[i].compositionName + '(' + data[i].compositionUUID + ')');
    return res;
  },
  
  renderRange: function (data, unit, type, prefix) {
    var out = "";
    if (typeof data == 'string' || typeof data == 'number')
      out += (type != 'display') ? data : ((!!prefix ? prefix + "&nbsp;=&nbsp;" : '') + jT.ui.valueWithUnits(data, unit));
    else if (typeof data == 'object' && data != null) {
      var loValue = ccLib.trim(data.loValue),
          upValue = ccLib.trim(data.upValue);

      if (!!loValue && !!upValue && !!data.upQualifier && data.loQualifier != '=') {
        if (!!prefix)
          out += prefix + "&nbsp;=&nbsp;";
        out += (data.loQualifier == ">=") ? "[" : "(";
        out += loValue + ", " + upValue;
        out += (data.upQualifier == "<=") ? "]" : ") ";
      }
      else // either of them is non-undefined
      {
        var fnFormat = function (q, v) {
          return (!!q ? q : "=") + " " + v;
        };
        
        if (!!prefix)
          out += prefix + ' ';
        if (!!loValue)
          out += fnFormat(data.loQualifier, loValue);
        else if (!!data.upValue)
          out += fnFormat(data.upQualifier, upValue);
        else
          out += type == 'display' ? '-' : '';
      }
      
      out = out.replace(/ /g, "&nbsp;");
      if (type == 'display') {
        unit = ccLib.trim(data.unit || unit);
        if (!!unit)
          out += '&nbsp;<span class="units">' + unit.replace(/ /g, "&nbsp;") + '</span>';
      }
    }
    else
      out += '-';
    return out;
  },
  
	renderObjValue: function (data, units, type, pre) {
		if (!data)
		  return type == 'display' ? '-' : '';
		  
		var val = jT.ui.renderRange(data, units, type, pre);
		if (ccLib.trim(val) == '-')
		  val = '';
		if (!!val && type != 'display' && !!data.units)
		  val += '&nbsp;' + data.units;
		if (!!data.textValue) {
  		if (!!val && type == 'display')
  		  val += '&nbsp;/&nbsp;';
  		val += data.textValue;
		}
		
		if (!val)
		  val = '-';
		return val;
	},
  
  putInfo: function (href, title) {
    return '<sup class="helper"><a target="_blank" href="' + (href || '#') + '" title="' + (title || href) + '"><span class="ui-icon ui-icon-info"></span></a></sup>';
  },
  
  putStars: function (kit, stars, title) {
    if (!kit.settings.shortStars) {
      var res = '<div title="' + title + '">';
      for (var i = 0;i < kit.settings.maxStars;++i) {
        res += '<span class="ui-icon ui-icon-star jtox-inline';
        if (i >= stars)
          res += ' transparent';
        res += '"></span>';
      }
      return res + '</div>';
    }
    else { // i.e. short version
      return '<span class="ui-icon ui-icon-star jtox-inline" title="' + title + '"></span>' + stars;
    }
  },
  
  diagramUri: function (URI) {
    return !!URI && (typeof URI == 'string') ? URI.replace(/(.+)(\/conformer.*)/, "$1") + "?media=image/png" : '';
  },
  
  valueWithUnits: function (val, unit) {
    var out = '';
    if (val != null) {
      out += ccLib.trim(val.toString()).replace(/ /g, "&nbsp;");
      if (!!unit)
        out += '&nbsp;<span class="units">' + unit.replace(/ /g, "&nbsp;") + '</span>';
    }
    return out;
  },
  
  updateCounter: function (str, count, total) {
    var re = null;
    var add = '';
    if (count == null)
      count = 0;
    if (total == null) {
      re = /\(([\d\?]+)\)$/;
      add = '' + count;
    }
    else {
      re = /\(([\d\?]+\/[\d\?\+-]+)\)$/;
      add = '' + count + '/' + total;
    }
    
    // now the addition    
    if (!str.match(re))
      str += ' (' + add + ')';
    else
      str = str.replace(re, "(" + add + ")");
    
    return str;
  },
    
  bindControls: function (kit, handlers) {
    var pane = jT.$('.jtox-controls', kit.rootElement)[0];
    if (kit.settings.showControls) {
      ccLib.fillTree(pane, { "pagesize": kit.settings.pageSize });
      jT.$('.next-field', pane).on('click', handlers.nextPage);
      jT.$('.prev-field', pane).on('click', handlers.prevPage);
      jT.$('select', pane).on('change', handlers.sizeChange)
      var pressTimeout = null;
      jT.$('input', pane).on('keydown', function(e) {
        var el = this;
        if (pressTimeout != null)
          clearTimeout(pressTimeout);
        pressTimeout = setTimeout(function () { handlers.filter.apply(el, [e]); }, 350);
      });
    }
    else // ok - hide me
      pane.style.display = "none";
  },

  putActions: function (kit, col, ignoreOriginal) {
    if (!!kit.settings.selectionHandler || !!kit.settings.onDetails) {
      var oldFn = col.mRender;
      var newFn = function (data, type, full) {
        var html = oldFn(data, type, full);
        if (type != 'display')
          return html;
          
        if (!!ignoreOriginal)
          html = '';
          
        // this is inserted BEFORE the original, starting with given PRE-content
        if (!!kit.settings.selectionHandler)
          html = '<input type="checkbox" value="' + data + '" class="' +
                (typeof kit.settings.selectionHandler == 'string' ? 'jtox-handler" data-handler="' + kit.settings.selectionHandler + '"' : 'jt-selection"') +
                '/>' + html;
                
        // strange enough - this is inserted AFTER the original
        if (!!kit.settings.onDetails)
          html += '<span class="jtox-details-toggle ui-icon ui-icon-folder-collapsed" data-data="' + data +'" title="Press to open/close detailed info for this entry"></span>';

        return html;
      };
      
      col.mRender = newFn;
    }
    return col;
  },
  
  toggleDetails: function (event, row) {
    self.$(event.currentTarget).toggleClass('ui-icon-folder-collapsed');
    self.$(event.currentTarget).toggleClass('ui-icon-folder-open');
    self.$(event.currentTarget).toggleClass('jtox-openned');
    if (!row)
      row = self.$(event.currentTarget).parents('tr')[0];

    var cell = self.$(event.currentTarget).parents('td')[0];
    
    if (self.$(event.currentTarget).hasClass('jtox-openned')) {
      var detRow = document.createElement('tr');
      var detCell = document.createElement('td');
      detRow.appendChild(detCell);
      self.$(detCell).addClass('jtox-details');
      
      detCell.setAttribute('colspan', self.$(row).children().length - 1);
      row.parentNode.insertBefore(detRow, row.nextElementSibling);

      cell.setAttribute('rowspan', '2');
      return detCell;
    }
    else {
      cell.removeAttribute('rowspan');
      self.$(self.$(row).next()).remove();
      return null;
    }
  }
};

// we need to do this here - because other tools/libraries could have scheduled themselves on 'ready',
// so it'll be too late to make this assignment then. Also - we can use jT.$ from now on :-)
jT.$ = jQuery; // .noConflict();

  	
jT.$(document).ready(function(){
  jT.init();
});
/* toxquery.js - Universal query widget, that can work with any kit (study or compound) inside
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxQuery = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    scanDom: true,          // whether to scan the whole DOM for finding the main query kit
    initialQuery: false,    // whether to perform an initial query, immediatly when loaded.
    kitSelector: null,      // selector for the main kit, if outside, for example.
    dom: null,              // an, all-prepared object with all sub-kits. Structure { kit: <main kit object>, widgets: { <kit type>: <widget kit object> } }

    configuration: {
      // this is the main thing to be configured
      handlers: { 
        query: function (e, query) { jT.parentKit(jToxQuery, this).query(); },
      }
    }
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized
    
    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);
    self.mainKit = null;
        
    if (self.settings.scanDom && !self.settings.dom) {
      self.settings.dom = { kit: null, widgets: { } };
      jT.$('.jtox-toolkit', self.rootElement).each(function () {
        if (jT.$(this).hasClass('jtox-widget'))
          self.settings.dom.widgets[jT.$(this).data('kit')] = this;
        else 
          self.settings.dom.kit = this;
      });
    }
    
    if (!!self.settings.kitSelector)
      self.settings.dom.kit = jT.$(self.settings.kitSelector)[0];
    
    // finally, wait a bit for everyone to get initialized and make a call, if asked to
    if (!!self.settings.initialQuery)
      self.initialQueryTimer = setTimeout(function () { self.query(); }, 200);
  };
  
  cls.prototype = {
    widget: function (name) {
      return this.settings.dom.widgets[name];
    },
    
    kit: function () {
      if (!this.mainKit)
        this.mainKit = jT.kit(this.settings.dom.kit);
        
      return this.mainKit;
    },
    
    setWidget: function (id, dom) {
      this.settings.dom.widgets[id] = dom;
    },
        
    cancelInitialQuery: function () {
      if (!!this.initialQueryTimer)
        clearTimeout(this.initialQueryTimer);
    },
    
    /* Perform the actual query, traversing all the widgets and asking them to
    alter the given URL, then - makes the call */
    query: function () {
      var uri = this.settings.service || '';
      for (var w in this.settings.dom.widgets) {
        var widget = jT.kit(this.settings.dom.widgets[w]);
        if (!widget) 
          console.log("jToxError: the widget [" + w + "] is not recognized: ignored");
        else if (!widget['modifyUri'])
          console.log("jToxError: the widget [" + w + "] doesn't have 'modifyUri' method: ignored");
        else
          uri = widget.modifyUri(uri);
      }
      
      if (!!uri)
        this.kit().query(uri);
    }
  }; // end of prototype
  
  return cls;
})();

/* Now comes the jToxSearch component, which implements the compound searching block
*/
var jToxSearch = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    defaultNeedle: '50-00-0',     // which is the default search string, if empty one is provided
    smartsList: 'funcgroups',     // which global JS variable to seek for smartsList
    hideOptions: '',              // comma separated list of search options to hide
    slideInput: false,            // whether to slide the input, when focussed
    contextUri: null,             // a search limitting contextUri - added as dataset_uri parameter
    configuration: {
      handlers: { }
    }
  };
  
  var queries = {
    'auto': "/query/compound/search/all",
    'uri': "/query/compound/url/all",
    'similarity': "/query/similarity",
    'smarts': "/query/smarts"
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized
    
    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);
    self.rootElement.appendChild(jT.getTemplate('#jtox-search'));
    self.queryKit = jT.parentKit(jToxQuery, self.rootElement);
    
    self.search = { mol: "", type: "", queryType: "auto"};
    
    var form = jT.$('form', self.rootElement)[0];
    form.onsubmit = function () { return false; }
    
    // go for buttonset preparation, starting with hiding / removing passed ones
    if (!!self.settings.hideOptions) {
      var hideArr = self.settings.hideOptions.split(',');
      for (var i = 0; i < hideArr.length; ++i) {
        jT.$('#search' + hideArr[i], self.rootElement).remove();
        jT.$('label[for="search' + hideArr[i] + '"]', self.rootElement).remove();
      }
    }

    if (!!form.searchcontext) {
      form.searchcontext.value = self.settings.contextUri;
      $(form.searchcontext).on('change', function (e) {
        self.settings.contextUri = this.value;
      });
    }

    // when we change the value here - all, possible MOL caches should be cleared.
    jT.$(form.searchbox).on('change', function () { 
      self.setAuto();
    });
    
    if (self.settings.slideInput)
      jT.$(form.searchbox)
      .on('focus', function () {
        var gap = jT.$(form).width() - jT.$(radios).width() - 30 - jT.$('.search-pane').width();
        var oldSize = $(this).data('oldSize') || $(this).width();
        $(this).data('oldSize', oldSize);
        jT.$(this).css('width', '' + (oldSize + gap) + 'px');
      })
      .on('blur', function () {
        jT.$(this).css('width', '');
      });
    
    var hasAutocomplete = false;
    if (jT.$('#searchuri', self.rootElement).length > 0) {
      hasAutocomplete = true;
      jT.$(form.searchbox).autocomplete({
        minLength: 2,
        open: function() { jT.$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" ); },
        close: function() { jT.$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" ); },
        source: function (request, response) {
          jT.call(self, '/dataset?search=^' + request.term, function (result) {
            response(!result ? [] : $.map( result.dataset, function( item ) {
              var pos =  item.URI.lastIndexOf("/"),
                  shortURI = (pos >= 0) ? "D" + item.URI.substring(pos+1) + ": " : "";
              return {
                label: shortURI + item.title,
                value: item.URI
              };
            }));
          });
        }
      });
    }

    var radios = jT.$('.jq-buttonset', root).buttonset();
    var onTypeClicked = function () {
      form.searchbox.placeholder = jT.$(this).data('placeholder');
      jT.$('.search-pane .auto-hide', self.rootElement).addClass('hidden');
      jT.$('.search-pane .' + this.id, self.rootElement).removeClass('hidden');
      self.search.queryType = this.value;
      if (this.value == 'uri') {
        jT.$(form.drawbutton).addClass('hidden');
        if (hasAutocomplete)
          jT.$(form.searchbox).autocomplete('enable');
      }
      else {
        jT.$(form.drawbutton).removeClass('hidden');
        if (hasAutocomplete)
          jT.$(form.searchbox).autocomplete('disable');
      }
    };
    
    jT.$('.jq-buttonset input', root).on('change', onTypeClicked);

    var typeEl = jT.$('#search' + self.settings.option, root)[0];
    if (typeEl != null)
      jT.$(typeEl).trigger('click');
    else
      ccLib.fireCallback(onTypeClicked, jT.$('.jq-buttonset input', root)[0])
        
    // spend some time to setup the SMARTS groups
    if (!!window[self.settings.smartsList]) {
      var list = window[self.settings.smartsList];
      var familyList = [];
      var familyIdx = {};
      
      for (var i = 0, sl = list.length; i < sl; ++i) {
        var entry = list[i];
        if (familyIdx[entry.family] === undefined) {
          familyIdx[entry.family] = familyList.length;
          familyList.push([]);
        }

        familyList[familyIdx[entry.family]].push(entry);        
      }
      
      // now we can iterate over them
      var df = document.createDocumentFragment();
      for (fi = 0, fl = familyList.length; fi < fl; ++fi) {
        var grp = document.createElement('optgroup');
        grp.label = familyList[fi][0].family;

        for (i = 0, el = familyList[fi].length; i < el; ++i) {
          var e = familyList[fi][i];
          var opt = document.createElement('option');
          opt.innerHTML = e.name;
          opt.value = e.smarts;
          if (!!e.hint)
            jT.$(opt).attr('data-hint', e.hint);
          grp.appendChild(opt);
        }
        df.appendChild(grp);
      }
      
      // now it's time to add all this and make the expected behavior
      form.smarts.appendChild(df);
      form.smarts.firstElementChild.checked = true;
      
      jT.$(form.smarts).on('change', function () {
        var hint = jT.$(this[this.selectedIndex]).data('hint');
        form.smarts.title = (!!hint ? hint : '');
        self.setAuto(this.value);
      });
    }
    
    // Now, deal with KETCHER - make it show, attach handlers to/from it, and handlers for showing/hiding it.
    var ketcherBox = jT.$('.ketcher', root)[0];
    var ketcherReady = false;
    var onKetcher = function (service, method, async, parameters, onready) {
      if (service == "knocknock")
        onready("You are welcome!", null);
      else
        jT.call(self.queryKit.kit(), '/ui/' + service, {dataType: "text", data: parameters}, function (res, jhr) { onready(res, jhr); });
    };
    
    var ensureKetcher = function () {
      if (!ketcherReady) {
        jT.insertTool('ketcher', ketcherBox);
        ketcher.init({ root: ketcherBox, ajaxRequest: onKetcher });
        
        var emptySpace = jT.$('.toolEmptyCell', ketcherBox)[0];
        jT.$(emptySpace.appendChild(jT.getTemplate('#ketcher-usebutton'))).on('click', function () {
          var smiles = ketcher.getSmiles();
          var mol = ketcher.getMolfile();
          self.setMol(mol);
          if (!!smiles)
            form.searchbox.value = smiles;
        });
        jT.$(emptySpace.appendChild(jT.getTemplate('#ketcher-drawbutton'))).on('click', function () {
          ketcher.setMolecule(self.search.mol || form.searchbox.value);
        });
        ketcherReady = true;
      }
    };
    
    jT.$(form.drawbutton).on('click', function () { 
      if (jT.$(ketcherBox).hasClass('shrinken')) {
        ensureKetcher();
        jT.$(ketcherBox).css('display', '');
      }
      else
        setTimeout(function () { jT.$(ketcherBox).css('display', 'none'); }, 500);

      setTimeout(function () { jT.$(ketcherBox).toggleClass('shrinken') }, 50);
    });

    // finally - parse the URL-passed parameters and setup the values appropriately.
    var doQuery = false;
    if (!!self.settings.b64search) {
      self.setMol($.base64.decode(self.settings.b64search));
      doQuery = true;
    }
    else if (!!self.settings.search) {
      self.setAuto(self.settings.search);
      doQuery = true;
    }

    jT.ui.installHandlers(self);
    if (doQuery) {
      self.queryKit.cancelInitialQuery();
      setTimeout(function () { self.queryKit.query(); }, 250);
    }
    // and very finally - install the handlers...
  };
  
  cls.prototype = {
    // required from jToxQuery - this is how we add what we've collected
    modifyUri: function (uri) {
      var form = jT.$('form', this.rootElement)[0];
      var params = { type: this.search.type };
      var type = this.search.queryType;
      
      if (type == "auto" && params.type == 'auto' && form.searchbox.value.indexOf('http') == 0)
        type = "uri";
        
      var res = queries[type] + (uri.indexOf('?') > -1 ? '' : '?') + uri;

      if (!!this.search.mol) {
        params.b64search = $.base64.encode(this.search.mol);
      }
      else {
        params.search = form.searchbox.value;
        if (!params.search)
          params.search = this.settings.defaultNeedle;
          this.setAuto(params.search);
      }

      if (type == "auto" && form.regexp.checked)
        params['condition'] = "regexp";        
      if (type == 'similarity')
        params.threshold = form.threshold.value;
      
      if (!!this.settings.contextUri)
        params['dataset_uri'] = this.settings.contextUri;

      return ccLib.addParameter(res, $.param(params));
    },
    
    // some shortcuts for outer world.
    makeQuery: function (needle) {
      if (!!needle) 
        this.setAuto(needle);
      this.queryKit.query();
    },
    
    getNeedle: function () {
      return this.search.type == 'mol' ? this.search.mol : jT.$('form', this.rootElement)[0].searchbox.value;
    },
    
    setAuto: function (needle) {
      this.search.mol = null;
      this.search.type = 'auto';

      var box = jT.$('form', this.rootElement)[0].searchbox;
      if (!!this.search.oldplace)
        box.placeholder = this.search.oldplace;
      if (needle != null)
        box.value = needle;
    },
    
    setMol: function (mol) {
      var box = jT.$('form', this.rootElement)[0].searchbox;
      this.search.mol = mol;
      this.search.type = 'mol';
      this.search.oldplace = box.placeholder;
      
      box.placeholder = "MOL formula saved_";
      box.value = '';
    },
  }; // end of prototype
  
  return cls;
})();
/* toxcompound.js - General, universal compound dataset visualizer.
 *
 * Copyright 2012-2013, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxCompound = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    "noInterface": false,     // runs in interface-less mode, so that it can be used only for information retrieval.
    "showTabs": true,         // should we show tabs with groups, or not
    "tabsFolded": false,      // should present the feature-selection tabs folded initially
    "showExport": true,       // should we add export tab up there
    "showControls": true,     // should we show the pagination/navigation controls.
    "showUnits": true,        // should we show units in the column title.
    "hideEmpty": false,       // whether to hide empty groups instead of making them inactive
    "groupSelection": true,   // wether to show select all / unselect all links in each group
    "hasDetails": true,       // whether browser should provide the option for per-item detailed info rows.
    "hideEmptyDetails": true, // hide feature values, when they are empty (only in detailed view)
    "detailsHeight": "fill",  // what is the tabs' heightStyle used for details row
    "fixedWidth": null,				// the width (in css units) of the left (non-scrollable) part of the table
    "pageSize": 20,           // what is the default (startint) page size.
    "pageStart": 0,           // what is the default startint point for entries retrieval
    "rememberChecks": false,  // whether to remember feature-checkbox settings between queries
    "featureUri": null,       // an URI for retrieving all feature for the dataset, rather than 1-sized initial query, which is by default
    "metricFeature": "http://www.opentox.org/api/1.1#Similarity",   // This is the default metric feature, if no other is specified
    "onTab": null,            // invoked after each group's tab is created - function (element, tab, name, isMain);
    "onLoaded": null,         // invoked when a set of compounds is loaded.
    "onPrepared": null,       // invoked when the initial call for determining the tabs/columns is ready
    "onDetails": null,        // invoked when a details pane is openned
    "preDetails": null,       // invoked prior of details pane creation to see if it is going to happen at all
    "oLanguage": {},          // some default language settings, which apply to first (static) table only
    "fnAccumulate": function(fId, oldVal, newVal, features) {
      if (ccLib.isNull(newVal))
        return oldVal;
      newVal = newVal.toString();
      if (ccLib.isNull(oldVal) || newVal.toLowerCase().indexOf(oldVal.toLowerCase()) >= 0)
        return newVal;
      if (oldVal.toLowerCase().indexOf(newVal.toLowerCase()) >= 0)
        return oldVal;
      return oldVal + ", " + newVal;
    },
    "configuration": {
      "groups": {
        "Identifiers" : [
          "http://www.opentox.org/api/1.1#Diagram",
          "#DetailedInfoRow",
          "http://www.opentox.org/api/1.1#CASRN", 
          "http://www.opentox.org/api/1.1#EINECS",
          "http://www.opentox.org/api/1.1#IUCLID5_UUID"
        ],
        
        "Names": [
          "http://www.opentox.org/api/1.1#ChemicalName",
          "http://www.opentox.org/api/1.1#TradeName",
          "http://www.opentox.org/api/1.1#IUPACName",
          "http://www.opentox.org/api/1.1#SMILES",
          "http://www.opentox.org/api/1.1#InChIKey",
          "http://www.opentox.org/api/1.1#InChI",
          "http://www.opentox.org/api/1.1#REACHRegistrationDate"
        ],
        
        "Calculated": function (name, miniset) {
          var arr = [];
          if (miniset.dataEntry.length > 0 && !ccLib.isNull(miniset.dataEntry[0].compound.metric))
            arr.push(this.settings.metricFeature);

          for (var f in miniset.feature) {
            var feat = miniset.feature[f];
            if (ccLib.isNull(feat.source) || ccLib.isNull(feat.source.type) || !!feat.basic)
              continue;
            else if (feat.source.type.toLowerCase() == "algorithm" || feat.source.type.toLowerCase() == "model") {
              arr.push(f);
            }
          }
          return arr;
        },
        
        "Other": function (name, miniset) {
          var arr = [];
          for (var f in miniset.feature) {
            if (!miniset.feature[f].used && !miniset.feature[f].basic)
              arr.push(f);
          }
          return arr;
        }
      },
      "exports": [
        {type: "chemical/x-mdl-sdfile", icon: "images/sdf64.png"},
        {type: "chemical/x-cml", icon: "images/cml64.png"},
        {type: "chemical/x-daylight-smiles", icon: "images/smi64.png"},
        {type: "chemical/x-inchi", icon: "images/inchi64.png"},
        {type: "text/uri-list", icon: "images/lnk64.png"},
        {type: "application/pdf", icon: "images/pdf64.png"},
        {type: "text/csv", icon: "images/csv64.png"},
        {type: "text/plain", icon: "images/txt64.png"},
        {type: "text/x-arff", icon: "images/arff.png"},
        {type: "text/x-arff-3col", icon: "images/arff-3.png"},
        {type: "application/rdf+xml", icon: "images/rdf64.png"},
        {type: "application/json", icon: "images/json64.png"}
      ],

      // These are instance-wide pre-definitions of default baseFeatures as described below.
      "baseFeatures": {
      	// and one for unified way of processing diagram. This can be used as template too
      	"http://www.opentox.org/api/1.1#Diagram": {
      	  title: "Diagram", 
      	  search: false,
        	visibility: "main",
        	primary: true,
      	  data: "compound.URI",
      	  column: { sClass: "paddingless", sWidth: "125px"},
      	  render: function(data, type, full) {
        	  dUri = jT.ui.diagramUri(data);
            return (type != "display") ? dUri : '<div class="jtox-diagram borderless"><span class="ui-icon ui-icon-zoomin"></span><a target="_blank" href="' + data + '"><img src="' + dUri + '" class="jtox-smalldiagram"/></a></div>';
          }
      	},
      	'#IdRow': {
      	  used: true,
      	  basic: true,
      	  data: "number",
      	  column: { "sClass": "middle"}
      	},
      	"#DetailedInfoRow": {
      	  title: "Diagram", 
      	  search: false,
      	  data: "compound.URI",
      	  basic: true,
      	  primary: true,
      	  column: { sClass: "jtox-hidden jtox-ds-details paddingless", sWidth: "0px"},
        	visibility: "none",
        	render: function(data, type, full) { return ''; }
      	},
      	
      	"http://www.opentox.org/api/1.1#Similarity": {title: "Similarity", data: "compound.metric", search: true},
      },
      "columns": {
        "compound": {
          "Name": { sTitle: "Name", mData: 'title', mRender: function (data, type, full) { return '<span>' + data + '</span>' + jT.ui.putInfo(full.URI); } },
          "Value": { sTitle: "Value", mData: 'value', sDefaultContent: "-" },
          "SameAs": { sTitle: "SameAs", mData: 'sameAs', sDefaultContent: "-" },
          "Source": { sTitle: "Source", mData: 'source', sDefaultContent: "-", mRender: function (data, type, full) { return !data || !data.type ? '-' : '<a target="_blank" href="' + data.URI + '">' + data.type + '</a>'; } }
        }
      }
    }
  };

  /* define the standard features-synonymes, working with 'sameAs' property. Beside the title we define the 'data' property
  as well which is used in processEntry() to location value(s) from given (synonym) properties into specific property of the compound entry itself.
  'data' can be an array, which results in adding value to several places.
  */
  var baseFeatures = {
    "http://www.opentox.org/api/1.1#REACHRegistrationDate" : { title: "REACH Date", data: "compound.reachdate", accumulate: true, basic: true },
    "http://www.opentox.org/api/1.1#CASRN" : { title: "CAS", data: "compound.cas", accumulate: true, basic: true, primary: true },
  	"http://www.opentox.org/api/1.1#ChemicalName" : { title: "Name", data: "compound.name", accumulate: true, basic: true },
  	"http://www.opentox.org/api/1.1#TradeName" : { title: "Trade Name", data: "compound.tradename", accumulate: true, basic: true },
  	"http://www.opentox.org/api/1.1#IUPACName": { title: "IUPAC Name", data: ["compound.name", "compound.iupac"], accumulate: true, basic: true },
  	"http://www.opentox.org/api/1.1#EINECS": { title: "EINECS", data: "compound.einecs", accumulate: true, basic: true, primary: true },
    "http://www.opentox.org/api/1.1#InChI": { title: "InChI", data: "compound.inchi", shorten: true, accumulate: true, basic: true },
  	"http://www.opentox.org/api/1.1#InChI_std": { title: "InChI", data: "compound.inchi", shorten: true, accumulate: true, sameAs: "http://www.opentox.org/api/1.1#InChI", basic: true },
    "http://www.opentox.org/api/1.1#InChIKey": { title: "InChI Key", data: "compound.inchikey", accumulate: true, basic: true },
  	"http://www.opentox.org/api/1.1#InChIKey_std": { title: "InChI Key", data: "compound.inchikey", accumulate: true, sameAs: "http://www.opentox.org/api/1.1#InChIKey", basic: true },
    "http://www.opentox.org/api/1.1#InChI_AuxInfo": { title: "InChI Aux", data: "compound.inchiaux", accumulate: true, basic: true },
  	"http://www.opentox.org/api/1.1#InChI_AuxInfo_std": { title: "InChI Aux", data: "compound.inchiaux", accumulate: true, sameAs: "http://www.opentox.org/api/1.1#InChI_AuxInfo", basic: true},
  	"http://www.opentox.org/api/1.1#IUCLID5_UUID": { title: "IUCLID5 UUID", data: "compound.i5uuid", shorten: true, accumulate: true, basic: true, primary: true },
  	"http://www.opentox.org/api/1.1#SMILES": { title: "SMILES", data: "compound.smiles", shorten: true, accumulate: true, basic: true },
  	"http://www.opentox.org/api/dblinks#CMS": { title: "CMS", accumulate: true, basic: true },
  	"http://www.opentox.org/api/dblinks#ChEBI": { title: "ChEBI", accumulate: true, basic: true },
  	"http://www.opentox.org/api/dblinks#Pubchem": { title: "PubChem", accumulate: true, basic: true },
  	"http://www.opentox.org/api/dblinks#ChemSpider": { title: "ChemSpider", accumulate: true, basic: true },
  	"http://www.opentox.org/api/dblinks#ChEMBL": { title: "ChEMBL", accumulate: true, basic: true },
  	"http://www.opentox.org/api/dblinks#ToxbankWiki": { title: "Toxbank Wiki", accumulate: true, basic: true },
  };
  var instanceCount = 0;

  // constructor
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even in manual initialization.
    
    var newDefs = jT.$.extend(true, { "configuration" : { "baseFeatures": baseFeatures} }, defaultSettings);
    self.settings = jT.$.extend(true, {}, newDefs, jT.settings, settings); // i.e. defaults from jToxCompound
    
    // make a dull copy here, because, otherwise groups are merged... which we DON'T want
    if (settings != null && settings.configuration != null && settings.configuration.groups != null)
      self.settings.configuration.groups = settings.configuration.groups;
      
    self.instanceNo = instanceCount++;
    if (self.settings.rememberChecks && self.settings.showTabs)
      self.featureStates = {};

    // finally make the query, if Uri is provided      
    if (self.settings['datasetUri'] != null){
      self.queryDataset(self.settings['datasetUri']);
    }
  };
  
  // now follow the prototypes of the instance functions.
  cls.prototype = {
    init: function () {
      var self = this;
      
      self.feature = null; // features, as downloaded from server, after being processed.
      self.dataset = null; // the last-downloaded dataset.
      self.groups = null; // computed groups, i.e. 'groupName' -> array of feature list, prepared.
      self.fixTable = self.varTable = null; // the two tables - to be initialized in prepareTables.
      self.entriesCount = null;
      self.suspendEqualization = false;
      self.orderList = [];
      self.usedFeatures = [];
      self.pageStart = self.settings.pageStart;
      self.pageSize = self.settings.pageSize;
      
      if (!self.settings.noInterface) {
        self.rootElement.appendChild(jT.getTemplate('#jtox-compound'));
        
        jT.ui.bindControls(self, { 
          nextPage: function () { self.nextPage(); },
          prevPage: function () { self.prevPage(); },
          sizeChange: function() { self.queryEntries(self.pageStart, parseInt(jT.$(this).val())); },
          filter: function () { self.updateTables() }
        });
      }
    },
    
    clearDataset: function () {
      if (this.usedFeatures !== undefined) {
        if (!this.settings.noInterface)     
          jT.$(this.rootElement).empty();
        for (var i = 0, fl = this.usedFeatures.length; i < fl; ++i) {
          var fid = this.usedFeatures[i];
          this.feature[fid].used = false;
        }
      }
    },
    
    /* make a tab-based widget with features and grouped on tabs. It relies on filled and processed 'self.feature' as well
    as created 'self.groups'.
    */
    prepareTabs: function (root, isMain, groupFn) {
      var self = this;
      
      var all = document.createElement('div');
      all.style.display = "none"; // we suppress the re-layouting engine this way, we'll show it at the end.
      root.appendChild(all);
      ulEl = document.createElement('ul');
      all.appendChild(ulEl);

      var createATab = function(grId, name) {
        var liEl = document.createElement('li');
        ulEl.appendChild(liEl);
        var aEl = document.createElement('a');
        aEl.href = "#" + grId;
        aEl.innerHTML = name;
        liEl.appendChild(aEl);
        return liEl;
      };
            
      var emptyList = [];
      var idx = 0;
      for (var gr in self.groups) {
        var grId = "jtox-ds-" + gr.replace(/\s/g, "_") + "-" + self.instanceNo + (isMain ? '' : '-details');
        var grName = gr.replace(/_/g, " ");
        var tabLi = createATab(grId, grName);
        if (isMain)
          tabLi.title = "Select which columns to be displayed";
        
        // now prepare the content...
        var divEl = document.createElement('div');
        divEl.id = grId;
        all.appendChild(divEl);
        // add the group check multi-change
        if (self.settings.groupSelection && isMain) {
          var sel = jT.getTemplate("#jtox-ds-selection");
          divEl.appendChild(sel);
          jT.$('.multi-select', sel).on('click', function (e) {
            var par = jT.$(this).closest('.ui-tabs-panel')[0];
            var doSel = jT.$(this).hasClass('select');
            $('input', par).each(function () {
              this.checked = doSel;
              jT.$(this).trigger('change');
            });
          });
        }
        
        if (groupFn(divEl, gr)) {
          if (self.settings.hideEmpty) {
            jT.$(divEl).remove();
            jT.$(tabLi).remove();
            --idx;
          }
          else
            emptyList.push(idx);
        }
        ++idx;
        
        ccLib.fireCallback(self.settings.onTab, self, divEl, tabLi, grName, isMain);
      }
      
      if (isMain && self.settings.showExport) {
        var tabId = "jtox-ds-export-" + self.instanceNo;
        var liEl = createATab(tabId, "Export");
        jT.$(liEl).addClass('jtox-ds-export');
        var divEl = jT.getTemplate('#jtox-ds-export')
        divEl.id = tabId;
        all.appendChild(divEl);
        divEl = jT.$('.jtox-exportlist', divEl)[0];
        
        for (var i = 0, elen = self.settings.configuration.exports.length; i < elen; ++i) {
          var expo = self.settings.configuration.exports[i];
          var el = jT.getTemplate('#jtox-ds-download');
          divEl.appendChild(el);
          
          jT.$('a', el)[0].href = ccLib.addParameter(self.datasetUri, "media=" + encodeURIComponent(expo.type));
          var img = el.getElementsByTagName('img')[0];
          img.alt = img.title = expo.type;
          img.src = (jT.settings.baseUrl || self.settings.baseUrl) + '/' + expo.icon;
        }
        
        ccLib.fireCallback(self.settings.onTab, self, divEl, liEl, "Export", isMain);
      }
      
      // now show the whole stuff and mark the disabled tabs
      all.style.display = "block";
      return jT.$(all).tabs({ 
        collapsible: isMain, 
        active: (self.settings.tabsFolded && isMain) ? false : 0, 
        disabled: emptyList, 
        heightStyle: isMain ? "content" : (self.settings.detailsHeight == 'auto' ? 'auto' : 'fill') 
      });
    },
    
    equalizeTables: function () {
      var self = this;
      if (!self.suspendEqualization && self.fixTable != null && self.varTable != null) {
        ccLib.equalizeHeights(self.fixTable.tHead, self.varTable.tHead);
        ccLib.equalizeHeights(self.fixTable.tBodies[0], self.varTable.tBodies[0]);

        // now we need to equalize openned details boxes, if any.
        var tabRoot = jT.$('.jtox-ds-tables', self.rootElement)[0];
        var varWidth = jT.$('.jtox-ds-variable', tabRoot).width();

        jT.$('.jtox-details-box', self.rootElement).each(function (i) {
          var cell = jT.$(this).data('rootCell');
          if (!!cell) {
            this.style.display = 'none';
            var ps = ccLib.positionTo(cell, tabRoot);
            this.style.top = ps.top + 'px';
            this.style.left = ps.left + 'px';
            var cellW = jT.$(cell).parents('.dataTables_wrapper').width() - cell.offsetLeft;
            this.style.width = (cellW + varWidth) + 'px';
            this.style.display = 'block';

            var rowH = jT.$(cell).parents('tr').height();
            var cellH = cell.offsetHeight;
            var meH = jT.$(this).height();
            
            if (cellH < rowH)
              jT.$(cell).height(cellH = rowH);
            if (cellH < meH)
              jT.$(cell).height(cellH = meH);
              
            if (meH < cellH) {
              jT.$(this).height(cellH);
              jT.$('.ui-tabs').tabs('refresh');
            }
          }
        });
      }
    },
    
    featureValue: function (fId, entry, type) {
      var self = this;
      var feature = self.feature[fId];
      var val = (feature.data !== undefined) ? (ccLib.getJsonValue(entry, jT.$.isArray(feature.data) ? feature.data[0] : feature.data)) : entry.values[fId];
      return (typeof feature.render == 'function') ? 
        feature.render(val, !!type ? type : 'filter', entry) : 
        jT.ui.renderObjValue(val, feature.units, type);
    },
    
    featureUri: function (fId) {
      return this.feature[fId].URI || fId;
    },
    
    featureData: function (entry, set, scope) {
      if (scope == null)
        scope = 'details';
      var self = this;
      var data = [];
      ccLib.enumObject(set, function (fId, idx, level) {
        var feat = jT.$.extend({}, self.feature[fId]);
        var vis = feat.visibility;
        if (!!vis && vis != scope)
          return;
        var title = feat.title;
        feat.value = self.featureValue(fId, entry, scope);
        if (!!title && (!self.settings.hideEmptyDetails || !!feat.value)) {
          if (!feat.value)
            feat.value = '-';
          data.push(feat);
        }
      });
      
      return data;
    },
    
    prepareColumn: function (fId, feature) {
      var self = this;
      if (feature.visibility == 'details')
        return null;
        
      // now we now we should show this one.
      var col = {
        "sTitle": !feature.title ? '' : jT.ui.valueWithUnits(feature.title.replace(/_/g, ' '), (!self.settings.showUnits ? null : feature.units)),
        "sDefaultContent": "-",
      };
      
      if (typeof feature.column == 'function')
        col = feature.column.call(self, col, fId);
      else if (!ccLib.isNull(feature.column))
        col = jT.$.extend(col, feature.column);
      
      col["mData"] = (feature.data != null) ? feature.data : 'values';

      if (feature.render != null)
        col["mRender"] = feature.data != null ? feature.render : function (data, type, full) { return feature.render(full.values[fId], type, full); };
      else if (!!feature.shorten)
        col["mRender"] = function(data, type, full) { 
          if (!feature.data) data = data[fId];
          return (type != "display") ? '' + data : jT.ui.shortenedData(data, "Press to copy the value in the clipboard"); 
        };
      else if (feature.data == null) // in other cases we want the default presenting of the plain value
        col["mRender"] = (function(featureId, units) { return function(data, type, full) {
          var val = full.values[featureId] || '-';
          if (typeof val != 'object')
            return val;
          if (!jT.$.isArray(val))
            val = [val];
          var html = '';
          for (var i = 0;i < val.length; ++i) {
            html += (type == 'display') ? '<div>' : '';
            html += jT.ui.renderObjValue(val[i], units, type);
            html += (type == 'display') ? '</div>' : ',';
          }
          return html;
        }; })(fId, feature.unit);
      
      // other convenient cases
      if (!!feature.shorten)
        col["sWidth"] = "75px";
  
      return col;
    },
    
    getVarRow: function (idx) {
	  	if (idx.tagName != null)
	  		idx = jT.ui.rowIndex(idx);
	  	
      return document.getElementById('jtox-var-' + this.instanceNo + '-' + idx);
    },
    
    prepareTables: function() {
      var self = this;
      var varCols = [];
      var fixCols = [];
      
      // first, some preparation of the first, IdRow column
      var idFeature = self.settings.configuration.baseFeatures['#IdRow'];
      if (!idFeature.render) {
        idFeature.render = self.settings.hasDetails ? 
          function (data, type, full) {
            return (type != "display") ?
              '' + data : 
              "&nbsp;-&nbsp;" + data + "&nbsp;-&nbsp;<br/>" + 
              '<span class="jtox-details-open ui-icon ui-icon-folder-collapsed" title="Press to open/close detailed info for this compound"></span>';
          } : // no details case
          function (data, type, full) { 
            return (type != "display") ?
              '' + data : 
              "&nbsp;-&nbsp;" + data + "&nbsp;-&nbsp;";
          };
      }
      
      fixCols.push(
        self.prepareColumn('#IdRow', idFeature),
        { "sClass": "jtox-hidden", "mData": "index", "sDefaultContent": "-", "bSortable": true, "mRender": function(data, type, full) { return ccLib.isNull(self.orderList) ? 0 : self.orderList[data]; } } // column used for ordering
      );
      
      varCols.push({ "sClass": "jtox-hidden jtox-ds-details paddingless", "mData": "index", "mRender": function(data, type, full) { return ''; }  });

      // prepare the function for column switching...      
      var fnShowColumn = function() {
        var dt = $(this).data();
        var cells = jT.$(dt.sel + ' table tr .' + dt.column, self.rootElement);
        if (this.checked) {
          jT.$(cells).show();
          jT.$("table tr .blank-col", self.rootElement).addClass('jtox-hidden');
        }
        else {
          jT.$(cells).hide();
          if (jT.$(dt.sel + " table tr *:visible", self.rootElement).length == 0)
            jT.$("table tr .blank-col", self.rootElement).removeClass('jtox-hidden');
        }
        if (self.settings.rememberChecks)
          self.featureStates[dt.id] = this.checked;
        self.equalizeTables();
      };
      
      var fnExpandCell = function (cell, expand) {
        var cnt = 0;
        for (var c = cell;c; c = c.nextElementSibling, ++cnt)
          jT.$(c).toggleClass('jtox-hidden');

        if (expand)
          cell.setAttribute('colspan', '' + cnt);
        else 
          cell.removeAttribute('colspan');
      };
      
      var fnShowDetails = (self.settings.hasDetails ? function(row, event) {
        var cell = jT.$(".jtox-ds-details", row)[0];
        var idx = jT.$(row).data('jtox-index');
        
        if (self.settings.preDetails != null && !ccLib.fireCallback(self.settings.preDetails, self, idx, cell) || !cell)
          return; // the !cell  means you've forgotten to add #DetailedInfoRow feature somewhere.
        jT.$(row).toggleClass('jtox-detailed-row');
        var toShow = jT.$(row).hasClass('jtox-detailed-row');

        // now go and expand both fixed and variable table details' cells.
        fnExpandCell(cell, toShow);
        var varCell = self.getVarRow(idx).firstElementChild;
        fnExpandCell(varCell, toShow);
        
        jT.$('.jtox-details-open', row).toggleClass('ui-icon-folder-open ui-icon-folder-collapsed');
        
        if (toShow) {
          // i.e. we need to show it - put the full sized diagram in the fixed part and the tabs in the variable one...
          var entry = self.dataset.dataEntry[idx];

          var detDiv = document.createElement('div');
          detDiv.className = 'jtox-details-box jtox-details';

          var tabRoot = jT.$('.jtox-ds-tables', self.rootElement)[0];
          var width = jT.$(cell).width() + jT.$('.jtox-ds-variable', tabRoot).width();
          jT.$(detDiv).width(width);

          if (ccLib.isNull(self.settings.detailsHeight) || self.settings.detailsHeight == 'fill')
            jT.$(detDiv).height(jT.$(cell).height() * 2);
          else if (parseInt(self.settings.detailsHeight) > 0)
            jT.$(detDiv).height(self.settings.detailsHeight)

          tabRoot.appendChild(detDiv);
          
          self.prepareTabs(detDiv, false, function (parent, gr) {
            var data = self.featureData(entry, self.groups[gr]);
            
            if (data.length > 0 || !self.settings.hideEmpty) {
              jT.$(parent).addClass('jtox-details-table');
              var tabTable = document.createElement('table');
              parent.appendChild(tabTable);
              jT.$(tabTable).dataTable({
                "bPaginate": false,
                "bProcessing": true,
                "bLengthChange": false,
        				"bAutoWidth": true,
                "sDom" : "rt<f>",
                "aoColumns": jT.ui.processColumns(self, 'compound'),
                "bSort": true,
              });
              jT.$(tabTable).dataTable().fnAddData(data);
              jT.$(tabTable).dataTable().fnAdjustColumnSizing();
            }
            
            return data.length == 0;
          });
          
          jT.$(cell).height(detDiv.offsetHeight);
          ccLib.fireCallback(self.settings.onDetails, self, detDiv, entry, cell);
          jT.$(cell).data('detailsDiv', detDiv);
          jT.$(detDiv).data('rootCell', cell);
        }
        else {
          // i.e. we need to hide
          jT.$(cell).data('detailsDiv').remove();
          jT.$(cell).data('detailsDiv', null);
          cell.style.height = '';
        }
        
        self.equalizeTables();
      } : null); // fnShowDetails definition end

      // now proceed to enter all other columns
      for (var gr in self.groups) {
        ccLib.enumObject(self.groups[gr], function (fId, idx, level) {
          if (idx == "name")
            return;
            
          var feature = self.feature[fId];
          var col = self.prepareColumn(fId, feature);
          if (!col)
            return;
          
          // finally - assign column switching to the checkbox of main tab.
          var colList = !!feature.primary ? fixCols : varCols;
          var colId = 'col-' + colList.length;
          col.sClass = (!!col.sClass ? col.sClass + ' ' : '') + colId;

          jT.$('.jtox-ds-features input.jtox-checkbox[value="' + fId + '"]', self.rootElement).data({ sel: !!feature.primary ? '.jtox-ds-fixed' : '.jtox-ds-variable', column: colId, id: fId}).on('change', fnShowColumn)
          
          // and push it into the proper list.
          colList.push(col);
        });
      }
      
      // now - sort columns and create the tables...
      if (self.settings.fixedWidth != null)
      	jT.$(".jtox-ds-fixed", self.rootElement).width(self.settings.fixedWidth);
      	
      jT.ui.sortColDefs(fixCols);
      self.fixTable = (jT.$(".jtox-ds-fixed table", self.rootElement).dataTable({
        "bPaginate": false,
        "bLengthChange": false,
				"bAutoWidth": true,
        "sDom" : "rt",
        "aoColumns": fixCols,
        "bSort": false,
        "fnCreatedRow": function( nRow, aData, iDataIndex ) {
          // attach the click handling
          if (self.settings.hasDetails)
            jT.$('.jtox-details-open', nRow).on('click', function(e) { fnShowDetails(nRow, e); });
          jT.$(nRow).data('jtox-index', iDataIndex);

          ccLib.fireCallback(self.settings.onRow, self, nRow, aData, iDataIndex);
          jT.ui.installHandlers(self, nRow);
          jT.$('.jtox-diagram span.ui-icon', nRow).on('click', function () {
            setTimeout(function () {
              jT.$(self.fixTable).dataTable().fnAdjustColumnSizing();
              self.equalizeTables();
            }, 50);
          });
        },
        "oLanguage" : { "sEmptyTable": '<span class="jt-feeding">' + (self.settings.oLanguage.sProcess || 'Feeding data...') + '</span>' }
      }))[0];

      // we need to put a fake column to stay, when there is no other column here, or when everything is hidden..
      varCols.push({ "sClass": "center blank-col" + (varCols.length > 1 ? " jtox-hidden" : ""), "mData": "index", "mRender": function(data, type, full) { return type != 'display' ? data : '...'; }  });

      jT.ui.sortColDefs(varCols);
      self.varTable = (jT.$(".jtox-ds-variable table", self.rootElement).dataTable({
        "bPaginate": false,
        "bProcessing": true,
        "bLengthChange": false,
				"bAutoWidth": false,
        "sDom" : "rt",
        "bSort": true,
        "aoColumns": varCols,
        "bScrollCollapse": true,
        "fnCreatedRow": function( nRow, aData, iDataIndex ) {
          nRow.id = 'jtox-var-' + self.instanceNo + '-' + iDataIndex;

	        // equalize multi-rows, if there are any
	        ccLib.equalizeHeights.apply(window, jT.$('td.jtox-multi table tbody', nRow).toArray());
          
          jT.$(nRow).addClass('jtox-row');
          jT.$(nRow).data('jtox-index', iDataIndex);
          ccLib.fireCallback(self.settings.onRow, self, nRow, aData, iDataIndex);
          jT.ui.installHandlers(self, nRow);
        },
        "fnDrawCallback": function(oSettings) {
          // this is for synchro-sorting the two tables
          var sorted = jT.$('.jtox-row', this);
          for (var i = 0, rlen = sorted.length;i < rlen; ++i) {
            var idx = jT.$(sorted[i]).data('jtox-index');
            self.orderList[idx] = i;
          }
          
          if (rlen > 0)
            jT.$(self.fixTable).dataTable().fnSort([[1, "asc"]]);
        },
        "oLanguage" : { "sEmptyTable": "-"}
      }))[0];
    },

    updateTables: function() {
      var self = this;
      if (self.settings.hasDetails) 
        $('div.jtox-details-box', self.rootElement).remove();

      self.filterEntries(jT.$('.jtox-controls input', self.rootElement).val());
    },
    
    /* Prepare the groups and the features.
    */
    prepareGroups: function (miniset) {
      var self = this;
      
      var grps = self.settings.configuration.groups;
      if (typeof grps == 'function')
        grps = grps(miniset, self);
        
      self.groups = {};
      for (var i in grps){
        var grp = grps[i];
        if (ccLib.isNull(grp))
          continue;
          
        var grpArr = (typeof grp == "function" || typeof grp == "string") ? ccLib.fireCallback(grp, self, i, miniset) : grp;
        self.groups[i] = [];
        ccLib.enumObject(grpArr, function(fid, idx) {
          var isUsed = false;
          cls.enumSameAs(fid, self.feature, function (feature) {
            isUsed |= feature.used;
          });
          if (idx != "name" && !isUsed) {
            self.groups[i].push(fid);
            // these we need to be able to return back to original state.
            self.usedFeatures.push(fid);
            self.feature[fid].used = true;
          }
        });
      }
    },
    
    /* Enumerate all recofnized features, caling fnMatch(featureId, groupId) for each of it. 
    Return true from the function to stop enumeration, which in turn will return true if it was stopped.
    */
    enumerateFeatures: function(fnMatch) {
      var self = this;
      var stopped = false;
      for (var gr in self.groups) {
        for (var i = 0, glen = self.groups[gr].length;i < glen; ++i) {
          if (stopped = fnMatch(self.groups[gr][i], gr))
            break;
        }
        
        if (stopped)
          break;
      }
      
      return stopped;
    },
    
    filterEntries: function(needle) {
      var self = this;
      
      if (ccLib.isNull(needle))
        needle = '';
      else
        needle = needle.toLowerCase();
        
      var dataFeed = [];
      if (needle != '') {
        for (var i = 0, slen = self.dataset.dataEntry.length;i < slen; ++i){
          var entry = self.dataset.dataEntry[i];
  
          var match = self.enumerateFeatures(function(fId, gr){
            var feat = self.feature[fId];
            if (feat.search !== undefined && !feat.search)
              return false;
            var val = self.featureValue(fId, entry, 'sort');
            return !ccLib.isNull(val) && val.toString().toLowerCase().indexOf(needle) >= 0;
          });
          
            
          if (match)
            dataFeed.push(entry);
        }
      }
      else {
        dataFeed = self.dataset.dataEntry;
      }
      
      jT.$(self.fixTable).dataTable().fnClearTable();
      jT.$(self.varTable).dataTable().fnClearTable();
      jT.$(self.fixTable).dataTable().fnAddData(dataFeed);
      jT.$(self.varTable).dataTable().fnAddData(dataFeed);
      jT.$('.jt-feeding', self.rootElement).html(self.settings.oLanguage.sZeroRecords || 'No records matching the filter.');

      ccLib.fillTree(jT.$('.jtox-controls', self.rootElement)[0], {
        "filtered-text": !needle ? " " : ' (filtered to <span class="high">' + dataFeed.length + '</span>) '
      });
      
      if (self.settings.showTabs){
        self.suspendEqualization = true;
        jT.$('.jtox-ds-features .jtox-checkbox', self.rootElement).trigger('change');     
        self.suspendEqualization = false;
      }
      
      // finally
      self.equalizeTables();
    },
    
    // These two are shortcuts for calling the queryEntries routine
    nextPage: function() {
      var self = this;
      if (self.entriesCount == null || self.pageStart + self.pageSize < self.entriesCount)
        self.queryEntries(self.pageStart + self.pageSize);
    },
    
    prevPage: function() {
      var self = this;
      if (self.pageStart > 0)
        self.queryEntries(self.pageStart - self.pageSize);
    },
    
    updateControls: function (qStart, qSize) {
      var self = this;
      var pane = jT.$('.jtox-controls', self.rootElement)[0];
      ccLib.fillTree(pane, {
        "pagestart": qSize > 0 ? qStart + 1 : 0,
        "pageend": qStart + qSize
      });
      
      var nextBut = jT.$('.next-field', pane);
      if (self.entriesCount == null || qStart + qSize < self.entriesCount)
        jT.$(nextBut).addClass('paginate_enabled_next').removeClass('paginate_disabled_next');
      else
        jT.$(nextBut).addClass('paginate_disabled_next').removeClass('paginate_enabled_next');
        
      var prevBut = jT.$('.prev-field', pane);
      if (qStart > 0)
        jT.$(prevBut).addClass('paginate_enabled_previous').removeClass('paginate_disabled_previous');
      else
        jT.$(prevBut).addClass('paginate_disabled_previous').removeClass('paginate_enabled_previous');
    },
    
    queryUri: function (scope) {
      var self = this;
      if (self.datasetUri == null)
        return null;
      if (scope == null)
        scope = { from: self.pageStart, size: self.pageSize };
      if (scope.from < 0)
        scope.from = 0;
      if (scope.size == null)
        scope.size = self.pageSize;
        
      return ccLib.addParameter(self.datasetUri, "page=" + Math.floor(scope.from / scope.size) + "&pagesize=" + scope.size);
    },
    
    // make the actual query for the (next) portion of data.
    queryEntries: function(from, size, dataset) {
      var self = this;
      var scope = { 'from': from, 'size': size };
      var qUri = self.queryUri(scope);
      jT.$('.jtox-controls select', self.rootElement).val(scope.size);
      self.dataset = null;

      // the function for filling
      var fillFn = function(dataset) {
        if (!!dataset){
          // first, arrange the page markers
          self.pageSize = scope.size;
          var qStart = self.pageStart = Math.floor(scope.from / scope.size) * scope.size;
          if (dataset.dataEntry.length < self.pageSize) // we've reached the end!!
            self.entriesCount = qStart + dataset.dataEntry.length;
          
          // then process the dataset
          self.dataset = cls.processDataset(dataset, $.extend(true, {}, dataset.feature, self.feature), self.settings.fnAccumulate, self.pageStart);
          // time to call the supplied function, if any.
          ccLib.fireCallback(self.settings.onLoaded, self, dataset);
          if (!self.settings.noInterface) {
            // ok - go and update the table, filtering the entries, if needed            
            self.updateTables();
            if (self.settings.showControls)
              // finally - go and update controls if they are visible
              self.updateControls(qStart, dataset.dataEntry.length);
          }
        }
        else
          ccLib.fireCallback(self.settings.onLoaded, self, dataset);
      };
  
      // we may be passed dataset, if the initial, setup query was 404: Not Found - to avoid second such query...
      if (dataset != null)
        fillFn(dataset)
      else 
        jT.call(self, qUri, fillFn);
    },
    
    /* Retrieve features for provided dataserUri, using settings.featureUri, if provided, or making automatice page=0&pagesize=1 query
      */
    queryFeatures: function (featureUri, callback) {
      var self = this;
      var dataset = null;

      // first, build the proper
      var queryUri = featureUri || self.settings.featureUri || self.settings.feature_uri;
      if (!queryUri) // rollback this is the automatic way, which makes a false query in the beginning
        queryUri = ccLib.addParameter(self.datasetUri, "page=0&pagesize=1");

      // now make the actual call...
      jT.call(self, queryUri, function (result, jhr) {
        if (!result && jhr.status == 404) {
          dataset = { feature: {}, dataEntry: [] }; // an empty set, to make it show the table...
        }
        
        // remove the loading pane in anyways..
        if (!!result) {
          self.feature = result.feature;

          cls.processFeatures(self.feature, self.settings.configuration.baseFeatures);
          var miniset = { dataEntry: [], feature: self.feature };
          if (!self.settings.noInterface) {
            self.prepareGroups(miniset);
            if (self.settings.showTabs) {
              // tabs feature building
              var nodeFn = function (id, name, parent) {
                var fEl = jT.getTemplate('#jtox-ds-feature');
                parent.appendChild(fEl);
                ccLib.fillTree(fEl, {title: name.replace(/_/g, ' '), uri: self.featureUri(id)});
          
                var checkEl = jT.$('input[type="checkbox"]', fEl)[0];
                if (!checkEl)
                  return;
                checkEl.value = id;
                if (self.settings.rememberChecks)
                  checkEl.checked = (self.featureStates[id] === undefined || self.featureStates[id]);
          
                return fEl;
              };
  
              self.prepareTabs(jT.$('.jtox-ds-features', self.rootElement)[0], true, function (divEl, gr) {
                var empty = true;
                ccLib.enumObject(self.groups[gr], function (fId, idx, level) {
                  var vis = (self.feature[fId] || {})['visibility'];
                  if (!!vis && vis != 'main')
                    return;
                  empty = false;
                  if (idx == "name")
                    var fEl = nodeFn(null, fId, divEl);
                  else if (level == 1) {
                    var title = self.feature[fId].title;
                    if (!ccLib.isNull(title))
                      nodeFn(fId, title, divEl);
                  }
                });
                
                return empty;
              });
            }
            
          	ccLib.fireCallback(self.settings.onPrepared, self, miniset, self);
            self.prepareTables(); // prepare the tables - we need features to build them - we have them!
            self.equalizeTables(); // to make them nicer, while waiting...
          }
          else
          	ccLib.fireCallback(self.settings.onPrepared, self, miniset, self);
  
          // finally make the callback for
          callback(dataset);
        }
      });
    },
    
    /* Makes a query to the server for particular dataset, asking for feature list first, so that the table(s) can be 
    prepared.
    */
    queryDataset: function (datasetUri, featureUri) {
      var self = this;
      // if some oldies exist...
      this.clearDataset();
      this.init();

      datasetUri = jT.grabPaging(self, datasetUri);

      self.settings.baseUrl = self.settings.baseUrl || jT.grabBaseUrl(datasetUri);
      
      // remember the _original_ datasetUri and make a call with one size length to retrieve all features...
      self.datasetUri = (datasetUri.indexOf('http') !=0 ? self.settings.baseUrl : '') + datasetUri;
      
      var procDiv = jT.$('.jt-processing', self.rootElement).show()[0];
      if (!!self.settings.oLanguage.sLoadingRecords)
        jT.$('.message', procDiv).html(self.settings.oLanguage.sLoadingRecords);
      
      self.queryFeatures(featureUri, function (dataset) {
        jT.$(procDiv).hide();
        self.queryEntries(self.pageStart, self.pageSize, dataset);
      });
    },
    
    /* This is a needed shortcut that jToxQuery routine will call
    */
    query: function (uri) {
      this.queryDataset(uri);
    }    
  }; // end of prototype
  
  // merge them for future use..
  cls.baseFeatures = jT.$.extend({}, baseFeatures, defaultSettings.configuration.baseFeatures);
  
  // some public, static methods
  cls.processEntry = function (entry, features, fnValue) {
    if (!fnValue)
      fnValue = defaultSettings.fnAccumulate;
    
    for (var fid in entry.values) {
      var feature = features[fid];
      if (!feature)
        continue;
      var newVal = entry.values[fid];
      
      // if applicable - location the feature value to a specific location whithin the entry
      if (!!feature.accumulate && !!newVal && !!feature.data) {
        var fn = typeof feature.accumulate == 'function' ? feature.accumulate : fnValue;
        var accArr = feature.data;
        if (!jT.$.isArray(accArr))
          accArr = [accArr];
        
        for (var v = 0; v < accArr.length; ++v)
          ccLib.setJsonValue(entry, accArr[v], ccLib.fireCallback(fn, this, fid,  /* oldVal */ ccLib.getJsonValue(entry, accArr[v]), newVal, features));
      }
    }
    
    return entry;
  };
  
  cls.extractFeatures = function (entry, features, callback) {
    var data = [];
    for (var fId in features) {
      var feat = jT.$.extend({}, features[fId]);
      feat.value = entry.values[fId];
      if (!!feat.title) {
        if (ccLib.fireCallback(callback, null, feat, fId) !== false) {
          if (!feat.value)
            feat.value = '-';
          data.push(feat);
        }
      }
    };
    
    return data;
  };
  
  cls.enumSameAs = function (fid, features, callback) {
    // starting from the feature itself move to 'sameAs'-referred features, until sameAs is missing or points to itself
    // This, final feature should be considered "main" and title and others taken from it.
    var feature = features[fid];
    var base = fid.replace(/(http.+\/feature\/).*/g, "$1");
    var retId = fid;
    
    for (;;){
      ccLib.fireCallback(callback, null, feature, retId);
      if (feature.sameAs == null || feature.sameAs == fid || fid == base + feature.sameAs)
        break;
      if (features[feature.sameAs] !== undefined)
        retId = feature.sameAs;
      else {
        if (features[base + feature.sameAs] !== undefined)
          retId = base + feature.sameAs;
        else
          break;
      }
      
      feature = features[retId];
    }
    
    return retId;
  };
  
  cls.processFeatures = function(features, bases) {
    if (bases == null)
      bases = baseFeatures;
    features = jT.$.extend(features, bases);
    
    for (var fid in features) {
      var theFeat = features[fid];
      if (!theFeat.URI)
        theFeat.URI = fid;
      cls.enumSameAs(fid, features, function (feature, id) {
        var sameAs = feature.sameAs;
        feature = jT.$.extend(true, feature, theFeat);
        theFeat = jT.$.extend(true, theFeat, feature);
        feature.sameAs = sameAs;
      });
    }
    
    return features;
  };
  
  cls.processDataset = function(dataset, features, fnValue, startIdx) {
    if (!features) {
      cls.processFeatures(dataset.feature);
      features = dataset.feature;
    }

    if (!fnValue)
      fnValue = defaultSettings.fnAccumulate;
    
    if (!startIdx)
      startIdx = 0;
      
    for (var i = 0, dl = dataset.dataEntry.length; i < dl; ++i) {
      cls.processEntry(dataset.dataEntry[i], features, fnValue);
      dataset.dataEntry[i].number = i + 1 + startIdx;
      dataset.dataEntry[i].index = i;
    }
    
    return dataset;
  };
  
  return cls;
})();
/* toxdataset.js - A kit for querying/manipulating all available datasets
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxDataset = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    shortStars: false,        // whether to show a star and number (short star) or maxStars with actual number highlighted.
    maxStars: 10,             // the maximal number of stars when in longStars mode
    selectionHandler: null,   // selection handler to be attached on checkbox, for jToxQuery integration
    noInterface: false,       // run in interface-less mode, with data retrieval and callback calling only
    sDom: "<Fif>rt",          // passed with dataTable settings upon creation
    onLoaded: null,           // callback called when the is available
    loadOnInit: false,        // whether to make an (empty) call when initialized.
    oLanguage: {
      "sLoadingRecords": "Loading dataset list.",
      "sZeroRecords": "No datasets found.",
      "sEmptyTable": "No datasets available.",
      "sInfo": "Showing _TOTAL_ dataset(s) (_START_ to _END_)"
    },
    /* datasetUri */
    configuration: { 
      columns : {
        dataset: {
          'Id': { iOrder: 0, sTitle: "Id", mData: "URI", sWidth: "50px", mRender: function (data, type, full) {
            var num = parseInt(data.match(/https{0,1}:\/\/.*\/dataset\/(\d+).*/)[1]);
            if (type != 'display')
              return num;
            return '<a target="_blank" href="' + data + '/metadata"><span class="ui-icon ui-icon-link jtox-inline"></span> D' + num + '</a>';
          }},
          'Title': { iOrder: 1, sTitle: "Title", mData: "title", sDefaultContent: "-" },
          'Stars': { iOrder: 2, sTitle: "Stars", mData: "stars", sWidth: "160px" },
          'Info': { iOrder: 3, sTitle: "Info", mData: "rights", mRender: function (data, type, full) {
            return '<a target="_blank" href="' + data.URI + '">rights</a>';
          } }
        }
      }
    }
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized

    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);
        
    if (!self.settings.noInterface) {
      self.rootElement.appendChild(jT.getTemplate('#jtox-dataset'));
      self.init(settings);
    }
        
    // finally, wait a bit for everyone to get initialized and make a call, if asked to
    if (self.settings.datasetUri != undefined || self.settings.loadOnInit)
      self.listDatasets(self.settings.datasetUri)
  };
  
  cls.prototype = {
    init: function (settings) {
      var self = this;
      
      // arrange certain things on the columns first - like dealing with short/long stars
      self.settings.configuration.columns.dataset.Stars.mRender = function (data, type, full) {
        return type != 'display' ? data : jT.ui.putStars(self, data, "Dataset quality stars rating (worst) 1-10 (best)");
      };
      
      if (self.settings.shortStars)
        self.settings.configuration.columns.dataset.Stars.sWidth = "40px";
      
      // deal if the selection is chosen
      if (!!self.settings.selectionHandler || !!self.settings.onDetails) {
        jT.ui.putActions(self, self.settings.configuration.columns.dataset.Id);
        self.settings.configuration.columns.dataset.Id.sWidth = "60px";
      }
      
      // again , so that changed defaults can be taken into account.
      self.settings.configuration = jT.$.extend(true, self.settings.configuration, settings.configuration);
      
      // READYY! Go and prepare THE table.
      self.table = jT.ui.putTable(self, jT.$('table', self.rootElement)[0], 'dataset');
    },
    
    listDatasets: function (uri) {
      var self = this;
      if (uri == null)
        uri = self.settings.baseUrl + '/dataset';
      else if (!self.settings.baseUrl)
        self.settings.baseUrl = jT.grabBaseUrl(uri);
      
      if (!self.settings.noInterface)
        jT.$(self.table).dataTable().fnClearTable();
      jT.call(self, uri, function (result, jhr) {
        if (!result && jhr.status == 404)
          result = { dataset: [] }; // empty one...
        if (!!result) {
          self.dataset = result.dataset;
          ccLib.fireCallback(self.settings.onLoaded, self, result);
          if (!self.settings.noInterface)
            jT.$(self.table).dataTable().fnAddData(result.dataset);
        }
        else {
          self.dataset = null;
          ccLib.fireCallback(self.settings.onLoaded, self, result);
        }
      });
    },
    
    query: function (uri) {
      this.listDatasets(uri);
    },
    
    modifyUri: function (uri) {
      jT.$('input[type="checkbox"]', this.rootElement).each(function () {
        if (this.checked)
          uri = ccLib.addParameter(uri, 'feature_uris[]=' + encodeURIComponent(this.value + '/feature'));
      })
      
      return uri;
    }
  };
    
  return cls;
})();
/* toxmodel.js - A kit for querying/manipulating all available models (algorithms)
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxModel = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    shortStars: false,        // whether to show a star and a number (short version) or all maxStars with actual number of them highlighed
    maxStars: 10,             // a total possible number of stars - see above description
    noInterface: false,       // run in interface-less mode - only data retrieval and callback calling.
    selectionHandler: null,   // jToxQuery handler to be attached on each entry's checkbox
    algorithmLink: true,      // when showing algorithms, whether to make it's id a link to an (external) full info page
    algorithms: false,        // ask for algorithms, not models
    forceCreate: false,       // upon creating a model from algorithm - whether to attempt getting a prepared one, or always create it new
    onLoaded: null,           // callback to be called when data has arrived.
    sDom: "<Fif>rt",          // merged to dataTable's settings, when created
    loadOnInit: false,        // whether to make a (blank) request upon loading
    oLanguage: null,          // merged to dataTable's settings, when created
    /* algorithmNeedle */
    /* modelUri */
    configuration: { 
      columns : {
        model: {
          'Id': { iOrder: 0, sTitle: "Id", mData: "URI", sWidth: "50px", mRender: function (data, type, full) {
            return (type != 'display') ? full.id : '<a target="_blank" href="' + data + '"><span class="ui-icon ui-icon-link jtox-inline"></span> M' + full.id + '</a>';
          }},
          'Title': { iOrder: 1, sTitle: "Title", mData: "title", sDefaultContent: "-" },
          'Stars': { iOrder: 2, sTitle: "Stars", mData: "stars", sWidth: "160px" },
          'Algorithm': {iOrder: 3, sTitle: "Algorithm", mData: "algorithm" },
          'Info': { iOrder: 4, sTitle: "Info", mData: "trainingDataset", mRender: function (data, type, full) {
            return (type != 'display' || !data) ? data : '<a href="' + data + '"><span class="ui-icon ui-icon-calculator"></span>&nbsp;training set</a>';
          } }
        },
        algorithm: {
          'Id': { iOrder: 0, sTitle: "Id", mData: "uri", sWidth: "150px", mRender: function (data, type, full) {
            return (type != 'display') ? full.id : '<a target="_blank" href="' + data + '"><span class="ui-icon ui-icon-link jtox-inline"></span> ' + full.id + '</a>';
          }},
          'Title': { iOrder: 1, sTitle: "Title", mData: "name", sDefaultContent: "-" },
          'Description': {iOrder: 2, sTitle: "Description", sClass: "shortened", mData: "description", sDefaultContent: '-' },
          'Info': { iOrder: 3, sTitle: "Info", mData: "format", mRender: function (data, type, full) {
            if (type != 'display' || !data)
              return data;
            return  '<strong>' + data + '</strong>; ' +
                    (full.isSupevised ? '<strong>Supervised</strong>; ' : '') +
                    '<a target="_blank" href="' + full.implementationOf + '">' + full.implementationOf + '</a>';
          } }
        }
      }
    }
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized
    
    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);
    self.models = null;
    
    if (!self.settings.noInterface) {
      self.rootElement.appendChild(jT.getTemplate('#jtox-model'));
      self.init(settings);
    }
        
    // finally, wait a bit for everyone to get initialized and make a call, if asked to
    if (self.settings.modelUri != null || self.settings.algorithmNeedle != null || self.settings.loadOnInit)
      self.query();
  };
  
  cls.prototype = {
    init: function (settings) {
      var self = this;
      
      // but before given it up - make a small sorting..
      if (!self.settings.algorithms) {
        self.settings.configuration.columns.model.Stars.mRender = function (data, type, full) { return type != 'display' ? data : jT.ui.putStars(self, data, "Model star rating (worst) 1 - 10 (best)"); };
        if (self.settings.shortStars)
          self.settings.configuration.columns.model.Stars.sWidth = "40px";

        self.settings.configuration.columns.model.Algorithm.mRender = function (data, type, full) { 
          var name = data.URI.match(/https{0,1}:\/\/.*\/algorithm\/(\w+).*/)[1];
          if (type != 'display')
            return name;
          var res = '<a target="_blank" href="' + data.URI + '">' + 
                    '<img src="' + (self.settings.baseUrl || jT.settings.baseUrl) + data.img + '"/>&nbsp;' +
                    name + 
                    '</a>';
          if (self.settings.algorithmLink) {
            res += '<a href="' + ccLib.addParameter(self.modelUri, 'algorithm=' + encodeURIComponent(data.URI)) + '"><span class="ui-icon ui-icon-calculator float-right" title="Show models using algorithm ' + name + '"></span></a>';
          }
  
          return res;
        };
      }
      
      var cat = self.settings.algorithms ? 'algorithm' : 'model';
      // deal if the selection is chosen
      if (!!self.settings.selectionHandler || !!self.settings.onDetails) {
        jT.ui.putActions(self, self.settings.configuration.columns[cat].Id);
        self.settings.configuration.columns[cat].Id.sWidth = "60px";
      }
      
      // again , so that changed defaults can be taken into account.
      self.settings.configuration = jT.$.extend(true, self.settings.configuration, settings.configuration);
      if (self.settings.oLanguage == null)
        self.settings.oLanguage = (self.settings.algorithms ? {
          "sLoadingRecords": "No algorithms found.",
          "sZeroRecords": "No algorithms found.",
          "sEmptyTable": "No algorithmss available.",
          "sInfo": "Showing _TOTAL_ algorithm(s) (_START_ to _END_)"
        } : {
          "sLoadingRecords": "No models found.",
          "sZeroRecords": "No models found.",
          "sEmptyTable": "No models available.",
          "sInfo": "Showing _TOTAL_ model(s) (_START_ to _END_)"
        });
      
      // READYY! Go and prepare THE table.
      self.table = jT.ui.putTable(self, $('table', self.rootElement)[0], cat);
    },
    
    listModels: function (uri) {
      var self = this;
      if (uri == null)
        uri = self.settings.baseUrl + '/model';
      else if (!self.settings.baseUrl)
        self.settings.baseUrl = jT.grabBaseUrl(uri);

      self.modelUri = uri;
      if (!self.settings.noInterface)
        jT.$(self.table).dataTable().fnClearTable();
      jT.call(self, uri, function (result, jhr) {
        if (!result && jhr.status == 404)
          result = { model: [] }; // empty one
          
        if (!!result) {
          self.models = result.model;
          ccLib.fireCallback(self.settings.onLoaded, self, result);
          if (!self.settings.noInterface)
            jT.$(self.table).dataTable().fnAddData(result.model);
        }
        else
          ccLib.fireCallback(self.settings.onLoaded, self, result);
      });
    },
    
    /* List algorithms that contain given 'needle' in their name
    */
    listAlgorithms: function (needle) {
      var self = this;
      var uri = self.settings.baseUrl + '/algorithm';
      if (!!needle)
        uri = ccLib.addParameter(uri, 'search=' + needle);
      if (!self.settings.noInterface)
        jT.$(self.table).dataTable().fnClearTable();
      jT.call(self, uri, function (result, jhr) {
        if (!result && jhr.status == 404)
          result = { algorithm: [] }; // empty one
        if (!!result) {
          self.algorithm = result.algorithm;
          ccLib.fireCallback(self.settings.onLoaded, self, result);
          if (!self.settings.noInterface)
            jT.$(self.table).dataTable().fnAddData(result.algorithm);
        }
        else
          ccLib.fireCallback(self.settings.onLoaded, self, result);
      });
    },
    
    getModel: function (algoUri, callback) {
      var self = this;
      var createIt = function () {
        jT.service(self, algoUri, { method: 'POST' }, function (result, jhr) {
          ccLib.fireCallback(callback, self, (!task.error ? task.result : null), jhr);
        });
      };
      
      if (self.settings.forceCreate)
        createIt();
      else 
        jT.call(self, self.settings.baseUrl + '/model?algorithm=' + encodeURIComponent(algoUri), function (result, jhr) {
          if (!result && jhr.status != 404)
            ccLib.fireCallback(callback, self, null, jhr);
          else if (!result || result.model.length == 0)
            createIt();
          else // we have it!
            ccLib.fireCallback(callback, self, result.model[0].URI, jhr);
        });
    },
    
    runPrediction: function (datasetUri, modelUri, callback) {
      var self = this;
      var q = ccLib.addParameter(datasetUri, 'feature_uris[]=' + encodeURIComponent(modelUri + '/predicted'));

      var createIt = function () {
        jT.service(self, modelUri, { method: "POST", data: { dataset_uri: datasetUri } }, function (task, jhr) {
          if (!task || !!task.error)
            ccLib.fireCallback(callback, self, null, jhr);
          else
            jT.call(self, task.result, callback);
        });
      };     
      jT.call(self, q, function (result, jhr) {
        if (!result)
          ccLib.fireCallback(callback, self, null, jhr);
        else if (!self.settings.forceCreate && result.dataEntry.length > 0) {
          var empty = true;
          for (var i = 0, rl = result.dataEntry.length; i < rl; ++i)
            if (!jT.$.isEmptyObject(result.dataEntry[i].values)) {
              empty = false;
              break;
            }
          if (empty)
            createIt();
          else
            ccLib.fireCallback(callback, self, result, jhr)  
        }
        else
          createIt();
      });
    },
    
    query: function (uri) {
      if (this.settings.algorithms)
        this.listAlgorithms(this.settings.algorithmNeedle = (uri || this.settings.algorithmNeedle));
      else
        this.listModels(this.settings.modelUri = (uri || this.settings.modelUri));
    },
    
    modifyUri: function (uri) {
      jT.$('input[type="checkbox"]', this.rootElement).each(function () {
        if (this.checked)
          uri = ccLib.addParameter(uri, 'feature_uris[]=' + encodeURIComponent(this.value + '/predicted'));
      })
      
      return uri;
    }
  };
  
  return cls;
})();
/* toxsubstance.js - A kit for browsing substances
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxSubstance = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    showControls: true,       // show navigation controls or not
    selectionHandler: null,   // if given - this will be the name of the handler, which will be invoked by jToxQuery when the attached selection box has changed...
    embedComposition: null,   // embed composition listing as details for each substance - it valid only if onDetails is not given.
    noInterface: false,       // run in interface-less mode - only data retrieval and callback calling.
    onDetails: null,          // called when a details row is about to be openned. If null - no details handler is attached at all.
    onLoaded: null,           // called when the set of substances (for this page) is loaded.
    oLanguage: {
      "sLoadingRecords": "No substances found.",
      "sZeroRecords": "No substances found.",
      "sEmptyTable": "No substances available.",
      "sInfo": "Showing _TOTAL_ substance(s) (_START_ to _END_)"
    },
  
    pageStart: 0,
    pageSize: 10,
    /* substanceUri */
    configuration: { 
      columns : {
        substance: {
          'Id': { sTitle: 'Id', mData: 'URI', sDefaultContent: "-", sWidth: "60px", mRender: function (data, type, full) { 
            return (type != 'display') ? full.index : '&nbsp;-&nbsp;' + full.index + '&nbsp;-&nbsp;';
          } },
          'Substance Name': { sTitle: "Substance Name", mData: "name", sDefaultContent: "-" },
          'Substance UUID': { sTitle: "Substance UUID", mData: "i5uuid", mRender: function (data, type, full) {
            return (type != 'display') ? data : jT.ui.shortenedData('<a target="_blank" href="' + full.URI + '/study">' + data + '</a>', "Press to copy the UUID in the clipboard", data)
          } },
          'Substance Type': { sTitle: "Substance Type", mData: "substanceType", sWidth: "15%", sDefaultContent: '-' },
          'Public name': { sTitle: "Public name", mData: "publicname", sDefaultContent: '-'},
          'Reference substance UUID': { sTitle: "Reference substance UUID", mData: "referenceSubstance", mRender: function (data, type, full) {
            return (type != 'display') ? data.i5uuid : jT.ui.shortenedData('<a target="_blank" href="' + data.uri + '">' + data.i5uuid + '</a>', "Press to copy the UUID in the clipboard", data.i5uuid);
          } },
          'Owner': { sTitle: "Owner", mData: "ownerName", sDefaultContent: '-'},
          'Info': { sTitle: "Info", mData: "externalIdentifiers", mRender: function (data, type, full) { return jToxSubstance.formatExtIdentifiers(data, type, full); } }
        }
      }
    }
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized
    
    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);

    self.pageStart = self.settings.pageStart;
    self.pageSize = self.settings.pageSize;
    
    if (!self.settings.noInterface) {
      if (self.settings.embedComposition && self.settings.onDetails == null) {
        self.settings.onDetails = function (root, data, element) {
          new jToxComposition(root, jT.$.extend(
            {}, 
            self.settings, 
            (typeof self.settings.embedComposition == 'object' ? self.settings.embedComposition : jT.blankSettings), 
            { compositionUri : data.URI + '/composition' }
          ));
        };
      }

      self.rootElement.appendChild(jT.getTemplate('#jtox-substance'));
      self.init(settings);
    }    
        
    // finally, if provided - make the query
    if (!!self.settings.substanceUri)
      self.querySubstance(self.settings.substanceUri)
  };
  
  // format the external identifiers column
  cls.formatExtIdentifiers = function (data, type, full) {
    if (type != 'display')
      return ccLib.joinDeep(data, 'id', ', ');
    
    var html = '';
    for (var i = 0;i < data.length;++i) {
      if (i > 0)
        html += '<br/>';
      html += data[i].type + '&nbsp;=&nbsp;' + data[i].id;
    }
    return html;
  };
  
  cls.prototype = {
    init: function (settings) {
      var self = this;
      
      // deal if the selection is chosen
      var colId = self.settings.configuration.columns.substance['Id'];
      jT.ui.putActions(self, colId);
      colId.sTitle = '';
      
      self.settings.configuration.columns.substance['Owner'].mRender = function (data, type, full) {
        return (type != 'display') ? data : '<a target="_blank" href="' + self.settings.baseUrl + '/substanceowner/' + full.ownerUUID + '/substance">' + data + '</a>';
      };
      
      var opts = {  "sDom": "rti" };
      if (self.settings.showControls) {
        jT.ui.bindControls(self, { 
          nextPage: function () { self.nextPage(); },
          prevPage: function () { self.prevPage(); },
          sizeChange: function() { self.queryEntries(self.pageStart, parseInt(jT.$(this).val())); },
          filter: function () { jT.$(self.table).dataTable().fnFilter(jT.$(this).val()); }
        });
        
        opts['fnInfoCallback'] = function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
          var needle = jT.$('.filterbox', self.rootElement).val();
          jT.$('.filtered-text', self.rootElement).html(!needle ? ' ' : ' (filtered to <span class="high">' + iTotal + '</span>) ');
          return '';
        };
      }
      else
        jT.$('.jtox-controls', self.rootElement).remove();
      
      // again , so that changed defaults can be taken into account.
      self.settings.configuration = jT.$.extend(true, self.settings.configuration, settings.configuration);
      
      // READYY! Go and prepare THE table.
      self.table = jT.ui.putTable(self, jT.$('table', self.rootElement)[0], 'substance', opts);
    },
    
    queryEntries: function(from, size) {
      var self = this;
      if (from < 0)
        from = 0;
        
      if (!size || size < 0)
        size = self.pageSize;
        
      var qStart = Math.floor(from / size);
      var qUri = ccLib.addParameter(self.substanceUri, "page=" + qStart + "&pagesize=" + size);
      jT.call(self, qUri, function (result, jhr) {
        if (!result && jhr.status == 404)
          result = { substabce: [] }; // empty one
        if (!!result) {
          self.pageSize = size;
          self.pageStart = from;
        
          for (var i = 0, rl = result.substance.length; i < rl; ++i)
            result.substance[i].index = i + from + 1;

          self.substance = result.substance;
          
          if (result.substance.length < self.pageSize) // we've reached the end!!
            self.entriesCount = from + result.substance.length;

          // time to call the supplied function, if any.
          ccLib.fireCallback(self.settings.onLoaded, self, result);
          if (!self.settings.noInterface) {
            jT.$(self.table).dataTable().fnClearTable();
            jT.$(self.table).dataTable().fnAddData(result.substance);
            
            self.updateControls(from, result.substance.length);
          }
        }
        else
          ccLib.fireCallback(self.settings.onLoaded, self, result);
      });
    },
    
    querySubstance: function (uri) {
      var self = this;
      self.substanceUri = jT.grabPaging(self, uri);
      if (ccLib.isNull(self.settings.baseUrl))
        self.settings.baseUrl = jT.grabBaseUrl(uri);
      self.queryEntries(self.pageStart);
    },   
    
    query: function (uri) {
      this.querySubstance(uri);
    }
  };
  
  // some "inheritance" :-)
  cls.prototype.nextPage = jToxCompound.prototype.nextPage;
  cls.prototype.prevPage = jToxCompound.prototype.prevPage;
  cls.prototype.updateControls = jToxCompound.prototype.updateControls;
  
  return cls;
})();
/* toxcomposition.js - A kit for visualizing substance composition(s)
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxComposition = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    selectionHandler: null,   // selection handler, if needed for selection checkbox, which will be inserted if this is non-null
    showBanner: true,         // whether to show a banner of composition info before each compounds-table
    showDiagrams: false,      // whether to show diagram for each compound in the composition
    noInterface: false,       // run in interface-less mode - just data retrieval and callback calling.
    sDom: "rt<Ffp>",          // compounds (ingredients) table sDom
    onLoaded: null,
    
    /* compositionUri */
    configuration: {
      columns : {
        composition: {
          'Type': { sTitle: "Type", sClass : "left", sWidth : "10%", mData : "relation", mRender : function(val, type, full) {
					  if (type != 'display')
					    return '' + val;
					  var func = ("HAS_ADDITIVE" == val) ? full.proportion.function_as_additive : "";
					  return '<span class="camelCase">' +  val.substr(4).toLowerCase() + '</span>' + ((func === undefined || func === null || func == '') ? "" : " (" + func + ")");
          } },
          'Name': { sTitle: "Name", sClass: "camelCase left", sWidth: "15%", mData: "component.compound.name", mRender: function(val, type, full) {
						return (type != 'display') ? '' + val : 
						  '<a href="' + full.component.compound.URI + '" target="_blank" title="Click to view the compound"><span class="ui-icon ui-icon-link" style="float: left; margin-right: .3em;"></span></a>' + val; } },
					'EC No.': { sTitle: "EC No.", sClass: "left", sWidth: "10%", mData: "component.compound.einecs" },
					'CAS No.': { sTitle: "CAS No.", sClass : "left", sWidth: "10%", mData : "component.compound.cas" },
					'Typical concentration': { sTitle: "Typical concentration", sClass: "center", sWidth: "15%", mData: "proportion.typical", mRender: function(val, type, full) { return type != 'display' ? '' + val.value : jToxComposition.formatConcentration(val.precision, val.value, val.unit); } },
					'Concentration ranges': { sTitle: "Concentration ranges", sClass : "center colspan-2", sWidth : "20%", mData : "proportion.real", mRender : function(val, type, full) { return type != 'display' ? '' + val.lowerValue : jToxComposition.formatConcentration(val.lowerPrecision, val.lowerValue, val.unit); } },
					'Upper range': { sTitle: 'Upper range', sClass: "center", sWidth: "20%", mData: "proportion.real", mRender: function(val, type, full) { return type != 'display' ? '' + val.upperValue : jToxComposition.formatConcentration(val.upperPrecision, val.upperValue, val.unit); } },
					'Also': { sTitle: "Also", sClass: "center", bSortable: false, mData: "component.compound.URI", sDefaultContent: "-" }
				}
      }
    }
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized
    
    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);
    
    // finally, if provided - make the query
    if (!!self.settings.compositionUri)
      self.queryComposition(self.settings.compositionUri)
  };
  
  cls.formatConcentration = function (precision, val, unit) {
  	return jT.ui.valueWithUnits((!precision || "=" == precision ? "" : precision) + val + " ", unit || '% (w/w)');
  };

  var fnDatasetValue = function (fid, old, value, features){
		return ccLib.extendArray(old, value != null ? value.trim().toLowerCase().split("|") : [value]).filter(ccNonEmptyFilter);
  };
  
  cls.prototype = {
    prepareTable: function (json, tab) {
      var self = this;
      
      // deal if the selection is chosen
      var colId = self.settings.configuration.columns.composition.Name;
      if (!!self.settings.selectionHandler) {
        jT.ui.putActions(self, colId);
        colId.sWidth = "60px";
      }
        
      // we need that processing to remove the title of "Also contained in..." column...
      var cols = jT.ui.processColumns(self, 'composition');
      for (var i = 0, cl = cols.length; i < cl; ++i)
        if (cols[i].sTitle == 'Also') {
          cols[i].sTitle = '';
          // we need to do this here, because 'self' is not defined up there...
          cols[i].mRender = function(val, type, full) { return !val ? '' : '<a href="' + (self.settings.baseUrl || self.baseUrl) + '/substance?type=related&compound_uri=' + encodeURIComponent(val) + '" target="_blank">Also contained in...</a>'; };
          break;
        }
        
      // if we have showDiagram set to true we need to show it up
      if (self.settings.showDiagrams) {
        var diagFeature = jToxCompound.baseFeatures['http://www.opentox.org/api/1.1#Diagram'];
        cols.push(jT.$.extend({}, diagFeature.column, { 
          sTitle: 'Structure', 
          mData: "component", 
          mRender: function (val, type, full) { return diagFeature.render(val.compound.URI, type, val); } 
        }));
      }
      // READYY! Go and prepare THE table.
      self.table = jT.ui.putTable(self, jT.$('table.composition-table', tab)[0], 'composition', {
        "aoColumns": cols
      });
      
      jT.$(self.table).dataTable().fnAddData(json);
      // now make a few fixing for multi-column title
      var colSpan = jT.$('th.colspan-2', self.table);
      jT.$(colSpan).attr('colspan', 2);
      jT.$(jT.$(colSpan).next()).remove();
      return self.table;
    },
    
    queryComposition: function (uri) {
      var self = this;
      self.compositionUri = uri;
      self.baseUrl = jT.grabBaseUrl(uri);
      jT.call(self, uri, function (json) {
        if (!!json && !!json.composition) {
          // clear the old tabs, if any.
          var substances = {};
    
          jToxCompound.processFeatures(json.feature);
          // proprocess the data...
          for (var i = 0, cmpl = json.composition.length; i < cmpl; ++i) {
            var cmp = json.composition[i];
            
            // TODO: Start using show banner!
            jToxCompound.processEntry(cmp.component, json.feature, fnDatasetValue);
    
            // now prepare the subs        
            var theSubs = substances[cmp.compositionUUID];
            if (theSubs === undefined)
              substances[cmp.compositionUUID] = theSubs = { name: "", purity: "", maxvalue: 0, uuid: cmp.compositionUUID, composition: [] };
            
            theSubs.composition.push(cmp);
            if (cmp.compositionName != '' && cmp.compositionName != null)
              theSubs.name = cmp.compositionName;
              
            var val = cmp.proportion.typical;
            if (cmp.relation == 'HAS_CONSTITUENT' && theSubs.name == '') {
              theSubs.name = cmp.component.compound['name'] + ' (' + jToxComposition.formatConcentration(val.precision, val.value, val.unit) + ')';
            }
            
            if (cmp.relation == 'HAS_CONSTITUENT' && theSubs.maxvalue < val.value) {
              theSubs.maxvalue = val.value;
              val = cmp.proportion.real;
              theSubs.purity = jToxComposition.formatConcentration(null, val.lowerValue + '-' + val.upperValue, val.unit);
            }
          }
          
          ccLib.fireCallback(self.settings.onLoaded, self, json.composition);
          // now make the actual filling
          if (!self.settings.noInterface) {
            for (var i in substances) {
              var panel = jT.getTemplate('#jtox-composition');
              self.rootElement.appendChild(panel);
              
              if (self.settings.showBanner)
                ccLib.fillTree(jT.$('.composition-info', panel)[0], substances[i]);
              else // we need to remove it
                jT.$('.composition-info', panel).remove();
              // we need to prepare tables, abyways.
              self.prepareTable(substances[i].composition, panel);
            }
          }
        }
        else
          ccLib.fireCallback(self.settings.onLoaded, self, json.composition);
      });
    },   
    
    query: function (uri) {
      jT.$(self.rootElement).empty();
      this.queryComposition(uri);
    }
  };
  
  return cls;
})();
/* toxstudy.js - Study-related functions from jToxKit
 *
 * Copyright 2012-2013, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxStudy = (function () {
  var defaultSettings = {
    tab: null,
    sDom: "rt<Fip>",
    oLanguage: null,
    // events
    onSummary: null,    // invoked when the summary is loaded
    onComposition: null, // invoked when the 
    onStudy: null,      // invoked for each loaded study
    onLoaded: null,     // invoked when the substance general info is loaded
    configuration: { 
      columns: {
      	"_": {
	    		"main" : { },
	    		"parameters": { },
	    		"conditions": { },
	    		"effects": { },
	    		"protocol": { },
	    		"interpretation": { },
	    	}
    	}
    }
  };    // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
  var instanceCount = 0;
  
  var fnDatasetValue = function (fid, old, value, features){
		return ccLib.extendArray(old, value != null ? value.trim().toLowerCase().split("|") : [value]).filter(ccNonEmptyFilter);
  };
  
  // constructor
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    self.instanceNo = instanceCount++;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even in manual initialization.
    
    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings); // i.e. defaults from jToxStudy
    self.settings.tab = self.settings.tab || jT.settings.fullUrl.hash;
    // now we have our, local copy of settings.
    
    // get the main template, add it (so that jQuery traversal works) and THEN change the ids.
    // There should be no overlap, because already-added instances will have their IDs changed already...
    var tree = jT.getTemplate('#jtox-studies');
    root.appendChild(tree);
    jT.ui.changeTabsIds(tree, '_' + self.instanceNo);
    
    // initialize the tab structure for several versions of tabs.
    self.tabs = jT.$(tree).tabs({
      "select" : function(event, ui) {
        self.loadPanel(ui.panel);
      },
      "beforeActivate" : function(event, ui) {
        if (ui.newPanel)
          self.loadPanel(ui.newPanel[0]);
      }
    });
    
    // when all handlers are setup - make a call, if needed.    
    if (self.settings['substanceUri'] != null) {
      self.querySubstance(self.settings['substanceUri']);
    }
  };
  
  // now follow the prototypes of the instance functions.
  cls.prototype = {
    loadPanel: function(panel){
      var self = this;
      if (jT.$(panel).hasClass('unloaded')){
        var uri = self.addParameters(jT.$(panel).data('jtox-uri'));
        jT.call(self, uri, function(study){
          if (!!study) {
            jT.$('.jtox-study.folded', panel).removeClass('folded');
            jT.$(panel).removeClass('unloaded').addClass('loaded');

            self.processStudies(panel, study.study, true);
            ccLib.fireCallback(self.settings.onStudy, self, study.study);
          }
        });  
      }
    },
    
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
    
    createCategory: function(tab, category) {
      var self = this;
  
      var theCat = jT.$('.' + category + '.jtox-study', tab)[0];
      if (!theCat) {
        theCat = jT.getTemplate('#jtox-study');
        tab.appendChild(theCat);
        jT.$(theCat).addClass(category);
      }
      
      return theCat;
    },
    
    addParameters: function (summaryURI) {
      var self = this;
      var pars = ["property_uri", "top", "category"];
      for (var i = 0; i < pars.length; ++i) {
        var p = pars[i];
        if (!!self.settings[p])
          summaryURI = ccLib.addParameter(summaryURI, p + "=" + self.settings[p]);
      }
      
      return summaryURI;
    },
    
    // modifies the column title, according to configuration and returns "null" if it is marked as "invisible".
    ensureTable: function (tab, study) {
      var self = this;
      var category = study.protocol.category.code;
      var theTable = jT.$('.' + category + ' .jtox-study-table', tab)[0];
      if (!jT.$(theTable).hasClass('dataTable')) {
	      var defaultColumns = [
	        { "sTitle": "Name", "sClass": "center middle", "sWidth": "20%", "mData": "protocol.endpoint" }, // The name (endpoint)
	        { "sTitle": "Endpoint", "sClass": "center middle jtox-multi", "sWidth": "15%", "mData": "effects", "mRender": function (data, type, full) { return jT.ui.renderMulti(data, type, full, function (data, type) { return self.getFormatted(data, type, "endpoint"); }); } },   // Effects columns
	        { "sTitle": "Result", "sClass": "center middle jtox-multi", "sWidth": "10%", "mData" : "effects", "mRender": function (data, type, full) { return jT.ui.renderMulti(data, type, full, function (data, type) { return jT.ui.renderRange(data.result, type) }); } },
	        { "sTitle": "Text", "sClass": "center middle jtox-multi", "sWidth": "10%", "mData" : "effects", "mRender": function (data, type, full) { return jT.ui.renderMulti(data, type, full, function (data, type) { return data.result.textValue ||  '-'; }); } },
	        { "sTitle": "Guideline", "sClass": "center middle", "sWidth": "15%", "mData": "protocol.guideline", "mRender" : "[,]", "sDefaultContent": "-"  },    // Protocol columns
	        { "sTitle": "Owner", "sClass": "center middle", "sWidth": "15%", "mData": "citation.owner", "sDefaultContent": "-" },
	        { "sTitle": "Citation", "sClass": "center middle", "sWidth": "15%", "mData": "citation", "mRender": function (data, type, full) { return (data.title || "") + ' ' + (!!data.year || ""); }  },
	        { "sTitle": "UUID", "sClass": "center middle", "sWidth": "15%", "mData": "uuid", "bSearchable": false, "mRender" : function(data, type, full) { return type != "display" ? '' + data : jT.ui.shortenedData(data, "Press to copy the UUID in the clipboard"); } }
	      ];
  
        var colDefs = [];
        
        // start filling it
        var parCount = 0;
  
        // this function takes care to add as columns all elements from given array
        var putAGroup = function(group, fProcess) {
          var count = 0;
          var skip = [];
          for (var p in group) {
            if (skip.indexOf(p) > -1)
              continue;
            if (group[p + " unit"] !== undefined)
              skip.push(p + " unit");
            var val = fProcess(p);
            if (ccLib.isNull(val))
              continue;
              
            colDefs.push(val);
            count++;
          }
          return count;
        }
        
        var putDefaults = function(start, len, group) {
          for (var i = 0;i < len; ++i) {
            var col = jT.$.extend({}, defaultColumns[i + start]);
            col = jT.ui.modifyColDef(self, col, category, group);
            if (col != null)
              colDefs.push(col);
          }
        };
        
        putDefaults(0, 1, "main");
        
        // use it to put parameters...
        putAGroup(study.parameters, function(p) {
          if (study.effects[0].conditions[p] !== undefined  || study.effects[0].conditions[p + " unit"] !== undefined)
            return undefined;

          var col = {
            "sTitle" : p,
            "sClass" : "center middle", 
            "mData" : "parameters." + p,
            "sDefaultContent": "-"
          };
          
          col = jT.ui.modifyColDef(self, col, category, "parameters");
          if (col == null)
            return null;
          
          col["mRender"] = function (data, type, full) { return jT.ui.renderRange(data, full[p + " unit"], type); };
          return col;
        });
        // .. and conditions
        putAGroup(study.effects[0].conditions, function(c){
          var col = { 
            "sTitle" : c,
            "sClass" : "center middle jtox-multi", 
            "mData" : "effects"
          };
          
          col = jT.ui.modifyColDef(self, col, category, "conditions");
          if (col == null)
            return null;
          
          col["mRender"] = function(data, type, full) {
            return jT.ui.renderMulti(data, type, full, function(data, type) { 
              return jT.ui.renderRange(data.conditions[c], data.conditions[c + " unit"], type); 
            }); 
          };
          return col;
        });
        
        // add also the "default" effects columns
        putDefaults(1, 3, "effects");
  
        // now is time to put interpretation columns..
        putAGroup(study.interpretation, function(i){
          var col = { "sTitle": i, "sClass" : "center middle jtox-multi", "mData" : "interpretation." + i, "sDefaultContent": "-"};
          return jT.ui.modifyColDef(self, col, category, "interpretation");
        });
        
        // finally put the protocol entries
        putDefaults(4, 4, "protocol");
        
        // but before given it up - make a small sorting..
        jT.ui.sortColDefs(colDefs);
        
        // READYY! Go and prepare THE table.
        jT.$(theTable).dataTable( {
          "bPaginate": true,
          "bProcessing": true,
          "bLengthChange": false,
  				"bAutoWidth": false,
          "sDom" : self.settings.sDom,
          "aoColumns": colDefs,
          "fnInfoCallback": function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
            var el = jT.$('.title .data-field', jT.$(this).parents('.jtox-study'))[0];
            el.innerHTML = jT.ui.updateCounter(el.innerHTML, iTotal);
            return sPre;
          },
          "fnCreatedRow": function( nRow, aData, iDataIndex ) {
            ccLib.equalizeHeights.apply(window, jT.$('td.jtox-multi table tbody', nRow).toArray());
          },
          
  				"oLanguage": jT.$.extend({
            "sProcessing": "<img src='" + (jT.settings.baseUrl || self.baseUrl) + "/images/24x24_ambit.gif' border='0'>",
            "sLoadingRecords": "No studies found.",
            "sZeroRecords": "No studies found.",
            "sEmptyTable": "No studies available.",
            "sInfo": "Showing _TOTAL_ study(s) (_START_ to _END_)",
            "sLengthMenu": 'Display <select>' +
              '<option value="10">10</option>' +
              '<option value="20">20</option>' +
              '<option value="50">50</option>' +
              '<option value="100">100</option>' +
              '<option value="-1">all</option>' +
              '</select> studies.'
          }, self.settings.oLanguage)
        });
        
        jT.$(theTable).dataTable().fnAdjustColumnSizing();
      }
      else
        jT.$(theTable).dataTable().fnClearTable();
        
      return theTable;
    },
    
    processSummary: function (summary) {
      var self = this;
      var typeSummary = {};
      var knownNames = { "P-CHEM": "P-Chem", "ENV_FATE" : "Env Fate", "ECOTOX" : "Eco Tox", "TOX" : "Tox"};
      
      // first - clear all existing tabs
			jT.$('.jtox-study', self.rootElement).remove();
      
      // create the groups on the corresponding tabs, first sorting them alphabetically
      summary.sort(function (a, b) {
      	var valA = (a.category.order || a.category.description || a.category.title);
      	var valB = (b.category.order || b.category.description || b.category.title);
      	if (valA == null)
      		return -1;
      	if (valB == null)
      		return 1;
        if (valA == valB)
          return 0;
	      return (valA < valB) ? -1 : 1;
      });
      
      var tabRoot = $('ul', self.rootElement).parent()[0];
      var added = 0;
      var lastAdded = null;
      var addStudyTab = function (top, sum) {
        var tab = jT.getTemplate('#jtox-study-tab');
        var link = jT.ui.addTab(tabRoot, (knownNames[top] || sum.topcategory.title) + " (0)", "jtox-" + top.toLowerCase() + '_' + self.instanceNo, tab).tab;
        jT.$(link).data('type', top);
        
        jT.$(tab).addClass(top).data('jtox-uri', sum.topcategory.uri);
        ccLib.fillTree(tab, self.substance);
        
        added++;
        lastAdded = top;
      
        jT.$('div.jtox-study-tab div button', tabRoot).on('click', function (e) {
        	var par = jT.$(this).parents('.jtox-study-tab')[0];
    	    if (jT.$(this).hasClass('expand-all')) {
    		    jT.$('.jtox-foldable', par).removeClass('folded');
    	    }
    	    else if (jT.$(this).hasClass('collapse-all')) {
    		    jT.$('.jtox-foldable', par).addClass('folded');
    	    }
        });
        
        return tab;
      };
      
      for (var si = 0, sl = summary.length; si < sl; ++si) {
        var sum = summary[si];
        var top = sum.topcategory.title;
        if (!top)
          continue;
        var top = top.replace(/ /g, "_");
        var tab = jT.$('.jtox-study-tab.' + top, self.rootElement)[0];
        if (!tab)
          tab = addStudyTab(top, sum);
        
        var catname = sum.category.title;
        if (!catname)
          typeSummary[top] = sum.count;
        else
          self.createCategory(tab, catname);
      }

      // a small hack to force openning of this, later in the querySummary()      
      if (added == 1)
        self.settings.tab = lastAdded;

      // update the number in the tabs...
      jT.$('ul li a', self.rootElement).each(function (i){
        var data = jT.$(this).data('type');
        if (!!data){
          var cnt = typeSummary[data];
          var el = jT.$(this)[0];
          el.innerHTML = jT.ui.updateCounter(el.innerHTML, cnt);
        }
      });
      
      // now install the filter box handler. It delays the query a bit and then spaws is to all tables in the tab.
      var filterTimeout = null;
      var fFilter = function (ev) {
        if (!!filterTimeout)
          clearTimeout(filterTimeout);
    
        var field = ev.currentTarget;
        var tab = jT.$(this).parents('.jtox-study-tab')[0];
        
        filterTimeout = setTimeout(function() {
          var tabList = jT.$('.jtox-study-table', tab);
          for (var t = 0, tlen = tabList.length; t < tlen; ++t) {
            jT.$(tabList[t]).dataTable().fnFilter(field.value);
          }
        }, 300);
      };
      
      var tabList = jT.$('.jtox-study-tab');
      for (var t = 0, tlen = tabList.length;t < tlen; t++){
        var filterEl = jT.$('.jtox-study-filter', tabList[t])[0].onkeydown = fFilter;
      }
    },
    
    processStudies: function (tab, study, map) {
      var self = this;
      var cats = {};
      var cntCats = 0;
      
      // first swipe to map them to different categories...
      if (!map){
        // add this one, if we're not remapping. map == false assumes that all passed studies will be from
        // one category.    
        cats[study[0].protocol.category.code] = study;
      }
      else{
        for (var i = 0, slen = study.length; i < slen; ++i) {
          var ones = study[i];
          if (map) {
            if (cats[ones.protocol.category.code] === undefined) {
              cats[ones.protocol.category.code] = [ones];
              cntCats++;
            }
            else {
              cats[ones.protocol.category.code].push(ones);
            }
          }
        }
      }
  
      // now iterate within all categories (if many) and initialize the tables
      for (var c in cats) {
        var onec = cats[c];
        var aStudy = jT.$('.' + c + '.jtox-study', tab)[0];
        if (aStudy === undefined)
          continue;
  
        ccLib.fillTree(aStudy, {title: onec[0].protocol.category.title + " (0)"});
        
        // now swipe through all studyies to build a "representative" one with all fields.
        var study = {};
        for (var i = 0, cl = onec.length; i < cl; ++i) {
          jT.$.extend(true, study, onec[i]);
          if (!ccLib.isEmpty(study.parameters) && !ccLib.isEmpty(study.effects[0].conditions))
            break;
        }

        var theTable = self.ensureTable(tab, study);
        jT.$(theTable).dataTable().fnAddData(onec);
        jT.$(theTable).colResizable({ minWidth: 30, liveDrag: true });
        if (cntCats > 1)
          jT.$(theTable).parents('.jtox-study').addClass('folded');

        // we need to fix columns height's because of multi-cells
        jT.$('#' + theTable.id + ' .jtox-multi').each(function(index){
          this.style.height = '' + this.offsetHeight + 'px';
        });
      }
    },
        
    querySummary: function(summaryURI) {
      var self = this;
      
      summaryURI = self.addParameters(summaryURI);      
      jT.call(self, summaryURI, function(summary) {
        if (!!summary && !!summary.facet)
          self.processSummary(summary.facet);
          ccLib.fireCallback(self.settings.onSummary, self, summary.facet);
          // check if there is an initial tab passed so we switch to it
          if (!!self.settings.tab) {
            var div = jT.$('.jtox-study-tab.' + decodeURIComponent(self.settings.tab).replace(/ /g, '_').toUpperCase(), self.root)[0];
            if (!!div) {
              for (var idx = 0, cl = div.parentNode.children.length; idx < cl; ++idx)
                if (div.parentNode.children[idx].id == div.id)
                  break;
              --idx;
              jT.$(self.tabs).tabs('option', 'active', idx);
              jT.$(self.tabs).tabs('option', 'selected', idx);
            }
          }
      });
    },
    
    insertComposition: function(compositionURI) {
      var compRoot = jT.$('.jtox-compo-tab', this.rootElement)[0];
      jT.$(compRoot).empty();
      new jToxComposition(compRoot, jT.$.extend({}, this.settings, jT.blankSettings, {'compositionUri': compositionURI}));
    },
    
    querySubstance: function(substanceURI) {
      var self = this;
      
      // re-initialize us on each of these calls.
      self.baseUrl = ccLib.isNull(self.settings.baseUrl) ? jT.grabBaseUrl(substanceURI) : self.settings.baseUrl;
      
      jT.call(self, substanceURI, function(substance){
        if (!!substance && !!substance.substance && substance.substance.length > 0){
          substance = substance.substance[0];
           
          substance["showname"] = substance.publicname || substance.name;
          substance["IUCFlags"] = jToxSubstance.formatExtIdentifiers(substance.externalIdentifiers, 'display', substance);
          self.substance = substance;
            
          ccLib.fillTree(self.rootElement, substance);
          // go and query for the reference query
          jT.call(self, substance.referenceSubstance.uri, function (dataset){
            if (!!dataset) {
              jToxCompound.processDataset(dataset, null, fnDatasetValue);
              ccLib.fillTree(jT.$('.jtox-substance', self.rootElement)[0], dataset.dataEntry[0]);
            }
          });
           
          ccLib.fireCallback(self.settings.onLoaded, self, substance.substance);
          // query for the summary and the composition too.
          self.querySummary(substance.URI + "/studysummary");
          self.insertComposition(substance.URI + "/composition");
        }
        else
          ccLib.fireCallback(self.settings.onLoaded, self, null);
      });
    },
    
    query: function (uri) {
      this.querySubstance(uri);
    }
  }; // end of prototype
  
  return cls;
})();
/* toxauth.js - Several kits for querying/manipulating authorization-related stuff: policies, roles, users
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

/* jToxPolicy - manages connection between user roles and permitted actions on specified services
*/
var jToxPolicy = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    loadOnInit: false,      // whether to make initial load upon initializing the kit
    noInterface: false,     // whether to have interface... at all.
    sDom: "rt",
    oLanguage: {
      "sLoadingRecords": "No policies found.",
      "sZeroRecords": "No policies found.",
      "sEmptyTable": "No policies available.",
      "sInfo": "Showing _TOTAL_ policy(s) (_START_ to _END_)"
    },
    onRow: function (row, data, index) {
      jT.$('select', row).each(function () {
        $(this).val(data[$(this).data('data')]);
      });
    },
    configuration: { 
      columns : {
        policy: {
          'Id': { iOrder: 0, sClass: "center", sDefaultContent: '', bSortable: false, sTitle: "Id", mData: "uri", sWidth: "50px", mRender: function (data, type, full) {
            if (type == 'sort')
              return !!data ? '0' : '1';
            else if (type != 'display')
              return data || '';
            else if (!data)
              return '<span class="ui-icon ui-icon-plusthick jtox-handler jtox-inline jtox-hidden" data-handler="add"></span>';
            else
              return '<span class="ui-icon ui-icon-closethick jtox-handler jtox-inline" data-handler="remove"></span>';
          }},
          'Role': { iOrder: 1, sTitle: "Role", sDefaultContent: '', sWidth: "20%", mData: "role"},
          'Service': {iOrder: 2, sTitle: "Service", sDefaultContent: '', mData: "resource", sWidth: "40%", mRender: jT.ui.inlineChanger('resource', 'text', 'Service_') },
          'Get': { iOrder: 3, sClass: "center", sTitle: "Get", bSortable: false, sDefaultContent: false, mData: "methods.get", mRender: jT.ui.inlineChanger('methods.get', 'checkbox') },
          'Post': { iOrder: 4, sClass: "center", sTitle: "Post", bSortable: false, sDefaultContent: false, mData: "methods.post", mRender: jT.ui.inlineChanger('methods.post', 'checkbox') },
          'Put': { iOrder: 5, sClass: "center", sTitle: "Put", bSortable: false, sDefaultContent: false, mData: "methods.put", mRender: jT.ui.inlineChanger('methods.put', 'checkbox') },
          'Delete': { iOrder: 6, sClass: "center", sTitle: "Delete", bSortable: false, sDefaultContent: false, mData: "methods.delete", mRender: jT.ui.inlineChanger('methods.delete', 'checkbox') },
        }
      }
    }
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized
    
    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);
    self.policies = null;
    
    if (!self.settings.noInterface)
      self.init(settings);
        
    // finally, wait a bit for everyone to get initialized and make a call, if asked to
    if (self.settings.loadOnInit)
      self.loadPolicies();
  };

  cls.prototype.init = function (settings) {
    var self = this;
    self.rootElement.appendChild(jT.getTemplate('#jtox-policy'));
    self.settings.configuration.columns.policy.Id.sTitle = '';
    self.settings.configuration.columns.policy.Role.mRender = function (data, type, full) {
      return type != 'display' ? (data || '') : 
        '<select class="jt-inlineaction jtox-handler" data-handler="changed" data-data="role" value="' + (data || '') + '">' + self.roleOptions + '</select>';
    };
    
    var alerter = function (el, icon, task) {
      var mess = (!!task ? task.error : "Unknown error");
      $(el).removeClass(icon).addClass('ui-icon-alert').attr('title', mess);
      setTimeout(function () {
        $(el).addClass(icon).removeClass('ui-icon-alert');
        $(el).removeAttr('title');
      }, 3500);
    };
    
    self.settings.configuration.handlers = {
      changed: function (e) {
        var data = jT.ui.rowData(this);
        if (!!data.uri) {
          // Initiate a change in THIS field.
          var el = this;
          var myData = $(el).data('data');
          var myObj = {};
          ccLib.setJsonValue(myObj, myData, ccLib.getObjValue(el));

          $(el).addClass('loading');
          // now make the update call...
          jT.service(self, data.uri, { method: 'PUT', data: ccLib.packData(myObj) }, function (task) {
            $(el).removeClass('loading');
            if (!task || !!task.error) {
              ccLib.setObjValue(el, ccLib.getJsonValue(data, myData)); // i.e. revert the old value
              alerter(el, '', task);
            }
            else {
              // we need to update the value... in our internal 'policies' array...
              var idx = $(self.table).dataTable().fnGetPosition($(el).closest('tr')[0]);
              ccLib.setJsonValue(self.policies[idx], myData, ccLib.getObjValue(el));
            }
          });
        }
        else {
          // collect and validate and react
          var row = $(this).closest('tr');
          var inline = jT.ui.rowInline(row);
          if (!inline.role || !inline.resource)
            $('span.ui-icon-plusthick', row).addClass('jtox-hidden');
          else
            $('span.ui-icon-plusthick', row).removeClass('jtox-hidden');
        }
      },
      remove: function (e) {
        if (!window.confirm("Do you really want to delete this policy?"))
          return;
        var el = this;
        var data = jT.ui.rowData(this);
        $(el).addClass('loading');
        jT.service(self, data.uri, { method: "DELETE" }, function (task) {
          $(el).removeClass('loading');
          if (!task || !!task.error)
            alerter(el, 'ui-icon-closethick', task)
          else // i.e. - on success - reload them!
            self.loadPolicies();
        });
      },
      add: function (e) {
        var data = jT.ui.rowInline(this, jT.ui.rowData(this));
        delete data['uri'];
        var el = this;
        $(el).addClass('loading');
        jT.service(self, '/admin/restpolicy', { method: "POST", data: ccLib.packData(data)}, function (task) {
          $(el).removeClass('loading');
          if (!task || !!task.error)
            alerter(el, 'ui-icon-plusthick', task)
          else // i.e. - on success - reload them!
            self.loadPolicies();
        });
      },
    };
    
    // again , so that changed defaults can be taken into account.
    self.settings.configuration = jT.$.extend(true, self.settings.configuration, settings.configuration);
    
    self.table = jT.ui.putTable(self, jT.$('table', self.rootElement)[0], 'policy', { "aaSortingFixed": [[0, 'asc']] });
  };
  
  cls.prototype.loadPolicies = function (force) {
    var self = this;
    $(self.table).dataTable().fnClearTable();
    
    // The real policy loader...
    var realLoader = function () {
      jT.call(self, '/admin/restpolicy', function (result) { 
        if (!!result) {
          self.policies = result.policy;  
          if (!self.settings.noInterface)
            $(self.table).dataTable().fnAddData(result.policy.concat({  }));
        }
      });
    };
    
    // we need to load roles first, if not before, or if forces to...
    if (!self.roles || force) {
      jT.call(self, '/admin/role', function (roles) {
        if (!!roles) {
          // remember and prepare roles for select presenting...
          self.roles = roles;
          if (!self.settings.noInterface) {
            var optList = '';
            for (var i = 0, rl = roles.roles.length; i < rl; ++i)
              optList += '<option>' + roles.roles[i] + '</option>';
            self.roleOptions = optList;
          }

          // and now - really load the list
          realLoader();
        }
      });
    }
    else
      realLoader();
  };
  
  cls.prototype.query = function () {
    this.loadPolicies();
  }
  
  return cls;
})();
  /* toxlog.js - An activity logger, especially - an error reporter
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxLog = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    autoInstall: true,      // auto install itself on the jToxKit instance, ie. - universally
    resend: false,          // whether to resend the coming events on installed kits to their original handlers
    statusDelay: 1500,      // number of milliseconds to keep success / error messages before fading out
    keepMessages: 50,       // how many messages to keep in the queue
    lineHeight: "20px",     // the height of each status line
    rightSide: false,       // put the status icon on the right side
    hasDetails: true,       // whether to have the ability to open each line, to show it's details
    noInterface: false,     // whether to have interface, or not - it can be used just as relay station
    onEvent: null,          // a callback, when new event has arrived: function (logEvent). See README.md for more details
    
    // line formatting function - function (service, state, params, jhr) -> { header: "", details: "" }
    formatEvent: function (service, state, params, jhr) {
      if (params != null)
        return {
          header: params.method.toUpperCase() + ": " + service,
          details: "..."
        };
      else if (jhr != null)
        // by returning only the details part, we leave the header as it is.
        return {
          details: jhr.status + " " + jhr.statusText + '<br/>' + jhr.getAllResponseHeaders()
        };
      else
        return null;
    }       
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit jtox-log'); // to make sure it is there even when manually initialized

    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);
    if (!self.settings.noInterface) {
      self.rootElement.appendChild(jT.getTemplate('#jtox-logger'));
      
      if (typeof self.settings.lineHeight == "number")
        self.settings.lineHeight = self.settings.lineHeight.toString() + 'px';
      if (typeof self.settings.keepMessages != "number")
        self.settings.keepMessages = parseInt(self.settings.keepMessages);
        
      // now the actual UI manipulation functions...
      var listRoot = $('.list-root', self.rootElement)[0];
      var statusEl = $('.status', self.rootElement)[0];
  
      if (!!self.settings.rightSide) {
        statusEl.style.right = '0px';
        jT.$(self.rootElement).addClass('right-side');
      }
      else
        statusEl.style.left = '0px';
    
      var setIcon = function (root, status) {
        if (status == "error")
          jT.$(root).addClass('ui-state-error');
        else
          jT.$(root).removeClass('ui-state-error');
  
        if (status == "error")
          jT.$('.icon', root).addClass('ui-icon ui-icon-alert').removeClass('loading ui-icon-check');
        else if (status == "success")
          jT.$('.icon', root).addClass('ui-icon ui-icon-check').removeClass('loading ui-icon-alert');
        else {
          jT.$('.icon', root).removeClass('ui-icon ui-icon-check ui-icon-alert');
          if (status == "connecting")
            jT.$('.icon', root).addClass('loading');
        }
      };
      
      var setStatus = function (status) {
        $(".icon", statusEl).removeClass("jt-faded");
        setIcon (statusEl, status);
        if (status == "error" || status == "success") {
          setTimeout(function () { 
            jT.$('.icon', statusEl).addClass('jt-faded');
            var hasConnect = false;
            jT.$('.logline', listRoot).each(function () {
              if (jT.$(this).data('status') == "connecting")
                hasConnect = true;
            });
            if (hasConnect)
              setStatus("connecting");
          }, self.settings.statusDelay);
        }
        ccLib.fireCallback(self.settings.onStatus, self, status, self.theStatus);
        self.theStatus = status;
      };
      
      var addLine = function (data) {
        var el = jT.getTemplate("#jtox-logline");
        el.style.height = '0px';
        listRoot.insertBefore(el, listRoot.firstElementChild);
        ccLib.fillTree(el, data);
        setTimeout(function () { el.style.height = self.settings.lineHeight; }, 150);
        if (!!self.settings.hasDetails) {
          jT.$('.icon', el).on('click', function (e) {
            jT.$(el).toggleClass('openned');
            if (jT.$(el).hasClass("openned")) {
              var height = 0;
              jT.$('.data-field', el).each(function () {
                height += this.offsetHeight;
              });
              el.style.height = (height + 6) + 'px';
            }
            else
              el.style.height = self.settings.lineHeight;
              
            // to make sure other clickable handler won't take control.
            e.stopPropagation();
          });
        }
        
        while (listRoot.childNodes.length > self.settings.keepMessages)
          listRoot.removeChild(listRoot.lastElementChild);
  
        return el;
      };
      
      setStatus('');
      
      // this is the queue of events - indexes by the passed service
      self.events = {};
    } // noInterface if
    
    // now the handlers - needed no matter if we have interface or not    
    self.handlers = {
      onConnect: function (service, params, id) {
        var info = ccLib.fireCallback(self.settings.formatEvent, this, service, "connecting", params, null);
        ccLib.fireCallback(self.settings.onEvent, this, service, "connecting", info);
        if (!self.settings.noInterface) {
          setStatus("connecting");
          var line = addLine(info);
          self.events[id] = line;
          setIcon(line, 'connecting');
          jT.$(line).data('status', "connecting");
        }
        if (!!self.settings.resend && this._handlers != null)
          ccLib.fireCallback(this._handlers.onConnect, this, service, params, id);
      },
      onSuccess: function (service, status, jhr, id) {
        var info = ccLib.fireCallback(self.settings.formatEvent, this, service, "success", null, jhr);
        ccLib.fireCallback(self.settings.onEvent, this, service, "success", info);
        if (!self.settings.noInterface) {
          setStatus("success");
          var line = self.events[id];
          if (!line) {
            console.log("jToxLog: missing line for:" + service);
            return;
          }
          delete self.events[service];
          setIcon(line, 'success');
          ccLib.fillTree(line, info);
          jT.$(line).data('status', "success");
        }
        if (!!self.settings.resend && this._handlers != null)
          ccLib.fireCallback(this._handlers.onSuccess, this, service, status, jhr, id);
      },
      onError: function (service, status, jhr, id) {
        var info = ccLib.fireCallback(self.settings.formatEvent, this, service, "error", null, jhr);
        ccLib.fireCallback(self.settings.onEvent, this, service, "error", info);
        if (!self.settings.noInterface) {
          setStatus("error");
          var line = self.events[id];
          if (!line) {
            console.log("jToxLog: missing line for:" + service + "(" + status + ")");
            return;
          }
          delete self.events[service];
          setIcon(line, 'error');
          ccLib.fillTree(line, info);
          jT.$(line).data('status', "error");

          console.log("Error [" + id + ": " + service);
        }
        if (!!self.settings.resend && this._handlers != null)
          ccLib.fireCallback(this._handlers.onError, this, service, status, jhr, id);
      }
    };
    
    if (!!self.settings.autoInstall)
      self.install();
  };
  
  // Install the handers for given kit
  cls.prototype.install = function (kit) {
    var self = this;
    if (kit == null)
      kit = jT;
    
    // save the oldies
    kit._handlers = {
      onConnect: kit.settings.onConnect,
      onSuccess: kit.settings.onSuccess,
      onError: kit.settings.onError
    };
    
    kit.settings = jT.$.extend(kit.settings, self.handlers);
    return kit;
  };
  
  // Deinstall the handlers for given kit, reverting old ones
  cls.prototype.revert = function (kit) {
    var self = this;
    if (kit == null)
      kit = jT;

    kit.settings = jT.$.extend(kit.settings, kit._handlers);
    return kit;
  };
  
  cls.prototype.modifyUri = function (uri) { 
    return uri;
  };
  
  return cls;
})();
/* toxendpoint.js - An endpoint listing and selection toolkit
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxEndpoint = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    selectionHandler: null,   // selection handler to be attached on checkbox, for jToxQuery integration    
    noInterface: false,       // run in interface-less mode, with data retrieval and callback calling only
    heightStyle: "content",   // the accordition heightStyle
    hideFilter: false,        // if you don't want to have filter box - just hide it
    maxHits: 10,              // max hits in autocomplete
    showMultiselect: true,    // whether to hide select all / unselect all buttons
    showEditors: false,       // whether to show endpoint value editing fields as details
    showConditions: true,     // whether to show conditions in endpoint field editing
    sDom: "<i>rt",            // passed with dataTable settings upon creation
    oLanguage: null,          // passed with dataTable settings upon creation
    onLoaded: null,           // callback called when the is available
    loadOnInit: false,        // whether to make an (empty) call when initialized.
    units: ['uSv', 'kg', 'mg/l', 'mg/kg bw', '°C', 'mg/kg bw/day', 'ppm', '%', 'h', 'd'],
    loTags: ['>', '>=', '='],
    hiTags: ['<', '<='],
    oLanguage: {
      "sLoadingRecords": "No endpoints found.",
      "sZeroRecords": "No endpoints found.",
      "sEmptyTable": "No endpoints available.",
      "sInfo": "Showing _TOTAL_ endpoint(s) (_START_ to _END_)"
    },
    /* endpointUri */
    configuration: { 
      columns : {
        endpoint: {
          'Id': { sTitle: "Id", mData: "uri", bSortable: false, sWidth: "30px", mRender: function (data, type, full) { return ''; } },
          'Name': { sTitle: "Name", mData: "value", sDefaultContent: "-", mRender: function (data, type, full) {
            return data + '<span class="float-right jtox-details">[<span title="Number of values">' + full.count + '</span>]' + jT.ui.putInfo(full.uri) + '</span>';
          } },
        }
      }
    }
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized

    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);
        
    if (!self.settings.noInterface) {
      self.rootElement.appendChild(jT.getTemplate('#jtox-endpoint'));
      self.init(settings);
    }
        
    // finally, wait a bit for everyone to get initialized and make a call, if asked to
    if (self.settings.endpointUri != undefined || self.settings.loadOnInit)
      self.loadEndpoints(self.settings.endpointUri)
  };
  
  // now the editors...
  cls.linkEditors = function (kit, root, settings) { // category, top, onchange, conditions
    // get the configuration so we can setup the fields and their titles according to it
    var config = jT.$.extend(true, {}, kit.settings.configuration.columns["_"], kit.settings.configuration.columns[settings.category]);

    var putAutocomplete = function (box, service, configEntry, options) {
      // if we're not supposed to be visible - hide us.
      var field = box.data('field');
      if (!configEntry || configEntry.bVisible === false) {
        box.hide();
        return null;
      }

      // now deal with the title...
      var t = !!configEntry ? configEntry.sTitle : null;
      if (!!t)
        jT.$('div', box[0]).html(t);

      // prepare the options
      if (!options)
        options = {};
      
      // finally - configure the autocomplete options, themselves to initialize the component itself
      if (!options.source) options.source = function( request, response ) {
        jT.call(kit, service, { method: "GET", data: { 
          'category': settings.category,
          'top': settings.top,
          'max': kit.settings.maxHits || defaultSettings.maxHits,
          'search': request.term }
        } , function (data) {
          response( !data ? [] : jT.$.map( data.facet, function( item ) {
            var val = item[field] || '';
            return {
              label: val + (!item.count ? '' : " [" + item.count + "]"),
              value: val
            }
          }));
        });
      };
      
      // and the change functon
      if (!options.change) options.change = function (e, ui) {
        settings.onchange.call(this, e, field, !ui.item ? ccLib.trim(this.value) : ui.item.value);
      };
      
      // and the final parameter
      if (!options.minLength) options.minLength = 0;
      
      return jT.$('input', box[0]).autocomplete(options);
    };
        
    var putValueComplete = function (root, configEntry) {
      var field = root.data('field');
      var extractLast = function( val ) { return !!val ? val.split( /[,\(\)\s]*/ ).pop() : val; };
      var parseValue = function ( text ) { 
        var obj = {};
        var parsers = [
          {
            regex: /^[\s=]*([\(\[])\s*(\-?\d*[\.eE]?\-?\d*)\s*,\s*(\-?\d*[\.eE]?\-?\d*)\s*([\)\]])\s*([^\d,]*)\s*$/,
            fields: ['', 'loQualifier', 'loValue', 'upValue', 'upQualifier', 'unit'],
            // adjust the parsed value, if needed
            adjust: function (obj, parse) {
              if (!obj.upValue) delete obj.upQualifier;
              else              obj.upQualifier = parse[4] == ']' ? '<=' : '<';
              
              if (!obj.loValue) delete obj.loQualifier;
              else              obj.loQualifier = parse[1] == '[' ? '>=' : '>';
            }
          },
          {
            regex: /^\s*(>|>=)?\s*(\-?\d+[\.eE]?\-?\d*)\s*([^\d,<=>]*)[,\s]+(<|<=)?\s*(\-?\d*[\.eE]?\-?\d*)\s*([^\d,<=>]*)\s*$/,
            fields: ['', 'loQualifier', 'loValue', 'unit', 'upQualifier', 'upValue', 'unit'],
          },
          {
            regex: /^\s*(>|>=|=)?\s*(\-?\d+[\.eE]?\-?\d*)\s*([^\d,<=>]*)\s*$/,
            fields: ['', 'loQualifier', 'loValue', 'unit'],
            adjust: function (obj, parse) { if (!obj.loQualifier) obj.loQualifier = '='; }
          },
          {
            regex: /^\s*(<|<=)\s*(\-?\d+[\.eE]?\-?\d*)\s*([^\d,<=>]*)\s*$/,
            fields: ['', 'upQualifier', 'upValue', 'unit'],
          },
          {
            regex: /^\s*(\-?\d+[\.eE]?\-?\d*)\s*(<|<=)\s*([^\d,<=>]*)\s*$/,
            fields: ['', 'upValue', 'upQualifier', 'unit'],
          },
          {
            regex: /^\s*(\-?\d+[\.eE]?\-?\d*)\s*(>|>=)\s*([^\d,<=>]*)\s*$/,
            fields: ['', 'loValue', 'loQualifier', 'unit'],
          }
        ];
        
        for (var pi = 0;pi < parsers.length; ++pi) {
          var parse = text.match(parsers[pi].regex);
          if (!parse)
            continue;
          for (var i = 1;i < parse.length; ++i)
            if (!!parse[i]) {
              var f = parsers[pi].fields[i];
              obj[f] = parse[i];
            }

          if (parsers[pi].adjust)
            parsers[pi].adjust(obj, parse);
          if (!!obj.unit) obj.unit = obj.unit.trim();
          break;
        }
        
        if (pi >= parsers.length)
          obj.textValue = ccLib.trim(text);
          
        return obj;
      };
      
      var allTags = [].concat(kit.settings.loTags || defaultSettings.loTags, kit.settings.hiTags || defaultSettings.hiTags, kit.settings.units || defaultSettings.units);
      
      var autoEl = putAutocomplete(root, null, configEntry, {
        change: function (e, ui) {
          settings.onchange.call(this, e, field, parseValue(this.value));
        },
        source: function( request, response ) {
          // delegate back to autocomplete, but extract the last term
          response( jT.$.ui.autocomplete.filter( allTags, extractLast(request.term)));
        },
        focus: function() { // prevent value inserted on focus
          return false;
        },
        select: function( event, ui ) {
          var theVal = this.value,
              last = extractLast(theVal);
          
          this.value = theVal.substr(0, theVal.length - last.length) + ui.item.value + ' ';
          return false;
        }
      });

      // it might be a hidden one - so, take care for this      
      if (!!autoEl) autoEl.bind('keydown', function (event) {
        if ( event.keyCode === jT.$.ui.keyCode.TAB && !!autoEl.menu.active )
          event.preventDefault();
      });
    };
    
    // deal with endpoint name itself
    putAutocomplete(jT.$('div.box-endpoint', root), '/query/experiment_endpoints', ccLib.getJsonValue(config, 'effects.endpoint'));
    putAutocomplete(jT.$('div.box-interpretation', root), '/query/interpretation_result', ccLib.getJsonValue(config, 'interpretation.result'));
    
    jT.$('.box-conditions', root).hide(); // to optimize process with adding children
    if (!!settings.conditions) {
      // now put some conditions...
      var any = false;
      var condRoot = jT.$('div.box-conditions .jtox-border-box', root)[0];
      for (var cond in config.conditions) {
        any = true;
        var div = jT.getTemplate('#jtox-endcondition');
        jT.$(div).attr('data-field', cond);
        condRoot.appendChild(div);
        ccLib.fillTree(div, { title: config.conditions[cond].sTitle || cond });
        jT.$('input', div).attr('placeholder', "Enter value or range");
        putValueComplete(jT.$(div), config.conditions[cond]);
      }
      if (any)
        jT.$('.box-conditions', root).show();
    }
      
    // now comes the value editing mechanism
    var confRange = ccLib.getJsonValue(config, 'effects.result') || {};
    var confText = ccLib.getJsonValue(config, 'effects.text') || {};
    putValueComplete(jT.$('.box-value', root), confRange.bVisible === false ? confText : confRange);
    
    // now initialize other fields, marked with box-field
    jT.$('.box-field', root).each(function () {
      var name = jT.$(this).data('name');
      jT.$('input, textarea, select', this).on('change', function (e) {
        settings.onchange.call(this, e, name, jT.$(this).val());
      });
    });
  };
  
  cls.prototype = {
    init: function (settings) {
      var self = this;
      
      // we can redefine onDetails only if there is not one passed and we're asked to show editors at ll
      if (!!self.settings.showEditors && !self.settings.onDetails) {
        self.edittedValues = {};
        self.settings.onDetails = function (root, data, element) {
          self.edittedValues[data.endpoint] = {};
          cls.linkEditors(self, root.appendChild(jT.getTemplate('#jtox-endeditor')), { 
            category: data.endpoint, 
            top: data.subcategory, 
            conditions: self.settings.showConditions, 
            onchange: function (e, field, value) { ccLib.setJsonValue(self.edittedValues[data.endpoint], field, value); }
          });
        };
      }
      
      // deal if the selection is chosen
      if (!!self.settings.selectionHandler || !!self.settings.onDetails)
        jT.ui.putActions(self, self.settings.configuration.columns.endpoint.Id);
      
      self.settings.configuration.columns.endpoint.Id.sTitle = '';
      
      // again , so that changed defaults can be taken into account.
      self.settings.configuration = jT.$.extend(true, self.settings.configuration, settings.configuration);
      var cols = jT.ui.processColumns(self, 'endpoint');
      
      // make the accordition now...
    	jT.$('.jtox-categories', self.rootElement).accordion( {
    		heightStyle: self.settings.heightStyle
    	});

      self.tables = { };
      // and now - initialize all the tables...
      jT.$('table', self.rootElement).each(function () {
        var name = this.className;
        self.tables[name] = jT.ui.putTable(self, this, "endpoint", { 
          "aoColumns": cols, 
          "fnInfoCallback": self.updateStats(name),
          "aaSortingFixed": [[1, 'asc']],
          "onRow": function (nRow, aData, iDataIndex) {
            jT.$(nRow).addClass(aData.endpoint);
          }
        });
      });
      
      if (!!self.settings.hideFilter)
        jT.$('.filter-box', self.rootElement).remove();
      else {
        var filterTimeout = null;
        var fFilter = function (ev) {
          if (!!filterTimeout)
            clearTimeout(filterTimeout);
      
          var field = ev.currentTarget;
          
          filterTimeout = setTimeout(function() {
            jT.$('table', self.rootElement).each(function () {
              jT.$(this).dataTable().fnFilter(field.value);
            });
          }, 300);
        };
        
        jT.$('.filter-box input', self.rootElement).on('keydown', fFilter);
      }
      
      if (!self.settings.showMultiselect || !self.settings.selectionHandler)
        jT.$('h3 a', self.rootElement).remove();
      else
        jT.ui.installMultiSelect(self.rootElement, null, function (el) { return el.parentNode.parentNode.nextElementSibling; });
    },
    
    getValues: function (needle) {
      var self = this;
      
      var filter = null;
      if (!needle)
        filter = function (end) { return true; };
      else if (typeof needle != 'function')
        filter = function (end) { return end.indexOf(needle) >= 0; };
      else 
        filter = needle;

      for (var endpoint in self.edittedValues) {
        if (filter(endpoint))
          return self.edittedValues[endpoint];
      }
      return null;
    },
    
    updateStats: function (name) {
      var self = this;
      return function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
        var head = jT.$('h3.' + name, self.rootElement)[0];
        // now make the summary...
        var html = '';
        if (iTotal > 0) {
          var count = 0;
          var data = this.fnGetData();
          for (var i = iStart; i <= iEnd && i < iMax; ++i)
            count += data[i].count;
          html = "[" + count + "]";
        }
        else
          html = '';
        
        jT.$('div.jtox-details span', head).html(html); 
        return sPre;
      }
    },
    
    fillEntries: function (facet) {
      var self = this;
      // first we need to group them and extract some summaries
      var ends = { };
      for (var i = 0, fl = facet.length; i < fl; ++i) {
        var entry = facet[i];
        var cat = ends[entry.subcategory];
        if (cat == null)
          ends[entry.subcategory] = cat = [];

        cat.push(entry);
      }
      
      // now, as we're ready - go and fill everything
      jT.$('h3', self.rootElement).each(function () {
        var name = jT.$(this).data('cat');
        var table = self.tables[name];
        table.fnClearTable();

        var cat = ends[name.replace("_", " ")];
        if (cat != null)
          table.fnAddData(cat);
      });
    },
    
    loadEndpoints: function (uri) {
      var self = this;
      if (uri == null)
        uri = self.settings.baseUrl + '/query/study';
      else if (!self.settings.baseUrl)
        self.settings.baseUrl = jT.grabBaseUrl(uri);

      // make the call...
      jT.call(self, uri, function (result, jhr) {
        if (!result && jhr.status == 404)
          result = { facet: [] }; // empty one
        if (!!result) {
          self.summary = result.facet;
          ccLib.fireCallback(self.settings.onLoaded, self, result);
          if (!self.settings.noInterface)
            self.fillEntries(result.facet);
        }
        else {
          self.facet = null;
          ccLib.fireCallback(self.settings.onLoaded, self, result);
        }
      });
    },
    
    query: function (uri) {
      this.loadEndpoints(uri);
    },
    
    modifyUri: function (uri) {
      jT.$('input[type="checkbox"]', this.rootElement).each(function () {
        if (this.checked)
          uri = ccLib.addParameter(uri, 'feature_uris[]=' + encodeURIComponent(this.value + '/feature'));
      })
      
      return uri;
    }
  };
  
  return cls;
})();
jT.templates['widget-search']  = 
"    <div id=\"jtox-search\" class=\"jtox-search\">" +
"      <form>" +
"  	  	<div class=\"jq-buttonset jtox-inline\">" +
"  			  <input type=\"radio\" id=\"searchauto\" value=\"auto\" name=\"searchtype\" checked=\"checked\" data-placeholder=\"Enter CAS, EINECS, Chemical name, SMILES or InChI_\"/>" +
"			    <label for=\"searchauto\" title=\"Exact structure or search by an identifier. CAS, Chemical name, SMILES or InChI. The input type is guessed automatically.\">Exact structure</label>" +
"  			  <input type=\"radio\" id=\"searchsimilarity\" value=\"similarity\" name=\"searchtype\" data-placeholder=\"Enter Chemical name, SMILES or InChI_\"/>" +
"			    <label for=\"searchsimilarity\" title=\"Enter SMILES or draw structure\">Similarity</label>" +
"  			  <input type=\"radio\" id=\"searchsmarts\" value=\"smarts\" name=\"searchtype\" data-placeholder=\"Enter SMARTS_\"/>" +
"			    <label for=\"searchsmarts\" title=\"Enter or draw a SMARTS query\">Substructure</label>" +
"  			  <input type=\"radio\" id=\"searchuri\" value=\"uri\" name=\"searchtype\" data-placeholder=\"Enter URL to be examined...\"/>" +
"			    <label for=\"searchuri\" title=\"Enter dataset URL\">URL</label>" +
"  			</div>" +
"    		<div class=\"float-right search-pane\">" +
"  			  <div class=\"dynamic auto-hide searchauto hidden jtox-inline\">" +
"  			    <div>" +
"    			    <input type=\"checkbox\" name=\"regexp\" title=\"fadsfas\"/><span>Enable fragment search<sup class=\"helper\"><a target=\"_blank\" href=\"http://en.wikipedia.org/wiki/Regular_expression\"><span class=\"ui-icon ui-icon-info\"></span></a></sup></span>" +
"  			    </div>" +
"  			  </div>" +
"  			  <div class=\"dynamic auto-hide searchsimilarity hidden jtox-inline\">" +
"      			<select name='threshold' title ='Tanimoto similarity threshold'>" +
"    					<option value='0.9' selected=\"selected\">0.9</option>" +
"  			   		<option value='0.8' >0.8</option>" +
"  			   		<option value='0.7' >0.7</option>" +
"  			   		<option value='0.6' >0.6</option>" +
"  			   		<option value='0.5' >0.5</option>" +
"  			   		<option value='0.4' >0.4</option>" +
"  			   		<option value='0.3' >0.3</option>" +
"  			   		<option value='0.2' >0.2</option>" +
"  			   		<option value='0.1' >0.1</option>" +
"      			</select>	" +
"  			  </div>" +
"  			  <div class=\"dynamic auto-hide searchsmarts hidden jtox-inline\">" +
"  			    <select name=\"smarts\" title =\"Predefined functional groups\"></select>" +
"  			  </div>" +
"  			  <div class=\"jtox-inline\">" +
"            <input type=\"text\" name=\"searchbox\"/>" +
"            <button name=\"searchbutton\" class=\"jtox-handler\" title=\"Search/refresh\" data-handler=\"query\"><span class=\"ui-icon ui-icon-search\"/></button>" +
"            <button name=\"drawbutton\" class=\"dynamic\" title=\"Draw the (sub)structure\"><span class=\"ui-icon ui-icon-pencil\"/></button>" +
"  			  </div>" +
"    		</div>" +
"    		<div id=\"searchcontext\" class=\"size-full\">" +
"    		  <input type=\"text\" name=\"searchcontext\" placeholder=\"Restrict the search within given dataset_\"/>" +
"    		</div>" +
"      </form>" +
"      <div class=\"ketcher shrinken\"></div>" +
"    </div>" +
""; // end of #jtox-ds-search 

jT.templates['ketcher-buttons']  = 
"    <button id=\"ketcher-usebutton\">Use <span class=\"ui-icon ui-icon-arrowthick-1-n\"></span></button>" +
"    <button id=\"ketcher-drawbutton\">Draw <span class=\"ui-icon ui-icon-arrowthick-1-s\"></span></button>" +
""; // end of ketcher-buttons 

jT.templates['all-compound']  = 
"	  <div id=\"jtox-compound\" class=\"jtox-compound\">" +
"	    <div class=\"jtox-ds-features\"></div>" +
"	    <div class=\"jtox-controls\">" +
"	      Showing from <span class=\"data-field high\" data-field=\"pagestart\">?</span> to <span class=\"data-field high\" data-field=\"pageend\">?</span><span class=\"data-field\" data-field=\"filtered-text\"> </span>in pages of <select class=\"data-field\" data-field=\"pagesize\">" +
"          <option value=\"10\" selected=\"yes\">10</option>" +
"          <option value=\"20\">20</option>" +
"          <option value=\"30\">30</option>" +
"          <option value=\"50\">50</option>" +
"          <option value=\"100\">100</option>" +
"        </select> entries" +
"	      <a class=\"paginate_disabled_previous prev-field\" tabindex=\"0\" role=\"button\">Previous</a><a class=\"paginate_enabled_next next-field\" tabindex=\"0\" role=\"button\">Next</a>" +
"	      <input type=\"text\" class=\"filterbox\" placeholder=\"Filter...\" />" +
"	    </div>" +
"	    <div class=\"jtox-ds-tables\">" +
"	      <div class=\"jt-processing\">	        " +
"          <span class=\"message\">Loading compounds...</span>" +
"	      </div>" +
"	      <div class=\"jtox-ds-fixed\">" +
"	        <table></table>" +
"	      </div><div class=\"jtox-ds-variable\">" +
"	        <table></table>" +
"	      </div>" +
"	    </div>" +
"	  </div>" +
""; // end of #jtox-compound 

jT.templates['compound-one-tab']  = 
"    <div id=\"jtox-ds-selection\" class=\"jtox-selection\">" +
"      <a href=\"#\" class=\"multi-select select\">select all</a>&nbsp;<a href=\"#\" class=\"multi-select unselect\">unselect all</a>" +
"    </div>" +
""; // end of #compound-one-tab 

jT.templates['compound-one-feature']  = 
"    <div id=\"jtox-ds-feature\" class=\"jtox-ds-feature\"><input type=\"checkbox\" checked=\"yes\" class=\"jtox-checkbox\" /><span class=\"data-field jtox-title\" data-field=\"title\"> ? </span><sup class=\"helper\"><a target=\"_blank\" class=\"data-field attribute\" data-attribute=\"href\" data-field=\"uri\"><span class=\"ui-icon ui-icon-info\"></span></a></sup></div>" +
""; // end of #jtox-ds-feature 

jT.templates['compound-download']  = 
"    <div id=\"jtox-ds-download\" class=\"jtox-inline jtox-ds-download\">" +
"      <a target=\"_blank\"><img class=\"borderless\"/></a>" +
"    </div>" +
""; // end of #jtox-ds-download 

jT.templates['compound-export']  = 
"    <div id=\"jtox-ds-export\">" +
"      <div class=\"jtox-inline\">Download dataset as: </div>" +
"      <div class=\"jtox-inline jtox-exportlist\"></div>" +
"    </div>" +
""; // end of #jtox-ds-export 

jT.templates['all-dataset']  = 
"	  <div id=\"jtox-dataset\" class=\"jtox-dataset\">" +
"      <table></table>" +
"	  </div>" +
""; // end of #jtox-dataset 

jT.templates['all-model']  = 
"	  <div id=\"jtox-model\" class=\"jtox-model\">" +
"      <table></table>" +
"	  </div>" +
""; // end of #jtox-model 

jT.templates['all-substance']  = 
"	  <div id=\"jtox-substance\" class=\"jtox-substance\">" +
"	    <div class=\"jtox-controls\">" +
"	      Showing from <span class=\"data-field high\" data-field=\"pagestart\"> ? </span> to <span class=\"data-field high\" data-field=\"pageend\"> ? </span><span class=\"filtered-text\"> </span>in pages of <select class=\"data-field\" data-field=\"pagesize\">" +
"          <option value=\"10\" selected=\"yes\">10</option>" +
"          <option value=\"20\">20</option>" +
"          <option value=\"50\">50</option>" +
"          <option value=\"100\">100</option>" +
"          <option value=\"200\">200</option>" +
"          <option value=\"500\">500</option>" +
"        </select> substances" +
"	      <a class=\"paginate_disabled_previous prev-field\" tabindex=\"0\" role=\"button\">Previous</a><a class=\"paginate_enabled_next next-field\" tabindex=\"0\" role=\"button\">Next</a>" +
"	      <input type=\"text\" class=\"filterbox\" placeholder=\"Filter...\" />" +
"	    </div>" +
"	    <div>" +
"        <table></table>" +
"	    </div>" +
"	  </div>" +
""; // end of #jtox-substance 

jT.templates['all-composition']  = 
"    <div id=\"jtox-composition\" class=\"jtox-composition unloaded\">" +
"      <table class=\"dataTable composition-info font-small\">" +
"        <thead>" +
"          <tr><th>Composition name:</th><td class=\"data-field camelCase\" data-field=\"name\"> ? </td></tr>" +
"          <tr><th>Composition UUID:</th><td class=\"data-field\" data-field=\"uuid\"> ? </td></tr>" +
"          <tr><th>Purity of IUC Substance:</th><td class=\"data-field\" data-field=\"purity\"> ? </td></tr>" +
"        </thead>" +
"      </table>" +
"      <table class=\"composition-table\"></table>" +
"    </div>" +
""; // end of #jtox-composition 

jT.templates['all-studies']  = 
"	  <div id=\"jtox-studies\">" +
"	    <ul>" +
"	      <li><a href=\"#jtox-substance\">IUC Substance</a></li>" +
"	      <li><a href=\"#jtox-compo-tab\">Composition</a></li>" +
"	    </ul>" +
"	    <div id=\"jtox-substance\" class=\"jtox-substance\">" +
"	      <table class=\"dataTable\">" +
"	        <thead>" +
"	          <tr>" +
"	            <th class=\"right size-third\">IUC Substance name:</th>" +
"	            <td class=\"data-field camelCase\" data-field=\"name\"> ? </td>" +
"	          </tr>" +
"	          <tr>" +
"	            <th class=\"right\">IUC Substance UUID:</th>" +
"	            <td class=\"data-field\" data-field=\"i5uuid\"> ? </td>" +
"	          </tr>" +
"	          <tr>" +
"	            <th class=\"right\">IUC Public name:</th>" +
"	            <td class=\"data-field camelCase\" data-field=\"publicname\"> ? </td>" +
"	          </tr>" +
"	          <tr>" +
"	            <th class=\"right\">Legal entity:</th>" +
"	            <td class=\"data-field\" data-field=\"ownerName\"> ? </td>" +
"	          </tr>" +
"	          <tr>" +
"	            <th class=\"right\">Legal entity UUID:</th>" +
"	            <td class=\"data-field\" data-field=\"ownerUUID\"> ? </td>" +
"	          </tr>" +
"	          <tr>" +
"	            <th class=\"right\">Type substance composition:</th>" +
"	            <td class=\"data-field\" data-field=\"substanceType\"> ? </td>" +
"	          </tr>" +
"	          <tr class=\"borderless-bottom\">" +
"	            <th class=\"right\">IUC Substance Reference Identifier</th>" +
"	            <td></td>" +
"	          </tr>" +
"	          <tr class=\"borderless-top borderless-bottom\">" +
"	            <td class=\"right\">CAS:</td>" +
"	            <td class=\"data-field\" data-field=\"compound.cas\"> ? </td>" +
"	          </tr>" +
"	          <tr class=\"borderless-top borderless-bottom\">" +
"	            <td class=\"right\">EC:</td>" +
"	            <td class=\"data-field\" data-field=\"compound.einecs\"> ? </td>" +
"	          </tr>" +
"	          <tr class=\"borderless-top borderless-bottom\">" +
"	            <td class=\"right\">Chemical name:</td>" +
"	            <td class=\"data-field\" data-field=\"compound.name\"> ? </td>" +
"	          </tr>" +
"	          <tr class=\"borderless-top borderless-bottom\">" +
"	            <td class=\"right\">IUPAC name:</td>" +
"	            <td class=\"data-field\" data-field=\"compound.iupac\"> ? </td>" +
"	          </tr>" +
"	          <tr class=\"borderless-top borderless-bottom\">" +
"	            <td class=\"right\">UUID:</td>" +
"	            <td class=\"data-field\" data-field=\"referenceSubstance.i5uuid\"> ? </td>" +
"	          </tr>" +
"	          <tr class=\"borderless-top\">" +
"	            <td class=\"right\">IUC Flags:</td>" +
"	            <td class=\"data-field\" data-field=\"IUCFlags\"> ? </td>" +
"	          </tr>" +
"	        </thead>" +
"	      </table>" +
"	    </div>" +
"	    <div id=\"jtox-compo-tab\" class=\"jtox-compo-tab\"></div>" +
"	  </div>" +
""; // end of #jtox-studies 

jT.templates['one-category']  = 
"    <div id=\"jtox-study-tab\" class=\"jtox-study-tab unloaded\">" +
"    	<div class=\"float-right\">" +
"      	<button class=\"expand-all\">Expand all</button><button class=\"collapse-all\">Collapse all</button>" +
"    	</div>" +
"      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\" /></p>" +
"      <h4 class=\"data-field camelCase\" data-field=\"showname\"> ? </h4>" +
"    </div>" +
""; // end of #jtox-category 

jT.templates['one-study']  = 
"    <div id=\"jtox-study\" class=\"jtox-study jtox-foldable folded\">" +
"      <div class=\"title\"><p class=\"data-field\" data-field=\"title\">? (0)</p></div>" +
"      <div class=\"content\">" +
"        <table class=\"jtox-study-table content\"></table>" +
"      </div>" +
"    </div>" +
""; // end of #jtox-study 

jT.templates['policy']  = 
"	  <div id=\"jtox-policy\" class=\"jtox-auth\">" +
"      <table></table>" +
"	  </div>" +
""; // end of #jtox-model 

jT.templates['logger']  = 
"	  <div id=\"jtox-logger\">" +
"	    <div class=\"list-wrap\">" +
"	      <div class=\"list-root\"></div>" +
"	    </div>" +
"	    <div class=\"status\"><div class=\"icon jtox-fadable\"></div></div>" +
"	  </div>" +
""; // end of #jtox-logger 

jT.templates['logline']  = 
"	  <div id=\"jtox-logline\" class=\"logline\">" +
"  	    <div class=\"icon\"></div>" +
"  	    <span class=\"content data-field\" data-field=\"header\"></span>" +
"  	    <div class=\"details data-field\" data-field=\"details\"></div>" +
"	  </div>" +
""; // end of #jtox-logline 

jT.templates['all-endpoint']  = 
"	  <div id=\"jtox-endpoint\" class=\"jtox-endpoint\">" +
"	    <div class=\"size-full filter-box\" style=\"height: 35px\"><input type=\"text\" class=\"float-right ui-input\" placeholder=\"Filter...\" /></div>" +
"	    <div class=\"jtox-categories\">" +
"    		<h3 class=\"P-CHEM\" data-cat=\"P-CHEM\">P-Chem <div class=\"float-right jtox-inline jtox-details\"><a href=\"#\" class=\"select-all\">select all</a>&nbsp;<a href=\"#\" class=\"unselect-all\">unselect all</a><span style=\"margin-left: 10px\"></span></div></h3>" +
"    		<div>" +
"    		  <table class=\"P-CHEM\"></table>" +
"    		</div>" +
"    		<h3 class=\"ENV_FATE\" data-cat=\"ENV_FATE\">Env Fate <div class=\"float-right jtox-inline jtox-details\"><a href=\"#\" class=\"select-all\">select all</a>&nbsp;<a href=\"#\" class=\"unselect-all\">unselect all</a><span style=\"margin-left: 10px\"></span></div></h3>" +
"    		<div>" +
"      		<table class=\"ENV_FATE\"></table>" +
"    		</div>" +
"    		<h3 class=\"ECOTOX\" data-cat=\"ECOTOX\">Eco Tox <div class=\"float-right jtox-inline jtox-details\"><a href=\"#\" class=\"select-all\">select all</a>&nbsp;<a href=\"#\" class=\"unselect-all\">unselect all</a><span style=\"margin-left: 10px\"></span></div></h3>" +
"    		<div>" +
"      		<table class=\"ECOTOX\"></table>" +
"    		</div>" +
"    		<h3 class=\"TOX\" data-cat=\"TOX\">Tox <div class=\"float-right jtox-inline jtox-details\"><a href=\"#\" class=\"select-all\">select all</a>&nbsp;<a href=\"#\" class=\"unselect-all\">unselect all</a><span style=\"margin-left: 10px\"></span></div></h3>" +
"    		<div>" +
"    		  <table class=\"TOX\"></table>" +
"    		</div>" +
"	    </div>" +
"	  </div>" +
""; // end of #jtox-endpoint 

jT.templates['editor-endpoint']  = 
"	  <div id=\"jtox-endeditor\" class=\"jt-endeditor\">" +
"	    <div class=\"jtox-medium-box box-endpoint\" data-field=\"endpoint\">" +
"  	    <div class=\"jtox-details font-heavy\">Endpoint name</div>" +
"  	    <input type=\"text\" placeholder=\"Endpoint_\"/>" +
"	    </div>" +
"	    <div class=\"jtox-medium-box box-value\" data-field=\"value\">" +
"  	    <div class=\"jtox-details font-heavy\">Value range</div>" +
"  	    <input type=\"text\"/>" +
"	    </div>" +
"      <div class=\"jtox-medium-box box-interpretation\" data-field=\"interpretation_result\">" +
"  	    <div class=\"jtox-details font-heavy\">Intepretation of the results</div>" +
"  	    <input type=\"text\" placeholder=\"Intepretation\"/>" +
"      </div>" +
"      <div class=\"size-full box-conditions\">" +
"	      <div class=\"jtox-details font-heavy\">Conditions</div>" +
"        <div class=\"jtox-border-box\"></div>" +
"      </div>" +
"	  </div>" +
""; // end of #jtox-end-editor 

jT.templates['editor-endpoint-condition']  = 
"    <div id=\"jtox-endcondition\" class=\"jtox-medium-box\">" +
"      <div class=\"jtox-details font-heavy data-field\" data-field=\"title\">?</div>" +
"      <input type=\"text\" placeholder=\"Intepretation\"/>" +
"    </div>" +
""; // end of #jtox-end-editor 


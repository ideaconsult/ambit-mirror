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

  fireCallback: function (callback, self) {
    if (!jQuery.isArray(callback))
      callback = [callback];
      
    var ret = null;
    for (var i = 0, cl = callback.length; i < cl; ++i) {
      var callone = callback[i];
      if (typeof callone != 'function')
        callone = window[callone];
      ret = (typeof callone == 'function') ? (callone.apply((self !== undefined && self != null) ? self : document, Array.prototype.slice.call(arguments, 2))) : null;
    }
    return ret;
  },
  
  /* Function setObjValue(obj, value)Set a given to the given element (obj) in the most appropriate way - be it property - the necessary one, or innetHTML
  */
  setObjValue: function (obj, value){
  	if ((value === undefined || value === null) && jQuery(obj).data('default') !== undefined)
  		value = jQuery(obj).data('default');
  
    if (obj.nodeName == "INPUT" || obj.nodeName == "SELECT")
      obj.value = value;
    else if (obj.nodeName == "IMG")
      obj.src = value;
    else if (obj.nodeName == "BUTTON")
  		jQuery(obj).data('value', value);
    else
      obj.innerHTML = value;      
  },

  isNull: function(obj) {
    return obj === undefined || obj == null;
  },
  
  isEmpty: function(obj) {
    var empty = true;
    if (obj !== undefined || obj != null) {
      if (typeof obj == 'object') {
        for (var i in obj) {
          if (obj[i] != null) {
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
    if (field !== undefined){
      try {
        eval("json." + field + " = val");
      }
      catch(e){
        ;
      }
    }  
  },
  
  getJsonValue: function (json, field){
    var value = undefined;
    if (field !== undefined) {
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
  
  // Prepare a form so that non-empty fields are checked before submit and accumuater fields
  // are accumulated. Call it after you've set submit behavior, etc.
  prepareForm: function (form) {
  	var self = this;
	  var $ = window.jQuery;

		// first - attach the accumulators handler.	  
	  $('.accumulate', form).each(function (){
		  $(this).on('change', function (e) {
			  var target = $(this).data('accumulate');
			  if (!!target) {
			  	target = this.form[target];
			  	var val = target.value.replace(new RegExp('(' + this.value + ')'), '');
			  	if (this.checked)
				  	val += ',' + this.value;

				  target.value = val.replace(/,,/g, ',').replace(/^,/g, '').replace(/,$/g, ''); // change double commas with one, and replaces commas at the beginning and at the end
			  }
				return false;
		  })
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
/* toxquery.js - Universal query widget, that can work with any kit (study or compound) inside
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxQuery = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    scanDom: true,
    initialQuery: false,
    dom: {
      kit: null, // ... here.
      widgets: {},
    },

    configuration: {
      // this is the main thing to be configured
      handlers: { 
        query: function (el, query) { query.query(); },
      }
    }
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized
    
    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);
    self.mainKit = null;
        
    if (self.settings.scanDom) {
      jT.$('.jtox-toolkit', self.rootElement).each(function () {
        if (jT.$(this).hasClass('jtox-widget'))
          self.settings.dom.widgets[jT.$(this).data('kit')] = this;
        else 
          self.settings.dom.kit = this;
      });
    }

    self.initHandlers();
    // finally, wait a bit for everyone to get initialized and make a call, if asked to
    if (!!self.settings.initialQuery)
      setTimeout(function () { self.query(); }, 200);
  };
  
  cls.prototype = {
    addHandlers: function (handlers) {
      self.settings.configuration.handlers = jT.$.extend(self.settings.configuration.handlers, handlers);
    },
    
    widget: function (name) {
      return this.settings.dom.widgets[name];
    },
    
    kit: function () {
      if (!this.mainKit)
        this.mainKit = jT.kit(this.settings.dom.kit);
        
      return this.mainKit;
    },
    
    initHandlers: function () {
      var self = this;
      var fireHandler = function (e) {
        var handler = self.settings.configuration.handlers[jT.$(this).data('handler')];
        if (!!handler)
          ccLib.fireCallback(handler, this, this, self);
        else
          console.log("jToxQuery: referring unknown handler: " + jT.$(this).data('handler'));
      };
      
      jT.$(document).on('change', '.jtox-handler', fireHandler);
      jT.$(document).on('click', 'button.jtox-handler', fireHandler);
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
    defaultSmiles: '50-00-0',
    smartsList: 'funcgroups',
    configuration: {
      handlers: { }
    }
  };
  
  var queries = {
    'auto': "/query/compound/search/all",
    'url': "/query/compound/url/all",
    'similarity': "/query/similarity",
    'smarts': "/query/smarts"
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized
    
    self.settings = jT.$.extend({}, defaultSettings, jT.settings, settings);
    self.rootElement.appendChild(jT.getTemplate('#jtox-search'));
    self.queryKit = jT.parentKit(jToxQuery, self.rootElement);
    
    self.search = { mol: "", type: ""};
    
    var form = jT.$('form', self.rootElement)[0];
    form.onsubmit = function () { return false; }

    var radios = jT.$('.jq-buttonset', root).buttonset();
    var onTypeClicked = function () {
      form.searchbox.placeholder = jT.$(this).data('placeholder');
      jT.$('.search-pane .dynamic').addClass('hidden');
      jT.$('.search-pane .' + this.id).removeClass('hidden');
    };
    
    jT.$('.jq-buttonset input', root).on('change', onTypeClicked);
    ccLib.fireCallback(onTypeClicked, jT.$('.jq-buttonset input', root)[0]);
    
    jT.$(form.searchbox)
    .on('focus', function () {
      var gap = jT.$(form).width() - jT.$(radios).width() - 30 - jT.$('.search-pane').width();
      var oldSize = $(this).width();
      jT.$(this).css('width', '' + (oldSize + gap) + 'px');
    })
    .on('blur', function () {
      jT.$(this).css('width', '');
    })
    .on('change', function () { // when we change the value here - all, possible MOL caches should be cleared.
      if (this.value.indexOf('http') > -1)
        self.setURL();
      else
        self.setAuto();
    });
    
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
    if (!!self.settings.b64search) {
      self.setMol($.base64.decode(self.settings.b64search));
    }
    else if (!!self.settings.search) {
      if (self.settings.search.indexOf('http') > -1)
        self.setURL(self.settings.search);
      else
        self.setAuto(self.settings.search);
    }
  };
  
  cls.prototype = {
    // required from jToxQuery - this is how we add what we've collected
    modifyUri: function (uri) {
      var form = jT.$('form', this.rootElement)[0];
      var params = { type: this.search.type };
      var type = jT.$('input[name="searchtype"]:checked', form).val();
      
      if (type == "auto" && params.type == 'url')
        type = "url";
        
      var res = queries[type] + (uri.indexOf('?') > -1 ? '' : '?') + uri;

      if (!!this.search.mol) {
        params.b64search = $.base64.encode(this.search.mol);
      }
      else {
        params.search = form.searchbox.value;
        if (!params.search)
          params.search = this.settings.defaultSmiles;
          this.setAuto(params.search);
      }
        
      if (type == 'similarity')
        params.threshold = form.threshold.value;
      
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
    
    setURL: function (url) {
      this.search.mol = null;
      this.search.type = 'url';
      var box = jT.$('form', this.rootElement)[0].searchbox;

      if (!!this.search.oldplace)
        box.placeholder = this.search.oldplace;
        
      if (url != null)
        box.value = url;
    }
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
    "showTabs": true,         // should we show tabs with groups, or not
    "showExport": true,       // should we add export tab up there
    "showControls": true,     // should we show the pagination/navigation controls.
    "hideEmpty": false,       // whether to hide empty groups instead of making them inactive
    "hasDetails": true,       // whether browser should provide the option for per-item detailed info rows.
    "pageSize": 20,           // what is the default (startint) page size.
    "pageStart": 0,           // what is the default startint point for entries retrieval
    "rememberChecks": false,  // whether to remember feature-checkbox settings between queries
    "metricFeature": "http://www.opentox.org/api/1.1#Similarity",   // This is the default metric feature, if no other is specified
    "onLoaded": null,         // invoked when a set of compound is loaded.
    "onPrepared": null,       // invoked when the initial call for determining the tabs/columns is ready
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

          for (var f in miniset.features) {
            var feat = miniset.features[f];
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
          for (var f in miniset.features) {
            if (!miniset.features[f].used && !miniset.features[f].basic)
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
      	// and one for unified way of processing diagram
      	"http://www.opentox.org/api/1.1#Diagram": {title: "Diagram", search: false,
      	  process: function(entry, fId, features) {
            entry.compound.diagramUri = entry.compound.URI.replace(/(.+)(\/conformer.*)/, "$1") + "?media=image/png";
      	  },
      	  render: function(col, fId){
      	    col["mData"] = "compound.diagramUri";
            col["mRender"] = function(data, type, full) {
              return (type != "display") ? "-" : '<a target="_blank" href="' + full.compound.URI + '"><img src="' + data + '" class="jtox-ds-smalldiagram"/></a>';
            };
            col["sClass"] = "paddingless";
            col["sWidth"] = "125px";
            return col;
        	},
        	visibility: "main"
      	},
      	"http://www.opentox.org/api/1.1#Similarity": {title: "Similarity", location: "compound.metric", search: true, used: true},
      }
    }
  };

  /* define the standard features-synonymes, working with 'sameAs' property. Beside the title we define the 'location' property
  as well which is used in processEntry() to location value(s) from given (synonym) properties into specific property of the compound entry itself.
  'location' can be an array, which results in adding value to several places.
  */
  var baseFeatures = {
    "http://www.opentox.org/api/1.1#REACHRegistrationDate" : { title: "REACH Date", location: "compound.reachdate", accumulate: true, basic: true},
    "http://www.opentox.org/api/1.1#CASRN" : { title: "CAS", location: "compound.cas", accumulate: true, basic: true},
  	"http://www.opentox.org/api/1.1#ChemicalName" : { title: "Name", location: "compound.name", accumulate: true, basic: true},
  	"http://www.opentox.org/api/1.1#TradeName" : {title: "Trade Name", location: "compound.tradename", accumulate: true, basic: true},
  	"http://www.opentox.org/api/1.1#IUPACName": {title: "IUPAC Name", location: ["compound.name", "compound.iupac"], accumulate: true, basic: true},
  	"http://www.opentox.org/api/1.1#EINECS": {title: "EINECS", location: "compound.einecs", accumulate: true, basic: true},
    "http://www.opentox.org/api/1.1#InChI": {title: "InChI", location: "compound.inchi", shorten: true, accumulate: true, basic: true},
  	"http://www.opentox.org/api/1.1#InChI_std": {title: "InChI", location: "compound.inchi", shorten: true, accumulate: true, used: true, basic: true},
    "http://www.opentox.org/api/1.1#InChIKey": {title: "InChI Key", location: "compound.inchikey", accumulate: true, basic: true},
  	"http://www.opentox.org/api/1.1#InChIKey_std": {title: "InChI Key", location: "compound.inchikey", accumulate: true, used: true, basic: true},
    "http://www.opentox.org/api/1.1#InChI_AuxInfo": {title: "InChI Aux", location: "compound.inchi", accumulate: true, used: true, basic: true},
  	"http://www.opentox.org/api/1.1#InChI_AuxInfo_std": {title: "InChI Aux", location: "compound.inchi", accumulate: true, used:true, basic: true},
  	"http://www.opentox.org/api/1.1#IUCLID5_UUID": {title: "IUCLID5 UUID", location: "compound.i5uuid", shorten: true, accumulate: true, basic: true},
  	"http://www.opentox.org/api/1.1#SMILES": {title: "SMILES", location: "compound.smiles", shorten: true, accumulate: true, basic: true},
  	"http://www.opentox.org/api/dblinks#CMS": {title: "CMS", accumulate: true, basic: true},
  	"http://www.opentox.org/api/dblinks#ChEBI": {title: "ChEBI", accumulate: true, basic: true},
  	"http://www.opentox.org/api/dblinks#Pubchem": {title: "PubChem", accumulate: true, basic: true},
  	"http://www.opentox.org/api/dblinks#ChemSpider": {title: "ChemSpider", accumulate: true, basic: true},
  	"http://www.opentox.org/api/dblinks#ChEMBL": {title: "ChEMBL", accumulate: true, basic: true},
  	"http://www.opentox.org/api/dblinks#ToxbankWiki": {title: "Toxbank Wiki", accumulate: true, basic: true},
  };
  var instanceCount = 0;

  // constructor
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even in manual initialization.
    
    var newDefs = jT.$.extend(true, { "configuration" : { "baseFeatures": baseFeatures} }, defaultSettings);
    self.settings = jT.$.extend(true, {}, newDefs, jT.settings, settings); // i.e. defaults from jToxCompound
    self.instanceNo = instanceCount++;
    if (self.settings.rememberChecks && self.settings.showTabs)
      self.featureStates = {};

    // finally make the query, if Uri is provided      
    if (self.settings['datasetUri'] !== undefined){
      self.queryDataset(self.settings['datasetUri']);
    }
  };
  
  // now follow the prototypes of the instance functions.
  cls.prototype = {
    init: function () {
      var self = this;
      
      self.features = null; // features, as downloaded from server, after being processed.
      self.dataset = null; // the last-downloaded dataset.
      self.groups = null; // computed groups, i.e. 'groupName' -> array of feature list, prepared.
      self.fixTable = self.varTable = null; // the two tables - to be initialized in prepareTables.
      self.entriesCount = null;
      self.suspendEqualization = false;
      self.orderList = [];
      self.usedFeatures = [];
      
      self.rootElement.appendChild(jT.getTemplate('#jtox-compound'));
      
      // now make some action handlers - on next, prev, filter, etc.
      var pane = jT.$('.jtox-ds-control', self.rootElement)[0];
      if (self.settings.showControls) {
        ccLib.fillTree(pane, { "pagesize": self.settings.pageSize });
        jT.$('.next-field', pane).on('click', function() { self.nextPage(); });
        jT.$('.prev-field', pane).on('click', function() { self.prevPage(); });
        jT.$('select', pane).on('change', function() { self.queryEntries(self.settings.pageStart, parseInt(jT.$(this).val())); })
        var pressTimeout = null;
        jT.$('input', pane).on('keydown', function() { 
          if (pressTimeout != null)
            clearTimeout(pressTimeout);
          pressTimeout = setTimeout(function(){
            self.updateTables();
          }, 350);
        });
      }
      else // ok - hide me
        pane.style.display = "none";
    },
    
    clearDataset: function () {
      if (this.usedFeatures !== undefined) {      
        jT.$(this.rootElement).empty();
        for (var i = 0, fl = this.usedFeatures.length; i < fl; ++i) {
          var fid = this.usedFeatures[i];
          this.features[fid].used = false;
        }
      }
    },
    
    /* make a tab-based widget with features and grouped on tabs. It relies on filled and processed 'self.features' as well
    as created 'self.groups'.
    */
    prepareTabs: function (root, isMain, nodeFn, divFn) {
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
        var grId = "jtox-ds-" + gr.replace(/\s/g, "_") + "-" + self.instanceNo;
        var tabLi = createATab(grId, gr.replace(/_/g, " "));
        
        // now prepare the content...
        var divEl = document.createElement('div');
        divEl.id = grId;
        all.appendChild(divEl);
        
        // .. check if we have something else to add in between
        if (typeof divFn == 'function')
          divEl = divFn(gr, divEl); // it's expected to attach it

        // ... and fill it.
        var grp = self.groups[gr];
        var empty = true;
        ccLib.enumObject(self.groups[gr], function (fId, idx, level) {
          var vis = (self.features[fId] || {})['visibility'];
          if (!!vis && (vis == "none" || (!isMain && vis == 'main') || (isMain && vis == "details")))
            return;
          empty = false;
          if (idx == "name") {
            if (isMain) {
              var fEl = nodeFn(null, fId, divEl);
              if (self.settings.rememberChecks)
                jT.$('input[type="checkbox"]', fEl)[0].checked = (self.featureStates[fId] === undefined || self.featureStates[fId]);
            }
          }
          else if (!isMain || level == 1) {
            var title = self.features[fId].title;
            if (!ccLib.isNull(title)) {
              var fEl = nodeFn(fId, title, divEl);
              if (isMain && self.settings.rememberChecks)
                jT.$('input[type="checkbox"]', fEl)[0].checked = (self.featureStates[fId] === undefined || self.featureStates[fId]);
            }
          }
        });

        if (empty) {
          if (self.settings.hideEmpty) {
            jT.$(divEl).remove();
            jT.$(tabLi).remove();
            --idx;
          }
          else
            emptyList.push(idx);
        }
        ++idx;
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
      }
      
      // now show the whole stuff and mark the disabled tabs
      all.style.display = "block";
      return jT.$(all).tabs({ collapsible: isMain, disabled: emptyList, heightStyle: isMain ? "content" : "fill" });
    },
    
    equalizeTables: function () {
      var self = this;
      if (!self.suspendEqualization && self.fixTable != null && self.varTable != null) {
        ccLib.equalizeHeights(self.fixTable.tHead, self.varTable.tHead);
        ccLib.equalizeHeights(self.fixTable.tBodies[0], self.varTable.tBodies[0]);
      }
    },
    
    featureValue: function (fId, data) {
      var self = this;
      var feature = self.features[fId];
      if (feature.location !== undefined)
        return ccLib.getJsonValue(data, jT.$.isArray(feature.location) ? feature.location[0] : feature.location);
      else
        return data.values[fId];
    },
    
    featureUri: function (fId) {
      var self = this;
      var origId = self.features[fId].originalId;
      return ccLib.isNull(origId) ? fId : origId;
    },
    
    prepareTables: function() {
      var self = this;
      var varCols = [];
      var fixCols = [];
      
      var colList = fixCols;
      // enter the first column - the number.
      
      fixCols.push({
          "mData": "number",
          "sClass": "middle",
          "mRender": function (data, type, full) { 
            return (type != "display") ?
              '' + data : 
              "&nbsp;-&nbsp;" + data + "&nbsp;-&nbsp;<br/>" + 
                (self.settings.hasDetails ?              
                  '<span class="jtox-details-open ui-icon ui-icon-circle-triangle-e" title="Press to open/close detailed info for this compound"></span>'
                  : '');
          }
        },
        { "sClass": "jtox-hidden", "mData": "index", "sDefaultContent": "-", "bSortable": true, "mRender": function(data, type, full) { return ccLib.isNull(self.orderList) ? 0 : self.orderList[data]; } }, // column used for ordering
        { "sClass": "jtox-hidden jtox-ds-details paddingless", "mData": "index", "sDefaultContent": "-", "mRender": function(data, type, full) { return ''; } } // details column
      );
      
      varCols.push({ "sClass": "jtox-hidden jtox-ds-details paddingless", "mData": "index", "mRender": function(data, type, full) { return ''; }  });

      // prepare the function for column switching...      
      var fnShowColumn = function() {
        var dt = $(this).data();
        var cells = jT.$(dt.sel + ' table tr>*:nth-child(' + (dt.idx + 1) + ')', self.rootElement);
        if (this.checked)
          jT.$(cells).show();
        else
          jT.$(cells).hide();
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
      
      var fnShowDetails = (self.settings.hasDetails ? function(row) {
        var cell = jT.$(".jtox-ds-details", row)[0];
        var idx = jT.$(row).data('jtox-index');
        jT.$(row).toggleClass('jtox-detailed-row');
        var toShow = jT.$(row).hasClass('jtox-detailed-row');

        // now go and expand both fixed and variable table details' cells.
        fnExpandCell(cell, toShow);
        var varCell = document.getElementById('jtox-var-' + self.instanceNo + '-' + idx).firstElementChild;
        fnExpandCell(varCell, toShow);
        
        var iconCell = jT.$('.jtox-details-open', row);
        jT.$(iconCell).toggleClass('ui-icon-circle-triangle-e');
        jT.$(iconCell).toggleClass('ui-icon-circle-triangle-w');

        if (toShow) {
          // i.e. we need to show it - put the full sized diagram in the fixed part and the tabs in the variable one...
          var full = self.dataset.dataEntry[idx];
          
          var detDiv = document.createElement('div');
          varCell.appendChild(detDiv);
          
          var img = new Image();
          img.onload = function(e) {
            self.equalizeTables();
            jT.$(detDiv).height(varCell.parentNode.clientHeight - 1);
            self.prepareTabs(detDiv, false, 
              function (id, name, parent) {
                var fEl = null;
                if (id != null) {
                  fEl = jT.getTemplate('#jtox-one-detail');
                  parent.appendChild(fEl);
                  ccLib.fillTree(fEl, {title: name, value: self.featureValue(id, full), uri: self.featureUri(id)});
                }
                return fEl;
              },
              function (id, parent) {
                var tabTable = jT.getTemplate('#jtox-details-table');
                parent.appendChild(tabTable);
                return tabTable;  
              }
            );
          };
          img.src = full.compound.diagramUri;
          cell.appendChild(img);
        }
        else {
          // i.e. we need to hide
          jT.$(cell).empty();
          jT.$(varCell).empty();
          self.equalizeTables();
        }
      } : null); // fnShowDetails definition end

      // make a query for all checkboxes in the main tab, so they can be traversed in parallel with the features and 
      // a change handler added.
      var checkList = jT.$('.jtox-ds-features .jtox-checkbox', self.rootElement);
      var checkIdx = -1;
      
      // now proceed to enter all other columns
      for (var gr in self.groups) {
        ccLib.enumObject(self.groups[gr], function (fId, idx, level) {
          if (idx == "name") {
            ++checkIdx;
            return;
          }
            
          var feature = self.features[fId];
          var col = {
            "sTitle": feature.title.replace(/_/g, ' ') + (ccLib.isNull(feature.units) ? "" : feature.units),
            "sDefaultContent": "-",
          };
          
          if (feature.location !== undefined)
            col["mData"] = feature.location;
          else {
            col["mData"] = 'values';
            col["mRender"] = (function(featureId) { return function(data, type, full) { var val = data[featureId]; return ccLib.isEmpty(val) ? '-' : val }; })(fId);
          }
          
          // some special cases, like diagram
          if (feature.render !== undefined) 
            col = ccLib.fireCallback(feature.render, self, col, fId);
          
          if (!!feature.shorten) {
            col["mRender"] = function(data, type, full) {
              return (type != "display") ? '' + data : jT.ui.shortenedData(data, "Press to copy the value in the clipboard");
            };
            col["sWidth"] = "75px";
          }
          
          // finally - assign column switching to the checkbox of main tab.
          if (level == 1)
            ++checkIdx;
          jT.$(checkList[checkIdx]).data({ sel: colList == fixCols ? '.jtox-ds-fixed' : '.jtox-ds-variable', idx: colList.length, id: fId}).on('change', fnShowColumn)
          
          // and push it into the proper list.
          colList.push(col);
        });
        
        // after the first one we switch to variable table's columns.
        colList = varCols;
      }
      
      // now - create the tables...
      self.fixTable = (jT.$(".jtox-ds-fixed table", self.rootElement).dataTable({
        "bPaginate": false,
        "bProcessing": true,
        "bLengthChange": false,
				"bAutoWidth": true,
        "sDom" : "rt",
        "aoColumns": fixCols,
        "bSort": false,
        "fnCreatedRow": function( nRow, aData, iDataIndex ) {
          // attach the click handling
          if (self.settings.hasDetails)
            jT.$('.jtox-details-open', nRow).on('click', function(e) {  fnShowDetails(nRow); });
          jT.$(nRow).data('jtox-index', iDataIndex);
        },
        "oLanguage" : {
          "sEmptyTable" : '<span id="jtox-ds-message-' + self.instanceNo + '">Loading data...</span>',
        }
      }))[0];

      self.varTable = (jT.$(".jtox-ds-variable table", self.rootElement).dataTable({
        "bPaginate": false,
        "bLengthChange": false,
				"bAutoWidth": false,
        "sDom" : "rt",
        "bSort": true,
        "aoColumns": varCols,
        "bScrollCollapse": true,
        "fnCreatedRow": function( nRow, aData, iDataIndex ) {
          nRow.id = 'jtox-var-' + self.instanceNo + '-' + iDataIndex;
          jT.$(nRow).addClass('jtox-row');
          jT.$(nRow).data('jtox-index', iDataIndex);
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
        "oLanguage" : { "sEmptyTable" : " - " }
      }))[0];
    },

    updateTables: function() {
      var self = this;
      self.filterEntries(jT.$('.jtox-ds-control input', self.rootElement).val());
    },
    
    /* Prepare the groups and the features.
    */
    prepareGroups: function (miniset) {
      var self = this;
      
      var grps = self.settings.configuration.groups;
      self.groups = {};
      for (var i in grps){
        var grp = grps[i];
        if (ccLib.isNull(grp))
          continue;
          
        var grpArr = (typeof grp == "function" || typeof grp == "string") ? ccLib.fireCallback(grp, self, i, miniset) : grp;
        self.groups[i] = [];
        ccLib.enumObject(grpArr, function(fid, idx) { 
          var sameAs = cls.findSameAs(fid, self.features);
          if (!self.features[sameAs].used && !self.features[fid].used)
            self.groups[i].push(fid);
          if (idx != "name") {
            // these we need to be able to return back to original state.
            self.usedFeatures.push(fid);
            self.usedFeatures.push(sameAs);
            self.features[fid].used = self.features[sameAs].used = true;
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
            var feat = self.features[fId];
            if (feat.search !== undefined && !feat.search)
              return false;
            var val = self.featureValue(fId, entry);
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
      jT.$('#jtox-ds-message-' + self.instanceNo).html('No records matching the filter.');
      
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
      if (self.entriesCount === null || self.settings.pageStart + self.settings.pageSize < self.entriesCount)
        self.queryEntries(self.settings.pageStart + self.settings.pageSize);
    },
    
    prevPage: function() {
      var self = this;
      if (self.settings.pageStart > 0)
        self.queryEntries(self.settings.pageStart - self.settings.pageSize);
    },
    
    // make the actual query for the (next) portion of data.
    queryEntries: function(from, size) {
      var self = this;
      if (from < 0)
        from = 0;
      if (size == null)
        size = self.settings.pageSize;
        
      // setup the size, as well
      jT.$('.jtox-ds-control select', self.rootElement).val(size);
      self.settings.pageSize = size;
      
      var qStart = Math.floor(from / size);
      var qUri = ccLib.addParameter(self.datasetUri, "page=" + qStart + "&pagesize=" + size);

      jT.call(self, qUri, function(dataset){
        if (!!dataset){
          // first initialize the counters.
          var qSize = dataset.dataEntry.length;
          qStart = self.settings.pageStart = qStart * self.settings.pageSize;
            if (qSize < self.settings.pageSize) // we've reached the end!!
              self.entriesCount = qStart + qSize;

          // then, preprocess the dataset
          self.dataset = cls.processDataset(dataset, self.features, self.settings.fnAccumulate, self.settings.pageStart);

          // ok - go and update the table, filtering the entries, if needed            
          self.updateTables();

          // finally - go and update controls if they are visible
          if (self.settings.showControls){
            var pane = jT.$('.jtox-ds-control', self.rootElement)[0];
            ccLib.fillTree(pane, {
              "pagestart": qStart + 1,
              "pageend": qStart + qSize,
            });
            
            var nextBut = jT.$('.next-field', pane);
            if (self.entriesCount === null || qStart + qSize < self.entriesCount)
              jT.$(nextBut).addClass('paginate_enabled_next').removeClass('paginate_disabled_next');
            else
              jT.$(nextBut).addClass('paginate_disabled_next').removeClass('paginate_enabled_next');
              
            var prevBut = jT.$('.prev-field', pane);
            if (qStart > 0)
              jT.$(prevBut).addClass('paginate_enabled_previous').removeClass('paginate_disabled_previous');
            else
              jT.$(prevBut).addClass('paginate_disabled_previous').removeClass('paginate_enabled_previous');
          }

          // time to call the supplied function, if any.
          ccLib.fireCallback(self.settings.onLoaded, self, dataset);
        }
      });
    },
    
    /* Makes a query to the server for particular dataset, asking for feature list first, so that the table(s) can be 
    prepared.
    */
    queryDataset: function (datasetUri) {
      var self = this;
      // if some oldies exist...
      this.clearDataset();
      this.init();

      datasetUri = jT.grabPaging(self, datasetUri);

      self.settings.baseUrl = self.settings.baseUrl || jT.grabBaseUrl(datasetUri);
      
      // remember the _original_ datasetUri and make a call with one size length to retrieve all features...
      self.datasetUri = datasetUri;
      jT.call(self, ccLib.addParameter(datasetUri, "page=0&pagesize=1"), function (dataset) {
        if (!!dataset) {
          self.features = dataset.feature;
          cls.processFeatures(self.features, self.settings.configuration.baseFeatures);
          dataset.features = self.features;
          self.prepareGroups(dataset);
          if (self.settings.showTabs) {
            self.prepareTabs(jT.$('.jtox-ds-features', self.rootElement)[0], true, function (id, name, parent){
              var fEl = jT.getTemplate('#jtox-ds-feature');
              parent.appendChild(fEl);
              ccLib.fillTree(fEl, {title: name.replace(/_/g, ' '), uri: self.featureUri(id)});
              return fEl;
            });
          }
          
          self.prepareTables(); // prepare the tables - we need features to build them - we have them!
          self.equalizeTables(); // to make them nicer, while waiting...
          ccLib.fireCallback(self.settings.onPrepared, self, dataset);
          self.queryEntries(self.settings.pageStart, self.settings.pageSize); // and make the query for actual data
        }
      });
    },
    
    /* This is a needed shortcut that jToxQuery routine will call
    */
    query: function (uri) {
      this.queryDataset(uri);
    }    
  }; // end of prototype
  
  // some public, static methods
  cls.processEntry = function (entry, features, fnValue) {
    for (var fid in features) {
      var feature = features[fid];
      var newVal = entry.values[fid];
      
      // if applicable - location the feature value to a specific location whithin the entry
      if (!!feature.accumulate && newVal !== undefined && feature.location !== undefined) {
        var accArr = feature.location;
        if (!jT.$.isArray(accArr))
          accArr = [accArr];
        
        for (var v = 0; v < accArr.length; ++v)
          ccLib.setJsonValue(entry, accArr[v], ccLib.fireCallback(fnValue, this, fid,  /* oldVal */ ccLib.getJsonValue(entry, accArr[v]), newVal, features));
      }
      
      if (feature.process !== undefined)
        ccLib.fireCallback(feature.process, this, entry, fid, features);
    }
    
    return entry;
  };
  
  cls.findSameAs = function (fid, features) {
    // starting from the feature itself move to 'sameAs'-referred features, until sameAs is missing or points to itself
    // This, final feature should be considered "main" and title and others taken from it.
    var feature = features[fid];
    var base = fid.replace(/(http.+\/feature\/).*/g, "$1");
    var retId = fid;
    
    for (;;){
      if (feature.sameAs === undefined || feature.sameAs == null || feature.sameAs == fid || fid == base + feature.sameAs)
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
      
      var sameAs = cls.findSameAs(fid, features);
      // now merge with this one... it copies everything that we've added, if we've reached to it. Including 'location'
      features[fid] = jT.$.extend(features[fid], features[sameAs], { originalId: fid, originalTitle: features[fid].title });
    }
    
    return features;
  };
  
  cls.processDataset = function(dataset, features, fnValue, startIdx) {
    if (ccLib.isNull(features)) {
      cls.processFeatures(dataset.feature);
      features = dataset.feature;
    }

    if (ccLib.isNull(fnValue))
      fnValue = cls.defaultSettings.fnAccumuate;
    
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
    shortStars: false,
    maxStars: 10,
    selectable: false,
    selectionHandler: null,
    sDom: "<Fif>rt",
    onLoaded: null,
    loadOnInit: false,
    /* datasetUri */
    configuration: { 
      columns : {
        dataset: {
          'Id': { iOrder: 0, sTitle: "Id", mData: "URI", sWidth: "50px", mRender: function (data, type, full) {
            var num = parseInt(data.match(/http:\/\/.*\/dataset\/(\d+).*/)[1]);
            if (type != 'display')
              return num;
            return '<a target="_blank" href="' + data + '"><span class="ui-icon ui-icon-link jtox-inline"></span> D' + num + '</a>';
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
    
    self.rootElement.appendChild(jT.getTemplate('#jtox-dataset'));
    self.init();
        
    // finally, wait a bit for everyone to get initialized and make a call, if asked to
    if (self.settings.datasetUri != undefined || self.settings.loadOnInit)
      self.listDatasets(self.settings.datasetUri)
  };
  
  cls.prototype = {
    init: function () {
      var self = this;
      
      // arrange certain things on the columns first - like dealing with short/long stars
      self.settings.configuration.columns.dataset.Stars.mRender = function (data, type, full) {
        return type != 'display' ? data : jT.ui.putStars(self, data, "Dataset quality stars rating (worst) 1-10 (best)");
      };
      if (self.settings.shortStars)
        self.settings.configuration.columns.dataset.Stars.sWidth = "40px";
      
      // deal if the selection is chosen
      if (self.settings.selectable) {
        self.settings.configuration.columns.dataset.Id.mRender = jT.ui.addSelection(self, self.settings.configuration.columns.dataset.Id.mRender);
        self.settings.configuration.columns.dataset.Id.sWidth = "60px";
      }
      
      // READYY! Go and prepare THE table.
      self.table = jT.$('table', self.rootElement).dataTable({
        "bPaginate": false,
        "bLengthChange": false,
				"bAutoWidth": false,
        "sDom" : self.settings.sDom,
        "aoColumns": jT.ui.processColumns(self, 'dataset'),
				"oLanguage": {
          "sLoadingRecords": "No datasets found.",
          "sZeroRecords": "No datasets found.",
          "sEmptyTable": "No datasets available.",
          "sInfo": "Showing _TOTAL_ dataset(s) (_START_ to _END_)"
        }
      });
      
      jT.$(self.table).dataTable().fnAdjustColumnSizing();
    },
    
    listDatasets: function (uri) {
      var self = this;
      if (uri == null)
        uri = self.settings.baseUrl + '/dataset';
      else if (!self.settings.baseUrl)
        self.settings.baseUrl = jT.grabBaseUrl(uri);
      
      jT.$(self.table).dataTable().fnClearTable();
      jT.call(self, uri, function (result) {
        if (!!result) {
          jT.$(self.table).dataTable().fnAddData(result.dataset);
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
    selectable: false,
    selectionHandler: null,
    maxStars: 10,
    algorithmLink: true,
    algorithms: false,
    onLoaded: null,
    sDom: "<Fif>rt",
    loadOnInit: false,
    /* algorithmNeedle */
    /* modelUri */
    configuration: { 
      columns : {
        model: {
          'Id': { iOrder: 0, sTitle: "Id", mData: "id", sWidth: "50px", mRender: function (data, type, full) {
            if (type != 'display')
              return data;
            return '<a target="_blank" href="' + full.URI + '"><span class="ui-icon ui-icon-link jtox-inline"></span> M' + data + '</a>';
          }},
          'Title': { iOrder: 1, sTitle: "Title", mData: "title", sDefaultContent: "-" },
          'Stars': { iOrder: 2, sTitle: "Stars", mData: "stars", sWidth: "160px" },
          'Algorithm': {iOrder: 3, sTitle: "Algorithm", mData: "algorithm" },
          'Info': { iOrder: 4, sTitle: "Info", mData: "trainingDataset", mRender: function (data, type, full) {
            if (type != 'display' || !data)
              return data;
            return '<a href="' + data + '"><span class="ui-icon ui-icon-calculator"></span>&nbsp;training set</a>';
          } }
        },
        algorithm: {
          'Id': { iOrder: 0, sTitle: "Id", mData: "id", sWidth: "150px", mRender: function (data, type, full) {
            if (type != 'display')
              return data;
            return '<a target="_blank" href="' + full.uri + '"><span class="ui-icon ui-icon-link jtox-inline"></span> M' + data + '</a>';
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
    
    self.rootElement.appendChild(jT.getTemplate('#jtox-model'));
    self.init();
        
    // finally, wait a bit for everyone to get initialized and make a call, if asked to
    if (self.settings.modelUri !== undefined || self.settings.algorithmNeedle !== undefined || self.settings.loadOnInit)
      self.query();
  };
  
  cls.prototype = {
    init: function () {
      var self = this;
      
      // but before given it up - make a small sorting..
      if (!self.settings.algorithms) {
        self.settings.configuration.columns.model.Stars.mRender = function (data, type, full) { return type != 'display' ? data : jT.ui.putStars(self, data, "Model star rating (worst) 1 - 10 (best)"); };
        if (self.settings.shortStars) {
          self.settings.configuration.columns.model.Stars.sWidth = "40px";
        }

        self.settings.configuration.columns.model.Algorithm.mRender = function (data, type, full) { 
          var name = data.URI.match(/http:\/\/.*\/algorithm\/(\w+).*/)[1];
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
      if (self.settings.selectable) {
        self.settings.configuration.columns[cat].Id.mRender = jT.ui.addSelection(self, self.settings.configuration.columns.model.Id.mRender);
        self.settings.configuration.columns[cat].Id.sWidth = "60px";
      }
      
      // READYY! Go and prepare THE table.
      self.table = jT.$('table', self.rootElement).dataTable({
        "bPaginate": false,
        "bLengthChange": false,
				"bAutoWidth": false,
        "sDom" : self.settings.sDom,
        "aoColumns": jT.ui.processColumns(self, cat),
				"oLanguage": {
          "sLoadingRecords": "No models found.",
          "sZeroRecords": "No models found.",
          "sEmptyTable": "No models available.",
          "sInfo": "Showing _TOTAL_ model(s) (_START_ to _END_)"
        }
      });
      
      jT.$(self.table).dataTable().fnAdjustColumnSizing();
    },
    
    listModels: function (uri) {
      var self = this;
      if (uri == null)
        uri = self.settings.baseUrl + '/model';
      else if (!self.settings.baseUrl)
        self.settings.baseUrl = jT.grabBaseUrl(uri);

      self.modelUri = uri;
      jT.$(self.table).dataTable().fnClearTable();
      jT.call(self, uri, function (result) {
        if (!!result) {
          self.models = result.model;
          jT.$(self.table).dataTable().fnAddData(result.model);
          ccLib.fireCallback(self.settings.onLoaded, self, result);
        }
      });
    },
    
    /* List algorithms that contain given 'needle' in their name
    */
    listAlgorithms: function (needle) {
      var self = this;
      var uri = self.settings.baseUrl + '/algorithm';
      if (!!needle)
        uri = ccLib.addParameter(uri, 'search=' + needle);
      jT.call(self, uri, function (result) {
        if (!!result) {
          self.algorithms = result.algorithm;
          jT.$(self.table).dataTable().fnAddData(result.algorithm);
          ccLib.fireCallback(self.settings.onLoaded, self, result);
        }
      });
    },
    
    getModel: function (algoUri, callback) {
      // TODO: make a request for getting / creating a model for given algorithm
    },
    
    runPrediction: function (compoundUri, modelUri, callback) {
      // TODO: make a POST request for prediction for given compound on given model
      // the callback is returned after the task polling is done.
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
/* toxcomposition.js - A kit for visualizing substance composition(s)
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxComposition = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    selectable: false,    // whether to show selection checkbox on each row
    showBanner: true,     // whether to show a banner of composition info before each compounds-table
    sDom: "rt<Ffp>",   // compounds (ingredients) table sDom
    onLoaded: null,
    
    /* compositionUri */
    configuration: {
      columns : {
        composition: {
          'Type': { sTitle: "Type", sClass : "left", sWidth : "10%", mData : "relation", mRender : function(val, type, full) {
					  if (type != 'display')
					    return '' + val;
					  var func = ("HAS_ADDITIVE" == val) ? full.proportion.function_as_additive : "";
					  return '<span class="camelCase">' +  val.replace("HAS_", "").toLowerCase() + '</span>' + ((func === undefined || func === null || func == '') ? "" : " (" + func + ")");
          } },
          'Name': { sTitle: "Name", sClass: "camelCase left", sWidth: "15%", mData: "component.compound.name", mRender: function(val, type, full) {
						return (type != 'display') ? '' + val : 
						  '<a href="' + full.component.compound.URI + '" target="_blank" title="Click to view the compound"><span class="ui-icon ui-icon-link" style="float: left; margin-right: .3em;"></span></a>' + val; } },
					'EC No.': { sTitle: "EC No.", sClass: "left", sWidth: "10%", mData: "component.compound.einecs" },
					'CAS No.': { sTitle: "CAS No.", sClass : "left", sWidth: "10%", mData : "component.compound.cas" },
					'Typical concentration': { sTitle: "Typical concentration", sClass: "center", sWidth: "15%", mData: "proportion.typical", mRender: function(val, type, full) { return type != 'display' ? '' + val.value : jToxComposition.formatConcentration(val.precision, val.value, val.unit); } },
					'Concentration ranges': { sTitle: "Concentration ranges", sClass : "center colspan-2", sWidth : "20%", mData : "proportion.real", mRender : function(val, type, full) { return type != 'display' ? '' + val.lowerValue : jToxComposition.formatConcentration(val.lowerPrecision, val.lowerValue, val.unit); } },
					'Upper range': { sTitle: 'Upper range', sClass: "center", sWidth: "20%", mData: "proportion.real", mRender: function(val, type, full) { return type != 'display' ? '' + val.upperValue : jToxComposition.formatConcentration(val.upperPrecision, val.upperValue, val.unit); } },
					'Also': { sTitle: "Also", sClass: "center", bSortable: false, mData: "component.compound.URI", mRender: function(val, type, full) { return !val ? '' : '<a href="' + (jT.settings.baseUrl || self.baseUrl) + 'substance?type=related&compound_uri=' + encodeURIComponent(val) + '" target="_blank">Also contained in...</span></a>'; } }
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
  	return ((precision === undefined || precision === null || "=" == precision ? "" : precision) + val + " " + (unit == null || unit == '' ? '<span class="units">% (w/w)</span>' : unit)).replace(/ /g, "&nbsp;").replace("span&nbsp;", "span ");
  };

  var fnDatasetValue = function (fid, old, value, features){
		return ccLib.extendArray(old, value != null ? value.trim().toLowerCase().split("|") : [value]).filter(ccNonEmptyFilter);
  };
  
  cls.prototype = {
    prepareTable: function (json, tab) {
      var self = this;
      
      // deal if the selection is chosen
      var colId = self.settings.configuration.columns.composition.Name;
      if (self.settings.selectable) {
        colId.mRender = jT.ui.addSelection(self, colId.mRender);
        colId.sWidth = "60px";
      }
        
      // we need that processing to remove the title of "Also contained in..." column...
      var cols = jT.ui.processColumns(self, 'composition');
      for (var i = 0, cl = cols.length; i < cl; ++i)
        if (cols[i].sTitle == 'Also') {
          cols[i].sTitle = '';
          break;
        }
      // READYY! Go and prepare THE table.
      self.table = jT.$('table.composition-table', tab).dataTable({
        "bPaginate": false,
        "bLengthChange": false,
				"bAutoWidth": false,
        "bServerSide": false,
        "sDom" : self.settings.sDom,
        "aoColumns": cols,
      });
      
      jT.$(self.table).dataTable().fnAdjustColumnSizing();
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
      jT.call(self, uri, function (json) {
        if (!!json && !!json.composition) {
          // clear the old tabs, if any.
          var substances = {};
    
          jToxCompound.processFeatures(json.feature);
          // proprocess the data...
          for (var i = 0, cmpl = json.composition.length; i < cmpl; ++i) {
            var cmp = json.composition[i];
            
            jToxCompound.processEntry(cmp.component, json.feature, fnDatasetValue);
    
            // now prepare the subs        
            var theSubs = substances[cmp.compositionUUID];
            if (theSubs === undefined)
              substances[cmp.compositionUUID] = theSubs = { name: "", purity: "", maxvalue: 0, uuid: cmp.compositionUUID, composition: [] };
            
            theSubs.composition.push(cmp);
            var val = cmp.proportion.typical;
            if (cmp.relation == 'HAS_CONSTITUENT' && (theSubs.maxvalue < val.value || theSubs.name == '')) {
              theSubs.name = cmp.component.compound['name'] + ' ' + jToxComposition.formatConcentration(val.precision, val.value, val.unit);
              theSubs.maxvalue = val.value;
              val = cmp.proportion.real;
              theSubs.purity = (val.lowerValue + '-' + val.upperValue + ' ' + (val.unit == null || val.unit == '' ? '<span class="units">% (w/w)</span>' : val.unit)).replace(/ /g, "&nbsp;").replace("span&nbsp;", "span ");
            }
          }
          
          // now make the actual filling
          for (var i in substances) {
            var panel = jT.getTemplate('#jtox-composition');
            self.rootElement.appendChild(panel);
            ccLib.fillTree(jT.$('.composition-info', panel)[0], substances[i]);
            self.prepareTable(substances[i].composition, panel);
          }
          
          ccLib.fireCallback(self.settings.onLoaded, self, json.composition);
        }
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
    var suffix = '_' + instanceCount++;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even in manual initialization.
    
    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings); // i.e. defaults from jToxStudy
    self.settings.tab = self.settings.tab || jT.settings.fullUrl.hash;
    // now we have our, local copy of settings.
    
    // get the main template, add it (so that jQuery traversal works) and THEN change the ids.
    // There should be no overlap, because already-added instances will have their IDs changed already...
    var tree = jT.getTemplate('#jtox-studies');
    root.appendChild(tree);
    jT.ui.changeTabsIds(tree, suffix);
    jT.$('div.jtox-study-tab div button', tree).on('click', function (e) {
    	var par = jT.$(this).parents('.jtox-study-tab')[0];
	    if (jT.$(this).hasClass('expand-all')) {
		    jT.$('.jtox-foldable', par).removeClass('folded');
	    }
	    else if (jT.$(this).hasClass('collapse-all')) {
		    jT.$('.jtox-foldable', par).addClass('folded');
	    }
    });
    
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
    if (self.settings['substanceUri'] !== undefined) {
      self.querySubstance(self.settings['substanceUri']);
    }
  };
  
  // now follow the prototypes of the instance functions.
  cls.prototype = {
    loadPanel: function(panel){
      var self = this;
      if (panel){
        jT.$('.jtox-study.unloaded', panel).each(function(i){
          var table = this;
          jT.call(self, jT.$(table).data('jtox-uri'), function(study){
            if (!!study) {
              jT.$(table).removeClass('unloaded folded');
              jT.$(table).addClass('loaded');
              self.processStudies(panel, study.study, false);
              ccLib.fireCallback(self.settings.onStudy, self, study.study);
            }
          });  
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
    
    renderMulti: function (data, type, full, format) {
      var self = this;
      var dlen = data.length;
      if (dlen < 2)
        return self.getFormatted(data[0], type, format);
  
      var height = 100 / dlen;
      var df = '<table>';
      for (var i = 0, dlen = data.length; i < dlen; ++i) {
        df += '<tr class="' + (i % 2 == 0 ? 'even' : 'odd') + '"><td class="center" style="height: ' + height + '%">' + self.getFormatted(data[i], type, format) + '</td></tr>';
      }
      
      df += '</table>';
      return df;
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
  
    updateCount: function(str, count) {
      if (count === undefined)
        count = 0;
      return str.replace(/(.+)\s\(([0-9]+)\)/, "$1 (" + count + ")");
    },
    
    // modifies the column title, according to configuration and returns "null" if it is marked as "invisible".
    ensureTable: function (tab, study) {
      var self = this;
      var category = study.protocol.category.code;
      var theTable = jT.$('.' + category + ' .jtox-study-table', tab)[0];
      if (!jT.$(theTable).hasClass('dataTable')) {
	      var defaultColumns = [
	        { "sTitle": "Name", "sClass": "center middle", "sWidth": "20%", "mData": "protocol.endpoint" }, // The name (endpoint)
	        { "sTitle": "Endpoint", "sClass": "center middle jtox-multi", "sWidth": "15%", "mData": "effects", "mRender": function (data, type, full) { return self.renderMulti(data, type, full, "endpoint");  } },   // Effects columns
	        { "sTitle": "Result", "sClass": "center middle jtox-multi", "sWidth": "10%", "mData" : "effects", "mRender": function (data, type, full) { return self.renderMulti(data, type, full, function (data, type) { return formatLoHigh(data.result, type) }); } },
	        { "sTitle": "Text", "sClass": "center middle jtox-multi", "sWidth": "10%", "mData" : "effects", "mRender": function (data, type, full) { return self.renderMulti(data, type, full, function (data, type) { return !!data.result.textValue  ? data.result.textValue : '-'; }); } },
	        { "sTitle": "Guideline", "sClass": "center middle", "sWidth": "15%", "mData": "protocol.guideline", "mRender" : "[,]", "sDefaultContent": "-"  },    // Protocol columns
	        { "sTitle": "Owner", "sClass": "center middle shortened", "sWidth": "15%", "mData": "citation.owner", "sDefaultContent": "-" },
	        { "sTitle": "Citation", "sClass": "center middle shortened", "sWidth": "15%", "mData": "citation", "mRender": function (data, type, full) { return (data.title || "") + ' ' + (!!data.year && data.year.length > 1 ? data.year : ""); }  },
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
        
        // some value formatting functions
        var formatLoHigh = function (data, type) {
          var out = "";
          if (!ccLib.isNull(data)) {
            data.loValue = ccLib.trim(data.loValue);
            data.upValue = ccLib.trim(data.upValue);
            if (!ccLib.isEmpty(data.loValue) && !ccLib.isEmpty(data.upValue)) {
              out += (data.loQualifier == ">=") ? "[" : "(";
              out += data.loValue + ", " + data.upValue;
              out += (data.upQualifier == "<=") ? "]" : ") ";
            }
            else // either of them is non-undefined
            {
              var fnFormat = function (q, v) {
                return (!!q ? q : "=") + " " + v;
              };
              
              if (!ccLib.isEmpty(data.loValue))
                out += fnFormat(data.loQualifier, data.loValue);
              else if (!ccLib.isEmpty(data.upValue))
                out += fnFormat(data.upQualifier, data.upValue);
              else
                out += '-';
            }
            
            data.unit = ccLib.trim(data.unit);
            if (!ccLib.isNull(data.unit))
              out += ' <span class="units">' + data.unit + '</span>';
          }
          return out.replace(/ /g, "&nbsp;").replace("span&nbsp;", "span ");
        };
        
        var formatUnits = function(data, unit) {
          data = ccLib.trim(data);
          unit = ccLib.trim(unit);
          return !ccLib.isNull(data) ? (data + (!!unit ? '&nbsp;<span class="units">' + unit + '</span>': "")) : "-";
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
          
          if (study.parameters[p] !== undefined && study.parameters[p] != null){
            col["mRender"] = study.parameters[p].loValue === undefined ?
              function (data, type, full) { return formatUnits(data, full[p + " unit"]); } : 
              function (data, type, full) { return formatLoHigh(data.parameters[p], type); };
          }
          
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
          
          var rnFn = null;
          if (study.effects[0].conditions[c] !== undefined && study.effects[0].conditions[c] != null)
            rnFn = study.effects[0].conditions[c].loValue === undefined ? 
              function(data, type) { return formatUnits(data.conditions[c],  data.conditions[c + " unit"]); } : 
              function(data, type) { return formatLoHigh(data.conditions[c], type); }
          else
            rnFn = function(data, type) { return "-"; }
            
          col["mRender"] = function(data, type, full) { return self.renderMulti(data, type, full, rnFn); };
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
          "sDom" : self.settings.sDom || "rt<Fip>",
          "aoColumns": colDefs,
          "fnInfoCallback": function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
            var el = jT.$('.title .data-field', jT.$(this).parents('.jtox-study'))[0];
            el.innerHTML = self.updateCount(el.innerHTML, iTotal);
            return sPre;
          },
  				"oLanguage": {
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
          }
        });
        
        jT.$(theTable).dataTable().fnAdjustColumnSizing();
      }
      else
        jT.$(theTable).dataTable().fnClearTable();
        
      return theTable;
    },
    
    processSummary: function (summary) {
      var self = this;
      var typeSummary = [];
      
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
	      return (valA < valB) ? -1 : 1;
      });
      
      for (var si = 0, sl = summary.length; si < sl; ++si) {
        var sum = summary[si];
        var top = sum.topcategory.title;
        if (!top)
          continue;
        var top = top.replace(/ /g, "_");
        var tab = jT.$('.jtox-study-tab.' + top, self.rootElement)[0];
        
        var catname = sum.category.title;
        if (!catname) {
          typeSummary[top] = sum.count;
        }
        else {
          var cat = self.createCategory(tab, catname);
          jT.$(cat).data('jtox-uri', sum.category.uri);
        }
      }
      
      // update the number in the tabs...
      jT.$('ul li a', self.rootElement).each(function (i){
        var data = jT.$(this).data('type');
        if (!!data){
          var cnt = typeSummary[data];
          var el = jT.$(this)[0];
          el.innerHTML = (self.updateCount(el.innerHTML, cnt));
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
            if (cats[ones.protocol.category] === undefined) {
              cats[ones.protocol.category.code] = [ones];
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
        jT.$(theTable).parents('.jtox-study').addClass('folded');
      }
      
      // we need to fix columns height's because of multi-cells
      jT.$('#' + theTable.id + ' .jtox-multi').each(function(index){
        this.style.height = '' + this.offsetHeight + 'px';
      });
    },
        
    querySummary: function(summaryURI) {
      var self = this;
      
      jT.call(self, summaryURI, function(summary) {
        if (!!summary && !!summary.facet)
          self.processSummary(summary.facet);
          ccLib.fireCallback(self.settings.onSummary, self, summary.facet);
          // check if there is an initial tab passed so we switch to it
          if (!!self.settings.tab) {
            var div = jT.$('.jtox-study-tab.' + decodeURIComponent(self.settings.tab).replace(/ /g, '_'), self.root)[0];
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
      var self = this;
      
      var compoRoot = jT.$('.jtox-compo-tab', self.rootElement)[0];
      var ds = new jToxComposition(compoRoot);
      ds.queryComposition(compositionURI);
    },
    
    querySubstance: function(substanceURI) {
      var self = this;
      
      // re-initialize us on each of these calls.
      self.baseUrl = ccLib.isNull(self.settings.baseUrl) ? jT.grabBaseUrl(substanceURI) : self.settings.baseUrl;
      
      var rootTab = jT.$('.jtox-substance', self.rootElement)[0];
      jT.call(self, substanceURI, function(substance){
        if (!!substance && !!substance.substance && substance.substance.length > 0){
          substance = substance.substance[0];
           
          substance["showname"] = substance.publicname || substance.name;
          var flags = '';
          for (var i = 0, iLen = substance.externalIdentifiers.length; i < iLen; ++i) {
            if (i > 0)
              flags += ', ';
            flags += substance.externalIdentifiers[i].id || '';
          }
          substance["IUCFlags"] = flags;
            
          ccLib.fillTree(self.rootElement, substance);
          // go and query for the reference query
          jT.call(self, substance.referenceSubstance.uri, function (dataset){
            if (!!dataset) {
              jToxCompound.processDataset(dataset, null, fnDatasetValue);
              ccLib.fillTree(rootTab, dataset.dataEntry[0]);
            }
          });
           
          ccLib.fireCallback(self.settings.onLoaded, self, substance.substance);
          // query for the summary and the composition too.
          self.querySummary(substance.URI + "/studysummary");
          self.insertComposition(substance.URI + "/composition");
        }
      });
    }
  }; // end of prototype
  
  return cls;
})();
window.jT = window.jToxKit = {
	templateRoot: null,

	/* A single place to hold all necessary queries. Parameters are marked with {id} and formatString() (common.js) is used
	to prepare the actual URLs
	*/
	queries: {
		taskPoll: { method: 'GET', service: "/task/{id}" },
	},
	
	templates: { },        // html2js routine will fill up this variable
	tools: { },        // additional, external tools added with html2js

	/* SETTINGS. The following parametes can be passed in settings object to jT.init(), or as data-XXX - with the same names. Values set here are the defaults.
	*/
	settings: {
	  plainText: false,               // whether to expect result as plain text, and not JSON. Used in special cases like ketcher.
  	jsonp: false,                   // whether to use JSONP approach, instead of JSON.
  	crossDomain: false,             // should it expect cross-domain capabilities for the queries.
  	baseUrl: null,                  // the baseUrl for the server that loaded the page.
  	fullUrl: null,                  // the url as it is on loading the page - this is parsed one, i.e. parseUrl() processed.
  	timeout: 15000,                 // the timeout an call to the server should be wait before the attempt is considered error.
  	pollDelay: 200,                 // after how many milliseconds a new attempt should be made during task polling.
  	onConnect: function(s){ },		  // function (service): called when a server request is started - for proper visualization. Part of settings.
  	onSuccess: function(s, c, m) { },	// function (code, mess): called on server request successful return. It is called along with the normal processing. Part of settings.
  	onError: function (s, c, m) { if (!!console && !!console.log) console.log("jToxKit call error (" + c + "): " + m + " from request: [" + s + "]"); },		// function (code, mess): called on server reques error. Part of settings.
  },
	
	// form the "default" baseUrl if no other is supplied
	formBaseUrl: function(url) {
    return !!url.host ? url.protocol + "://" + url.host + (url.port.length > 0 ? ":" + url.port : '') + '/' + url.segments[0] : null;
	},
    
  // initializes one kit, based on the kit name passed, either as params, or found within data-XXX parameters of the element
  initKit: function(element) {
    var self = this;

  	var dataParams = self.$(element).data();
    
  	if (!dataParams.manualInit) {
    	var kit = dataParams.kit;
    	var topSettings = self.$.extend(true, {}, self.settings);

    	// we need to traverse up, to collect some parent's settings...
    	self.$(self.$(element).parents('.jtox-toolkit').toArray().reverse()).each(function(){
      	if (!self.$(this).hasClass('jtox-widget')) {
        	topSettings = self.$.extend(true, topSettings, self.$(this).data());
      	}
    	});
    	
      dataParams = self.$.extend(true, topSettings, dataParams);

  	  // the real initialization function
      var realInit = function (params) {
      	if (!kit)
      		return null;
        // add jTox if it is missing AND there is not existing object/function with passed name. We can initialize ketcher and others like this too.
      	if (!window[kit] && kit.indexOf('jTox') != 0)
    	  	kit = 'jTox' + kit.charAt(0).toUpperCase() + kit.slice(1);
    
      	var fn = window[kit];
      	if (typeof fn == 'function') {
      	  var obj = new fn(element, params);
          if (fn.kits === undefined)
            fn.kits = [];
          fn.kits.push(obj);
          return obj;
      	}
        else if (typeof fn == "object" && typeof fn.init == "function")
          return fn.init(element, params);
        else
          console.log("jToxError: trying to initialize unexistend jTox kit: " + kit);

        return null;
      };

  	  // first, get the configuration, if such is passed
  	  if (!ccLib.isNull(dataParams.configFile)) {
  	    // we'll use a trick here so the baseUrl parameters set so far to take account... thus passing 'fake' kit instance
  	    // as the first parameter of jT.call();
    	  self.call({ settings: dataParams}, dataParams.configFile, function(config){
      	  if (!!config)
      	    dataParams['configuration'] = self.$.extend(true, dataParams['configuration'], config);
          jT.$(element).data('jtKit', realInit(dataParams));
    	  });
  	  }
  	  else {
  	    if (!ccLib.isNull(dataParams.configuration) && typeof dataParams.configuration == "string" && !ccLib.isNull(window[dataParams.configuration]))
  	      dataParams.configuration = window[dataParams.configuration];
    	  
        jT.$(element).data('jtKit', realInit(dataParams));
  	  }
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

      // scan the query parameter for settings
  		var url = self.settings.fullUrl = ccLib.parseURL(document.location);
  		var queryParams = url.params;
  		if (!queryParams.baseUrl)
  		  queryParams.baseUrl = self.formBaseUrl(url);
  	
      self.settings = self.$.extend(true, self.settings, queryParams); // merge with defaults
      root = document;
  	}

  	// now scan all insertion divs
  	self.$('.jtox-toolkit', root).each(function(i) { self. initKit(this); });
	},
	
	kit: function (element) {
  	return $(element).data('jtKit');
	},
	
	parentKit: function(name, element) {
    var query = null;
    if (typeof name == 'string')
      name = window[name];
    self.$(element).parents('.jtox-toolkit').each(function() {
      var kit = jT.kit(this);
      if (!kit)
        return;
      if (kit instanceof name)
        query = kit;
    });
    
    return query;
  },
	
	initTemplates: function() {
	  var self = this;

    var root = jT.$('.jtox-template')[0];
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
  	var el = jT.$(selector, this.templateRoot)[0];
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
	pollTask : function(kit, task, callback) {
		var self = this;
		if (task === undefined || task.task === undefined || task.task.length < 1){
			console.log("Wrong task passed for polling: " + JSON.stringify(task));
			return;
		}
		task = task.task[0];
		ccLib.fireCallback(callback, kit, task);
		if (task.completed == -1){ // i.e. - running
			setTimeout(function(){
				self.call(kit, task.result, function(newTask){
					self.pollTask(newTask, callback);
				});
			}, self.pollDelay);
		}
		else if (task.error){ // additionally call the error handler
		  ccLib.fireCallback(self.settings.onError, self, '-1', task.error);
		}
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
        kit.settings.pageSize = sz;
      url = ccLib.removeParameter(url, 'pagesize');
    }
    
    if (urlObj.params['page'] !== undefined) {
      var beg = parseInt(urlObj.params['page']);
      if (beg >= 0)
        kit.settings.pageStart = beg * kit.settings.pageSize;
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
		if (typeof params != 'object') {
			callback = params; // the params parameters is obviously omitted
			params = {};
		}
		else if (params == null)
		  params = {};
		
	  var settings = jT.$.extend({}, this.settings, params);
		if (kit == null)
		  kit = this;
		else 
  		settings = jT.$.extend(settings, kit.settings);

		ccLib.fireCallback(settings.onConnect, kit, service);
		  
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
			
		// now make the actual call
		jT.$.ajax(service, {
			dataType: params.dataType || (settings.plainText ? "text": (settings.jsonp ? 'jsonp' : 'json')),
			headers: { Accept: accType },
			crossDomain: settings.crossDomain || settings.jsonp,
			timeout: settings.timeout,
			type: params.method,
			data: params.data,
			jsonp: settings.jsonp ? 'callback' : false,
			error: function(jhr, status, error){
			  ccLib.fireCallback(settings.onError, kit, service, status, error);
				callback(null, jhr);
			},
			success: function(data, status, jhr){
			  ccLib.fireCallback(settings.onSuccess, kit, service, status, jhr.statusText);
				callback(data, jhr);
			}
		});
	}
};

/* UI related functions of jToxKit are put here for more convenient usage
*/
window.jT.ui = {
  shortenedData: function (data, message, deflen) {
    var res = '';
    
    if (ccLib.isNull(deflen))
      deflen = 5;
    if (data.toString().length <= deflen) {
      res += data;
    }
    else {
      res += '<div class="shortened">' + data + '</div>';
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
	  colDefs.sort(function(a, b) {
	    var valA = ccLib.isNull(a.iOrder) ? 0 : a.iOrder;
	    var valB = ccLib.isNull(b.iOrder) ? 0 : b.iOrder;
	    return valA - valB;
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
  
  addSelection: function (kit, oldFn) {
    return function (data, type, full) {
      var oldRes = oldFn(data, type, full);
      if (type != 'display')
        return oldRes;
      
      var html = '<input type="checkbox" value="' + (full.URI || full.uri) + '"' +
              (!!kit.settings.selectionHandler ? ' class="jtox-handler" data-handler="' + kit.settings.selectionHandler + '"' : '') +
              '/>';
              
      return html + oldRes;
    }
  }
};

// we need to do this here - because other tools/libraries could have scheduled themselves on 'ready',
// so it'll be too late to make this assignment then. Also - we can use jT.$ from now on :-)
jT.$ = jQuery; // .noConflict();

  	
jT.$(document).ready(function(){
  jT.init();
});
jT.templates['widget-search']  = 
"    <div id=\"jtox-search\" class=\"jtox-search\">" +
"      <form>" +
"  	  	<div class=\"jq-buttonset jtox-inline\">" +
"  			  <input type=\"radio\" id=\"searchauto\" value=\"auto\" name=\"searchtype\" checked=\"checked\" data-placeholder=\"Enter CAS, EINECS, Chemical name, SMILES or InChI_\">" +
"  			    <label for=\"searchauto\" title=\"Exact structure or search by an identifier. CAS, Chemical name, SMILES or InChI. The input type is guessed automatically.\">Exact structure</label>" +
"          </input>" +
"  			  <input type=\"radio\" id=\"searchsimilarity\" value=\"similarity\" name=\"searchtype\" data-placeholder=\"Enter Chemical name, SMILES or InChI_\">" +
"  			    <label for=\"searchsimilarity\" title=\"Enter SMILES or draw structure\">Similarity</label>" +
"          </input>" +
"  			  <input type=\"radio\" id=\"searchsmarts\" value=\"smarts\" name=\"searchtype\" data-placeholder=\"Enter SMARTS_\">" +
"  			    <label for=\"searchsmarts\" title=\"Enter or draw a SMARTS query\">Substructure</label>" +
"          </input>" +
"  			</div>" +
"    		<div class=\"jtox-inline float-right search-pane\">" +
"  			  <div class=\"dynamic searchsimilarity hidden jtox-inline\">" +
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
"  			  <div class=\"dynamic searchsmarts hidden jtox-inline\">" +
"  			    <select name=\"smarts\" title =\"Predefined functional groups\"></select>" +
"  			  </div>" +
"  			  <div class=\"jtox-inline\">" +
"            <input type=\"text\" name=\"searchbox\" class=\"jtox-handler\"/>" +
"            <button name=\"searchbutton\" class=\"jtox-handler\" title=\"Search/refresh\" data-handler=\"query\"><span class=\"ui-icon ui-icon-search\"/></button>" +
"            <button name=\"drawbutton\" title=\"Draw the (sub)structure\"><span class=\"ui-icon ui-icon-pencil\"/></button>" +
"  			  </div>" +
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
"	  <div id=\"jtox-compound\">" +
"	    <div class=\"jtox-ds-features\"></div>" +
"	    <div class=\"jtox-ds-control\">" +
"	      Showing from <span class=\"data-field from-field\" data-field=\"pagestart\"> ? </span> to <span class=\"data-field\" data-field=\"pageend\"> ? </span> in pages of <select class=\"data-field\" data-field=\"pagesize\">" +
"          <option value=\"10\" selected=\"yes\">10</option>" +
"          <option value=\"20\">20</option>" +
"          <option value=\"50\">50</option>" +
"          <option value=\"100\">100</option>" +
"          <option value=\"200\">200</option>" +
"          <option value=\"500\">500</option>" +
"        </select> entries" +
"	      <a class=\"paginate_disabled_previous prev-field\" tabindex=\"0\" role=\"button\">Previous</a><a class=\"paginate_enabled_next next-field\" tabindex=\"0\" role=\"button\">Next</a>" +
"	      <input type=\"text\" class=\"filterbox\" placeholder=\"Filter...\" />" +
"	    </div>" +
"	    <div class=\"jtox-ds-tables\">" +
"	      <div class=\"jtox-ds-fixed\">" +
"	        <table></table>" +
"	      </div><div class=\"jtox-ds-variable\">" +
"	        <table></table>" +
"	      </div>" +
"	    </div>" +
"	  </div>" +
""; // end of #jtox-dataset 

jT.templates['compound-one-feature']  = 
"    <div id=\"jtox-ds-feature\" class=\"jtox-ds-feature\"><input type=\"checkbox\" checked=\"yes\" class=\"jtox-checkbox\" /><span class=\"data-field jtox-title\" data-field=\"title\"> ? </span><sup><a target=\"_blank\" class=\"data-field attribute\" data-attribute=\"href\" data-field=\"uri\">?</a></sup></div>" +
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

jT.templates['compound-one-detail']  = 
"    <table>" +
"      <tbody>" +
"        <tr id=\"jtox-one-detail\">" +
"          <th class=\"right\"><span class=\"data-field\" data-field=\"title\"> ? </span><sup><a target=\"_blank\" class=\"data-field attribute\" data-attribute=\"href\" data-field=\"uri\">?</a></sup></th>" +
"          <td class=\"left data-field\" data-field=\"value\"> ? </td>" +
"        </tr>" +
"      </tbody>" +
"    </table>" +
""; // end of #jtox-one-detail 

jT.templates['compound-details-table']  = 
"    <table id=\"jtox-details-table\" class=\"jtox-details-table\"></table>" +
""; // end of #jtox-details-table 

jT.templates['all-dataset']  = 
"	  <div id=\"jtox-dataset\">" +
"      <table></table>" +
"	  </div>" +
""; // end of #jtox-dataset 

jT.templates['all-model']  = 
"	  <div id=\"jtox-model\">" +
"      <table></table>" +
"	  </div>" +
""; // end of #jtox-model 

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
"	      <li><a href=\"#jtox-pchem\" data-type=\"P-CHEM\">P-Chem (0)</a></li>" +
"	      <li><a href=\"#jtox-envfate\" data-type=\"ENV_FATE\">Env Fate (0)</a></li>" +
"	      <li><a href=\"#jtox-ecotox\" data-type=\"ECOTOX\">Eco Tox (0)</a></li>" +
"	      <li><a href=\"#jtox-tox\" data-type=\"TOX\">Tox (0)</a></li>" +
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
"	    <div id=\"jtox-compo-tab\" class=\"jtox-compo-tab unloaded\"></div>" +
"	    <div id=\"jtox-pchem\" class=\"jtox-study-tab P-CHEM\">" +
"	    	<div class=\"float-right\">" +
"	      	<button class=\"expand-all\">Expand all</button><button class=\"collapse-all\">Collapse all</button>" +
"	    	</div>" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\" /></p>" +
"	      <h4 class=\"data-field camelCase\" data-field=\"showname\"> ? </h4>" +
"      </div>" +
"	    <div id=\"jtox-envfate\" class=\"jtox-study-tab ENV_FATE\">" +
"	    	<div class=\"float-right\">" +
"	      	<button class=\"expand-all\">Expand all</button><button class=\"collapse-all\">Collapse all</button>" +
"	    	</div>" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\" /></p>" +
"	      <h4 class=\"data-field camelCase\" data-field=\"showname\"> ? </h4>" +
"	    </div>" +
"	    <div id=\"jtox-ecotox\" class=\"jtox-study-tab ECOTOX\">" +
"	    	<div class=\"float-right\">" +
"	      	<button class=\"expand-all\">Expand all</button><button class=\"collapse-all\">Collapse all</button>" +
"	    	</div>" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\" /></p>" +
"	      <h4 class=\"data-field camelCase\" data-field=\"showname\"> ? </h4>" +
"	    </div>" +
"	    <div id=\"jtox-tox\" class=\"jtox-study-tab TOX\">" +
"	    	<div class=\"float-right\">" +
"	      	<button class=\"expand-all\">Expand all</button><button class=\"collapse-all\">Collapse all</button>" +
"	    	</div>" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\" /></p>" +
"	      <h4 class=\"data-field camelCase\" data-field=\"showname\"> ? </h4>" +
"	    </div>" +
"	  </div>" +
""; // end of #jtox-studies 

jT.templates['one-study']  = 
"    <div id=\"jtox-study\" class=\"jtox-study jtox-foldable folded unloaded\">" +
"      <div class=\"title\"><p class=\"data-field\" data-field=\"title\">? (0)</p></div>" +
"      <div class=\"content\">" +
"        <table class=\"jtox-study-table content\"></table>" +
"      </div>" +
"    </div>" +
""; // end of #jtox-study 


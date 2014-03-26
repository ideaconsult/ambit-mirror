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
    if (typeof callback != 'function')
      callback = window[callback];
    return callback.apply((self !== undefined && self != null) ? self : document, Array.prototype.slice.call(arguments, 2));
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
  
  /*
  Passed a HTML DOM element - it clears all children folowwing last one. Pass null for clearing all.
  */
  clearChildren: function(obj, last) {
    while (obj.lastChild && obj.lastChild != last) {
      obj.removeChild(obj.lastChild);
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
    return url + (url.indexOf('?') > 0 ? "&" : "?") + param;
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
/* toxdataset.js - General, universal dataset visualizer.
 *
 * Copyright 2012-2013, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxDataset = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    "showTabs": true,         // should we show tabs with groups, or not
    "showExport": true,       // should we add export tab up there
    "showControls": true,     // should we show the pagination/navigation controls.
    "pageSize": 20,           // what is the default (startint) page size.
    "pageStart": 0,           // what is the default startint point for entries retrieval
    "metricFeature": "http://www.opentox.org/api/1.1#Similarity",   // This is the default metric feature, if no other is specified
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
          if (!ccLib.isNull(miniset.dataEntry[0].compound.metric))
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
        {type: "chemical/x-mdl-sdfile", icon: "images/sdf.jpg"},
        {type: "chemical/x-cml", icon: "images/cml.jpg"},
        {type: "chemical/x-daylight-smiles", icon: "images/smi.png"},
        {type: "chemical/x-inchi", icon: "images/inchi.png"},
        {type: "text/uri-list", icon: "images/link.png"},
        {type: "application/pdf", icon: "images/pdf.png"},
        {type: "text/csv", icon: "images/excel.png"},
        {type: "text/plain", icon: "images/excel.png"},
        {type: "text/x-arff", icon: "images/weka.png"},
        {type: "text/x-arff-3col", icon: "images/weka.png"},
        {type: "application/rdf+xml", icon: "images/rdf.gif"},
        {type: "application/json", icon: "images/json.png"}
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
        	}
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
    self.settings = jT.$.extend(true, {}, newDefs, jT.settings, settings); // i.e. defaults from jToxDataset
    self.features = null; // features, as downloaded from server, after being processed.
    self.dataset = null; // the last-downloaded dataset.
    self.groups = null; // computed groups, i.e. 'groupName' -> array of feature list, prepared.
    self.fixTable = self.varTable = null; // the two tables - to be initialized in prepareTables.
    self.instanceNo = instanceCount++;
    self.entriesCount = null;
    self.suspendEqualization = false;
    self.orderList = [];
    
    root.appendChild(jT.getTemplate('#jtox-dataset'));
    
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

    // finally make the query, if Uri is provided      
    if (self.settings['datasetUri'] !== undefined){
      self.queryDataset(self.settings['datasetUri']);
    }
  };
  
  // now follow the prototypes of the instance functions.
  cls.prototype = {
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
        createATab(grId, gr.replace(/_/g, " "));
        
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
          empty = false;
          if (idx == "name") {
            if (isMain)
              nodeFn(null, fId, divEl);
          }
          else if (!isMain || level == 1) {
            var title = self.features[fId].title;
            if (!ccLib.isNull(title))
              nodeFn(fId, title, divEl);
          }
        });

        if (empty)
          emptyList.push(idx);
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
          img.src = self.baseUrl + expo.icon;
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
                '<span class="jtox-details-open ui-icon ui-icon-circle-triangle-e" title="Press to open/close detailed info for the entry"></span>';
          }
        },
        { "sClass": "jtox-hidden", "mData": "index", "sDefaultContent": "-", "bSortable": true, "mRender": function(data, type, full) { return ccLib.isNull(self.orderList) ? 0 : self.orderList[data]; } }, // column used for ordering
        { "sClass": "jtox-hidden jtox-ds-details paddingless", "mData": "index", "sDefaultContent": "-", "mRender": function(data, type, full) { return ''; } } // details column
      );
      
      varCols.push({ "sClass": "jtox-hidden jtox-ds-details paddingless", "mData": "index", "mRender": function(data, type, full) { return ''; }  });

      // prepare the function for column switching...      
      var fnShowColumn = function(sel, idx) {
        return function() {
          var cells = jT.$(sel + ' table tr>*:nth-child(' + (idx + 1) + ')', self.rootElement);
          if (this.checked)
            jT.$(cells).show();
          else
            jT.$(cells).hide();
          self.equalizeTables();
        }
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
      
      var fnShowDetails = function(row) {
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
                if (id != null && cls.shortFeatureId(id) != "Diagram") {
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
      };

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
              return (type != "display") ? '' + data : jT.shortenedData(data, "Press to copy the value in the clipboard");
            };
            col["sWidth"] = "75px";
          }
          
          // finally - assign column switching to the checkbox of main tab.
          if (level == 1)
            ++checkIdx;
          jT.$(checkList[checkIdx]).on('change', fnShowColumn(colList == fixCols ? '.jtox-ds-fixed' : '.jtox-ds-variable', colList.length))
          
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
          if (idx != "name")
            self.features[fid].used = self.features[sameAs].used = true;
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
    queryEntries: function(from, size, fnComplete) {
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
          if (typeof fnComplete == 'function')
            fnComplete();
        }
      });
    },
    
    /* Makes a query to the server for particular dataset, asking for feature list first, so that the table(s) can be 
    prepared.
    */
    queryDataset: function (datasetUri) {
      var self = this;
      
      // we want to take into account the passed page & pagesize, but remove them, afterwards.
      var urlObj = ccLib.parseURL(datasetUri);
      if (urlObj.params['pagesize'] !== undefined) {
        var sz = parseInt(urlObj.params['pagesize']);
        if (sz > 0)
          self.settings.pageSize = sz;
          datasetUri = ccLib.removeParameter(datasetUri, 'pagesize');
      }
      if (urlObj.params['page'] !== undefined) {
        var beg = parseInt(urlObj.params['page']);
        if (beg >= 0)
          self.settings.pageStart = beg * self.settings.pageSize;
        datasetUri = ccLib.removeParameter(datasetUri, 'page');
      }
      
      self.baseUrl = ccLib.isNull(self.settings.baseUrl) ? jT.grabBaseUrl(datasetUri) : self.settings.baseUrl;
      
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
          self.queryEntries(self.settings.pageStart, self.settings.pageSize); // and make the query for actual data
        }
      });
    },
  }; // end of prototype
  
  // some public, static methods
  cls.shortFeatureId = function(fId) {
    return fId.substr(fId.indexOf('#') + 1); // a small trick - 'not-found' returns -1, and adding 1 results in exactly what we want: 0, i.e. - start, i.e. - no change.
  };
  
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
/* toxstudy.js - Study-related functions from jT.
 *
 * Copyright 2012-2013, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jToxStudy = (function () {
  var defaultSettings = {
    configuration: { 
      columns: { 
    		"main" : { },
    		"parameters": { },
    		"conditions": { },
    		"effects": { },
    		"protocol": { },
    		"interpretation": { }
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
    self.suffix = '_' + instanceCount++;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even in manual initialization.
    
    self.settings = jT.$.extend({}, defaultSettings, jT.settings, settings); // i.e. defaults from jToxStudy
    // now we have our, local copy of settings.
    
    // get the main template, add it (so that jQuery traversal works) and THEN change the ids.
    // There should be no overlap, because already-added instances will have their IDs changed already...
    var tree = jT.getTemplate('#jtox-studies');
    root.appendChild(tree);
    jT.changeTabsIds(tree, self.suffix);
    
    // keep on initializing...
    var loadPanel = function(panel){
      if (panel){
        jT.$('.jtox-study.unloaded', panel).each(function(i){
          var table = this;
          jT.call(self, jT.$(table).data('jtox-uri'), function(study){
            if (!!study) {
              jT.$(table).removeClass('unloaded folded');  
              jT.$(table).addClass('loaded');
              self.processStudies(panel, study.study, false);
            }
          });  
        });
      }
    };
    
    // initialize the tab structure for several versions of dataTables.
    jT.$(tree).tabs({
      "select" : function(event, ui) {
        loadPanel(ui.panel);
      },
      "beforeActivate" : function(event, ui) {
        if (ui.newPanel)
          loadPanel(ui.newPanel[0]);
      }
    });
    
    // when all handlers are setup - make a call, if needed.    
    if (self.settings['substanceUri'] !== undefined){
      self.querySubstance(self.settings['substanceUri']);
    }
  };
  
  // now follow the prototypes of the instance functions.
  cls.prototype = {
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
        
        // install the click handler for fold / unfold
        jT.$('.jtox-study-title', theCat).click(function() {
          jT.$(theCat).toggleClass('folded');
        });
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
      var defaultColumns = [
        { "sTitle": "Name", "sClass": "center middle", "sWidth": "20%", "mData": "protocol.endpoint" }, // The name (endpoint)
        { "sTitle": "Endpoint", "sClass": "center middle jtox-multi", "sWidth": "15%", "mData": "effects", "mRender": function (data, type, full) { return self.renderMulti(data, type, full, "endpoint");  } },   // Effects columns
        { "sTitle": "Result", "sClass": "center middle jtox-multi", "sWidth": "15%", "mData" : "effects", "mRender": function (data, type, full) { return self.renderMulti(data, type, full, function (data, type) { return formatLoHigh(data.result, type) }) } },
        { "sTitle": "Guideline", "sClass": "center middle", "sWidth": "15%", "mData": "protocol.guideline", "mRender" : "[,]", "sDefaultContent": "?"  },    // Protocol columns
        { "sTitle": "Owner", "sClass": "center middle shortened", "sWidth": "50px", "mData": "citation.owner", "sDefaultContent": "?"  }, 
        { "sTitle": "UUID", "sClass": "center middle", "sWidth": "50px", "mData": "uuid", "bSearchable": false, "mRender" : function(data, type, full) { return type != "display" ? '' + data : jT.shortenedData(data, "Press to copy the UUID in the clipboard"); } }
      ];
  
      var category = study.protocol.category.code;
      var theTable = jT.$('.' + category + ' .jtox-study-table', tab)[0];
      if (!jT.$(theTable).hasClass('dataTable')) {
  
        var colDefs = [];
        
        // start filling it
        var parCount = 0;
  
        var modifyColumn = function(col, group) {
          if (group == null)
            group = "main";
          var name = col.sTitle.toLowerCase();
          
          // helper function for retrieving col definition, if exists. Returns empty object, if no.          
          var getColDef = function (cat) {
            var catCol = self.settings.configuration.columns[cat];
            if (!ccLib.isNull(catCol)) {
              catCol = catCol[group];
              if (!ccLib.isNull(catCol))
                catCol = catCol[name];
            }

            if (ccLib.isNull(catCol))
              catCol = {};
            return catCol;
          };
          // now form the default column, if existing and the category-specific one...
          // extract column redefinitions and merge them all.
          col = jT.$.extend(col, getColDef('_'), getColDef(category));
          return ccLib.isNull(col.bVisible) || col.bVisible ? col : null;
        };
  
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
            col = modifyColumn(col, group);
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
            
            out += (data.unit = ccLib.trim(data.unit));
          }
          return out.replace(/ /g, "&nbsp;");
        };
        
        var formatUnits = function(data, unit) {
          data = ccLib.trim(data);
          unit = ccLib.trim(unit);
          return !ccLib.isNull(data) ? (data + (!!unit ? "&nbsp;" + unit : "")) : "-";
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
          
          col = modifyColumn(col, "parameters");
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
          
          col = modifyColumn(col, "conditions");
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
        putDefaults(1, 2, "effects");
  
        // now is time to put interpretation columns..
        putAGroup(study.interpretation, function(i){
          var col = { "sTitle": i, "sClass" : "center middle jtox-multi", "mData" : "interpretation." + i, "sDefaultContent": "-"};
          return modifyColumn(col, "interpretation");
        });
        
        // finally put the protocol entries
        putDefaults(3, 3, "protocol");
        
        // but before given it up - make a small sorting..
        colDefs.sort(function(a, b) {
          var valA = ccLib.isNull(a.iOrder) ? 0 : a.iOrder;
          var valB = ccLib.isNull(b.iOrder) ? 0 : b.iOrder;
          return valA - valB;
        });
        
        // READYY! Go and prepare THE table.
        jT.$(theTable).dataTable( {
          "bPaginate": true,
          "bProcessing": true,
          "bLengthChange": false,
  				"bAutoWidth": false,
          "sDom" : "rt<Fip>",
          "aoColumns": colDefs,
          "fnInfoCallback": function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
            var el = jT.$('.jtox-study-title .data-field', jT.$(this).parents('.jtox-study'))[0];
            el.innerHTML = self.updateCount(el.innerHTML, iTotal);
            return sPre;
          },
  				"oLanguage": {
            "sProcessing": "<img src='" + self.baseUrl + "images/24x24_ambit.gif' border='0'>",
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
      var catList = jT.$('.jtox-study', self.rootElement);
      while(catList.length > 0) {
        catList[0].parentNode.removeChild(catList[0]);
      }
      
      // create the groups on the corresponding tabs
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
      var cats = [];
      
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
        jT.$(theTable).colResizable();
      }
      
      // we need to fix columns height's because of multi-cells
      jT.$('#' + theTable.id + ' .jtox-multi').each(function(index){
        this.style.height = '' + this.offsetHeight + 'px';
      });
    },
    
    formatConcentration: function (precision, val, unit) {
    	return ((precision === undefined || precision === null || "=" == precision ? "" : precision) + val + " " + (unit == null || unit == '' ? "% (w/w)" : unit)).replace(/ /g, "&nbsp;");
    },
    
    processComposition: function(json){
      var self = this;
      var tab = jT.$('.jtox-composition', self.rootElement)[0];
      
      // clear the old tabs, if any.
      if (jT.$(tab).hasClass('unloaded')){
        jT.$(tab).removeClass('unloaded');
        jT.$(tab).empty();
      }
      
      var prepareFillTable = function (json, panel) {
        var theTable = jT.$('.substances-table', panel);
        // prepare the table...
        jT.$(theTable).dataTable({
  				"bSearchable": true,
  				"bProcessing" : true,
  				"bPaginate" : true,
          "sDom" : "rt<Fip>",
/*   				"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>', */
/* 				  "sPaginationType": "full_numbers", */
  				"sPaginate" : ".dataTables_paginate _paging",
  				"bAutoWidth": false,
  				"oLanguage": {
            "sProcessing": "<img src='" + self.baseUrl + "images/24x24_ambit.gif' border='0'>",
            "sLoadingRecords": "No substances found.",
            "sZeroRecords": "No substances found.",
            "sEmptyTable": "No substances available.",
            "sInfo": "Showing _TOTAL_ substance(s) (_START_ to _END_)",
          },
  		    "aoColumns": [
            {  //1
    					"sClass" : "left",
    					"sWidth" : "10%",
    					"mData" : "relation",
    					"mRender" : function(val, type, full) {
    					  if (type != 'display')
    					    return '' + val;
    					  var func = ("HAS_ADDITIVE" == val) ? full.proportion.function_as_additive : "";
    					  return '<span class="camelCase">' +  val.replace("HAS_", "").toLowerCase() + '</span>' + ((func === undefined || func === null || func == '') ? "" : " (" + func + ")");
              }
            },	    
    				{ //2
    					"sClass" : "camelCase left",
    					"sWidth" : "15%",
    					"mData" : "component.compound.name",
    					"mRender" : function(val, type, full) {
    						return (type != 'display') ? '' + val : 
    						  '<a href="' + full.component.compound.URI + '" target="_blank" title="Click to view the compound"><span class="ui-icon ui-icon-link" style="float: left; margin-right: .3em;"></span></a>' + val;
    					}
    				},	    	
    				{ //3
    					"sClass" : "left",
    					"sWidth" : "10%",
    					"mData" : "component.compound.einecs",
    				},
    				{ //4
    					"sClass" : "left",
    					"sWidth" : "10%",
    					"mData" : "component.compound.cas",
    				},
    				{ //5
    					"sClass" : "center",
    					"sWidth" : "15%",
    					"mData" : "proportion.typical",
    					"mRender" : function(val, type, full) { return type != 'display' ? '' + val.value : self.formatConcentration(val.precision, val.value, val.unit); }
    				},
    				{ //6
    					"sClass" : "center",
    					"sWidth" : "15%",
    					"mData" : "proportion.real",
    					"mRender" : function(val, type, full) { return type != 'display' ? '' + val.lowerValue : self.formatConcentration(val.lowerPrecision, val.lowerValue, val.unit); }
    				},
    				{ //7,8
    					"sClass" : "center",
    					"sWidth" : "15%",
    					"mData" : "proportion.real",
    					"mRender" : function(val, type, full) { return type != 'display' ? '' + val.upperValue : self.formatConcentration(val.upperPrecision, val.upperValue, val.unit); }
    				},
            { //9
    					"sClass" : "center",
    					"bSortable": false,
    					"mData" : "component.compound.URI",
    					"mRender" : function(val, type, full) {
    					  return !val ? '' : '<a href="' + self.baseUrl + 'substance?type=related&compound_uri=' + encodeURIComponent(val) + '" target="_blank">Also contained in...</span></a>';
  					}
	    		}    				
  		    ]
  		  });

        // and fill up the table.
        jT.$(theTable).dataTable().fnAddData(json);
        return theTable;
      };
      
      var substances = {};

      jToxDataset.processFeatures(json.feature);
      // proprocess the data...
      for (var i = 0, cmpl = json.composition.length; i < cmpl; ++i) {
        var cmp = json.composition[i];
        
        jToxDataset.processEntry(cmp.component, json.feature, fnDatasetValue);

        // now prepare the subs        
        var theSubs = substances[cmp.compositionUUID];
        if (theSubs === undefined)
          substances[cmp.compositionUUID] = theSubs = { name: "", purity: "", maxvalue: 0, uuid : cmp.compositionUUID, composition : [] };
        
        theSubs.composition.push(cmp);
        var val = cmp.proportion.typical;
        if (cmp.relation == 'HAS_CONSTITUENT' && (theSubs.maxvalue < val.value || theSubs.name == '')) {
          theSubs.name = cmp.component.compound['name'] + ' ' + self.formatConcentration(val.precision, val.value, val.unit);
          theSubs.maxvalue = val.value;
          val = cmp.proportion.real;
          theSubs.purity = (val.lowerValue + '-' + val.upperValue + ' ' + (val.unit == null || val.unit == '' ? "% (w/w)" : val.unit)).replace(/ /g, "&nbsp;");
        }
      }
      
      // now make the actual filling
      for (var i in substances) {
        var panel = jT.getTemplate('#jtox-compoblock');
        tab.appendChild(panel);
        ccLib.fillTree(jT.$('.composition-info', panel)[0], substances[i]);
        prepareFillTable(substances[i].composition, panel);
      }
    },
    
    querySummary: function(substanceURI) {
      var self = this;
      
      jT.call(self, substanceURI + "/studysummary", function(summary) {
        if (!!summary && !!summary.facet)
          self.processSummary(summary.facet);
      });
    },
    
    queryComposition: function(substanceURI) {
      var self = this;
      
      jT.call(self, substanceURI + "/composition", function(composition) {
        if (!!composition && !!composition.composition)
          self.processComposition(composition);
        });
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
              jToxDataset.processDataset(dataset, null, fnDatasetValue);
              ccLib.fillTree(rootTab, dataset.dataEntry[0]);
            }
          });
           
          // query for the summary and the composition too.
          self.querySummary(substance.URI);
          self.queryComposition(substance.URI);
        }
      });
    }
  }; // end of prototype
  
  return cls;
})();
window.jT = window.jToxKit = {
	templateRoot: null,

	/* A single place to hold all necessary queries. Parameters are marked with <XX> and formatString() (common.js) is used
	to prepare the actual URLs
	*/
	queries: {
		taskPoll: "/task/<1>",
	},
	
	templates: { },        // html2js routine will fill up this variable
	tools: { },        // additional, external tools added with html2js

	/* SETTINGS. The following parametes can be passed in settings object to jT.init(), or as data-XXX - with the same names. Values set here are the defaults.
	*/
	settings: {
  	jsonp: false,                   // whether to use JSONP approach, instead of JSON.
  	crossDomain: false,             // should it expect cross-domain capabilities for the queries.
  	host: null,                     // same as above, but for the calling server, i.e. - the one that loaded the page.        
  	timeout: 15000,                 // the timeout an call to the server should be wait before the attempt is considered error.
  	pollDelay: 200,                 // after how many milliseconds a new attempt should be made during task polling.
  	onConnect: function(s){ },		  // function (service): called when a server request is started - for proper visualization. Part of settings.
  	onSuccess: function(s, c, m) { },	// function (code, mess): called on server request successful return. It is called along with the normal processing. Part of settings.
  	onError: function (s, c, m) { if (!!console && !!console.log) console.log("jToxKit call error (" + c + "): " + m + " from request: [" + s + "]"); },		// function (code, mess): called on server reques error. Part of settings.
  },
	
	// form the "default" baseUrl if no other is supplied
	formBaseUrl: function(url) {
    return url.protocol + "://" + url.host + (url.port.length > 0 ? ":" + url.port : '') + '/' + url.segments[0] + '/';	
	},
    
  // the jToxKit initialization routine, which scans all elements, marked as 'jtox-toolkit' and initializes them
	init: function() {
  	var self = this;
  	
  	self.initTemplates();

    // make this handler for UUID copying. Once here - it's live, so it works for all tables in the future
    jT.$(document).on('click', '.jtox-toolkit span.ui-icon-copy', function (e) { ccLib.copyToClipboard(jT.$(this).data('uuid')); return false;});
  
    // scan the query parameter for settings
		var url = ccLib.parseURL(document.location);
		var queryParams = url.params;
		queryParams.host = self.formBaseUrl(url);
	
    self.settings = jT.$.extend(self.settings, queryParams); // merge with defaults
    
	  // initializes the kit, based on the passed kit name
	  var initKit = function(element, params) {
    	if (params.kit == "study")
    	  new jToxStudy(element, params);
      if (params.kit == "dataset")
        new jToxDataset(element, params);
	  };
	  
  	// now scan all insertion divs
  	jT.$('.jtox-toolkit').each(function(i) {
    	var dataParams = jT.$.extend(true, self.settings, jT.$(this).data());
    	if (!dataParams.manualInit){
    	  var el = this;
    	  // first, get the configuration, if such is passed
    	  if (!ccLib.isNull(dataParams.configFile)) {
    	    // we'll use a trick here so the baseUrl parameters set so far to take account... thus passing 'fake' kit instance
    	    // as the first parameter of jT.call();
      	  self.call({ settings: dataParams}, dataParams.configFile, function(config){
        	  if (!!config)
        	    dataParams['configuration'] = config;
            initKit(el, dataParams);
      	  });
    	  }
    	  else
    	    initKit(el, dataParams);
      }
  	});
	},
	
	initTemplates: function() {
	  var self = this;

    var root = jT.$('.jtox-template')[0];
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
  	var el = jT.$(selector, this.templateRoot)[0];
  	if (!!el){
    	var el = el.cloneNode(true);
      el.removeAttribute('id');
    }
    return el;
	},
	
	insertTool: function (name, root) {
	  var html = this.tools[name];
	  if (!ccLib.isNull(html))
  	  root.innerHTML = html;
  	 return root;
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
  
  copySpan: function (data, message) {
    return ;
  },
  
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
		
	/* Poll a given taskId and calls the callback when a result from the server comes - 
	be it "running", "completed" or "error" - the callback is always called.
	*/
	pollTask : function(task, callback) {
		var self = this;
		if (task === undefined || task.task === undefined || task.task.length < 1){
		  ccLib.fireCallback(self.settings.onError, self, '-1', localMessage.taskFailed);
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
      return this.settings.host;
	},
	
	/* Makes a server call with the provided method. If none is given - the internally stored one is used
	*/
	call: function (kit, service, callback, adata){
	  var settings = jT.$.extend({"baseUrl" : this.settings.host}, this.settings);
		if (kit == null)
		  kit = this;
		else 
  		settings = jT.$.extend(true, settings, kit.settings);

		ccLib.fireCallback(settings.onConnect, kit, service);
		  
		var method = 'GET';
		var accType = settings.jsonp ? "application/x-javascript" : "application/json";
		
		if (adata !== undefined){
			method = 'POST';
			if (typeof adata == "boolean")
				adata = {};
		}
		else if (settings.jsonp)
		  adata = { media: accType };
		else
			adata = { };

		// on some queries, like tasks, we DO have baseUrl at the beginning
		if (service.indexOf("http") != 0)
			service = settings.baseUrl + service;
			
		// now make the actual call
		jT.$.ajax(service, {
			dataType: settings.jsonp ? 'jsonp' : 'json',
			headers: { Accept: accType },
			crossDomain: settings.crossDomain || settings.jsonp,
			timeout: settings.timeout,
			type: method,
			data: adata,
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

// we need to do this here - because other tools/libraries could have scheduled themselves on 'ready',
// so it'll be too late to make this assignment then. Also - we can use jT.$ from now on :-)
jT.$ = jQuery; // .noConflict();

  	
jT.$(document).ready(function(){
  jT.init();
});
jT.templates['all-dataset']  = 
"	  <div id=\"jtox-dataset\">" +
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

jT.templates['dataset-one-feature']  = 
"    <div id=\"jtox-ds-feature\" class=\"jtox-ds-feature\"><input type=\"checkbox\" checked=\"yes\" class=\"jtox-checkbox\" /><span class=\"data-field jtox-title\" data-field=\"title\"> ? </span><sup><a target=\"_blank\" class=\"data-field attribute\" data-attribute=\"href\" data-field=\"uri\">?</a></sup></div>" +
""; // end of #jtox-ds-feature 

jT.templates['dataset-download']  = 
"    <div id=\"jtox-ds-download\" class=\"jtox-inline jtox-ds-download\">" +
"      <a target=\"_blank\"><img class=\"borderless\"/></a>" +
"    </div>" +
""; // end of #jtox-ds-download 

jT.templates['dataset-export']  = 
"    <div id=\"jtox-ds-export\">" +
"      <div class=\"jtox-inline\">Download dataset as: </div>" +
"      <div class=\"jtox-inline jtox-exportlist\"></div>" +
"    </div>" +
""; // end of #jtox-ds-export 

jT.templates['dataset-one-detail']  = 
"    <table>" +
"      <tbody>" +
"        <tr id=\"jtox-one-detail\">" +
"          <th class=\"right\"><span class=\"data-field\" data-field=\"title\"> ? </span><sup><a target=\"_blank\" class=\"data-field attribute\" data-attribute=\"href\" data-field=\"uri\">?</a></sup></th>" +
"          <td class=\"left data-field\" data-field=\"value\"> ? </td>" +
"        </tr>" +
"      </tbody>" +
"    </table>" +
""; // end of #jtox-one-detail 

jT.templates['dataset-details-table']  = 
"    <table id=\"jtox-details-table\" class=\"jtox-details-table\"></table>" +
""; // end of #jtox-details-table 

jT.templates['all-studies']  = 
"	  <div id=\"jtox-studies\">" +
"	    <ul>" +
"	      <li><a href=\"#jtox-substance\">IUC Substance</a></li>" +
"	      <li><a href=\"#jtox-composition\">Composition</a></li>" +
"	      <li><a href=\"#jtox-pchem\" data-type=\"P-CHEM\">P-Chem (0)</a></li>" +
"	      <li><a href=\"#jtox-envfate\" data-type=\"ENV_FATE\">Env Fate (0)</a></li>" +
"	      <li><a href=\"#jtox-ecotox\" data-type=\"ECOTOX\">Eco Tox (0)</a></li>" +
"	      <li><a href=\"#jtox-tox\" data-type=\"TOX\">Tox (0)</a></li>" +
"	    </ul>" +
"	    <div id=\"jtox-substance\" class=\"jtox-substance\">" +
"	      <table class=\"dataTable\">" +
"	        <thead>" +
"	          <tr>" +
"	            <th class=\"right jtox-size-third\">IUC Substance name:</th>" +
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
"	    <div id=\"jtox-composition\" class=\"jtox-composition unloaded\"></div>" +
"	    <div id=\"jtox-pchem\" class=\"jtox-study-tab P-CHEM\">" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\" /></p>" +
"	      <h4 class=\"data-field camelCase\" data-field=\"showname\"> ? </h4>" +
"      </div>" +
"	    <div id=\"jtox-envfate\" class=\"jtox-study-tab ENV_FATE\">" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\" /></p>" +
"	      <h4 class=\"data-field camelCase\" data-field=\"showname\"> ? </h4>" +
"	    </div>" +
"	    <div id=\"jtox-ecotox\" class=\"jtox-study-tab ECOTOX\">" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\" /></p>" +
"	      <h4 class=\"data-field camelCase\" data-field=\"showname\"> ? </h4>" +
"	    </div>" +
"	    <div id=\"jtox-tox\" class=\"jtox-study-tab TOX\">" +
"	      <p><input type=\"text\" class=\"jtox-study-filter ui-input\" placeholder=\"Filter...\" /></p>" +
"	      <h4 class=\"data-field camelCase\" data-field=\"showname\"> ? </h4>" +
"	    </div>" +
"	  </div>" +
""; // end of #jtox-studies 

jT.templates['composition-block']  = 
"    <div id=\"jtox-compoblock\" class=\"jtox-compoblock\">" +
"      <table class=\"dataTable composition-info jtox-font-small\">" +
"        <thead>" +
"          <tr><th>Composition name:</th><td class=\"data-field camelCase\" data-field=\"name\"> ? </td></tr>" +
"          <tr><th>Composition UUID:</th><td class=\"data-field\" data-field=\"uuid\"> ? </td></tr>" +
"          <tr><th>Purity of IUC Substance:</th><td class=\"data-field\" data-field=\"purity\"> ? </td></tr>" +
"        </thead>" +
"      </table>" +
"      <table class=\"substances-table\">" +
"        <thead>" +
"          <tr>" +
"            <th>Type</th>" +
"            <th>Name</th>" +
"            <th>EC No.</th>" +
"            <th>CAS No.</th>" +
"            <th>Typical concentration</th>" +
"            <th colspan=\"2\">Concentration ranges</th>" +
"            <th></th>" +
"          </tr>" +
"        </thead>" +
"      </table>" +
"    </div>" +
""; // end of #jtox-compoblock 

jT.templates['one-study']  = 
"    <div id=\"jtox-study\" class=\"jtox-study jtox-foldable folded unloaded\">" +
"      <div class=\"jtox-study-title\"><p class=\"data-field\" data-field=\"title\">? (0)</p></div>" +
"      <table class=\"jtox-study-table\"></table>" +
"    </div>" +
""; // end of #jtox-study 


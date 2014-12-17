/* toxmatrix.js - Read-across UI tool
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

var jTConfig = {};

function jTConfigurator(kit) {
  return jTConfig.matrix;
}

var jToxBundle = {
	createForm: null,
	rootElement: null,
	bundleUri: null,
	bundleSummary: {
  	compound: 0,
  	substance: 0,
  	property: 0,
	},
	
	edit: {
  	added: { },
  	deleted: { },
		refreshMatrix: true,
	},
		
  settings: {
  	studyTypeList: _i5.qaSettings["Study result type"],
  	maxStars: 10,
  	configuration: {
    	columns: {
      	
    	}
  	}
  },
  
  parseFeatureId: function (featureId, kit) {
    var parse = featureId.substr(kit.settings.baseUrl.length).match(/property\/([^\/]+)\/([^\/]+)\/.+/);
    if (parse == null)
      return null;
    else
      return {
        topcategory: parse[1].replace("+", " "),
        category: parse[2].replace("+", " ")
      };
  },
  
	init: function (root, settings) {
		var self = this;

		self.rootElement = root;
    self.settings = $.extend(self.settings, jT.settings, settings);
    
    // deal with some configuration
    if (typeof self.settings.studyTypeList == 'string')
      self.settings.studyTypeList = window[self.settings.studyTypeList];
		
		// the (sub)action in the panel
		var loadAction = function () {
    	if (!this.checked)
    		return;
      document.body.className = this.id;
	    var method = $(this).parent().data('action');
	    if (!method)
	    	return;
	    ccLib.fireCallback(self[method], self, this.id, $(this).closest('.ui-tabs-panel')[0], false);
		};
		
    var loadPanel = function(panel) {
      if (panel){
        var subs = $('.jq-buttonset.action input:checked', panel);
        if (subs.length > 0)
      	  subs.each(loadAction);
        else 
  		    ccLib.fireCallback(self[$(panel).data('action')], self, panel.id, panel, true);
      }
    };
    
    // initialize the tab structure for several versions of dataTables.
    $(root).tabs({
      "disabled": [1, 2, 3, 4],
      "heightStyle": "fill",
      "select" : function(event, ui) {
        loadPanel(ui.panel);
      },
      "activate" : function(event, ui) {
        if (ui.newPanel)
          loadPanel(ui.newPanel[0]);
      },
      "create" : function(event, ui) {
        if (ui.panel)
          loadPanel(ui.panel[0]);
      }
    });
    
    $('.jq-buttonset', root).buttonset();
    $('.jq-buttonset.action input', root).on('change', loadAction);
    
    self.onIdentifiers(null, $('#jtox-identifiers', self.rootElement)[0]);
    // finally, if provided - load the given bundleUri
    if (!ccLib.isNull(self.settings.bundleUri))
	    self.load(self.settings.bundleUri);
    
    return self;
	},
	
	starHighlight: function (root, stars) {
    $('span', root).each(function (idx) {
      if (idx < stars)
        $(this).removeClass('transparent');
      else
        $(this).addClass('transparent');
    });
	},
	
	modifyUri: function (uri) {
  	return ccLib.addParameter(uri, "bundle_uri=" + encodeURIComponent(this.bundleUri));
	},
	
	onIdentifiers: function (id, panel) {
	  var self = this;
	  if (!$(panel).hasClass('initialized')) {
  	  $(panel).addClass('initialized');
      var checkForm = function () {
      	this.placeholder = "You need to fill this box";
      	return this.value.length > 0;
      };
      
      self.createForm = $('form', panel)[0];
      self.createForm.assStart.onclick = function (e) {
        e.preventDefault();
        e.stopPropagation();
      	if (ccLib.validateForm(self.createForm, checkForm)) {
  		    jT.service(self, '/bundle', { method: 'POST', data: ccLib.serializeForm(self.createForm)}, function (bundleUri) {
    		    self.load(bundleUri);
  			    console.log("Data set created: " + JSON.stringify(bundleUri));
  		    });
  		  }
      };

      self.createForm.assFinalize.style.display = 'none';
      self.createForm.assDuplicate.style.display = 'none';
      
      var starsEl = $('.data-stars-field', self.createForm)[0];
      starsEl.innerHTML += jT.ui.putStars(self, 0, "Assessment rating");
      $('span.ui-icon-star', starsEl)
      .on('mouseover', function (e) {
        for (var el = this; !!el; el = el.previousElementSibling)
          $(el).removeClass('transparent');
        for (var el = this.nextElementSibling; !!el; el = el.nextElementSibling)
          $(el).addClass('transparent');
      })
      .on('click', function (e) {
        var cnt = 0;
        for (var el = this; !!el; el = el.previousElementSibling, ++cnt);
        self.createForm.stars.value = cnt;
        $(self.createForm.stars).trigger('change');
      })
      .parent().on('mouseout', function (e) {
        self.starHighlight(this, parseInt(self.createForm.stars.value));
      });
      
      // install change handlers so that we can update the values
      $('input', self.createForm).on('change', function (e) {
        e.preventDefault();
        e.stopPropagation();
        if (!self.bundleUri)
          return;
        var el = this;
        if (ccLib.fireCallback(checkForm, el, e)) {
        	var data = {};
        	data[el.name] = el.value;
        	$(el).addClass('loading');
  		    jT.service(self, self.bundleUri, { method: 'PUT', data: data } , function (result) {
    		    $(el).removeClass('loading');
    		    if (!result) // i.e. on error - request the old data
    		      self.load(self.bundleUri);
  		    });
  		  }
      });
      
      ccLib.prepareForm(self.createForm);
    }
	},
	
	// called when a sub-action in bundle details tab is called
	onMatrix: function (id, panel) {
	  var self = this;
		if (!$(panel).hasClass('initialized')) {
      jTConfig.matrix.groups = function (miniset, kit) {
        var groups = {
          "Identifiers" : [
            "http://www.opentox.org/api/1.1#Diagram", 
            "#DetailedInfoRow",
            "http://www.opentox.org/api/1.1#CASRN", 
            "http://www.opentox.org/api/1.1#EINECS",
            "http://www.opentox.org/api/1.1#IUCLID5_UUID",
            // Now some names
            "http://www.opentox.org/api/1.1#ChemicalName",
            "http://www.opentox.org/api/1.1#TradeName",
            "http://www.opentox.org/api/1.1#IUPACName",
            "http://www.opentox.org/api/1.1#SMILES",
            "http://www.opentox.org/api/1.1#InChIKey",
            "http://www.opentox.org/api/1.1#InChI",
            "http://www.opentox.org/api/1.1#REACHRegistrationDate"
          ]
      	};
      	
      	var endpoints = {};
      	
      	var fRender = function (feat, theId) {
      	  return function (data, type, full) {
      	    if (type != 'display')
      	      return '-';

            var html = '';
            for (var fId in self.matrixKit.dataset.feature) {
              var f = self.matrixKit.dataset.feature[fId];
              if (f.sameAs != feat.sameAs || full.values[fId] == null)
                continue;
                
              var catId = self.parseFeatureId(fId, kit).category,
                  config = jT.$.extend(true, {}, kit.settings.configuration.columns["_"], kit.settings.configuration.columns[catId]),
                  val = '';
              
              if (ccLib.getJsonValue(config, 'effects.endpoint.bVisible') !== false)
                val += f.title.replace(" ", '&nbsp;') + "&nbsp;=&nbsp;";
              val += jT.ui.valueWithUnits(full.values[fId], f.units);

              // now - ready to produce HTML
              html += '<span class="ui-icon ui-icon-circle-minus delete-popup" data-feature="' + theId + '"></span>&nbsp;';
              html += '<a class="info-popup" data-feature="' + fId + '" href="#">' + val + '</a>';
              html += '<sup class="helper"><a target="jtox-study" href="' + full.compound.URI + '/study?property_uri=' + encodeURIComponent(fId) + '">?</a></sup>';
              html += '<br/>';
            }
            
            html += '<span class="ui-icon ui-icon-circle-plus edit-popup" data-feature="' + theId + '"></span>';
      	    return  html;
          };
        };
      	
      	for (var fId in miniset.feature) {
      	  var feat = miniset.feature[fId];
      	  if (feat.sameAs == null || feat.sameAs.indexOf("echaEndpoints.owl#") < 0)
      	    continue;
          
          var catId = self.parseFeatureId(fId, kit).topcategory;
          var grp = groups[catId];
          if (grp == null)
            groups[catId] = grp = [];

          if (endpoints[feat.sameAs] == null) {
            endpoints[feat.sameAs] = true;
            feat.title = feat.sameAs.substr(feat.sameAs.indexOf('#') + 1);
            feat.render = fRender(feat, fId);
            grp.push(fId);
          }
      	}
      		
      	return groups;
      }
		
  		$(panel).addClass('initialized');
  		var conf = $.extend(true, {}, jTConfig.matrix, config_study);
  		delete conf.baseFeatures['#IdRow'];
  		
  		var saveButton = $('.save-button', panel)[0];
  		saveButton.disabled = true;
  		var dressButton = function() {
	  		if ($.isEmptyObject(self.edit.added) && $.isEmptyObject(self.edit.deleted)) {
	  			saveButton.disabled = true;
	  			$(saveButton).removeClass('jt-alert').addClass('jt-disabled');
	  			saveButton.innerHTML = "Saved";
	  		}
	  		else {
	  			saveButton.disabled = false;
	  			$(saveButton).addClass('jt-alert').removeClass('jt-disabled');
	  			saveButton.innerHTML = "Save";
	  		}
  		};
  		
  		$(saveButton).on('click', function() {
	  		// TODO: make actuall call to the server for adding and deleting
	  		self.edit.added = {};
				self.edit.deleted = {};
				dressButton();
	  	});
  		
  		var addFeature = function(compoundUri, featureId, value) {
	  		var compound = self.edit.added[compoundUri];
	  		if (compound == null)
	  			self.edit.added[compoundUri] = compound = {};
	  		compound[featureId] = value;
	  		dressButton();
  		};
  		
  		var deleteFeature = function (compoundUri, featureId) {
	  		var compound = self.edit.added[compoundUri];
	  		if (compound != null && compound[featureId] != null)
	  			delete compound[featureId];
	  		else {
	  			compound = self.edit.deleted[compoundUri];
		  		if (compound == null)
		  			self.edit.deleted[compoundUri] = compound = [];
		  		compound.push(featureId);
		  	}
	  		dressButton();
  		};
  		
  		var infoDiv = $('#info-box')[0];
  		var editDiv = $('#edit-box')[0];
  		// now, fill the select with proper values...
  		var df = document.createDocumentFragment();
  		for (var id in self.settings.studyTypeList) {
  		  var opt = document.createElement('option');
  		  opt.value = id;
  		  opt.innerHTML = self.settings.studyTypeList[id].title;
  		  df.appendChild(opt);
  		}
  		
  		$('select.type-list', editDiv)[0].appendChild(df);
  		
  		self.matrixKit = new jToxCompound($('.jtox-toolkit', panel)[0], {
    		crossDomain: true,
    		rememberChecks: true,
    		tabsFolded: true,
    		showDiagrams: true,
    		showUnits: false,
    		hasDetails: false,
    		configuration: conf,
    		onLoaded: function () {
	    		self.edit.refreshMatrix = false;
    		},
    		onRow: function (row, data, index) {
      		$('.info-popup, .edit-popup, .delete-popup', row).on('click', function () {
      		  var boxOptions = { 
        		  overlay: true,
        		  closeOnEsc: true,
        		  closeOnClick: "overlay",
        		  addClass: "popup-box jtox-toolkit ui-front",
        		  animation: "zoomIn",
      		    target: $(this),
      		    maxWidth: 600,
      		    zIndex: 90,
      		    onCloseComplete: function () { this.destroy(); }
      		  };

            var featureId = $(this).data('feature');
    		    var feature = self.matrixKit.dataset.feature[featureId];
      		  if (!$(this).hasClass('edit-popup')) {
      		    
        		  $('.dynamic-condition', infoDiv).remove();
        		  var dynHead = $('tr.conditions', infoDiv)[0];
        		  var postCell = $('td.postconditions', infoDiv)[0];
        		  
        		  for (var i = 0, cl = feature.annotation.length; i < cl; ++i) {
          		  var ano = feature.annotation[i];
          		  // first add the column
          		  var el = document.createElement('th');
          		  el.className = 'dynamic-condition';
          		  el.innerHTML = ano.p;
          		  dynHead.appendChild(el);
          		  // now add the value
          		  el = document.createElement('td');
          		  el.className = 'dynamic-condition';
          		  el.innerHTML = ano.o;
          		  postCell.parentNode.insertBefore(el, postCell);
        		  }
        		  
        		  // make sure there is at least one cell.
              if (cl < 1) {
          		  el = document.createElement('td');
          		  el.className = 'dynamic-condition';
          		  el.innerHTML = '-';
          		  postCell.parentNode.insertBefore(el, postCell);
              }
              
        		  $('th.conditions', infoDiv).attr('colspan', cl);
        		  
        		  ccLib.fillTree(infoDiv, {
        		    endpoint: feature.title,
        		    guidance: feature.creator,
          		  value: jT.ui.valueWithUnits(data.values[featureId], feature.units),
//           		  source: '<a target="_blank" href="' + feature.source.URI + '">' + feature.source.type + '</a>'
        		  });
        		  
        		  if ($(this).hasClass('delete-popup')) {
        		  	$('.delete-box', infoDiv).show();
	              boxOptions.onOpen = function () {
		              var box = this;
		              var content = this.content[0];
		              $('button.jt-alert', content).on('click', function (){ deleteFeature(data.compound.URI, featureId); box.close(); });
              	};
        		  }
        		  else
        		  	$('.delete-box', infoDiv).hide();
        		  	
        		  boxOptions.content = infoDiv.innerHTML;
              new jBox('Tooltip', boxOptions).open();
      		  }
      		  else { // edit mode
              var parse = self.parseFeatureId(featureId, self.matrixKit);
              var featureJson = {};
              
              // we're taking the original jToxEndpoint editor here and glue our part after it.
              boxOptions.content = jT.getTemplate('#jtox-endeditor').innerHTML + editDiv.innerHTML;
              boxOptions.title = parse.category;
              boxOptions.closeButton = "box";
              boxOptions.confirmButton = "Add";
              boxOptions.cancelButton = "Cancel";
              var endSetValue = function (e, field, value) {
	              // TODO: form the JSON here
                console.log("Value set [" + field + "] = `" + value + "`");
              };
              
              boxOptions.onOpen = function () {
	              var box = this;
	              var content = this.content[0];
                jToxEndpoint.linkEditors(self.matrixKit, content, parse.category, parse.topcategory, endSetValue);
	              $('input[type=button]', content).on('click', function (){ addFeature(data.compound.URI, featureId, featureJson); box.close();});	              
              };
              new jBox('Modal', boxOptions).open();
      		  }
      		});
    		}
  		});
		}
		if (self.edit.refreshMatrix)
			self.matrixKit.query(self.bundleUri + '/dataset');
	},
	
	// called when a sub-action in endpoint selection tab is called
	onEndpoint: function (id, panel) {
	  var self = this;
	  var sub = $(".tab-" + id.substr(3), panel)[0];
	  sub.parentNode.style.left = (-sub.offsetLeft) + 'px';
	  var bUri = encodeURIComponent(self.bundleUri);
	  
	  if (id == "endsubstance") {
  	  if (sub.firstElementChild == null) {
    	  var root = document.createElement('div');
    	  sub.appendChild(root);
    	  self.substanceKit = new jToxSubstance(root, { 
      	  crossDomain: true, 
      	  showDiagrams: true, 
      	  embedComposition: true, 
      	  selectionHandler: "onSelectSubstance", 
      	  configuration: jTConfig.matrix,
      	  onRow: function (row, data, index) {
	      	  if (!data.bundles)
	      	  	return;
        	  var bundleInfo = data.bundles[self.bundleUri];
        	  if (!!bundleInfo && bundleInfo.tag == "selected")
        	    $('input.jtox-handler', row).attr('checked', 'checked');
      	  }
        });
  	  }
  	  
      self.substanceKit.query('/query/substance/related?filterbybundle=' + bUri + '&bundle_uri=' + bUri);
	  }
	  else {// i.e. endpoints
  	  var checkAll = $('input', sub)[0];
  	  if (sub.childNodes.length == 1) {
    	  var root = document.createElement('div');
    	  sub.appendChild(root);
    	  self.endpointKit = new jToxEndpoint(root, { 
      	  selectionHandler: "onSelectEndpoint",
      	  onRow: function (row, data, index) {
	      	  if (!data.bundles)
	      	  	return;
        	  var bundleInfo = data.bundles[self.bundleUri];
        	  if (!!bundleInfo && bundleInfo.tag == "selected")
        	    $('input.jtox-handler', row).attr('checked', 'checked');
      	  }
        });
    	  $(checkAll).on('change', function (e) {
	    	  var qUri = "/query/study?bundle_uri=" + bUri;
	    	  if (!this.checked)
	    	  	qUri += "&selected=substances&filterbybundle=" + bUri;
          self.endpointKit.loadEndpoints(qUri);
    	  });
  	  }
  	  $(checkAll).trigger('change'); // i.e. initiating a proper reload
	  }
	},
	
	// called when a sub-action in structures selection tab is called
	onStructures: function (id, panel) {
  	var self = this;
	  if (!self.queryKit) {
  	  self.queryKit = jT.kit($('#jtox-query')[0]);
  	  self.queryKit.setWidget("bundle", self.rootElement);
  	  // provid onRow function so the buttons can be se properly...
  	  self.queryKit.kit().settings.onRow = function (row, data, index) {
    	  if (!data.bundles)
    	  	return;
    	  	
    	  var bundleInfo = data.bundles[self.bundleUri] || {};
    	  // we need to setup remarks field regardless of bundleInfo presence
				var noteEl = $('textarea.remark', row).on('change', function (e) {
					var data = jT.ui.rowData(this);
					var el = this;
					$(el).addClass('loading');
			  	jT.service(self, self.bundleUri + '/compound', { 
			      'method': 'PUT', 
			      'data': { 
			        compound_uri: data.compound.URI, 
			        command: 'add', 
			        tag: data.bundles[self.bundleUri].tag,
			        remarks: $(el).val()
			      } 
			    }, function (result) {
				  	$(el).removeClass('loading');    
			    });
				});

    	  if (!!bundleInfo.tag) {
          $('button.jt-toggle.' + bundleInfo.tag.toLowerCase(), row).addClass('active');
          noteEl.val(bundleInfo.remarks);
        }
        else
        	noteEl.prop('disabled', true).val(' ');
  	  };
    }
    
    if (id == 'structlist')
      self.queryKit.kit().queryDataset(self.bundleUri + '/compound');
    else
      self.queryKit.query();
	},
	
	load: function(bundleUri) {
  	var self = this;
  	jT.call(self, bundleUri, function (bundle) {
    	if (!!bundle) {
      	bundle = bundle.dataset[0];
      	self.bundleUri = bundle.URI;
      	  
      	ccLib.fillTree(self.createForm, bundle);
      	self.starHighlight($('.data-stars-field div', self.createForm)[0], bundle.stars);
      	self.createForm.stars.value = bundle.stars;
      	
      	// now take care for enabling proper buttons on the Indetifiers page
        self.createForm.assFinalize.style.display = '';
        self.createForm.assDuplicate.style.display = '';
        self.createForm.assStart.style.display = 'none';
        
        $(self.rootElement).tabs('enable', 1);
        // now request and process the bundle summary
        jT.call(self, bundle.URI + "/summary", function (summary) {
          if (!!summary) {
            for (var i = 0, sl = summary.facet.length; i < sl; ++i) {
              var facet = summary.facet[i];
              self.bundleSummary[facet.value] = facet.count;
            }  
          }
          self.progressTabs();
        });
    	}
  	});
	},
	
	progressTabs: function () {
    $(this.rootElement).tabs(this.bundleSummary.compound > 0 ? 'enable' : 'disable', 2);
    $(this.rootElement).tabs(this.bundleSummary.substance > 0  && this.bundleSummary.property > 0 ? 'enable' : 'disable', 3);
	},
	
	selectStructure: function (uri, what, el) {
  	var self = this;
  	var activate = !$(el).hasClass('active');
  	$(el).addClass('loading');
  	var noteEl = $('textarea.remark', self.queryKit.kit().getVarRow(el))[0];
  	jT.service(self, self.bundleUri + '/compound', { 
      method: 'PUT', 
      data: { 
        compound_uri: uri, 
        command: activate ? 'add': 'delete',
        tag: what,
        remarks: $(noteEl).val()
      } 
    }, function (result) {
    	$(el).removeClass('loading');
    	if (!!result) {
      	$(el).toggleClass('active');
      	self.edit.refreshMatrix = true;
      	if (activate)
      	{
      	  self.bundleSummary.compound++;
      	  what = (what == "target" ? "source" : "target");
      	  $('button.' + what, el.parentNode).removeClass('active');
        }
        else
          self.bundleSummary.compound--;

      	$(noteEl).prop('disabled', !activate).val(activate ? "" : " ");
        self.progressTabs();
      }
  	});
	},
	
	structuresLoaded: function (kit, dataset) {
    if (document.body.className == 'structlist') {
      this.bundleSummary.compound = dataset.dataEntry.length;
      this.edit.refreshMatrix = true;
      this.progressTabs();
    }
	},
	
	selectSubstance: function (uri, el) {
  	var self = this;
  	$(el).addClass('loading');
  	jT.service(self, self.bundleUri + '/substance', { method: 'PUT', data: { substance_uri: uri, command: el.checked ? 'add' : 'delete' } }, function (result) {
    	$(el).removeClass('loading');
    	if (!result)
    	  el.checked = !el.checked; // i.e. revert
      else {
      	self.edit.refreshMatrix = true;
        if (el.checked)
          self.bundleSummary.substance++;
        else
          self.bundleSummary.substance--;
        self.progressTabs();
        console.log("Substance [" + uri + "] selected");
      }
  	});
	},
	
	selectEndpoint: function (topcategory, endpoint, el) {
  	var self = this;
  	$(el).addClass('loading');
  	jT.service(self, self.bundleUri + '/property', { 
      method: 'PUT', 
      data: { 
        'topcategory': topcategory, 
        'endpointcategory': endpoint,
        'command': el.checked ? 'add' : 'delete' 
      } 
    }, function (result) {
    	$(el).removeClass('loading');
    	if (!result)
    	  el.checked = !el.checked; // i.e. revert
      else {
      	self.edit.refreshMatrix = true;
        if (el.checked)
          self.bundleSummary.property++;
        else
          self.bundleSummary.property--;
        self.progressTabs();
        console.log("Endpoint [" + endpoint + "] selected");  
      }
  	});
	}
};

// Now some handlers - they should be outside, because they are called within windows' context.
function onSelectStructure(e) {
  jToxBundle.selectStructure($(this).data('data'), $(this).hasClass('target') ? 'target' : 'source', this);
}

function onBrowserFilled(dataset) {
  jToxBundle.structuresLoaded(this, dataset);
}

function onSelectSubstance(e) {
  jToxBundle.selectSubstance(this.value, this);
}

function onSelectEndpoint(e) {
  var rowData = jT.ui.rowData(this);
  jToxBundle.selectEndpoint(rowData.subcategory, rowData.endpoint, this);
}

function onDetailedRow(row, data, event) {
  var el = $('.jtox-details-composition', row)[0];
  if (!el)
    return;
  var uri = this.settings.baseUrl + '/substance?type=related&compound_uri=' + encodeURIComponent(data.compound.URI);
  el = $(el).parents('table')[0];
  el = el.parentNode;
  $(el).empty();
  $(el).addClass('paddingless');
  var div = document.createElement('div');
  el.appendChild(div);
  new jToxSubstance(div, $.extend(true, {}, this.settings, {crossDomain: true, selectionHandler: null, substanceUri: uri, showControls: false, onLoaded: null, onDetails: function (root, data, element) {
    new jToxStudy(root, $.extend({}, this.settings, {substanceUri: data.URI}));
  } } ) );
}

$(document).ready(function(){
  $('#logger').on('mouseover', function () { $(this).removeClass('hidden'); }).on('mouseout', function () { $(this).addClass('hidden');});
});

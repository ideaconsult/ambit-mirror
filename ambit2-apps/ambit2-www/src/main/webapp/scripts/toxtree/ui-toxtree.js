var tt = {
  browserKit: null,
  modelKit: null,
  featuresList: null,
  algoMap: {}, // { <id> : { index: <>, results: <>, dom: <>, model: <>}
  compoundIdx: 0,
  coreFeatures: [
    "http://www.opentox.org/api/1.1#CASRN", 
    "http://www.opentox.org/api/1.1#EINECS",
    "http://www.opentox.org/api/1.1#IUCLID5_UUID",
    "http://www.opentox.org/api/1.1#ChemicalName",
/*
    "http://www.opentox.org/api/1.1#TradeName",
    "http://www.opentox.org/api/1.1#IUPACName",
*/
    "http://www.opentox.org/api/1.1#SMILES",
    "http://www.opentox.org/api/1.1#InChIKey",
    "http://www.opentox.org/api/1.1#InChI",
    "http://www.opentox.org/api/1.1#REACHRegistrationDate"
  ]
};

var config_toxtree = {
  "baseFeatures": {}, // to be expanded upon algorithm loading
	"handlers": {
  	"query": function (e, query) {
  	  clearSlate(true);
      jT.parentKit(jToxQuery, this).query();
    },
    "markAuto": function (e) {
      $(this).toggleClass('active');
      onSelectedUpdate(e);
      e.stopPropagation();
    },
    "makeModel": function (e) { makeModel(this); e.stopPropagation(); },
    "runPredict": function (e) { runPredict(this, $(this).data('algoId'), $(this).data('index')); e.stopPropagation(); }
	},
	"groups" : {
	  "Identifiers": [
      "http://www.opentox.org/api/1.1#Diagram",
      "http://www.opentox.org/api/1.1#CASRN", 
      "http://www.opentox.org/api/1.1#EINECS",
      "http://www.opentox.org/api/1.1#IUCLID5_UUID"
	  ],
  	"ToxTree": []   // to be expanded upon algorithm loading
	}
};

function makeModel(el, algoId, callback) {
  if (!el)
    el = $('button.jt-toggle.model', tt.algoMap[algoId].dom)[0];
  else
    algoId = $(el).parents('.tt-algorithm').data('algoId');
  
  var uri = tt.modelKit.algorithm[tt.algoMap[algoId].index].uri;
  $(el).addClass('loading');

  tt.modelKit.getModel(uri, function (result) {
    if (!!result) {
      $(el).addClass('active');
      tt.algoMap[algoId].model = result;
    }
    $(el).removeClass('loading');
    ccLib.fireCallback(callback, null, result);
  });
}

function runPredict (el, algoId, index) {
  if (multi = !el) // yes, it IS one =
    el = $('button.jt-toggle.predict', tt.algoMap[algoId].dom)[0];
  else if (algoId == null) {
    algoId = $(el).data('algoId');
    if (algoId == null) 
      algoId = $(el).parents('.tt-algorithm').data('algoId');
  }

  var datasetUri = null;
  if (multi)
    datasetUri = tt.browserKit.queryUri();
  else if (tt.browserKit.dataset != null) {
    if (index == null)
      index = $(el).data('index');
    if (index == null)
      index = tt.compoundIdx;
    if (index >= 0 && index < tt.browserKit.dataset.dataEntry.length)
      datasetUri = tt.browserKit.dataset.dataEntry[index].compound.URI;  
  }
  
  if (tt.browserKit.dataset == null || datasetUri == null)
    return;

  var runIt = function (modelUri) {
    tt.modelKit.runPrediction(datasetUri, modelUri, function (result) {
      if (!!result) {
        parsePrediction(result, algoId, index);
        $(el).addClass('active');
      }
      $(el).removeClass('loading');
    })
  };
  
  $(el).addClass('loading');
  if (tt.algoMap[algoId].model == null) {
    makeModel(null, algoId, function (result) { 
      if (!!result)
        runIt(result);
      else
        $(el).removeClass('loading');
    });
  }
  else
    runIt(tt.algoMap[algoId].model);
}

function runSelected() {
  $('#tt-models-panel button.jt-toggle.auto.active').each(function () {
    var tEl = $(this).parents('.tt-algorithm');
    runPredict(null, tEl.data('algoId'));
  });  
}

function formatAlgoName(val) {
  return (val.indexOf('ToxTree: ') == 0) ? val = val.substr(9) : val;
}

function buildCategories(features, values) {
	var cats = [];
	var regex = /\^\^(\S+)Category/i;
	var multi = false;
	
	for (var fId in values) {
	  if (features[fId].title.indexOf('#explanation') > -1 || features[fId].source.type.toLowerCase() != 'model')
	    continue;
  	var val = values[fId];
  	var anot = features[fId].annotation;
  	if (anot.length > 0) {
  	  if (cats.length > 0)
  	    multi = true;
    	for (var i = 0;i < anot.length; ++i) {
    	  cats.push({
    	    name: features[fId].title.replace(/\s/g, '&nbsp;'),
      	  title: anot[i].o.replace(/\s/g, '&nbsp;'),
      	  toxicity: anot[i].type.replace(regex, '$1').toLowerCase(),
      	  active: anot[i].o == val
    	  });
    	}
    }
    else
      cats.push({
        name: features[fId].title.replace(/\s/g, '&nbsp;'),
        title: val,
        toxicity: 'unknown',
        active: true
      });
	};
	
	if (multi) {
	  var old = cats;
	  cats = [];
	  $.map(old, function (o) {
  	  if (o.active) {
    	  o.title = o.name + ':&nbsp;' + o.title;
    	  cats.push(o);
  	  }
	  });
  	
	}

	return cats;
}

function fillClassification(root, cats, filter) {
  ccLib.populateData(root, '#tt-class', cats, function (data) {
    if (filter && !data.active)
      return false;
    $(this).addClass(data.toxicity);
    if (data.active)
      $(this).addClass('active');
  });
}

function onSelectedUpdate(e) {
  if (tt.modelKit.algorithm == null)
    return;
	var tEl = $('#tt-models-panel .counter-field')[0];
	var v = $('button.jt-toggle.auto.active', tt.modelKit.rootElement).length;
	tEl.innerHTML = jT.ui.updateCounter(tEl.innerHTML, v, tt.modelKit.algorithm.length);;
}

function onDataLoaded(result) {
  showCompound();
  runSelected();
}

function onAlgoLoaded(result) {
  if (!!result) {
    var idx = 0;
    ccLib.populateData(tt.modelKit.rootElement, '#tt-algorithm', result.algorithm, function (data) {
      tt.algoMap[data.id] = {
        index: idx,
        dom: this,
        results: {},
      };
      
      $(this).data('algoId', data.id);
      config_toxtree.groups.ToxTree.push(data.uri);
      config_toxtree.baseFeatures[data.uri] = {
    	  title: formatAlgoName(data.name), 
    	  search: false,
    	  data: "index",
    	  column: { sClass: data.id },
    	  render: (function (aId) { return function(data, type, full) {
    	    return (type != 'display') ? data : 
    	      '<button class="jt-toggle jtox-handler predict" data-algo-id="' + aId + '" data-index="' + data + '" data-handler="runPredict" title="Run prediction on current compound">Run</button>';
          };
        })(data.id)
      };
      idx++;
    });
    
    jT.ui.installHandlers(tt.modelKit);
  }
  
  onSelectedUpdate(null);
  // now it's time to create the browser table
  jT.initKit($('#tt-table')[0]);
  tt.browserKit = jToxCompound.kits[0];
}

var updateTimer = null;
function updateSize(root) {
  if (typeof root == 'string')
    root = $(root)[0];
  if (updateTimer != null)
    clearTimeout(updateTimer);
  updateTimer = setTimeout(function () { ccLib.flexSize(root); }, 100);
}

function addFeatures(data, className) {
  if (data.length > 0) {
    var enumFn = null;
    if (className != null) {
      enumFn = function () { $(this).addClass(className); };
      $('.' + className, tt.featuresList).remove();
    }
    ccLib.populateData(tt.featuresList, '#tt-feature', data, enumFn);
    var sep = $('#tt-feature')[0].cloneNode(true);
    sep.removeAttribute('id');
    $(sep).addClass('separator').empty();
    if (className != null)
      $(sep).addClass(className);
    tt.featuresList.appendChild(sep);
  }
}

function clearSlate(all) {
  $(tt.featuresList).empty();
  $('#tt-diagram img.toxtree-diagram')[0].src = '';
  updateSize();
  $('#tt-models-panel .tt-algorithm button.predict').removeClass('active');
  $('#tt-models-panel .tt-algorithm .tt-explanation').empty();
  $('#tt-models-panel .tt-algorithm .tt-classification').empty();
  
  if (all) {
    for (var aId in tt.algoMap)
  	  tt.algoMap[aId].results = {};
      $('.tt-class', tt.browserKit.rootElement).remove();
      $('.calculated', tt.browserKit.rootElement).removeClass('calculated');
      tt.browserKit.equalizeTables();
  }
}

function changeImage(part, path) {
  $('#tt-diagram img.toxtree-diagram')[0].src = tt.browserKit.dataset.dataEntry[tt.compoundIdx].compound.URI + path + '&media=image/png';
  updateSize('#tt-browser-panel');
}

function showCompound() {
  var kit = tt.browserKit;

  if (kit.dataset.dataEntry[tt.compoundIdx] != null) {
    $('#tt-diagram img.toxtree-diagram')[0].src = jT.ui.diagramUri(kit.dataset.dataEntry[tt.compoundIdx].compound.URI);
    updateSize('#tt-browser-panel');
  }

  var counter = $('#tt-browser-panel .counter-field')[0];
  counter.innerHTML = jT.ui.updateCounter(
    counter.innerHTML, 
    tt.compoundIdx + kit.pageStart + (kit.dataset.dataEntry[tt.compoundIdx] ? 1 : 0), 
    kit.entriesCount != null ? kit.entriesCount : kit.pageStart + kit.pageSize + '+'
  );
  
  if (tt.compoundIdx == 0 && kit.pageStart == 0) // we need to disable prev 
    $('#tt-browser-panel .prev-field').addClass('paginate_disabled_previous').removeClass('paginate_enabled_previous');
  else
    $('#tt-browser-panel .prev-field').removeClass('paginate_disabled_previous').addClass('paginate_enabled_previous');
    
  if (kit.entriesCount != null && tt.compoundIdx + kit.pageStart >= kit.entriesCount - 1)
    $('#tt-browser-panel .next-field').addClass('paginate_disabled_next').removeClass('paginate_enabled_next');
  else
    $('#tt-browser-panel .next-field').removeClass('paginate_disabled_next').addClass('paginate_enabled_next');

  var entry = tt.browserKit.dataset.dataEntry[tt.compoundIdx];
  if (entry != null)
    addFeatures(tt.browserKit.featureData(entry, tt.coreFeatures));
}

function showPrediction(algoId) {
  var map = tt.algoMap[algoId];
  var mapRes = map.results[tt.compoundIdx];
  
  // check if we have results for this guy at all...
  if (mapRes == null || mapRes.data == null)
    return;

  addFeatures(mapRes.data, algoId);
  var aEl = map.dom;
  if (mapRes.explanation != null)
    $('.tt-explanation', aEl).html(mapRes.explanation.replace(/(Yes|No)/g, '<span class="answer $1">$1</span>'));
  $('.tt-classification', aEl).empty();
  
  
  fillClassification($('.tt-classification', aEl)[0], mapRes.categories);
  $(aEl).removeClass('folded');
}

function parsePrediction(result, algoId, index) {
  var map = tt.algoMap[algoId];
  
  var cells = $('#tt-table table td.' + algoId);
  for (var i = 0, rl = result.dataEntry.length; i < rl; ++i) {
    var idx = i + (index || 0);
    var mapRes = map.results[idx];
    if (mapRes == null)
      map.results[idx] = mapRes = {};
    mapRes.explanation = '';
    mapRes.data = jToxCompound.extractFeatures(result.dataEntry[i], result.feature, function (feature, fId) {
      if (feature.title.indexOf("#explanation") > -1) {
        mapRes.explanation = feature.value;
        return false;
      }
      else if (feature.source.type.toLowerCase() == 'model' && !!feature.value)
        return true;
      else
        return false;
    });

    mapRes.categories = buildCategories(result.feature, result.dataEntry[i].values);
    
    if (mapRes.categories.length == 0 && index == null)
        runPredict ($('button', cells[idx]), algoId);
    else { 
      $('.tt-class', cells[idx]).remove();
      $(cells[idx]).addClass('calculated');
      if (mapRes.categories.length > 0) {
        fillClassification(cells[idx], mapRes.categories, true)
        // we need to show it, if this is the selected one
        if (idx == tt.compoundIdx)
          showPrediction(algoId);
      }
      else // no data at all!
        cells[idx].innerHTML = '-';
    }
  }
  
  tt.browserKit.equalizeTables();
}

function loadCompound(index) {
  if (index < 0 && tt.browserKit.pageStart > 0) { // we might need a reload...
    tt.compoundIdx = tt.browserKit.pageSize - 1;
    tt.browserKit.prevPage();
    clearSlate(true);
  }
  else if (index >= tt.browserKit.dataset.dataEntry.length) {
    tt.compoundIdx = 0;
    tt.browserKit.nextPage();
    clearSlate(true);
  }
  else if (index != tt.compoundIdx) { // normal showing up
    tt.compoundIdx = index;
    clearSlate();
    showCompound();
    for (var aId in tt.algoMap)
      showPrediction(aId);
  }
}

function switchView(mode) {
  if (typeof mode != 'string')
    mode = $(this).data('mode');
  $('#sidebar .side-title>div').each(function () {
    if ($(this).data('mode') == mode)
      $(this).addClass("pressed");
    else
      $(this).removeClass("pressed");
  });
  
  var scroller = $('#tt-bigpane')[0];
  $(scroller).animate({ scrollTop: (mode == 'single') ? 0 : $('#tt-table')[0].offsetTop }, 300, 'easeOutQuad');
}

function onTableDetails(idx) {
  loadCompound(idx);
  switchView('single');
  return false;  
}

$(document).ready(function(){
  $('#tt-models-panel a.select-unselect').on('click', function () {
    var alt = $(this).data('other');
    $(this).data('other', this.innerHTML);
    this.innerHTML = alt;
    if (alt != 'select')
      $('#tt-models-panel button.jt-toggle.auto').addClass('active');
    else
      $('#tt-models-panel button.jt-toggle.auto.active').removeClass('active');
    
    onSelectedUpdate.call(this);
  });
  $('#tt-models-panel a.expand-collapse').on('click', function () {
    var alt = $(this).data('other');
    $(this).data('other', this.innerHTML);
    this.innerHTML = alt;
    if (alt != 'collapse')
      $('#tt-models-panel .tt-algorithm').addClass('folded');
    else
      $('#tt-models-panel .tt-algorithm.folded').removeClass('folded');
  });
  $('#tt-models-panel a.run-selected').on('click', function () {
    runSelected();
  });
  $('#tt-models-panel a.show-hide').on('click', function () {
    var alt = $(this).data('other');
    if ($('#tt-models-panel button.jt-toggle.auto.active').length == 0 && alt != 'hide')
      return;
    $(this).data('other', this.innerHTML);
    this.innerHTML = alt;
    $('#tt-models-panel button.jt-toggle.auto').each(function () {
      var par = $(this).parents('.tt-algorithm');
      var aId = $(par).data('algoId');
      if (alt != 'show'){ // i.e. we need to show
        par.show();
        $('table .' + aId, tt.browserKit.rootElement).show();
      }
      else if (!$(this).hasClass('active')) {
        par.hide();
        $('table .' + aId, tt.browserKit.rootElement).hide();
      }
    });
    
    tt.browserKit.equalizeTables();
  });
  
  tt.modelKit = jToxModel.kits[0];
  tt.featuresList = $('#tt-features .list')[0];
  
  $('#tt-browser-panel .prev-field').on('click', function () { if ($(this).hasClass('paginate_enabled_previous')) loadCompound(tt.compoundIdx - 1); });
  $('#tt-browser-panel .next-field').on('click', function () { if ($(this).hasClass('paginate_enabled_next')) loadCompound(tt.compoundIdx + 1); });
  $('#tt-diagram .title').on('click', function () { updateSize('#tt-browser-panel'); });
  $('#sidebar .side-title>div').on('click', switchView);
  switchView('single');
  
  $('#logger').on('click', function () { $(this).toggleClass('hidden'); });
  
  $(window).on('resize', function () { updateSize(); });
  updateSize();
});

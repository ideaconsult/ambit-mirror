var jTConfig = {};

function onSideLoaded(result) {
  if (!result)
    return;
	var tEl = $('.title', $(this.rootElement).parents('.jtox-foldable')[0])[0];
	var set = (result.model || result.dataset);
	$(tEl).data('total', set.length);
	tEl.innerHTML = jT.ui.updateCounter(tEl.innerHTML, 0, set.length);
	this.table.fnSort([ [2,'desc'] ]);
}

function onSelectedUpdate(e) {
  var par = $(this).parents('.jtox-foldable')[0];
	var tEl = $('.title', par)[0];
	var v = $('input[type="checkbox"]:checked', par).length;
	
	tEl.innerHTML = jT.ui.updateCounter(tEl.innerHTML, v, $(tEl).data('total'));
}

function jTConfigurator(kit) {
  return jTConfig.dataset;
}

function onDetailedRow(row, data, element) {
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
  new jToxSubstance(div, $.extend(true, {}, this.settings, {crossDomain: true, selectionHandler: null, substanceUri: uri, showControls: true, onDetails: function (root, data, element) {
    new jToxStudy(root, $.extend({}, this.settings, {substanceUri: data.URI}));
  } } ) );
}

function createGroups(miniset, kit) {
  var groups = {
    "Identifiers" : [
      "http://www.opentox.org/api/1.1#Diagram", 
      "#DetailedInfoRow",
      "http://www.opentox.org/api/1.1#CASRN", 
      "http://www.opentox.org/api/1.1#EINECS",
      "http://www.opentox.org/api/1.1#IUCLID5_UUID",
      // Names
      "http://www.opentox.org/api/1.1#ChemicalName",
      "http://www.opentox.org/api/1.1#TradeName",
      "http://www.opentox.org/api/1.1#IUPACName",
      "http://www.opentox.org/api/1.1#SMILES",
      "http://www.opentox.org/api/1.1#InChIKey",
      "http://www.opentox.org/api/1.1#InChI",
      "http://www.opentox.org/api/1.1#REACHRegistrationDate"
    ]
	};
	for (var fId in miniset.feature) {
	  var feat = miniset.feature[fId]; 
  	var src = feat.source;
  	if (!src || !src.type || src.type.toLowerCase() != 'model')
  	  continue;
    src = src.URI.substr(src.URI.lastIndexOf('/') + 1);
    if (groups[src] === undefined)
      groups[src] = [];
    if (feat.title.indexOf('explanation') > 0)
      feat.visibility = "details";
    groups[src].push(fId);
	}
	groups["Substances"] = [ "http://www.opentox.org/api/1.1#CompositionInfo" ];
	groups["Datasets"] = function (name, miniset) {
    var arr = [];
    for (var f in miniset.feature) {
      var feat = miniset.feature[f];
      if (!feat.used && !feat.basic) {
        arr.push(f);
        if (feat.title.indexOf('explanation') > 0)
          feat.visibility = "details";
      }
    }
    return arr;
  }
	return groups;
}

$(document).ready(function(){
  var toggleBar = function () {
    $('#sidebar').toggleClass('hidden');
  };
  $('#sidebar span.ui-icon').on('click', toggleBar);
  $('#sidebar div.side-title').on('click', toggleBar);
  $('#sidebar').on('mouseover', function () { $(this).removeClass('hidden'); }).on('mouseout', function () { $(this).addClass('hidden');});
  jT.ui.installMultiSelect($('#sidebar')[0], onSelectedUpdate);
  $('#logger').on('click', function (e) { $(this).toggleClass('hidden'); });
});

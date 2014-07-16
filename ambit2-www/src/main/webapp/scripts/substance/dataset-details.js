var jTConfig = {};

function onSideLoaded(result) {
	var tEl = $('.title', $(this.rootElement).parents('.jtox-foldable')[0])[0];
	var set = (result.model || result.dataset);
	$(tEl).data('total', set.length);
	tEl.innerHTML = jT.ui.updateCounter(tEl.innerHTML, 0, set.length);
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

function onDetailedRow(row, data, event) {
      var el = $('.jtox-details-composition', row);
      var uri = $(el).data('uri');
      el = $(el).parents('table')[0];
      el = el.parentNode;
      $(el).empty();
      var div = document.createElement('div');
      el.appendChild(div);
  var ds = new jToxSubstance(div, $.extend(true, {}, this.settings, {crossDomain: true, selectionHandler: null, substanceUri: uri, showControls: false, onDetails: function (root, data, event) {
    new jToxStudy(root, $.extend({}, this.settings, {substanceUri: data}));
  } } ) );
}
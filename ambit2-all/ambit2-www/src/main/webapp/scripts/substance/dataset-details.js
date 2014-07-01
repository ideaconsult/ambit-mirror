    function onSideLoaded(result) {
    	var tEl = $('.title', $(this.rootElement).parents('.jtox-foldable')[0])[0];
    	var set = result.dataset || result.model;
    	tEl.innerHTML = tEl.innerHTML.replace(/(.+)\((\d+)\/(\d+)(.*)?/, '$1($2/' + set.length + '$4');;
    }
    
    
    function onSelectedUpdate(el) {
      var par = $(el).parents('.jtox-foldable')[0];
    	var tEl = $('.title', par)[0];
    	var v = $('input[type="checkbox"]:checked', par).length;
    	tEl.innerHTML = tEl.innerHTML.replace(/(.+)\((\d+)\/(\d+)(.*)?/, '$1(' + v + '/$3$4');;
    }
    

    
    function onDetailedRow(row, data, index) {
      var el = $('.jtox-details-composition', row);
      var uri = $(el).data('uri');
      el = $(el).parents('table')[0];
      el = el.parentNode;
      $(el).empty();
      var div = document.createElement('div');
      el.appendChild(div);
      var ds = new jToxSubstance(div, {
      			crossDomain: true, 
      			substanceUri: uri, 
      			showControls: false,
      			showDiagrams: true , 
      			onDetails: function (root, data, event) {
        			var comp = new jToxStudy(root);
        			comp.querySubstance(data);
      			} 
      		} );
    }
    
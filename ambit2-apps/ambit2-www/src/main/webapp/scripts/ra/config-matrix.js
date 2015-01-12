jTConfig.matrix = {
  "baseFeatures": {
		"http://www.opentox.org/api/1.1#CompositionInfo" : {
		  "visibility": "details",
			"title": "Composition",
			"data": "compound.URI",
			"column": { bVisible: false },
			"basic": true,
			"render" : function(data, type, full) {
        return (type != "details") ? "-" : '<span class="jtox-details-composition"></span>';
      }
		},
		"http://www.opentox.org/api/1.1#Reasoning" : {
			"title": "Reason",
			"data": "compound.URI",
			"column": { sWidth: "300px", sClass: "paddingless" },
			"render" : function(data, type, full) {
			  data = data || '';
			  return (type != 'display') ? data : '<textarea class="remark" placeholder="Reason for selection_"></textarea>';
      }
		},
		'#SubstanceName' : { title: "Substance Name", data: "compound.name", primary: true, basic: true, column: { sClass: "breakable word-break" }, render: function (data, type, full) { 
  		return data || full.compound.tradename;
		} },
    '#SubstanceUUID': { title: "I5UUID", data: "compound.i5uuid", primary: true, render: function (data, type, full) {
      return (type != 'display') ? data : jT.ui.shortenedData('<a target="_blank" href="' + full.compound.URI + '/study">' + data + '</a>', "Press to copy the UUID in the clipboard", data)
    } },

		"http://www.opentox.org/api/1.1#SubstanceDataSource": { title: "Data source", data: "compound.ownerName", accumulate: true, primary: true, column: { sClass: "breakable" } },
		"#ConstituentName": { title: "Constituent Name", data: "component.compound.name", accumulate: false, primary: true, column: { sClass: "breakable work-break" } },
    "#ConstituentContent": { title: "Content", data: "proportion.typical", accumulate: false, primary: true, column: { sClass: "center" }, render: function (data, type, full) { 
      return type != 'display' ? '' + data.value : jToxComposition.formatConcentration(data.precision, data.value, data.unit);
    } },
    "#ConstituentContainedAs": { title: "Contained As", data: "relation", accumulate: false, primary: true, column: { sClass: "center" }, render: function (data, type, full) { 
      return (type != 'display') ? data : '<span>' + data.substring(4).toLowerCase() + '</span>' + jT.ui.putInfo(full.substance.URI + '/composition', full.compositionName);      
    } },
		
		"#IdRow" : {
			"data": "compound.URI",
			"column": { sWidth: "80px", sClass: "text-top" },
			"render" : function(data, type, full) {
        return  (type != 'display') ? data : 
          '<button class="jt-toggle jtox-handler target" data-data="' + data + '" data-handler="onSelectStructure" title="Select the structure as Target">T</button>' +
          '<button class="jt-toggle jtox-handler source" data-data="' + data + '" data-handler="onSelectStructure" title="Select the structure as Source">S</button>' +
          '<span class="jtox-details-open ui-icon ui-icon-folder-collapsed" title="Press to open/close detailed info for this compound"></span>';
      }
		}
  },
  "groups": {
    "Identifiers" : [
      "http://www.opentox.org/api/1.1#Diagram",
      "#DetailedInfoRow",
      "http://www.opentox.org/api/1.1#CASRN", 
      "http://www.opentox.org/api/1.1#EINECS",
      // The rest of them
      "http://www.opentox.org/api/1.1#Reasoning",
      "http://www.opentox.org/api/1.1#ChemicalName",
      "http://www.opentox.org/api/1.1#SMILES",
      "http://www.opentox.org/api/1.1#InChIKey",
      "http://www.opentox.org/api/1.1#InChI",
      "http://www.opentox.org/api/1.1#REACHRegistrationDate"
    ],

    "Substances": [ 
      "http://www.opentox.org/api/1.1#CompositionInfo" 
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
    }
  },
	"columns": {
  	"substance": {
    	'Contained in as': { iOrder: 20, mData: "composition", sTitle: "Contained in as", mRender: jT.ui.renderRelation }
  	}
	}
};

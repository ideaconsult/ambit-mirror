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
    "http://www.opentox.org/api/1.1#ChemicalName" : {
      primary: false,
      render : function(data, type, full) {
        // Allow longer names that are list, separated by "|", to break
        return data.replace(/\|/g, ' | ');
      }
    },
    "http://www.opentox.org/api/1.1#CASRN" : {
      primary: false
    },
    "http://www.opentox.org/api/1.1#EINECS" : {
      primary: false
    },
		"http://www.opentox.org/api/1.1#Reasoning" : {
			"title": "Rationale",
			"data": "compound.URI",
      "search": true,
			"column": { sWidth: "300px", sClass: "paddingless" },
			"render" : function(data, type, full) {
        // This looks realy hacky, but works.
        // Get the remarks info here so that the column can be filtered.
        var bundleInfo = full.bundles[jToxBundle.bundleUri] || {};
        if (!!bundleInfo.tag) {
          data = bundleInfo.remarks;
        }
			  data = data || '';
			  return (type != 'display') ? data : '<textarea class="remark" placeholder="Reason for selection_">' + data + '</textarea>';
      }
		},
		'#SubstanceName' : {
      title: "Substance Name",
      data: "compound.name",
      primary: true,
      basic: true,
      column: { sClass: "breakable word-break" },
      render: function (data, type, full) {
    		return data || full.compound.tradename;
  		}
    },
    '#SubstanceUUID': {
      title: "I5UUID",
      data: "compound.i5uuid",
      primary: true,
      render: function (data, type, full) {
        return (type != 'display') ? data : jT.ui.shortenedData('<a target="_blank" href="' + full.compound.URI + '/study">' + data + '</a>', "Press to copy the UUID in the clipboard", data)
      }
    },
    "http://www.opentox.org/api/1.1#SubstanceDataSource": {
      title: "Data source",
      data: "compound.ownerName",
      accumulate: true,
      primary: true,
      column: { sClass: "breakable" }
    },
		"#ConstituentName": {
      title: "Constituent Name",
      data: "component.compound.name",
      accumulate: false,
      primary: true,
      column: { sClass: "breakable work-break" }
    },
    "#ConstituentContent": {
      title: "Content",
      data: "proportion.typical",
      accumulate: false,
      primary: true,
      column: { sClass: "center" },
      render: function (data, type, full) {
        return type != 'display' ? '' + data.value : jToxComposition.formatConcentration(data.precision, data.value, data.unit);
      }
    },
    "#ConstituentContainedAs": {
      title: "Contained As",
      data: "relation",
      accumulate: false,
      primary: true,
      column: { sClass: "center" },
      render: function (data, type, full) {
        return (type != 'display') ? data : '<span>' + data.substring(4).toLowerCase() + '</span>' + jT.ui.putInfo(full.substance.URI + '/composition', full.compositionName);
      }
    },
		"#IdRow" : {
			"data": "compound.URI",
			"column": { sWidth: "80px", sClass: "text-top" },
			"render" : function(data, type, full) {
        return  (type != 'display') ? data :
          '<button class="jt-toggle jtox-handler target" data-tag="target" data-data="' + data + '" data-handler="onSelectStructure" title="Select the structure as Target">T</button>' +
          '<button class="jt-toggle jtox-handler source" data-tag="source" data-data="' + data + '" data-handler="onSelectStructure" title="Select the structure as Source">S</button>' +
          '<button class="jt-toggle jtox-handler cm" data-tag="cm" data-data="' + data + '" data-handler="onSelectStructure" title="Select the structure as Category Member">CM</button>' +
          '<span class="jtox-details-open ui-icon ui-icon-folder-collapsed" title="Press to open/close detailed info for this compound"></span>';
      }
		},
    "http://www.opentox.org/api/1.1#Similarity" : {
      "title": "Similarity",
      "data" : "compound.metric",
      "accumulate": true,
      "column": { bVisible: false },
      "render" : function(data, type, full) {
        return data;
      }
    }
  },
  "groups": {
    "Identifiers" : [
      "http://www.opentox.org/api/1.1#Diagram",
      "#DetailedInfoRow",
      "http://www.opentox.org/api/1.1#CASRN",
      "http://www.opentox.org/api/1.1#EINECS",
      "http://www.opentox.org/api/1.1#ChemicalName",
      "http://www.opentox.org/api/1.1#Similarity",
      "http://www.opentox.org/api/1.1#Reasoning"
      // The rest of them
      //"http://www.opentox.org/api/1.1#SMILES",
      //"http://www.opentox.org/api/1.1#InChIKey",
      //"http://www.opentox.org/api/1.1#InChI",
      //"http://www.opentox.org/api/1.1#REACHRegistrationDate"
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
      'Contained in as': { iOrder: 20, mData: "composition", sTitle: "Contained in as", mRender: jT.ui.renderRelation },
      'Reference substance UUID': {
        sTitle: "Reference substance UUID",
        mData: "referenceSubstance",
        mRender: function (data, type, full) {
          if (data.i5uuid == null) return '';
          return (type != 'display') ? data.i5uuid : jT.ui.shortenedData('<a target="_blank" href="' + data.uri + '">' + data.i5uuid + '</a>', "Press to copy the UUID in the clipboard", data.i5uuid);
        }
      },
    }
	},
  "handlers": {
    "query": function (e, query) {

      var kit = jT.parentKit(jToxQuery, this),
          searchKit = kit.widget('search');
      var form = $(searchKit).find('form')[0];
      var searchType = form.searchtype.value;
      if (searchType == 'similarity') {
        kit.mainKit.settings.configuration.baseFeatures["http://www.opentox.org/api/1.1#Similarity"].column.bVisible = true;
      }
      else {
        kit.mainKit.settings.configuration.baseFeatures["http://www.opentox.org/api/1.1#Similarity"].column.bVisible = false;
      }

      kit.query();

    }
  }
}
